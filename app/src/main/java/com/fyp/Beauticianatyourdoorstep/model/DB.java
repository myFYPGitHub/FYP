package com.fyp.Beauticianatyourdoorstep.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Singleton Class for Database Connectivity
 */

public final class DB {
    private static FirebaseDatabase database;
    private static FirebaseStorage storage;

    private DB() {
        //Constructor Locked
    }

    public static DatabaseReference getRtDBRootNodeReference() {
        //Firebase Realtime database reference
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        return database.getReference("DB");
    }

    public static StorageReference getStorageReference() {
        //Firebase Storage reference
        if (storage == null) {
            storage = FirebaseStorage.getInstance();
        }
        return storage.getReference("ProfilePics");
    }
}
