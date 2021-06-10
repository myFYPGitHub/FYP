package com.fyp.Beauticianatyourdoorstep.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.controller.ServerLogic;
import com.fyp.Beauticianatyourdoorstep.helper.LoginManagement;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.helper.StringHelper;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.view.beauticianModule.BeauticianDashboardActivity;
import com.fyp.Beauticianatyourdoorstep.view.customerModule.CustomerDashboardActivity;

public class SignInActivity extends AppCompatActivity implements MyConstants {
    private EditText emailEd, passwordEd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginManagement loginManagement = new LoginManagement(this);
        if (loginManagement.isLoginActive() && loginManagement.getLoginCategory() != null) {
            if (loginManagement.getLoginCategory().toLowerCase().equals(USER_CATEGORY_BEAUTICIAN)) {
                startActivity(new Intent(SignInActivity.this, BeauticianDashboardActivity.class));
            } else if (loginManagement.getLoginCategory().toLowerCase().equals(USER_CATEGORY_CUSTOMER)) {
                startActivity(new Intent(SignInActivity.this, CustomerDashboardActivity.class));
            }
            finish();
            return;
        }
        setContentView(R.layout.activity_signin);
        emailEd = findViewById(R.id.loginEmail);
        passwordEd = findViewById(R.id.loginPassword);
    }

    public void signIn(View view) {
        String email = emailEd.getText().toString().trim();
        String password = passwordEd.getText().toString();
        if (StringHelper.isEmpty(email)) {
            emailEd.setError("Email is required field");
            emailEd.requestFocus();
            Toast.makeText(this, "Email is required field", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!StringHelper.isValidEmail(email)) {
            emailEd.setError("Invalid Email");
            emailEd.requestFocus();
            Toast.makeText(this, "Enter a Valid Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringHelper.isEmpty(password)) {
            passwordEd.setError("Password is required field");
            passwordEd.requestFocus();
            Toast.makeText(this, "Password is required field", Toast.LENGTH_SHORT).show();
            return;
        }
        if (CheckInternetConnectivity.isInternetConnected(this)) {
            ServerLogic.verifyLoginCredentials(this, email, password);
        } else {
            Toast.makeText(this, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoSignup(View view) {
        startActivity(new Intent(SignInActivity.this, RegistrationActivity.class));
    }

    public void gotoForgetPassword(View view) {
        startActivity(new Intent(SignInActivity.this, ForgetPasswordActivity.class));
    }
}