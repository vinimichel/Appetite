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

public class RegisterActivity extends AppCompatActivity {

    private final String url="http://172.29.48.1:3000/auth/user/register";

    EditText firstName,lastName,email,password,confirmPassword;
    Button registerButton;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        TextView btn = findViewById(R.id.textSignUp);

        //Registration
        firstName=(EditText)findViewById(R.id.inputFirstName);
        lastName=(EditText)findViewById(R.id.inputLastName);


        email=(EditText)findViewById(R.id.inputUserName);
        password=(EditText)findViewById(R.id.password);
        confirmPassword=(EditText)findViewById(R.id.password2);



        registerButton=(Button) findViewById(R.id.btnRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {





                if(! email.getText().toString().matches(emailPattern)){
                     email.setError("Please enter a Correct Email");
                }
                else if( password.getText().toString().isEmpty() || password.length()<7){
                    password.setError("Enter Proper Password");
                }
                else if(! password.getText().toString().equals(confirmPassword.getText().toString())){
                    confirmPassword.setError("Password Not the Same");
                }
                else{

                }
                try {
                    run();
                }
                catch (IOException e){
                    Toast.makeText(RegisterActivity.this, "An error occurred",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            }
        });






        //zu Login Activity
    btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    //Hier ist die Verbindung mit dem Server

    void run() throws IOException {

        OkHttpClient client = new OkHttpClient();

        // create your json here
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstname", firstName.getText().toString() );
            jsonObject.put("lastname", lastName.getText().toString());
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
                        //UserInfoActivity user = new UserInfoActivity(ResFirstName,ResLastName,ResEmail,ResUserID);

                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                        //intent.putExtra("User", user);
                        startActivity(intent);

                    } catch (JSONException e) {
                        Toast.makeText(RegisterActivity.this, "An error occurred",
                                Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);

                    }
                    // Wechslen zu MAinActivity
                     sendUserToNextActivity();

                } else {
                    String errorBodyString = response.body().string(), errmsg;
                    Toast.makeText(RegisterActivity.this, "An error occurred",
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
                Toast.makeText(RegisterActivity.this, "An error occurred",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }
    //Send to Next to Home
    private void sendUserToNextActivity() {
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
