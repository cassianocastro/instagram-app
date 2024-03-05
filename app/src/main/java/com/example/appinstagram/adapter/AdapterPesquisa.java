package com.example.appinstagram.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appinstagram.R;
import com.example.appinstagram.model.Usuario;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 *
 */
public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.MyViewHolder>
{

    private List<Usuario> listaUsuarios;
    private Context context;

    public AdapterPesquisa(List<Usuario> listaUsuarios, Context context)
    {
        this.listaUsuarios = listaUsuarios;
        this.context       = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesquisa_usuario, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterPesquisa.MyViewHolder holder, int position)
    {
        Usuario usuario = listaUsuarios.get(position);

        holder.nome.setText(usuario.getNome());

        if ( usuario.getCaminhoFoto() != null )
        {
            Uri uri = Uri.parse(usuario.getCaminhoFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }
        else
        {
            holder.foto.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public int getItemCount()
    {
        return listaUsuarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView foto;
        TextView nome;

        public MyViewHolder(@NonNull @NotNull View itemView)
        {
            super(itemView);

            foto = itemView.findViewById(R.id.imageFotoPesquisa);
            nome = itemView.findViewById(R.id.textNomePesquisa);
        }
    }
}