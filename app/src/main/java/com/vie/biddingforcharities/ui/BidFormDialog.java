package com.vie.biddingforcharities.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.vie.biddingforcharities.AuctionItemActivity;
import com.vie.biddingforcharities.R;

public class BidFormDialog extends DialogFragment {
    EditText BidAmmountText;
    Button FormSubmitButton;

    public BidFormDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final AuctionItemActivity activity = (AuctionItemActivity) getActivity();

        View DialogView = inflater.inflate(R.layout.dialog_itembid, container);

        // Bid Amount Input
        BidAmmountText = (EditText) DialogView.findViewById(R.id.dialog_bid_amount);

        // Sumbit Button
        FormSubmitButton = (Button) DialogView.findViewById(R.id.dialog_bid_button);
        FormSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bidAmount = BidAmmountText.getText().toString();
                Float bitNum = Float.parseFloat(bidAmount);
                activity.startBidTask(bitNum);
            }
        });

        //Allow scrolling
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // Prevent automatic keyboard
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return DialogView;
    }
}
