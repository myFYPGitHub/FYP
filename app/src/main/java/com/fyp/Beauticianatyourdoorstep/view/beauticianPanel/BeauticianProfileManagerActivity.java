package com.fyp.Beauticianatyourdoorstep.view.beauticianPanel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
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
import com.fyp.Beauticianatyourdoorstep.model.Beautician;
import com.fyp.Beauticianatyourdoorstep.model.DB;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomConfirmDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomProgressDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomToast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

public class BeauticianProfileManagerActivity extends AppCompatActivity implements MyConstants {
    private Uri new_image_uri, current_image_uri;
    private RoundedImageView profile_pic;
    private Context context;
    private String current_specialization;
    private EditText descEd, addressEd, ageEd;
    private Spinner citySpinner, specializationSpinner;
    private String profilePicStorageId;
    private RatingBar beautyMgrRatingBar;
    private SwitchMaterial beautyMgrSwitch;
    private DatabaseReference realtimeDatabaseReference;
    private String email_identifier;
    private TextView specializationTv, rating_counterTv;
    private ImageView deletePicBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beautician_profile_manager);
        context = BeauticianProfileManagerActivity.this;
        Intent it = getIntent();
        specializationTv = findViewById(R.id.beautyMgrCurrent_specialization);
        current_specialization = it.getStringExtra(EXTRA_BEAUTICIAN_SPECIALIZATION);
        specializationTv.setText(current_specialization);
        beautyMgrRatingBar = findViewById(R.id.beautyMgrRatingBar);
        rating_counterTv = findViewById(R.id.beautyMgrRatingCounter);
        descEd = findViewById(R.id.beautyMgrDesc);
        addressEd = findViewById(R.id.beautyMgrAddress);
        ageEd = findViewById(R.id.beautyMgrAge);
        citySpinner = findViewById(R.id.beautyMgrCity);
        specializationSpinner = findViewById(R.id.beautyMgrSpecialization);
        profile_pic = findViewById(R.id.beautyMgrProfilePic);
        beautyMgrSwitch = findViewById(R.id.beautyMgrSwitch);
        email_identifier = new LoginManagement(context).getEmailIdentifier();
        realtimeDatabaseReference = DB.getRtDBRootNodeReference();
        findViewById(R.id.beautyMgrSelectPic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicFromDevice();
            }
        });
        deletePicBtn = findViewById(R.id.beautyMgrDeletePic);
        deletePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProfilePic();
            }
        });
        findViewById(R.id.beautyMgrSaveBtn).setOnClickListener(new View.OnClickListener() {
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
        String desc = descEd.getText().toString();
        String address = addressEd.getText().toString();
        String age = ageEd.getText().toString();
        String specializationChanged = specializationSpinner.getSelectedItem().toString();
        int specializationChanged_pos = specializationSpinner.getSelectedItemPosition();
        if (address.isEmpty()) {
            addressEd.setError("Please Enter your Address");
            return;
        }
        if (age.isEmpty()) {
            ageEd.setError("Age is a required field");
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
                        String city = citySpinner.getSelectedItem().toString();
                        dbRef.child(USER_DESCRIPTION).setValue(desc);
                        dbRef.child(USER_ADDRESS).setValue(address);
                        dbRef.child(USER_AGE).setValue(age);
                        dbRef.child(USER_CITY).setValue(city);
                        if (specializationChanged_pos != 0 && !specializationChanged.equals(current_specialization)) {
                            dbRef.child(BEAUTICIAN_SPECIALIZATION).setValue(specializationChanged)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dbRef.child(NODE_BEAUTICIAN_SERVICES).removeValue();
                                            beautyMgrSwitch.setChecked(false);
                                            current_specialization = specializationChanged;
                                            specializationTv.setText(current_specialization);
                                            Toast.makeText(context, "Your Specialization has been Changed", Toast.LENGTH_LONG).show();
                                        }
                                    });
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
                        Beautician beautician = dataSnapshot.getValue(Beautician.class);
                        Integer total_rating = beautician.getTotalRating();
                        Integer num_of_rating = beautician.getNumOfRating();
                        int beautician_rating = total_rating / (num_of_rating == 0 ? 1 : num_of_rating);
                        rating_counterTv.setText(total_rating + "/" + num_of_rating);
                        beautyMgrRatingBar.setRating(beautician_rating);
                        descEd.setText(beautician.getDescription());
                        if (descEd.getText().toString().equals("")) {
                            descEd.setError("please write your description");
                        }
                        addressEd.setText(beautician.getAddress());
                        ageEd.setText(beautician.getAge());
                        String city = beautician.getCity();
                        selectSpinnerItemByValue(citySpinner, city);
                        if (beautician.getAvailability()) {
                            beautyMgrSwitch.setChecked(true);
                            beautyMgrSwitch.setText("Available");
                        }
                        profilePicStorageId = beautician.getProfilePicStorageId();
                        if (beautician.getProfilePicUri() != null) {
                            current_image_uri = Uri.parse(beautician.getProfilePicUri());
                            Glide.with(context).load(current_image_uri).into(profile_pic);
                            deletePicBtn.setVisibility(View.VISIBLE);
                        }
                        progDialog.dismissDialog();
                    }
                    beautyMgrSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            progDialog.showDialog();
                            if (b) {
                                dbRef.child(NODE_BEAUTICIAN_SERVICES)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    dbRef.child(BEAUTICIAN_AVAILABILITY).setValue(true)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    beautyMgrSwitch.setText("Available");
                                                                    progDialog.dismissDialog();
                                                                }
                                                            });
                                                } else {
                                                    beautyMgrSwitch.setChecked(false);
                                                    Toast.makeText(context, "You have not provide any Service!\nMake sure you provide at least one Service.", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                            } else {
                                dbRef.child(BEAUTICIAN_AVAILABILITY).setValue(false)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                beautyMgrSwitch.setText("Unavailable");
                                                progDialog.dismissDialog();
                                            }
                                        });
                            }
                        }
                    });
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
                        ServerLogic.uploadProfilePic(BeauticianProfileManagerActivity.this, new_image_uri);
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