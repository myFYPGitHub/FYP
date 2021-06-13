package com.fyp.Beauticianatyourdoorstep.view;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.controller.ServerLogic;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.helper.StringHelper;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.model.Beautician;
import com.fyp.Beauticianatyourdoorstep.model.User;

public class RegistrationActivity extends AppCompatActivity implements MyConstants {
    private EditText firstNameEd, lastNameEd, ageEd, addressEd, emailEd, contactEd, passwordEd, confirmPassEd;
    private Spinner genderSpinner, citySpinner, categorySpinner, specializationSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firstNameEd = findViewById(R.id.regFirstName);
        lastNameEd = findViewById(R.id.regLastName);
        ageEd = findViewById(R.id.regAge);
        addressEd = findViewById(R.id.regAddress);
        emailEd = findViewById(R.id.regEmail);
        contactEd = findViewById(R.id.regContact);
        passwordEd = findViewById(R.id.regPass);
        confirmPassEd = findViewById(R.id.regConfirmPass);
        genderSpinner = findViewById(R.id.regGender);
        citySpinner = findViewById(R.id.regCity);
        categorySpinner = findViewById(R.id.regCategory);
        specializationSpinner = findViewById(R.id.regSpecialization);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    specializationSpinner.setVisibility(View.VISIBLE);
                } else {
                    specializationSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                specializationSpinner.setVisibility(View.GONE);
            }
        });
    }

    public void SignUp(View v) {
        String fname = firstNameEd.getText().toString();
        String lname = lastNameEd.getText().toString();
        String age = ageEd.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        String email = emailEd.getText().toString();
        String contact = contactEd.getText().toString();
        String address = addressEd.getText().toString();
        String city = citySpinner.getSelectedItem().toString();
        String password = passwordEd.getText().toString();
        String confirmPass = confirmPassEd.getText().toString();
        int categoryIndex = categorySpinner.getSelectedItemPosition();
        String category = categorySpinner.getSelectedItem().toString().toLowerCase();
        int genderIndex = genderSpinner.getSelectedItemPosition();
        int cityIndex = citySpinner.getSelectedItemPosition();
        int specializationIndex = specializationSpinner.getSelectedItemPosition();
        String specialization = specializationSpinner.getSelectedItem().toString();
        if (SignUpValidation(fname, email, age, genderIndex, contact, address, cityIndex, password, confirmPass, categoryIndex, specializationIndex)) {
            if (CheckInternetConnectivity.isInternetConnected(this)) {
                password = StringHelper.toMD5String(password);
                if (categoryIndex == 1) {
                    User user = new Beautician(fname, lname, age, gender, email, password, contact, address, city, category, specialization, false);
                    ServerLogic.createNewUserAccount(RegistrationActivity.this, user);
                } else if (categoryIndex == 2) {
                    User user = new User(fname, lname, age, gender, email, password, contact, address, city, category);
                    ServerLogic.createNewUserAccount(RegistrationActivity.this, user);
                }
            } else {
                Toast.makeText(this, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please fill all required Fields", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean SignUpValidation(String fname, String email, String age, int genderIndex, String contact, String address, int cityIndex, String password, String confirmPass, int categoryIndex, int specializationIndex) {
        if (fname.isEmpty()) {
            firstNameEd.setError("Please Enter your First Name");
            return false;
        }
        if (age.isEmpty()) {
            ageEd.setError("Please Enter your Age");
            return false;
        }
        if (genderIndex == 0) {
            Toast.makeText(this, "Please Select a Gender", Toast.LENGTH_LONG).show();
        }
        if (email.isEmpty()) {
            emailEd.setError("Please Enter your valid Email");
            return false;
        }
        if (contact.isEmpty()) {
            contactEd.setError("Please Enter valid Contact Number");
            return false;
        }
        if (address.isEmpty()) {
            addressEd.setError("Please Enter your Address");
            return false;
        }
        if (cityIndex == 0) {
            Toast.makeText(this, "Please Select a City", Toast.LENGTH_LONG).show();
        }
        if (password.isEmpty() || password.length() < MIN_PASSWORD_CHARS) {
            passwordEd.setError("Password must be at least " + MIN_PASSWORD_CHARS + " letters");
            return false;
        }
        if (!(confirmPass.equals(password))) {
            confirmPassEd.setError("Password does not matched");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEd.setError("Please Enter valid Email Address");
            return false;
        }
        String MobilePattern = "[0-9]{11}";
        if (!contactEd.getText().toString().matches(MobilePattern)) {
            contactEd.setError("Please Enter valid Contact Number");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEd.setError("Please Enter valid Email Address");
            return false;
        }
        if (categoryIndex == 0) {
            Toast.makeText(this, "Please Select a Category", Toast.LENGTH_LONG).show();
            return false;
        }
        if (categoryIndex == 1 && specializationIndex == 0) {
            Toast.makeText(this, "Please Select your Specialization", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}