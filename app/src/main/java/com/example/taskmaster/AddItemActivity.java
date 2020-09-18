package com.example.taskmaster;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String EXTRA_REPLY = "com.exmaple.android.taskmaster.extra.REPLY";
    public static final String EXTRA_CHOICE = "com.example.android.taskmaster.extra.CHOICE";

    private EditText addItemEditText;
    private Spinner spinner;
    private CheckBox checkbox;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.additem_activity);
       addItemEditText = findViewById(R.id.additem_edittext);
       Intent intent = getIntent();
       String[] choices;

       if (intent.getStringArrayExtra(MainActivity.EXTRA_CHOICES) != null) {
            choices = intent.getStringArrayExtra(MainActivity.EXTRA_CHOICES);
            // Toast.makeText(this, "Choices received!", Toast.LENGTH_LONG).show();
       } else {
           choices = new String[]{};
           // Toast.makeText(this, "Choices not received!", Toast.LENGTH_LONG).show();
       }

       initToolbar();
       initSpinner(choices);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.additem_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.additem_toolbartitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    private void initSpinner(String[] choices) {
        spinner = findViewById(R.id.additem_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Arrays.asList(choices));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    public void replyWithItem(View view) {
        String label = addItemEditText.getText().toString();
        String currentChoice = spinner.getSelectedItem().toString();
        Intent replyIntent = new Intent();

        replyIntent.putExtra(EXTRA_REPLY, label);
        replyIntent.putExtra(EXTRA_CHOICE, currentChoice);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}
