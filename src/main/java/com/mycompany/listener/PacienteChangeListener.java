package com.mycompany.listener;

import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import java.util.List;

/**
 *
 * @author vitor
 */
public interface PacienteChangeListener {
    void onPacienteAdded(Paciente paciente);
    void onPacienteUpdated(Paciente paciente);
    void onPacienteDeleted(int pacienteId);
}
