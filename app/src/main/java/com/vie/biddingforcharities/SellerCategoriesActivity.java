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

public class SellerCategoriesActivity extends Activity {
    DrawerLayout NavLayout;
    ListView NavList;
    Button HomeButton;
    ImageButton NavDrawerButton;

    ListView CategoryList;
    AddSettingDialog SettingDialog;
    ProgressDialog spinner, getSpinner;
    Button AddCategoryButton;

    ArrayList<Pair> Categories;
    Pair SavedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_categories);

        NavLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavList = (ListView) findViewById(R.id.navList);
        NavDrawerButton = (ImageButton) findViewById(R.id.nav_drawer_expand);
        CategoryList = (ListView) findViewById(R.id.category_list);

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

        // Add Category
        AddCategoryButton = (Button) findViewById(R.id.add_category_button);
        AddCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build Dialog
                SettingDialog = new AddSettingDialog(SellerCategoriesActivity.this, AddSettingDialog.FormType.Create);
                SettingDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
                SettingDialog.show(getFragmentManager(), "add");
            }
        });

        SavedCategory = new Pair("", 0);

        startGetTask();
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
        getSpinner = new ProgressDialog(SellerCategoriesActivity.this);
        getSpinner.setMessage("Getting Categories...");
        getSpinner.setCanceledOnTouchOutside(false);
        getSpinner.show();

        // Load Categories
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "0"}
                });
        new GetInfoTask(SellerCategoriesActivity.this).execute(GetInfoTask.SourceType.getUserCategories.toString(), queryStr);
    }

    public void onGetTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            JSONArray cats = (JSONArray) json.get("cats");

            Categories = new ArrayList<>();
            for (int i = 0; i < cats.length(); i++) {
                final JSONObject cat = (JSONObject) cats.get(i);
                int cat_id = cat.getInt("cat_id");
                String cat_name = cat.getString("cat_name");

                Categories.add(new Pair(cat_name, cat_id));
            }

            // Add to UI
            final ListAdapter adapter = new SettingsListAdapter(this, Categories);
            CategoryList.setAdapter(adapter);

            // Update Cache
            ((Global) getApplication()).getUser().updateCategories(Categories);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner
        if (getSpinner != null && getSpinner.isShowing()) {
            getSpinner.dismiss();
        }
    }

    public void startAddTask(String categoryName) {
        SavedCategory.Label = categoryName;

        // Dismiss Dialog
        SettingDialog.dismiss();

        // Show Spinner
        spinner = new ProgressDialog(SellerCategoriesActivity.this);
        spinner.setMessage("Adding Category...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Add Task
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "1"},
                        new String[]{"parent_cat_id", "0"},
                        new String[]{"cat_name", categoryName}
                });
        new GetInfoTask(SellerCategoriesActivity.this).execute(GetInfoTask.SourceType.addUserCategory.toString(), queryStr);
    }

    public void onAddTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int success = json.getInt("success");

            if(success > 0) {
                startGetTask();
                Toast.makeText(this, "Successfully Added Category " + SavedCategory.Label, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error Adding Category", Toast.LENGTH_LONG).show();
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

    public void startUpdateDialog(int categoryId) {
        // Save ID
        SavedCategory.ID = categoryId;

        // Get Label
        String categoryName = "";
        for(Pair category : Categories) {
            if(category.ID.equals(categoryId)) {
                categoryName = category.Label;
                break;
            }
        }

        // Build Dialog
        SettingDialog = new AddSettingDialog(this, AddSettingDialog.FormType.Edit, categoryName);
        SettingDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
        SettingDialog.show(getFragmentManager(), "edit");
    }

    public void startUpdateTask(String categoryName) {
        SavedCategory.Label = categoryName;

        // Dismiss Dialog
        SettingDialog.dismiss();

        // Show Spinner
        spinner = new ProgressDialog(SellerCategoriesActivity.this);
        spinner.setMessage("Updating Category...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Update Task
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "2"},
                        new String[]{"cat_id", String.valueOf(SavedCategory.ID)},
                        new String[]{"cat_name", categoryName}
                });
        new GetInfoTask(SellerCategoriesActivity.this).execute(GetInfoTask.SourceType.updateUserCategory.toString(), queryStr);
    }

    public void onUpdateTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int was_updated = json.getInt("was_updated");

            if(was_updated > 0) {
                startGetTask();
                Toast.makeText(this, "Successfully Updated Category " + SavedCategory.Label, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error Updating Category " + SavedCategory.Label, Toast.LENGTH_LONG).show();
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

    public void startDeleteDialog(final int categoryId) {
        // Confirm Choice
        new AlertDialog.Builder(this)
                .setTitle("Delete Category?")
                .setMessage("Do You Want To Delete This Category?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDeleteTask(categoryId);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void startDeleteTask(int categoryId) {
        // Save ID
        SavedCategory.ID = categoryId;

        // Show Spinner
        spinner = new ProgressDialog(SellerCategoriesActivity.this);
        spinner.setMessage("Deleting Category...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Delete Task
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][] {
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "3"},
                        new String[]{"cat_id", String.valueOf(SavedCategory.ID)},
                });
        new GetInfoTask(SellerCategoriesActivity.this).execute(GetInfoTask.SourceType.deleteUserCategory.toString(), queryStr);
    }

    public void onDeleteTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int was_deleted = json.getInt("was_deleted");
            int has_auction_items = json.getInt("has_auction_items");
            int has_inventory_items = json.getInt("has_inventory_items");
            int has_children = json.getInt("has_children");

            if (has_auction_items > 0) {
                Toast.makeText(this, "Could not delete category, auction items are attached", Toast.LENGTH_LONG).show();
            } else if(has_inventory_items > 0) {
                Toast.makeText(this, "Could not delete category, inventory items are attached", Toast.LENGTH_LONG).show();
            } else if(has_children > 0) {
                Toast.makeText(this, "Could not delete category, contains child categories", Toast.LENGTH_LONG).show();
            } else if(was_deleted > 0) {
                startGetTask();
                Toast.makeText(this, "Successfully Deleted Category", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error Deleting Category", Toast.LENGTH_LONG).show();
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