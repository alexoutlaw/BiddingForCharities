package com.vie.biddingforcharities.logic;

import java.util.ArrayList;

/**
 * Created by Alex Outlaw on 2/19/2015.
 */
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
    ArrayList<String[]> Categories;
    ArrayList<String[]> Folders;
    ArrayList<String[]> Consignors;
    ArrayList<String[]> ReturnPolicies;
    ArrayList<String[]> PaymentPolicies;

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

        ArrayList<String[]> Categories = new ArrayList<>();
        ArrayList<String[]> Folders = new ArrayList<>();
        ArrayList<String[]> Consignors = new ArrayList<>();
        ArrayList<String[]> ReturnPolicies = new ArrayList<>();
        ArrayList<String[]> PaymentPolicies = new ArrayList<>();
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
    public ArrayList getCategories() { return Categories; }
    public ArrayList getFolders() { return Folders; }
    public ArrayList getConsignors() { return Consignors; }
    public ArrayList getReturnPolicies() { return ReturnPolicies; }
    public ArrayList getPaymentPolicies() { return PaymentPolicies; }


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

    public void updateCategories(ArrayList<String[]> categories) { Categories = categories; }
    public void updateFolders(ArrayList<String[]> folders) { Folders = folders; }
    public void updateConsignors(ArrayList<String[]> consignors) { Consignors = consignors; }
    public void updateReturnPolicies(ArrayList<String[]> returnPolicies) { ReturnPolicies = returnPolicies; }
    public void updatePaymentPolcies(ArrayList<String[]> paymentPolicies) { PaymentPolicies = paymentPolicies; }
}
