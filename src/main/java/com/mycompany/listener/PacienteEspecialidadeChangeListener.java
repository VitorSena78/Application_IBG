package com.mycompany.listener;

import com.mycompany.model.bean.PacienteEspecialidade;

/**
 * Interface para escutar mudanças na tabela Paciente_has_Especialidade
 * @author vitor
 */
public interface PacienteEspecialidadeChangeListener {
    
    void onPacienteEspecialidadeAdded(PacienteEspecialidade pacienteEspecialidade);
    void onPacienteEspecialidadeUpdated(PacienteEspecialidade pacienteEspecialidade);
    void onPacienteEspecialidadeDeleted(Integer pacienteId, Integer especialidadeId);
}