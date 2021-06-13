package com.fyp.Beauticianatyourdoorstep.view.customerPanel;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.model.BookingItem;

public class BookingDetailsActivity extends AppCompatActivity implements MyConstants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        Intent it = getIntent();
        BookingItem bookingItem = (BookingItem) it.getSerializableExtra(EXTRA_BOOKING_DETAILS);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setReorderingAllowed(true);
        switch (bookingItem.getBookingInstance().getBookingStatus()) {
            case ORDER_PENDING:
            case ORDER_PROGRESS:
                ft.replace(R.id.booking_details_fragment, new BookingDetailsFragment()).commit();
                break;
            case ORDER_CANCELLED:
            case ORDER_COMPLETED:
                ft.replace(R.id.booking_details_fragment, new BookingHistoryDetailsFragment()).commit();
                break;
        }
    }
}