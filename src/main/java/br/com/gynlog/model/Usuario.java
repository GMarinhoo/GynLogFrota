package br.com.gynlog.model;

import br.com.gynlog.enums.TipoUsuario;

public class Usuario {
    private int id;
    private String login;
    private String senha;
    private String nome;
    private TipoUsuario tipo;

    public Usuario() {}

    public Usuario(int id, String login, String senha, String nome, TipoUsuario tipo) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }

    public String toCsvLine() {
        return id + ";" + login + ";" + senha + ";" + nome + ";" + tipo.name();
    }

    public static Usuario fromString(String linha) {
        String[] p = linha.split(";");
        if(p.length < 5) return null;
        return new Usuario(Integer.parseInt(p[0]), p[1], p[2], p[3], TipoUsuario.valueOf(p[4]));
    }
}