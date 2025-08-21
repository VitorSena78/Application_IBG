package com.mycompany.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO corrigido para corresponder EXATAMENTE ao formato da API Spring Boot
 * Baseado na análise do JSON de resposta real da API de PacienteEspecialidade
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PacienteEspecialidadeDTO {
    
    @JsonProperty("isDeleted")
    private Boolean isDeleted;
    
    @JsonProperty("paciente_local_id")
    private String pacienteLocalId;
    
    @JsonProperty("paciente_server_id")
    private Integer pacienteServerId;
    
    @JsonProperty("especialidade_server_id")
    private Integer especialidadeServerId;
    
    @JsonProperty("especialidade_local_id")
    private String especialidadeLocalId;
    
    @JsonProperty("data_atendimento")
    private String dataAtendimento;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("last_sync_timestamp")
    private Long lastSyncTimestamp;
    
    public PacienteEspecialidadeDTO() {}
    
    // Getters e Setters
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    
    public String getPacienteLocalId() { return pacienteLocalId; }
    public void setPacienteLocalId(String pacienteLocalId) { this.pacienteLocalId = pacienteLocalId; }
    
    public Integer getPacienteServerId() { return pacienteServerId; }
    public void setPacienteServerId(Integer pacienteServerId) { this.pacienteServerId = pacienteServerId; }
    
    public Integer getEspecialidadeServerId() { return especialidadeServerId; }
    public void setEspecialidadeServerId(Integer especialidadeServerId) { this.especialidadeServerId = especialidadeServerId; }
    
    public String getEspecialidadeLocalId() { return especialidadeLocalId; }
    public void setEspecialidadeLocalId(String especialidadeLocalId) { this.especialidadeLocalId = especialidadeLocalId; }
    
    public String getDataAtendimento() { return dataAtendimento; }
    public void setDataAtendimento(String dataAtendimento) { this.dataAtendimento = dataAtendimento; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public Long getLastSyncTimestamp() { return lastSyncTimestamp; }
    public void setLastSyncTimestamp(Long lastSyncTimestamp) { this.lastSyncTimestamp = lastSyncTimestamp; }
    
    /**
     * Métodos de compatibilidade com código existente
     * Mapeia os campos server_id para os getters esperados pelo DtoMapper
     */
    public Integer getPacienteId() {
        return pacienteServerId;
    }
    
    public Integer getEspecialidadeId() {
        return especialidadeServerId;
    }
    
    /**
     * Métodos para compatibilidade com código que faz set
     */
    public void setPacienteId(Integer pacienteId) {
        this.pacienteServerId = pacienteId;
    }
    
    public void setEspecialidadeId(Integer especialidadeId) {
        this.especialidadeServerId = especialidadeId;
    }
    
    /**
     * Método de conveniência para converter timestamp para data legível
     */
    public String getLastSyncAtFormatted() {
        if (lastSyncTimestamp != null) {
            return new java.util.Date(lastSyncTimestamp).toString();
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "PacienteEspecialidadeDTO{" +
                "isDeleted=" + isDeleted +
                ", pacienteServerId=" + pacienteServerId +
                ", especialidadeServerId=" + especialidadeServerId +
                ", dataAtendimento='" + dataAtendimento + '\'' +
                ", lastSyncTimestamp=" + lastSyncTimestamp +
                '}';
    }
}