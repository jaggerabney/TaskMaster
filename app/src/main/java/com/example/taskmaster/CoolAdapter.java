package com.example.taskmaster;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CoolAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<GroupInfo> groupList;
    private MainActivity ma;

    public CoolAdapter(Context context, ArrayList<GroupInfo> groupList) {
        this.context = context;
        this.groupList = groupList;
        this.ma = (MainActivity) context;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ItemInfo> items = groupList.get(groupPosition).getList();
        return items.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ItemInfo> items = groupList.get(groupPosition).getList();
        return items.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
        GroupInfo group = (GroupInfo) getGroup(groupPosition);

        if (view == null) {
            LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = lf.inflate(R.layout.list_group, null);
        }

        TextView name = view.findViewById(R.id.listTitle);
        name.setText(group.getName());
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        final ItemInfo item = (ItemInfo) getChild(groupPosition, childPosition);

        if (view == null) {
            LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = lf.inflate(R.layout.list_item, null);
        }

        final TextView name = view.findViewById(R.id.expandedlistitem_text);
        name.setText(item.getName());

        CheckBox checkbox = view.findViewById(R.id.expandedlistitem_checkbox);
        checkbox.setOnCheckedChangeListener(null); // prevents weird behavior. don't question it; idk either
        checkbox.setChecked(item.isChecked());

        // holy shit i can't believe i got this to work first try
        name.setPaintFlags(item.isChecked() ? name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                item.setChecked(isChecked);
                if (isChecked)
                    name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                ma.save();
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
