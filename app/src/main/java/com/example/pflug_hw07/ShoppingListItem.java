package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Group25_HW07 ShoppingListItem.java
 * Full Name: Kristin Pflug
 */

public class ShoppingListItem {

    String itemName;
    double itemPrice;
    boolean itemAcquired;
    String itemID;

    public ShoppingListItem() {
    }

    public ShoppingListItem(String itemName, double itemPrice, boolean itemAcquired, String itemID) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemAcquired = itemAcquired;
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public boolean isItemAcquired() {
        return itemAcquired;
    }

    public void setItemAcquired(boolean itemAcquired) {
        this.itemAcquired = itemAcquired;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }
}
