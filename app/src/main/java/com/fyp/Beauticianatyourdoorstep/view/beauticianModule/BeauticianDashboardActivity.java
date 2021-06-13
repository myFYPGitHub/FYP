package com.fyp.Beauticianatyourdoorstep.view.beauticianModule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.helper.LoginManagement;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.helper.StringHelper;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.model.DB;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomConfirmDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomProgressDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomToast;
import com.fyp.Beauticianatyourdoorstep.view.AppSettingsActivity;
import com.fyp.Beauticianatyourdoorstep.view.MapActivity;
import com.fyp.Beauticianatyourdoorstep.view.SignInActivity;
import com.fyp.Beauticianatyourdoorstep.view.customerModule.UpdatesActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

public class BeauticianDashboardActivity extends AppCompatActivity implements MyConstants {
    private Context context;
    private static DrawerLayout drawerLayout;
    private static final Intent activity_opener = new Intent();
    private TextView drawer_fullname_tv;
    private static LoginManagement loginManagement;
    private static String specialization;
    private RoundedImageView drawer_profile_pic;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beautician_nav_drawer);
        context = BeauticianDashboardActivity.this;
        loginManagement = new LoginManagement(context);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.drawer_nav_view);
        navigationView.setItemIconTintList(null);   //This is make drawer icons colorful
        View headerView = navigationView.getHeaderView(0);
        drawer_profile_pic = headerView.findViewById(R.id.nav_drawer_profile_pic);
        drawer_fullname_tv = headerView.findViewById(R.id.nav_drawer_fullname);
        TextView drawer_email_tv = headerView.findViewById(R.id.nav_drawer_email);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                try {
                    switch (item.getItemId()) {
                        case R.id.drawer_settings_item:
                            Intent it = new Intent();
                            it.putExtra(EXTRA_CLASS, BeauticianDashboardActivity.class);
                            it.setClass(context, AppSettingsActivity.class);
                            startActivity(it);
                            finish();
                            break;
                        case R.id.drawer_logout_item:
                            logout();
                            break;
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error" + e, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        findViewById(R.id.open_drawer_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        findViewById(R.id.skillBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCorrectActivity();
            }
        });
        findViewById(R.id.historyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternetConnectivity.isInternetConnected(context)) {
                    startActivity(activity_opener.setClass(context, BeauticianOrderHistoryActivity.class));
                } else {
                    Toast.makeText(context, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.appointmentsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternetConnectivity.isInternetConnected(context)) {
                    startActivity(activity_opener.setClass(context, BeauticianOrderRequestsActivity.class));
                } else {
                    Toast.makeText(context, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.mapBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternetConnectivity.isInternetConnected(context)) {
                    Intent it = new Intent();
                    it.putExtra(EXTRA_CUSTOMER_CITY, city);
                    it.setClass(context, MapActivity.class);
                    startActivity(it);
                } else {
                    Toast.makeText(context, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.updateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(activity_opener.setClass(context, UpdatesActivity.class));
            }
        });
        findViewById(R.id.profileMgrBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (specialization == null) {
                    onStart();
                } else {
                    Intent it = new Intent(context, BeauticianProfileManagerActivity.class);
                    it.putExtra(EXTRA_BEAUTICIAN_SPECIALIZATION, specialization);
                    startActivity(it);
                }
            }
        });
        drawer_email_tv.setText(loginManagement.getLoginEmail());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            getBeauticianData(this);
        } else {
            Toast.makeText(context, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    private void openCorrectActivity() {
        if (specialization == null) {
            onStart();
        } else {
            Intent it = new Intent(context, SpecializationActivity.class);
            it.putExtra(EXTRA_BEAUTICIAN_SPECIALIZATION, specialization);
            startActivity(it);
        }
    }

    private void logout() {
        final CustomConfirmDialog confirmDialog = new CustomConfirmDialog(context, "Are you sure to Logout?");

        confirmDialog.setOkBtnText("Logout")
                .setOkBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismissDialog();
                        loginManagement.removeLoginFromDevice();
                        startActivity(activity_opener.setClass(context, SignInActivity.class));
                        finish();
                    }
                });
    }

    private void getBeauticianData(final Context context) {
        DatabaseReference dbRef = DB.getRtDBRootNodeReference();
        final CustomProgressDialog progDialog = new CustomProgressDialog(context, "Refreshing Data . . .");
        progDialog.showDialog();
        try {
            String email_identifier = new LoginManagement(context).getEmailIdentifier();
            dbRef.child(NODE_USER).child(email_identifier)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                city = dataSnapshot.child(USER_CITY).getValue(String.class);
                                String first_name = dataSnapshot.child(USER_FIRST_NAME).getValue(String.class);
                                String last_name = dataSnapshot.child(USER_LAST_NAME).getValue(String.class);
                                String full_name = first_name + " " + last_name;
                                drawer_fullname_tv.setText(StringHelper.capitalizeString(full_name));
                                specialization = dataSnapshot.child(BEAUTICIAN_SPECIALIZATION).getValue(String.class);
                                String profile_pic_uri = dataSnapshot.child(USER_PROFILE_PIC_URI).getValue(String.class);
                                if (profile_pic_uri != null)
                                    Glide.with(context).load(Uri.parse(profile_pic_uri)).into(drawer_profile_pic);
                            }
                            progDialog.dismissDialog();
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

    private static boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isOpen()) {
            drawerLayout.close();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
            doubleBackToExitPressedOnce = true;
            Snackbar.make(findViewById(android.R.id.content), "Press back again to exit", Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1500);
        }
    }
}