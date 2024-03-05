package com.example.appinstagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.appinstagram.R;
import com.example.appinstagram.adapter.AdapterMiniaturas;
import com.example.appinstagram.helper.ConfigFireBase;
import com.example.appinstagram.helper.RecyclerItemClickListener;
import com.example.appinstagram.helper.UsuarioFirebase;
import com.example.appinstagram.model.Postagem;
import com.example.appinstagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FiltroActivity extends AppCompatActivity
{

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;
    private List<ThumbnailItem> listaFiltros;
    private String idUsuarioLogado;
    private TextInputEditText textDescricaoFiltro;
    private Usuario usuarioLogado;
    // private ProgressBar progressBar;
    private boolean estaCarregando;

    private RecyclerView recyclerFiltros;
    private AdapterMiniaturas adapterMiniaturas;

    private DatabaseReference usuarioRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference firebaseRef;
    private DataSnapshot seguidoresSnapShot;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filtro);

        Toolbar t = findViewById(R.id.toolbarPrincipal);

        t.setTitle("Filtros");

        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        listaFiltros        = new ArrayList<>();
        idUsuarioLogado     = UsuarioFirebase.getID();
        usuarioRef          = ConfigFireBase.getFireBaseDataBase().child("usuarios");
        firebaseRef         = ConfigFireBase.getFireBaseDataBase();

        imageFotoEscolhida  = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros     = findViewById(R.id.recyclerFiltros);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);
        // progressBar         = findViewById(R.id.progressFiltro);

        recuperarDadosPostagem();

        Bundle bundle = getIntent().getExtras();

        if ( bundle != null )
        {
            byte[] dadosImg = bundle.getByteArray("fotoEscolhida");

            imagem       = BitmapFactory.decodeByteArray(dadosImg, 0, dadosImg.length);
            imageFotoEscolhida.setImageBitmap(imagem);
            imagemFiltro = imagem.copy(imagem.getConfig(), true);

            adapterMiniaturas = new AdapterMiniaturas(listaFiltros, getApplicationContext());

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            );

            recyclerFiltros.setLayoutManager(layoutManager);
            recyclerFiltros.setAdapter(adapterMiniaturas);

            recyclerFiltros.addOnItemTouchListener(
                new RecyclerItemClickListener(
                    getApplicationContext(),
                    recyclerFiltros,
                    new RecyclerItemClickListener.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(View view, int position)
                        {
                            ThumbnailItem item = listaFiltros.get(position);
                            imagemFiltro       = imagem.copy(imagem.getConfig(), true);

                            Filter filter      = item.filter;
                            imageFotoEscolhida.setImageBitmap(filter.processFilter(imagemFiltro));
                        }

                        @Override
                        public void onLongItemClick(View view, int position)
                        {

                        }

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {

                        }
                    }
                )
            );

            recuperarFiltros();
        }
    }

    // private void carregando(boolean estado)
    // {
    //     if ( estado )
    //     {
    //         estaCarregando = true;
    //         // progressBar.setVisibility(View.VISIBLE);
    //     }
    //     else
    //     {
    //         estaCarregando = false;
    //         // progressBar.setVisibility(View.GONE);
    //     }
    // }

    private void abrirDialogCarregamento(String titulo)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(titulo);
        alert.setCancelable(false);
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();
    }

    private void recuperarDadosPostagem()
    {
        abrirDialogCarregamento("Carregando dados, aguarde!");

        usuarioLogadoRef = usuarioRef.child(idUsuarioLogado);

        usuarioLogadoRef.addListenerForSingleValueEvent(
            new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                {
                    usuarioLogado = snapshot.getValue(Usuario.class);

                    DatabaseReference seguidoresRef = firebaseRef
                        .child("seguidores")
                        .child(idUsuarioLogado);

                    seguidoresRef.addListenerForSingleValueEvent(
                        new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                            {
                                seguidoresSnapShot = snapshot;
                                dialog.cancel();
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error)
                            {

                            }
                        }
                    );
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error)
                {

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

    private void recuperarFiltros()
    {
        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();

        ThumbnailItem item = new ThumbnailItem();
        item.image         = imagem;
        item.filterName    = "Normal";

        ThumbnailsManager.addThumb(item);

        List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

        for ( Filter filter : filters )
        {
            ThumbnailItem itemFiltro = new ThumbnailItem();

            itemFiltro.image      = imagem;
            itemFiltro.filter     = filter;
            itemFiltro.filterName = filter.getName();

            ThumbnailsManager.addThumb(itemFiltro);
        }

        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapterMiniaturas.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch ( item.getItemId() )
        {
            case R.id.ic_salvar_postagem:
                publicarPostagem();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void publicarPostagem()
    {
        abrirDialogCarregamento("Salvando Postagem");

        Postagem postagem = new Postagem();

        postagem.setIdUsuario(idUsuarioLogado);
        postagem.setDescricao(textDescricaoFiltro.getText().toString());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        imagemFiltro.compress(Bitmap.CompressFormat.JPEG,70, baos);

        byte[] dadosImg = baos.toByteArray();

        StorageReference storageRef = ConfigFireBase.getStorage();

        final StorageReference imagemRef = storageRef
            .child("imagens")
            .child("postagens")
            .child(postagem.getId() + ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes(dadosImg);

        uploadTask
            .addOnFailureListener(
                new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast
                            .makeText(
                                FiltroActivity.this,
                                "Erro ao salvar a Imagem, tente novamente.",
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
                        imagemRef.getDownloadUrl().addOnCompleteListener(
                            new OnCompleteListener<Uri>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task)
                                {
                                    Uri url = task.getResult();

                                    postagem.setCaminhoFoto(url.toString());

                                    int qtdePostagens = usuarioLogado.getPostagens() + 1;

                                    usuarioLogado.setPostagens(qtdePostagens);
                                    usuarioLogado.atualizarQtdePostagens();

                                    if ( postagem.salvar(seguidoresSnapShot) )
                                    {
                                        Toast
                                            .makeText(
                                                FiltroActivity.this,
                                                "Sucesso ao salvar postagem.",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show();

                                        dialog.cancel();

                                        finish();
                                    }
                                }
                            }
                        );
                    }
                }
            );
    }
}