package com.mycompany.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

// Renderer personalizado para destacar valores críticos
public class TabelaHealthDataCellRenderer2 extends DefaultTableCellRenderer {
    
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
            Color bgColor = getPatientDataStatusColor(column, stringValue);

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
        
        
    private Color getPatientDataStatusColor(int column, String value) {
        try {
            switch (column) {
                case 1: // Data de Nascimento
                    return validateDateOfBirth(value);

                case 2: // Idade
                    return validateAge(value);

                case 4: // CPF
                    return validateCPF(value);

                case 5: // SUS
                    return validateSUS(value);

                case 6: // Telefone
                    return validatePhone(value);

                case 7: // Endereço
                    return validateAddress(value);

                default:
                    return null; // Para Nome, Nome da Mãe e ID não aplicamos validação visual
            }
        } catch (Exception e) {
            return CRITICAL_COLOR; // Se houver erro na validação, marcar como crítico
        }
    }

    // Método auxiliar para validar data de nascimento
    private Color validateDateOfBirth(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return CRITICAL_COLOR; // Data obrigatória
        }

        try {
            // Verificar formato dd/MM/yyyy
            if (!dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return CRITICAL_COLOR; // Formato inválido
            }

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            java.util.Date birthDate = sdf.parse(dateStr);

            // Verificar se a data não é futura
            if (birthDate.after(new java.util.Date())) {
                return CRITICAL_COLOR; // Data futura
            }

            // Verificar idade razoável (0-120 anos)
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.YEAR, -120);
            if (birthDate.before(cal.getTime())) {
                return WARNING_COLOR; // Muito antigo, pode ser erro
            }

        } catch (java.text.ParseException e) {
            return CRITICAL_COLOR; // Erro de parsing
        }

        return null; // Data válida
    }

    // Método auxiliar para validar idade
    private Color validateAge(String ageStr) {
        if (ageStr == null || ageStr.trim().isEmpty()) {
            return CRITICAL_COLOR; // Idade obrigatória
        }

        try {
            int age = Integer.parseInt(ageStr.trim());
            if (age < 0 || age > 120) {
                return CRITICAL_COLOR; // Idade impossível
            }
            if (age > 100) {
                return WARNING_COLOR; // Idade muito avançada, verificar
            }
        } catch (NumberFormatException e) {
            return CRITICAL_COLOR; // Não é um número válido
        }

        return null; // Idade válida
    }

    // Método auxiliar para validar CPF
    private Color validateCPF(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return CRITICAL_COLOR; // CPF obrigatório
        }

        // Remover formatação
        String cpfNumbers = cpf.replaceAll("[^0-9]", "");

        // Verificar se tem 11 dígitos
        if (cpfNumbers.length() != 11) {
            return CRITICAL_COLOR; // CPF deve ter 11 dígitos
        }

        // Verificar se não são todos os dígitos iguais
        if (cpfNumbers.matches("(\\d)\\1{10}")) {
            return CRITICAL_COLOR; // CPF inválido (todos iguais)
        }

        // Aqui você pode adicionar validação completa do CPF se necessário
        // Por simplicidade, vamos apenas verificar o formato
        if (!cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            return WARNING_COLOR; // Formato esperado: 000.000.000-00
        }

        return null; // CPF válido
    }

    // Método auxiliar para validar cartão SUS
    private Color validateSUS(String sus) {
        if (sus == null || sus.trim().isEmpty()) {
            return WARNING_COLOR; // SUS não é obrigatório, mas é recomendado
        }

        // Remover formatação
        String susNumbers = sus.replaceAll("[^0-9]", "");

        // Cartão SUS deve ter 15 dígitos
        if (susNumbers.length() != 15) {
            return CRITICAL_COLOR; // Número de dígitos incorreto
        }

        // Verificar se começa com os códigos válidos (7, 8 ou 9)
        if (!susNumbers.startsWith("7") && !susNumbers.startsWith("8") && !susNumbers.startsWith("9")) {
            return WARNING_COLOR; // Pode ser um formato antigo ou incorreto
        }

        return null; // SUS válido
    }

    // Método auxiliar para validar telefone
    private Color validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return WARNING_COLOR; // Telefone não é obrigatório, mas é importante
        }

        // Remover formatação
        String phoneNumbers = phone.replaceAll("[^0-9]", "");

        // Verificar se tem 10 ou 11 dígitos (fixo ou celular)
        if (phoneNumbers.length() < 10 || phoneNumbers.length() > 11) {
            return CRITICAL_COLOR; // Número de dígitos incorreto
        }

        // Verificar formato brasileiro: (XX) XXXXX-XXXX ou (XX) XXXX-XXXX
        if (!phone.matches("\\(\\d{2}\\) \\d{4,5}-\\d{4}")) {
            return WARNING_COLOR; // Formato recomendado não seguido
        }

        return null; // Telefone válido
    }

    // Método auxiliar para validar endereço
    private Color validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return CRITICAL_COLOR; // Endereço obrigatório
        }

        // Verificar se tem pelo menos 10 caracteres (endereço muito curto é suspeito)
        if (address.trim().length() < 10) {
            return WARNING_COLOR; // Endereço muito curto
        }

        // Verificar se contém pelo menos uma vírgula ou hífen (separador típico)
        if (!address.contains(",") && !address.contains("-")) {
            return WARNING_COLOR; // Formato de endereço incompleto
        }

        return null; // Endereço válido
    }
}
