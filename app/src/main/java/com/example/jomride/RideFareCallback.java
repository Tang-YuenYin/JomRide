package com.example.jomride;

import com.google.firebase.database.DatabaseError;

public interface RideFareCallback {
    void onRideFareRetrieved(Double rideFare);

    void onCancelled(DatabaseError error);
}
