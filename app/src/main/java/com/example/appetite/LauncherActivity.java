package com.example.appetite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }

    public void openUserLogin(View v) {
        Button btn = (Button) findViewById(R.id.btnLgUser);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
            }
        });
    }

    public void openOwnerLogin(View v) {
        Button btn = (Button)findViewById(R.id.btnLgOwner);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, LoginOwner.class));
            }
        });
    }
}