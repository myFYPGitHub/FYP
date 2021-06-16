package com.fyp.Beauticianatyourdoorstep.view.customerPanel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.controller.ServerLogic;
import com.fyp.Beauticianatyourdoorstep.helper.LoginManagement;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.model.DB;
import com.fyp.Beauticianatyourdoorstep.model.User;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomConfirmDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomProgressDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomToast;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

public class CustomerProfileManagerActivity extends AppCompatActivity implements MyConstants {
    private Uri new_image_uri, current_image_uri;
    private RoundedImageView profile_pic;
    private Context context;
    private EditText firstNameEd, lastNameEd, ageEd, addressEd;
    private Spinner citySpinner;
    private String profilePicStorageId;
    private RatingBar ratingBar;
    private DatabaseReference realtimeDatabaseReference;
    private String email_identifier;
    private TextView email_tv;
    private ImageView deletePicBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile_manager);
        context = CustomerProfileManagerActivity.this;
        ratingBar = findViewById(R.id.customerMgrRatingBar);
        email_tv = findViewById(R.id.customerMgrEmail);
        firstNameEd = findViewById(R.id.customerMgrFirstName);
        lastNameEd = findViewById(R.id.customerMgrLastName);
        ageEd = findViewById(R.id.customerMgrAge);
        addressEd = findViewById(R.id.customerMgrAddress);
        citySpinner = findViewById(R.id.customerMgrCity);
        profile_pic = findViewById(R.id.customerMgrProfilePic);
        email_identifier = new LoginManagement(context).getEmailIdentifier();
        realtimeDatabaseReference = DB.getRtDBRootNodeReference();
        findViewById(R.id.customerMgrSelectPic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicFromDevice();
            }
        });
        deletePicBtn = findViewById(R.id.customerMgrDeletePic);
        deletePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProfilePic();
            }
        });
        findViewById(R.id.customerMgrSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternetConnectivity.isInternetConnected(context)) {
                    saveChanges();
                } else {
                    Toast.makeText(context, NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            getBeauticianProfile();
        } else {
            Toast.makeText(context, NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveChanges() {
        String firstName = firstNameEd.getText().toString();
        String lastName = lastNameEd.getText().toString();
        String age = ageEd.getText().toString();
        String address = addressEd.getText().toString();
        String city = citySpinner.getSelectedItem().toString();
        if (firstName.isEmpty()) {
            firstNameEd.setError("First Name is a required field");
            return;
        }
        if (age.isEmpty()) {
            ageEd.setError("Age is a required field");
            return;
        }
        if (address.isEmpty()) {
            addressEd.setError("Address is a required field");
            return;
        }
        if (citySpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please Select a City", Toast.LENGTH_LONG).show();
            return;
        }
        final CustomProgressDialog progDialog = new CustomProgressDialog(context, "Saving Data . . .");
        progDialog.showDialog();
        try {
            DatabaseReference dbRef = realtimeDatabaseReference.child(NODE_USER).child(email_identifier);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dbRef.child(USER_FIRST_NAME).setValue(firstName);
                        dbRef.child(USER_LAST_NAME).setValue(lastName);
                        dbRef.child(USER_AGE).setValue(age);
                        dbRef.child(USER_ADDRESS).setValue(address);
                        dbRef.child(USER_CITY).setValue(city);
                        progDialog.dismissDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(context, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void getBeauticianProfile() {
        final CustomProgressDialog progDialog = new CustomProgressDialog(context, "Refreshing Data . . .");
        progDialog.showDialog();
        try {
            DatabaseReference dbRef = realtimeDatabaseReference.child(NODE_USER).child(email_identifier);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User customer = dataSnapshot.getValue(User.class);
                        Integer total_rating = customer.getTotalRating();
                        Integer num_of_rating = customer.getNumOfRating();
                        int rating = total_rating / (num_of_rating == 0 ? 1 : num_of_rating);
                        ratingBar.setRating(rating);
                        email_tv.setText(customer.getEmail());
                        firstNameEd.setText(customer.getFirstName());
                        lastNameEd.setText(customer.getLastName());
                        ageEd.setText(customer.getAge());
                        addressEd.setText(customer.getAddress());
                        String city = customer.getCity();
                        selectSpinnerItemByValue(citySpinner, city);
                        profilePicStorageId = customer.getProfilePicStorageId();
                        if (customer.getProfilePicUri() != null) {
                            current_image_uri = Uri.parse(customer.getProfilePicUri());
                            Glide.with(context).load(current_image_uri).into(profile_pic);
                            deletePicBtn.setVisibility(View.VISIBLE);
                        }
                        progDialog.dismissDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(context, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void selectSpinnerItemByValue(Spinner spnr, String value) {
        for (int i = 0; i < spnr.getCount(); i++) {
            if (spnr.getItemAtPosition(i).toString().equals(value)) {
                spnr.setSelection(i);
                return;
            }
        }
    }

    private void selectProfilePicFromDevice() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 89);
            } else {
                startActivityForResult(intent, 89);
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(intent, 89);
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 89 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 89 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            new_image_uri = data.getData();
            profile_pic.setImageURI(new_image_uri);
            final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, "Do you want to Update your Profile Photo?");
            customConfirmDialog.setOkBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckInternetConnectivity.isInternetConnected(context)) {
                        ServerLogic.uploadProfilePic(CustomerProfileManagerActivity.this, new_image_uri);
                        deletePicBtn.setVisibility(View.VISIBLE);
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                    }
                    customConfirmDialog.dismissDialog();
                }
            });
            customConfirmDialog.setNegBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (current_image_uri != null) {
                        Glide.with(context).load(current_image_uri).into(profile_pic);
                    } else {
                        Glide.with(context).load(getResources().getDrawable(R.drawable.no_dp)).into(profile_pic);
                    }
                    Toast.makeText(context, "Updation Cancelled", Toast.LENGTH_SHORT).show();
                    customConfirmDialog.dismissDialog();
                }
            });
        } else {
            new_image_uri = null;
            Toast.makeText(context, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProfilePic() {
        final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, "Are sure to delete this Profile Photo?");
        customConfirmDialog.setOkBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternetConnectivity.isInternetConnected(context)) {
                    if (profilePicStorageId != null) {
                        ServerLogic.deleteProfilePic(context, profilePicStorageId);
                        profilePicStorageId = null;
                        current_image_uri = null;
                        Glide.with(context).load(getResources().getDrawable(R.drawable.no_dp)).into(profile_pic);
                        deletePicBtn.setVisibility(View.GONE);
                    }

                } else {
                    Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                }
                customConfirmDialog.dismissDialog();
            }
        });
        customConfirmDialog.setNegBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customConfirmDialog.dismissDialog();
            }
        });
    }
}