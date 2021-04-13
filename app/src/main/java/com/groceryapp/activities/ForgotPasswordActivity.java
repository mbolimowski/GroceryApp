package com.groceryapp.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.groceryapp.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private EditText emailEt;
    private Button recoverBtn;
    private RelativeLayout toolbarRl;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        backBtn = findViewById(R.id.backBtn);
        emailEt = findViewById(R.id.emailEt);
        recoverBtn = findViewById(R.id.recoverBtn);
        toolbarRl = findViewById(R.id.toolbarRl);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

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
                    recoverBtn.setBackgroundResource(R.drawable.shape_rect01);
                    setTheme(R.style.Theme_GroceryApp_NoActionBar);
                    break;
                case "option2":
                    toolbarRl.setBackgroundResource(R.drawable.shape_rect_option2);
                    recoverBtn.setBackgroundResource(R.drawable.shape_rect_option2);
                    setTheme(R.style.Theme_GroceryApp_Option2);
                    break;
                case "option3":
                    toolbarRl.setBackgroundResource(R.drawable.shape_rect_option3);
                    recoverBtn.setBackgroundResource(R.drawable.shape_rect_option3);
                    setTheme(R.style.Theme_GroceryApp_Option3);
                    break;
                case "option4":
                    toolbarRl.setBackgroundResource(R.drawable.shape_rect_option4);
                    recoverBtn.setBackgroundResource(R.drawable.shape_rect_option4);
                    setTheme(R.style.Theme_GroceryApp_Option4);
                    break;
            };
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private String email;
    private void recoverPassword(){
        email = emailEt.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email...", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Sending instructions to reset password...");
        progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //instructions sent
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Password reset instructions sent, check your email...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed sending instructions
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}