package com.example.appetite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
                    Toast.makeText(LoginActivity.this, "Emptyfields not allowed!",
                    Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        run();
                    }
                    catch (IOException e){
                        Toast.makeText(LoginActivity.this, "An error occurred",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
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
                    final String myResponse = response.body().string();
                  String ResFirstName,ResLastName,ResEmail,ResUserID;

                    try {
                        JSONObject json = new JSONObject(myResponse);

                        ResFirstName = json.getString("firstname");
                        ResLastName = json.getString("lastname");
                        ResEmail= json.getString("email");
                        ResUserID = json.getString("_id");
                        //Log.d("json", "firstname : "+ ResUserID);
                        UserInfoActivity user = new UserInfoActivity(ResFirstName,ResLastName,ResEmail,ResUserID);

                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("User", user);
                        startActivity(intent);

                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "An error occurred",
                                Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);

                    }
                    // Wechslen zu MAinActivity
                      sendUserToNextActivity();

                } else {
                    String errorBodyString = response.body().string(), errmsg;
                    Toast.makeText(LoginActivity.this, "An error occurred",
                            Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LoginActivity.this, "An error occurred",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void sendUserToNextActivity() {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
