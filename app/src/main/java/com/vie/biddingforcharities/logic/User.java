package com.vie.biddingforcharities.logic;

import java.util.ArrayList;

public final class User {
    public enum UserTypes { STANDARD, SELLER, SELLER_COSIGNOR }

    String Email;
    int UserID;
    String UserGuid;
    UserTypes UserType;
    int UserNameId;
    String UserName;
    String FirstName;
    String LastName;
    int AddressId;
    boolean HasSellerInvoiceDefaults;

    // Unsaved Session Cache
    ArrayList<Integer> WatchlistItemIDs;
    ArrayList<Pair> Categories;
    ArrayList<Pair> Folders;
    ArrayList<Pair> Consignors;
    ArrayList<Trio> ReturnPolicies;
    ArrayList<Pair> PaymentPolicies;

    public User(String email, int userId, String userGuid, int type, int nameId, String name, int addressId, boolean hasInvoiceDefaults) throws Exception {
        Email = email;
        UserID = userId;
        UserGuid = userGuid;

        switch(type) {
            case 0:
                UserType = UserTypes.STANDARD;
                break;
            case 1:
                UserType = UserTypes.SELLER;
                break;
            case 2:
                UserType = UserTypes.SELLER_COSIGNOR;
                break;
            default:
                throw new Exception("Invalid User Type");
        }

        UserNameId = nameId;
        UserName = name;
        AddressId = addressId;
        HasSellerInvoiceDefaults = hasInvoiceDefaults;

        // Default null, flag if has been set
        WatchlistItemIDs = null;
        Categories = null;
        Folders = null;
        Consignors = null;
        ReturnPolicies = null;
        PaymentPolicies = null;
    }

    public String getEmail() {
        return Email;
    }
    public int getUserID() {
        return UserID;
    }
    public String getUserGuid() {
        return UserGuid;
    }
    public UserTypes getUserType() {
        return UserType;
    }
    public int getUserNameId() {
        return UserNameId;
    }
    public String getUserName() {
        return UserName;
    }
    public String getFirstName() { return FirstName; }
    public String getLastName() { return LastName; }
    public int getAddressId() {
        return AddressId;
    }
    public boolean getHasInvoiceDefaults() {
        return HasSellerInvoiceDefaults;
    }
    public ArrayList<Integer> getWatchlistItemIds() { return WatchlistItemIDs; }
    public ArrayList<Pair> getCategories() { return Categories; }
    public ArrayList<Pair> getFolders() { return Folders; }
    public ArrayList<Pair> getConsignors() { return Consignors; }
    public ArrayList<Trio> getReturnPolicies() { return ReturnPolicies; }
    public ArrayList<Pair> getPaymentPolicies() { return PaymentPolicies; }


    public void updateFullName(String first, String last) {
        FirstName = first;
        LastName = last;
    }

    public void updateUserName(String userName, int userNameId) {
        UserName = userName;
        UserNameId = userNameId;
    }

    public void updateEmail(String newEmail) {
        Email = newEmail;
    }

    public void updateAddressId(int addressId) {
        AddressId = addressId;
    }

    public void updateWatchlistItemIds(ArrayList<Integer> itemIds) { WatchlistItemIDs = itemIds; }
    public void updateCategories(ArrayList<Pair> categories) { Categories = categories; }
    public void updateFolders(ArrayList<Pair> folders) { Folders = folders; }
    public void updateConsignors(ArrayList<Pair> consignors) { Consignors = consignors; }
    public void updateReturnPolicies(ArrayList<Trio> returnPolicies) { ReturnPolicies = returnPolicies; }
    public void updatePaymentPolcies(ArrayList<Pair> paymentPolicies) { PaymentPolicies = paymentPolicies; }

    public boolean HasPermission(User.UserTypes permission) {
        switch (UserType) {
            case STANDARD:
                return !(permission == UserTypes.SELLER || permission == UserTypes.SELLER_COSIGNOR);
            case SELLER:
                return permission != UserTypes.SELLER_COSIGNOR;
            case SELLER_COSIGNOR:
                return true;
            default:
                return false;
        }
    }
}
