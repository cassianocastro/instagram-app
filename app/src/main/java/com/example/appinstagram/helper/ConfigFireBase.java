package com.example.appinstagram.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 *
 */
public class ConfigFireBase
{

    private static DatabaseReference reference;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    public static FirebaseAuth getFireBaseAuth()
    {
        if ( auth == null )
        {
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }

    public static DatabaseReference getFireBaseDataBase()
    {
        if ( reference == null )
        {
            reference = FirebaseDatabase.getInstance().getReference();
        }

        return reference;
    }

    public static StorageReference getStorage()
    {
        if ( storage == null )
        {
            storage = FirebaseStorage.getInstance().getReference();
        }

        return storage;
    }
}