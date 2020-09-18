package com.example.taskmaster;

import java.io.Serializable;

public class ItemInfo implements Serializable {
    private String name;
    private boolean checked;

    public ItemInfo(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
