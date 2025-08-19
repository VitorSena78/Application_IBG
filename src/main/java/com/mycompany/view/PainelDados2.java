
package com.mycompany.view;

import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class PainelDados2 extends javax.swing.JPanel {
    
    private static final Color SELECTION_COLOR = new Color(59, 130, 246, 50);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_COLOR = new Color(51, 65, 85);
    
    // Cores para a barra de rolagem personalizada
    private static final Color SCROLLBAR_TRACK_COLOR = new Color(248, 250, 252);
    
    private PatientSelectionListener patientSelectionListener;
    
    //DefaultTableModel ;
    DefaultTableModel tableModel;
    private List<Paciente> pacientes;
    
    //Variaves da seleção paciente especialidade
    private List<PacienteEspecialidade> pacienteEspecialidades;
    
    public PainelDados2(List<Paciente> pacientes, List<PacienteEspecialidade> pacienteEspecialidades) {
        initComponents();
        tableModel = (DefaultTableModel) jTable1.getModel();
        setupTableDesign();
        this.pacientes = pacientes;
        
        //Seleção paciente especialidade
        this.pacienteEspecialidades = pacienteEspecialidades;
                
        loadPacientes();
        setOpaque(false);
    }
    
    public void setPatientSelectionListener(PatientSelectionListener listener) {
        this.patientSelectionListener = listener;
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(800, 620));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome do Paciente", "Data de Nascimento", "Idade", "Nome da Mãe", "CPF", "SUS", "Telefone", "Endereço", "Id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(g); 
    }
    
    // Método público para adicionar novos pacientes
    private void loadPacientes() {
        java.text.SimpleDateFormat formatoDesejado = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.text.SimpleDateFormat formatoISO = new java.text.SimpleDateFormat("yyyy-MM-dd");

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

                    // Formata a data no formato desejado para exibir na tabela
                    dataFormatada = formatoDesejado.format(data);

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                    // Pode logar ou mostrar alerta, se desejar
                    // Exemplo: JOptionPane.showMessageDialog(null, "Data inválida: " + p.getDataNascimento());
                }
            }

            tableModel.addRow(new Object[]{
                p.getNome(),
                dataFormatada,
                p.getIdade() != null ? p.getIdade().toString() : "",
                p.getNomeDaMae(),
                p.getCpf(),
                p.getSus(),
                p.getTelefone(),
                p.getEndereco(),
                String.valueOf(p.getId())
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
        java.text.SimpleDateFormat formatoDesejado = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.text.SimpleDateFormat formatoISO = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String dataFormatada = "";

        // Formata a data de nascimento, se disponível
        if (p.getDataNascimento() != null && !p.getDataNascimento().isEmpty()) {
            try {
                java.util.Date data;

                try {
                    // Tenta "dd/MM/yyyy"
                    data = formatoDesejado.parse(p.getDataNascimento());
                } catch (java.text.ParseException ex1) {
                    // Se falhar, tenta "yyyy-MM-dd"
                    data = formatoISO.parse(p.getDataNascimento());
                }

                dataFormatada = formatoDesejado.format(data);
            } catch (java.text.ParseException e) {
                e.printStackTrace(); // Log opcional
            }
        }

        Object[] row = {
            p.getNome() != null ? p.getNome() : "",                            // Nome
            dataFormatada,                                                    // Data de nascimento formatada
            p.getIdade() != null ? p.getIdade().toString() : "",              // Idade
            p.getNomeDaMae() != null ? p.getNomeDaMae() : "",                 // Nome da mãe
            p.getCpf() != null ? p.getCpf() : "",                             // CPF
            p.getSus() != null ? p.getSus() : "",                             // SUS
            p.getTelefone() != null ? p.getTelefone() : "",                   // Telefone
            p.getEndereco() != null ? p.getEndereco() : "",                   // Endereço
            String.valueOf(p.getId())                                         // ID (não exibido na tabela)
        };

        tableModel.addRow(row);
        //pacientes.add(p);
        

        // Scroll opcional até a nova linha
        int lastRow = tableModel.getRowCount() - 1;
        jTable1.scrollRectToVisible(jTable1.getCellRect(lastRow, 0, true));
    }
    
    public void atualizarPacienteEspecialidade(List<PacienteEspecialidade> novaListaPacienteEspecialidade) {
        // Atualizar a lista interna
        this.pacienteEspecialidades = novaListaPacienteEspecialidade;

        // Recarregar/atualizar a exibição dos dados
        // Exemplo: se você tem uma tabela ou lista visual
    }
    
    // Método para atualizar um paciente existente
    public void atualizarPaciente(Paciente pacienteAtualizado) {
        java.text.SimpleDateFormat formatoDesejado = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.text.SimpleDateFormat formatoISO = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String dataFormatada = "";
        
        // Formata a data de nascimento, se disponível
        if (pacienteAtualizado.getDataNascimento() != null && !pacienteAtualizado.getDataNascimento().isEmpty()) {
            try {
                java.util.Date data;

                try {
                    data = formatoDesejado.parse(pacienteAtualizado.getDataNascimento());
                } catch (java.text.ParseException ex1) {
                    data = formatoISO.parse(pacienteAtualizado.getDataNascimento());
                }

                dataFormatada = formatoDesejado.format(data);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < pacientes.size(); i++) {
            Paciente pacienteNaLista = pacientes.get(i);

            if (pacienteNaLista.getId() == pacienteAtualizado.getId()) {
                // Verifica se o nome da tabela na linha corresponde ao nome da lista
                Object nomeNaTabela = tableModel.getValueAt(i, 0); // Coluna 0 = Nome
                String nomeEsperado = pacienteNaLista.getNome() != null ? pacienteNaLista.getNome() : "";

                if (nomeNaTabela != null && nomeNaTabela.toString().equals(nomeEsperado)) {
                    // Atualiza os dados da tabela
                    tableModel.setValueAt(pacienteAtualizado.getNome() != null ? pacienteAtualizado.getNome() : "", i, 0); // Nome
                    tableModel.setValueAt(dataFormatada, i, 1); // Data Nasc.
                    tableModel.setValueAt(pacienteAtualizado.getIdade() != null ? pacienteAtualizado.getIdade().toString() : "", i, 2); // Idade
                    tableModel.setValueAt(pacienteAtualizado.getNomeDaMae() != null ? pacienteAtualizado.getNomeDaMae() : "", i, 3); // Mãe
                    tableModel.setValueAt(pacienteAtualizado.getCpf() != null ? pacienteAtualizado.getCpf() : "", i, 4); // CPF
                    tableModel.setValueAt(pacienteAtualizado.getSus() != null ? pacienteAtualizado.getSus() : "", i, 5); // SUS
                    tableModel.setValueAt(pacienteAtualizado.getTelefone() != null ? pacienteAtualizado.getTelefone() : "", i, 6); // Telefone
                    tableModel.setValueAt(pacienteAtualizado.getEndereco() != null ? pacienteAtualizado.getEndereco() : "", i, 7); // Endereço
                    tableModel.setValueAt(String.valueOf(pacienteAtualizado.getId()), i, 8); // ID

                    // Atualiza a lista local
                    pacientes.set(i, pacienteAtualizado);

                    // Destaca visualmente a linha atualizada
                    jTable1.setRowSelectionInterval(i, i);
                    jTable1.scrollRectToVisible(jTable1.getCellRect(i, 0, true));
                }

                break;
            }
        }
    }
    
    // Método para remover um paciente
    public void removerPaciente(int pacienteId) {
        // Procura na tabela pela linha com o ID correspondente
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object idNaTabela = tableModel.getValueAt(i, 8); // Coluna 8 = ID

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

                // Data de Nascimento
                Object dataObj = tableModel.getValueAt(row, 1);
                if (dataObj != null) {
                    paciente.setDataNascimento(dataObj.toString());
                }

                // Idade
                Object idadeObj = tableModel.getValueAt(row, 2);
                if (idadeObj != null && !idadeObj.toString().trim().isEmpty()) {
                    paciente.setIdade(Integer.valueOf(idadeObj.toString()));
                }

                // Nome da Mãe
                Object nomeMaeObj = tableModel.getValueAt(row, 3);
                if (nomeMaeObj != null) {
                    paciente.setNomeDaMae(nomeMaeObj.toString());
                }

                // CPF
                Object cpfObj = tableModel.getValueAt(row, 4);
                if (cpfObj != null) {
                    paciente.setCpf(cpfObj.toString());
                }

                // SUS
                Object susObj = tableModel.getValueAt(row, 5);
                if (susObj != null) {
                    paciente.setSus(susObj.toString());
                }

                // Telefone
                Object telefoneObj = tableModel.getValueAt(row, 6);
                if (telefoneObj != null) {
                    paciente.setTelefone(telefoneObj.toString());
                }

                // Endereço
                Object enderecoObj = tableModel.getValueAt(row, 7);
                if (enderecoObj != null) {
                    paciente.setEndereco(enderecoObj.toString());
                }

                // ID 
                Object idObj = tableModel.getValueAt(row, 8);
                if (idObj != null && !idObj.toString().trim().isEmpty()) {
                    paciente.setId(Integer.parseInt(idObj.toString()));
                }

            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter dados numéricos do paciente: " + e.getMessage());
                return null;
            } catch (Exception e) {
                System.err.println("Erro geral ao processar dados do paciente: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
            return paciente;
        }
        return null;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

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
        jTable1.setDefaultRenderer(Object.class, new TabelaHealthDataCellRenderer2());
        
        // Configuração das larguras das colunas
        setupColumnWidths();
        
        // Configuração do scroll pane com barra de rolagem personalizada
        setupCustomScrollPane();
        
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1 && patientSelectionListener != null) {
                    // Obter dados da linha selecionada
                    Paciente patientData = getPatientData(selectedRow);
                    
                    //Busca paienete da linha(Row) na lista
                    patientData = buscaPaciente(patientData);
                    List<PacienteEspecialidade> pacienteEspecialidadeData = buscaPacienteEspecialidade(patientData);
                    
                    // Notificar o listener
                    patientSelectionListener.onPatientSelected(patientData, pacienteEspecialidadeData);
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
    
    //Busca binaria pacienete
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
    
    private List<PacienteEspecialidade> buscaPacienteEspecialidade(Paciente paciente){
        // Verifica se o paciente e a lista de especialidades são válidos
        if (paciente == null || pacienteEspecialidades == null || pacienteEspecialidades.isEmpty()) {
            return new ArrayList<>();
        }

        // Filtra as especialidades relacionadas ao paciente usando stream
        return pacienteEspecialidades.stream().filter(pe -> pe.getPacienteId() == paciente.getId()).collect(Collectors.toList());
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
    int[] columnWidths = {150, 120, 80, 200, 120, 100, 120, 250, 60};
    String[] columnNames = {
        "Nome do Paciente", "Data de Nascimento", "Idade", "Nome da Mãe", 
        "CPF", "SUS", "Telefone", "Endereço", "ID"
    };
    
    for (int i = 0; i < columnWidths.length && i < jTable1.getColumnCount(); i++) {
        TableColumn column = jTable1.getColumnModel().getColumn(i);
        column.setPreferredWidth(columnWidths[i]);
        column.setHeaderValue(columnNames[i]);
    }
}
}
