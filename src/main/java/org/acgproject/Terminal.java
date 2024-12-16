package org.acgproject;

public class Terminal {
    private Integer id;
    private String nome;
    private String municipio;

    public Terminal(Integer id, String nome, String municipio) {
        this.id = id;
        this.nome = nome;
        this.municipio = municipio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getMunicipio() {
        return municipio;
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", municipio='" + municipio + '\'' +
                '}';
    }
}
