package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ygaps.travelapp.pojo.LoginRequest;
import com.ygaps.travelapp.pojo.LoginResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String PREF_NAME="1712390_1712518";
    EditText emailPhoneInput;
    EditText passwordInput;
    Button loginButton;
    Button SignUpButton;
    UserService userService;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressBar spinner;
    CheckBox remembermeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Builds HTTP Client for API Calls
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);

        this.setContent();
        spinner.setVisibility(View.GONE);
        String temptoken=sharedPreferences.getString("token","");
        if(temptoken.compareTo("")!=0){
            StartMainActivity();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    void setContent() {
        loginButton = findViewById(R.id.sign_in_button);
        emailPhoneInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        sharedPreferences=getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SignUpButton=findViewById(R.id.sign_up_button);
        editor=sharedPreferences.edit();
        spinner=findViewById(R.id.login_progressBar);
        remembermeBtn=findViewById(R.id.login_rememberMeBtn);

        remembermeBtn.setChecked(sharedPreferences.getBoolean("rememberMe",false));
        emailPhoneInput.setText(sharedPreferences.getString("emailPhone",""));
        passwordInput.setText(sharedPreferences.getString("password",""));
    }
    void StartMainActivity(){
        Intent myIntent = new Intent(LoginActivity.this, FrontPage.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
    }
    void doLogin() {
        LoginRequest loginRequest=new LoginRequest();
        loginRequest.setEmailPhone(emailPhoneInput.getText().toString());
        loginRequest.setPassword(passwordInput.getText().toString());
        spinner.setVisibility(View.VISIBLE);
        Call<LoginResponse> call = userService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call,
                                   @NonNull Response<LoginResponse> response) {
                spinner.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(remembermeBtn.isChecked()){
                        editor.putString("emailPhone",emailPhoneInput.getText().toString());
                        editor.putString("password",passwordInput.getText().toString());
                        editor.putBoolean("rememberMe",true);
                    }
                    else{
                        editor.putString("emailPhone","");
                        editor.putString("password","");
                        editor.putBoolean("rememberMe",false);
                    }
                    editor.commit();
                    String token;
                    Integer ID;
                    try{
                        token = response.body().getToken();
                        ID=response.body().getUserId();
                    }catch (NullPointerException e){
                        return;
                    }
                    editor.putString("token",token);
                    editor.putInt("userID",ID);
                    editor.commit();
                    StartMainActivity();
                }
                else {
                    Gson gson = new Gson();
                    LoginResponse message=
                            gson.fromJson(response.errorBody().charStream(), LoginResponse.class);
                    Toast.makeText(LoginActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                spinner.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }
}

