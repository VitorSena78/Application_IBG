package com.mycompany.view;

import com.mycompany.listener.PatientUpdateListener;
import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.mycompany.printer.Printer;
import com.mycompany.service.EspecialidadeService;
import com.mycompany.service.PacienteEspecialidadeService;
import com.mycompany.service.PacienteService;
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
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.common.errors.ApiException;

public class FormularioSaude2P extends JPanel implements PatientSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(FormularioSaude2P.class.getName());
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    // Services
    private PacienteService pacienteService;
    private PacienteEspecialidadeService pacienteEspecialidadeService;
    private EspecialidadeService especialidadeService;
    
    // Dados
    private Paciente paciente = new Paciente();
    private DecimalFormat df = new DecimalFormat("#.##");
    private Printer printer;
    
    // Controla se está em modo de edição
    private boolean modoEdicao = false;
    private PatientUpdateListener patientUpdateListener;

    // Componentes
    private JLabel lblTitulo;
    private JTextField txtPressaoArterial, txtFrequenciaCardiaca, txtFrequenciaRespiratoria;
    private JTextField txtTemperatura, txtHemoglicoteste, txtSaturacaoOxigenio;
    private JTextField txtPeso, txtAltura, txtImc;
    private JButton btnSalvar, btnEditar, btnExcluir, btnImprimir;

    public FormularioSaude2P(PacienteService pacienteService, 
                           PacienteEspecialidadeService pacienteEspecialidadeService, 
                           EspecialidadeService especialidadeService, 
                           List<Especialidade> especialidades) {
        
        this.pacienteService = pacienteService;
        this.pacienteEspecialidadeService = pacienteEspecialidadeService;
        this.especialidadeService = especialidadeService;
        
        initComponents();
        
        this.printer = new Printer(this, pacienteEspecialidadeService, especialidadeService, especialidades);
        setupEvents();
        setOpaque(false);
        
        // Linha temporária para debug
        debugBotoes();
    }
    
    public void debugBotoes() {
            LOGGER.info("=== DEBUG DOS BOTÕES ===");
            LOGGER.info("btnSalvar: " + (btnSalvar != null ? "Visível=" + btnSalvar.isVisible() + ", Enabled=" + btnSalvar.isEnabled() : "NULL"));
            LOGGER.info("btnEditar: " + (btnEditar != null ? "Visível=" + btnEditar.isVisible() + ", Enabled=" + btnEditar.isEnabled() : "NULL"));
            LOGGER.info("btnExcluir: " + (btnExcluir != null ? "Visível=" + btnExcluir.isVisible() + ", Enabled=" + btnExcluir.isEnabled() : "NULL"));
            LOGGER.info("btnImprimir: " + (btnImprimir != null ? "Visível=" + btnImprimir.isVisible() + ", Enabled=" + btnImprimir.isEnabled() : "NULL"));

            // Verificar se os botões estão no painel
            Component[] componentes = getComponents();
            LOGGER.info("Total de componentes no formulário: " + componentes.length);

            for (int i = 0; i < componentes.length; i++) {
                Component comp = componentes[i];
                if (comp instanceof JPanel) {
                    JPanel painel = (JPanel) comp;
                    if (painel.getLayout() instanceof FlowLayout) {
                        LOGGER.info("Painel de botões encontrado com " + painel.getComponentCount() + " componentes");
                        for (int j = 0; j < painel.getComponentCount(); j++) {
                            Component botao = painel.getComponent(j);
                            if (botao instanceof JButton) {
                                JButton btn = (JButton) botao;
                                LOGGER.info("  - Botão: '" + btn.getText() + "' (Visível: " + btn.isVisible() + ")");
                            }
                        }
                    }
                }
            }
        }
    
    public void setPatientUpdateListener(PatientUpdateListener listener) {
        this.patientUpdateListener = listener;
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
    private void initComponents() {
        // **MUDANÇA PRINCIPAL**: Usar GridBagLayout ao invés de GroupLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Configurações padrão do GridBagConstraints
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Criar componentes
        criarComponentes();
        
        // Montar layout seguindo o guia do FormularioDados2P
        criarTitulo(gbc);
        criarSecaoSinaisVitais(gbc);
        criarSecaoAntropometrica(gbc);
        criarSecaoBotoes(gbc);
        
        // Espaço flexível no final para responsividade
        gbc.gridx = 0; gbc.gridy = 20;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(Box.createVerticalGlue(), gbc);
        
        // **MELHORIA**: Definir tamanhos responsivos
        setPreferredSize(new Dimension(500, 650));
        setMinimumSize(new Dimension(350, 500));
    }

    private void criarComponentes() {
        // Título
        lblTitulo = new JLabel("Registro de Sinais Vitais");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(51, 51, 51));
        
        // Campos de texto com configuração uniforme
        txtPressaoArterial = criarCampoTexto();
        txtFrequenciaCardiaca = criarCampoTexto();
        txtFrequenciaRespiratoria = criarCampoTexto();
        txtTemperatura = criarCampoTexto();
        txtHemoglicoteste = criarCampoTexto();
        txtSaturacaoOxigenio = criarCampoTexto();
        txtPeso = criarCampoTexto();
        txtAltura = criarCampoTexto();
        
        // Campo IMC (não editável)
        txtImc = criarCampoTexto();
        txtImc.setEditable(false);
        txtImc.setBackground(new Color(240, 240, 240));
        
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
    
    private void criarSecaoSinaisVitais(GridBagConstraints gbc) {
        //  Agrupar sinais vitais com painel com borda
        JPanel painelSinaisVitais = new JPanel(new GridBagLayout());
        painelSinaisVitais.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER_COLOR), "Sinais Vitais"));
        painelSinaisVitais.setOpaque(false);

        GridBagConstraints gbcInterno = new GridBagConstraints();
        gbcInterno.insets = new Insets(5, 5, 5, 5);
        gbcInterno.anchor = GridBagConstraints.WEST;

        // Pressão Arterial (campo largo) - Linha 0
        gbcInterno.gridx = 0; gbcInterno.gridy = 0;
        gbcInterno.gridwidth = 1; gbcInterno.weightx = 0.0; 
        gbcInterno.fill = GridBagConstraints.NONE;
        painelSinaisVitais.add(new JLabel("Pressão Arterial (mmHg):"), gbcInterno);

        gbcInterno.gridx = 1; gbcInterno.gridwidth = 3;
        gbcInterno.weightx = 1.0; gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelSinaisVitais.add(txtPressaoArterial, gbcInterno);

        // Frequência Cardíaca - Linha 1
        gbcInterno.gridx = 0; gbcInterno.gridy = 1;
        gbcInterno.gridwidth = 1; gbcInterno.weightx = 0.0;
        gbcInterno.fill = GridBagConstraints.NONE;
        painelSinaisVitais.add(new JLabel("Freq. Cardíaca (bpm):"), gbcInterno);

        gbcInterno.gridx = 1; gbcInterno.gridwidth = 3;
        gbcInterno.weightx = 1.0; gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelSinaisVitais.add(txtFrequenciaCardiaca, gbcInterno);

        // Frequência Respiratória - Linha 2
        gbcInterno.gridx = 0; gbcInterno.gridy = 2;
        gbcInterno.gridwidth = 1; gbcInterno.weightx = 0.0;
        gbcInterno.fill = GridBagConstraints.NONE;
        painelSinaisVitais.add(new JLabel("Freq. Resp. (irpm):"), gbcInterno);

        gbcInterno.gridx = 1; gbcInterno.gridwidth = 3;
        gbcInterno.weightx = 1.0; gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelSinaisVitais.add(txtFrequenciaRespiratoria, gbcInterno);

        // Temperatura - Linha 3
        gbcInterno.gridx = 0; gbcInterno.gridy = 3;
        gbcInterno.gridwidth = 1; gbcInterno.weightx = 0.0;
        gbcInterno.fill = GridBagConstraints.NONE;
        painelSinaisVitais.add(new JLabel("Temperatura (°C):"), gbcInterno);

        gbcInterno.gridx = 1; gbcInterno.gridwidth = 3;
        gbcInterno.weightx = 1.0; gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelSinaisVitais.add(txtTemperatura, gbcInterno);

        // Saturação O₂ - Linha 4
        gbcInterno.gridx = 0; gbcInterno.gridy = 4;
        gbcInterno.gridwidth = 1; gbcInterno.weightx = 0.0;
        gbcInterno.fill = GridBagConstraints.NONE;
        painelSinaisVitais.add(new JLabel("Saturação O₂ (%):"), gbcInterno);

        gbcInterno.gridx = 1; gbcInterno.gridwidth = 3;
        gbcInterno.weightx = 1.0; gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelSinaisVitais.add(txtSaturacaoOxigenio, gbcInterno);

        // Hemoglicoteste - Linha 5
        gbcInterno.gridx = 0; gbcInterno.gridy = 5;
        gbcInterno.gridwidth = 1; gbcInterno.weightx = 0.0;
        gbcInterno.fill = GridBagConstraints.NONE;
        painelSinaisVitais.add(new JLabel("Hemoglicoteste (mg/dL):"), gbcInterno);

        gbcInterno.gridx = 1; gbcInterno.gridwidth = 3;
        gbcInterno.weightx = 1.0; gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelSinaisVitais.add(txtHemoglicoteste, gbcInterno);

        // Adicionar painel ao layout principal
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(painelSinaisVitais, gbc);
    }
    
    private void criarSecaoAntropometrica(GridBagConstraints gbc) {
        // **MELHORIA**: Seção antropométrica separada
        JPanel painelAntropometrico = new JPanel(new GridBagLayout());
        painelAntropometrico.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), "Dados Antropométricos"));
        painelAntropometrico.setOpaque(false);
        
        GridBagConstraints gbcInterno = new GridBagConstraints();
        gbcInterno.insets = new Insets(5, 5, 5, 5);
        gbcInterno.anchor = GridBagConstraints.WEST;
        
        // Peso e Altura na mesma linha
        gbcInterno.gridx = 0; gbcInterno.gridy = 0;
        gbcInterno.weightx = 0.0; gbcInterno.fill = GridBagConstraints.NONE;
        painelAntropometrico.add(new JLabel("Peso (kg):"), gbcInterno);
        
        gbcInterno.gridx = 1; gbcInterno.weightx = 0.4;
        gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelAntropometrico.add(txtPeso, gbcInterno);
        
        gbcInterno.gridx = 2; gbcInterno.weightx = 0.0;
        gbcInterno.fill = GridBagConstraints.NONE;
        painelAntropometrico.add(new JLabel("Altura (m):"), gbcInterno);
        
        gbcInterno.gridx = 3; gbcInterno.weightx = 0.6;
        gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelAntropometrico.add(txtAltura, gbcInterno);
        
        // IMC (campo largo, não editável)
        gbcInterno.gridx = 0; gbcInterno.gridy = 1;
        gbcInterno.gridwidth = 1; gbcInterno.weightx = 0.0;
        gbcInterno.fill = GridBagConstraints.NONE;
        painelAntropometrico.add(new JLabel("IMC (kg/m²):"), gbcInterno);
        
        gbcInterno.gridx = 1; gbcInterno.gridwidth = 3;
        gbcInterno.weightx = 1.0; gbcInterno.fill = GridBagConstraints.HORIZONTAL;
        painelAntropometrico.add(txtImc, gbcInterno);
        
        // Adicionar painel ao layout principal
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(painelAntropometrico, gbc);
    }
    
    private void criarSecaoBotoes(GridBagConstraints gbc) {
        // **MELHORIA**: Painel de botões centralizado
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        painelBotoes.setOpaque(false);
        
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnImprimir);
        
        gbc.gridx = 0; gbc.gridy = 3;
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
            setPreferredSize(new Dimension(500, 650));
        }
        
        revalidate();
        repaint();
    }

    // Método para preencher os campos do formulário com dados da tabela
    public void preencherCamposComDadosTabela(Paciente patientData) {
        if (patientData != null) {
            txtPressaoArterial.setText(patientData.getPaXMmhg() != null ? patientData.getPaXMmhg() : "");
            txtFrequenciaCardiaca.setText(patientData.getFcBpm() != null ? patientData.getFcBpm().toString() : "");
            txtFrequenciaRespiratoria.setText(patientData.getFrIbpm() != null ? patientData.getFrIbpm().toString() : "");
            txtTemperatura.setText(patientData.getTemperaturaC() != null ? patientData.getTemperaturaC().toString() : "");
            txtHemoglicoteste.setText(patientData.getHgtMgld() != null ? patientData.getHgtMgld().toString() : "");
            txtSaturacaoOxigenio.setText(patientData.getSpo2() != null ? patientData.getSpo2().toString() : "");
            txtPeso.setText(patientData.getPeso() != null ? patientData.getPeso().toString() : "");
            txtAltura.setText(patientData.getAltura() != null ? patientData.getAltura().toString() : "");
            txtImc.setText(patientData.getImc() != null ? patientData.getImc().toString() : "");
            
            aplicarBloqueioCondicional();
            modoEdicao = false;
        }
    }
    
    private void aplicarBloqueioCondicional() {
        JTextField[] campos = {
            txtPressaoArterial, txtFrequenciaCardiaca, txtFrequenciaRespiratoria,
            txtTemperatura, txtHemoglicoteste, txtSaturacaoOxigenio,
            txtPeso, txtAltura
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

        // IMC sempre permanece não editável
        txtImc.setEditable(false);
        txtImc.setBackground(new Color(240, 240, 240));
    }
    
    private void adicionarListenersCamposVazios() {
        JTextField[] campos = {
            txtPressaoArterial, txtFrequenciaCardiaca, txtFrequenciaRespiratoria,
            txtTemperatura, txtHemoglicoteste, txtSaturacaoOxigenio,
            txtPeso, txtAltura
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
    }
    
    public void atualizarEspecialidades(List<Especialidade> especialidades) {
        if (printer != null) {
            this.printer = new Printer(this, pacienteEspecialidadeService, especialidadeService, especialidades);
        }
        LOGGER.info("Especialidades atualizadas no FormularioSaude2P: " + especialidades.size());
    }
    
    private void setupEvents() {
        adicionarListenersCamposVazios();
        
        // Eventos para calcular IMC automaticamente
        txtPeso.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularImcAutomaticamente();
            }
        });

        txtAltura.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularImcAutomaticamente();
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
                    btnEditar.setText("Cancelar");
                    btnEditar.setBackground(new Color(158, 158, 158)); // Cor cinza
                } else {
                    // Cancelar edição - voltar ao estado original
                    modoEdicao = false;
                    aplicarBloqueioCondicional(); // Rebloquear campos preenchidos
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

    private void calcularImcAutomaticamente() {
        try {
            String pesoStr = txtPeso.getText().replace(",", ".");
            String alturaStr = txtAltura.getText().replace(",", ".");

            if (!pesoStr.isEmpty() && !alturaStr.isEmpty()) {
                float peso = Float.parseFloat(pesoStr);
                float altura = Float.parseFloat(alturaStr);

                if (peso > 0 && altura > 0) {
                    float imc = peso / (altura * altura);
                    txtImc.setText(df.format(imc));
                } else {
                    txtImc.setText("");
                }
            } else {
                txtImc.setText("");
            }
        } catch (NumberFormatException ex) {
            txtImc.setText("");
        }
    }

    private String getClassificacaoImc(float imc) {
        if (imc < 18.5) return "Abaixo do peso";
        else if (imc < 25) return "Peso normal";
        else if (imc < 30) return "Sobrepeso";
        else if (imc < 35) return "Obesidade grau I";
        else if (imc < 40) return "Obesidade grau II";
        else return "Obesidade grau III";
    }

    private void salvarPaciente() {
        // Verifica se há um paciente selecionado
        if (paciente == null || paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Paciente não selecionado!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Atualizar campos não vazios
            if (!txtPressaoArterial.getText().isEmpty()) {
                paciente.setPaXMmhg(txtPressaoArterial.getText().replace(",", "."));
            }
            if (!txtFrequenciaCardiaca.getText().isEmpty()) {
                paciente.setFcBpm(Float.parseFloat(txtFrequenciaCardiaca.getText().replace(",", ".")));
            }
            if (!txtFrequenciaRespiratoria.getText().isEmpty()) {
                paciente.setFrIbpm(Float.parseFloat(txtFrequenciaRespiratoria.getText().replace(",", ".")));
            }
            if (!txtTemperatura.getText().isEmpty()) {
                paciente.setTemperaturaC(Float.parseFloat(txtTemperatura.getText().replace(",", ".")));
            }
            if (!txtHemoglicoteste.getText().isEmpty()) {
                paciente.setHgtMgld(Float.parseFloat(txtHemoglicoteste.getText().replace(",", ".")));
            }
            if (!txtSaturacaoOxigenio.getText().isEmpty()) {
                paciente.setSpo2(Float.parseFloat(txtSaturacaoOxigenio.getText().replace(",", ".")));
            }
            if (!txtPeso.getText().isEmpty()) {
                paciente.setPeso(Float.parseFloat(txtPeso.getText().replace(",", ".")));
            }
            if (!txtAltura.getText().isEmpty()) {
                paciente.setAltura(Float.parseFloat(txtAltura.getText().replace(",", ".")));
            }
            if (!txtImc.getText().isEmpty()) {
                paciente.setImc(Float.parseFloat(txtImc.getText().replace(",", ".")));
            }

            LOGGER.info("Atualizando sinais vitais do paciente via API: " + paciente.toString());

            boolean sucesso = pacienteService.atualizar(paciente);

            if (!sucesso) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar dados do paciente!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar dados atualizados
            Paciente pacienteAtualizado = pacienteService.buscarPorId(paciente.getId());
            if (pacienteAtualizado != null) {
                this.paciente = pacienteAtualizado;

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
                }

                preencherCamposComDadosTabela(pacienteAtualizado);
            }

            // Mostrar classificação IMC se houver
            if (!txtImc.getText().isEmpty()) {
                String classificacao = getClassificacaoImc(paciente.getImc());
                JOptionPane.showMessageDialog(this,
                    "Sinais vitais salvos com sucesso!\n" +
                    "IMC: " + df.format(paciente.getImc()) + " - " + classificacao,
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Sinais vitais salvos com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }

            // Cancelar edição - voltar ao estado original
            modoEdicao = false;
            aplicarBloqueioCondicional();
            btnEditar.setText("Editar");
            btnEditar.setBackground(new Color(255, 152, 0));

        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Erro de formato nos dados", ex);
            JOptionPane.showMessageDialog(this, "Verifique se todos os valores numéricos estão corretos!",
                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao salvar", ex);
            JOptionPane.showMessageDialog(this, "Erro inesperado ao salvar: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirPaciente() {
        if (paciente == null || paciente.getId() == 0 || paciente.getNome().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Paciente não selecionado!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
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
                    int pacienteId = paciente.getId();

                    JOptionPane.showMessageDialog(this, "Paciente excluído com sucesso!",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    // NOTIFICAR OS PAINÉIS DA EXCLUSÃO
                    if (patientUpdateListener != null) {
                        patientUpdateListener.onPatientDeleted(pacienteId);
                    }

                    limparCampos();
                    this.paciente = new Paciente();
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
    
    private void imprimirDadosPaciente() {
        if (paciente == null || paciente.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Paciente não selecionado!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        printer.imprimirDadosPaciente(paciente);
    }

    public void limparCampos() {
        System.out.println("=== Limpando campos do FormularioSaude2P ===");

        // Executar limpeza na EDT para garantir thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Limpar campos de texto
                if (txtPressaoArterial != null) txtPressaoArterial.setText("");
                if (txtFrequenciaCardiaca != null) txtFrequenciaCardiaca.setText("");
                if (txtFrequenciaRespiratoria != null) txtFrequenciaRespiratoria.setText("");
                if (txtTemperatura != null) txtTemperatura.setText("");
                if (txtHemoglicoteste != null) txtHemoglicoteste.setText("");
                if (txtSaturacaoOxigenio != null) txtSaturacaoOxigenio.setText("");
                if (txtPeso != null) txtPeso.setText("");
                if (txtAltura != null) txtAltura.setText("");
                if (txtImc != null) txtImc.setText("");

                System.out.println("✅ Campos de texto limpos");

                // Resetar variáveis de estado de forma thread-safe
                synchronized (this) {
                    this.paciente = new Paciente();
                    System.out.println("✅ Variáveis de estado resetadas");
                }

                // Resetar estado dos campos e formulário
                modoEdicao = false;
                aplicarBloqueioCondicional();

                // Resetar botão editar
                if (btnEditar != null) {
                    btnEditar.setText("Editar");
                    btnEditar.setBackground(new Color(255, 152, 0));
                }

                // Forçar repaint de todos os componentes
                revalidate();
                repaint();

                System.out.println("✅ Campos limpos com sucesso no FormularioSaude2P");

            } catch (Exception e) {
                System.err.println("❌ Erro ao limpar campos: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onPatientSelected(Paciente patientData) {
        System.out.println("=== onPatientSelected FormularioSaude2P ===");
        System.out.println("Paciente: " + (patientData != null ? patientData.getNome() + " (ID: " + patientData.getId() + ")" : "NULL"));

        if (patientData != null) {
            // Verificar se é um paciente diferente do atual
            boolean mesmoPaciente = (this.paciente != null && 
                                   this.paciente.getId() != null && 
                                   patientData.getId() != null &&
                                   this.paciente.getId().equals(patientData.getId()));

            if (!mesmoPaciente) {
                System.out.println("Paciente diferente detectado - limpando estado anterior");
                // Limpar estado anterior apenas se for paciente diferente
                this.paciente = new Paciente();
                modoEdicao = false;
            }

            this.paciente = patientData;
            preencherCamposComDadosTabela(patientData);

            System.out.println("✅ Paciente configurado no formulário de saúde");
        } else {
            // Limpar formulário quando não há seleção
            limparCampos();
            System.out.println("✅ Formulário de saúde limpo - nenhum paciente selecionado");
        }
    }
    
    @Override
    public void onPatientSelected(Paciente patientData, List<PacienteEspecialidade> pacienteEspecialidadeData) {
        // Delegar para o método principal, já que este formulário não usa especialidades
        onPatientSelected(patientData);
    }
}