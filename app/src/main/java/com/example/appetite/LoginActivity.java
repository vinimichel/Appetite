package com.example.appetite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;
import java.io.*;
public class LoginActivity extends AppCompatActivity  {

    private final String url="http://192.168.0.30:3000/auth/user/login";

    EditText email,password;
    Button loginButton;
    String myResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView btn = findViewById(R.id.textSignUp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        //setBottomNavigationItem();

        // Login ApI
        email=(EditText)findViewById(R.id.inputEmail);
        password=(EditText)findViewById(R.id.password);


        loginButton=(Button) findViewById(R.id.btnRegister);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(LoginActivity.this, "leeres Eingabefeld",
                    Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    try {
                        run();
                    }
                    catch (IOException e){
                        Toast.makeText(LoginActivity.this, "Fehler!!",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        return;
                    }
                }

            }
        });


    }

    void run() throws IOException {

        OkHttpClient client = new OkHttpClient();

        // create your json here
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email.getText().toString() );
            jsonObject.put("password", password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON,  jsonObject.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if(response.isSuccessful()) {
                    myResponse = response.body().string();
                  String ResFirstName,ResLastName,ResEmail,ResUserID;

                    try {
                        JSONObject json = new JSONObject(myResponse);

                        ResFirstName = json.getString("firstname");
                        ResLastName = json.getString("lastname");
                        ResEmail= json.getString("email");
                        ResUserID = json.getString("_id");
                        //Log.d("json", "firstname : "+ ResUserID);

                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("userFirstName", ResFirstName);
                        intent.putExtra("userID", ResUserID);
                        startActivity(intent);

                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "Fehler!!",
                                Toast.LENGTH_SHORT).show();
                        return;

                    }


                } else {
                    String errorBodyString = myResponse, errmsg;
                    Log.d("Login Error", errorBodyString);
                    return;
                    /**try {
                        JSONObject err = new JSONObject(errorBodyString);
                        errmsg = err.getString("message");
                        Log.d("error", response.code()+","+errmsg);
                    } catch(JSONException e) {

                    }
                     */

                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
                call.cancel();
                return;
            }

        });
    }

/**
    private void setBottomNavigationItem() {

        NavigationBarView bottomNavigationView = (NavigationBarView)findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.settings_tab);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.find_restaurant:
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.reservations_tab:
                        startActivity(new Intent(getApplicationContext(), ReservationActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.settings_tab:
                        return true;
                }
                return false;
            }
        });
    }
 */

}
