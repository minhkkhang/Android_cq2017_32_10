package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.UserInfoObj;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoEditActivity extends AppCompatActivity {

    EditText nameTxt,emailTxt,dobTxt,phoneTxt,curPasswordTxt,newPasswordTxt;
    Button infoSubmitBtn,passSubmitBtn;
    RadioGroup genderRadioGroup;
    ImageView selectedImage;
    Menu mMenu;
    Integer userId;
    UserService userService;
    String token;
    DatePickerDialog picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);

        SetContent();
        GetUserInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.default_menu,menu);
        mMenu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.default_done: {
                Intent mIntent=new Intent(UserInfoEditActivity.this,FrontPage.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
                break;
            }
            default:break;
        }
        return true;
    }

    private void SetContent(){
        nameTxt=findViewById(R.id.user_edit_nameTxt);
        emailTxt=findViewById(R.id.user_edit_emailTxt);
        dobTxt=findViewById(R.id.user_edit_dobTxt);
        phoneTxt=findViewById(R.id.user_edit_phoneTxt);
        curPasswordTxt=findViewById(R.id.user_edit_currentPass);
        newPasswordTxt=findViewById(R.id.user_edit_newPass);
        selectedImage=findViewById(R.id.user_edit_avatarImage);
        infoSubmitBtn=findViewById(R.id.user_edit_infoSubmitBtn);
        passSubmitBtn=findViewById(R.id.user_edit_passwordSubmitBtn);
        genderRadioGroup=findViewById(R.id.user_edit_gendergroup);
        SharedPreferences pref=getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        userId=pref.getInt("userID",0);
        token = pref.getString("token", "");
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);

        dobTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    final Calendar cldr = Calendar.getInstance();
                    int day;
                    int month;
                    int year;
                    try{
                        day=Integer.parseInt(dobTxt.getText().toString().substring(8,10));
                    }catch(Exception e){day = cldr.get(Calendar.DAY_OF_MONTH);}
                    try{
                        month=Integer.parseInt(dobTxt.getText().toString().substring(5,7));
                    }catch(Exception e){month = cldr.get(Calendar.MONTH);}
                    try{
                        year=Integer.parseInt(dobTxt.getText().toString().substring(0,4));
                    }catch(Exception e){year = cldr.get(Calendar.YEAR);}
                    // date picker dialog
                    picker = new DatePickerDialog(UserInfoEditActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    String day,month;
                                    Integer temp=monthOfYear+1;
                                    month=temp.toString();
                                    temp=dayOfMonth;
                                    day=temp.toString();
                                    if(monthOfYear+1<10)month="0"+month;
                                    if(dayOfMonth<10)day="0"+day;
                                    dobTxt.setText(year + "-" + month + "-" + day);
                                }
                            }, year, month, day);
                    picker.show();
                    dobTxt.clearFocus();
                }
            }
        });
        infoSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserInfo();
            }
        });
        passSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePassword();
            }
        });
    }


    private void GetUserInfo(){
        Call<UserInfoObj>call=userService.getUserInfo(token);
        call.enqueue(new Callback<UserInfoObj>() {
            @Override
            public void onResponse(Call<UserInfoObj> call, Response<UserInfoObj> response) {
                if(response.isSuccessful()){
                    UserInfoObj user=response.body();
                    if(user.getAvatar()!=null){
                        if(!user.getAvatar().isEmpty()){
                            Picasso.with(UserInfoEditActivity.this).load(user.getAvatar())
                                    .centerCrop()
                                    .fit()
                                    .into(selectedImage);
                        }
                        else{
                            selectedImage.setImageResource(R.drawable.man);
                        }
                    }else{
                        selectedImage.setImageResource(R.drawable.man);
                    }
                    if(user.getEmail()!=null){
                        emailTxt.setText(user.getEmail().toString());
                    }
                    if(user.getPhone()!=null){
                        phoneTxt.setText(user.getPhone().toString());
                    }
                    if(user.getDob()!=null){
                        dobTxt.setText(user.getDob().substring(0,10));
                    }
                    if(user.getGender()!=null){
                        if(user.getGender()==1)genderRadioGroup.check(R.id.user_edit_male);
                        else genderRadioGroup.check(R.id.user_edit_female);
                    }
                    if(user.getFullName()!=null)nameTxt.setText(user.getFullName().toString());
                }
            }

            @Override
            public void onFailure(Call<UserInfoObj> call, Throwable t) {

            }
        });
    }

    private void UpdateUserInfo(){
        UserInfoObj user=new UserInfoObj();
        user.setFullName(nameTxt.getText().toString());
        user.setPhone(phoneTxt.getText().toString());
        user.setEmail(emailTxt.getText().toString());
        user.setGender(genderRadioGroup.getCheckedRadioButtonId()==R.id.user_edit_male?1:0);
        user.setDob(dobTxt.getText().toString());
        Call<Message>call=userService.editUserInfo(token,user);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UserInfoEditActivity.this, "User info updated!",
                            Toast.LENGTH_SHORT).show();
                    GetUserInfo();
                }
                else{
                    Toast.makeText(UserInfoEditActivity.this, "Fail to update info",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(UserInfoEditActivity.this, "Fail to update info",
                        Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }


    private void UpdatePassword(){
        UserInfoObj user=new UserInfoObj();
        user.setCurrentPassword(curPasswordTxt.getText().toString());
        user.setNewPassword(newPasswordTxt.getText().toString());
        user.setId(userId);
        Call<Message>call=userService.editUserPassword(token,user);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UserInfoEditActivity.this, "User password updated!",
                            Toast.LENGTH_SHORT).show();
                    curPasswordTxt.setText("");
                    newPasswordTxt.setText("");
                }
                else{
                    Toast.makeText(UserInfoEditActivity.this, "Fail to update password",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(UserInfoEditActivity.this, "Fail to update password",
                        Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }
}
