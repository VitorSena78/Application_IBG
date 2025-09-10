package com.mycompany.listener;

import com.mycompany.model.bean.Paciente;

/**
 *
 * @author vitor
 */
public interface PatientUpdateListener {
    void onPatientUpdated(Paciente pacienteAtualizado);
    void onPatientDeleted(int pacienteId);
}
