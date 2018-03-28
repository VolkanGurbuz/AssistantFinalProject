package com.volkangurbuz.intro;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by VolkanGurbuz on 11/19/2017.
 */

public class FireBaseOperations {

    private FirebaseDatabase database ;
    private DatabaseReference myRef ;

    public FireBaseOperations() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users");
    }


    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    public DatabaseReference getMyRef() {
        return myRef;
    }

    public void setMyRef(DatabaseReference myRef) {
        this.myRef = myRef;
    }
}
