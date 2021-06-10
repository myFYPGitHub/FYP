package com.fyp.Beauticianatyourdoorstep.view.customerModule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;

public class ServicesActivity extends AppCompatActivity implements MyConstants {
    private String[] specializations;
    private String customerCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_services);
        Intent it = getIntent();
        customerCity = it.getStringExtra(EXTRA_CUSTOMER_CITY);
        specializations = getResources().getStringArray(R.array.beautician_specialization);
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void showBeautician(View view) {
        Intent it = new Intent();
        String specialist = "";
        switch (view.getId()) {
            case R.id.hairSpecialistBtn:
                specialist = specializations[1];
                break;
            case R.id.massageSpecialistBtn:
                specialist = specializations[2];
                break;
            case R.id.nailsSpecialistBtn:
                specialist = specializations[3];
                break;
            case R.id.makeupSpecialistBtn:
                specialist = specializations[4];
                break;
            case R.id.waxingSpecialistBtn:
                specialist = specializations[5];
                break;
            case R.id.facialSpecialistBtn:
                specialist = specializations[6];
        }
        it.setClass(ServicesActivity.this, BeauticianSearchActivity.class);
        it.putExtra(EXTRA_CUSTOMER_CITY, customerCity);
        it.putExtra(EXTRA_BEAUTICIAN_SPECIALIZATION, specialist);
        startActivity(it);
    }
}