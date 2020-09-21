package com.example.taskmaster;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
        GroupInfo group = (GroupInfo) getGroup(groupPosition);
        group.updateItemsRemaining();

        if (view == null) {
            LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = lf.inflate(R.layout.list_group, null);
        }

        TextView name = view.findViewById(R.id.listTitle);
        name.setText(group.getName());
        name.append(" (" + group.getItemsRemaining() + " items)");

        if (ma.getFontSize() != null) {
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, ma.getFontSize().getGroupSizeInSp());
        } else {
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.FONT_SIZE_DEFAULT.getGroupSizeInSp());
        }

        return view;
    }

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
        if (ma.getFontSize() != null) {
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, ma.getFontSize().getItemSizeInSp());
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
                ma.save();
            }
        });

        // Gets a reference to the delete button within this view, and defines its onClick method
        // It's important to note that, since there is no guarantee as to how many or few elements will be in an
        // ExpandableListView, all of its views (groups and items) are generated programmatically
        // This is why an OnClickListener needs to be used, as opposed to an onClick field in the view's .xml file
        // The only reference you'll get to the image button will be upon its creation/mutation
        ImageButton deleteButton = view.findViewById(R.id.expandedlistitem_deletebutton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int elementType = ExpandableListView.getPackedPositionType(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                DeleteItemDialogFragment didf = new DeleteItemDialogFragment(groupPosition, childPosition, elementType);
                didf.show(((MainActivity)view.getContext()).getSupportFragmentManager(), "delete_item");
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
