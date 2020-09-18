package com.example.taskmaster;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListItem extends LinearLayout  {
    TextView text;
    CheckBox checkbox;

    public ListItem(final Context context) {
        super(context);
        inflate(context, R.layout.list_item, this);
        text = findViewById(R.id.expandedlistitem_text);
        checkbox = findViewById(R.id.expandedlistitem_checkbox);
    }
}
