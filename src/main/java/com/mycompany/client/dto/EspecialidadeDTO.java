package com.mycompany.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO corrigido para corresponder EXATAMENTE ao formato da API Spring Boot
 * Baseado na análise do JSON de resposta real da API de Especialidades
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EspecialidadeDTO {
    
    private Integer id;
    private String nome;
    
    // Campos extras que podem existir em outras chamadas da API
    // (mantidos para compatibilidade, mas não estão no JSON atual)
    @JsonProperty("atendimentos_restantes_hoje")
    private Integer atendimentosRestantesHoje;
    
    @JsonProperty("atendimentos_totais_hoje")
    private Integer atendimentosTotaisHoje;
    
    private Integer fichas;
    
    // Campos de auditoria (podem aparecer em outras calls)
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    // Lista de pacientes (relacionamento - flexível)
    private List<PacienteEspecialidadeDTO> pacientes;
    
    public EspecialidadeDTO() {}
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public Integer getAtendimentosRestantesHoje() { return atendimentosRestantesHoje; }
    public void setAtendimentosRestantesHoje(Integer atendimentosRestantesHoje) { 
        this.atendimentosRestantesHoje = atendimentosRestantesHoje; 
    }
    
    public Integer getAtendimentosTotaisHoje() { return atendimentosTotaisHoje; }
    public void setAtendimentosTotaisHoje(Integer atendimentosTotaisHoje) { 
        this.atendimentosTotaisHoje = atendimentosTotaisHoje; 
    }
    
    public Integer getFichas() { return fichas; }
    public void setFichas(Integer fichas) { this.fichas = fichas; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public List<PacienteEspecialidadeDTO> getPacientes() { return pacientes; }
    public void setPacientes(List<PacienteEspecialidadeDTO> pacientes) { this.pacientes = pacientes; }
    
    @Override
    public String toString() {
        return "EspecialidadeDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", atendimentosRestantesHoje=" + atendimentosRestantesHoje +
                ", atendimentosTotaisHoje=" + atendimentosTotaisHoje +
                ", fichas=" + fichas +
                '}';
    }
}