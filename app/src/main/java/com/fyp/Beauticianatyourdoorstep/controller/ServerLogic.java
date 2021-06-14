package com.fyp.Beauticianatyourdoorstep.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fyp.Beauticianatyourdoorstep.helper.LoginManagement;
import com.fyp.Beauticianatyourdoorstep.helper.MyConstants;
import com.fyp.Beauticianatyourdoorstep.helper.StringHelper;
import com.fyp.Beauticianatyourdoorstep.model.BeauticianService;
import com.fyp.Beauticianatyourdoorstep.model.Booking;
import com.fyp.Beauticianatyourdoorstep.model.DB;
import com.fyp.Beauticianatyourdoorstep.model.User;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomMsgDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomProgressDialog;
import com.fyp.Beauticianatyourdoorstep.uihelper.CustomToast;
import com.fyp.Beauticianatyourdoorstep.view.beauticianPanel.BeauticianDashboardActivity;
import com.fyp.Beauticianatyourdoorstep.view.customerPanel.CustomerDashboardActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;

public final class ServerLogic implements MyConstants {
    private static final DatabaseReference realtimeDatabaseReference;
    private static final StorageReference storageReference;
    private static String email_identifier;
    private static CustomProgressDialog progDialog;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    static {
        //This block initialize static variables
        realtimeDatabaseReference = DB.getRtDBRootNodeReference();
        storageReference = DB.getStorageReference();
    }

    public static void createNewUserAccount(final Activity activity, final User user) {
        progDialog = new CustomProgressDialog(activity, "Processing . . .");
        progDialog.showDialog();
        try {
            Query checkDuplicateAcc = realtimeDatabaseReference.child(NODE_USER).orderByChild(USER_EMAIL).equalTo(user.getEmail());
            email_identifier = StringHelper.removeInvalidCharsFromIdentifier(user.getEmail());
            checkDuplicateAcc.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //Account already exists
                        progDialog.dismissDialog();
                        Toast.makeText(activity, "Account already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            //New Account Created
                            realtimeDatabaseReference.child(NODE_USER).child(email_identifier).setValue(user);
                            new CustomMsgDialog(activity, "Congratulations", "New Account Created Successfully!\n\nNow you can Sign In.");
                            progDialog.dismissDialog();
                        } catch (Exception e) {
                            progDialog.dismissDialog();
                            System.out.println(e.toString());
                            CustomToast.makeToast(activity, "Can't create account at this time", Toast.LENGTH_SHORT);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void verifyLoginCredentials(final Activity activity, final String email, final String password) {
        progDialog = new CustomProgressDialog(activity, "Processing . . .");
        progDialog.showDialog();
        try {
            email_identifier = StringHelper.removeInvalidCharsFromIdentifier(email);
            DatabaseReference userRef = realtimeDatabaseReference.child(NODE_USER).child(email_identifier);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String passFromDB = dataSnapshot.child(USER_PASSWORD).getValue(String.class);
                        String categoryFromDB = dataSnapshot.child(USER_CATEGORY).getValue(String.class);
                        if (password.equals(passFromDB)) {
                            LoginManagement loginManagement = new LoginManagement(activity);
                            loginManagement.setLoginEmail(email);
                            loginManagement.setLoginCategory(categoryFromDB);
                            progDialog.dismissDialog();
                            if (categoryFromDB.toLowerCase().equals(USER_CATEGORY_BEAUTICIAN)) {
                                activity.startActivity(new Intent(activity, BeauticianDashboardActivity.class));
                            } else {
                                activity.startActivity(new Intent(activity, CustomerDashboardActivity.class));
                            }
                            activity.finish();
                        } else {
                            progDialog.dismissDialog();
                            Toast.makeText(activity, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progDialog.dismissDialog();
                        Toast.makeText(activity, "Incorrect Email", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    /*public static void sendNewPasswordToCorrectEmail(final Activity activity, final String recipient_email) {
        progDialog = new CustomProgressDialog(activity, "Checking your email in database . . .");
        progDialog.showDialog();
        try {
            Query checkExistingAcc = realtimeDatabaseReference.orderByChild(USER_KEY_EMAIL).equalTo(recipient_email);
            checkExistingAcc.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        progDialog.dismissDialog();
                        final String new_key = String.valueOf(1316548 + new SecureRandom().nextInt(3216546));
                        email_identifier = StringOperations.removeInvalidCharsFromIdentifier(recipient_email);
                        Task<Void> task = realtimeDatabaseReference.child(email_identifier).child(USER_KEY_PASSWORD).setValue(StringOperations.toMD5String(new_key));
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MailService.sendMessage(activity, recipient_email, new_key);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progDialog.dismissDialog();
                                CustomToast.makeToast(activity, "Can't change your password at this time!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        progDialog.dismissDialog();
                        new CustomMsgDialog(activity, "WRONG EMAIL!", "Email you entered is not associated with any account in our database.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }*/

    public static void uploadProfilePic(final Activity activity, final Uri pic_uri) {
        try {
            progDialog = new CustomProgressDialog(activity, "It may take a while please wait . . .");
            progDialog.showDialog();
            final String file_storage_id = System.currentTimeMillis() + "";
            email_identifier = new LoginManagement(activity).getEmailIdentifier();
            DatabaseReference dbRef = realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_PROFILE_PIC_ID);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Task<Void> task = storageReference.child(snapshot.getValue(String.class)).delete();
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                final UploadTask uploadTask = storageReference.child(file_storage_id).putFile(pic_uri);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(final Uri uri) {
                                                realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_PROFILE_PIC_ID).setValue(file_storage_id);
                                                realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_PROFILE_PIC_URI).setValue(uri.toString());
                                                Toast.makeText(activity, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                progDialog.dismissDialog();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(activity, "Update Failed", Toast.LENGTH_LONG).show();
                                        progDialog.dismissDialog();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progDialog.dismissDialog();
                                Toast.makeText(activity, "Profile Picture Update Failed\nError:" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        final UploadTask uploadTask = storageReference.child(file_storage_id).putFile(pic_uri);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_PROFILE_PIC_ID).setValue(file_storage_id);
                                        realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_PROFILE_PIC_URI).setValue(uri.toString());
                                        Toast.makeText(activity, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                        progDialog.dismissDialog();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, "Profile Picture Update Failed", Toast.LENGTH_LONG).show();
                                progDialog.dismissDialog();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error: " + e, Toast.LENGTH_LONG);
        }
    }

    public static void deleteProfilePic(final Context context, final String profilePicStorageId) {
        try {
            progDialog = new CustomProgressDialog(context, "It may take a while please wait . . .");
            progDialog.showDialog();
            final String file_storage_id = System.currentTimeMillis() + "";
            email_identifier = new LoginManagement(context).getEmailIdentifier();
            DatabaseReference dbRef = realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_PROFILE_PIC_ID);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Task<Void> task = storageReference.child(profilePicStorageId).delete();
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_PROFILE_PIC_ID).removeValue();
                                realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_PROFILE_PIC_URI).removeValue();
                                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                progDialog.dismissDialog();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progDialog.dismissDialog();
                                Toast.makeText(context, "Profile Picture Deletion Failed\nError:" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(context, "Error: " + e, Toast.LENGTH_LONG);
        }
    }

    public static void addBeauticianService(final Context context, final BeauticianService beauticianService, final Integer index) {
        progDialog = new CustomProgressDialog(context, "Processing . . .");
        progDialog.showDialog();
        try {
            email_identifier = new LoginManagement(context).getEmailIdentifier();
            Query checkAccount = realtimeDatabaseReference.child(NODE_USER).child(email_identifier);

            checkAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String serviceId = String.valueOf(index);
                        realtimeDatabaseReference.child(NODE_USER).child(email_identifier)
                                .child(NODE_BEAUTICIAN_SERVICES).child(serviceId).setValue(beauticianService);
                        CustomToast.makeToast(context, beauticianService.getServiceName() + " Service Added", Toast.LENGTH_SHORT);
                    }
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

    public static void removeBeauticianService(final Context context, final EditText servicePriceEd, final Integer index) {
        progDialog = new CustomProgressDialog(context, "Processing . . .");
        progDialog.showDialog();
        try {
            email_identifier = new LoginManagement(context).getEmailIdentifier();
            Query checkAccount = realtimeDatabaseReference.child(NODE_USER).child(email_identifier);

            checkAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String serviceId = String.valueOf(index);
                        realtimeDatabaseReference.child(NODE_USER).child(email_identifier)
                                .child(NODE_BEAUTICIAN_SERVICES).child(serviceId).setValue(null);
                        servicePriceEd.setText("");
                        CustomToast.makeToast(context, "Service Removed", Toast.LENGTH_LONG);
                    }
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

    public static void requestBooking(final Context context, final Booking booking) {
        progDialog = new CustomProgressDialog(context, "Sending Booking Request . . .");
        progDialog.showDialog();
        try {
            realtimeDatabaseReference.child(NODE_BOOKING).child(booking.getBookingId()).setValue(booking)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Booking Request Sent", Toast.LENGTH_SHORT).show();
                            progDialog.dismissDialog();
                        }
                    });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(context, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    ////////////////////////////////    SETTINGS LOGIC    ////////////////////////////////////////////////////
    public static void changeFirstName(final Activity activity, final String newFirstName) {
        progDialog = new CustomProgressDialog(activity, "Changing First Name . . .");
        progDialog.showDialog();
        try {
            email_identifier = new LoginManagement(activity).getEmailIdentifier();
            Task<Void> task = realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_FIRST_NAME).setValue(newFirstName);
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "First Name Changed Successfully", Toast.LENGTH_LONG);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Changing first name failed!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void changeLastName(final Activity activity, final String newLastName) {
        progDialog = new CustomProgressDialog(activity, "Changing Last Name . . .");
        progDialog.showDialog();
        try {
            email_identifier = new LoginManagement(activity).getEmailIdentifier();
            Task<Void> task = realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_LAST_NAME).setValue(newLastName);
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Last Name Changed Successfully", Toast.LENGTH_LONG);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Changing last name failed!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void changeContact(final Activity activity, final String newContact) {
        progDialog = new CustomProgressDialog(activity, "Changing Contact . . .");
        progDialog.showDialog();
        try {
            email_identifier = new LoginManagement(activity).getEmailIdentifier();
            Task<Void> task = realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_CONTACT).setValue(newContact);
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Contact Changed Successfully", Toast.LENGTH_LONG);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Changing contact failed!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void changePassword(final Activity activity, final String newPassword) {
        progDialog = new CustomProgressDialog(activity, "Changing Password . . .");
        progDialog.showDialog();
        try {
            email_identifier = new LoginManagement(activity).getEmailIdentifier();
            Task<Void> task = realtimeDatabaseReference.child(NODE_USER).child(email_identifier).child(USER_PASSWORD).setValue(newPassword);
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Password Changed Successfully", Toast.LENGTH_LONG);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Changing password failed!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }
    ////////////////////////////////    SETTINGS LOGIC   ////////////////////////////////////////////////////
}
