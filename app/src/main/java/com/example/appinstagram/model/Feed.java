package com.example.appinstagram.model;

/**
 *
 */
public class Feed
{

    private String id, fotoPostagem, descricao;
    private String nomeUsuario, fotoUsuario;

    public Feed()
    {

    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getFotoPostagem()
    {
        return fotoPostagem;
    }

    public void setFotoPostagem(String fotoPostagem)
    {
        this.fotoPostagem = fotoPostagem;
    }

    public String getDescricao()
    {
        return descricao;
    }

    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
    }

    public String getNomeUsuario()
    {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario)
    {
        this.nomeUsuario = nomeUsuario;
    }

    public String getFotoUsuario()
    {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario)
    {
        this.fotoUsuario = fotoUsuario;
    }
}