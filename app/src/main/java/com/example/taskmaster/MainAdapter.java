package com.example.taskmaster;

import android.content.Context;
import android.graphics.Paint;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// BEWARE: STINKY (AND DEPRECATED)

public class MainAdapter extends BaseExpandableListAdapter {
    Context context;
    List<String> listGroup;
    HashMap<String, List<String>> listItem;
    HashMap<Integer, boolean[]> checkedStates;
    GroupViewHolder groupViewHolder;
    ItemViewHolder itemViewHolder;
    FontSize fontSize;

    public MainAdapter(Context context, List<String> listGroup, HashMap<String, List<String>> listItem) {
        this.context = context;
        this.listGroup = listGroup;
        this.listItem = listItem;
        this.checkedStates = new HashMap<Integer, boolean[]>();
    }

    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listItem.get(this.listGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listItem.get(this.listGroup.get(groupPosition))
                .get(childPosition);
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
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String group = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.groupText = (TextView) convertView.findViewById(R.id.listTitle);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.groupText.setText(group);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String child = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            itemViewHolder = new ItemViewHolder();
            itemViewHolder.itemText = (TextView) convertView.findViewById(R.id.expandedlistitem_text);
            itemViewHolder.itemCheckbox = (CheckBox) convertView.findViewById(R.id.expandedlistitem_checkbox);
            convertView.setTag(R.layout.list_item, itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) convertView.getTag(R.layout.list_item);
        }

        itemViewHolder.itemText.setText(child);
        itemViewHolder.itemCheckbox.setOnCheckedChangeListener(null);

        if (checkedStates.containsKey(groupPosition)) {
            boolean[] getChecked = checkedStates.get(groupPosition);
            if (getChecked != null) itemViewHolder.itemCheckbox.setChecked(getChecked[childPosition]);
        } else {
            boolean[] getChecked = new boolean[getChildrenCount(groupPosition)];
            checkedStates.put(groupPosition, getChecked);
            itemViewHolder.itemCheckbox.setChecked(false);
        }

        // no shit, this took me like three hours to figure out. kill me
        itemViewHolder.itemCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                boolean[] getChecked = checkedStates.get(groupPosition);
                if (getChecked != null) {
                    getChecked[childPosition] = isChecked;
                }
                checkedStates.put(groupPosition, getChecked);

                if (isChecked)
                    itemViewHolder.itemText.setPaintFlags(itemViewHolder.itemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    itemViewHolder.itemText.setPaintFlags(itemViewHolder.itemText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public List<String> getListGroup() {
        return listGroup;
    }

    public final class GroupViewHolder {
        TextView groupText;
    }

    public final class ItemViewHolder {
        TextView itemText;
        CheckBox itemCheckbox;
    }
}
