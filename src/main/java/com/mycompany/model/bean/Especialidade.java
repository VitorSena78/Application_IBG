package com.mycompany.model.bean;

/**
 *
 * @author vitor
 */
public class Especialidade {
    private int id;
    private String nome;
    private int atendimentosRestantesHoje;
    private int atendimentosTotaisHoje;

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

    public int getAtendimentosRestantesHoje() {
        return atendimentosRestantesHoje;
    }

    public void setAtendimentosRestantesHoje(int atendimentosRestantesHoje) {
        this.atendimentosRestantesHoje = atendimentosRestantesHoje;
    }

    public int getAtendimentosTotaisHoje() {
        return atendimentosTotaisHoje;
    }

    public void setAtendimentosTotaisHoje(int atendimentosTotaisHoje) {
        this.atendimentosTotaisHoje = atendimentosTotaisHoje;
    }
    
    //Calcula o número do próximo atendimento
    public int calcularNumeroProximoAtendimento() {
        return (atendimentosTotaisHoje - atendimentosRestantesHoje + 1);
    }
    
    //Verifica se ainda há atendimentos disponíveis
    public boolean temAtendimentosDisponiveis() {
        return atendimentosRestantesHoje > 0;
    }
    
    //Formata a numeração do atendimento (ex: "3/15")
    public String formatarNumeracaoAtendimento() {
        return calcularNumeroProximoAtendimento() + "/" + atendimentosTotaisHoje;
    }

    @Override
    public String toString() {
        return "Especialidade{" + "id=" + id + ", nome=" + nome + ", atendimentosRestantesHoje=" + atendimentosRestantesHoje + ", atendimentosTotaisHoje=" + atendimentosTotaisHoje + '}';
    }
  
}
