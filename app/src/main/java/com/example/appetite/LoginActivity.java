package com.example.appetite;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;
import java.io.*;
public class LoginActivity extends AppCompatActivity  {

    private final String url="http://172.20.64.1:3000/auth/user/login";

    TextView email,password;
    Button loginButton;


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
        setBottomNavigationItem();

        // Login ApI
        email=(TextView)findViewById(R.id.inputEmail);
        password=(TextView)findViewById(R.id.password);


        loginButton=(Button) findViewById(R.id.btnLog);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    run();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        });


    }

    void run() throws IOException {

        OkHttpClient client = new OkHttpClient();




        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "Content-Type: application/json\r\n\r\n{\r\n    \"email\" : \""+email+"\",\r\n " +
                "    \"password\" : \""+password+"\"\r\n}");
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "text/plain")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if(response.isSuccessful()) {
                    final String myResponse = response.body().string();
                  String firstName,lastName,email,userID;
                    try {
                        JSONObject json = new JSONObject(myResponse);

                        firstName = json.getString("firstname");
                        lastName = json.getString("firstname");
                        email= json.getString("firstname");
                        userID = json.getString("firstname");

                        UserInfoActivity user = new UserInfoActivity(firstName,lastName,email,userID);

                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("User", user);
                        startActivity(intent);



                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    // Uebergibt JSOn AN Die MainActivity


                    // Wechslen zu MAinActivity
                      sendUserToNextActivity();


                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
                call.cancel();
            }

        });
    }

    private void sendUserToNextActivity() {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


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

}
