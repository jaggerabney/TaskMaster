package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

    @SuppressLint("NewApi")
    @Override
    public View getGroupView(final int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
        // Gets a reference to the GroupInfo associated with this view, as it's used/updated a lot
        GroupInfo group = (GroupInfo) getGroup(groupPosition);
        // Updates the items remaining using the associated GroupInfo's updateItemsRemaining() method
        // itemsRemaining is an integer that keeps track of how many items in a given group still need to be completed
        // Items count toward the total if they: a) exist, and b) are not checked (i.e. not completed)
        group.updateItemsRemaining();

        // Creates the view if it hasn't been already
        if (view == null) {
            LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = lf.inflate(R.layout.list_group, null);
        }

        // Gets a reference to the TextView associated with this view, and sets its text according to the
        // GroupInfo's name field
        TextView name = view.findViewById(R.id.listTitle);
        name.setText(group.getName());
        // The "(x items)" string is appended to the TextView rather than the GroupInfo's string so as to
        // not affect any methods that may look to the GroupInfo's string for information
        // As of writing this, none exist yet, but they might in the future
        name.append(" (" + group.getItemsRemaining() + " items)");

        // Sets the size of the text in this element's TextView according to the fontSize specified in SharedPrefs
        // By default - if there is no SharedPrefs - this is the Medium option, which (as of writing this) is 16sp
        // Whenever this value is changed in the Settings activity - specifically when the MainActivity resumes via the
        // onResume method - the MainActivity's CoolAdapter.notifyDataSetChanged() is called, which in turn
        // changed the size of all group elements' texts in the ExpandableListView
        // Look to getItemView for the code that changes item text size (spoiler: it's literally the same)
        FontSize preferredFontSize = ma.getFontSize();
        if (preferredFontSize != null) {
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, preferredFontSize.getGroupSizeInSp());
        } else {
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.FONT_SIZE_DEFAULT.getGroupSizeInSp());
        }

        return view;
    }

    @SuppressLint("NewApi")
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        // Gets a reference to the ItemInfo for the item in the group at index groupPosition with index
        // childPosition for cleanliness
        final ItemInfo item = (ItemInfo) getChild(groupPosition, childPosition);

        // Creates the view if it is null (i.e. if it hasn't been created yet)
        if (view == null) {
            LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = lf.inflate(R.layout.list_item, null);
        }

        // TODO: make item views respond visually to clicks!
        view.setLongClickable(true);

        // Gets a reference to the TextView element within this item, for cleanliness
        // Reference is final because it is used in the OnCheckedChangeListener of the view's checkbox
        final TextView name = view.findViewById(R.id.expandedlistitem_text);
        name.setText(item.getName());

        // Gets a reference to the CheckBox element of this view, and sets its OnCheckedChangeListener to null
        // to prevent weird behavior when setChecked() is called
        // Not setting the listener to null before this call results in the checkbox's state not being what it should -
        // checked when it shouldn't be, not checked when it should, etc.
        CheckBox checkbox = view.findViewById(R.id.expandedlistitem_checkbox);
        checkbox.setOnCheckedChangeListener(null);
        checkbox.setChecked(item.isChecked());

        // Strikes out the text in the element's TextView according to whether or not the checkbox is checked
        // if checkbox is checked -> text is striked; if checkbox is not checked -> text is not striked
        name.setPaintFlags(item.isChecked() ? name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        // Sets the size of the text in this element's TextView according to the fontSize specified in SharedPrefs
        // By default - if there is no SharedPrefs - this is the Medium option, which (as of writing this) is 16sp
        // Whenever this value is changed in the Settings activity - specifically when the MainActivity resumes via the
        // onResume method - the MainActivity's CoolAdapter.notifyDataSetChanged() is called, which in turn
        // changed the size of all items (child elements, NOT group elements) in the ExpandableListView
        // Look to getGroupView for the code that changes group text size (spoiler: it's literally the same)
        FontSize preferredFontSize = ma.getFontSize();
        if (preferredFontSize != null) {
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, preferredFontSize.getItemSizeInSp());
        } else {
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.FONT_SIZE_DEFAULT.getItemSizeInSp());
        }

        // Sets the OnCheckedChangeListener for the checkbox of this element
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // First, it sets the checked status of the ItemInfo associated with this view according to the isChecked
                // parameter that is passed whenever onCheckedChanged is called
                // (The ItemInfo for any given view keeps track of the states of the view's text and checkbox, and is
                // necessary to prevent a bug when collapsing/expanding groups changes the state of views' checkboxes)
                item.setChecked(isChecked);
                // Then, the text in the TextView of this view is strike-throughed (is that a word?) according to the
                // state of the checkbox:
                // if checkbox is checked -> text is striked; if checkbox is not checked -> text is not striked
                if (isChecked) {
                    name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                // The adapter is notified of any changes made to the view whenever this is called, so that the text
                // is strike-thru'd in real time
                notifyDataSetChanged();
                // Finally, the state of the newly-checked (or unchecked) checkbox is saved using the MainActivity's save() method
                ma.save(ma.getUserData());
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
