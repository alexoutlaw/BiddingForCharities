package com.vie.biddingforcharities.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.vie.biddingforcharities.R;
import com.vie.biddingforcharities.SellerCategoriesActivity;
import com.vie.biddingforcharities.SellerFolderActivity;
import com.vie.biddingforcharities.logic.Pair;

import java.util.ArrayList;

public class SettingsListAdapter extends ArrayAdapter<Pair> {
    private Context context;
    private ArrayList<Pair> settingPairs;

    public SettingsListAdapter(Context context, ArrayList<Pair> settings) {
        super(context, R.layout.row_user_setting, settings);

        this.context = context;
        this.settingPairs = settings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;
        if(convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.row_user_setting, parent, false);
        } else {
            rowView = convertView;
        }

        TextView label = (TextView) rowView.findViewById(R.id.setting_label);
        Button editButton = (Button) rowView.findViewById(R.id.setting_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditClick((int) rowView.getTag());
            }
        });
        Button deleteButton = (Button) rowView.findViewById(R.id.setting_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteClick((int) rowView.getTag());
            }
        });

        label.setText(settingPairs.get(position).Label);
        rowView.setTag(settingPairs.get(position).ID);

        return rowView;
    }

    private void EditClick(int rowID) {
        if(context instanceof SellerCategoriesActivity) {
            ((SellerCategoriesActivity) context).startUpdateDialog(rowID);
        } else if(context instanceof SellerFolderActivity) {
            ((SellerFolderActivity) context).startUpdateDialog(rowID);
        }
    }

    private void DeleteClick(int rowID) {
        if(context instanceof SellerCategoriesActivity) {
            ((SellerCategoriesActivity) context).startDeleteDialog(rowID);
        } else if(context instanceof SellerFolderActivity) {
            ((SellerFolderActivity) context).startDeleteDialog(rowID);
        }
    }
}
