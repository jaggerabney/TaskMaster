package com.example.taskmaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

/*  TODO: fix bug where days are not kept track of between activities
          add snackbars to indicate that days are loaded (makes it more obvious when they're empty)
          import uncompleted activities to subsequent days (upon creation? addition of item?)
          add an 'add items...' activity once the above are completed
          refactor? lol
 */

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_CHOICES = "com.example.android.taskmaster.extra.CHOICES";
    public static final String DATE_FORMAT = "MM/dd/yy";
    public static final String USERDATA_FILE_EXTENSION = ".txt";
    public static final int TEXT_REQUEST_ITEM = 1;
    public static final int TEXT_REQUEST_GROUP = 2;
    public static final FontSize FONT_SIZE_DEFAULT = FontSize.MEDIUM;

    private FloatingActionButton fab_addGroup, fab_addItem;
    private CardView fab_addGroup_card, fab_addItem_card;
    private TextView fab_addGroup_cardText, fab_addItem_cardText;
    private Animation fab_open, fab_close;
    private String userData;

    ExpandableListView expandableListView;
    LinkedHashMap<String, GroupInfo> listItem;
    ArrayList<GroupInfo> listGroup;
    CoolAdapter ca;
    FontSize fontSize;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    Calendar calendar;

    boolean fabIsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();

        expandableListView = findViewById(R.id.expandableListView);
        listGroup = new ArrayList<>();
        listItem = new LinkedHashMap<>();
        calendar = Calendar.getInstance();
        ca = new CoolAdapter(this, listGroup);
        expandableListView.setAdapter(ca);
        registerForContextMenu(expandableListView);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPrefs.edit();

        if (sharedPrefs.getString("user_data", null) != null) {
            userData = sharedPrefs.getString("user_data", null);
        } else {
            userData = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
                    .format(calendar.getTime()).replace('/', '-').concat(USERDATA_FILE_EXTENSION);
        }

        load(userData);
        initFab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String fontSizeAsString = sharedPrefs.getString(getString(R.string.sharedprefs_fontsize_key), MainActivity.FONT_SIZE_DEFAULT.name());
        FontSize fontSize = FontSize.valueOf(fontSizeAsString);
        setFontSize(fontSize);
        fab_addGroup_cardText.setTextSize(fontSize.getCardSizeInSp());
        fab_addItem_cardText.setTextSize(fontSize.getCardSizeInSp());
        ca.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        int elementType = ExpandableListView.getPackedPositionType(info.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

        switch (item.getTitle().toString()) {
            case "Rename":
                RenameItemDialogFragment ridf = new RenameItemDialogFragment(groupPosition, childPosition, elementType);
                ridf.show(getSupportFragmentManager(), "rename_item");
                return true;
            case "Delete":
                DeleteItemDialogFragment didf = new DeleteItemDialogFragment(groupPosition, childPosition, elementType);
                didf.show(getSupportFragmentManager(), "delete_item");
                return true;
            default:
                return false;
        }
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
        } else if (requestCode == TEXT_REQUEST_GROUP && resultCode == RESULT_OK) {
            String group = data.getStringExtra(AddGroupActivity.EXTRA_GROUP);
            for (GroupInfo gi : listGroup) {
                if (group != null && gi.getName().contentEquals(group)) {
                    Toast.makeText(this, R.string.error_warning, Toast.LENGTH_LONG).show();
                    return;
                }
            }
            addGroup(new GroupInfo(group));
        } else Toast.makeText(this, R.string.error_warning, Toast.LENGTH_LONG).show();
        save(userData);
    }

    private GroupInfo findObjectWithNameIn(String name, ArrayList<GroupInfo> list) {
        for (GroupInfo gi : list) {
            if (gi.getName() != null && gi.getName().contentEquals(name)) {
                return gi;
            }
        }
        return null;
    }

    public void save(String filename) {
        try {
            File file = new File(getFilesDir(), filename);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            for (GroupInfo gi : listGroup) {
                oos.writeObject(gi);
            }

            oos.close();
        } catch (Exception e) {
            Toast.makeText(this, userData, Toast.LENGTH_LONG).show();
        }
    }

    public void load(String filename) {
        // clears the listGroup so that when notifyDataSetChanged() is called, the screen clears
        listGroup.clear();
        File file = new File(getFilesDir(), filename);

        // if the file's empty, notify the adapter and bail out of the function
        if (file.length() == 0) {
            ca.notifyDataSetChanged();
            return;
        }

        try {
            FileInputStream fis = this.openFileInput(filename);
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
            try {
                file.createNewFile();
                load(filename);
            } catch (IOException ioe) {
                Toast.makeText(this, R.string.error_load, Toast.LENGTH_LONG).show();
            }
        }

        ca.notifyDataSetChanged();
    }

    public void delete(String filename) throws IOException {
        File file = new File(getApplicationContext().getFilesDir() + File.separator + userData);
        if (file.delete()) {
            file.createNewFile();
        }
        // recreate() is called after this, so there's no point in including a toast
    }

    public void rename(int groupPosition, int childPosition, int elementType, String name) {
        switch (elementType) {
            case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
                if (!Arrays.asList(getAllNamesInGroupInfo()).contains(name)) {
                    listGroup.get(groupPosition).setName(name);
                    ca.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, R.string.error_warning, Toast.LENGTH_SHORT).show();
                }
                break;
            case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
                if (!listGroup.get(groupPosition).getNames().contains(name)) {
                    listGroup.get(groupPosition).getList().get(childPosition).setName(name);
                    ca.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, R.string.error_warning, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, R.string.error_warning, Toast.LENGTH_SHORT).show();
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

    public void deleteItemFromList(int groupPosition, int childPosition) {
        Toast.makeText(this, R.string.info_item_delete, Toast.LENGTH_SHORT).show();
        listGroup.get(groupPosition).getList().remove(childPosition);
        listGroup.get(groupPosition).updateItemsRemaining();
        save(userData);
        ca.notifyDataSetChanged();
    }

    public void deleteGroupFromList(int groupPosition) {
        Toast.makeText(this, R.string.info_group_delete, Toast.LENGTH_SHORT).show();
        listGroup.remove(groupPosition);
        save(userData);
        ca.notifyDataSetChanged();
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
            case R.id.action_calendar:
                DatePickerFragment dpf = new DatePickerFragment();
                dpf.show(getSupportFragmentManager(), "select_date");
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
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

    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
    }

    public FontSize getFontSize() {
        return fontSize;
    }


    public String getUserData() {
        return userData;
    }

    public void setUserData(String simplifiedDate) {
        userData = simplifiedDate + USERDATA_FILE_EXTENSION;
        editor.putString("user_data", userData);
        editor.commit();
        load(userData);
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