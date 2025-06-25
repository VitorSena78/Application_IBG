
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class PainelDados2 extends javax.swing.JPanel {
    
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
    
    //DefaultTableModel ;
    DefaultTableModel tableModel;
    private List<Paciente> pacientes;
    
    public PainelDados2(List<Paciente> pacientes) {
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
    
    //Busca binaria paienete
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
    
    //Metodo para adicionar exemplos de pacientes a tabela
    private void populateWithSampleData() {
    tableModel.setRowCount(0); // Limpar dados existentes
    
    // Dados de exemplo com informações pessoais dos pacientes
    Object[][] sampleData = {
        {"Maria Silva Santos", "15/03/1985", "39", "Ana Santos Silva", "123.456.789-01", "700123456789889", "(11) 98765-4321", "Rua das Flores, 123 - Centro", "1"},
        {"João Carlos Oliveira", "22/07/1990", "34", "Carmen Oliveira Lima", "234.567.890-12", "700234567890786", "(11) 97654-3210", "Av. Paulista, 456 - Bela Vista", "2"},
        {"Ana Paula Costa", "08/12/1988", "36", "Rosa Costa Mendes", "345.678.901-23", "700345678901452", "(11) 96543-2109", "Rua Augusta, 789 - Consolação", "3"},
        {"Carlos Eduardo Lima", "30/01/1975", "49", "Lucia Lima Ferreira", "456.789.012-34", "700456789012698", "(11) 95432-1098", "Rua Oscar Freire, 321 - Jardins", "4"},
        {"Lucia Fernanda Alves", "14/09/1992", "32", "Maria Alves Rodrigues", "567.890.123-45", "700567890123785", "(11) 94321-0987", "Av. Faria Lima, 654 - Itaim Bibi", "5"},
        {"Pedro Henrique Martins", "03/05/1980", "44", "Isabel Martins Souza", "678.901.234-56", "700678901234784", "(11) 93210-9876", "Rua Haddock Lobo, 987 - Cerqueira César", "6"},
        {"Sofia Cristina Mendes", "27/11/1995", "29", "Beatriz Mendes Castro", "789.012.345-67", "700789012345698", "(11) 92109-8765", "Av. Rebouças, 147 - Pinheiros", "7"},
        {"Roberto José Ferreira", "16/04/1978", "46", "Helena Ferreira Dias", "890.123.456-78", "700890123456125", "(11) 91098-7654", "Rua Teodoro Sampaio, 258 - Pinheiros", "8"},
        {"Camila de Souza Rocha", "11/10/1983", "40", "Teresa Rocha Lima", "901.234.567-89", "700901234567893", "(11) 90987-6543", "Rua Bela Cintra, 321 - Consolação", "9"},
        {"Marcelo Antunes Dias", "04/02/1986", "38", "Sônia Dias Antunes", "012.345.678-90", "700012345678994", "(11) 89876-5432", "Av. Angélica, 789 - Higienópolis", "10"},
        {"Vanessa Regina Tavares", "19/06/1993", "31", "Regina Tavares Lopes", "123.987.654-32", "700123987654321", "(11) 88765-4321", "Rua Frei Caneca, 159 - Consolação", "11"},
        {"Felipe Augusto Leal", "26/08/1979", "45", "Adriana Leal Mota", "234.876.543-21", "700234876543210", "(11) 87654-3210", "Rua Pamplona, 963 - Jardim Paulista", "12"},
        {"Rafaela Gomes Monteiro", "09/01/1991", "33", "Luciana Gomes Monteiro", "345.765.432-10", "700345765432101", "(11) 86543-2109", "Rua Tabapuã, 332 - Itaim Bibi", "13"},
        {"Eduardo Vinícius Prado", "17/05/1982", "42", "Silvana Prado Martins", "456.654.321-09", "700456654321098", "(11) 85432-1098", "Av. JK, 147 - Vila Olímpia", "14"},
        {"Natália Andrade Ramos", "01/03/1989", "35", "Andreia Ramos Andrade", "567.543.210-98", "700567543210987", "(11) 84321-0987", "Rua João Cachoeira, 444 - Itaim", "15"},
        {"Thiago Moreira Pinto", "23/12/1977", "47", "Sandra Pinto Moreira", "678.432.109-87", "700678432109876", "(11) 83210-9876", "Rua Funchal, 888 - Vila Olímpia", "16"},
        {"Juliana Neves Carvalho", "06/06/1994", "30", "Helena Carvalho Silva", "789.321.654-98", "700789321654987", "(11) 82109-8765", "Rua do Rocio, 120 - Vila Olímpia", "17"},
        {"Rodrigo Azevedo Maia", "12/09/1987", "37", "Marina Maia Azevedo", "890.432.165-79", "700890432165794", "(11) 81234-5678", "Rua Gomes de Carvalho, 500 - Brooklin", "18"},
        {"Tatiane Lopes Vieira", "25/11/1981", "43", "Renata Vieira Lopes", "901.543.276-80", "700901543276801", "(11) 86789-1234", "Rua Sansão Alves dos Santos, 89 - Brooklin", "19"},
        {"Fernando Souza Dias", "29/04/1976", "48", "Elaine Dias Souza", "012.654.387-91", "700012654387916", "(11) 81298-7654", "Av. Santo Amaro, 999 - Santo Amaro", "20"},
        {"Aline Cristina Duarte", "31/01/1996", "28", "Juliana Duarte Castro", "123.765.498-02", "700123765498024", "(11) 81111-2222", "Rua das Orquídeas, 321 - Moema", "21"},
        {"Bruno Henrique Vasconcelos", "18/03/1984", "40", "Fabiana Vasconcelos Lima", "234.876.509-13", "700234876509135", "(11) 82222-3333", "Av. Ibirapuera, 1500 - Moema", "22"},
        {"Patrícia Figueiredo Costa", "10/07/1998", "26", "Sueli Costa Figueiredo", "345.987.610-24", "700345987610246", "(11) 83333-4444", "Rua Gaivota, 210 - Moema", "23"},
        {"Leandro Batista Nunes", "07/02/1985", "39", "Tatiana Nunes Batista", "456.098.721-35", "700456098721357", "(11) 84444-5555", "Rua Canário, 88 - Moema", "24"}
    };

    
    for (Object[] row : sampleData) {
        tableModel.addRow(row);
    }
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
