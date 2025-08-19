package com.mycompany.viewNaoUsadas;

import com.mycompany.view.*;
import com.mycompany.components.JCheckBoxCustom;
import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.mycompany.model.dao.EspecialidadeDAO;
import com.mycompany.model.dao.PacienteDAO;
import com.mycompany.model.dao.PacienteEspecialidadeDAO;
import com.mycompany.printer.Printer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.Insets;

public class FormularioDados2PChekBoxAtuomatica extends javax.swing.JPanel implements PatientSelectionListener {

    // Lista para armazenar os registros de pacientes
    private List<Paciente> listaPacientes;
    Paciente paciente = new Paciente();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private PacienteDAO pacienteDAO;
    
    //variaves especialidades selecionadas
    private List<Especialidade> especialidades;
    List<PacienteEspecialidade> pacienteEspecialidades;
    private PacienteEspecialidadeDAO pacienteEspecialidadeDAO;
  
    // Controla se está em modo de edição
    private boolean modoEdicao = false; 
    
    private Printer printer;
    
    // NOVOS ATRIBUTOS PARA CHECKBOXES DINÂMICOS
    private Map<Integer, JCheckBox> especialidadeCheckBoxes;
    private JPanel panelEspecialidades;

    public FormularioDados2PChekBoxAtuomatica(PacienteDAO pacienteDAO, PacienteEspecialidadeDAO pacienteEspecialidadeDAO, EspecialidadeDAO especialidadeDAO, List<Especialidade> especialidades) {
        listaPacientes = new ArrayList<>();
        this.pacienteDAO = pacienteDAO;
        
        //especialidades selecionadas
        this.especialidades = especialidades;
        this.pacienteEspecialidadeDAO = pacienteEspecialidadeDAO;
        
        // Inicializar o Map de checkboxes
        this.especialidadeCheckBoxes = new HashMap<>();
        
        initComponents();
        
        // Criar checkboxes dinamicamente APÓS initComponents
        criarCheckboxesEspecialidades();
        
        // Inicializar o printer com as dependências necessárias
        //this.printer = new Printer(this, pacienteEspecialidadeDAO, especialidadeDAO, especialidades);
        
        setupEvents();
        setOpaque(false); // Importante para o efeito de borda funcionar
    }

    // NOVO MÉTODO PARA CRIAR CHECKBOXES DINAMICAMENTE
    private void criarCheckboxesEspecialidades() {
        // Limpar checkboxes existentes
        especialidadeCheckBoxes.clear();
        panelEspecialidades.removeAll();
        
        if (especialidades == null || especialidades.isEmpty()) {
            System.out.println("Lista de especialidades vazia!");
            return;
        }

        // Configurar layout do panel (GridLayout com 4 colunas)
        int totalEspecialidades = especialidades.size();
        int linhas = (totalEspecialidades + 3) / 4; // Calcula linhas necessárias para 4 colunas
        panelEspecialidades.setLayout(new GridLayout(linhas, 4, 15, 3));
        panelEspecialidades.setOpaque(false);

        // Criar checkbox para cada especialidade
        for (Especialidade especialidade : especialidades) {
            JCheckBoxCustom checkbox = new JCheckBoxCustom();
            checkbox.setFont(new Font("Arial", 0, 12));
            checkbox.setText(especialidade.getNome());
            checkbox.setOpaque(false);
            
            // Armazenar no Map usando o ID da especialidade como chave
            especialidadeCheckBoxes.put(especialidade.getId(), checkbox);
            
            // Adicionar ao panel
            panelEspecialidades.add(checkbox);
        }
        
        // Preencher espaços vazios se necessário
        int espacosVazios = (linhas * 4) - totalEspecialidades;
        for (int i = 0; i < espacosVazios; i++) {
            panelEspecialidades.add(new JLabel("")); // Label vazio para ocupar espaço
        }
        
        // Atualizar o painel
        panelEspecialidades.revalidate();
        panelEspecialidades.repaint();
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Define a cor de fundo
        g2.setColor(new Color(245, 248, 250));
        
        // Desenha um retângulo com todos os cantos arredondados
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        
        // Sobrepõe retângulos normais para manter apenas o canto inferior direito arredondado
        g2.fillRect(0, 0, getWidth() - 15, getHeight()); // Remove arredondamento dos cantos esquerdos e superior direito
        g2.fillRect(0, 0, getWidth(), getHeight() - 15); // Remove arredondamento dos cantos superiores
        
        super.paintComponent(grphcs);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        // Labels
        lblTitulo = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        lblNomeDaMae = new javax.swing.JLabel();
        lblEndereco = new javax.swing.JLabel();
        lblDataNascimento = new javax.swing.JLabel();
        lblIdade = new javax.swing.JLabel();
        lblCpf = new javax.swing.JLabel();
        lblSus = new javax.swing.JLabel();
        lblTelefone = new javax.swing.JLabel();

        // Campos de texto
        txtNome = new javax.swing.JTextField();
        txtNomeDaMae = new javax.swing.JTextField();
        txtEndereco = new javax.swing.JTextField();
        txtDataNascimento = new javax.swing.JTextField();
        txtIdade = new javax.swing.JTextField();
        txtCpf = new javax.swing.JTextField();
        txtSus = new javax.swing.JTextField();
        txtTelefone = new javax.swing.JTextField();

        // Botões
        btnSalvar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("jMenu3");
        
        // Label das especialidades
        lblEspecialidades = new javax.swing.JLabel();
        lblEspecialidades.setFont(new java.awt.Font("Arial", 1, 12));
        lblEspecialidades.setText("Especialidades Médicas:");

        // SUBSTITUIÇÃO: Criar o panel que vai conter os checkboxes dinâmicos
        panelEspecialidades = new JPanel();
        panelEspecialidades.setOpaque(false);

        // Configurar título
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 20));
        lblTitulo.setForeground(new java.awt.Color(51, 51, 51));
        lblTitulo.setText("Cadastro de Pacientes");

        // Configurar labels
        lblNome.setFont(new java.awt.Font("Arial", 0, 12));
        lblNome.setText("Nome Completo:");

        lblNomeDaMae.setFont(new java.awt.Font("Arial", 0, 12));
        lblNomeDaMae.setText("Nome da Mãe:");

        lblEndereco.setFont(new java.awt.Font("Arial", 0, 12));
        lblEndereco.setText("Endereço:");

        lblDataNascimento.setFont(new java.awt.Font("Arial", 0, 12));
        lblDataNascimento.setText("Data de Nascimento:");

        lblIdade.setFont(new java.awt.Font("Arial", 0, 12));
        lblIdade.setText("Idade:");

        lblCpf.setFont(new java.awt.Font("Arial", 0, 12));
        lblCpf.setText("CPF:");

        lblSus.setFont(new java.awt.Font("Arial", 0, 12));
        lblSus.setText("Cartão do SUS:");

        lblTelefone.setFont(new java.awt.Font("Arial", 0, 12));
        lblTelefone.setText("Telefone:");

        // Configurar campos de texto com tamanhos otimizados
        
        // Campos grandes (mais texto esperado)
        txtNome.setFont(new java.awt.Font("Arial", 0, 12));
        txtNome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtNomeDaMae.setFont(new java.awt.Font("Arial", 0, 12));
        txtNomeDaMae.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtEndereco.setFont(new java.awt.Font("Arial", 0, 12));
        txtEndereco.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Campos médios
        txtDataNascimento.setFont(new java.awt.Font("Arial", 0, 12));
        txtDataNascimento.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtTelefone.setFont(new java.awt.Font("Arial", 0, 12));
        txtTelefone.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtSus.setFont(new java.awt.Font("Arial", 0, 12));
        txtSus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Campos pequenos
        txtCpf.setFont(new java.awt.Font("Arial", 0, 12));
        txtCpf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Campo idade não editável e com aparência diferenciada
        txtIdade.setFont(new java.awt.Font("Arial", 0, 12));
        txtIdade.setEditable(false);
        txtIdade.setBackground(new java.awt.Color(240, 240, 240));
        txtIdade.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Configurar botões
        btnSalvar.setBackground(new java.awt.Color(76, 175, 80));
        btnSalvar.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvar.setFont(new java.awt.Font("Arial", 1, 12));
        btnSalvar.setText("Salvar");

        btnEditar.setBackground(new java.awt.Color(255, 152, 0));
        btnEditar.setForeground(new java.awt.Color(255, 255, 255));
        btnEditar.setFont(new java.awt.Font("Arial", 1, 12));
        btnEditar.setText("Editar");

        btnExcluir.setBackground(new java.awt.Color(244, 67, 54));
        btnExcluir.setForeground(new java.awt.Color(255, 255, 255));
        btnExcluir.setFont(new java.awt.Font("Arial", 1, 12));
        btnExcluir.setText("Excluir");
        
        btnImprimir.setBackground(new java.awt.Color(33, 150, 243)); // Azul
        btnImprimir.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimir.setFont(new java.awt.Font("Arial", 1, 12));
        btnImprimir.setText("Imprimir");


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(30, 30, 30)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblTitulo)
                        // Campos grandes (largura total - 440px)
                        .addComponent(lblNome)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNomeDaMae)
                        .addComponent(txtNomeDaMae, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblEndereco)
                        .addComponent(txtEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)

                        // Linha com campos médios lado a lado
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblDataNascimento)
                                .addComponent(txtDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(20, 20, 20)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblIdade)
                                .addComponent(txtIdade, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(20, 20, 20)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblTelefone)
                                .addComponent(txtTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))

                        // Linha com campos pequenos/médios lado a lado
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblCpf)
                                .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(20, 20, 20)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblSus)
                                .addComponent(txtSus, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))

                        // SUBSTITUIÇÃO: Especialidades agora dinâmicas
                        .addComponent(lblEspecialidades)
                        .addComponent(panelEspecialidades, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)

                        // Botões
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(64, 64, 64)
                            .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(25, 25, 25)
                    .addComponent(lblTitulo)
                    .addGap(25, 25, 25)

                    // Campos grandes
                    .addComponent(lblNome)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(15, 15, 15)

                    .addComponent(lblNomeDaMae)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtNomeDaMae, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(15, 15, 15)

                    .addComponent(lblEndereco)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(15, 15, 15)

                    // Linha com Data, Idade e Telefone
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblDataNascimento)
                        .addComponent(lblIdade)
                        .addComponent(lblTelefone))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtIdade, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(15, 15, 15)

                    // Linha com CPF e SUS
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblCpf)
                        .addComponent(lblSus))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(20, 20, 20)

                    // SUBSTITUIÇÃO: Especialidades médicas - agora dinâmicas
                    .addComponent(lblEspecialidades)
                    .addGap(10, 10, 10)
                    .addComponent(panelEspecialidades, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(25, 25, 25)

                    // Botões
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(30, Short.MAX_VALUE))
        );
        
        // Define tamanho preferido do painel - AJUSTADO para as mesmas dimensões do Formulario2
        this.setPreferredSize(new Dimension(500, 750)); // Reduzido devido à otimização do layout
    }// </editor-fold>//GEN-END:initComponents

    // MÉTODO ATUALIZADO: Usa checkboxes dinâmicos
    private void setEspecialidadesSelecionadas(List<PacienteEspecialidade> pacienteEspecialidades) {
        // Limpar todas as seleções primeiro
        limparEspecialidades();

        // Verificar se a lista é válida
        if (pacienteEspecialidades == null || pacienteEspecialidades.isEmpty()) {
            System.out.println("pacienteEspecialidades é null ou está vazio");
            return;
        }

        // Selecionar checkboxes baseado nos IDs das especialidades
        for (PacienteEspecialidade pe : pacienteEspecialidades) {
            JCheckBox checkbox = especialidadeCheckBoxes.get(pe.getEspecialidadeId());
            if (checkbox != null) {
                checkbox.setSelected(true);
            } else {
                System.out.println("Checkbox não encontrado para especialidade ID: " + pe.getEspecialidadeId());
            }
        }
    }
    
    // MÉTODO ATUALIZADO: Obter especialidades selecionadas dinamicamente
    private List<Especialidade> getEspecialidadesSelecionadas() {
        List<Especialidade> especialidadesSelecionadas = new ArrayList<>();
        
        for (Map.Entry<Integer, JCheckBox> entry : especialidadeCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                // Buscar a especialidade na lista pelo ID
                for (Especialidade esp : especialidades) {
                    if (esp.getId() == entry.getKey()) {
                        especialidadesSelecionadas.add(esp);
                        break;
                    }
                }
            }
        }
        
        return especialidadesSelecionadas;
    }
    
    //Cria uma lista de PacienteEspecialidade a partir de uma lista de especialidades e o ID do paciente
    private List<PacienteEspecialidade> listaPacienteEspecialidade(int pacienteId, List<Especialidade> especialidadesSelecionadas, String dataAtendimento) {

        List<PacienteEspecialidade> listaPacienteEspecialidade = new ArrayList<>();
        // Verifica se a lista de especialidades não é nula ou vazia
        if (especialidadesSelecionadas == null || especialidadesSelecionadas.isEmpty()) {
            System.out.println("Nenhuma especialidade selecionada para o paciente ID: " + pacienteId);
            return listaPacienteEspecialidade; // Retorna lista vazia
        }

        // Se a data não foi fornecida, usa a data atual formatada como String
        if (dataAtendimento == null || dataAtendimento.trim().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dataAtendimento = sdf.format(new java.util.Date()); // Data atual formatada
        }

        // Cria um objeto PacienteEspecialidade para cada especialidade selecionada
        for (Especialidade especialidade : especialidadesSelecionadas) {
            if (especialidade != null && especialidade.getId() > 0) {
                PacienteEspecialidade pe = new PacienteEspecialidade();
                pe.setPacienteId(pacienteId);
                pe.setEspecialidadeId(especialidade.getId());
                pe.setDataAtendimento(dataAtendimento);
                listaPacienteEspecialidade.add(pe);
                System.out.println("Criada associação: Paciente " + pacienteId + 
                                 " - Especialidade " + especialidade.getId() + 
                                 " (" + especialidade.getNome() + ")");
            } else {
                System.err.println("Especialidade inválida encontrada na lista - ignorando");
            }
        }
        System.out.println("Total de associações criadas: " + listaPacienteEspecialidade.size());
        return listaPacienteEspecialidade;
    }
    
    // MÉTODO ATUALIZADO: Limpar especialidades dinamicamente
    private void limparEspecialidades() {
        for (JCheckBox checkbox : especialidadeCheckBoxes.values()) {
            checkbox.setSelected(false);
        }
    }
    
    // MÉTODO ATUALIZADO: Aplicar bloqueio condicional nas especialidades
    private void aplicarBloqueioCondicionalEspecialidades() {
        for (JCheckBox checkbox : especialidadeCheckBoxes.values()) {
            checkbox.setEnabled(modoEdicao || !temEspecialidadesSelecionadas());
        }
    }
    
    // MÉTODO ATUALIZADO: Verificar se há especialidades selecionadas
    private boolean temEspecialidadesSelecionadas() {
        for (JCheckBox checkbox : especialidadeCheckBoxes.values()) {
            if (checkbox.isSelected()) {
                return true;
            }
        }
        return false;
    }
    
    //Método para preencher os campos do formulário com dados da tabela
    public void preencherCamposComDadosTabela(Paciente patientData) {
        if (patientData != null) {
            // Preencher os campos com os dados da tabela
            txtNome.setText(patientData.getNome() != null ? patientData.getNome().toString() : "");
            txtDataNascimento.setText(patientData.getDataNascimento() != null ? patientData.getDataNascimento().toString() : "");
            txtIdade.setText(patientData.getIdade() != null ? patientData.getIdade().toString() : "");
            txtNomeDaMae.setText(patientData.getNomeDaMae() != null ? patientData.getNomeDaMae().toString() : "");
            txtCpf.setText(patientData.getCpf() != null ? patientData.getCpf().toString() : "");
            txtSus.setText(patientData.getSus() != null ? patientData.getSus().toString() : "");
            txtTelefone.setText(patientData.getTelefone() != null ? patientData.getTelefone().toString() : "");
            txtEndereco.setText(patientData.getEndereco() != null ? patientData.getEndereco().toString() : "");
            
            // Verifica se pacienteEspecialidades recebeu as especialidades para esse paciente selecionado
            if (pacienteEspecialidades != null) {
                setEspecialidadesSelecionadas(pacienteEspecialidades);
            }

            // E modificar o método aplicarBloqueioCondicional() para incluir:
            aplicarBloqueioCondicionalEspecialidades();
            
            aplicarBloqueioCondicional();
            modoEdicao = false; // Garantir que não está em modo edição
        }
    }
    
    private void aplicarBloqueioCondicional() {
        JTextField[] campos = {
            txtNome, txtDataNascimento, txtNomeDaMae, txtCpf, 
            txtSus, txtTelefone, txtEndereco
        };

        for (JTextField campo : campos) {
            if (!campo.getText().trim().isEmpty() && !modoEdicao) {
                campo.setEditable(false);
                campo.setBackground(new Color(240, 240, 240)); // Cor de campo bloqueado
            } else {
                campo.setEditable(true);
                campo.setBackground(Color.WHITE); // Cor normal
            }
        }

        //Idade sempre permanecem não editáveis
        txtIdade.setEditable(false);
        txtIdade.setBackground(new Color(240, 240, 240));
    }

    private void adicionarListenersCamposVazios() {
        JTextField[] campos = {
            txtNome, txtDataNascimento, txtNomeDaMae, txtCpf, 
            txtSus, txtTelefone, txtEndereco
        };

        for (JTextField campo : campos) {
            campo.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    // Se o campo está vazio, sempre permitir edição
                    if (campo.getText().trim().isEmpty()) {
                        campo.setEditable(true);
                        campo.setBackground(Color.WHITE);
                    }
                }
            });
        }

        // Listener especial para data de nascimento para calcular idade automaticamente
        txtDataNascimento.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calcularIdade();
            }
        });
    }

    // Método auxiliar para calcular a idade baseada na data de nascimento
    private void calcularIdade() {
        String dataNasc = txtDataNascimento.getText().trim();
        if (!dataNasc.isEmpty()) {
            try {
                // Assumindo formato dd/MM/yyyy
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date nascimento = sdf.parse(dataNasc);

                Calendar hoje = Calendar.getInstance();
                Calendar nascCal = Calendar.getInstance();
                nascCal.setTime(nascimento);

                int idade = hoje.get(Calendar.YEAR) - nascCal.get(Calendar.YEAR);

                // Verifica se ainda não fez aniversário este ano
                if (hoje.get(Calendar.DAY_OF_YEAR) < nascCal.get(Calendar.DAY_OF_YEAR)) {
                    idade--;
                }

                txtIdade.setText(String.valueOf(idade));
            } catch (ParseException ex) {
                // Em caso de erro na conversão, limpa o campo idade
                txtIdade.setText("");
            }
        } else {
            txtIdade.setText("");
        }
    }
    
    // Método para configurar eventos
    private void setupEvents() {
        
        adicionarListenersCamposVazios();
        
        // Evento para calcular idade automaticamente quando a data de nascimento mudar
        txtDataNascimento.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularIdadeAutomaticamente();
            }
        });

        // Evento para salvar
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarPaciente();
            }
        });

        // Evento para Editar
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!modoEdicao) {
                    // Ativar modo edição
                    modoEdicao = true;
                    aplicarBloqueioCondicional(); // Liberar todos os campos preenchidos
                    aplicarBloqueioCondicionalEspecialidades(); // Liberar todos os campos Especialidades preenchidos
                    btnEditar.setText("Cancelar");
                    btnEditar.setBackground(new Color(158, 158, 158)); // Cor cinza
                } else {
                    // Cancelar edição - voltar ao estado original
                    modoEdicao = false;
                    aplicarBloqueioCondicional(); // Rebloquear campos preenchidos
                    aplicarBloqueioCondicionalEspecialidades(); // Rebloquear campos Especialidades preenchidos
                    btnEditar.setText("Editar");
                    btnEditar.setBackground(new Color(255, 152, 0)); // Cor laranja original
                    preencherCamposComDadosTabela(paciente);
                }
            }
        });

        // Evento para Excluir
        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirPaciente();
                limparCampos();
            }
        });
        
        // Evento para Imprimir
        btnImprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imprimirDadosPaciente();
            }
        });
    }

    // Método para calcular idade automaticamente
    private void calcularIdadeAutomaticamente() {
        try {
            String dataStr = txtDataNascimento.getText();
            
            // Verifica se a data está completa (formato dd/mm/aaaa)
            if (dataStr.length() == 10) {
                Date dataNascimento = dateFormat.parse(dataStr);
                
                Calendar nascimento = Calendar.getInstance();
                nascimento.setTime(dataNascimento);
                
                Calendar hoje = Calendar.getInstance();
                
                int idade = hoje.get(Calendar.YEAR) - nascimento.get(Calendar.YEAR);
                
                // Verifica se ainda não fez aniversário este ano
                if (hoje.get(Calendar.DAY_OF_YEAR) < nascimento.get(Calendar.DAY_OF_YEAR)) {
                    idade--;
                }
                
                if (idade >= 0 && idade <= 150) { // Validação básica
                    txtIdade.setText(String.valueOf(idade));
                } else {
                    txtIdade.setText("");
                }
            } else {
                txtIdade.setText("");
            }
        } catch (ParseException ex) {
            txtIdade.setText("");
        }
    }

    // Método para salvar pacienteSalvo
    private void salvarPaciente() {
        
        // Verifica se há um paciente selecionado
        if (paciente == null || paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Paciente não selecionado!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Paciente pacienteSalvo = paciente;
            
            
            // Validação dos campos obrigatórios
            if (txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo Nome é obrigatório!",
                        "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                txtNome.requestFocus();
                return;
            }

            if (txtCpf.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo CPF é obrigatório!",
                        "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                txtCpf.requestFocus();
                return;
            }
            
            // Preenchimento dos campos
            pacienteSalvo.setNome(txtNome.getText().trim());
            pacienteSalvo.setCpf(txtCpf.getText().trim());
            
            // Campos opcionais
            if (!txtDataNascimento.getText().isEmpty()) {
                pacienteSalvo.setDataNascimento(txtDataNascimento.getText());
            }
            
            if (!txtIdade.getText().isEmpty()) {
                try {
                    pacienteSalvo.setIdade(Integer.parseInt(txtIdade.getText()));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Idade deve ser um número inteiro!",
                            "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!txtNomeDaMae.getText().trim().isEmpty()) {
                pacienteSalvo.setNomeDaMae(txtNomeDaMae.getText().trim());
            }
            
            if (!txtSus.getText().trim().isEmpty()) {
                pacienteSalvo.setSus(txtSus.getText().trim());
            }
            
            if (!txtTelefone.getText().trim().isEmpty()) {
                pacienteSalvo.setTelefone(txtTelefone.getText().trim());
            }
            
            if (!txtEndereco.getText().trim().isEmpty()) {
                pacienteSalvo.setEndereco(txtEndereco.getText().trim());
            }
            
            // Salva/atualiza os dados do paciente
            System.out.println("paciente Salvo Painel Dados: " + pacienteSalvo.toString());
            boolean pacienteSalvoComSucesso = pacienteDAO.atualizar(pacienteSalvo);

            if (!pacienteSalvoComSucesso) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar dados do paciente!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Salva as especialidades selecionadas
            List<Especialidade> especialidadesSelecionadas = getEspecialidadesSelecionadas();
            List<PacienteEspecialidade> listaPacienteEspecialidade = null;
            if (!especialidadesSelecionadas.isEmpty()) {

                // Cria a lista de PacienteEspecialidade
                listaPacienteEspecialidade = listaPacienteEspecialidade(pacienteSalvo.getId(), especialidadesSelecionadas, null);
                
                // Insere todas as associações de uma vez
                if (!listaPacienteEspecialidade.isEmpty()) {
                    boolean especialidadesDeletadasComSucesso = pacienteEspecialidadeDAO.deletarPorPacienteId(pacienteSalvo.getId());
                    boolean especialidadesSalvasComSucesso = pacienteEspecialidadeDAO.inserirLista(listaPacienteEspecialidade);

                    if (!especialidadesSalvasComSucesso) {
                        JOptionPane.showMessageDialog(this, 
                            "Paciente salvo, mas houve problemas ao Salvar algumas especialidades.",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                    if(!especialidadesDeletadasComSucesso){
                        System.out.println("Paciente salvo, mas houve problemas ao Deletar as especialidades.");
                    }
                }
            } else {
                System.out.println("Nenhuma especialidade selecionada para o paciente.");
            }
            
           
            
            JOptionPane.showMessageDialog(this,
                    "Paciente cadastrado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            //Preenche os campos
            preencherCamposComDadosTabela(pacienteSalvo);
            setEspecialidadesSelecionadas(listaPacienteEspecialidade);
            
            // Cancelar edição - voltar ao estado original
            modoEdicao = false;
            aplicarBloqueioCondicional(); // Rebloquear campos preenchidos
            aplicarBloqueioCondicionalEspecialidades(); // Rebloquear campos Especialidades preenchidos
            btnEditar.setText("Editar");
            btnEditar.setBackground(new Color(255, 152, 0)); // Cor laranja original


        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar paciente: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado ao salvar: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    //Metodo para excluir paciente
    private void excluirPaciente(){
        
        // Verifica se há um paciente selecionado
        if (paciente == null || paciente.getId() == 0 || paciente.getNome().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Paciente não selecionado!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }else{
            pacienteDAO.deletar(paciente.getId());
        }
    }
    
    //Metodo para imprimir
    private void imprimirDadosPaciente() {
        printer.imprimirDadosPaciente(paciente);
    }

    // Método para limpar campos
    private void limparCampos() {
        txtNome.setText("");
        txtDataNascimento.setText("");
        txtIdade.setText("");
        txtNomeDaMae.setText("");
        txtCpf.setText("");
        txtSus.setText("");
        txtTelefone.setText("");
        txtEndereco.setText("");
        limparEspecialidades();
    }

    // Método para listar pacientes
    private void listarPacientes() {
        if (listaPacientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum paciente cadastrado.",
                    "Lista Vazia", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder lista = new StringBuilder("LISTA DE PACIENTES:\n\n");
        for (int i = 0; i < listaPacientes.size(); i++) {
            Paciente pac = listaPacientes.get(i);
            lista.append("Paciente ").append(i + 1).append(":\n");
            
            lista.append("  Nome: ").append(pac.getNome()).append("\n");
            if (pac.getDataNascimento() != null) {
                lista.append("  Data Nascimento: ").append(dateFormat.format(pac.getDataNascimento())).append("\n");
            }
            if (pac.getIdade() != null) {
                lista.append("  Idade: ").append(pac.getIdade()).append(" anos\n");
            }
            if (pac.getNomeDaMae() != null) {
                lista.append("  Nome da Mãe: ").append(pac.getNomeDaMae()).append("\n");
            }
            lista.append("  CPF: ").append(pac.getCpf()).append("\n");
            if (pac.getSus() != null) {
                lista.append("  SUS: ").append(pac.getSus()).append("\n");
            }
            if (pac.getTelefone() != null) {
                lista.append("  Telefone: ").append(pac.getTelefone()).append("\n");
            }
            if (pac.getEndereco() != null) {
                lista.append("  Endereço: ").append(pac.getEndereco()).append("\n");
            }
            
            lista.append("\n");
        }

        JTextArea textArea = new JTextArea(lista.toString());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", 0, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Lista de Pacientes",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    // NOVO MÉTODO: Para atualizar especialidades caso a lista mude no banco
    public void atualizarEspecialidades(List<Especialidade> novasEspecialidades) {
        this.especialidades = novasEspecialidades;
        criarCheckboxesEspecialidades();
    }

    @Override
    public void onPatientSelected(Paciente patientData, List<PacienteEspecialidade> pacienteEspecialidadeData) {
        paciente = patientData;
        pacienteEspecialidades = pacienteEspecialidadeData;
        preencherCamposComDadosTabela(patientData);
    }
    
    @Override
    public void onPatientSelected(Paciente patientData) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;

    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblDataNascimento;
    private javax.swing.JLabel lblIdade;
    private javax.swing.JLabel lblNomeDaMae;
    private javax.swing.JLabel lblCpf;
    private javax.swing.JLabel lblSus;
    private javax.swing.JLabel lblTelefone;
    private javax.swing.JLabel lblEndereco;

    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtDataNascimento;
    private javax.swing.JTextField txtIdade;
    private javax.swing.JTextField txtNomeDaMae;
    private javax.swing.JTextField txtCpf;
    private javax.swing.JTextField txtSus;
    private javax.swing.JTextField txtTelefone;
    private javax.swing.JTextField txtEndereco;

    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnImprimir;
    
    // Especialidades médicas - ATUALIZADO para versão dinâmica
    private javax.swing.JLabel lblEspecialidades;
    // REMOVIDO: Todos os checkboxes individuais foram substituídos pelo Map e Panel dinâmicos
    // End of variables declaration//GEN-END:variables
}