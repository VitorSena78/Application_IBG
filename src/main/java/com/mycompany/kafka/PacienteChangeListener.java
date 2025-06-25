package com.mycompany.kafka;

import com.mycompany.model.bean.Paciente;

/**
 *
 * @author vitor
 */
public interface PacienteChangeListener {
    void onPacienteAdded(Paciente paciente);
    void onPacienteUpdated(Paciente paciente);
    void onPacienteDeleted(int pacienteId);
}
