package com.mycompany.view;


import com.mycompany.listener.PatientUpdateListener;
import com.mycompany.manager.ApiManager;
import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.mycompany.printer.Printer;
import com.mycompany.service.EspecialidadeService;
import com.mycompany.service.PacienteEspecialidadeService;
import com.mycompany.service.PacienteService;
import com.mycompany.view.Componentes.EspecialidadeCheckBox;
import com.mycompany.view.Componentes.EspecialidadeListCellRenderer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.common.errors.ApiException;

public class FormularioDados2P extends javax.swing.JPanel implements PatientSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(FormularioDados2P.class.getName());
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color SCROLLBAR_TRACK_COLOR = new Color(248, 250, 252);
    
    // Services que substituem os DAOs
    private PacienteService pacienteService;
    private PacienteEspecialidadeService pacienteEspecialidadeService;
    
    // Dados
    private Paciente paciente = new Paciente();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private List<Especialidade> especialidades;
    private List<PacienteEspecialidade> pacienteEspecialidades;
    
    // Controla se está em modo de edição
    private boolean modoEdicao = false;
    private volatile boolean recarregandoDados = false;
    private Printer printer;
    private PatientUpdateListener patientUpdateListener;
    
   // Componentes para especialidades
    private JList<EspecialidadeCheckBox> listaEspecialidades;
    private DefaultListModel<EspecialidadeCheckBox> modeloLista;
    private JScrollPane scrollEspecialidades;
    
    // Componentes do formulário
    private JLabel lblTitulo;
    private JTextField txtNome, txtNomeDaMae, txtEndereco, txtDataNascimento;
    private JTextField txtIdade, txtCpf, txtSus, txtTelefone;
    private JButton btnSalvar, btnEditar, btnExcluir, btnImprimir;
    
    public void setPatientUpdateListener(PatientUpdateListener listener) {
        this.patientUpdateListener = listener;
    }
    
    public FormularioDados2P(PacienteService pacienteService, 
                            PacienteEspecialidadeService pacienteEspecialidadeService, 
                            EspecialidadeService especialidadeService, 
                            List<Especialidade> especialidades) {
        
        // Inicializar services ANTES de initComponents
        this.pacienteService = pacienteService;
        this.pacienteEspecialidadeService = pacienteEspecialidadeService;
        this.especialidades = especialidades;
        
        initComponents();
        
        // Inicializar o printer com as dependências necessárias
        this.printer = new Printer(this, pacienteEspecialidadeService, especialidadeService, especialidades);
        
        setupEvents();
        setOpaque(false);
    }

    private void criarListaEspecialidades() {
        modeloLista = new DefaultListModel<>();
        
        if (especialidades != null) {
            for (Especialidade esp : especialidades) {
                modeloLista.addElement(new EspecialidadeCheckBox(esp));
            }
        }
        
        listaEspecialidades = new JList<>(modeloLista);
        listaEspecialidades.setCellRenderer(new EspecialidadeListCellRenderer());
        listaEspecialidades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaEspecialidades.setLayoutOrientation(JList.VERTICAL);
        listaEspecialidades.setVisibleRowCount(-1);
        
        // **MELHORIA**: Configuração visual otimizada
        listaEspecialidades.setFont(new Font("Arial", Font.PLAIN, 12));
        listaEspecialidades.setBackground(new Color(245, 248, 250));
        listaEspecialidades.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        listaEspecialidades.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!listaEspecialidades.isEnabled()) return;
                
                int index = listaEspecialidades.locationToIndex(e.getPoint());
                if (index >= 0) {
                    EspecialidadeCheckBox item = modeloLista.getElementAt(index);
                    item.setSelecionada(!item.isSelecionada());
                    listaEspecialidades.repaint();
                }
            }
        });
        
        // **MELHORIA**: ScrollPane responsivo
        scrollEspecialidades = new JScrollPane(listaEspecialidades);
        scrollEspecialidades.setPreferredSize(new Dimension(440, 120));
        scrollEspecialidades.setMinimumSize(new Dimension(300, 100));
        scrollEspecialidades.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollEspecialidades.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setupCustomScrollPane();
    }
    
    private void setupCustomScrollPane() {
        scrollEspecialidades.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollEspecialidades.getViewport().setBackground(Color.WHITE);
        
        JScrollBar verticalScrollBar = scrollEspecialidades.getVerticalScrollBar();
        verticalScrollBar.setUI(new ModernScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(12, 0));
        verticalScrollBar.setBackground(SCROLLBAR_TRACK_COLOR);
        
        scrollEspecialidades.setBackground(Color.WHITE);
        scrollEspecialidades.getViewport().setOpaque(true);
        scrollEspecialidades.setViewportBorder(null);
        scrollEspecialidades.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(245, 248, 250));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fillRect(0, 0, getWidth() - 15, getHeight());
        g2.fillRect(0, 0, getWidth(), getHeight() - 15);
        
        super.paintComponent(grphcs);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        // Usar GridBagLayout ao invés de GroupLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Configurações padrão do GridBagConstraints
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Criar componentes
        criarComponentes();
        criarListaEspecialidades();
        
        // Montar layout seguindo o guia
        criarTitulo(gbc);
        criarSecaoDadosPessoais(gbc);
        criarSecaoContato(gbc);
        criarSecaoEspecialidades(gbc);
        criarSecaoBotoes(gbc);
        
        // Espaço flexível no final para responsividade
        gbc.gridx = 0; gbc.gridy = 20;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(Box.createVerticalGlue(), gbc);
        
        // **MELHORIA**: Definir tamanhos responsivos
        setPreferredSize(new Dimension(500, 750));
        setMinimumSize(new Dimension(350, 500));
    }// </editor-fold>//GEN-END:initComponents

    private void criarComponentes() {
        // Título
        lblTitulo = new JLabel("Cadastro de Pacientes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(51, 51, 51));
        
        // Campos de texto com configuração uniforme
        txtNome = criarCampoTexto();
        txtNomeDaMae = criarCampoTexto();
        txtEndereco = criarCampoTexto();
        txtDataNascimento = criarCampoTexto();
        txtTelefone = criarCampoTexto();
        txtCpf = criarCampoTexto();
        txtSus = criarCampoTexto();
        
        // Campo idade (não editável)
        txtIdade = criarCampoTexto();
        txtIdade.setEditable(false);
        txtIdade.setBackground(new Color(240, 240, 240));
        
        // Botões com cores padronizadas
        btnSalvar = criarBotao("Salvar", new Color(76, 175, 80));
        btnEditar = criarBotao("Editar", new Color(255, 152, 0));
        btnExcluir = criarBotao("Excluir", new Color(244, 67, 54));
        btnImprimir = criarBotao("Imprimir", new Color(33, 150, 243));
    }
    
    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Arial", Font.PLAIN, 12));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        campo.setPreferredSize(new Dimension(150, 30));
        campo.setMinimumSize(new Dimension(100, 25));
        return campo;
    }
    
    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setPreferredSize(new Dimension(80, 35));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        return botao;
    }
    
    private void criarTitulo(GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 10, 20, 10);
        add(lblTitulo, gbc);
        
        // Resetar configurações
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
    }
    
    private void criarSecaoDadosPessoais(GridBagConstraints gbc) {
        // Agrupar dados pessoais com painel com borda
        JPanel painelDadosPessoais = new JPanel(new GridBagLayout());
        painelDadosPessoais.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), "Dados Pessoais"));
        painelDadosPessoais.setOpaque(false);
        
        GridBagConstraints gbcInterno = new GridBagConstraints();
        gbcInterno.insets = new Insets(5, 5, 5, 5);
        gbcInterno.anchor = GridBagConstraints.WEST;
        
        // Nome (campo largo)
        gbcInterno.gridx = 0; gbcInterno.gridy = 0;
        gbcInterno.weightx = 0.0; gbcInterno.fill = GridBagConstraints.NONE;
        painelDadosPessoais.add(new JLabel("Nome Completo:"), gbcInterno);
        
        gbcInterno.gridx = 1; gbcInterno.gridwidth = 3;
        gbcInterno.weightx = 1.0; gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelDadosPessoais.add(txtNome, gbcInterno);
        
        // Nome da Mãe (campo largo)
        gbcInterno.gridx = 0; gbcInterno.gridy = 1;
        gbcInterno.gridwidth = 1; gbcInterno.weightx = 0.0;
        gbcInterno.fill = GridBagConstraints.NONE;
        painelDadosPessoais.add(new JLabel("Nome da Mãe:"), gbcInterno);
        
        gbcInterno.gridx = 1; gbcInterno.gridwidth = 3;
        gbcInterno.weightx = 1.0; gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelDadosPessoais.add(txtNomeDaMae, gbcInterno);
        
        // Data ne nascimento e Idade na mesma linha
        gbcInterno.gridx = 0; gbcInterno.gridy = 2;
        gbcInterno.gridwidth = 1; gbcInterno.weightx = 0.0;
        gbcInterno.fill = GridBagConstraints.NONE;
        painelDadosPessoais.add(new JLabel("Data Nascimento:"), gbcInterno);
        
        gbcInterno.gridx = 1; gbcInterno.weightx = 0.3;
        gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelDadosPessoais.add(txtDataNascimento, gbcInterno);
        
        gbcInterno.gridx = 2; gbcInterno.weightx = 0.0;
        gbcInterno.fill = GridBagConstraints.NONE;
        painelDadosPessoais.add(new JLabel("Idade:"), gbcInterno);
        
        gbcInterno.gridx = 3; gbcInterno.weightx = 0.2;
        gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelDadosPessoais.add(txtIdade, gbcInterno);
        
        // CPF e SUS na mesma linha
        gbcInterno.gridx = 0; gbcInterno.gridy = 3;
        gbcInterno.weightx = 0.0; gbcInterno.fill = GridBagConstraints.NONE;
        painelDadosPessoais.add(new JLabel("Cartão SUS:"), gbcInterno);
        
        gbcInterno.gridx = 1; gbcInterno.weightx = 0.4;
        gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelDadosPessoais.add(txtSus, gbcInterno);
        
        gbcInterno.gridx = 2; gbcInterno.weightx = 0.0;
        gbcInterno.fill = GridBagConstraints.NONE;
        painelDadosPessoais.add(new JLabel("CPF:"), gbcInterno);
        
        gbcInterno.gridx = 3; gbcInterno.weightx = 0.6;
        gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelDadosPessoais.add(txtCpf, gbcInterno);
        
        // Adicionar painel ao layout principal
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(painelDadosPessoais, gbc);
    }
    
    private void criarSecaoContato(GridBagConstraints gbc) {
        // **MELHORIA**: Seção de contato separada
        JPanel painelContato = new JPanel(new GridBagLayout());
        painelContato.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), "Dados de Contato"));
        painelContato.setOpaque(false);
        
        GridBagConstraints gbcInterno = new GridBagConstraints();
        gbcInterno.insets = new Insets(5, 5, 5, 5);
        gbcInterno.anchor = GridBagConstraints.WEST;
        
        // Telefone
        gbcInterno.gridx = 0; gbcInterno.gridy = 0;
        gbcInterno.weightx = 0.0; gbcInterno.fill = GridBagConstraints.NONE;
        painelContato.add(new JLabel("Telefone:"), gbcInterno);
        
        gbcInterno.gridx = 1; gbcInterno.weightx = 0.4;
        gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelContato.add(txtTelefone, gbcInterno);
        
        // Endereço (campo largo)
        gbcInterno.gridx = 0; gbcInterno.gridy = 1;
        gbcInterno.weightx = 0.0; gbcInterno.fill = GridBagConstraints.NONE;
        painelContato.add(new JLabel("Endereço:"), gbcInterno);
        
        gbcInterno.gridx = 1; gbcInterno.gridwidth = 3;
        gbcInterno.weightx = 1.0; gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelContato.add(txtEndereco, gbcInterno);
        
        // Adicionar painel ao layout principal
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(painelContato, gbc);
    }
    
    private void criarSecaoEspecialidades(GridBagConstraints gbc) {
        // **MELHORIA**: Seção de especialidades com painel próprio
        JPanel painelEspec = new JPanel(new GridBagLayout());
        painelEspec.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), "Especialidades Médicas"));
        painelEspec.setOpaque(false);
        
        GridBagConstraints gbcInterno = new GridBagConstraints();
        gbcInterno.gridx = 0; gbcInterno.gridy = 0;
        gbcInterno.fill = GridBagConstraints.BOTH;
        gbcInterno.weightx = 1.0; gbcInterno.weighty = 1.0;
        gbcInterno.insets = new Insets(5, 5, 5, 5);
        
        painelEspec.add(scrollEspecialidades, gbcInterno);
        
        // Adicionar painel ao layout principal
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.3;
        add(painelEspec, gbc);
    }
    
    private void criarSecaoBotoes(GridBagConstraints gbc) {
        // **MELHORIA**: Painel de botões centralizado
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        painelBotoes.setOpaque(false);
        
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnImprimir);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 15, 10);
        add(painelBotoes, gbc);
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        
        // Adicionar listener de redimensionamento
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustLayoutForSize();
            }
        });
    }
    
    private void adjustLayoutForSize() {
        Dimension size = getSize();
        
        // **RESPONSIVIDADE**: Ajustar layout baseado no tamanho
        if (size.width < 400) {
            // Layout compacto para telas pequenas
            setMinimumSize(new Dimension(350, 500));
        } else {
            // Layout normal
            setPreferredSize(new Dimension(500, 750));
        }
        
        revalidate();
        repaint();
    }
    
    // MÉTODOS ATUALIZADOS PARA USAR JLIST
    private void setEspecialidadesSelecionadas(List<PacienteEspecialidade> pacienteEspecialidades) {
        System.out.println("=== setEspecialidadesSelecionadas ===");

        // CORREÇÃO: Verificar se a lista de especialidades foi criada
        if (modeloLista == null) {
            System.out.println("⚠️ modeloLista é NULL - não é possível selecionar especialidades");
            return;
        }

        // Limpar todas as seleções primeiro
        limparEspecialidades();

        if (pacienteEspecialidades == null || pacienteEspecialidades.isEmpty()) {
            System.out.println("pacienteEspecialidades é null ou está vazio");
            return;
        }

        int especialidadesSelecionadas = 0;

        // Selecionar itens na lista baseado nos IDs das especialidades
        for (PacienteEspecialidade pe : pacienteEspecialidades) {
            boolean encontrada = false;
            for (int i = 0; i < modeloLista.getSize(); i++) {
                EspecialidadeCheckBox item = modeloLista.getElementAt(i);
                if (item.getEspecialidade().getId() == pe.getEspecialidadeId()) {
                    item.setSelecionada(true);
                    especialidadesSelecionadas++;
                    encontrada = true;
                    break;
                }
            }

            if (!encontrada) {
                System.out.println("⚠️ Especialidade ID " + pe.getEspecialidadeId() + " não encontrada na lista disponível");
            }
        }

        // Atualizar a visualização da lista
        if (listaEspecialidades != null) {
            listaEspecialidades.repaint();
        }

        System.out.println("Especialidades selecionadas: " + especialidadesSelecionadas + "/" + pacienteEspecialidades.size());
    }
    
    // Método para obter especialidades selecionadas da JList
    private List<Especialidade> getEspecialidadesSelecionadas() {
        List<Especialidade> especialidadesSelecionadas = new ArrayList<>();
        
        for (int i = 0; i < modeloLista.getSize(); i++) {
            EspecialidadeCheckBox item = modeloLista.getElementAt(i);
            if (item.isSelecionada()) {
                especialidadesSelecionadas.add(item.getEspecialidade());
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
    
    // Método para limpar especialidades na JList
    private void limparEspecialidades() {
        for (int i = 0; i < modeloLista.getSize(); i++) {
            EspecialidadeCheckBox item = modeloLista.getElementAt(i);
            item.setSelecionada(false);
        }
        listaEspecialidades.repaint();
    }
    
    // Método para aplicar bloqueio condicional nas especialidades
    private void aplicarBloqueioCondicionalEspecialidades() {
        listaEspecialidades.setEnabled(modoEdicao || !temEspecialidadesSelecionadas());
        
        // Mudar a cor de fundo para indicar se está habilitado ou não
        if (listaEspecialidades.isEnabled()) {
            listaEspecialidades.setBackground(new Color(245, 248, 250));
        } else {
            listaEspecialidades.setBackground(new Color(240, 240, 240));
        }
    }
    
    // Método para verificar se há especialidades selecionadas
    private boolean temEspecialidadesSelecionadas() {
        for (int i = 0; i < modeloLista.getSize(); i++) {
            EspecialidadeCheckBox item = modeloLista.getElementAt(i);
            if (item.isSelecionada()) {
                return true;
            }
        }
        return false;
    }
    
    //Método para preencher os campos do formulário com dados da tabela
    public void preencherCamposComDadosTabela(Paciente patientData) {
        System.out.println("=== preencherCamposComDadosTabela ===");
        System.out.println("Paciente: " + (patientData != null ? patientData.getNome() : "NULL"));

        // CORREÇÃO: Verificar se as especialidades do paciente estão disponíveis
        if (pacienteEspecialidades != null) {
            System.out.println("Especialidades do paciente disponíveis: " + pacienteEspecialidades.size());
        } else {
            System.out.println("⚠️ pacienteEspecialidades é NULL - pode causar problemas na exibição");
        }

        if (patientData != null) {
            // Preencher os campos com os dados da tabela
            txtNome.setText(patientData.getNome() != null ? patientData.getNome() : "");
            txtDataNascimento.setText(patientData.getDataNascimento() != null ? patientData.getDataNascimento() : "");
            txtIdade.setText(patientData.getIdade() != null ? patientData.getIdade().toString() : "");
            txtNomeDaMae.setText(patientData.getNomeDaMae() != null ? patientData.getNomeDaMae() : "");
            txtCpf.setText(patientData.getCpf() != null ? patientData.getCpf() : "");
            txtSus.setText(patientData.getSus() != null ? patientData.getSus() : "");
            txtTelefone.setText(patientData.getTelefone() != null ? patientData.getTelefone() : "");
            txtEndereco.setText(patientData.getEndereco() != null ? patientData.getEndereco() : "");

            // Verificar estado da lista de especialidades antes de limpar
            if (modeloLista == null) {
                System.out.println("⚠️ modeloLista é NULL - criando lista de especialidades");
                criarListaEspecialidades();
            }

            // Sempre limpar especialidades antes de configurar as novas
            limparEspecialidades();

            // Configurar especialidades apenas se a lista não estiver nula
            if (pacienteEspecialidades != null && !pacienteEspecialidades.isEmpty()) {
                setEspecialidadesSelecionadas(pacienteEspecialidades);
                System.out.println("✅ Especialidades configuradas: " + pacienteEspecialidades.size());
            } else {
                System.out.println("ℹ️ Nenhuma especialidade para configurar");
            }

            // Aplicar bloqueios condicionais
            aplicarBloqueioCondicionalEspecialidades();
            aplicarBloqueioCondicional();
            modoEdicao = false;

            System.out.println("✅ Campos preenchidos com sucesso");
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

    // Método para salvar pacienteSalvo FormularioDados2P
    private void salvarPaciente() {
        // Verifica se há um paciente selecionado
        if (paciente == null || paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Paciente não selecionado!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
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

            // Atualizar o paciente existente ao invés de criar um novo
            paciente.setNome(txtNome.getText().trim());
            paciente.setCpf(txtCpf.getText().trim());

            // Campos opcionais
            if (!txtDataNascimento.getText().isEmpty()) {
                paciente.setDataNascimento(txtDataNascimento.getText());
            }

            if (!txtIdade.getText().isEmpty()) {
                try {
                    paciente.setIdade(Integer.parseInt(txtIdade.getText()));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Idade deve ser um número inteiro!",
                            "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (!txtNomeDaMae.getText().trim().isEmpty()) {
                paciente.setNomeDaMae(txtNomeDaMae.getText().trim());
            }

            if (!txtSus.getText().trim().isEmpty()) {
                paciente.setSus(txtSus.getText().trim());
            }

            if (!txtTelefone.getText().trim().isEmpty()) {
                paciente.setTelefone(txtTelefone.getText().trim());
            }

            if (!txtEndereco.getText().trim().isEmpty()) {
                paciente.setEndereco(txtEndereco.getText().trim());
            }

            LOGGER.info("Salvando paciente via API: " + paciente.toString());
            boolean sucessoSalvamento = pacienteService.atualizar(paciente);

            if (!sucessoSalvamento) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar dados do paciente!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Salva as especialidades selecionadas via API
            List<Especialidade> especialidadesSelecionadas = getEspecialidadesSelecionadas();
            List<PacienteEspecialidade> listaPacienteEspecialidade = null;

            if (!especialidadesSelecionadas.isEmpty()) {
                // Cria a lista de PacienteEspecialidade - CORRIGIDO: usar paciente.getId()
                listaPacienteEspecialidade = listaPacienteEspecialidade(paciente.getId(), especialidadesSelecionadas, null);

                // Remove associações antigas e insere as novas via API
                if (!listaPacienteEspecialidade.isEmpty()) {
                    try {
                        // Primeiro, remove todas as associações existentes - CORRIGIDO: usar paciente.getId()
                        boolean especialidadesDeletadas = pacienteEspecialidadeService.deletarPorPacienteId(paciente.getId());

                        // Depois, insere as novas associações
                        boolean especialidadesSalvas = pacienteEspecialidadeService.inserirLista(listaPacienteEspecialidade);

                        if (!especialidadesSalvas) {
                            JOptionPane.showMessageDialog(this, 
                                "Paciente salvo, mas houve problemas ao salvar algumas especialidades.",
                                "Aviso", JOptionPane.WARNING_MESSAGE);
                        }

                        if (!especialidadesDeletadas) {
                            LOGGER.warning("Paciente salvo, mas houve problemas ao deletar as especialidades antigas.");
                        }

                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Erro ao gerenciar especialidades", e);
                        JOptionPane.showMessageDialog(this, 
                            "Paciente salvo, mas houve problemas com as especialidades: " + e.getMessage(),
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else {
                LOGGER.info("Nenhuma especialidade selecionada para o paciente.");
                // Remove todas as associações se nenhuma especialidade foi selecionada - CORRIGIDO: usar paciente.getId()
                try {
                    pacienteEspecialidadeService.deletarPorPacienteId(paciente.getId());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erro ao remover especialidades", e);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Paciente atualizado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            // Buscar os dados atualizados da API para garantir consistência - CORRIGIDO: usar paciente.getId()
            Paciente pacienteAtualizado = pacienteService.buscarPorId(paciente.getId());
            if (pacienteAtualizado != null) {
                // Atualiza a referência local
                this.paciente = pacienteAtualizado;

                try {
                    List<PacienteEspecialidade> especialidadesAtualizadas = pacienteEspecialidadeService.buscarPorPacienteId(paciente.getId());
                    this.pacienteEspecialidades = especialidadesAtualizadas != null ? especialidadesAtualizadas : new ArrayList<>();
                    System.out.println("Especialidades recarregadas após salvamento: " + this.pacienteEspecialidades.size());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erro ao recarregar especialidades após salvamento", e);
                    this.pacienteEspecialidades = new ArrayList<>();
                }
                
                System.out.println("=== NOTIFICANDO ATUALIZAÇÃO VIA FORMULÁRIO ===");
                System.out.println("Paciente atualizado: " + pacienteAtualizado.getNome() + " (ID: " + pacienteAtualizado.getId() + ")");

                // Atualizar campos do formulário com dados atualizados
                preencherCamposComDadosTabela(pacienteAtualizado);
                
                // NOTIFICAR OS PAINÉIS DA ATUALIZAÇÃO
                if (patientUpdateListener != null) {
                    // Executar na EDT para garantir thread safety
                    SwingUtilities.invokeLater(() -> {
                        try {
                            patientUpdateListener.onPatientUpdated(pacienteAtualizado);
                            System.out.println("✅ PatientUpdateListener notificado com sucesso");
                        } catch (Exception e) {
                            System.err.println("❌ Erro ao notificar PatientUpdateListener: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                } else {
                    System.err.println("⚠️ patientUpdateListener é NULL - não foi possível notificar atualização");
                } 
            }else {
                System.err.println("❌ Erro: não foi possível buscar dados atualizados do paciente");
            }

            // Cancelar edição - voltar ao estado original
            modoEdicao = false;
            aplicarBloqueioCondicional();
            aplicarBloqueioCondicionalEspecialidades();
            btnEditar.setText("Editar");
            btnEditar.setBackground(new Color(255, 152, 0));

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao salvar", ex);
            JOptionPane.showMessageDialog(this, "Erro inesperado ao salvar: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    //Metodo para excluir paciente
     private void excluirPaciente() {
        // Verifica se há um paciente selecionado
        if (paciente == null || paciente.getId() == 0 || paciente.getNome().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Paciente não selecionado!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmar exclusão
        int confirmacao = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o paciente: " + paciente.getNome() + "?",
                "Confirmar Exclusão", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                LOGGER.info("Excluindo paciente ID: " + paciente.getId());
                
                boolean sucesso = pacienteService.deletar(paciente.getId());
                
                if (sucesso) {
                    int pacienteId = paciente.getId(); // Salvar ID antes de limpar

                    JOptionPane.showMessageDialog(this, "Paciente excluído com sucesso!",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    // NOTIFICAR OS PAINÉIS DA EXCLUSÃO
                    if (patientUpdateListener != null) {
                        patientUpdateListener.onPatientDeleted(pacienteId);
                    }

                    limparCampos();
                    this.paciente = new Paciente(); // Reset
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir paciente!",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (ApiException ex) {
                LOGGER.log(Level.SEVERE, "Erro na API ao excluir paciente", ex);
                JOptionPane.showMessageDialog(this, "Erro na comunicação com a API: " + ex.getMessage(),
                        "Erro de API", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    //Metodo para imprimir
    private void imprimirDadosPaciente() {
        if (paciente == null || paciente.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Paciente não selecionado!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        printer.imprimirDadosPaciente(paciente);
    }

    // Método para limpar campos
    public void limparCampos() {
        System.out.println("=== Limpando campos do FormularioDados2P ===");

        // Executar limpeza na EDT para garantir thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Limpar campos de texto
                if (txtNome != null) txtNome.setText("");
                if (txtDataNascimento != null) txtDataNascimento.setText("");
                if (txtIdade != null) txtIdade.setText("");
                if (txtNomeDaMae != null) txtNomeDaMae.setText("");
                if (txtCpf != null) txtCpf.setText("");
                if (txtSus != null) txtSus.setText("");
                if (txtTelefone != null) txtTelefone.setText("");
                if (txtEndereco != null) txtEndereco.setText("");

                System.out.println("✅ Campos de texto limpos");

                // CORREÇÃO PRINCIPAL: Limpar especialidades de forma mais robusta
                limparEspecialidadesCompleto();

                // Resetar variáveis de estado de forma thread-safe
                synchronized (this) {
                    this.paciente = new Paciente();
                    this.pacienteEspecialidades = new ArrayList<>();
                    System.out.println("✅ Variáveis de estado resetadas");
                }

                // Resetar estado dos campos e formulário
                modoEdicao = false;
                aplicarBloqueioCondicional();
                aplicarBloqueioCondicionalEspecialidades();

                // Resetar botão editar
                if (btnEditar != null) {
                    btnEditar.setText("Editar");
                    btnEditar.setBackground(new Color(255, 152, 0));
                }

                // NOVA ADIÇÃO: Forçar repaint de todos os componentes
                revalidate();
                repaint();

                System.out.println("✅ Campos limpos com sucesso no FormularioDados2P");

            } catch (Exception e) {
                System.err.println("❌ Erro ao limpar campos: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    //  Limpeza robusta das especialidades
    private void limparEspecialidadesCompleto() {
        try {
            System.out.println("=== Iniciando limpeza completa das especialidades ===");

            if (modeloLista != null) {
                System.out.println("Limpando " + modeloLista.getSize() + " especialidades...");

                // Limpar todas as seleções
                for (int i = 0; i < modeloLista.getSize(); i++) {
                    EspecialidadeCheckBox item = modeloLista.getElementAt(i);
                    if (item != null) {
                        item.setSelecionada(false);
                    }
                }

                // Forçar repaint da lista
                if (listaEspecialidades != null) {
                    listaEspecialidades.clearSelection();
                    listaEspecialidades.revalidate();
                    listaEspecialidades.repaint();

                    // NOVA ADIÇÃO: Forçar repaint do scroll pane também
                    if (scrollEspecialidades != null) {
                        scrollEspecialidades.revalidate();
                        scrollEspecialidades.repaint();
                    }
                }

                System.out.println("✅ Especialidades limpas: " + modeloLista.getSize() + " itens processados");
            } else {
                System.out.println("ℹ️ modeloLista é null - não há especialidades para limpar");
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao limpar especialidades: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    // Método para atualizar a lista de especialidades (caso seja necessário recarregar do banco)
    public void atualizarEspecialidades(List<Especialidade> novasEspecialidades) {
        System.out.println("=== atualizarEspecialidades FormularioDados2P ===");
        System.out.println("Especialidades recebidas: " + (novasEspecialidades != null ? novasEspecialidades.size() : "NULL"));

        this.especialidades = novasEspecialidades;

        // Limpar estado atual ANTES de recriar a lista
        limparEstadoCompleto();

        // Recriar a lista de especialidades
        criarListaEspecialidades();

        //  Resetar estado dos campos após recriar a lista
        modoEdicao = false;
        aplicarBloqueioCondicional();
        aplicarBloqueioCondicionalEspecialidades();
        btnEditar.setText("Editar");
        btnEditar.setBackground(new Color(255, 152, 0));

        // Reposicionar o scrollPane no layout se necessário
        if (scrollEspecialidades != null) {
            scrollEspecialidades.revalidate();
            scrollEspecialidades.repaint();
        }

        System.out.println("✅ Especialidades atualizadas e estado resetado");
    }
    
    // Limpa completamente o estado do formulário
    private void limparEstadoCompleto() {
        System.out.println("=== Limpando estado completo do FormularioDados2P ===");

        // Limpar campos de texto
        txtNome.setText("");
        txtDataNascimento.setText("");
        txtIdade.setText("");
        txtNomeDaMae.setText("");
        txtCpf.setText("");
        txtSus.setText("");
        txtTelefone.setText("");
        txtEndereco.setText("");

        // Limpar especialidades da lista atual
        if (modeloLista != null) {
            limparEspecialidades();
        }

        // Resetar variáveis de estado
        this.paciente = new Paciente();
        this.pacienteEspecialidades = new ArrayList<>();

        System.out.println("✅ Estado completo limpo");
    }

    // NOVO MÉTODO: Atualizar especialidades SEM limpar estado atual
    public void atualizarEspecialidadesSemLimpar(List<Especialidade> novasEspecialidades) {
        System.out.println("=== atualizarEspecialidadesSemLimpar FormularioDados2P ===");
        System.out.println("Especialidades recebidas: " + (novasEspecialidades != null ? novasEspecialidades.size() : "NULL"));

        this.especialidades = novasEspecialidades;

        // SALVAR estado atual das seleções ANTES de recriar a lista
        List<Integer> especialidadesSelecionadasIds = new ArrayList<>();
        if (modeloLista != null) {
            for (int i = 0; i < modeloLista.getSize(); i++) {
                EspecialidadeCheckBox item = modeloLista.getElementAt(i);
                if (item.isSelecionada()) {
                    especialidadesSelecionadasIds.add(item.getEspecialidade().getId());
                }
            }
        }

        System.out.println("Estado atual salvo: " + especialidadesSelecionadasIds.size() + " especialidades selecionadas");

        // Recriar a lista de especialidades
        criarListaEspecialidades();

        // RESTAURAR as seleções que estavam ativas
        if (!especialidadesSelecionadasIds.isEmpty()) {
            for (int i = 0; i < modeloLista.getSize(); i++) {
                EspecialidadeCheckBox item = modeloLista.getElementAt(i);
                if (especialidadesSelecionadasIds.contains(item.getEspecialidade().getId())) {
                    item.setSelecionada(true);
                }
            }

            if (listaEspecialidades != null) {
                listaEspecialidades.repaint();
            }

            System.out.println("Estado restaurado: " + especialidadesSelecionadasIds.size() + " especialidades re-selecionadas");
        }

        // Reposicionar o scrollPane no layout se necessário
        if (scrollEspecialidades != null) {
            scrollEspecialidades.revalidate();
            scrollEspecialidades.repaint();
        }

        System.out.println("✅ Especialidades atualizadas mantendo seleções");
    }

    @Override
    public void onPatientSelected(Paciente patientData, List<PacienteEspecialidade> pacienteEspecialidadeData) {
        System.out.println("=== onPatientSelected FormularioDados2P ===");
        System.out.println("Paciente: " + (patientData != null ? patientData.getNome() + " (ID: " + patientData.getId() + ")" : "NULL"));
        System.out.println("Especialidades recebidas: " + (pacienteEspecialidadeData != null ? pacienteEspecialidadeData.size() : "0"));

        if (patientData != null) {
            // Verificar se é um paciente diferente do atual
            boolean mesmoPaciente = (this.paciente != null && 
                                   this.paciente.getId() != null && 
                                   patientData.getId() != null &&
                                   this.paciente.getId().equals(patientData.getId()));

            if (!mesmoPaciente) {
                // Paciente diferente - limpar estado anterior
                limparEstadoAnterior();
                System.out.println("Paciente diferente detectado - estado anterior limpo");
            } else {
                System.out.println("Mesmo paciente - mantendo estado atual");
            }

            this.paciente = patientData;
            this.pacienteEspecialidades = pacienteEspecialidadeData != null ? 
                new ArrayList<>(pacienteEspecialidadeData) : new ArrayList<>();

            // Preencher campos com os dados recebidos
            preencherCamposComDadosTabela(patientData);

            System.out.println("✅ Paciente e especialidades configurados no formulário");
        } else {
            // Limpar formulário quando não há seleção
            limparCampos();
            System.out.println("✅ Formulário limpo - nenhum paciente selecionado");
        }
    }
    
    @Override
    public void onPatientSelected(Paciente patientData) {
        System.out.println("=== onPatientSelected (sem especialidades) FormularioDados2P ===");
        System.out.println("Paciente: " + (patientData != null ? patientData.getNome() + " (ID: " + patientData.getId() + ")" : "NULL"));

        // limpar estado anterior primeiro
        limparEstadoAnterior();

        if (patientData != null && patientData.getId() > 0) {
            this.paciente = patientData;

            // Buscar especialidades via API de forma assíncrona
            CompletableFuture.supplyAsync(() -> {
                try {
                    return pacienteEspecialidadeService.buscarPorPacienteId(patientData.getId());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erro ao buscar especialidades do paciente via API", e);
                    return new ArrayList<PacienteEspecialidade>();
                }
            }).thenAcceptAsync(especialidades -> {
                // Executa na EDT
                SwingUtilities.invokeLater(() -> {
                    // Verificar se ainda é o mesmo paciente selecionado
                    if (this.paciente != null && this.paciente.getId().equals(patientData.getId())) {
                        this.pacienteEspecialidades = especialidades;
                        preencherCamposComDadosTabela(patientData);
                        System.out.println("✅ Especialidades carregadas via API: " + especialidades.size());
                    } else {
                        System.out.println("ℹ️ Paciente mudou durante carregamento - ignorando resultado");
                    }
                });
            });
        } else {
            // Limpar formulário quando não há seleção válida
            SwingUtilities.invokeLater(() -> {
                limparCampos();
                System.out.println("✅ Formulário limpo - paciente inválido");
            });
        }
    }
    
    // Limpa especificamente o estado anterior
    private void limparEstadoAnterior() {
        System.out.println("=== Limpando estado anterior ===");

        //  Limpar as especialidades selecionadas antes de carregar novo paciente
        limparEspecialidades();

        // Resetar variáveis de estado
        this.paciente = new Paciente();
        this.pacienteEspecialidades = new ArrayList<>();

        // Resetar modo edição
        modoEdicao = false;

        System.out.println("✅ Estado anterior limpo");
    }

    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;

    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblDataNascimento;
    private javax.swing.JLabel lblIdade;
    private javax.swing.JLabel lblNomeDaMae;
    private javax.swing.JLabel lblCpf;
    private javax.swing.JLabel lblSus;
    private javax.swing.JLabel lblTelefone;
    private javax.swing.JLabel lblEndereco;

}