package com.example.taskmaster;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupInfo implements Serializable {
    private String name;
    private ArrayList<ItemInfo> list;

    public GroupInfo(String name) {
        this.name = name;
        this.list = new ArrayList<>();
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
}
