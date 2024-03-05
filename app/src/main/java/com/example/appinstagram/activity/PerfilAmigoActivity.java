package com.example.appinstagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appinstagram.R;
import com.example.appinstagram.adapter.AdapterGrid;
import com.example.appinstagram.helper.ConfigFireBase;
import com.example.appinstagram.helper.UsuarioFirebase;
import com.example.appinstagram.model.Postagem;
import com.example.appinstagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 *
 */
public class PerfilAmigoActivity extends AppCompatActivity
{

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private GridView gridViewPerfil;
    private AdapterGrid adapterGrid;
    private List<Postagem> postagens;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuarioRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfilAmigo;

    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_perfil_amigo);

        idUsuarioLogado  = UsuarioFirebase.getID();
        gridViewPerfil   = findViewById(R.id.gridViewPerfil);
        firebaseRef      = ConfigFireBase.getFireBaseDataBase();
        usuarioRef       = firebaseRef.child("usuarios");
        seguidoresRef    = firebaseRef.child("seguidores");
        textPublicacoes  = findViewById(R.id.textPublicacoes);
        textSeguidores   = findViewById(R.id.textSeguidores);
        textSeguindo     = findViewById(R.id.textSeguindo);
        imagePerfil      = findViewById(R.id.imageEditarPerfil);
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);

        buttonAcaoPerfil.setText("Carregando");

        Toolbar t = findViewById(R.id.toolbarPrincipal);

        t.setTitle("Perfil");

        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        Bundle bundle = getIntent().getExtras();

        if ( bundle != null )
        {
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            getSupportActionBar().setTitle(usuarioSelecionado.getNome());

            postagensUsuarioRef = ConfigFireBase.getFireBaseDataBase()
                .child("postagens")
                .child(usuarioSelecionado.getId());

            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();

            if ( caminhoFoto != null )
            {
                Uri url = Uri.parse(caminhoFoto);
                Glide
                    .with(PerfilAmigoActivity.this)
                    .load(url)
                    .into(imagePerfil);
            }
        }

        inicializarImageLoader();
        carregarFotosPostagem();

        gridViewPerfil.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Postagem postagem = postagens.get(position);

                    Intent i = new Intent(getApplicationContext(), VisualizarPostagemActivity.class);

                    i.putExtra("postagem", postagem);
                    i.putExtra("usuario", usuarioSelecionado);

                    startActivity(i);
                }
            }
        );
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();

        return false;
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        recuperarDadosPerfilAmigo();
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    private void recuperarDadosPerfilAmigo()
    {
        usuarioAmigoRef = usuarioRef.child(usuarioSelecionado.getId());

        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                {
                    Usuario usuario   = snapshot.getValue(Usuario.class);
                    String postagens  = String.valueOf(usuario.getPostagens());
                    String seguidores = String.valueOf(usuario.getSeguidores());
                    String seguindo   = String.valueOf(usuario.getSeguindo());

                    textPublicacoes.setText(postagens);
                    textSeguidores .setText(seguidores);
                    textSeguindo   .setText(seguindo);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error)
                {

                }
            }
        );
    }

    private void inicializarImageLoader()
    {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
            .Builder(this)
            .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
            .memoryCacheSize(2 * 1024 * 1024)
            .diskCacheSize(50 * 1024 * 1024)
            .diskCacheFileCount(100)
            .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
            .build();

        ImageLoader.getInstance().init(config);
    }

    public void carregarFotosPostagem()
    {
        postagens = new ArrayList<>();

        postagensUsuarioRef.addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                {
                    int tamanhoGrid   = getResources().getDisplayMetrics().widthPixels;
                    int tamanhoImagem = tamanhoGrid / 3;
                    gridViewPerfil.setColumnWidth(tamanhoImagem);

                    List<String> urlFotos = new ArrayList<>();

                    for ( DataSnapshot ds : snapshot.getChildren() )
                    {
                        Postagem postagem = ds.getValue(Postagem.class);

                        postagens.add(postagem);
                        urlFotos.add(postagem.getCaminhoFoto());
                    }

                    // int qtdePostagens = urlFotos.size();
                    // textPublicacoes.setText(String.valueOf(qtdePostagens));

                    adapterGrid = new AdapterGrid(
                        getApplicationContext(),
                        R.layout.grid_postagem,
                        urlFotos
                    );

                    gridViewPerfil.setAdapter(adapterGrid);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error)
                {

                }
            }
        );
    }

    private void recuperarDadosUsuarioLogado()
    {
        usuarioLogadoRef = usuarioRef.child(idUsuarioLogado);

        usuarioLogadoRef.addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                {
                    usuarioLogado = snapshot.getValue(Usuario.class);

                    verificaSegueUsuarioAmigo();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error)
                {

                }
            }
        );
    }

    private void verificaSegueUsuarioAmigo()
    {
        DatabaseReference seguidorRef = seguidoresRef
            .child(usuarioSelecionado.getId())
            .child(idUsuarioLogado);

        seguidorRef.addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                {
                    if (snapshot.exists())
                    {
                        habilitaButtonSeguir(true);
                    }
                    else
                    {
                        habilitaButtonSeguir(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error)
                {

                }
            }
        );
    }

    private void habilitaButtonSeguir(boolean segueUsuario)
    {
        if ( segueUsuario )
        {
            buttonAcaoPerfil.setText("Seguindo");
        }
        else
        {
            buttonAcaoPerfil.setText("Seguir");
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                }
            });
        }
    }

    private void salvarSeguidor(Usuario usuarioLogado, Usuario usuarioAmigo)
    {
        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();

        dadosUsuarioLogado.put("nome", usuarioLogado.getNome());
        dadosUsuarioLogado.put("caminhoFoto", usuarioLogado.getCaminhoFoto());

        DatabaseReference seguidorRef = seguidoresRef
            .child(usuarioAmigo.getId())
            .child(usuarioLogado.getId());

        seguidorRef.setValue(dadosUsuarioLogado);

        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

        int seguindo = usuarioLogado.getSeguindo() + 1;

        HashMap<String, Object> dadosSeguindo = new HashMap<>();

        dadosSeguindo.put("seguindo", seguindo);

        DatabaseReference usuarioSeguindo = usuarioRef.child(usuarioLogado.getId());

        usuarioSeguindo.updateChildren(dadosSeguindo);

        int seguidores = usuarioAmigo.getSeguidores() + 1;

        HashMap<String, Object> dadosSeguidores = new HashMap<>();

        dadosSeguidores.put("seguidores", seguidores);

        DatabaseReference usuarioSeguidores = usuarioRef.child(usuarioAmigo.getId());

        usuarioSeguidores.updateChildren(dadosSeguidores);
    }
}