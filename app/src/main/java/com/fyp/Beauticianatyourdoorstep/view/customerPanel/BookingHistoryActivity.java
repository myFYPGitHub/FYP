package com.fyp.Beauticianatyourdoorstep.view.customerPanel;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.adapters.CustomerBookingAdapter;
import com.fyp.Beauticianatyourdoorstep.helper.LoginManagement;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.helper.StringHelper;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.model.Beautician;
import com.fyp.Beauticianatyourdoorstep.model.Booking;
import com.fyp.Beauticianatyourdoorstep.model.BookingItem;
import com.fyp.Beauticianatyourdoorstep.model.DB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookingHistoryActivity extends AppCompatActivity implements MyConstants {
    private CustomerBookingAdapter adapter;
    private final ArrayList<BookingItem> list = new ArrayList<>();
    private static DatabaseReference parent_node, beauticianRef;
    private Context context;
    private LinearLayout cusBookingHistoryRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        context = BookingHistoryActivity.this;
        cusBookingHistoryRoot = findViewById(R.id.cusBookingHistoryRoot);
        RecyclerView recyclerView = findViewById(R.id.cusBookingHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        parent_node = DB.getRtDBRootNodeReference();
        adapter = new CustomerBookingAdapter(BookingHistoryActivity.this, list);
        recyclerView.setAdapter(adapter);
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        adapter.notifyDataSetChanged();
        cusBookingHistoryRoot.setBackgroundResource(R.drawable.no_data_found);
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            try {
                String myEmail = new LoginManagement(context).getLoginEmail();
                Query checkMyBooking = parent_node.child(NODE_BOOKING).orderByChild(BOOKING_CUSTOMER_EMAIL).equalTo(myEmail);
                checkMyBooking.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot orderData : snapshot.getChildren()) {
                                String booking_status = orderData.child(BOOKING_STATUS).getValue(String.class);
                                if (!(booking_status.equals(ORDER_COMPLETED) || booking_status.equals(ORDER_CANCELLED))) {
                                    continue;
                                }
                                String beauticianId = orderData.child(BOOKING_BEAUTICIAN_EMAIL).getValue(String.class);
                                beauticianId = StringHelper.removeInvalidCharsFromIdentifier(beauticianId);
                                beauticianRef = parent_node.child(NODE_USER).child(beauticianId);
                                beauticianRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            Booking booking = orderData.getValue(Booking.class);
                                            Beautician beautician = snapshot.getValue(Beautician.class);
                                            list.add(new BookingItem(beautician, booking));
                                            cusBookingHistoryRoot.setBackgroundResource(R.color.colorWhite);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }
}