package com.example.appinstagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appinstagram.R;
import com.example.appinstagram.model.Postagem;
import com.example.appinstagram.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {

    private TextView textPerfilPostagem;
    private TextView textQtdeCurtidasPostagem;
    private TextView textDescricaoPostagem;
    private TextView textVisualizarComentariosPostagem;
    private ImageView imagePostagemSelecionada;
    private CircleImageView imagePerfilPostagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);

        textPerfilPostagem                = findViewById(R.id.textPerfilPostagem);
        textQtdeCurtidasPostagem          = findViewById(R.id.textQtdeCurtidasPostagem);
        textDescricaoPostagem             = findViewById(R.id.textDescricaoPostagem);

        imagePostagemSelecionada = findViewById(R.id.imagePostagemSelecionada);
        imagePerfilPostagem      = findViewById(R.id.imagePerfilPostagem);

        Toolbar t = findViewById(R.id.toolbarPrincipal);
        t.setTitle("Visualizar Postagem");
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Postagem postagem = (Postagem) bundle.getSerializable("postagem");
            Usuario usuario   = (Usuario)  bundle.getSerializable("usuario");

            Uri uri = Uri.parse(usuario.getCaminhoFoto());
            Glide
                    .with(VisualizarPostagemActivity.this)
                    .load(uri)
                    .into(imagePerfilPostagem);
            textPerfilPostagem.setText(usuario.getNome());

            Uri uriPostagem = Uri.parse(postagem.getCaminhoFoto());
            Glide
                    .with(VisualizarPostagemActivity.this)
                    .load(uriPostagem)
                    .into(imagePostagemSelecionada);
            textDescricaoPostagem.setText(postagem.getDescricao());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}