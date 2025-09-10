
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
        System.out.println("=== DEBUG loadPacientes PainelDados2 ===");
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

        java.text.SimpleDateFormat formatoDesejado = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.text.SimpleDateFormat formatoISO = new java.text.SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < pacientes.size(); i++) {
            Paciente p = pacientes.get(i);
            System.out.println("Processando paciente " + (i+1) + ": " + 
                              (p != null ? p.getNome() : "NULL") + " (ID: " + 
                              (p != null ? p.getId() : "NULL") + ")");

            if (p == null) {
                System.err.println("Paciente " + i + " é NULL - pulando");
                continue;
            }

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
                    System.err.println("Erro ao formatar data para paciente " + p.getNome() + ": " + e.getMessage());
                    dataFormatada = p.getDataNascimento(); // Usar valor original se falhar
                }
            }

            Object[] row = {
                p.getNome() != null ? p.getNome() : "",
                dataFormatada,
                p.getIdade() != null ? p.getIdade().toString() : "",
                p.getNomeDaMae() != null ? p.getNomeDaMae() : "",
                p.getCpf() != null ? p.getCpf() : "",
                p.getSus() != null ? p.getSus() : "",
                p.getTelefone() != null ? p.getTelefone() : "",
                p.getEndereco() != null ? p.getEndereco() : "",
                p.getId() != null ? String.valueOf(p.getId()) : ""
            };

            tableModel.addRow(row);
            System.out.println("Linha adicionada para: " + p.getNome());
        }

        // Forçar atualização visual
        tableModel.fireTableDataChanged();
        jTable1.revalidate();
        jTable1.repaint();

        System.out.println("Total de linhas na tabela: " + tableModel.getRowCount());
        System.out.println("=== FIM loadPacientes PainelDados2 ===");
    }
    
    public void debugEstadoTabela() {
        System.out.println("=== DEBUG ESTADO DA TABELA (PainelDados2) ===");
        System.out.println("TableModel existe: " + (tableModel != null));
        System.out.println("JTable existe: " + (jTable1 != null));
        System.out.println("Lista pacientes: " + (pacientes != null ? pacientes.size() : "NULL"));

        if (tableModel != null) {
            System.out.println("Linhas na tabela: " + tableModel.getRowCount());
            System.out.println("Colunas na tabela: " + tableModel.getColumnCount());

            // Mostrar algumas linhas se existirem
            for (int i = 0; i < Math.min(3, tableModel.getRowCount()); i++) {
                Object nome = tableModel.getValueAt(i, 0);
                Object id = tableModel.getValueAt(i, 8);
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
    
    // Método para recarregar todos os dados (fallback)
    public void reloadData(List<Paciente> novosPacientes) {
        System.out.println("=== reloadData chamado PainelDados2 ===");
        System.out.println("Novos pacientes: " + (novosPacientes != null ? novosPacientes.size() : "NULL"));

        //  Limpar seleção ANTES de recarregar
        limparSelecaoAtual();

        // Atualizar referência da lista
        this.pacientes = novosPacientes;

        // Recarregar dados na tabela
        SwingUtilities.invokeLater(() -> {
            loadPacientes();

            //  Forçar repaint após carregar
            jTable1.revalidate();
            jTable1.repaint();

            System.out.println("✅ Dados recarregados no PainelDados2 - " + 
                              (novosPacientes != null ? novosPacientes.size() : 0) + " pacientes");
        });
    }
    
    //Limpa a seleção atual e reseta o estado
    public void limparSelecaoAtual() {
        System.out.println("=== limparSelecaoAtual PainelDados2 ===");

        SwingUtilities.invokeLater(() -> {
            try {
                // Limpar seleção da tabela
                if (jTable1 != null) {
                    jTable1.clearSelection();
                    System.out.println("✅ Seleção da tabela limpa");
                }

                // CORREÇÃO PRINCIPAL: Notificar listener com dados limpos de forma mais robusta
                if (patientSelectionListener != null) {
                    System.out.println("Notificando listener sobre limpeza de seleção...");

                    // Criar lista vazia de especialidades e paciente nulo
                    List<PacienteEspecialidade> listaVazia = new ArrayList<>();

                    // Notificar com ambos os métodos para garantir compatibilidade
                    try {
                        // Primeiro método: com paciente e especialidades
                        patientSelectionListener.onPatientSelected(null, listaVazia);

                        // Segundo método: apenas com paciente (para fallback)
                        if (patientSelectionListener instanceof com.mycompany.view.PatientSelectionListener) {
                            ((com.mycompany.view.PatientSelectionListener) patientSelectionListener).onPatientSelected(null);
                        }

                        System.out.println("✅ Listener notificado sobre limpeza com ambos os métodos");
                    } catch (Exception e) {
                        System.err.println("❌ Erro ao notificar listener: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("⚠️ patientSelectionListener é NULL - não foi possível notificar sobre limpeza");
                }

                // Forçar repaint da tabela
                if (jTable1 != null) {
                    jTable1.revalidate();
                    jTable1.repaint();
                }

                System.out.println("✅ Seleção limpa no PainelDados2");

            } catch (Exception e) {
                System.err.println("❌ Erro ao limpar seleção: " + e.getMessage());
                e.printStackTrace();
            }
        });
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
    
    // NOVO MÉTODO: Recarregar dados SEM limpar seleção
    public void reloadDataSemLimparSelecao(List<Paciente> novosPacientes) {
        System.out.println("=== reloadDataSemLimparSelecao chamado PainelDados2 ===");
        System.out.println("Novos pacientes: " + (novosPacientes != null ? novosPacientes.size() : "NULL"));

        // NÃO limpar seleção aqui - apenas atualizar dados
        // Atualizar referência da lista
        this.pacientes = novosPacientes;

        // Recarregar dados na tabela
        SwingUtilities.invokeLater(() -> {
            loadPacientes();

            // Forçar repaint após carregar
            jTable1.revalidate();
            jTable1.repaint();

            System.out.println("✅ Dados recarregados no PainelDados2 - " + 
                              (novosPacientes != null ? novosPacientes.size() : 0) + " pacientes");
        });
    }
    
    // O método atualizarPacienteEspecialidade:
    public void atualizarPacienteEspecialidade(List<PacienteEspecialidade> novaListaPacienteEspecialidade) {
        System.out.println("=== Atualizando PacienteEspecialidade no PainelDados2 ===");
        System.out.println("Nova lista: " + (novaListaPacienteEspecialidade != null ? novaListaPacienteEspecialidade.size() : "NULL") + " associações");

        // Atualizar a lista interna de forma thread-safe
        synchronized (this) {
            this.pacienteEspecialidades = novaListaPacienteEspecialidade != null ? 
                new ArrayList<>(novaListaPacienteEspecialidade) : new ArrayList<>();
        }

        // CORREÇÃO: Forçar repaint para garantir que mudanças sejam refletidas visualmente
        SwingUtilities.invokeLater(() -> {
            if (jTable1 != null) {
                jTable1.revalidate();
                jTable1.repaint();
            }
        });

        System.out.println("✅ Lista PacienteEspecialidade atualizada: " + this.pacienteEspecialidades.size() + " associações");
    }
    
    // Método para atualizar um paciente existente
    public void atualizarPaciente(Paciente pacienteAtualizado) {
        System.out.println("=== atualizarPaciente chamado PainelDados2 ===");
        System.out.println("Paciente a atualizar: " + 
                          (pacienteAtualizado != null ? 
                           pacienteAtualizado.getNome() + " (ID: " + pacienteAtualizado.getId() + ")" : 
                           "NULL"));

        if (pacienteAtualizado == null || pacienteAtualizado.getId() == null) {
            System.err.println("❌ Paciente inválido para atualização");
            return;
        }

        // EXECUTAR NA EDT PARA GARANTIR THREAD SAFETY
        SwingUtilities.invokeLater(() -> {
            try {
                // Preparar data formatada
                java.text.SimpleDateFormat formatoDesejado = new java.text.SimpleDateFormat("dd/MM/yyyy");
                java.text.SimpleDateFormat formatoISO = new java.text.SimpleDateFormat("yyyy-MM-dd");
                String dataFormatada = "";

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
                        System.err.println("Erro ao formatar data: " + e.getMessage());
                        dataFormatada = pacienteAtualizado.getDataNascimento(); // Usar valor original
                    }
                }

                //  Buscar por ID diretamente na tabela
                boolean pacienteEncontrado = false;
                int linhaEncontrada = -1;

                // Procurar pela linha correta baseada no ID (coluna 8)
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    Object idNaTabela = tableModel.getValueAt(row, 8); // Coluna 8 = ID

                    if (idNaTabela != null) {
                        try {
                            int idTabelaInt = Integer.parseInt(idNaTabela.toString());
                            if (idTabelaInt == pacienteAtualizado.getId()) {
                                linhaEncontrada = row;
                                pacienteEncontrado = true;
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Erro ao converter ID da tabela: " + idNaTabela);
                            continue;
                        }
                    }
                }

                if (pacienteEncontrado) {
                    System.out.println("✅ Paciente encontrado na linha " + linhaEncontrada + " - atualizando...");

                    // Atualizar TODOS os campos na linha encontrada
                    tableModel.setValueAt(
                        pacienteAtualizado.getNome() != null ? pacienteAtualizado.getNome() : "", 
                        linhaEncontrada, 0); // Nome

                    tableModel.setValueAt(dataFormatada, linhaEncontrada, 1); // Data

                    tableModel.setValueAt(
                        pacienteAtualizado.getIdade() != null ? pacienteAtualizado.getIdade().toString() : "", 
                        linhaEncontrada, 2); // Idade

                    tableModel.setValueAt(
                        pacienteAtualizado.getNomeDaMae() != null ? pacienteAtualizado.getNomeDaMae() : "", 
                        linhaEncontrada, 3); // Mãe

                    tableModel.setValueAt(
                        pacienteAtualizado.getCpf() != null ? pacienteAtualizado.getCpf() : "", 
                        linhaEncontrada, 4); // CPF

                    tableModel.setValueAt(
                        pacienteAtualizado.getSus() != null ? pacienteAtualizado.getSus() : "", 
                        linhaEncontrada, 5); // SUS

                    tableModel.setValueAt(
                        pacienteAtualizado.getTelefone() != null ? pacienteAtualizado.getTelefone() : "", 
                        linhaEncontrada, 6); // Telefone

                    tableModel.setValueAt(
                        pacienteAtualizado.getEndereco() != null ? pacienteAtualizado.getEndereco() : "", 
                        linhaEncontrada, 7); // Endereço

                    tableModel.setValueAt(
                        String.valueOf(pacienteAtualizado.getId()), 
                        linhaEncontrada, 8); // ID

                    // FORÇAR NOTIFICAÇÃO DA MUDANÇA
                    tableModel.fireTableRowsUpdated(linhaEncontrada, linhaEncontrada);

                    // Atualizar também a lista local de pacientes
                    synchronized (pacientes) {
                        for (int i = 0; i < pacientes.size(); i++) {
                            if (pacientes.get(i).getId() != null && 
                                pacientes.get(i).getId().equals(pacienteAtualizado.getId())) {
                                pacientes.set(i, pacienteAtualizado);
                                System.out.println("✅ Lista local de pacientes também atualizada");
                                break;
                            }
                        }
                    }

                    // FORÇAR REPAINT DA TABELA
                    jTable1.revalidate();
                    jTable1.repaint();

                    // Destacar visualmente a linha atualizada (opcional)
                    try {
                        jTable1.setRowSelectionInterval(linhaEncontrada, linhaEncontrada);
                        jTable1.scrollRectToVisible(jTable1.getCellRect(linhaEncontrada, 0, true));
                        System.out.println("✅ Linha " + linhaEncontrada + " destacada visualmente");
                    } catch (Exception e) {
                        // Ignore se houver problema com a seleção visual
                        System.out.println("⚠️ Não foi possível destacar a linha: " + e.getMessage());
                    }

                    System.out.println("✅ Paciente atualizado com sucesso na tabela: " + 
                                      pacienteAtualizado.getNome() + " (linha " + linhaEncontrada + ")");

                } else {
                    System.err.println("❌ Paciente ID " + pacienteAtualizado.getId() + 
                                      " não encontrado na tabela para atualização");

                    // Log adicional para debug
                    System.err.println("⚠️ DEBUG: IDs presentes na tabela:");
                    for (int row = 0; row < Math.min(5, tableModel.getRowCount()); row++) {
                        Object idNaTabela = tableModel.getValueAt(row, 8);
                        System.err.println("    Linha " + row + ": ID = " + idNaTabela);
                    }
                }

            } catch (Exception e) {
                System.err.println("❌ Erro inesperado ao atualizar paciente na tabela: " + e.getMessage());
                e.printStackTrace();
            }
        });
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
    
    // O método buscaPacienteEspecialidade para ser thread-safe:
    private List<PacienteEspecialidade> buscaPacienteEspecialidade(Paciente paciente) {
        // Verifica se o paciente é válido
        if (paciente == null || paciente.getId() <= 0) {
            System.out.println("Paciente inválido para busca de especialidades");
            return new ArrayList<>();
        }

        // Busca thread-safe das especialidades
        List<PacienteEspecialidade> especialidadesLocais;
        synchronized (this) {
            especialidadesLocais = this.pacienteEspecialidades != null ? 
                new ArrayList<>(this.pacienteEspecialidades) : new ArrayList<>();
        }

        if (especialidadesLocais.isEmpty()) {
            System.out.println("⚠️ Lista de especialidades vazia para paciente ID: " + paciente.getId() + 
                              " - Verifique se atualizarPacienteEspecialidade foi chamado primeiro");
            return new ArrayList<>();
        }

        // Filtra as especialidades relacionadas ao paciente
        List<PacienteEspecialidade> especialidadesPaciente = especialidadesLocais.stream()
            .filter(pe -> pe.getPacienteId() != null && pe.getPacienteId().equals(paciente.getId()))
            .collect(Collectors.toList());

        System.out.println("Encontradas " + especialidadesPaciente.size() + 
                          " especialidades para paciente: " + paciente.getNome() + " (ID: " + paciente.getId() + ")");

        // CORREÇÃO: Log adicional para debug
        if (especialidadesPaciente.isEmpty() && !especialidadesLocais.isEmpty()) {
            System.out.println("⚠️ DEBUG: Total de associações disponíveis: " + especialidadesLocais.size());
            System.out.println("⚠️ DEBUG: Procurando por pacienteId: " + paciente.getId());
            // Log das primeiras 3 associações para debug
            for (int i = 0; i < Math.min(3, especialidadesLocais.size()); i++) {
                PacienteEspecialidade pe = especialidadesLocais.get(i);
                System.out.println("⚠️ DEBUG: Associação " + i + " - PacienteId: " + pe.getPacienteId() + 
                                  ", EspecialidadeId: " + pe.getEspecialidadeId());
            }
        }

        return especialidadesPaciente;
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
