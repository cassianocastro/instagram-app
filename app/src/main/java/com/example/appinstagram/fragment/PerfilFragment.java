package com.example.appinstagram.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appinstagram.R;
import com.example.appinstagram.activity.EditarPerfilActivity;
import com.example.appinstagram.activity.PerfilAmigoActivity;
import com.example.appinstagram.adapter.AdapterGrid;
import com.example.appinstagram.helper.ConfigFireBase;
import com.example.appinstagram.helper.UsuarioFirebase;
import com.example.appinstagram.model.Postagem;
import com.example.appinstagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {
    private ProgressBar progressBar;
    private CircleImageView imagePerfil;
    private GridView gridViewPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private Button buttonAcaoPerfil;

    private Usuario usuarioLogado;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuarioRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfil;
    private AdapterGrid adapterGrid;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view           = inflater.inflate(R.layout.fragment_perfil, container, false);

        usuarioLogado       = UsuarioFirebase.getDadosUserLogged();
        firebaseRef         = ConfigFireBase.getFireBaseDataBase();
        usuarioRef          = firebaseRef.child("usuarios");
        postagensUsuarioRef = ConfigFireBase.getFireBaseDataBase()
                .child("postagens")
                .child(usuarioLogado.getId());


        progressBar      = view.findViewById(R.id.progressBarPerfil);
        imagePerfil      = view.findViewById(R.id.imageEditarPerfil);
        gridViewPerfil   = view.findViewById(R.id.gridViewPerfil);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);
        textPublicacoes  = view.findViewById(R.id.textPublicacoes);
        textSeguidores   = view.findViewById(R.id.textSeguidores);
        textSeguindo     = view.findViewById(R.id.textSeguindo);

        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if (caminhoFoto != null){
            Uri url = Uri.parse(caminhoFoto);
            Glide
                    .with(getActivity())
                    .load(url)
                    .into(imagePerfil);
        }

        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        inicializarImageLoader();
        carregarFotosPostagem();

        return view;
    }

    public void carregarFotosPostagem(){
        postagensUsuarioRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        int tamanhoGrid   = getResources().getDisplayMetrics().widthPixels;
                        int tamanhoImagem = tamanhoGrid / 3;
                        gridViewPerfil.setColumnWidth(tamanhoImagem);

                        List<String> urlFotos = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            Postagem postagem = ds.getValue(Postagem.class);
                            urlFotos.add(postagem.getCaminhoFoto());
                        }
                        int qtdePostagens = urlFotos.size();
                        textPublicacoes.setText(String.valueOf(qtdePostagens));
                        adapterGrid = new AdapterGrid(
                                getActivity(),
                                R.layout.grid_postagem,
                                urlFotos
                        );
                        gridViewPerfil.setAdapter(adapterGrid);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                }
        );
    }

    private void inicializarImageLoader(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onStart() {
        super.onStart();

        recuperarDadosUsuarioLogado();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioLogadoRef.removeEventListener(valueEventListenerPerfil);
    }

    private void recuperarDadosUsuarioLogado() {
        usuarioLogadoRef         = usuarioRef.child(usuarioLogado.getId());
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        Usuario usuario   = snapshot.getValue(Usuario.class);
                        String postagens  = String.valueOf(usuario.getPostagens());
                        String seguidores = String.valueOf(usuario.getSeguidores());
                        String seguindo   = String.valueOf(usuario.getSeguindo());
                        textPublicacoes.setText(postagens);
                        textSeguidores .setText(seguidores);
                        textSeguindo   .setText(seguindo);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                }
        );
    }
}