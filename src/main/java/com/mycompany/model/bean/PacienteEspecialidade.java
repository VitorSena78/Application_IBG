package com.mycompany.model.bean;


/**
 *
 * @author vitor
 */
public class PacienteEspecialidade {
    
    private Integer pacienteId;
    private Integer especialidadeId;
    private String dataAtendimento;

    public PacienteEspecialidade() {
        // Construtor padrão
    }
    
    // Getters e setters

    public Integer getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Integer getEspecialidadeId() {
        return especialidadeId;
    }

    public void setEspecialidadeId(int especialidadeId) {
        this.especialidadeId = especialidadeId;
    }

    public String getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(String dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    @Override
    public String toString() {
        return "PacienteEspecialidade{" + "pacienteId=" + pacienteId + ", especialidadeId=" + especialidadeId + ", dataAtendimento=" + dataAtendimento + '}';
    }
    
    
}
