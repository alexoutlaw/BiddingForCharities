package com.vie.biddingforcharities.logic;

public class AuctionItem {
    public String itemImageUrl;
    public String itemTitle;
    public String itemEndDate;
    public double itemHighBid;

    //non-display
    public int itemId;

    public AuctionItem(String imageUrl, String title, String endDate, double highBid, int id) {
        itemImageUrl = imageUrl;
        itemTitle = title;
        itemEndDate = endDate;
        itemHighBid = highBid;
        itemId = id;
    }
}
