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
import com.fyp.Beauticianatyourdoorstep.model.BeauticianItem;
import com.fyp.Beauticianatyourdoorstep.view.customerModule.BeauticianDetailsActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class BeauticianSearchAdapter extends RecyclerView.Adapter<BeauticianSearchAdapter.ViewHolder> implements MyConstants {
    private final Activity activity;
    private final ArrayList<BeauticianItem> list;
    private static final Intent activity_opener = new Intent();
    private int lastPosition = -1;

    public BeauticianSearchAdapter(Activity activity, ArrayList<BeauticianItem> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public BeauticianSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beautician_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeauticianSearchAdapter.ViewHolder holder, int pos) {
        Beautician beautician = list.get(pos).getBeauticianInstance();
        String first_name = beautician.getFirstName();
        String last_name = beautician.getLastName();
        String fullName = first_name + " " + last_name;
        String picUri = beautician.getProfilePicUri();
        if (picUri != null)
            Glide.with(activity).load(Uri.parse(picUri)).into(holder.profile_pic);
        holder.beauticianName.setText(StringHelper.capitalizeString(fullName));
        holder.beauticianContact.setText(beautician.getContact());
        holder.beauticianCity.setText(StringHelper.capitalizeString(beautician.getCity()));
        holder.beauticianAddress.setText(beautician.getAddress());
        Integer total_rating = beautician.getTotalRating();
        Integer num_of_rating = beautician.getNumOfRating();
        int customer_rating = total_rating / (num_of_rating == 0 ? 1 : num_of_rating);
        holder.beauticianRating.setRating(customer_rating);
        holder.beauticianItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternetConnectivity.isInternetConnected(activity)) {
                    try {
                        activity_opener.setClass(activity, BeauticianDetailsActivity.class);
                        activity_opener.putExtra(EXTRA_BEAUTICIAN_DETAILS, list.get(pos));
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView beauticianName, beauticianAddress, beauticianContact, beauticianCity;
        RatingBar beauticianRating;
        RoundedImageView profile_pic;
        CardView beauticianItem;

        public ViewHolder(@NonNull View view) {
            super(view);
            beauticianItem = view.findViewById(R.id.beauticianItem);
            beauticianName = view.findViewById(R.id.beauticianName);
            beauticianAddress = view.findViewById(R.id.beauticianAddress);
            beauticianContact = view.findViewById(R.id.beauticianContact);
            beauticianCity = view.findViewById(R.id.beauticianCity);
            beauticianRating = view.findViewById(R.id.beauticianRating);
            profile_pic = view.findViewById(R.id.beauticianDp);
        }
    }
}
