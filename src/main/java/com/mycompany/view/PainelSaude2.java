package com.mycompany.view;

import com.mycompany.model.bean.Paciente;
import com.mycompany.model.dao.PacienteDAO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.util.List;
import java.util.ArrayList;

public class PainelSaude2 extends javax.swing.JPanel {
    
    private static final Color HEADER_COLOR = new Color(41, 98, 255);
    private static final Color HEADER_TEXT_COLOR = Color.WHITE;
    private static final Color ROW_COLOR_1 = new Color(248, 250, 252);
    private static final Color ROW_COLOR_2 = Color.WHITE;
    private static final Color SELECTION_COLOR = new Color(59, 130, 246, 50);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_COLOR = new Color(51, 65, 85);
    private static final Color CRITICAL_COLOR = new Color(239, 68, 68);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color NORMAL_COLOR = new Color(34, 197, 94);
    
    // Cores para a barra de rolagem personalizada
    private static final Color SCROLLBAR_TRACK_COLOR = new Color(248, 250, 252);
    private static final Color SCROLLBAR_THUMB_COLOR = new Color(203, 213, 225);
    private static final Color SCROLLBAR_THUMB_HOVER_COLOR = new Color(148, 163, 184);
    private static final Color SCROLLBAR_THUMB_PRESSED_COLOR = new Color(100, 116, 139);
    
    private PatientSelectionListener patientSelectionListener;
    
    DefaultTableModel tableModel;
    private List<Paciente> pacientes;
    
    public PainelSaude2(List<Paciente> pacientes) {
        initComponents();
        tableModel = (DefaultTableModel) jTable1.getModel();
        setupTableDesign();
        this.pacientes = pacientes;
        loadPacientes();
        setOpaque(false);
    }
    
    public void setPatientSelectionListener(PatientSelectionListener listener) {
        this.patientSelectionListener = listener;
    }
    
    private void setupTableDesign() {
        // Configuração do cabeçalho com renderer personalizado para forçar as cores
        JTableHeader header = jTable1.getTableHeader();
        header.setDefaultRenderer(new TabelaHeaderRenderer2());
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createEmptyBorder());
        
        // Configuração da tabela com propriedades específicas do Nimbus
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jTable1.setRowHeight(35);
        jTable1.setGridColor(BORDER_COLOR);
        jTable1.setSelectionBackground(SELECTION_COLOR);
        jTable1.setSelectionForeground(TEXT_COLOR);
        jTable1.setShowVerticalLines(true);
        jTable1.setShowHorizontalLines(true);
        jTable1.setIntercellSpacing(new Dimension(1, 1));
        
        // Forçar o fundo da tabela para sobrescrever o Nimbus
        jTable1.setOpaque(true);
        jTable1.setBackground(Color.WHITE);
        
        // Renderer personalizado para as células
        jTable1.setDefaultRenderer(Object.class, new TabelaHealthSaudeCellRenderer());
        
        // Configuração das larguras das colunas
        setupColumnWidths();
        
        // Ocultar a coluna ID (última coluna - índice 10)
        hideIdColumn();
        
        // Configuração do scroll pane com barra de rolagem personalizada
        setupCustomScrollPane();
        
        // Configuração do scroll pane
        jScrollPane1.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1 && patientSelectionListener != null) {
                    // Obter dados da linha selecionada
                    Paciente patientData = getPatientData(selectedRow);
                    
                    //Bãusca paienete da linha(Row) na lista
                    patientData = buscaPaciente(patientData);
                    
                    // Notificar o listener
                    patientSelectionListener.onPatientSelected(patientData);
                }
            }
        });
        
        // Adicionar efeito hover
        jTable1.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = jTable1.rowAtPoint(e.getPoint());
                if (row != jTable1.getSelectedRow()) {
                    jTable1.clearSelection();
                    jTable1.setRowSelectionInterval(row, row);
                }
            }
        });
    }
    
    private Paciente buscaPaciente(Paciente patientData) {
        
        int inicio = 0;
        int fim = pacientes.size() - 1;
        int centro;
        
        do{
            centro = (inicio + fim)/2;
            if(patientData.getId()<pacientes.get(centro).getId()){
                fim = centro - 1;
                
            }else if(patientData.getId()>pacientes.get(centro).getId()){
                inicio = centro + 1;
            }else{
                return pacientes.get(centro);
            }
            
        }while (inicio<=fim);
        
        System.out.println("Paciente não encontrado");
        return patientData;
    }
    
    private void setupCustomScrollPane() {
        // Configuração básica do scroll pane
        jScrollPane1.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        
        // Personalizar a barra de rolagem vertical
        JScrollBar verticalScrollBar = jScrollPane1.getVerticalScrollBar();
        verticalScrollBar.setUI(new ModernScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(12, 0));
        verticalScrollBar.setBackground(SCROLLBAR_TRACK_COLOR);
        
        // Personalizar a barra de rolagem horizontal
        JScrollBar horizontalScrollBar = jScrollPane1.getHorizontalScrollBar();
        horizontalScrollBar.setUI(new ModernScrollBarUI());
        horizontalScrollBar.setPreferredSize(new Dimension(0, 12));
        horizontalScrollBar.setBackground(SCROLLBAR_TRACK_COLOR);
        
        // Configurações adicionais do scroll pane
        jScrollPane1.setBackground(Color.WHITE);
        jScrollPane1.getViewport().setOpaque(true);
        
        // Remover bordas desnecessárias
        jScrollPane1.setViewportBorder(null);
        jScrollPane1.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }
    
    private void setupColumnWidths() {
        // Agora temos 11 colunas (incluindo a coluna ID oculta)
        int[] columnWidths = {120, 80, 60, 70, 90, 80, 100, 80, 80, 60, 0}; // Última coluna (ID) com largura 0
        String[] columnNames = {
            "Nome do Paciente", "Pressão (mmHg)", "FC (bpm)", "FR (irpm)", 
            "Temp (°C)", "Glicemia (mg/dL)", "Sat O₂ (%)", "Peso (kg)", 
            "Altura (m)", "IMC", "ID"
        };
        
        for (int i = 0; i < columnWidths.length && i < jTable1.getColumnCount(); i++) {
            TableColumn column = jTable1.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
            column.setHeaderValue(columnNames[i]);
        }
    }
    
    // Método para ocultar a coluna ID
    private void hideIdColumn() {
        TableColumn idColumn = jTable1.getColumnModel().getColumn(10); // Coluna ID (índice 10)
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setPreferredWidth(0);
        idColumn.setWidth(0);
    }
    
    // Método para obter o ID do paciente da linha selecionada
    public String getSelectedPatientId() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            Object idValue = tableModel.getValueAt(selectedRow, 10); // Coluna ID
            return idValue != null ? idValue.toString() : null;
        }
        return null;
    }
    
    //Metodo para adicionar exemplos de pacientes a tabela
    private void populateWithSampleData() {
        tableModel.setRowCount(0); // Limpar dados existentes
        
        // Dados de exemplo com diferentes estados de saúde (agora incluindo ID)
        Object[][] sampleData = {
            {"Maria Silva", "120/80", "72", "16", "36.5", "95", "98", "65.2", "1.65", "23.9", "1"},
            {"João Santos", "140/90", "88", "18", "37.2", "180", "95", "78.5", "1.75", "25.6", "2"},
            {"Ana Costa", "110/70", "65", "14", "36.8", "85", "99", "55.0", "1.60", "21.5", "3"},
            {"Carlos Lima", "160/100", "95", "20", "38.1", "220", "92", "85.3", "1.80", "26.3", "4"},
            {"Lucia Ferreira", "115/75", "70", "15", "36.3", "100", "97", "62.8", "1.68", "22.3", "5"},
            {"Pedro Oliveira", "130/85", "80", "17", "36.9", "150", "96", "90.1", "1.85", "26.3", "6"},
            {"Sofia Mendes", "105/65", "68", "13", "36.4", "78", "99", "48.5", "1.55", "20.2", "7"},
            {"Roberto Alves", "145/95", "92", "19", "37.8", "195", "94", "95.7", "1.78", "30.2", "8"},
            {"Fernanda Rocha", "118/76", "74", "16", "36.6", "102", "97", "60.4", "1.67", "21.6", "9"},
            {"Bruno Martins", "135/85", "82", "17", "37.0", "160", "95", "83.2", "1.80", "25.7", "10"},
            {"Juliana Souza", "125/80", "70", "15", "36.7", "110", "98", "58.9", "1.63", "22.1", "11"},
            {"Ricardo Nunes", "150/95", "90", "19", "38.0", "200", "93", "88.6", "1.76", "28.6", "12"},
            {"Patrícia Lima", "112/72", "66", "14", "36.2", "92", "99", "52.3", "1.58", "21.0", "13"},
            {"Gustavo Ribeiro", "142/92", "89", "18", "37.5", "175", "96", "91.0", "1.82", "27.5", "14"},
            {"Aline Barros", "108/68", "64", "13", "36.5", "80", "100", "49.7", "1.54", "20.9", "15"},
            {"Diego Ferreira", "138/88", "85", "17", "37.1", "165", "94", "87.4", "1.79", "27.3", "16"},
            {"Camila Teixeira", "122/78", "76", "16", "36.6", "105", "97", "61.0", "1.66", "22.1", "17"},
            {"Marcelo Duarte", "148/98", "94", "20", "38.2", "210", "91", "92.3", "1.83", "27.6", "18"},
            {"Vanessa Moraes", "113/70", "69", "14", "36.3", "89", "99", "53.8", "1.60", "21.0", "19"},
            {"Felipe Castro", "132/86", "81", "17", "36.8", "145", "96", "84.5", "1.77", "27.0", "20"},
            {"Rafaela Brito", "109/67", "66", "13", "36.4", "83", "100", "47.6", "1.52", "20.6", "21"},
            {"Eduardo Lima", "144/93", "87", "18", "37.4", "185", "95", "89.2", "1.81", "27.2", "22"},
            {"Natália Pires", "117/74", "71", "15", "36.5", "98", "98", "59.9", "1.64", "22.3", "23"},
            {"Thiago Neves", "155/100", "97", "21", "38.3", "230", "90", "96.1", "1.84", "28.3", "24"}
        };

        
        for (Object[] row : sampleData) {
            tableModel.addRow(row);
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Nome", "mmHg", "bpm", "irpm", "Temperatura°C", "mg/dL", "Saturação O₂ (%)", "Peso (kg)", "Altura (m)", "IMC", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, 
                java.lang.String.class, java.lang.String.class, java.lang.String.class, 
                java.lang.String.class, java.lang.String.class, java.lang.String.class, 
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
        );
    }// </editor-fold>                        
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(g);
    }
    
    // Método público para adicionar novos pacientes
    private void loadPacientes() {
        if (pacientes == null) {
            System.err.println("A lista de pacientes está nula. Nenhum dado será adicionado.");
            return;
        }

        java.text.SimpleDateFormat formatoDesejado = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.text.SimpleDateFormat formatoISO = new java.text.SimpleDateFormat("yyyy-MM-dd");
        
        tableModel.setRowCount(0); // Limpar tabela

        for (Paciente p : pacientes) {
            String dataFormatada = "";

            if (p.getDataNascimento() != null && !p.getDataNascimento().isEmpty()) {
                try {
                    java.util.Date data;
                    String dataNascimento = p.getDataNascimento();

                    // Tenta interpretar no formato dd/MM/yyyy
                    try {
                        data = formatoDesejado.parse(dataNascimento);
                    } catch (java.text.ParseException ex1) {
                        // Se falhar, tenta no formato yyyy-MM-dd
                        data = formatoISO.parse(dataNascimento);
                    }

                    // Formata a data no formato desejado
                    dataFormatada = formatoDesejado.format(data);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }

            tableModel.addRow(new Object[]{
                p.getNome() != null ? p.getNome() : "",                          // Nome do Paciente
                p.getPaXmmhg() != null ? p.getPaXmmhg() : "",                    // Pressão arterial (ex: 120/80 mmHg)
                p.getFcBpm() != null ? String.valueOf(p.getFcBpm()) : "",        // FC (bpm)
                p.getFrIbpm() != null ? String.valueOf(p.getFrIbpm()) : "",      // FR (irpm)
                p.getTemperaturaC() != null ? String.valueOf(p.getTemperaturaC()) : "", // Temperatura (°C)
                p.getHgtMgld() != null ? String.valueOf(p.getHgtMgld()) : "",    // Glicemia (mg/dL)
                p.getSpo2() != null ? String.valueOf(p.getSpo2()) : "",          // Saturação O2 (%)
                p.getPeso() != null ? String.valueOf(p.getPeso()) : "",          // Peso (kg)
                p.getAltura() != null ? String.valueOf(p.getAltura()) : "",      // Altura (m)
                p.getImc() != null ? String.valueOf(p.getImc()) : "",            // IMC calculado
                p.getId() != 0 ? String.valueOf(p.getId()) : ""               // ID do paciente (oculta)
            });
        }
    }
    
    // Método para recarregar todos os dados (fallback)
    public void reloadData(List<Paciente> pacientes) {
        this.pacientes = pacientes;
        loadPacientes();
    }
    
    // Método público para limpar todos os dados
    public void clearAllData() {
        tableModel.setRowCount(0);
    }
    
    // Método para adicionar um novo paciente
    public void adicionarPaciente(Paciente p) {
        
        System.out.println("adicionarPaciente em PainelSaude2: "+p.toString());
        
        Object[] row = {
            p.getNome() != null ? p.getNome() : "",                          // Nome do Paciente
            p.getPaXmmhg() != null ? p.getPaXmmhg() : "",                    // Pressão arterial (ex: 120/80 mmHg)
            p.getFcBpm() != null ? String.valueOf(p.getFcBpm()) : "",        // FC (bpm)
            p.getFrIbpm() != null ? String.valueOf(p.getFrIbpm()) : "",      // FR (irpm)
            p.getTemperaturaC() != null ? String.valueOf(p.getTemperaturaC()) : "", // Temperatura (°C)
            p.getHgtMgld() != null ? String.valueOf(p.getHgtMgld()) : "",    // Glicemia (mg/dL)
            p.getSpo2() != null ? String.valueOf(p.getSpo2()) : "",          // Saturação O2 (%)
            p.getPeso() != null ? String.valueOf(p.getPeso()) : "",          // Peso (kg)
            p.getAltura() != null ? String.valueOf(p.getAltura()) : "",      // Altura (m)
            p.getImc() != null ? String.valueOf(p.getImc()) : "",            // IMC calculado
            p.getId() != 0 ? String.valueOf(p.getId()) : ""               // ID do paciente (oculta)
        };
        tableModel.addRow(row);
        //pacientes.add(p);
        
        // Scroll para o novo item (opcional)
        int lastRow = tableModel.getRowCount() - 1;
        jTable1.scrollRectToVisible(jTable1.getCellRect(lastRow, 0, true));  
    }
    
    // Método para atualizar um paciente existente
    public void atualizarPaciente(Paciente pacienteAtualizado) {
        for (int i = 0; i < pacientes.size(); i++) {
            Paciente pacienteNaLista = pacientes.get(i);

            if (pacienteNaLista.getId() == pacienteAtualizado.getId()) {
                // Verifica se o nome da tabela na linha corresponde ao da lista
                Object nomeNaTabela = tableModel.getValueAt(i, 0); // Coluna 0 = Nome
                String nomeEsperado = pacienteNaLista.getNome() != null ? pacienteNaLista.getNome() : "";

                if (nomeNaTabela != null && nomeNaTabela.toString().equals(nomeEsperado)) {
                    // Atualiza a linha correspondente da tabela
                    tableModel.setValueAt(pacienteAtualizado.getNome() != null ? pacienteAtualizado.getNome() : "", i, 0);
                    tableModel.setValueAt(pacienteAtualizado.getPaXmmhg() != null ? pacienteAtualizado.getPaXmmhg() : "", i, 1);
                    tableModel.setValueAt(pacienteAtualizado.getFcBpm() != null ? String.valueOf(pacienteAtualizado.getFcBpm()) : "", i, 2);
                    tableModel.setValueAt(pacienteAtualizado.getFrIbpm() != null ? String.valueOf(pacienteAtualizado.getFrIbpm()) : "", i, 3);
                    tableModel.setValueAt(pacienteAtualizado.getTemperaturaC() != null ? String.valueOf(pacienteAtualizado.getTemperaturaC()) : "", i, 4);
                    tableModel.setValueAt(pacienteAtualizado.getHgtMgld() != null ? String.valueOf(pacienteAtualizado.getHgtMgld()) : "", i, 5);
                    tableModel.setValueAt(pacienteAtualizado.getSpo2() != null ? String.valueOf(pacienteAtualizado.getSpo2()) : "", i, 6);
                    tableModel.setValueAt(pacienteAtualizado.getPeso() != null ? String.valueOf(pacienteAtualizado.getPeso()) : "", i, 7);
                    tableModel.setValueAt(pacienteAtualizado.getAltura() != null ? String.valueOf(pacienteAtualizado.getAltura()) : "", i, 8);
                    tableModel.setValueAt(pacienteAtualizado.getImc() != null ? String.valueOf(pacienteAtualizado.getImc()) : "", i, 9);
                    tableModel.setValueAt(pacienteAtualizado.getId() != 0 ? String.valueOf(pacienteAtualizado.getId()) : "", i, 10);

                    // Atualiza a lista local
                    pacientes.set(i, pacienteAtualizado);
                    
                    // Destacar a linha atualizada (opcional)
                    jTable1.setRowSelectionInterval(i, i);
                    jTable1.scrollRectToVisible(jTable1.getCellRect(i, 0, true));
                }

                break; // Para após encontrar e (possivelmente) atualizar
            }
        }
    }
    
    // Método para remover um paciente
    public void removerPaciente(int pacienteId) {
        // Procura na tabela pela linha com o ID correspondente
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object idNaTabela = tableModel.getValueAt(i, 10); // Coluna 10 = ID

            if (idNaTabela != null && idNaTabela.toString().equals(String.valueOf(pacienteId))) {
                // Remove da tabela
                tableModel.removeRow(i);

                // Remove da lista de pacientes
                pacientes.removeIf(p -> p.getId() == pacienteId);

                return; // Sai do método após encontrar e remover
            }
        }

        // Log opcional se o paciente não foi encontrado
        System.out.println("Paciente com ID " + pacienteId + " não encontrado na tabela.");
    }
    
    // Método público para obter dados de um paciente específico
    public Paciente getPatientData(int row) {
        if (row >= 0 && row < tableModel.getRowCount()) {
            Paciente paciente = new Paciente();

            try {
                // Nome
                Object nomeObj = tableModel.getValueAt(row, 0);
                if (nomeObj != null) {
                    paciente.setNome(nomeObj.toString());
                }

                // Pressão Arterial (PA)
                Object paObj = tableModel.getValueAt(row, 1);
                if (paObj != null) {
                    paciente.setPaXmmhg(paObj.toString());
                }

                // Frequência Cardíaca (FC)
                Object fcObj = tableModel.getValueAt(row, 2);
                if (fcObj != null && !fcObj.toString().trim().isEmpty()) {
                    paciente.setFcBpm(Float.valueOf(fcObj.toString().replace(",", ".")));
                }

                // Frequência Respiratória (FR)
                Object frObj = tableModel.getValueAt(row, 3);
                if (frObj != null && !frObj.toString().trim().isEmpty()) {
                    paciente.setFrIbpm(Float.valueOf(frObj.toString().replace(",", ".")));
                }

                // Temperatura
                Object tempObj = tableModel.getValueAt(row, 4);
                if (tempObj != null && !tempObj.toString().trim().isEmpty()) {
                    paciente.setTemperaturaC(Float.valueOf(tempObj.toString().replace(",", ".")));
                }

                // Hemoglicoteste (HGT)
                Object hgtObj = tableModel.getValueAt(row, 5);
                if (hgtObj != null && !hgtObj.toString().trim().isEmpty()) {
                    paciente.setHgtMgld(Float.valueOf(hgtObj.toString().replace(",", ".")));
                }

                // Saturação de Oxigênio (SPO2)
                Object spo2Obj = tableModel.getValueAt(row, 6);
                if (spo2Obj != null && !spo2Obj.toString().trim().isEmpty()) {
                    paciente.setSpo2(Float.valueOf(spo2Obj.toString().replace(",", ".")));
                }

                // Peso
                Object pesoObj = tableModel.getValueAt(row, 7);
                if (pesoObj != null && !pesoObj.toString().trim().isEmpty()) {
                    paciente.setPeso(Float.valueOf(pesoObj.toString().replace(",", ".")));
                }

                // Altura
                Object alturaObj = tableModel.getValueAt(row, 8);
                if (alturaObj != null && !alturaObj.toString().trim().isEmpty()) {
                    paciente.setAltura(Float.valueOf(alturaObj.toString().replace(",", ".")));
                }

                // IMC
                Object imcObj = tableModel.getValueAt(row, 9);
                if (imcObj != null && !imcObj.toString().trim().isEmpty()) {
                    paciente.setImc(Float.valueOf(imcObj.toString().replace(",", ".")));
                }

                // ID
                Object idObj = tableModel.getValueAt(row, 10);
                if (idObj != null && !idObj.toString().trim().isEmpty()) {
                    paciente.setId(Integer.valueOf(idObj.toString()));
                }

            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter dados de saúde do paciente: " + e.getMessage());
                return null;
            } catch (Exception e) {
                System.err.println("Erro geral ao processar dados do paciente: " + e.getMessage());
                return null;
            }

            return paciente;
        }
        return null;
    }
    
    // Variables declaration - do not modify                     
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration                   
}