package com.example.taskmaster;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddGroupActivity extends AppCompatActivity {
    public static final String EXTRA_GROUP = "com.example.android.taskmaster.extra.GROUP";

    private EditText addGroupEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addgroup_activity);
        addGroupEditText = findViewById(R.id.addgroup_edittext);
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.addgroup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.addgroup_toolbartitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    public void replyWithGroup(View view) {
        String label = addGroupEditText.getText().toString();
        Intent replyIntent = new Intent();

        replyIntent.putExtra(EXTRA_GROUP, label);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}
