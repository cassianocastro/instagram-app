package com.example.appinstagram.model;

import com.example.appinstagram.helper.ConfigFireBase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class PostagemCurtida {

    public  Feed feed;
    public Usuario usuario;
    public int qtdCurtidas = 0;

    public PostagemCurtida() {

    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfigFireBase.getFireBaseDataBase();

        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNome());
        dadosUsuario.put("caminhoFoto", usuario.getCaminhoFoto());

        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child(feed.getId())
                .child(usuario.getId());

        pCurtidasRef.setValue(dadosUsuario);
        atualizarQtde(1);
    }

    public void atualizarQtde(int valor){
        DatabaseReference firebaseRef  = ConfigFireBase.getFireBaseDataBase();
        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child(feed.getId())
                .child("qtdCurtidas");

        setQtdCurtidas(getQtdCurtidas() + valor);
        pCurtidasRef.setValue(getQtdCurtidas());
    }

    public void remover(){
        DatabaseReference firebaseRef  = ConfigFireBase.getFireBaseDataBase();
        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child(feed.getId())
                .child(usuario.getId());

        pCurtidasRef.removeValue();
        atualizarQtde(-1);
    }

    public int getQtdCurtidas() {
        return qtdCurtidas;
    }

    public void setQtdCurtidas(int qtdCurtidas) {
        this.qtdCurtidas = qtdCurtidas;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
