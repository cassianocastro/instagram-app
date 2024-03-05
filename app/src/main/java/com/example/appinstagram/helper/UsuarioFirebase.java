package com.example.appinstagram.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.appinstagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 *
 */
public class UsuarioFirebase
{

    static public FirebaseUser getCurrentUser()
    {
        FirebaseAuth usuario = ConfigFireBase.getFireBaseAuth();

        return usuario.getCurrentUser();
    }

    static public String getID()
    {
        return getCurrentUser().getUid();
    }

    static public void atualizarNomeUsuario(String nome)
    {
        try
        {
            FirebaseUser user = getCurrentUser();

            UserProfileChangeRequest profile = new UserProfileChangeRequest
                .Builder()
                .setDisplayName(nome)
                .build();

            user
                .updateProfile(profile)
                .addOnCompleteListener(
                    new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if ( ! task.isSuccessful() )
                            {
                                Log.d("Perfil", "Erro ao atualizar nome de Perfil.");
                            }
                        }
                    }
                );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    static public void atualizarFotoUsuario(Uri url)
    {
        try
        {
            FirebaseUser user = getCurrentUser();

            UserProfileChangeRequest profile = new UserProfileChangeRequest
                .Builder()
                .setPhotoUri(url)
                .build();

            user
                .updateProfile(profile)
                .addOnCompleteListener(
                    new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if ( ! task.isSuccessful() )
                            {
                                Log.d("Perfil", "Erro ao atualizar foto de Perfil.");
                            }
                        }
                    }
                );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    static public Usuario getDadosUserLogged()
    {
        FirebaseUser firebaseUser = getCurrentUser();

        Usuario usuario = new Usuario();

        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setId(firebaseUser.getUid());

        if ( firebaseUser.getPhotoUrl() == null )
        {
            usuario.setCaminhoFoto("");
        }
        else
        {
            usuario.setCaminhoFoto(firebaseUser.getPhotoUrl().toString());
        }

        return usuario;
    }
}