/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.view.Componentes;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Renderer customizado para mostrar checkboxes na JList
 * @author vitor
 */
public class EspecialidadeListCellRenderer extends JCheckBox implements ListCellRenderer<EspecialidadeCheckBox> {

    @Override
    public Component getListCellRendererComponent(
            JList<? extends EspecialidadeCheckBox> list,
            EspecialidadeCheckBox value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        setComponentOrientation(list.getComponentOrientation());

        setFont(new Font("Arial", 0, 12));
        setText(value.toString());
        setSelected(value.isSelecionada());

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setEnabled(list.isEnabled());
        setOpaque(true);

        return this;
    }
}
