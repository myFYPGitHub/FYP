package com.fyp.Beauticianatyourdoorstep.view.beauticianModule;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.fyp.Beauticianatyourdoorstep.R;

public class BeauticianRatingDialog extends AlertDialog {
    private final Button rating_dialogSendBtn;
    private final AlertDialog alertDialog;
    private Integer rating = 0;

    public BeauticianRatingDialog(@NonNull Context context) {
        super(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.beautician_rating_dialog, null);
        builder.setView(view);
        RatingBar ratingBar = view.findViewById(R.id.rating_dialogRatingBar);
        rating_dialogSendBtn = view.findViewById(R.id.rating_dialogSendBtn);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rate, boolean b) {
                rating = (int) rate;
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public Integer getRating() {
        return rating;
    }

    public void setSendBtnListener(View.OnClickListener listener) {
        rating_dialogSendBtn.setOnClickListener(listener);
    }

    public void dismissDialog() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        });
    }
}
