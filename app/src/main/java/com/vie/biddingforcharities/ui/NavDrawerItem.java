package com.vie.biddingforcharities.ui;

import com.vie.biddingforcharities.logic.User;

public class NavDrawerItem {

    private String Title;
    private int Icon;
    public User.UserTypes PermissionLevel;
    Class LinkedActivity;

    public NavDrawerItem(){}

    public NavDrawerItem(String title, int icon, User.UserTypes permissionLevel, Class linkedActivity){
        Title = title;
        Icon = icon;
        PermissionLevel = permissionLevel;
        LinkedActivity = linkedActivity;
    }

    public String getTitle(){
        return Title;
    }

    public int getIcon(){
        return Icon;
    }

    public Class getLinkedActivity() {
        return LinkedActivity;
    }

    public void setTitle(String title){
        Title = title;
    }

    public void setIcon(int icon){
        Icon = icon;
    }

    @Override
    public String toString() {
        return Title;
    }
}
