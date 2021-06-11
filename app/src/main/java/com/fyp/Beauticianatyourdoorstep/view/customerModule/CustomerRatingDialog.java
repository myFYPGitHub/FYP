package com.fyp.Beauticianatyourdoorstep.view.customerModule;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.fyp.Beauticianatyourdoorstep.R;

public class CustomerRatingDialog extends AlertDialog {
    private final Button rating_dialogSendBtn;
    private final AlertDialog alertDialog;
    private final EditText reviewEd;
    private Integer rating = 0;

    public CustomerRatingDialog(@NonNull Context context) {
        super(context);
        Builder builder = new Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.customer_rating_dialog, null);
        builder.setView(view);
        RatingBar ratingBar = view.findViewById(R.id.rating_dialogRatingBar);
        reviewEd = view.findViewById(R.id.rating_dialogReview);
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

    public String getReview() {
        return reviewEd.getText().toString();
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
