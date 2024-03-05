package com.example.appinstagram.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.appinstagram.R;
import com.example.appinstagram.activity.EditarPerfilActivity;
import com.example.appinstagram.activity.FiltroActivity;
import com.example.appinstagram.helper.Permissao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 *
 */
public class PostagemFragment extends Fragment
{

    private Button buttonAbrirGaleria, buttonAbrirCamera;

    private static final int SELECAO_GALERIA = 200;
    private static final int SELECAO_CAMERA = 100;

    private String[] permissoes_necessarias = new String[]
    {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    };

    public PostagemFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postagem, container, false);

        Permissao.validarPermissoes(permissoes_necessarias, getActivity(), 1);

        buttonAbrirCamera  = view.findViewById(R.id.buttonAbrirCamera);
        buttonAbrirGaleria = view.findViewById(R.id.buttonAbrirGaleria);

        buttonAbrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if ( i.resolveActivity(getActivity().getPackageManager()) != null )
                {
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });

        buttonAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if ( i.resolveActivity(getActivity().getPackageManager()) != null )
                {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == getActivity().RESULT_OK )
        {
            Bitmap img = null;

            try
            {
                switch ( requestCode )
                {
                    case SELECAO_CAMERA:
                        img = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImgSelecionada = data.getData();
                        img = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImgSelecionada);
                        break;
                }

                if ( img != null )
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    img.compress(Bitmap.CompressFormat.JPEG,70, baos);

                    byte[] dadosImg = baos.toByteArray();

                    Intent i = new Intent(getActivity(), FiltroActivity.class);

                    i.putExtra("fotoEscolhida", dadosImg);

                    startActivity(i);
                }
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
    }
}