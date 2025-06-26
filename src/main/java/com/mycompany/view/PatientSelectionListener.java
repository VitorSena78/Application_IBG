package com.mycompany.view;

import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import java.util.List;

/**
 *
 * @author vitor
 */
public interface PatientSelectionListener {
    
    void onPatientSelected(Paciente patientData);
    void onPatientSelected(Paciente patientData, List<PacienteEspecialidade> pacienteEspecialidadeData);
    
    
}
