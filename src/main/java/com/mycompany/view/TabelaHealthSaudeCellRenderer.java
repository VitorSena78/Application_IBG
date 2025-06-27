package com.mycompany.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

// Renderer personalizado para destacar valores críticos
public class TabelaHealthSaudeCellRenderer extends DefaultTableCellRenderer {
    
    private static final Color ROW_COLOR_1 = new Color(248, 250, 252);
    private static final Color ROW_COLOR_2 = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(51, 65, 85);
    private static final Color CRITICAL_COLOR = new Color(239, 68, 68);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Cores alternadas para as linhas
        if (!isSelected) {
            if (row % 2 == 0) {
                c.setBackground(ROW_COLOR_1);
            } else {
                c.setBackground(ROW_COLOR_2);
            }
        }

        c.setForeground(TEXT_COLOR);
        setFont(new Font("Segoe UI", Font.PLAIN, 11));
        setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        // Destacar valores críticos com cores
        if (value != null && column > 0) { // Não aplicar cor na coluna nome
            String stringValue = value.toString();
            Color bgColor = getHealthStatusColor(column, stringValue);

            if (bgColor != null && !isSelected) {
                c.setBackground(bgColor);
                c.setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 11));
            }
        }

        // Primeira coluna (nome) com formatação especial
        if (column == 0) {
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            c.setForeground(new Color(30, 58, 138));
        }

        return c;
    }
        
    private Color getHealthStatusColor(int column, String value) {
        try {
            switch (column) {
                case 1: // Pressão arterial
                    if (value.contains("/")) {
                        String[] parts = value.split("/");
                        int sistolic = Integer.parseInt(parts[0]);
                        int diastolic = Integer.parseInt(parts[1]);
                        if (sistolic >= 140 || diastolic >= 90) return CRITICAL_COLOR;
                        if (sistolic >= 130 || diastolic >= 80) return WARNING_COLOR;
                    }
                    break;
                case 2: // Frequência cardíaca
                    int fc = Integer.parseInt(value);
                    if (fc > 100 || fc < 60) return CRITICAL_COLOR;
                    if (fc > 90 || fc < 65) return WARNING_COLOR;
                    break;
                case 4: // Temperatura
                    double temp = Double.parseDouble(value);
                    if (temp >= 38.0 || temp <= 35.0) return CRITICAL_COLOR;
                    if (temp >= 37.5 || temp <= 35.5) return WARNING_COLOR;
                    break;
                case 5: // Glicemia
                    int glicemia = Integer.parseInt(value);
                    if (glicemia >= 200 || glicemia <= 70) return CRITICAL_COLOR;
                    if (glicemia >= 140 || glicemia <= 80) return WARNING_COLOR;
                    break;
                case 6: // Saturação O2
                    int sat = Integer.parseInt(value);
                    if (sat < 95) return CRITICAL_COLOR;
                    if (sat < 97) return WARNING_COLOR;
                    break;
                case 9: // IMC
                    double imc = Double.parseDouble(value);
                    if (imc >= 30.0 || imc < 18.5) return CRITICAL_COLOR;
                    if (imc >= 25.0 || imc < 20.0) return WARNING_COLOR;
                    break;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Ignorar erros de parsing
        }
        return null;
    }
        
}
