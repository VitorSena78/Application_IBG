package com.mycompany.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;

/**
 * DTO corrigido para corresponder EXATAMENTE ao formato da API Spring Boot
 * Baseado na análise do JSON de resposta real da API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PacienteDTO {
    
    // IMPORTANTE: A API retorna "server_id", não "id"
    @JsonProperty("server_id")
    private Integer id;
    
    private String nome;
    
    @JsonProperty("data_nascimento")
    private String dataNascimento;
    
    private Integer idade;
    
    @JsonProperty("nome_da_mae")
    private String nomeDaMae;
    
    private String cpf;
    private String sus;
    private String telefone;
    
    @JsonProperty("endereço")
    private String endereco;
    
    @JsonProperty("pa_x_mmhg")
    private String paXMmhg;
    
    // CRÍTICO: A API retorna todos esses campos como Double, não Integer
    @JsonProperty("fc_bpm")
    private Double fcBpm;
    
    @JsonProperty("fr_ibpm")
    private Double frIbpm;
    
    @JsonProperty("temperatura_c")
    private Double temperaturaC;
    
    @JsonProperty("hgt_mgld")
    private Double hgtMgld;
    
    private Double spo2;
    private Double peso;
    private Double altura;
    private Double imc;
    
    // Campos de auditoria e sincronização
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("sync_status")
    private String syncStatus;
    
    // IMPORTANTE: A API retorna "last_sync_timestamp", não "last_sync_at"
    @JsonProperty("last_sync_timestamp")
    private Long lastSyncTimestamp;
    
    // Campos adicionais que podem ser úteis (mantidos para compatibilidade)
    @JsonProperty("device_id")
    private String deviceId;
    
    @JsonProperty("local_id")
    private String localId;
    
    // Lista de especialidades (flexível - pode não estar presente na resposta atual)
    private List<PacienteEspecialidadeDTO> especialidades;
    
    public PacienteDTO() {}
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }
    
    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
    
    public String getNomeDaMae() { return nomeDaMae; }
    public void setNomeDaMae(String nomeDaMae) { this.nomeDaMae = nomeDaMae; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getSus() { return sus; }
    public void setSus(String sus) { this.sus = sus; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    
    public String getPaXMmhg() { return paXMmhg; }
    public void setPaXMmhg(String paXMmhg) { this.paXMmhg = paXMmhg; }
    
    public Double getFcBpm() { return fcBpm; }
    public void setFcBpm(Double fcBpm) { this.fcBpm = fcBpm; }
    
    public Double getFrIbpm() { return frIbpm; }
    public void setFrIbpm(Double frIbpm) { this.frIbpm = frIbpm; }
    
    public Double getTemperaturaC() { return temperaturaC; }
    public void setTemperaturaC(Double temperaturaC) { this.temperaturaC = temperaturaC; }
    
    public Double getHgtMgld() { return hgtMgld; }
    public void setHgtMgld(Double hgtMgld) { this.hgtMgld = hgtMgld; }
    
    public Double getSpo2() { return spo2; }
    public void setSpo2(Double spo2) { this.spo2 = spo2; }
    
    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
    
    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }
    
    public Double getImc() { return imc; }
    public void setImc(Double imc) { this.imc = imc; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    
    public Long getLastSyncTimestamp() { return lastSyncTimestamp; }
    public void setLastSyncTimestamp(Long lastSyncTimestamp) { this.lastSyncTimestamp = lastSyncTimestamp; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }
    
    public List<PacienteEspecialidadeDTO> getEspecialidades() {
        return especialidades != null ? especialidades : new ArrayList<>();
    }
    
    public void setEspecialidades(List<PacienteEspecialidadeDTO> especialidades) {
        this.especialidades = especialidades;
    }
    
    /**
     * Método de conveniência para obter apenas os IDs das especialidades
     */
    public List<Integer> getEspecialidadeIds() {
        List<Integer> ids = new ArrayList<>();
        if (especialidades != null) {
            for (PacienteEspecialidadeDTO esp : especialidades) {
                Integer id = esp.getEspecialidadeId();
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        return ids;
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
        return "PacienteDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataNascimento='" + dataNascimento + '\'' +
                ", idade=" + idade +
                ", cpf='" + cpf + '\'' +
                ", syncStatus='" + syncStatus + '\'' +
                ", lastSyncTimestamp=" + lastSyncTimestamp +
                ", especialidades=" + (especialidades != null ? especialidades.size() : 0) +
                '}';
    }
}