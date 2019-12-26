package com.ygaps.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.SignUpRequest;
import com.ygaps.travelapp.pojo.SignUpResponse;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    UserService userService;
    EditText nameInput;
    EditText emailInput;
    EditText phoneInput,addressInput,dobyearInput,dobmonthInput,dobdayInput,passwordInput;
    RadioGroup genderInput;
    Button SignUpButton;
    ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SetContent();
        spinner.setVisibility(View.GONE);
        //Builds HTTP Client for API Calls
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignUp();
            }
        });


    }
    void SetContent(){
        nameInput=findViewById(R.id.signup_fullname);
        emailInput=findViewById(R.id.signup_email);
        phoneInput=findViewById(R.id.signup_phone);
        addressInput=findViewById(R.id.signup_address);
        dobyearInput=findViewById(R.id.signup_yearofbirth);
        dobmonthInput=findViewById(R.id.signup_monthofbirth);
        dobdayInput=findViewById(R.id.signup_dayofbirth);
        passwordInput=findViewById(R.id.signup_password);
        genderInput=findViewById(R.id.signup_gendergroup);
        SignUpButton=findViewById(R.id.sign_up_confirm_button);
        spinner=findViewById(R.id.signup_progressBar);
    }
    void doSignUp(){
        spinner.setVisibility(View.VISIBLE);
        Integer gender;
        RadioButton checkedRadio=findViewById(genderInput.getCheckedRadioButtonId());
        if(checkedRadio == findViewById(R.id.signup_male))gender=1;
        else gender=0;

        String day,month,year;
        day=dobdayInput.getText().toString();
        month=dobmonthInput.getText().toString();
        year=dobyearInput.getText().toString();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);

        String date = year + "-"+month+"-"+day;
        try {
            date = format.format(format.parse(date));
        } catch (ParseException e) {
            date="1900-01-01";
            System.out.println("Date " + date + " is not valid according to " +
                    ((SimpleDateFormat) format).toPattern() + " pattern.");
        }
        catch(NullPointerException e){
            date="1900-01-01";
        }
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFullName(nameInput.getText().toString());
        signUpRequest.setPhone(phoneInput.getText().toString());
        signUpRequest.setEmail(emailInput.getText().toString());
        signUpRequest.setAddress(addressInput.getText().toString());
        signUpRequest.setPassword(passwordInput.getText().toString());
        signUpRequest.setDob(date);
        signUpRequest.setGender(gender);
        Call<SignUpResponse> call=userService.signup(signUpRequest);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                spinner.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),
                            "Sign up success!", Toast.LENGTH_SHORT).show();
                    StartLoginActivity();
                }
                else {
                    Gson gson = new Gson();
                    SignUpResponse message=
                            gson.fromJson(response.errorBody().charStream(), SignUpResponse.class);
                    List<Message> error=message.getMessage();
                    StringBuilder errorMsg = new StringBuilder();
                    errorMsg.append("Errors:");
                    errorMsg.append(System.getProperty("line.separator"));
                    for(int i=0;i<error.size();i++){
                        errorMsg.append(error.get(i).getMsg());
                        errorMsg.append(System.getProperty("line.separator"));
                    }
                    Toast.makeText(SignUpActivity.this,
                            errorMsg.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                spinner.setVisibility(View.GONE);
                call.cancel();

            }
        });
    }

    void StartLoginActivity(){
        Intent myIntent = new Intent(SignUpActivity.this, LoginActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
    }
}
