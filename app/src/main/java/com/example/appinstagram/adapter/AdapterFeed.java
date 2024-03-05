package com.example.appinstagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appinstagram.R;
import com.example.appinstagram.activity.ComentariosActivity;
import com.example.appinstagram.helper.ConfigFireBase;
import com.example.appinstagram.helper.UsuarioFirebase;
import com.example.appinstagram.model.Feed;
import com.example.appinstagram.model.PostagemCurtida;
import com.example.appinstagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 *
 */
public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder>
{

    private List<Feed> listaFeed;
    private Context context;

    public AdapterFeed(List<Feed> listaFeed, Context context)
    {
        this.listaFeed = listaFeed;
        this.context   = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed, parent, false);

        return new AdapterFeed.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterFeed.MyViewHolder holder, int position)
    {
        Feed feed = listaFeed.get(position);
        Usuario usuarioLogado = UsuarioFirebase.getDadosUserLogged();

        Uri uriFotoUsuario  = Uri.parse(feed.getFotoUsuario());
        Uri uriFotoPostagem = Uri.parse(feed.getFotoPostagem());

        Glide
            .with(context)
            .load(uriFotoUsuario)
            .into(holder.fotoPerfil);
        Glide
            .with(context)
            .load(uriFotoPostagem)
            .into(holder.fotoPostagem);

        holder.descricao.setText(feed.getDescricao());
        holder.nome.setText(feed.getNomeUsuario());

        holder.visualizarComentario.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent(context, ComentariosActivity.class);

                    i.putExtra("idPostagem", feed.getId());

                    context.startActivity(i);
                }
            }
        );

        DatabaseReference curtidasRef = ConfigFireBase
            .getFireBaseDataBase()
            .child("postagens-curtidas")
            .child(feed.getId());

        curtidasRef.addListenerForSingleValueEvent(
            new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                {
                    int qtdCurtidas = 0;

                    if ( snapshot.hasChild("qtdCurtidas") )
                    {
                        PostagemCurtida postagemCurtida = snapshot.getValue(PostagemCurtida.class);
                        qtdCurtidas = postagemCurtida.getQtdCurtidas();
                    }

                    if ( snapshot.hasChild(usuarioLogado.getId()) )
                    {
                        holder.likeButton.setLiked(true);
                    }
                    else
                    {
                        holder.likeButton.setLiked(false);
                    }

                    PostagemCurtida curtida = new PostagemCurtida();

                    curtida.setFeed(feed);
                    curtida.setUsuario(usuarioLogado);
                    curtida.setQtdCurtidas(qtdCurtidas);

                    holder.likeButton.setOnLikeListener(new OnLikeListener()
                    {
                        @Override
                        public void liked(LikeButton likeButton)
                        {
                            curtida.salvar();
                            holder.qtdCurtidas.setText(curtida.getQtdCurtidas() + "curtidas");
                        }

                        @Override
                        public void unLiked(LikeButton likeButton)
                        {
                            curtida.remover();
                            holder.qtdCurtidas.setText(curtida.getQtdCurtidas() + "curtidas");
                        }
                    });

                    holder.qtdCurtidas.setText(curtida.getQtdCurtidas() + "curtidas");
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error)
                {

                }
            }
        );
    }

    @Override
    public int getItemCount()
    {
        return listaFeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView fotoPerfil;
        TextView nome, descricao, qtdCurtidas;
        ImageView fotoPostagem, visualizarComentario;
        LikeButton likeButton;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            fotoPerfil   = itemView.findViewById(R.id.imagePerfilPostagem);
            nome         = itemView.findViewById(R.id.textPerfilPostagem);
            descricao    = itemView.findViewById(R.id.textDescricaoPostagem);
            visualizarComentario = itemView.findViewById(R.id.imageComentarioFeed);
            qtdCurtidas  = itemView.findViewById(R.id.textQtdeCurtidasPostagem);
            fotoPostagem = itemView.findViewById(R.id.imagePostagemSelecionada);
            likeButton   = itemView.findViewById(R.id.likeButtonFeed);
        }
    }
}