package com.fyp.Beauticianatyourdoorstep.view.beauticianModule;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.controller.ServerLogic;
import com.fyp.Beauticianatyourdoorstep.helper.LoginManagement;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.model.BeauticianService;
import com.fyp.Beauticianatyourdoorstep.model.DB;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomProgressDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomToast;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HairServicesFragment extends Fragment {
    private final int fieldsLength = 3;
    private final TextView[] serviceNameTv = new TextView[fieldsLength];
    private final EditText[] servicePriceEd = new EditText[fieldsLength];
    private final SwitchMaterial[] serviceSwitch = new SwitchMaterial[fieldsLength];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hair_services, container, false);
        serviceNameTv[0] = rootView.findViewById(R.id.service1Name);
        serviceNameTv[1] = rootView.findViewById(R.id.service2Name);
        serviceNameTv[2] = rootView.findViewById(R.id.service3Name);
        servicePriceEd[0] = rootView.findViewById(R.id.service1Price);
        servicePriceEd[1] = rootView.findViewById(R.id.service2Price);
        servicePriceEd[2] = rootView.findViewById(R.id.service3Price);
        serviceSwitch[0] = rootView.findViewById(R.id.service1Switch);
        serviceSwitch[1] = rootView.findViewById(R.id.service2Switch);
        serviceSwitch[2] = rootView.findViewById(R.id.service3Switch);

        if (CheckInternetConnectivity.isInternetConnected(getActivity())) {
            checkBeauticianService(getActivity());
        } else {
            Toast.makeText(getActivity(), MyConstants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    private void checkBeauticianService(final Context context) {
        DatabaseReference realtimeDatabaseReference = DB.getRtDBRootNodeReference();
        CustomProgressDialog progDialog = new CustomProgressDialog(context, "Processing . . .");
        progDialog.showDialog();
        try {
            String email_identifier = new LoginManagement(context).getEmailIdentifier();
            Query checkAccount = realtimeDatabaseReference.child(MyConstants.NODE_USER).child(email_identifier)
                    .child(MyConstants.NODE_BEAUTICIAN_SERVICES);
            checkAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String key = ds.getKey();
                            int i = Integer.parseInt(key);
                            Double price = ds.child("servicePrice").getValue(Double.class);
                            servicePriceEd[i].setText(String.valueOf(price));
                            Boolean isChecked = ds.child("serviceAvailability").getValue(Boolean.class);
                            serviceSwitch[i].setChecked(isChecked);
                            serviceSwitch[i].setText("Enable");
                        }
                    }
                    service(0);
                    service(1);
                    service(2);
                    progDialog.dismissDialog();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(context, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void service(int index) {
        serviceSwitch[index].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String serviceName = serviceNameTv[index].getText().toString();
                if (isChecked) {
                    String service1PriceStr = servicePriceEd[index].getText().toString();
                    Double servicePrice = service1PriceStr.length() > 0 ? Double.parseDouble(service1PriceStr) : 0;
                    ServerLogic.addBeauticianService(getActivity(), new BeauticianService(serviceName, servicePrice, isChecked), index);
                    serviceSwitch[index].setText("Enable");
                } else {
                    ServerLogic.removeBeauticianService(getActivity(), servicePriceEd[index], index);
                    serviceSwitch[index].setText("Disable");
                }
            }
        });
    }
}