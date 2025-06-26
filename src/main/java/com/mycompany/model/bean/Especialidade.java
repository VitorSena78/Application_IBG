package com.mycompany.model.bean;

/**
 *
 * @author vitor
 */
public class Especialidade {
    private int idEspecialidade;
    private String nome;

    public Especialidade() {
        // Construtor padrão
    }

    public Especialidade(String nome) {
        this.nome = nome;
    }
  
    // Getters e Setters

    public int getIdEspecialidade() {
        return idEspecialidade;
    }

    public void setIdEspecialidade(int idEspecialidade) {
        this.idEspecialidade = idEspecialidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Especialidade{" + "idEspecialidade=" + idEspecialidade + ", nome=" + nome + '}';
    }
    
    
    
}
