package com.fyp.Beauticianatyourdoorstep.view.beauticianPanel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;

public class SpecializationActivity extends AppCompatActivity implements MyConstants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialization);
        Intent it = getIntent();
        TextView current_specialization = findViewById(R.id.specialization);
        String specialization = it.getStringExtra(EXTRA_BEAUTICIAN_SPECIALIZATION);
        current_specialization.setText(specialization);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setReorderingAllowed(true);
        switch (specialization) {
            case "Hair Specialist":
                ft.replace(R.id.services_fragment, new HairServicesFragment()).commit();
                break;
            case "Nails Specialist":
                ft.replace(R.id.services_fragment, new NailsServicesFragment()).commit();
                break;
            case "Makeup Specialist":
                ft.replace(R.id.services_fragment, new MakeupServicesFragment()).commit();
                break;
            case "Waxing Specialist":
                ft.replace(R.id.services_fragment, new WaxingServicesFragment()).commit();
                break;
            case "Massage Specialist":
                ft.replace(R.id.services_fragment, new MassageServicesFragment()).commit();
                break;
            case "Facial Specialist":
                ft.replace(R.id.services_fragment, new FacialServicesFragment()).commit();
                break;
        }
    }
}