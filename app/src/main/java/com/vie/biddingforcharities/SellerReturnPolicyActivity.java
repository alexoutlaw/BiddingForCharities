package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.vie.biddingforcharities.logic.GetInfoTask;
import com.vie.biddingforcharities.logic.Trio;
import com.vie.biddingforcharities.logic.User;
import com.vie.biddingforcharities.logic.Utilities;
import com.vie.biddingforcharities.ui.AddSettingExtraDialog;
import com.vie.biddingforcharities.ui.SettingsExtraListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SellerReturnPolicyActivity extends Activity {
    DrawerLayout NavLayout;
    ListView NavList;
    Button HomeButton;
    ImageButton NavDrawerButton;

    ListView ReturnPolicyList;
    AddSettingExtraDialog SettingDialog;
    ProgressDialog spinner, getSpinner;
    Button AddReturnPolicyButton;

    ArrayList<Trio> ReturnPolicies;
    Trio SavedReturnPolicy;
    ArrayList<GetInfoTask> detailTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_returnpolicy);

        // RESTRICTED: seller only
        if(((Global) getApplication()).getUser().getUserType() == User.UserTypes.STANDARD) {
            Toast.makeText(this, "Access Restricted to Sellers only, please submit a request to become a Seller.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, CharityRequestActivity.class));
            finish();
        }

        NavLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavList = (ListView) findViewById(R.id.navList);
        NavDrawerButton = (ImageButton) findViewById(R.id.nav_drawer_expand);
        ReturnPolicyList = (ListView) findViewById(R.id.returnpolicy_list);

        // Build Side Nav Menu
        ((Global) getApplication()).BuildNavigationMenu(NavList);
        NavDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavLayout.openDrawer(Gravity.LEFT);
            }
        });

        // Build Top Nav Menu
        HomeButton = (Button) findViewById(R.id.home_button);
        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        // Add Return Policy
        AddReturnPolicyButton = (Button) findViewById(R.id.add_returnpolicy_button);
        AddReturnPolicyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build Dialog
                SettingDialog = new AddSettingExtraDialog();
                Bundle args = new Bundle();
                args.putString("type", "create");
                SettingDialog.setArguments(args);
                SettingDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
                SettingDialog.show(getFragmentManager(), "add");
            }
        });

        SavedReturnPolicy = new Trio("", 0, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        startGetTask();
    }

    @Override
    protected void onPause() {
        //Cancel threads while reference is valid
        for(GetInfoTask t: detailTasks) {t.cancel(true);}
        detailTasks.clear();

        super.onPause();
    }

    public void startGetTask() {
        if (getSpinner != null && getSpinner.isShowing()) {
            getSpinner.dismiss();
        }
        // Show Spinner
        getSpinner = new ProgressDialog(SellerReturnPolicyActivity.this);
        getSpinner.setMessage("Getting Return Policies...");
        getSpinner.setCanceledOnTouchOutside(false);
        getSpinner.show();

        // Load Return Policies
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "0"}
                });
        new GetInfoTask(SellerReturnPolicyActivity.this).execute(GetInfoTask.SourceType.getUserReturnPolicies.toString(), queryStr);
    }

    public void onGetTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            JSONArray return_policy_list = (JSONArray) json.get("return_policy_list");

            ReturnPolicies = new ArrayList<>();
            User user = ((Global) getApplication()).getUser();
            for(int i = 0; i < return_policy_list.length(); i++) {
                final JSONObject policy = (JSONObject) return_policy_list.get(i);
                int policy_id = policy.getInt("policy_id");
                String policy_name = policy.getString("policy_name");

                ReturnPolicies.add(new Trio(policy_name, policy_id, ""));

                // Detail Task
                String queryStr = Utilities.BuildQueryParams(
                        new String[][]{
                                new String[]{"user_guid", user.getUserGuid()},
                                new String[]{"user_id", String.valueOf(user.getUserID())},
                                new String[]{"mode", "1"},
                                new String[]{"return_policy_id", String.valueOf(policy_id)},
                        });
                detailTasks.add((GetInfoTask) new GetInfoTask(SellerReturnPolicyActivity.this).execute(GetInfoTask.SourceType.getUserReturnPolicyDetails.toString(), queryStr));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        if(detailTasks.isEmpty()) {
            //Dismiss Spinner
            if(getSpinner != null && getSpinner.isShowing()) {
                getSpinner.dismiss();
            }

            // Add to UI
            final ListAdapter adapter = new SettingsExtraListAdapter(this, ReturnPolicies);
            ReturnPolicyList.setAdapter(adapter);

            // Update Cache
            ((Global) getApplication()).getUser().updateReturnPolicies(ReturnPolicies);
        }
    }

    public void onDetailsTaskFinish(GetInfoTask task, String data) {
        detailTasks.remove(task);

        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int return_policy_id = json.getInt("return_policy_id");
            String return_policy_name = json.getString("return_policy_name");
            String return_policy_details = json.getString("return_policy_details");

            // Add Detail to
            for(int i = 0; i < ReturnPolicies.size(); i++) {
                Trio temp = ReturnPolicies.get(i);
                if(temp.ID.equals(return_policy_id)) {
                    temp.Extra = return_policy_details;
                    ReturnPolicies.set(i, temp);
                    break;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }


        if(detailTasks.isEmpty()) {
            //Dismiss Spinner
            if(getSpinner != null && getSpinner.isShowing()) {
                getSpinner.dismiss();
            }

            // Add to UI
            final ListAdapter adapter = new SettingsExtraListAdapter(this, ReturnPolicies);
            ReturnPolicyList.setAdapter(adapter);

            // Update Cache
            ((Global) getApplication()).getUser().updateReturnPolicies(ReturnPolicies);
        }
    }

    public void startAddTask(String policyName, String policyDetail) {
        SavedReturnPolicy.Label = policyName;
        SavedReturnPolicy.Extra = policyDetail;

        // Dismiss Dialog
        SettingDialog.dismiss();

        // Show Spinner
        spinner = new ProgressDialog(SellerReturnPolicyActivity.this);
        spinner.setMessage("Adding Return Policy...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Add Task
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "2"},
                        new String[]{"return_policy_name", policyName},
                        new String[]{"return_policy_details", policyDetail}
                });
        new GetInfoTask(SellerReturnPolicyActivity.this).execute(GetInfoTask.SourceType.addUserReturnPolicy.toString(), queryStr);
    }

    public void onAddTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int return_policy_id = json.getInt("return_policy_id");

            if(return_policy_id > 0) {
                startGetTask();

                Toast.makeText(this, "Successfully Added Return Policy " + SavedReturnPolicy.Label, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error Adding Return Policy", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner
        if(spinner != null && spinner.isShowing()) {
            spinner.dismiss();
        }
    }

    public void startUpdateDialog(int policyId) {
        // Save ID
        SavedReturnPolicy.ID = policyId;

        // Get Label
        String policyName = "";
        String policyExtra = "";
        for(Trio policy : ReturnPolicies) {
            if(policy.ID.equals(policyId)) {
                policyName = policy.Label;
                policyExtra = policy.Extra;
                break;
            }
        }

        // Build Dialog
        SettingDialog = new AddSettingExtraDialog();
        Bundle args = new Bundle();
        args.putString("type", "edit");
        args.putString("default_name", policyName);
        args.putString("default_extra", policyExtra);
        SettingDialog.setArguments(args);
        SettingDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
        SettingDialog.show(getFragmentManager(), "edit");
    }

    public void startUpdateTask(String policyName, String policyDetail) {
        SavedReturnPolicy.Label = policyName;
        SavedReturnPolicy.Extra = policyDetail;

        // Dismiss Dialog
        SettingDialog.dismiss();

        // Show Spinner
        spinner = new ProgressDialog(SellerReturnPolicyActivity.this);
        spinner.setMessage("Updating Return Policy...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Update Task
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "3"},
                        new String[]{"return_policy_id", String.valueOf(SavedReturnPolicy.ID)},
                        new String[]{"return_policy_name", policyName},
                        new String[]{"return_policy_details", policyDetail}
                });
        new GetInfoTask(SellerReturnPolicyActivity.this).execute(GetInfoTask.SourceType.updateUserReturnPolicy.toString(), queryStr);
    }

    public void onUpdateTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int return_policy_was_updated = json.getInt("return_policy_was_updated");

            if(return_policy_was_updated > 0) {
                startGetTask();
                Toast.makeText(this, "Successfully Updated Return Policy " + SavedReturnPolicy.Label, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error Updating Return Policy " + SavedReturnPolicy.Label, Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner
        if(spinner != null && spinner.isShowing()) {
            spinner.dismiss();
        }
    }

    public void startDeleteDialog(final int policyId) {
        // Confirm Choice
        new AlertDialog.Builder(this)
                .setTitle("Delete Return Policy?")
                .setMessage("Do You Want To Delete This Policy?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDeleteTask(policyId);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void startDeleteTask(int policyId) {
        // Save ID
        SavedReturnPolicy.ID = policyId;

        // Show Spinner
        spinner = new ProgressDialog(SellerReturnPolicyActivity.this);
        spinner.setMessage("Deleting Return Policy...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Delete Task
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][] {
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "4"},
                        new String[]{"return_policy_id", String.valueOf(SavedReturnPolicy.ID)},
                });
        new GetInfoTask(SellerReturnPolicyActivity.this).execute(GetInfoTask.SourceType.deleteUserReturnPolicy.toString(), queryStr);
    }

    public void onDeleteTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int was_deleted = json.getInt("was_deleted");

             if(was_deleted > 0) {
                startGetTask();
                Toast.makeText(this, "Successfully Deleted Return Policy", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error Deleting Return Policy", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner
        if(spinner != null && spinner.isShowing()) {
            spinner.dismiss();
        }
    }
}