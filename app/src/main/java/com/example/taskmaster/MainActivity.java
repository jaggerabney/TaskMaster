package com.example.taskmaster;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/* TODO: Version 1.0 is done! Good job. A few (of many) things for 1.1:
            - Upload v1.0 to github! You'll hate doing it, but your future self will thank you
            - Add an option to remove items from groups, checked or not
            - Increase overall font size? It's kinda small - settings menu maybe?
            - Implement a counter for the number of items in a group, and display it in the group's name
            - Bundle groups into days, and add the ability to switch between days
            - Also implement a counter for the total number of items to do in a day
            - Ability to switch between days via the calendar menu icon? Will require multiple files; that's okay
 */

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_CHOICES = "com.example.android.taskmaster.extra.CHOICES";
    public static final int TEXT_REQUEST_ITEM = 1;
    public static final int TEXT_REQUEST_GROUP = 2;

    private FloatingActionButton fab_addGroup, fab_addItem;
    private CardView fab_addGroup_card, fab_addItem_card;
    private TextView fab_addGroup_cardText, fab_addItem_cardText;
    private Animation fab_open, fab_close;
    private String userData;
    ExpandableListView expandableListView;
    LinkedHashMap<String, GroupInfo> listItem;
    ArrayList<GroupInfo> listGroup;
    CoolAdapter ca;

    boolean fabIsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();

        expandableListView = findViewById(R.id.expandableListView);
        listGroup = new ArrayList<>();
        listItem = new LinkedHashMap<>();
        ca = new CoolAdapter(this, listGroup);
        expandableListView.setAdapter(ca);
        userData = getResources().getString(R.string.user_data_filename);

        load();
        initFab();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        // this has to be done programmatically because things can't be easy
        toolbar.getOverflowIcon().setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab_addGroup = findViewById(R.id.fab_addGroup);
        fab_addItem = findViewById(R.id.fab_addItem);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_addGroup_card = findViewById(R.id.fab_addGroup_card);
        fab_addGroup_cardText = findViewById(R.id.fab_addGroup_card_text);
        fab_addItem_card = findViewById(R.id.fab_addItem_card);
        fab_addItem_cardText = findViewById(R.id.fab_addItem_card_text);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (fabIsOpen) {
                    fab_addGroup.startAnimation(fab_close);
                    fab_addItem.startAnimation(fab_close);
                    fab_addGroup_card.startAnimation(fab_close);
                    fab_addGroup_cardText.setVisibility(View.INVISIBLE);
                    fab_addItem_card.startAnimation(fab_close);
                    fab_addItem_cardText.setVisibility(View.INVISIBLE);
                    fab_addGroup.setClickable(false);
                    fab_addItem.setClickable(false);
                    fabIsOpen = false;
                } else {
                    fab_addGroup.setVisibility(View.VISIBLE);
                    fab_addItem.setVisibility(View.VISIBLE);
                    fab_addGroup_card.setVisibility(View.VISIBLE);
                    fab_addItem_card.setVisibility(View.VISIBLE);
                    fab_addGroup_cardText.setVisibility(View.VISIBLE);
                    fab_addItem_cardText.setVisibility(View.VISIBLE);
                    fab_addGroup.startAnimation(fab_open);
                    fab_addItem.startAnimation(fab_open);
                    fab_addGroup_card.startAnimation(fab_open);
                    fab_addItem_card.startAnimation(fab_open);
                    fab_addGroup.setClickable(true);
                    fab_addItem.setClickable(true);
                    fabIsOpen = true;
                }
            }
        });

        fab_addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAddGroupActivity();
            }
        });

        fab_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listGroup.isEmpty()) {
                    launchAddItemActivity();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_warning, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEXT_REQUEST_ITEM && resultCode == RESULT_OK) {
            String item = data.getStringExtra(AddItemActivity.EXTRA_REPLY);
            String group = data.getStringExtra(AddItemActivity.EXTRA_CHOICE);
            GroupInfo groupInfo = findObjectWithNameIn(group, listGroup);
            for (ItemInfo ii : groupInfo.getList()) {
                if (item != null && ii.getName().contentEquals(item)) {
                    Toast.makeText(this, R.string.error_warning, Toast.LENGTH_LONG).show();
                    return;
                }
            }
            addItem(new ItemInfo(item, false), groupInfo);
            ca.notifyDataSetChanged();
        } else if (requestCode == TEXT_REQUEST_GROUP && resultCode == RESULT_OK) {
            String group = data.getStringExtra(AddGroupActivity.EXTRA_GROUP);
            for (GroupInfo gi : listGroup) {
                if (group != null && gi.getName().contentEquals(group)) {
                    Toast.makeText(this, R.string.error_warning, Toast.LENGTH_LONG).show();
                    return;
                }
            }
            addGroup(new GroupInfo(group));
            ca.notifyDataSetChanged();
        } else Toast.makeText(this, R.string.error_warning, Toast.LENGTH_LONG).show();
        save();
    }

    private GroupInfo findObjectWithNameIn(String name, ArrayList<GroupInfo> list) {
        for (GroupInfo gi : list) {
            if (gi.getName() != null && gi.getName().contentEquals(name)) {
                return gi;
            }
        }
        return null;
    }

    public void save() {
        try {
            FileOutputStream fos = this.openFileOutput(userData, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for (GroupInfo gi : listGroup) {
                oos.writeObject(gi);
            }
            oos.close();
        } catch (Exception e) {
            Toast.makeText(this, "Couldn't save!", Toast.LENGTH_LONG).show();
        }
    }

    public void load() {
        try {
            FileInputStream fis = this.openFileInput(userData);
            ObjectInputStream ois = new ObjectInputStream(fis);
            boolean keepReading = true;
            GroupInfo gi;
            try {
                while (keepReading) {
                    gi = (GroupInfo) ois.readObject();
                    listGroup.add(gi);
                }
            } catch (EOFException e) {
                keepReading = false;
            }
            ois.close();
        } catch (Exception e) {
            Toast.makeText(this, "Couldn't load!", Toast.LENGTH_LONG).show();
        }
    }

    public void delete() throws IOException {
        File file = new File(getApplicationContext().getFilesDir() + File.separator + userData);
        if (file.delete()) {
            file.createNewFile();
        } else {
            Toast.makeText(this, "File couldn't be deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchAddItemActivity() {
        Intent intent = new Intent(this, AddItemActivity.class);
        intent.putExtra(EXTRA_CHOICES, getAllNamesInGroupInfo());
        startActivityForResult(intent, TEXT_REQUEST_ITEM);
    }

    private void launchAddGroupActivity() {
        Intent intent = new Intent(this, AddGroupActivity.class);
        startActivityForResult(intent, TEXT_REQUEST_GROUP);
    }

    // Adds a new group to the end of the ExpandableListView
    private void addGroup(GroupInfo group) {
        listGroup.add(group);
        listItem.put(listGroup.get(listGroup.size() - 1).getName(), listGroup.get(listGroup.size() - 1));
        ca.notifyDataSetChanged();
    }

    private void addItem(ItemInfo item, GroupInfo group) {
        if (group.getList() != null) {
            group.getList().add(item);
        }
        ca.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_delete_data:
                DialogFragment df = new DeleteDataDialogFragment();
                df.show(getSupportFragmentManager(), "delete_data");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private String[] getAllNamesInGroupInfo() {
        ArrayList<String> groupNames = new ArrayList<>();
        for (GroupInfo gi : listGroup) {
            groupNames.add(gi.getName());
        }
        return groupNames.toArray(new String[0]);
    }

    public CoolAdapter getAdapter() {
        return ca;
    }

    public void setAdapter(CoolAdapter ca) {
        expandableListView.setAdapter(ca);
    }

    public ArrayList<GroupInfo> getListGroup() {
        return listGroup;
    }

    public void setListGroup(ArrayList<GroupInfo> listGroup) {
        this.listGroup = listGroup;
    }
}

/* Dev log, 9-13-20: It took me five days to get the fab working with activities for adding groups and items
                  ...this is gonna be a long project, isn't it?
            9-15-20: Fixed the problem where expanding/collapsing groups would invert the state of the checkboxes
                     Whoever designed that deserves to be shot, but w/e
                     The beginning of school is approaching soon, and I'm afraid I'll run out of time to finish
                     this project. Worse comes to worst, I can just use a planner lmao
            9-17-20: Finished v1.0 Surprising how fast it came together...especially consider that I played
                     games for a majority of last night. Maybe I will get this done on time...
 */