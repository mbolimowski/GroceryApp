package com.groceryapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.groceryapp.R;
import com.squareup.picasso.Picasso;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class SettingsActivity extends AppCompatActivity {

    private RelativeLayout toolbarRl;
    private ImageButton backBtn;
    private ImageView profileIv, option1Iv, option2Iv, option3Iv, option4Iv;
    private TextView nameTv, emailTv, phoneTv;

    private FirebaseAuth firebaseAuth;

    private String option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPref = getSharedPreferences("settingsLayoutsColors", MODE_PRIVATE);


        toolbarRl = findViewById(R.id.toolbarRl);
        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        option1Iv = findViewById(R.id.option1Iv);
        option2Iv = findViewById(R.id.option2Iv);
        option3Iv = findViewById(R.id.option3Iv);
        option4Iv = findViewById(R.id.option4Iv);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);

        firebaseAuth = FirebaseAuth.getInstance();

        //make fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

        //SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String txt = sharedPref.getString("dane", "default");
        if(txt.isEmpty()){
            toolbarRl.setBackgroundResource(R.drawable.shape_rect01);
        }
        else{
            switch (txt){
                case "default":
                    toolbarRl.setBackgroundResource(R.drawable.shape_rect01);
                    setTheme(R.style.Theme_GroceryApp_NoActionBar);
                    break;
                case "option2":
                    toolbarRl.setBackgroundResource(R.drawable.shape_rect_option2);
                    setTheme(R.style.Theme_GroceryApp_Option2);
                    break;
                case "option3":
                    toolbarRl.setBackgroundResource(R.drawable.shape_rect_option3);
                    setTheme(R.style.Theme_GroceryApp_Option3);
                    break;
                case "option4":
                    toolbarRl.setBackgroundResource(R.drawable.shape_rect_option4);
                    setTheme(R.style.Theme_GroceryApp_Option4);
                    break;
            };
        }

        loadMyInfo();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        option1Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                option = "default";
                editor.putString("dane", option);
                editor.apply();
                editor.commit();
                recreate();
            }
        });
        option2Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toolbarRl.setBackgroundResource(R.drawable.shape_rect_option2);
                SharedPreferences.Editor editor = sharedPref.edit();
                option = "option2";
                editor.putString("dane", option);
                editor.apply();
                editor.commit();
                recreate();
            }
        });
        option3Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                option = "option3";
                editor.putString("dane", option);
                editor.apply();
                editor.commit();
                recreate();
            }
        });
        option4Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                option = "option4";
                editor.putString("dane", option);
                editor.apply();
                editor.commit();
                recreate();
            }
        });
    }

    private void loadMyInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            String name = "" + ds.child("name").getValue();
                            String email = "" + ds.child("email").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String accountType = "" + ds.child("accountType").getValue();
                            String city = "" + ds.child("city").getValue();

                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(profileIv);
                            }
                            catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}