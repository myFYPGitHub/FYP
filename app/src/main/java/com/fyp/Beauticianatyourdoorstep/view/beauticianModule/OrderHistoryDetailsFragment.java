package com.fyp.Beauticianatyourdoorstep.view.beauticianModule;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.helper.StringHelper;
import com.fyp.Beauticianatyourdoorstep.model.Booking;
import com.fyp.Beauticianatyourdoorstep.model.OrderItem;
import com.fyp.Beauticianatyourdoorstep.model.User;
import com.makeramen.roundedimageview.RoundedImageView;

public class OrderHistoryDetailsFragment extends Fragment implements MyConstants {
    private TextView serviceNameTv, serviceDetailsTv, cusGenderTv, cusNameTv, cusAddressTv, cusContactTv,cusAgeTv, serviceDateTv, serviceTimingTv, bookingStatusTv, cusCityTv, cancelReasonTv, serviceReviewTv;
    private RatingBar customerRatingBar, serviceRatingBar;
    private RoundedImageView profile_pic;
    private LinearLayout cancelReasonContainer;
    private User customer;
    private Booking booking;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_history_details, container, false);
        Intent it = getActivity().getIntent();
        OrderItem orderItem = (OrderItem) it.getSerializableExtra(EXTRA_BOOKING_DETAILS);
        customer = orderItem.getUserInstance();
        booking = orderItem.getBookingInstance();
        serviceNameTv = rootView.findViewById(R.id.history_details_service_name);
        serviceDetailsTv = rootView.findViewById(R.id.history_details_serviceDetails);
        cusNameTv = rootView.findViewById(R.id.history_details_cusName);
        cusAddressTv = rootView.findViewById(R.id.history_details_cusAddress);
        cusContactTv = rootView.findViewById(R.id.history_details_cusContact);
        cusGenderTv = rootView.findViewById(R.id.history_details_cusGender);
        cusAgeTv = rootView.findViewById(R.id.history_details_cusAge);
        serviceDateTv = rootView.findViewById(R.id.history_details_serviceDate);
        cusCityTv = rootView.findViewById(R.id.history_details_cusCity);
        serviceTimingTv = rootView.findViewById(R.id.history_details_serviceTiming);
        bookingStatusTv = rootView.findViewById(R.id.history_details_bookingStatus);
        customerRatingBar = rootView.findViewById(R.id.history_details_cusRating);
        profile_pic = rootView.findViewById(R.id.history_details_cusDp);
        cancelReasonTv = rootView.findViewById(R.id.history_details_cancelReason);
        serviceRatingBar = rootView.findViewById(R.id.history_details_service_rating);
        serviceReviewTv = rootView.findViewById(R.id.history_details_review);
        cancelReasonContainer = rootView.findViewById(R.id.history_details_cancelReasonContainer);
        fillAvailableData();
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
        customerRatingBar.setRating(customer_rating);
        cusContactTv.setText(customer.getContact());
        cusGenderTv.setText(customer.getGender());
        cusAgeTv.setText(customer.getAge());
        cusCityTv.setText(StringHelper.capitalizeString(customer.getCity()));
        cusAddressTv.setText(customer.getAddress());
        if (booking_status.equals(ORDER_CANCELLED)) {
            cancelReasonTv.setText(booking.getCancellationReason());
            System.out.println(booking.getCancellationReason());
            cancelReasonContainer.setVisibility(View.VISIBLE);
        }
        String service_review = booking.getServiceReview();
        if (service_review != null) {
            serviceReviewTv.setText(service_review);
        }
        serviceRatingBar.setRating(booking.getServiceRating());
    }
}