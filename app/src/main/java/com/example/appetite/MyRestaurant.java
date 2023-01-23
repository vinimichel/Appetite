package com.example.appetite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyRestaurant extends AppCompatActivity {
    private static final String FILE_NAME = "rest_details.txt";
    private static final String FILE_NAME_JSON = "rest_geo.json";
    public static TextView[] txt = new TextView[8];
    private final int [] ids = {R.id.editRestName, R.id.editDescription, R.id.editLongitude, R.id.editLatitude, R.id.editCity, R.id.editAddress, R.id.editZip, R.id.editCategory};


    Button editButton, saveButton, discardButton, submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_restaurant);
        setBottomNavigationItem();

        for (int i = 0; i < txt.length; i++) txt[i] = findViewById(ids[i]);

        if (fileExists(FILE_NAME)) load();

        discardButton = findViewById(R.id.discardBtn);
        discardButton.setEnabled(false);
        submitButton = findViewById(R.id.submitBtn);
        submitButton.setEnabled(false);
        editButton = findViewById(R.id.editBtn);
        editButton.setOnClickListener(view -> {
            for (int i = 0; i < txt.length; i++) txt[i].setEnabled(true);
            saveButton.setEnabled(true);
        });

        saveButton = findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(view -> {
            //for (int i = 0; i < txt.length; i++) txt[i].setEnabled(false);
            saveButton.setEnabled(false);
            submitButton.setEnabled(true);
            discardButton.setEnabled(true);
            // Save the text to a database or file here
        });

        submitButton.setOnClickListener(view -> {
            submit();
            createGeoJSON();
            for (int i = 0; i < txt.length; i++) txt[i].setEnabled(false);
            discardButton.setEnabled(false);
            submitButton.setEnabled(false);
        });

        discardButton.setOnClickListener(view -> {
            if (fileExists(FILE_NAME)) load();
            for (int i = 0; i < txt.length; i++) txt[i].setEnabled(false);
            discardButton.setEnabled(false);
            submitButton.setEnabled(false);
            for (int i = 0; i < txt.length; i++) txt[i].setEnabled(false);
        });


        for (int i = 0; i < txt.length; i++) txt[i].setEnabled(false);
        saveButton.setEnabled(false);
        discardButton.setEnabled(false);
        submitButton.setEnabled(false);
    }

    private void setBottomNavigationItem() {
        NavigationBarView bottomNavigationView = (NavigationBarView) findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.my_restaurant);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeBtn:
                        startActivity(new Intent(getApplicationContext(), MainOwner.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.my_restaurant:
                        return true;
                    case R.id.tables:
                        startActivity(new Intent(getApplicationContext(), Tables.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.settingsBtn:
                        //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    public void submit() {
        String text = "";
        for (int i = 0; i < txt.length; i++) text += txt[i].getText().toString() + "\n";
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public void load() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < txt.length; i++) txt[i].setText(br.readLine());
            System.out.println("Line read successfully");
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createGeoJSON () {
        FileOutputStream fos = null;
        String[] text = new String[8];
        for (int i = 0; i < txt.length; i++) text[i] += txt[i].getText().toString();
        String strToJSON =
                "[{\"type\":\"Feature\",\"properties\":{\"title\":\"" + text[0]
                + "\",\"description\":\"" + text[1]
                + "\",\"city\":\"" + text[2]
                + "\",\"PLZ\":\"" + text[3]
                + "\",\"address\":\"" + text[4]
                + "\",\"category\":\"" + text[5]
                + "\"},\"geometry\":{\"coordinates\":[" +  text[6] + "," + text[7]
                + "],\"type\":\"Point\"},\"id\":\"\"}";

        try {
            fos = openFileOutput(FILE_NAME_JSON, MODE_PRIVATE);
            fos.write(strToJSON.getBytes());
            Toast.makeText(this, "Saves JSON File to " + getFilesDir() + "/" + FILE_NAME_JSON, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public boolean fileExists(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }


}