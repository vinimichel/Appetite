package com.example.appetite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.appetite.adapter.Adapter_Table;
import com.example.appetite.dataModels.Table;
import com.google.android.material.navigation.NavigationBarView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Tables extends AppCompatActivity {

    private static final String FILE_NAME = "table_data.txt";
    ArrayList<Table> tables = new ArrayList<Table>();
    TextView freeSeats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);
        setBottomNavigationItem();
        RecyclerView recyclerView = findViewById(R.id.myRecyclerView);
        Button addBtn = findViewById(R.id.addBtn);
        Button deleteBtn = findViewById(R.id.deleteBtn);
        freeSeats = findViewById(R.id.freeSeats);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTable(4);
                updateView(recyclerView);
                saveTableData();
                freeSeats.setText("Freie sitze: " + tables.get(0).getFreeSeats());
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTable();
                updateView(recyclerView);
                saveTableData();
                if (tables.size() > 0) {
                    freeSeats.setText("Freie sitze: " + tables.get(0).getFreeSeats());
                } else freeSeats.setText("Freie sitze: 0");
            }
        });

        // load Table Data here
        if (fileExists(FILE_NAME)) {
            loadTableData(); // load Table Data;
        }
        updateFreeSeats();
        updateView(recyclerView);
    }

    public void updateFreeSeats() {
        if (tables.size() > 0) {
            freeSeats.setText("Freie sitze: " + tables.get(0).getFreeSeats());
        } else freeSeats.setText("Freie sitze: 0");
    }

    public void updateView(RecyclerView recyclerView) {
        Adapter_Table adapter = new Adapter_Table(this, tables);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void addTable(int seatCount) {
        tables.add(new Table(seatCount));
    }

    public void deleteTable() {
        if (tables.size() > 0) {
            Table toDelete = tables.get(tables.size() - 1);
            toDelete.deleteTable();
            tables.remove(tables.size() - 1);
        }
    }

    private void setBottomNavigationItem() {
        NavigationBarView bottomNavigationView = (NavigationBarView)findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.tables);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeBtn:
                        startActivity(new Intent(getApplicationContext(), MainOwner.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.my_restaurant:
                        startActivity(new Intent(getApplicationContext(), MyRestaurant.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.tables:
                        return true;
                    case R.id.settingsBtn:
                        //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    public void saveTableData() {
        String text = "";
        for (int i = 0; i < tables.size(); i++) {
                text += "seatCount:" + tables.get(i).getSeatCount() + "occupied:" + tables.get(i).getStatus() + "\n";
            }
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
            //Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
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

    public void loadTableData() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;
            boolean occupied = false;
            while ((text = br.readLine()) != null) {
                System.out.println(text);
                occupied = Boolean.parseBoolean(text.substring(text.length() - 4));
                String seatCount = text.replaceAll("[^0-9]", " ");
                seatCount = seatCount.replaceAll(" +", "");
                tables.add(new Table (Integer.parseInt(seatCount)));
                System.out.println("occupied: " + occupied);
                if (occupied) tables.get(tables.size() - 1).changeOccupiedStatus();
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean fileExists(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

}