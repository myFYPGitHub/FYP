package com.fyp.Beauticianatyourdoorstep.view.beauticianModule;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.model.OrderItem;

public class OrdersDetailsActivity extends AppCompatActivity implements MyConstants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_details);
        Intent it = getIntent();
        OrderItem orderItem = (OrderItem) it.getSerializableExtra(EXTRA_BOOKING_DETAILS);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setReorderingAllowed(true);
        switch (orderItem.getBookingInstance().getBookingStatus()) {
            case ORDER_PENDING:
            case ORDER_PROGRESS:
                ft.replace(R.id.order_details_fragment, new OrderDetailsFragment()).commit();
                break;
            case ORDER_CANCELLED:
            case ORDER_COMPLETED:
                ft.replace(R.id.order_details_fragment, new OrderHistoryDetailsFragment()).commit();
                break;
        }
    }
}