package com.fyp.Beauticianatyourdoorstep.view.beauticianPanel;

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
import com.fyp.Beauticianatyourdoorstep.adapter.BeauticianOrdersAdapter;
import com.fyp.Beauticianatyourdoorstep.helper.LoginManagement;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.helper.StringHelper;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.model.Booking;
import com.fyp.Beauticianatyourdoorstep.model.DB;
import com.fyp.Beauticianatyourdoorstep.model.OrderItem;
import com.fyp.Beauticianatyourdoorstep.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BeauticianOrderHistoryActivity extends AppCompatActivity implements MyConstants {
    private BeauticianOrdersAdapter adapter;
    private final ArrayList<OrderItem> list = new ArrayList<>();
    private static DatabaseReference parent_node, customerRef;
    private Context context;
    private LinearLayout beauticianHistoryRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beautician_order_history);
        context = BeauticianOrderHistoryActivity.this;
        beauticianHistoryRoot = findViewById(R.id.beauticianHistoryRoot);
        RecyclerView recyclerView = findViewById(R.id.beautician_history_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        parent_node = DB.getRtDBRootNodeReference();
        adapter = new BeauticianOrdersAdapter(BeauticianOrderHistoryActivity.this, list);
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
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            try {
                String myEmail = new LoginManagement(context).getLoginEmail();
                Query checkMyBooking = parent_node.child(NODE_BOOKING).orderByChild(BOOKING_BEAUTICIAN_EMAIL).equalTo(myEmail);
                checkMyBooking.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot orderData : snapshot.getChildren()) {
                                String booking_status = orderData.child(BOOKING_STATUS).getValue(String.class);
                                if (!(booking_status.equals(ORDER_COMPLETED) || booking_status.equals(ORDER_CANCELLED))) {
                                    continue;
                                }
                                String customerId = orderData.child(BOOKING_CUSTOMER_EMAIL).getValue(String.class);
                                customerId = StringHelper.removeInvalidCharsFromIdentifier(customerId);
                                customerRef = parent_node.child(NODE_USER).child(customerId);
                                customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            Booking booking = orderData.getValue(Booking.class);
                                            User customer = snapshot.getValue(User.class);
                                            list.add(new OrderItem(customer, booking));
                                            beauticianHistoryRoot.setBackgroundResource(R.color.colorWhite);
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