package com.example.demomainpage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    public static final String PREF_NAME="1712390_1712518";
    EditText emailPhoneInput;
    EditText passwordInput;
    Button loginButton;
    UserService userService;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Builds HTTP Client for API Calls
        userService = APIClient.buildHTTPClient();

        this.setContent();
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
    }

    void setContent() {
        loginButton = findViewById(R.id.sign_in_button);
        emailPhoneInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        sharedPreferences=getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }
    void StartMainActivity(){
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
    }
    void doLogin() {

        userService.login(emailPhoneInput.getText().toString(),
                passwordInput.getText().toString()).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call,
                                   @NonNull Response<LoginResponse> response) {
                String token = response.body().getToken();
                String ID=response.body().getUserID();
                if(response.code()==200){
                    editor.putString("token",token);
                    editor.putString("userID",ID);
                    editor.commit();
                    StartMainActivity();
                }
                else {
                    Gson gson = new Gson();
                    LoginResponse message=
                            gson.fromJson(response.errorBody().charStream(),LoginResponse.class);
                    Toast.makeText(LoginActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                t.printStackTrace();

            }
        });
    }
}

