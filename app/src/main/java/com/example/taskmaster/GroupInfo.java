package com.example.taskmaster;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupInfo implements Serializable {
    private String name;
    private ArrayList<ItemInfo> list;
    private int itemsRemaining;

    public GroupInfo(String name) {
        this.name = name;
        this.list = new ArrayList<>();
        this.itemsRemaining = 0;
    }

    public void updateItemsRemaining() {
        itemsRemaining = 0;
        for (ItemInfo ii : list) {
            if (!ii.isChecked()) {
                itemsRemaining++;
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ItemInfo> getList() {
        return list;
    }

    public void setList(ArrayList<ItemInfo> list) {
        this.list = list;
    }

    public int getItemsRemaining() {
        return itemsRemaining;
    }

    public void setItemsRemaining(int itemsRemaining) {
        this.itemsRemaining = itemsRemaining;
    }
}
