package com.groceryapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.groceryapp.R;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private TextView forgotTv, createAccountTv;
    private Button loginBtn;
    private RelativeLayout toolbarRl;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init UI views
        emailEt =(EditText) findViewById(R.id.emailEt);
        passwordEt =(EditText) findViewById(R.id.passwordEt);
        forgotTv =(TextView) findViewById(R.id.forgotTv);
        createAccountTv =(TextView) findViewById(R.id.createAccountTv);
        loginBtn =(Button) findViewById(R.id.loginBtn);
        toolbarRl =(RelativeLayout) findViewById(R.id.toolbarRl);

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

        SharedPreferences sharedPref = getSharedPreferences("settingsLayoutsColors", MODE_PRIVATE);
        String txt = sharedPref.getString("dane", "default");
        if(txt.isEmpty()){
            toolbarRl.setBackgroundResource(R.drawable.shape_rect01);
        }
        else{
            switch (txt){
                case "default":
                    toolbarRl.setBackgroundResource(R.drawable.shape_rect01);
                    loginBtn.setBackgroundResource(R.drawable.shape_rect01);
                    setTheme(R.style.Theme_GroceryApp_NoActionBar);
                    break;
                case "option2":
                    toolbarRl.setBackgroundResource(R.drawable.shape_rect_option2);
                    loginBtn.setBackgroundResource(R.drawable.shape_rect_option2);
                    setTheme(R.style.Theme_GroceryApp_Option2);
                    break;
                case "option3":
                    toolbarRl.setBackgroundResource(R.drawable.shape_rect_option3);
                    loginBtn.setBackgroundResource(R.drawable.shape_rect_option3);
                    setTheme(R.style.Theme_GroceryApp_Option3);
                    break;
                case "option4":
                    toolbarRl.setBackgroundResource(R.drawable.shape_rect_option4);
                    loginBtn.setBackgroundResource(R.drawable.shape_rect_option4);
                    setTheme(R.style.Theme_GroceryApp_Option4);
                    break;
            };
        }

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        createAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterUserActivity.class));
            }
        });
        forgotTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    private String email, password;

    private void loginUser() {
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email pattern...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Loggin In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //logged in successfully
                        makeMeOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed logging in
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void makeMeOnline() {
        //after loggin in, make user online
        progressDialog.setMessage("Checking User...");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "true");

        //update value to database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update successfully
                        checkUserType();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType() {
        //if user is seller, start seller main screen
        // if user is buyer, start useer main screen

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            String accountType = "" + ds.child("accountType").getValue();
                            if(accountType.equals("Seller")){
                                progressDialog.dismiss();
                                //user is seller
                                startActivity(new Intent(LoginActivity.this, MainSellerActivity.class));
                                finish();
                            }
                            else{
                                progressDialog.dismiss();
                                //user is buyer
                                startActivity(new Intent(LoginActivity.this, MainUserActivity.class));
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}