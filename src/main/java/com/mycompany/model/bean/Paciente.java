package com.mycompany.model.bean;

/**
 *
 * @author vitor
 */
public class Paciente {
    
    private Integer id;                   // Identificador único do paciente
    private String nome;              // Nome completo do paciente
    private String dataNascimento;    // Data de nascimento
    private Integer idade;            // Idade calculada a partir da data de nascimento
    private String nomeDaMae;         // Nome da mãe do paciente
    private String cpf;               // CPF do paciente (Cadastro de Pessoa Física)
    private String sus;               // Número do cartão SUS (Sistema Único de Saúde)
    private String telefone;          // Telefone de contato do paciente
    private String endereco;          // Endereço residencial do paciente
    private String paXMmhg;           // Pressão arterial no formato sistólica/diastólica (ex: "120/80 mmHg")
    private float fcBpm;              // Frequência cardíaca em batimentos por minuto (bpm)
    private float frIbpm;             // Frequência respiratória em movimentos por minuto (rpm)
    private float temperaturaC;       // Temperatura corporal em graus Celsius
    private float hgtMgld;            // Glicemia capilar (Hemoglucoteste) em mg/dL
    private float spo2;               // Saturação de oxigênio no sangue (SpO2) em porcentagem (%)
    private float peso;               // Peso corporal em quilogramas (kg)
    private float altura;             // Altura em metros (m)
    private float imc;                // Índice de Massa Corporal calculado (IMC = peso / altura²)

    public Paciente() {
        // Construtor padrão
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

    public String getPaXMmhg() {
        return paXMmhg;
    }

    public void setPaXMmhg(String paXMmhg) {
        this.paXMmhg = paXMmhg;
    }

    public Float getFcBpm() {
        return fcBpm;
    }

    public void setFcBpm(Float fcBpm) {
        this.fcBpm = (fcBpm != null) ? fcBpm : 0.0f;
    }

    public Float getFrIbpm() {
        return frIbpm;
    }

    public void setFrIbpm(Float frIbpm) {
        this.frIbpm = (frIbpm != null) ? frIbpm : 0.0f;
    }

    public Float getTemperaturaC() {
        return temperaturaC;
    }

    public void setTemperaturaC(Float temperaturaC) {
        this.temperaturaC = (temperaturaC != null) ? temperaturaC : 0.0f;
    }

    public Float getHgtMgld() {
        return hgtMgld;
    }

    public void setHgtMgld(Float hgtMgld) {
        this.hgtMgld = (hgtMgld != null) ? hgtMgld : 0.0f;
    }

    public Float getSpo2() {
        return spo2;
    }

    public void setSpo2(Float spo2) {
        this.spo2 = (spo2 != null) ? spo2 : 0.0f;
    }

    public Float getPeso() {
        return peso;
    }

    public void setPeso(Float peso) {
        this.peso = (peso != null) ? peso : 0.0f;
    }

    public Float getAltura() {
        return altura;
    }

    public void setAltura(Float altura) {
        this.altura = (altura != null) ? altura : 0.0f;
    }

    public Float getImc() {
        return imc;
    }

    public void setImc(Float imc) {
        this.imc = (imc != null) ? imc : 0.0f;
    }
    
    @Override
    public String toString() {
        return "Paciente{" +
                "\n  ID: " + id +
                "\n  Nome: " + (nome != null ? nome : "N/A") +
                "\n  Data de Nascimento: " + (dataNascimento != null ? dataNascimento : "N/A") +
                "\n  Idade: " + (idade != null ? idade + " anos" : "N/A") +
                "\n  Nome da Mãe: " + (nomeDaMae != null ? nomeDaMae : "N/A") +
                "\n  CPF: " + (cpf != null ? cpf : "N/A") +
                "\n  SUS: " + (sus != null ? sus : "N/A") +
                "\n  Telefone: " + (telefone != null ? telefone : "N/A") +
                "\n  Endereço: " + (endereco != null ? endereco : "N/A") +
                "\n  Pressão Arterial: " + (paXMmhg != null ? paXMmhg : "N/A") +
                "\n  Frequência Cardíaca: " + (fcBpm > 0 ? fcBpm + " bpm" : "N/A") +
                "\n  Frequência Respiratória: " + (frIbpm > 0 ? frIbpm + " ipm" : "N/A") +
                "\n  Temperatura: " + (temperaturaC > 0 ? temperaturaC + " °C" : "N/A") +
                "\n  Glicemia: " + (hgtMgld > 0 ? hgtMgld + " mg/dL" : "N/A") +
                "\n  SpO2: " + (spo2 > 0 ? spo2 + " %" : "N/A") +
                "\n  Peso: " + (peso > 0 ? peso + " kg" : "N/A") +
                "\n  Altura: " + (altura > 0 ? altura + " m" : "N/A") +
                "\n  IMC: " + (imc > 0 ? String.format("%.2f", imc) : "N/A") +
                "\n}";
    }
    
}
