package com.example.appetite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView btn =findViewById(R.id.textSignUp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(SettingsActivity.this,RegisterActivity.class));
            }
        });

        Intent i = getIntent();
    }

}
