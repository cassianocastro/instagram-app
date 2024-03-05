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

    static private DatabaseReference reference;
    static private FirebaseAuth auth;
    static private StorageReference storage;

    static public FirebaseAuth getFireBaseAuth()
    {
        if ( auth == null )
        {
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }

    static public DatabaseReference getFireBaseDataBase()
    {
        if ( reference == null )
        {
            reference = FirebaseDatabase.getInstance().getReference();
        }

        return reference;
    }

    static public StorageReference getStorage()
    {
        if ( storage == null )
        {
            storage = FirebaseStorage.getInstance().getReference();
        }

        return storage;
    }
}