package com.mycompany.listener;

import com.mycompany.model.bean.PacienteEspecialidade;
import java.util.List;

/**
 * Interface para escutar mudanças na tabela Paciente_has_Especialidade
 * @author vitor
 */
public interface PacienteEspecialidadeChangeListener {
    
    void onPacienteEspecialidadeAdded(PacienteEspecialidade pacienteEspecialidade);
    void onPacienteEspecialidadeUpdated(PacienteEspecialidade pacienteEspecialidade);
    void onPacienteEspecialidadeDeleted(Integer pacienteId, Integer especialidadeId);
    
    // Para lidar com operações em batch
    default void onPacienteEspecialidadeBatchCreated(Integer pacienteId, List<PacienteEspecialidade> associacoesList) {
        // Implementação padrão - chama o método individual para cada associação
        if (associacoesList != null) {
            for (PacienteEspecialidade associacao : associacoesList) {
                onPacienteEspecialidadeAdded(associacao);
            }
        }
    }
    
    default void onPacienteEspecialidadeBatchDeleted(Integer pacienteId, List<PacienteEspecialidade> associacoesDeletadas) {
        // Implementação padrão - chama o método individual para cada associação
        if (associacoesDeletadas != null) {
            for (PacienteEspecialidade associacao : associacoesDeletadas) {
                onPacienteEspecialidadeDeleted(pacienteId, associacao.getEspecialidadeId());
            }
        }
    }
    
    // Para atualização completa das associações de um paciente
    default void onPacienteEspecialidadeCompleteUpdate(Integer pacienteId, List<PacienteEspecialidade> novasAssociacoes) {
        System.out.println("🔄 Atualização completa das associações do paciente " + pacienteId + 
                          ": " + (novasAssociacoes != null ? novasAssociacoes.size() : 0) + " associações");
    }
}