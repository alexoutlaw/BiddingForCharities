package com.vie.biddingforcharities.netcode;

import java.util.UUID;

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
    int AddressId;
    boolean HasSellerInvoiceDefaults;

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
    public int getAddressId() {
        return AddressId;
    }
    public boolean getHasInvoiceDefaults() {
        return HasSellerInvoiceDefaults;
    }
}
