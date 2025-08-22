package com.mycompany.model.bean;

/**
 *
 * @author vitor
 */
public class Especialidade {
    private Integer id;
    private String nome;
    private int atendimentosRestantesHoje;
    private int atendimentosTotaisHoje;

    public Especialidade() {
        // Construtor padrão
    }

    public Especialidade(String nome, int atendimentosRestantesHoje, int atendimentosTotaisHoje) {
        this.nome = nome;
        this.atendimentosRestantesHoje = atendimentosRestantesHoje;
        this.atendimentosTotaisHoje = atendimentosTotaisHoje;
    }
  
    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
        this.atendimentosRestantesHoje = Math.max(0, atendimentosRestantesHoje);
    }

    public int getAtendimentosTotaisHoje() {
        return atendimentosTotaisHoje;
    }

    public void setAtendimentosTotaisHoje(int atendimentosTotaisHoje) {
        this.atendimentosTotaisHoje = Math.max(0, atendimentosTotaisHoje);
    }
    
    //Calcula o número do próximo atendimento
    public int calcularNumeroProximoAtendimento() {
        // Validação para evitar valores negativos
        int proximo = (atendimentosTotaisHoje - atendimentosRestantesHoje + 1);
        return Math.max(1, proximo);
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
        return "Especialidade{" + 
               "id=" + id + 
               ", nome='" + getNome() + "'" + 
               ", atendimentosRestantesHoje=" + atendimentosRestantesHoje + 
               ", atendimentosTotaisHoje=" + atendimentosTotaisHoje + 
               '}';
    }
  
}
