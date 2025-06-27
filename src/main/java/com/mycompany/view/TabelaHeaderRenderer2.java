package com.mycompany.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

// Renderer personalizado para o cabeçalho que força as cores
public class TabelaHeaderRenderer2 extends DefaultTableCellRenderer {
    
    private static final Color HEADER_COLOR = new Color(41, 98, 255);
    private static final Color HEADER_TEXT_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    public TabelaHeaderRenderer2() {
            setHorizontalAlignment(JLabel.CENTER);
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Forçar as cores do cabeçalho
            c.setBackground(HEADER_COLOR);
            c.setForeground(HEADER_TEXT_COLOR);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 1, BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            setOpaque(true);
            
            return c;
        }
    
}
