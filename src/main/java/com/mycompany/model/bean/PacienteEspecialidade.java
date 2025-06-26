package com.mycompany.model.bean;

import java.util.Date;


/**
 *
 * @author vitor
 */
public class PacienteEspecialidade {
    
    private int pacienteId;
    private int especialidadeId;
    private Date dataAtendimento;

    public PacienteEspecialidade() {
        // Construtor padrão
    }
    
    // Getters e setters

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public int getEspecialidadeId() {
        return especialidadeId;
    }

    public void setEspecialidadeId(int especialidadeId) {
        this.especialidadeId = especialidadeId;
    }

    public Date getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(Date dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    @Override
    public String toString() {
        return "PacienteEspecialidade{" + "pacienteId=" + pacienteId + ", especialidadeId=" + especialidadeId + ", dataAtendimento=" + dataAtendimento + '}';
    }

    
    
}
