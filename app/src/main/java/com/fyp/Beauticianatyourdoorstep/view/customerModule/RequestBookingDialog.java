package com.fyp.Beauticianatyourdoorstep.view.customerModule;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.fyp.Beauticianatyourdoorstep.R;

public final class RequestBookingDialog extends AlertDialog {
    private final Button sendRequestBtn;
    private final AlertDialog alertDialog;
    private final EditText booking_detailsEd;
    private final Spinner startTimeHour, startTimeDayNight, endTimeHour, endTimeDayNight;
    private String date = "";

    public RequestBookingDialog(@NonNull Context context) {
        super(context);
        Builder builder = new Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.request_booking_dialog, null);
        builder.setView(view);
        booking_detailsEd = view.findViewById(R.id.booking_details);
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        startTimeHour = view.findViewById(R.id.startTimeHour);
        startTimeDayNight = view.findViewById(R.id.startTimeDayNight);
        endTimeHour = view.findViewById(R.id.endTimeHour);
        endTimeDayNight = view.findViewById(R.id.endTimeDayNight);
        sendRequestBtn = view.findViewById(R.id.sendRequestBtn);
        if (calendarView != null) {
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    date = dayOfMonth + "-" + (month + 1) + "-" + year;
                }
            });
        }
        alertDialog = builder.create();
        alertDialog.show();
    }

    public String getBookingDetails() {
        return booking_detailsEd.getText().toString();
    }

    public String getSelectedDate() {
        return date;
    }

    public String getSelectedStartTime() {
        return startTimeHour.getSelectedItem().toString() + startTimeDayNight.getSelectedItem().toString().toLowerCase();
    }

    public String getSelectedEndTime() {
        return endTimeHour.getSelectedItem().toString() + endTimeDayNight.getSelectedItem().toString().toLowerCase();
    }

    public void setSendRequestBtnListener(View.OnClickListener listener) {
        sendRequestBtn.setOnClickListener(listener);
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
