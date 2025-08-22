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
    
    public boolean isValid() {
        return pacienteId != null && 
               especialidadeId != null && 
               pacienteId > 0 && 
               especialidadeId > 0;
    }

    @Override
    public String toString() {
        return "PacienteEspecialidade{" + 
               "pacienteId=" + (pacienteId != null ? pacienteId : "N/A") + 
               ", especialidadeId=" + (especialidadeId != null ? especialidadeId : "N/A") + 
               ", dataAtendimento='" + getDataAtendimento() + "'" + 
               '}';
    }

}
