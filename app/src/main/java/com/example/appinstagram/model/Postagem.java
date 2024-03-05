package com.example.appinstagram.model;

import com.example.appinstagram.helper.ConfigFireBase;
import com.example.appinstagram.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Postagem implements Serializable
{

    private String id, idUsuario, descricao, caminhoFoto;

    public Postagem()
    {
        DatabaseReference firebaseRef = ConfigFireBase.getFireBaseDataBase();
        DatabaseReference postagemRef = firebaseRef.child("postagens");

        String idPostagem = postagemRef.push().getKey();

        this.id = idPostagem;
    }

    public boolean salvar(DataSnapshot seguidoresSnapShot)
    {
        Map objeto = new HashMap<>();

        Usuario usuarioLogado = UsuarioFirebase.getDadosUserLogged();
        DatabaseReference firebaseRef = ConfigFireBase.getFireBaseDataBase();

        String combinacaoID = "/" + getIdUsuario() + "/" + getId();

        objeto.put("/postagens" +  combinacaoID, this);

        for ( DataSnapshot seguidores : seguidoresSnapShot.getChildren() )
        {
            String idSeguidor = seguidores.getKey();

            Map<String, Object> dadosSeguidor = new HashMap<>();

            dadosSeguidor.put("fotoPostagem", getCaminhoFoto());
            dadosSeguidor.put("descricao", getDescricao());
            dadosSeguidor.put("id", getId());
            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNome());
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getCaminhoFoto());

            String idsAtualizacao = "/" + idSeguidor + "/" + getId();

            objeto.put("/feed" + idsAtualizacao, dadosSeguidor);
        }

        firebaseRef.updateChildren(objeto);

        return true;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getIdUsuario()
    {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario)
    {
        this.idUsuario = idUsuario;
    }

    public String getDescricao()
    {
        return descricao;
    }

    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
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