package com.mycompany.model.bean;

/**
 *
 * @author vitor
 */
public class Especialidade {
    private int id;
    private String nome;

    public Especialidade() {
        // Construtor padrão
    }

    public Especialidade(String nome) {
        this.nome = nome;
    }
  
    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Especialidade{" + "id=" + id + ", nome=" + nome + '}';
    }
    
    
    
}
