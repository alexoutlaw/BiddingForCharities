package com.vie.biddingforcharities.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vie.biddingforcharities.R;
import com.vie.biddingforcharities.logic.AuctionItem;
import com.vie.biddingforcharities.logic.GetBitmapTask;

import java.util.ArrayList;

public class AuctionGridAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<AuctionItem> items;

    public AuctionGridAdapter(Activity activity, ArrayList<AuctionItem> items) {
        super();
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.tile_auction_item, parent, false);
        } else {
            view = convertView;
        }

        ImageView itemImage = (ImageView) view.findViewById(R.id.item_image);
        TextView titleText = (TextView) view.findViewById(R.id.item_title);
        TextView endTimeText = (TextView) view.findViewById(R.id.item_end_time);
        TextView highBidText = (TextView) view.findViewById(R.id.item_high_bid);

        AuctionItem item = items.get(position);

        itemImage.setAdjustViewBounds(true);
        itemImage.setScaleType(ImageView.ScaleType.FIT_XY);
        new GetBitmapTask(activity, itemImage, 0, 200).execute(item.itemImageUrl);

        titleText.setText(item.itemTitle);
        endTimeText.setText(item.itemEndDate);
        highBidText.setText("$" + Double.toString(item.itemHighBid));

        view.setTag(item.itemId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(activity, "Auction Details, Coming Soon!", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
