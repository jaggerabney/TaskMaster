package com.example.taskmaster;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.fonts.Font;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private TextView fontSize_textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPrefs.edit();
        fontSize_textView = findViewById(R.id.settings_fontsize_textview);
        initToolbar();
        initSpinner();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    private void initSpinner() {
        final Spinner spinner = findViewById(R.id.settings_fontsize_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, FontSize.names());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerAdapter.getPosition(sharedPrefs.getString(getString(R.string.sharedprefs_fontsize_key),
                MainActivity.FONT_SIZE_DEFAULT.name())));
        // death
        fontSize_textView.setTextSize(FontSize.valueOf(sharedPrefs.getString(getString(R.string.sharedprefs_fontsize_key),
                MainActivity.FONT_SIZE_DEFAULT.name())).getItemSizeInSp());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                editor.putString(getString(R.string.sharedprefs_fontsize_key), spinner.getSelectedItem().toString());
                editor.commit();
                fontSize_textView.setTextSize(FontSize.valueOf(spinner.getSelectedItem().toString()).getItemSizeInSp());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });
    }
}
