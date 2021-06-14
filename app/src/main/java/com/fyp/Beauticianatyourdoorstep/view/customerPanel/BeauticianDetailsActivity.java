package com.fyp.Beauticianatyourdoorstep.view.customerPanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.controller.ServerLogic;
import com.fyp.Beauticianatyourdoorstep.helper.LoginManagement;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.helper.StringHelper;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.model.Beautician;
import com.fyp.Beauticianatyourdoorstep.model.BeauticianItem;
import com.fyp.Beauticianatyourdoorstep.model.BeauticianService;
import com.fyp.Beauticianatyourdoorstep.model.Booking;
import com.fyp.Beauticianatyourdoorstep.model.DB;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomMsgDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class BeauticianDetailsActivity extends AppCompatActivity implements MyConstants {
    private TextView beauticianNameTv, descriptionTv, genderTv, addressTv, contactTv, ageTv, cityTv;
    private RatingBar ratingBar;
    private RoundedImageView profile_pic;
    private Beautician beautician;
    private ArrayList<BeauticianService> services;
    private static DatabaseReference parent_node;
    private Context context;
    private LinearLayout services_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beautician_details);
        context = BeauticianDetailsActivity.this;
        parent_node = DB.getRtDBRootNodeReference();
        Intent it = getIntent();
        BeauticianItem beauticianItem = (BeauticianItem) it.getSerializableExtra(EXTRA_BEAUTICIAN_DETAILS);
        beautician = beauticianItem.getBeauticianInstance();
        services = beauticianItem.getServices();
        beauticianNameTv = findViewById(R.id.beauticianDetails_name);
        descriptionTv = findViewById(R.id.beauticianDetails_description);
        addressTv = findViewById(R.id.beauticianDetails_address);
        contactTv = findViewById(R.id.beauticianDetails_contact);
        genderTv = findViewById(R.id.beauticianDetails_gender);
        ageTv = findViewById(R.id.beauticianDetails_age);
        cityTv = findViewById(R.id.beauticianDetails_city);
        ratingBar = findViewById(R.id.beauticianDetails_rating);
        profile_pic = findViewById(R.id.beauticianDetails_Dp);
        services_container = findViewById(R.id.services_container);
        Button requestBookingBtn = findViewById(R.id.beauticianDetails_requestBookingBtn);
        fillBeauticianData();
        drawBeauticianServices();
        requestBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestBookingDialog requestBookingDialog = new RequestBookingDialog(context);
                requestBookingDialog.setSendRequestBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String date = requestBookingDialog.getSelectedDate();
                        String start_time = requestBookingDialog.getSelectedStartTime();
                        String end_time = requestBookingDialog.getSelectedEndTime();
                        if (start_time.equals(end_time)) {
                            Toast.makeText(context, "Start Time and End Time are same", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (StringHelper.isEmpty(date)) {
                            Toast.makeText(context, "Select booking date", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            final CustomProgressDialog progDialog = new CustomProgressDialog(context, "Checking booking availability . . .");
                            progDialog.showDialog();
                            LoginManagement loginManagement = new LoginManagement(context);
                            final String email = loginManagement.getLoginEmail();
                            try {
                                Query checkBooking = parent_node.child(NODE_BOOKING).orderByChild(BOOKING_CUSTOMER_EMAIL).equalTo(email);
                                checkBooking.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean isBookingAvailable = true;
                                        if (snapshot.exists()) {
                                            for (DataSnapshot orderData : snapshot.getChildren()) {
                                                String booking_status = orderData.child(BOOKING_STATUS).getValue(String.class);
                                                if (booking_status.equals(ORDER_PROGRESS)) {
                                                    new CustomMsgDialog(context, "Sending booking request failed", "You have already sent a booking request to a beautician which is in progress.");
                                                    isBookingAvailable = false;
                                                    break;
                                                } else if (booking_status.equals(ORDER_PENDING)) {
                                                    new CustomMsgDialog(context, "Sending booking request failed", "You have already sent a booking request to a beautician which is pending.");
                                                    isBookingAvailable = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (isBookingAvailable) {
                                            String id = System.currentTimeMillis() + "";
                                            String serviceName = beautician.getSpecialization().split(" ")[0];
                                            String booking_details = requestBookingDialog.getBookingDetails();
                                            Booking booking = new Booking(id, email, beautician.getEmail(), serviceName + " Service", booking_details, date, start_time, end_time);
                                            ServerLogic.requestBooking(context, booking);

                                        }
                                        progDialog.dismissDialog();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                            } catch (Exception e) {
                                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                        }
                        requestBookingDialog.dismissDialog();
                    }
                });
            }
        });
    }

    private boolean isDateTimeValid(String date, String startTime, String endTime) {
        return false;
    }

    private void fillBeauticianData() {
        descriptionTv.setText(beautician.getDescription());
        ageTv.setText(beautician.getAge());
        if (beautician.getProfilePicUri() != null)
            Glide.with(this).load(Uri.parse(beautician.getProfilePicUri())).into(profile_pic);
        String first_name = beautician.getFirstName();
        String last_name = beautician.getLastName();
        String fullName = first_name + " " + last_name;
        beauticianNameTv.setText(StringHelper.capitalizeString(fullName));
        Integer total_rating = beautician.getTotalRating();
        Integer num_of_rating = beautician.getNumOfRating();
        int rating = total_rating / (num_of_rating == 0 ? 1 : num_of_rating);
        ratingBar.setRating(rating);
        contactTv.setText(beautician.getContact());
        genderTv.setText(beautician.getGender());
        cityTv.setText(StringHelper.capitalizeString(beautician.getCity()));
        addressTv.setText(beautician.getAddress());
    }

    @SuppressLint("SetTextI18n")
    private void drawBeauticianServices() {
        LinearLayout[] row = new LinearLayout[services.size()];
        LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        LinearLayout.LayoutParams columnLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        int paddingValue = 5;
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingValue, this.getResources().getDisplayMetrics());
        for (int i = 0; i < row.length; i++) {
            row[i] = new LinearLayout(this);
            row[i].setLayoutParams(rowLayoutParams);
            row[i].setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 2; j++) {
                TextView column = new TextView(this);
                column.setLayoutParams(columnLayoutParams);
                column.setGravity(Gravity.CENTER);
                column.setPadding(0, dp, 0, dp);
                switch (j) {
                    case 0:
                        column.setText(services.get(i).getServiceName());
                        break;
                    case 1:
                        String price = services.get(i).getServicePrice().toString();
                        column.setText(price.equals("0.0") ? "Free" : price);
                }
                row[i].addView(column);
            }
            services_container.addView(row[i]);
        }
    }
}