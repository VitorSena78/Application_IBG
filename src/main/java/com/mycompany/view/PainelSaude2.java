package com.mycompany.view;

import com.mycompany.model.bean.Paciente;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.util.List;

public class PainelSaude2 extends javax.swing.JPanel {
    
    private static final Color SELECTION_COLOR = new Color(59, 130, 246, 50);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_COLOR = new Color(51, 65, 85);
    
    // Cores para a barra de rolagem personalizada
    private static final Color SCROLLBAR_TRACK_COLOR = new Color(248, 250, 252);
    
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
        System.out.println("=== DEBUG loadPacientes ===");
        System.out.println("Pacientes recebidos: " + (pacientes != null ? pacientes.size() : "NULL"));

        if (pacientes == null) {
            System.err.println("A lista de pacientes está nula. Nenhum dado será adicionado.");
            return;
        }

        if (pacientes.isEmpty()) {
            System.err.println("A lista de pacientes está vazia. Nenhum dado será adicionado.");
            return;
        }

        // Limpar tabela apenas uma vez no início
        tableModel.setRowCount(0);
        System.out.println("Tabela limpa. Adicionando " + pacientes.size() + " pacientes...");

        for (int i = 0; i < pacientes.size(); i++) {
            Paciente p = pacientes.get(i);
            System.out.println("Processando paciente " + (i+1) + ": " + 
                              (p != null ? p.getNome() : "NULL") + " (ID: " + 
                              (p != null ? p.getId() : "NULL") + ")");

            if (p == null) {
                System.err.println("Paciente " + i + " é NULL - pulando");
                continue;
            }

            Object[] row = {
                p.getNome() != null ? p.getNome() : "",                          // Nome do Paciente
                p.getPaXMmhg() != null ? p.getPaXMmhg() : "",                    // Pressão arterial
                p.getFcBpm() != null ? String.valueOf(p.getFcBpm()) : "",        // FC (bpm)
                p.getFrIbpm() != null ? String.valueOf(p.getFrIbpm()) : "",      // FR (irpm)
                p.getTemperaturaC() != null ? String.valueOf(p.getTemperaturaC()) : "", // Temperatura (°C)
                p.getHgtMgld() != null ? String.valueOf(p.getHgtMgld()) : "",    // Glicemia (mg/dL)
                p.getSpo2() != null ? String.valueOf(p.getSpo2()) : "",          // Saturação O2 (%)
                p.getPeso() != null ? String.valueOf(p.getPeso()) : "",          // Peso (kg)
                p.getAltura() != null ? String.valueOf(p.getAltura()) : "",      // Altura (m)
                p.getImc() != null ? String.valueOf(p.getImc()) : "",            // IMC calculado
                p.getId() != null ? String.valueOf(p.getId()) : ""               // ID do paciente (oculta)
            };

            tableModel.addRow(row);
            System.out.println("Linha adicionada para: " + p.getNome());
        }

        // Forçar atualização visual
        tableModel.fireTableDataChanged();
        jTable1.revalidate();
        jTable1.repaint();

        System.out.println("Total de linhas na tabela: " + tableModel.getRowCount());
        System.out.println("=== FIM loadPacientes ===");
    }
    
    // Método para recarregar todos os dados (fallback)
    public void reloadData(List<Paciente> novosPacientes) {
        System.out.println("=== reloadData chamado ===");
        System.out.println("Novos pacientes: " + (novosPacientes != null ? novosPacientes.size() : "NULL"));

        // **CORREÇÃO 1: Limpar seleção ANTES de recarregar**
        limparSelecaoAtual();

        // Atualizar referência da lista
        this.pacientes = novosPacientes;

        // Recarregar dados na tabela
        SwingUtilities.invokeLater(() -> {
            loadPacientes();

            // **CORREÇÃO 2: Forçar repaint após carregar**
            jTable1.revalidate();
            jTable1.repaint();

            System.out.println("✅ Dados recarregados no PainelSaude2 - " + 
                              (novosPacientes != null ? novosPacientes.size() : 0) + " pacientes");
        });
    }
    
    // Limpa a seleção atual e reseta o estado
    public void limparSelecaoAtual() {
        SwingUtilities.invokeLater(() -> {
            jTable1.clearSelection();

            // Notificar o listener que não há mais seleção
            if (patientSelectionListener != null) {
                patientSelectionListener.onPatientSelected(null);
            }
        });
    }
    
    public void debugEstadoTabela() {
        System.out.println("=== DEBUG ESTADO DA TABELA (PainelSaude2) ===");
        System.out.println("TableModel existe: " + (tableModel != null));
        System.out.println("JTable existe: " + (jTable1 != null));
        System.out.println("Lista pacientes: " + (pacientes != null ? pacientes.size() : "NULL"));

        if (tableModel != null) {
            System.out.println("Linhas na tabela: " + tableModel.getRowCount());
            System.out.println("Colunas na tabela: " + tableModel.getColumnCount());

            // Mostrar algumas linhas se existirem
            for (int i = 0; i < Math.min(3, tableModel.getRowCount()); i++) {
                Object nome = tableModel.getValueAt(i, 0);
                Object id = tableModel.getValueAt(i, 10);
                System.out.println("Linha " + i + ": " + nome + " (ID: " + id + ")");
            }
        }

        if (jTable1 != null) {
            System.out.println("Tabela visível: " + jTable1.isVisible());
            System.out.println("Scroll pane visível: " + jScrollPane1.isVisible());
            System.out.println("Tamanho da tabela: " + jTable1.getSize());
        }
        System.out.println("=== FIM DEBUG ===");
    }
    
    
    // Método para adicionar um novo paciente
    public void adicionarPaciente(Paciente p) {
        
        System.out.println("adicionarPaciente em PainelSaude2: "+p.toString());
        
        Object[] row = {
            p.getNome() != null ? p.getNome() : "",                          // Nome do Paciente
            p.getPaXMmhg() != null ? p.getPaXMmhg() : "",                    // Pressão arterial (ex: 120/80 mmHg)
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
                    tableModel.setValueAt(pacienteAtualizado.getPaXMmhg() != null ? pacienteAtualizado.getPaXMmhg() : "", i, 1);
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
                    paciente.setPaXMmhg(paObj.toString());
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
                    paciente.setId(Integer.parseInt(idObj.toString()));
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