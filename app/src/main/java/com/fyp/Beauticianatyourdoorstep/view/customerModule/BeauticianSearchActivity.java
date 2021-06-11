package com.fyp.Beauticianatyourdoorstep.view.customerModule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.adapter.BeauticianSearchAdapter;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.model.Beautician;
import com.fyp.Beauticianatyourdoorstep.model.BeauticianItem;
import com.fyp.Beauticianatyourdoorstep.model.BeauticianService;
import com.fyp.Beauticianatyourdoorstep.model.DB;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomProgressDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BeauticianSearchActivity extends AppCompatActivity implements MyConstants {
    private BeauticianSearchAdapter adapter;
    private final ArrayList<BeauticianItem> list = new ArrayList<>();
    private static DatabaseReference parent_node;
    private Context context;
    private LinearLayout beauticianListRoot;
    private String specialization;
    private String customerCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beautician_search);
        context = BeauticianSearchActivity.this;
        Intent it = getIntent();
        customerCity = it.getStringExtra(EXTRA_CUSTOMER_CITY);
        specialization = it.getStringExtra(EXTRA_BEAUTICIAN_SPECIALIZATION);
        SearchView searchView = findViewById(R.id.beautician_searchView);
        beauticianListRoot = findViewById(R.id.beauticianListRoot);
        RecyclerView recyclerView = findViewById(R.id.beautician_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        parent_node = DB.getRtDBRootNodeReference();
        adapter = new BeauticianSearchAdapter(BeauticianSearchActivity.this, list);
        recyclerView.setAdapter(adapter);
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBeautician(query.toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchBeautician(newText.toLowerCase());
                return false;
            }
        });
    }

    private void searchBeautician(String toSearch) {
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            try {
                Query checkBeautician = parent_node.child(NODE_USER).orderByChild(USER_CATEGORY).equalTo(USER_CATEGORY_BEAUTICIAN);
                checkBeautician.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot b_data : snapshot.getChildren()) {
                                ArrayList<BeauticianService> services = new ArrayList<>();
                                Beautician beautician = b_data.getValue(Beautician.class);
                                if (!(beautician.getAvailability() && beautician.getSpecialization().equals(specialization)
                                        && beautician.getCity().equals(customerCity)
                                        && (beautician.getFirstName() + " " + beautician.getLastName()).toLowerCase().contains(toSearch))) {
                                    continue;
                                }
                                for (DataSnapshot serviceObject : b_data.child(NODE_BEAUTICIAN_SERVICES).getChildren()) {
                                    services.add(serviceObject.getValue(BeauticianService.class));
                                }
                                list.add(new BeauticianItem(beautician, services));
                                beauticianListRoot.setBackgroundResource(R.color.colorWhite);
                            }
                        }
                        adapter.notifyDataSetChanged();
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

    @Override
    protected void onStart() {
        super.onStart();
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            final CustomProgressDialog progDialog = new CustomProgressDialog(context, "Getting data . . .");
            progDialog.showDialog();
            try {
                Query checkBeautician = parent_node.child(NODE_USER).orderByChild(USER_CATEGORY).equalTo(USER_CATEGORY_BEAUTICIAN);
                checkBeautician.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot b_data : snapshot.getChildren()) {
                                ArrayList<BeauticianService> services = new ArrayList<>();
                                Beautician beautician = b_data.getValue(Beautician.class);
                                if (!(beautician.getAvailability() && beautician.getSpecialization().equals(specialization) && beautician.getCity().equals(customerCity))) {
                                    continue;
                                }
                                for (DataSnapshot serviceObject : b_data.child(NODE_BEAUTICIAN_SERVICES).getChildren()) {
                                    services.add(serviceObject.getValue(BeauticianService.class));
                                }
                                list.add(new BeauticianItem(beautician, services));
                                beauticianListRoot.setBackgroundResource(R.color.colorWhite);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        progDialog.dismissDialog();
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