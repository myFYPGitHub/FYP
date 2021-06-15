package com.fyp.Beauticianatyourdoorstep.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.controller.ServerLogic;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText emailEd, phoneEd;
    private String email;
    private String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        emailEd = findViewById(R.id.forgetPassEmail);
        phoneEd = findViewById(R.id.forgetPassPhone);
        Button sendPassword = findViewById(R.id.sendPassword_btn);
        sendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage();
            }
        });
    }

    protected void sendSMSMessage() {
        email = emailEd.getText().toString();
        phoneNo = phoneEd.getText().toString();
        if (email.isEmpty()) {
            emailEd.setError("Please Enter your valid Email");
            return;
        }
        String MobilePattern = "[0-9]{11}";
        if (!phoneEd.getText().toString().matches(MobilePattern)) {
            phoneEd.setError("Please Enter valid Phone Number");
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 89);
        }else {
            ServerLogic.sendNewPassword(ForgetPasswordActivity.this, email, phoneNo);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 89: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ServerLogic.sendNewPassword(ForgetPasswordActivity.this, email, phoneNo);
                } else {
                    Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}