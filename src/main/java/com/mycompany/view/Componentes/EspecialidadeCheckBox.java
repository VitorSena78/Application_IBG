/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.view.Componentes;

import com.mycompany.model.bean.Especialidade;

/**
 * Classe interna para representar uma especialidade com checkbox
 * @author vitor
 */
public class EspecialidadeCheckBox {
    private Especialidade especialidade;
    private boolean selecionada;

    public EspecialidadeCheckBox(Especialidade especialidade) {
        this.especialidade = especialidade;
        this.selecionada = false;
    }

    // getters e setters
    public Especialidade getEspecialidade() { return especialidade; }
    public boolean isSelecionada() { return selecionada; }
    public void setSelecionada(boolean selecionada) { this.selecionada = selecionada; }

    @Override
    public String toString() {
        return especialidade.getNome();
    }
}
