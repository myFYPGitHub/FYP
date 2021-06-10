package com.fyp.Beauticianatyourdoorstep.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.helper.StringHelper;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.model.Beautician;
import com.fyp.Beauticianatyourdoorstep.model.Booking;
import com.fyp.Beauticianatyourdoorstep.model.BookingItem;
import com.fyp.Beauticianatyourdoorstep.view.customerModule.BookingDetailsActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class CustomerBookingAdapter extends RecyclerView.Adapter<CustomerBookingAdapter.ViewHolder> implements MyConstants {
    private final Activity activity;
    private final ArrayList<BookingItem> list;
    private static final Intent activity_opener = new Intent();
    private int lastPosition = -1;

    public CustomerBookingAdapter(Activity activity, ArrayList<BookingItem> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        Beautician beautician = list.get(pos).getBeauticianInstance();
        Booking booking = list.get(pos).getBookingInstance();
        String first_name = beautician.getFirstName();
        String last_name = beautician.getLastName();
        String fullName = first_name + " " + last_name;
        holder.service_name.setText(booking.getServiceName());
        String picUri = beautician.getProfilePicUri();
        if (picUri != null)
            Glide.with(activity).load(Uri.parse(picUri)).into(holder.profile_pic);
        holder.name.setText(StringHelper.capitalizeString(fullName));
        holder.contact.setText(beautician.getContact());
        holder.city.setText(StringHelper.capitalizeString(beautician.getCity()));
        holder.address.setText(beautician.getAddress());
        Integer total_rating = beautician.getTotalRating();
        Integer num_of_rating = beautician.getNumOfRating();
        int customer_rating = total_rating / (num_of_rating == 0 ? 1 : num_of_rating);
        holder.ratingBar.setRating(customer_rating);
        holder.order_date.setText("Date: " + booking.getRequestDate());
        String start_time = booking.getStartTime();
        String end_time = booking.getEndTime();
        String timing = "Timing: " + start_time + "-" + end_time;
        holder.order_timing.setText(timing);
        String status = booking.getBookingStatus();
        switch (status) {
            case ORDER_PROGRESS:
                holder.order_status.setBackgroundResource(R.color.colorProgress);
                break;
            case ORDER_CANCELLED:
                holder.order_status.setBackgroundResource(R.color.colorCancelled);
                break;
            case ORDER_COMPLETED:
                holder.order_status.setBackgroundResource(R.color.colorCompleted);
                break;
        }
        holder.order_status.setText(status);
        holder.booking_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternetConnectivity.isInternetConnected(activity)) {
                    try {
                        activity_opener.setClass(activity, BookingDetailsActivity.class);
                        activity_opener.putExtra(EXTRA_BOOKING_DETAILS, list.get(pos));
                        activity.startActivity(activity_opener);
                    } catch (Exception e) {
                    }
                } else {
                    Toast.makeText(activity, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                }
            }
        });
        setAnimation(holder.itemView, pos);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView service_name, name, address, contact, order_date, order_timing, order_status, city;
        RatingBar ratingBar;
        RoundedImageView profile_pic;
        CardView booking_request;

        public ViewHolder(@NonNull View view) {
            super(view);
            booking_request = view.findViewById(R.id.orderItem_request);
            service_name = view.findViewById(R.id.orderItem_service_name);
            name = view.findViewById(R.id.orderItem_cusName);
            address = view.findViewById(R.id.orderItem_cusAddress);
            contact = view.findViewById(R.id.orderItem_cusContact);
            order_date = view.findViewById(R.id.orderItem_date);
            city = view.findViewById(R.id.orderItem_cusCity);
            order_timing = view.findViewById(R.id.orderItem_timing);
            order_status = view.findViewById(R.id.orderItem_status);
            ratingBar = view.findViewById(R.id.orderItem_rating);
            profile_pic = view.findViewById(R.id.orderItem_cusDp);
        }
    }
}