package com.example.appinstagram.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appinstagram.R;
import com.example.appinstagram.helper.ConfigFireBase;
import com.example.appinstagram.helper.Permissao;
import com.example.appinstagram.helper.UsuarioFirebase;
import com.example.appinstagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 *
 */
public class EditarPerfilActivity extends AppCompatActivity
{

    static private final int SELECAO_GALERIA = 200;

    private CircleImageView imageEditarPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;
    private Usuario userLoggado;
    private StorageReference storageRef;
    private String identificadorUsuario;

    private String[] permissoes_necessarias = new String[]
    {
        Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editar_perfil);

        Permissao.validarPermissoes(permissoes_necessarias, this, 1);

        userLoggado          = UsuarioFirebase.getDadosUserLogged();
        storageRef           = ConfigFireBase.getStorage();
        identificadorUsuario = UsuarioFirebase.getID();

        Toolbar t = findViewById(R.id.toolbarPrincipal);

        t.setTitle("Editar Perfil");

        setSupportActionBar(t);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        imageEditarPerfil      = findViewById(R.id.imageEditarPerfil);
        textAlterarFoto        = findViewById(R.id.textAlterarFoto);
        editNomePerfil         = findViewById(R.id.editNomePerfil);
        editEmailPerfil        = findViewById(R.id.editEmailPerfil);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);

        editEmailPerfil.setFocusable(false);

        FirebaseUser usuarioPerfil = UsuarioFirebase.getCurrentUser();

        editNomePerfil.setText(usuarioPerfil.getDisplayName().toUpperCase());
        editEmailPerfil.setText(usuarioPerfil.getEmail());

        Uri url = usuarioPerfil.getPhotoUrl();

        if ( url != null )
        {
            Glide
                .with(EditarPerfilActivity.this)
                .load(url)
                .into(imageEditarPerfil);
        }
        else
            imageEditarPerfil.setImageResource(R.drawable.avatar);

        buttonSalvarAlteracoes.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String nomeAtualizado = editNomePerfil.getText().toString();

                    UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);
                    userLoggado.setNome(nomeAtualizado);
                    userLoggado.atualizar();

                    Toast.makeText(EditarPerfilActivity.this, "Dados atualizados.", Toast.LENGTH_SHORT).show();
                }
            }
        );

        textAlterarFoto.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    if ( i.resolveActivity(getPackageManager()) != null )
                    {
                        startActivityForResult(i, SELECAO_GALERIA);
                    }
                }
            }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK )
        {
            Bitmap img = null;

            try
            {
                switch ( requestCode )
                {
                    case SELECAO_GALERIA:
                        Uri local = data.getData();
                        img = MediaStore.Images.Media.getBitmap(getContentResolver(), local);
                        break;
                }

                if ( img != null )
                {
                    imageEditarPerfil.setImageBitmap(img);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    img.compress(Bitmap.CompressFormat.JPEG,70, baos);

                    byte[] dadosImg = baos.toByteArray();

                    StorageReference imgRef = storageRef
                        .child("imagens")
                        .child("perfil")
                        .child( identificadorUsuario + ".jpeg");

                    UploadTask uploadTask = imgRef.putBytes(dadosImg);

                    uploadTask.addOnFailureListener(
                        new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toast
                                    .makeText(
                                        EditarPerfilActivity.this,
                                        "Erro ao fazer upload da Imagem.",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show();
                            }
                        }
                    ).addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>()
                        {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                imgRef.getDownloadUrl().addOnCompleteListener(
                                    new OnCompleteListener<Uri>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task)
                                        {
                                            Uri url = task.getResult();

                                            atualizarFotoUsuario(url);
                                        }
                                    }
                                );

                                Toast
                                    .makeText(
                                        EditarPerfilActivity.this,
                                        "Sucesso ao fazer upload da Imagem.",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show();
                            }
                        }
                    );
                }
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
    }

    private void atualizarFotoUsuario(Uri url)
    {
        UsuarioFirebase.atualizarFotoUsuario(url);

        userLoggado.setCaminhoFoto(url.toString());
        userLoggado.atualizar();

        Toast
            .makeText(
                EditarPerfilActivity.this,
                "Foto de Perfil atualizada.",
                Toast.LENGTH_SHORT
            )
            .show();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();

        return false;
    }
}