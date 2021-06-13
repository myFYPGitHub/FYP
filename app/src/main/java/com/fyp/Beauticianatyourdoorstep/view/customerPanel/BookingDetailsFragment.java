package com.fyp.Beauticianatyourdoorstep.view.customerPanel;

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
import com.fyp.Beauticianatyourdoorstep.model.BookingItem;
import com.fyp.Beauticianatyourdoorstep.model.DB;
import com.fyp.Beauticianatyourdoorstep.model.User;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomInputDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomProgressDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

public class BookingDetailsFragment extends Fragment implements MyConstants {
    private TextView serviceNameTv, serviceDetailsTv, genderTv, nameTv, addressTv, contactTv, serviceDateTv, serviceTimingTv, bookingStatusTv, ageTv, cityTv;
    private RatingBar beauticianRatingBar;
    private RoundedImageView profile_pic;
    private User beautician;
    private Booking booking;
    private Button cancelBtn, completeBtn;
    private Context context;
    private DatabaseReference parent_node;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_booking_details, container, false);
        parent_node = DB.getRtDBRootNodeReference();
        context = getActivity();
        Intent it = getActivity().getIntent();
        BookingItem bookingItem = (BookingItem) it.getSerializableExtra(EXTRA_BOOKING_DETAILS);
        beautician = bookingItem.getBeauticianInstance();
        booking = bookingItem.getBookingInstance();
        serviceNameTv = rootView.findViewById(R.id.booking_details_service_name);
        serviceDetailsTv = rootView.findViewById(R.id.booking_details_serviceDetails);
        nameTv = rootView.findViewById(R.id.booking_details_name);
        addressTv = rootView.findViewById(R.id.booking_details_address);
        contactTv = rootView.findViewById(R.id.booking_details_contact);
        genderTv = rootView.findViewById(R.id.booking_details_gender);
        ageTv = rootView.findViewById(R.id.booking_details_age);
        serviceDateTv = rootView.findViewById(R.id.booking_details_serviceDate);
        cityTv = rootView.findViewById(R.id.booking_details_city);
        serviceTimingTv = rootView.findViewById(R.id.booking_details_serviceTiming);
        bookingStatusTv = rootView.findViewById(R.id.booking_details_bookingStatus);
        beauticianRatingBar = rootView.findViewById(R.id.booking_details_rating);
        profile_pic = rootView.findViewById(R.id.booking_details_Dp);
        cancelBtn = rootView.findViewById(R.id.booking_details_cancelBtn);
        completeBtn = rootView.findViewById(R.id.booking_details_completeBtn);
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
                                CustomProgressDialog progDialog = new CustomProgressDialog(context, "Processing . . .");
                                progDialog.showDialog();
                                if (!customInputDialog.getInputText().equals("")) {
                                    if (CheckInternetConnectivity.isInternetConnected(context)) {
                                        booking_node.child(BOOKING_STATUS).setValue(ORDER_CANCELLED)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        booking_node.child(BOOKING_BEAUTICIAN_STATUS).setValue(ORDER_CANCELLED);
                                                        booking_node.child(BOOKING_CUSTOMER_STATUS).setValue(ORDER_CANCELLED);
                                                        cancelBtn.setVisibility(View.GONE);
                                                        bookingStatusTv.setText(ORDER_CANCELLED);
                                                        booking_node.child(BOOKING_CANCELLATION_REASON).setValue(customInputDialog.getInputText());
                                                        progDialog.dismissDialog();
                                                        Toast.makeText(context, "Booking Cancelled", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Cancellation Failed", Toast.LENGTH_SHORT).show();
                                                progDialog.dismissDialog();
                                            }
                                        });
                                    } else {
                                        progDialog.dismissDialog();
                                        Toast.makeText(context, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Cancellation Reason is mandatory", Toast.LENGTH_SHORT).show();
                                }
                                customInputDialog.dismissDialog();
                            }
                        });
            }
        });
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerRatingDialog ratingDialog = new CustomerRatingDialog(context);
                ratingDialog.setSendBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ratingDialog.getRating() == 0) {
                            Toast.makeText(context, "Rating the Service is required", Toast.LENGTH_SHORT).show();
                            ratingDialog.dismissDialog();
                            return;
                        }
                        CustomProgressDialog progDialog = new CustomProgressDialog(context, "Sending Feedback . . .");
                        progDialog.showDialog();
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            booking_node.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        final Booking new_booking = snapshot.getValue(Booking.class);
                                        new_booking.setBookingCustomerStatus(ORDER_COMPLETED);
                                        new_booking.setBookingStatus(ORDER_COMPLETED);
                                        new_booking.setServiceRating(ratingDialog.getRating());
                                        new_booking.setServiceReview(ratingDialog.getReview());
                                        booking_node.setValue(new_booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        String beauticianEmailId = StringHelper.removeInvalidCharsFromIdentifier(beautician.getEmail());
                                                        DatabaseReference beauticianRef = parent_node.child(NODE_USER).child(beauticianEmailId);
                                                        beauticianRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()) {
                                                                    Beautician object = snapshot.getValue(Beautician.class);
                                                                    int total_rating = object.getTotalRating();
                                                                    int num_of_rating = object.getNumOfRating();
                                                                    num_of_rating += 1;
                                                                    beauticianRef.child(USER_TOTAL_RATING).setValue(total_rating + ratingDialog.getRating());
                                                                    beauticianRef.child(USER_NUM_OF_RATING).setValue(num_of_rating);
                                                                    completeBtn.setVisibility(View.GONE);
                                                                    bookingStatusTv.setText(ORDER_COMPLETED);
                                                                    progDialog.dismissDialog();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                            }
                                                        });
                                                    }
                                                });
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
        if (beautician.getProfilePicUri() != null)
            Glide.with(this).load(Uri.parse(beautician.getProfilePicUri())).into(profile_pic);
        String first_name = beautician.getFirstName();
        String last_name = beautician.getLastName();
        String beauticianName = first_name + " " + last_name;
        nameTv.setText(StringHelper.capitalizeString(beauticianName));
        Integer total_rating = beautician.getTotalRating();
        Integer num_of_rating = beautician.getNumOfRating();
        int beautician_rating = total_rating / (num_of_rating == 0 ? 1 : num_of_rating);
        beauticianRatingBar.setRating(beautician_rating);
        contactTv.setText(beautician.getContact());
        genderTv.setText(beautician.getGender());
        cityTv.setText(StringHelper.capitalizeString(beautician.getCity()));
        ageTv.setText(beautician.getAge());
        addressTv.setText(beautician.getAddress());
        String bookingBeauticianStatus = booking.getBookingBeauticianStatus();
        if (bookingBeauticianStatus.equals(ORDER_PENDING) || bookingBeauticianStatus.equals(ORDER_PROGRESS)) {
            cancelBtn.setVisibility(View.VISIBLE);
        } else if (bookingBeauticianStatus.equals(ORDER_COMPLETED)) {
            completeBtn.setVisibility(View.VISIBLE);
        }
    }
}