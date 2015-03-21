package com.vie.biddingforcharities.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.vie.biddingforcharities.R;
import com.vie.biddingforcharities.SellerReturnPolicyActivity;
import com.vie.biddingforcharities.logic.Trio;

import java.util.ArrayList;

public class SettingsExtraListAdapter extends ArrayAdapter<Trio> {
    private Context context;
    private ArrayList<Trio> settingPairs;

    public SettingsExtraListAdapter(Context context, ArrayList<Trio> settings) {
        super(context, R.layout.row_user_setting, settings);

        this.context = context;
        this.settingPairs = settings;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View rowView;
        if(convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.row_user_setting, parent, false);
        } else {
            rowView = convertView;
        }

        TextView label = (TextView) rowView.findViewById(R.id.setting_label);
        label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LabelClick(settingPairs.get(position).Extra);
            }
        });
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

    private void LabelClick(String details) {
        // Display Details
        new AlertDialog.Builder(context)
                .setTitle("Details")
                .setMessage(details)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void EditClick(int rowID) {
        if(context instanceof SellerReturnPolicyActivity) {
            ((SellerReturnPolicyActivity) context).startUpdateDialog(rowID);
        }
    }

    private void DeleteClick(int rowID) {
        if(context instanceof SellerReturnPolicyActivity) {
            ((SellerReturnPolicyActivity) context).startDeleteDialog(rowID);
        }
    }
}
