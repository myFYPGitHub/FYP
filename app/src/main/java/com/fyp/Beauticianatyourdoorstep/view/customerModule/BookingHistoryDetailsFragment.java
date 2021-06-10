package com.fyp.Beauticianatyourdoorstep.view.customerModule;

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
import com.fyp.Beauticianatyourdoorstep.model.BookingItem;
import com.fyp.Beauticianatyourdoorstep.model.User;
import com.makeramen.roundedimageview.RoundedImageView;

public class BookingHistoryDetailsFragment extends Fragment implements MyConstants {
    private TextView serviceNameTv, serviceDetailsTv, genderTv, nameTv, addressTv, contactTv, serviceDateTv, serviceTimingTv, bookingStatusTv, ageTv, cityTv, cancelReasonTv, serviceReviewTv;
    private RatingBar beauticianRatingBar, serviceRatingBar;
    private RoundedImageView profile_pic;
    private LinearLayout cancelReasonContainer;
    private User beautician;
    private Booking booking;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_booking_history_details, container, false);
        Intent it = getActivity().getIntent();
        BookingItem bookingItem = (BookingItem) it.getSerializableExtra(EXTRA_BOOKING_DETAILS);
        beautician = bookingItem.getBeauticianInstance();
        booking = bookingItem.getBookingInstance();
        serviceNameTv = rootView.findViewById(R.id.booking_history_details_service_name);
        serviceDetailsTv = rootView.findViewById(R.id.booking_history_details_serviceDetails);
        nameTv = rootView.findViewById(R.id.booking_history_details_name);
        addressTv = rootView.findViewById(R.id.booking_history_details_address);
        contactTv = rootView.findViewById(R.id.booking_history_details_contact);
        genderTv = rootView.findViewById(R.id.booking_history_details_gender);
        ageTv = rootView.findViewById(R.id.booking_history_details_age);
        serviceDateTv = rootView.findViewById(R.id.booking_history_details_serviceDate);
        cityTv = rootView.findViewById(R.id.booking_history_details_city);
        serviceTimingTv = rootView.findViewById(R.id.booking_history_details_serviceTiming);
        bookingStatusTv = rootView.findViewById(R.id.booking_history_details_bookingStatus);
        beauticianRatingBar = rootView.findViewById(R.id.booking_history_details_rating);
        profile_pic = rootView.findViewById(R.id.booking_history_details_Dp);
        cancelReasonTv = rootView.findViewById(R.id.booking_history_details_cancelReason);
        serviceRatingBar = rootView.findViewById(R.id.booking_history_details_service_rating);
        serviceReviewTv = rootView.findViewById(R.id.booking_history_details_review);
        cancelReasonContainer = rootView.findViewById(R.id.booking_history_details_cancelReasonContainer);
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
        if (beautician.getProfilePicUri() != null)
            Glide.with(this).load(Uri.parse(beautician.getProfilePicUri())).into(profile_pic);
        String first_name = beautician.getFirstName();
        String last_name = beautician.getLastName();
        String customerName = first_name + " " + last_name;
        nameTv.setText(StringHelper.capitalizeString(customerName));
        Integer total_rating = beautician.getTotalRating();
        Integer num_of_rating = beautician.getNumOfRating();
        int beautician_rating = total_rating / (num_of_rating == 0 ? 1 : num_of_rating);
        beauticianRatingBar.setRating(beautician_rating);
        contactTv.setText(beautician.getContact());
        genderTv.setText(beautician.getGender());
        cityTv.setText(StringHelper.capitalizeString(beautician.getCity()));
        ageTv.setText(beautician.getAge());
        addressTv.setText(beautician.getAddress());
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