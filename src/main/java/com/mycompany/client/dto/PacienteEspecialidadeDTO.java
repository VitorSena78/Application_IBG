package com.mycompany.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para receber dados de PacienteEspecialidade da API Spring Boot
 * Corresponde exatamente ao formato JSON retornado pela API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PacienteEspecialidadeDTO {
    
    // Campo ID que existe na API mas não no modelo de domínio
    private Integer id;
    
    @JsonProperty("paciente_id")
    private Integer pacienteId;
    
    @JsonProperty("especialidade_id")
    private Integer especialidadeId;
    
    @JsonProperty("data_atendimento")
    private String dataAtendimento;
    
    // Campos de auditoria
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    // Construtor padrão
    public PacienteEspecialidadeDTO() {}
    
    // Getters e Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getPacienteId() {
        return pacienteId;
    }
    
    public void setPacienteId(Integer pacienteId) {
        this.pacienteId = pacienteId;
    }
    
    public Integer getEspecialidadeId() {
        return especialidadeId;
    }
    
    public void setEspecialidadeId(Integer especialidadeId) {
        this.especialidadeId = especialidadeId;
    }
    
    public String getDataAtendimento() {
        return dataAtendimento;
    }
    
    public void setDataAtendimento(String dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "PacienteEspecialidadeDTO{" +
                "id=" + id +
                ", pacienteId=" + pacienteId +
                ", especialidadeId=" + especialidadeId +
                ", dataAtendimento='" + dataAtendimento + '\'' +
                '}';
    }
}