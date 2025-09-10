package com.mycompany.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PacienteEspecialidadeDTO {
    
    // Campos principais - usando os mesmos nomes do servidor
    @JsonProperty("paciente_id")
    private Integer pacienteId;
    
    @JsonProperty("especialidade_id")
    private Integer especialidadeId;
    
    // Campos de sincronização
    @JsonProperty("paciente_local_id")
    private String pacienteLocalId;
    
    @JsonProperty("especialidade_local_id")
    private String especialidadeLocalId;
    
    @JsonProperty("paciente_server_id")
    private Integer pacienteServerId;
    
    @JsonProperty("especialidade_server_id")
    private Integer especialidadeServerId;
    
    // Outros campos
    @JsonProperty("data_atendimento")
    private String dataAtendimento;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("is_deleted")
    private Boolean isDeleted;
    
    @JsonProperty("last_sync_timestamp")
    private Long lastSyncTimestamp;
    
    // Construtores
    public PacienteEspecialidadeDTO() {}
    
    public PacienteEspecialidadeDTO(Integer pacienteId, Integer especialidadeId, String dataAtendimento) {
        this.pacienteId = pacienteId;
        this.especialidadeId = especialidadeId;
        this.dataAtendimento = dataAtendimento;
    }
    
    // Getters e Setters
    public Integer getPacienteId() { return pacienteId; }
    public void setPacienteId(Integer pacienteId) { this.pacienteId = pacienteId; }
    
    public Integer getEspecialidadeId() { return especialidadeId; }
    public void setEspecialidadeId(Integer especialidadeId) { this.especialidadeId = especialidadeId; }
    
    public String getPacienteLocalId() { return pacienteLocalId; }
    public void setPacienteLocalId(String pacienteLocalId) { this.pacienteLocalId = pacienteLocalId; }
    
    public String getEspecialidadeLocalId() { return especialidadeLocalId; }
    public void setEspecialidadeLocalId(String especialidadeLocalId) { this.especialidadeLocalId = especialidadeLocalId; }
    
    public Integer getPacienteServerId() { return pacienteServerId; }
    public void setPacienteServerId(Integer pacienteServerId) { 
        this.pacienteServerId = pacienteServerId;
        // Sincronizar com o campo principal se não estiver definido
        if (this.pacienteId == null) {
            this.pacienteId = pacienteServerId;
        }
    }
    
    public Integer getEspecialidadeServerId() { return especialidadeServerId; }
    public void setEspecialidadeServerId(Integer especialidadeServerId) { 
        this.especialidadeServerId = especialidadeServerId;
        // Sincronizar com o campo principal se não estiver definido
        if (this.especialidadeId == null) {
            this.especialidadeId = especialidadeServerId;
        }
    }
    
    public String getDataAtendimento() { return dataAtendimento; }
    public void setDataAtendimento(String dataAtendimento) { this.dataAtendimento = dataAtendimento; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    
    public Long getLastSyncTimestamp() { return lastSyncTimestamp; }
    public void setLastSyncTimestamp(Long lastSyncTimestamp) { this.lastSyncTimestamp = lastSyncTimestamp; }
    
    @Override
    public String toString() {
        return "PacienteEspecialidadeDTO{" +
                "pacienteId=" + pacienteId +
                ", especialidadeId=" + especialidadeId +
                ", dataAtendimento='" + dataAtendimento + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}