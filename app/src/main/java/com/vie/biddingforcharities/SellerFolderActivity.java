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
import com.vie.biddingforcharities.logic.Pair;
import com.vie.biddingforcharities.logic.User;
import com.vie.biddingforcharities.logic.Utilities;
import com.vie.biddingforcharities.ui.AddSettingDialog;
import com.vie.biddingforcharities.ui.SettingsListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SellerFolderActivity extends Activity {
    DrawerLayout NavLayout;
    ListView NavList;
    Button HomeButton;
    ImageButton NavDrawerButton;

    ListView FolderList;
    AddSettingDialog SettingDialog;
    ProgressDialog spinner, getSpinner;
    Button AddFolderButton;

    ArrayList<Pair> Folders;
    Pair SavedFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_folder);

        NavLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavList = (ListView) findViewById(R.id.navList);
        NavDrawerButton = (ImageButton) findViewById(R.id.nav_drawer_expand);
        FolderList = (ListView) findViewById(R.id.folder_list);

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

        // Add Folder
        AddFolderButton = (Button) findViewById(R.id.add_folder_button);
        AddFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build Dialog
                SettingDialog = new AddSettingDialog(SellerFolderActivity.this, AddSettingDialog.FormType.Create);
                SettingDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
                SettingDialog.show(getFragmentManager(), "add");
            }
        });

        SavedFolder = new Pair("", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startGetTask();
    }

    public void startGetTask() {
        if (getSpinner != null && getSpinner.isShowing()) {
            getSpinner.dismiss();
        }
        // Show Spinner
        getSpinner = new ProgressDialog(SellerFolderActivity.this);
        getSpinner.setMessage("Getting Folders...");
        getSpinner.setCanceledOnTouchOutside(false);
        getSpinner.show();

        // Load Folders
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "0"}
                });
        new GetInfoTask(SellerFolderActivity.this).execute(GetInfoTask.SourceType.getUserFolders.toString(), queryStr);
    }

    public void onGetTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            JSONArray folders = (JSONArray) json.get("folders");

            Folders = new ArrayList<>();
            for(int i = 0; i < folders.length(); i++) {
                final JSONObject folder = (JSONObject) folders.get(i);
                int folder_id = folder.getInt("folder_id");
                String crumb = folder.getString("crumb");
                //indent characters messes up query string
                crumb = crumb.replace(">", "").trim();

                Folders.add(new Pair(crumb, folder_id));
            }

            // Add to UI
            final ListAdapter adapter = new SettingsListAdapter(this, Folders);
            FolderList.setAdapter(adapter);

            // Update Cache
            ((Global) getApplication()).getUser().updateFolders(Folders);
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner
        if(getSpinner != null && getSpinner.isShowing()) {
            getSpinner.dismiss();
        }
    }

    public void startAddTask(String folderName) {
        SavedFolder.Label = folderName;

        // Dismiss Dialog
        SettingDialog.dismiss();

        // Show Spinner
        spinner = new ProgressDialog(SellerFolderActivity.this);
        spinner.setMessage("Adding Folder...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Add Task
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "1"},
                        new String[]{"parent_folder_id", "0"},
                        new String[]{"new_folder_name", folderName}
                });
        new GetInfoTask(SellerFolderActivity.this).execute(GetInfoTask.SourceType.addUserFolder.toString(), queryStr);
    }

    public void onAddTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int was_inserted = json.getInt("was_inserted");

            if(was_inserted > 0) {
                startGetTask();
                Toast.makeText(this, "Successfully Added Folder " + SavedFolder.Label, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error Adding Folder", Toast.LENGTH_LONG).show();
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

    public void startUpdateDialog(int folderId) {
        // Save ID
        SavedFolder.ID = folderId;

        // Get Label
        String folderName = "";
        for(Pair folder : Folders) {
            if(folder.ID.equals(folderId)) {
                folderName = folder.Label;
                break;
            }
        }

        // Build Dialog
        SettingDialog = new AddSettingDialog(this, AddSettingDialog.FormType.Edit, folderName);
        SettingDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
        SettingDialog.show(getFragmentManager(), "edit");
    }

    public void startUpdateTask(String folderName) {
        SavedFolder.Label = folderName;

        // Dismiss Dialog
        SettingDialog.dismiss();

        // Show Spinner
        spinner = new ProgressDialog(SellerFolderActivity.this);
        spinner.setMessage("Updating Folder...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Update Task
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "2"},
                        new String[]{"update_folder_id", String.valueOf(SavedFolder.ID)},
                        new String[]{"new_folder_name", folderName}
                });
        new GetInfoTask(SellerFolderActivity.this).execute(GetInfoTask.SourceType.updateUserFolder.toString(), queryStr);
    }

    public void onUpdateTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int was_updated = json.getInt("was_updated");

            if(was_updated > 0) {
                startGetTask();
                Toast.makeText(this, "Successfully Updated Folder " + SavedFolder.Label, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error Updating Folder " + SavedFolder.Label, Toast.LENGTH_LONG).show();
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

    public void startDeleteDialog(final int folderId) {
        // Confirm Choice
        new AlertDialog.Builder(this)
                .setTitle("Delete Folder?")
                .setMessage("Do You Want To Delete This Folder?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDeleteTask(folderId);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void startDeleteTask(int folderId) {
        // Save ID
        SavedFolder.ID = folderId;

        // Show Spinner
        spinner = new ProgressDialog(SellerFolderActivity.this);
        spinner.setMessage("Deleting Folder...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Delete Task
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][] {
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "3"},
                        new String[]{"delete_folder_id", String.valueOf(SavedFolder.ID)},
                });
        new GetInfoTask(SellerFolderActivity.this).execute(GetInfoTask.SourceType.deleteUserFolder.toString(), queryStr);
    }

    public void onDeleteTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int was_deleted = json.getInt("was_deleted");
            int has_inventory_items = json.getInt("has_inventory_items");
            int has_children = json.getInt("has_children");

            if(has_inventory_items > 0) {
                Toast.makeText(this, "Could not delete folder, inventory items are attached", Toast.LENGTH_LONG).show();
            } else if(has_children > 0) {
                Toast.makeText(this, "Could not delete folder, contains child folders", Toast.LENGTH_LONG).show();
            } else if(was_deleted > 0) {
                startGetTask();
                Toast.makeText(this, "Successfully Deleted Folder", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error Deleting Folder", Toast.LENGTH_LONG).show();
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