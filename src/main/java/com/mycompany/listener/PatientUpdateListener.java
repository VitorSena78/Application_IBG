/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
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
