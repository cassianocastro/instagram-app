package com.example.appinstagram.model;

import com.example.appinstagram.helper.ConfigFireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Usuario implements Serializable
{
    private String id, nome, email, senha, caminhoFoto;
    private int seguidores, seguindo, postagens;

    public Usuario()
    {
        this.seguidores = 0;
        this.seguindo   = 0;
        this.postagens  = 0;
    }

    public Usuario(String id, String nome, String email, String senha, String caminhoFoto)
    {
        this.id          = id;
        this.nome        = nome;
        this.email       = email;
        this.senha       = senha;
        this.caminhoFoto = caminhoFoto;

        this();
    }

    public void salvar()
    {
        DatabaseReference fireDatabaseReference = ConfigFireBase.getFireBaseDataBase();
        DatabaseReference usuariosReference     = fireDatabaseReference.child("usuarios").child(this.id);

        usuariosReference.setValue(this);
    }

    public void atualizarQtdePostagens()
    {
        DatabaseReference firebaseref       = ConfigFireBase.getFireBaseDataBase();
        DatabaseReference usuariosReference = firebaseref.child("usuarios").child(this.id);

        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put("postagens", getPostagens());

        usuariosReference.updateChildren(userMap);
    }

    public void atualizar()
    {
        DatabaseReference firebaseref       = ConfigFireBase.getFireBaseDataBase();
        DatabaseReference usuariosReference = firebaseref.child("usuarios").child(this.id);

        usuariosReference.updateChildren(toMap());
    }

    public Map<String, Object> toMap()
    {
        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put("email", this.email);
        userMap.put("nome", this.nome);
        userMap.put("id", this.id);
        userMap.put("caminhoFoto", this.caminhoFoto);
        userMap.put("seguidores", this.seguidores);
        userMap.put("seguindo", this.seguindo);
        userMap.put("postagens", this.postagens);

        return userMap;
    }

    public int getSeguidores()
    {
        return seguidores;
    }

    public int getSeguindo()
    {
        return seguindo;
    }

    public int getPostagens()
    {
        return postagens;
    }

    public void setSeguidores(int seguidores)
    {
        this.seguidores = seguidores;
    }

    public void setSeguindo(int seguindo)
    {
        this.seguindo = seguindo;
    }

    public void setPostagens(int postagens)
    {
        this.postagens = postagens;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNome()
    {
        return this.nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome.toUpperCase();
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Exclude
    public String getSenha()
    {
        return senha;
    }

    public void setSenha(String senha)
    {
        this.senha = senha;
    }

    public String getCaminhoFoto()
    {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto)
    {
        this.caminhoFoto = caminhoFoto;
    }
}