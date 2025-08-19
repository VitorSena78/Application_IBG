package com.mycompany.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO para receber dados de Paciente da API Spring Boot
 * Corresponde exatamente ao formato JSON retornado pela API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PacienteDTO {
    
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
    
    @JsonProperty("pa_xmmhg")
    private String paXmmhg;
    
    @JsonProperty("fc_bpm")
    private Float fcBpm;
    
    @JsonProperty("fr_ibpm")
    private Float frIbpm;
    
    @JsonProperty("temperatura_c")
    private Float temperaturaC;
    
    @JsonProperty("hgt_mgld")
    private Float hgtMgld;
    
    private Float spo2;
    
    private Float peso;
    
    private Float altura;
    
    private Float imc;
    
    // Campos extras da API que não existem no modelo de domínio
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("sync_status")
    private String syncStatus;
    
    @JsonProperty("device_id")
    private String deviceId;
    
    @JsonProperty("local_id")
    private String localId;
    
    @JsonProperty("last_sync_at")
    private String lastSyncAt;
    
    // Lista de especialidades (relacionamento)
    private List<PacienteEspecialidadeDTO> especialidades;
    
    // Construtor padrão
    public PacienteDTO() {}
    
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
    
    public String getDataNascimento() {
        return dataNascimento;
    }
    
    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
    public Integer getIdade() {
        return idade;
    }
    
    public void setIdade(Integer idade) {
        this.idade = idade;
    }
    
    public String getNomeDaMae() {
        return nomeDaMae;
    }
    
    public void setNomeDaMae(String nomeDaMae) {
        this.nomeDaMae = nomeDaMae;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public String getSus() {
        return sus;
    }
    
    public void setSus(String sus) {
        this.sus = sus;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public String getPaXmmhg() {
        return paXmmhg;
    }
    
    public void setPaXmmhg(String paXmmhg) {
        this.paXmmhg = paXmmhg;
    }
    
    public Float getFcBpm() {
        return fcBpm;
    }
    
    public void setFcBpm(Float fcBpm) {
        this.fcBpm = fcBpm;
    }
    
    public Float getFrIbpm() {
        return frIbpm;
    }
    
    public void setFrIbpm(Float frIbpm) {
        this.frIbpm = frIbpm;
    }
    
    public Float getTemperaturaC() {
        return temperaturaC;
    }
    
    public void setTemperaturaC(Float temperaturaC) {
        this.temperaturaC = temperaturaC;
    }
    
    public Float getHgtMgld() {
        return hgtMgld;
    }
    
    public void setHgtMgld(Float hgtMgld) {
        this.hgtMgld = hgtMgld;
    }
    
    public Float getSpo2() {
        return spo2;
    }
    
    public void setSpo2(Float spo2) {
        this.spo2 = spo2;
    }
    
    public Float getPeso() {
        return peso;
    }
    
    public void setPeso(Float peso) {
        this.peso = peso;
    }
    
    public Float getAltura() {
        return altura;
    }
    
    public void setAltura(Float altura) {
        this.altura = altura;
    }
    
    public Float getImc() {
        return imc;
    }
    
    public void setImc(Float imc) {
        this.imc = imc;
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
    
    public String getSyncStatus() {
        return syncStatus;
    }
    
    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getLocalId() {
        return localId;
    }
    
    public void setLocalId(String localId) {
        this.localId = localId;
    }
    
    public String getLastSyncAt() {
        return lastSyncAt;
    }
    
    public void setLastSyncAt(String lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }
    
    public List<PacienteEspecialidadeDTO> getEspecialidades() {
        return especialidades;
    }
    
    public void setEspecialidades(List<PacienteEspecialidadeDTO> especialidades) {
        this.especialidades = especialidades;
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
                '}';
    }
}