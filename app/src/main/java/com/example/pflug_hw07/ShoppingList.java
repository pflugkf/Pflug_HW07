package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Group25_HW07 ShoppingList.java
 * Full Name: Kristin Pflug
 */

import java.util.ArrayList;

public class ShoppingList {
    String listName;
    String listOwner;
    ArrayList<String> invitedUsers;
    String listID;
    double totalCost;

    public ShoppingList() {
        this.listName = "list";
        this.listOwner = "0";
        this.invitedUsers = new ArrayList<>();
        this.listID = "1";
        totalCost = 0.0;
    }

    public ShoppingList(String listName, String listOwner, ArrayList<String> invitedUsers, String listID, double totalCost) {
        this.listName = listName;
        this.listOwner = listOwner;
        this.invitedUsers = invitedUsers;
        this.listID = listID;
        this.totalCost = totalCost;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListOwner() {
        return listOwner;
    }

    public void setListOwner(String listOwner) {
        this.listOwner = listOwner;
    }

    public ArrayList<String> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(ArrayList<String> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
