package com.fyp.Beauticianatyourdoorstep.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.Beauticianatyourdoorstep.R;
import com.fyp.Beauticianatyourdoorstep.controller.ServerLogic;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.helper.StringHelper;
import com.fyp.Beauticianatyourdoorstep.internetchecking.CheckInternetConnectivity;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomInputDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomMsgDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomToast;
import com.google.android.material.snackbar.Snackbar;

public class AppSettingsActivity extends AppCompatActivity implements MyConstants {
    private Context context;
    private String newInput;
    private Class<?> cls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        context = AppSettingsActivity.this;
        Intent it = getIntent();
        cls = (Class<?>) it.getSerializableExtra(EXTRA_CLASS);
        ListView listView = findViewById(R.id.settings_list_view);
        final String[] array = getResources().getStringArray(R.array.app_settings_items);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.settings_item_design, array);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        changeFirstName();
                        break;
                    case 1:
                        changeLastName();
                        break;
                    case 2:
                        changeContact();
                        break;
                    case 3:
                        changePassword();
                        break;
                    case 4:
                        startActivity(new Intent(context, AppAboutActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void changeFirstName() {
        final CustomInputDialog customInputDialog = new CustomInputDialog(context, "Change First Name");
        customInputDialog.setInputHint("Enter first name")
                .setOkBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customInputDialog.dismissDialog();
                        newInput = customInputDialog.getInputText();
                        if (StringHelper.isEmpty(newInput)) {
                            new CustomMsgDialog(context, "Alert", "Can't Set Empty Name.");
                            return;
                        }
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            newInput = StringHelper.capitalizeString(newInput);
                            ServerLogic.changeFirstName(AppSettingsActivity.this, newInput);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void changeLastName() {
        final CustomInputDialog customInputDialog = new CustomInputDialog(context, "Change Last Name");
        customInputDialog.setInputHint("Enter last name")
                .setOkBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customInputDialog.dismissDialog();
                        newInput = customInputDialog.getInputText();
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            newInput = StringHelper.capitalizeString(newInput);
                            ServerLogic.changeLastName(AppSettingsActivity.this, newInput);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void changePassword() {
        final CustomInputDialog customInputDialog = new CustomInputDialog(context, "Change Password");
        customInputDialog.setInputHint("Enter a new Password")
                .setOkBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customInputDialog.dismissDialog();
                        newInput = customInputDialog.getInputText();
                        if (StringHelper.isEmpty(newInput)) {
                            new CustomMsgDialog(context, "Alert", "Can't Set Empty Password.");
                            return;
                        }
                        if (newInput.length() < MIN_PASSWORD_CHARS) {
                            CustomToast.makeToast(context, "Password must be " + MIN_PASSWORD_CHARS + " characters or longer", Toast.LENGTH_LONG);
                            return;
                        }
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                             newInput = StringHelper.toMD5String(newInput);
                            ServerLogic.changePassword(AppSettingsActivity.this, newInput);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void changeContact() {
        final CustomInputDialog customInputDialog = new CustomInputDialog(context, "Change Contact");
        customInputDialog.setInputType(InputType.TYPE_CLASS_PHONE)
                .setInputHint("Enter Phone Number")
                .setOkBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customInputDialog.dismissDialog();
                        newInput = customInputDialog.getInputText();
                        String MobilePattern = "[0-9]{11}";
                        if (!newInput.matches(MobilePattern)) {
                            Toast.makeText(context, "Please Enter valid Contact Number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            newInput = StringHelper.capitalizeString(newInput);
                            ServerLogic.changeContact(AppSettingsActivity.this, newInput);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, cls));
        finish();
    }
}