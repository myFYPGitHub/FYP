package com.fyp.Beauticianatyourdoorstep.view.beauticianPanel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.helper.StringHelper;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.model.Beautician;
import com.fyp.Beauticianatyourdoorstep.model.Booking;
import com.fyp.Beauticianatyourdoorstep.model.DB;
import com.fyp.Beauticianatyourdoorstep.model.OrderItem;
import com.fyp.Beauticianatyourdoorstep.model.User;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomConfirmDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomInputDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomProgressDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

public class OrderDetailsFragment extends Fragment implements MyConstants {
    private TextView serviceNameTv, serviceDetailsTv, cusGenderTv, cusNameTv, cusAddressTv, cusAgeTv, cusContactTv, serviceDateTv, serviceTimingTv, bookingStatusTv, cusCityTv;
    private RatingBar ratingBar;
    private RoundedImageView profile_pic;
    private User customer;
    private Booking booking;
    private Button cancelBtn, acceptBtn, completeBtn;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_details, container, false);
        DatabaseReference parent_node = DB.getRtDBRootNodeReference();
        context = getActivity();
        Intent it = getActivity().getIntent();
        OrderItem orderItem = (OrderItem) it.getSerializableExtra(EXTRA_BOOKING_DETAILS);
        customer = orderItem.getUserInstance();
        booking = orderItem.getBookingInstance();
        serviceNameTv = rootView.findViewById(R.id.order_details_service_name);
        serviceDetailsTv = rootView.findViewById(R.id.order_details_serviceDetails);
        cusNameTv = rootView.findViewById(R.id.order_details_cusName);
        cusAddressTv = rootView.findViewById(R.id.order_details_cusAddress);
        cusContactTv = rootView.findViewById(R.id.order_details_cusContact);
        cusGenderTv = rootView.findViewById(R.id.order_details_cusGender);
        cusAgeTv = rootView.findViewById(R.id.order_details_cusAge);
        serviceDateTv = rootView.findViewById(R.id.order_details_serviceDate);
        cusCityTv = rootView.findViewById(R.id.order_details_cusCity);
        serviceTimingTv = rootView.findViewById(R.id.order_details_serviceTiming);
        bookingStatusTv = rootView.findViewById(R.id.order_details_bookingStatus);
        ratingBar = rootView.findViewById(R.id.order_details_rating);
        profile_pic = rootView.findViewById(R.id.order_details_cusDp);
        cancelBtn = rootView.findViewById(R.id.order_details_cancelBtn);
        acceptBtn = rootView.findViewById(R.id.order_details_acceptBtn);
        completeBtn = rootView.findViewById(R.id.order_details_completeBtn);
        fillAvailableData();
        DatabaseReference booking_node = parent_node.child(NODE_BOOKING).child(booking.getBookingId());
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomInputDialog customInputDialog = new CustomInputDialog(context, "Cancellation Reason");
                customInputDialog.setInputHint("Why you are cancelling this booking?")
                        .setOkBtnListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!customInputDialog.getInputText().equals("")) {
                                    if (CheckInternetConnectivity.isInternetConnected(context)) {
                                        booking_node.child(BOOKING_STATUS).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String status = snapshot.getValue(String.class);
                                                if (status.equals(ORDER_PENDING) || status.equals(ORDER_PROGRESS)) {
                                                    booking_node.child(BOOKING_STATUS).setValue(ORDER_CANCELLED)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    booking_node.child(BOOKING_BEAUTICIAN_STATUS).setValue(ORDER_CANCELLED);
                                                                    booking_node.child(BOOKING_CUSTOMER_STATUS).setValue(ORDER_CANCELLED);
                                                                    cancelBtn.setVisibility(View.GONE);
                                                                    acceptBtn.setVisibility(View.GONE);
                                                                    completeBtn.setVisibility(View.GONE);
                                                                    bookingStatusTv.setText(ORDER_CANCELLED);
                                                                    booking_node.child(BOOKING_CANCELLATION_REASON).setValue(customInputDialog.getInputText());
                                                                    Toast.makeText(context, "Booking Cancelled", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(context, "Sorry! This booking is already cancelled by requested customer", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Cancellation Reason is mandatory", Toast.LENGTH_LONG).show();
                                }
                                customInputDialog.dismissDialog();
                            }
                        });
            }
        });
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, "Are you sure to Accept this Appointment?");
                customConfirmDialog.setOkBtnText("Accept")
                        .setOkBtnListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (CheckInternetConnectivity.isInternetConnected(context)) {
                                    booking_node.child(BOOKING_STATUS).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String status = snapshot.getValue(String.class);
                                            if (status.equals(ORDER_PENDING)) {
                                                booking_node.child(BOOKING_BEAUTICIAN_STATUS).setValue(ORDER_PROGRESS)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                booking_node.child(BOOKING_STATUS).setValue(ORDER_PROGRESS);
                                                                acceptBtn.setVisibility(View.GONE);
                                                                completeBtn.setVisibility(View.VISIBLE);
                                                                bookingStatusTv.setText(ORDER_PROGRESS);
                                                                Toast.makeText(context, "Booking Accepted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(context, "Sorry! This booking is already cancelled by requested customer", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                                }
                                customConfirmDialog.dismissDialog();
                            }
                        });
            }
        });
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BeauticianRatingDialog ratingDialog = new BeauticianRatingDialog(context);
                ratingDialog.setSendBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*if (ratingDialog.getRating() == 0) {
                            Toast.makeText(context, "Rating your customer is required", Toast.LENGTH_SHORT).show();
                            ratingDialog.dismissDialog();
                            return;
                        }*/
                        CustomProgressDialog progDialog = new CustomProgressDialog(context, "Sending Feedback . . .");
                        progDialog.showDialog();
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            booking_node.child(BOOKING_STATUS).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String status = snapshot.getValue(String.class);
                                    if (status.equals(ORDER_PENDING) || status.equals(ORDER_PROGRESS)) {
                                        booking_node.child(BOOKING_BEAUTICIAN_STATUS).setValue(ORDER_COMPLETED)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        String customerEmailId = StringHelper.removeInvalidCharsFromIdentifier(customer.getEmail());
                                                        DatabaseReference customerRef = parent_node.child(NODE_USER).child(customerEmailId);
                                                        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()) {
                                                                    User cusObject = snapshot.getValue(Beautician.class);
                                                                    int total_rating = cusObject.getTotalRating();
                                                                    int num_of_rating = cusObject.getNumOfRating();
                                                                    num_of_rating += 1;
                                                                    customerRef.child(USER_TOTAL_RATING).setValue(total_rating + ratingDialog.getRating());
                                                                    customerRef.child(USER_NUM_OF_RATING).setValue(num_of_rating);
                                                                    completeBtn.setVisibility(View.GONE);
                                                                    cancelBtn.setVisibility(View.GONE);
                                                                    bookingStatusTv.setText(ORDER_COMPLETED);
                                                                    Toast.makeText(context, "Please wait for Customer Response", Toast.LENGTH_SHORT).show();
                                                                    progDialog.dismissDialog();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                            }
                                                        });
                                                    }
                                                });
                                    } else {
                                        progDialog.dismissDialog();
                                        Toast.makeText(context, "Sorry! This booking is already cancelled by requested customer", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        } else {
                            progDialog.dismissDialog();
                            Toast.makeText(context, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                        }
                        ratingDialog.dismissDialog();
                    }
                });
            }
        });
        return rootView;
    }

    private void fillAvailableData() {
        serviceNameTv.setText(booking.getServiceName());
        serviceDetailsTv.setText(booking.getServiceDetails());
        String booking_status = booking.getBookingStatus();
        bookingStatusTv.setText(StringHelper.capitalizeString(booking_status));
        serviceDateTv.setText("Date: " + booking.getRequestDate());
        String start_time = booking.getStartTime();
        String end_time = booking.getEndTime();
        String timing = "Timing: " + start_time + "-" + end_time;
        serviceTimingTv.setText(timing);
        if (customer.getProfilePicUri() != null)
            Glide.with(this).load(Uri.parse(customer.getProfilePicUri())).into(profile_pic);
        String first_name = customer.getFirstName();
        String last_name = customer.getLastName();
        String customerName = first_name + " " + last_name;
        cusNameTv.setText(StringHelper.capitalizeString(customerName));
        Integer total_rating = customer.getTotalRating();
        Integer num_of_rating = customer.getNumOfRating();
        int customer_rating = total_rating / (num_of_rating == 0 ? 1 : num_of_rating);
        ratingBar.setRating(customer_rating);
        cusContactTv.setText(customer.getContact());
        cusGenderTv.setText(customer.getGender());
        cusAgeTv.setText(customer.getAge());
        cusCityTv.setText(StringHelper.capitalizeString(customer.getCity()));
        cusAddressTv.setText(customer.getAddress());
        String bookingBeauticianStatus = booking.getBookingBeauticianStatus();
        if (bookingBeauticianStatus.equals(ORDER_PROGRESS)) {
            acceptBtn.setVisibility(View.GONE);
            completeBtn.setVisibility(View.VISIBLE);
        } else if (bookingBeauticianStatus.equals(ORDER_COMPLETED)) {
            acceptBtn.setVisibility(View.GONE);
            completeBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        }
    }
}