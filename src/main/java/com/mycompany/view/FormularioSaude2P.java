package com.mycompany.view;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormularioSaude2P extends javax.swing.JPanel implements PatientSelectionListener {

    // Lista para armazenar os registros de sinais vitais
    private List<Paciente> listaPacientes;
    Paciente paciente = new Paciente();
    private DecimalFormat df = new DecimalFormat("#.##");
    private PacienteDAO pacienteDAO;
    private Printer printer;
    
    // Controla se está em modo de edição
    private boolean modoEdicao = false; 

    public FormularioSaude2P(PacienteDAO pacienteDAO, PacienteEspecialidadeDAO pacienteEspecialidadeDAO, EspecialidadeDAO especialidadeDAO, List<Especialidade> especialidades) {
    listaPacientes = new ArrayList<>();
    initComponents();
    this.pacienteDAO = pacienteDAO;
    
    // Inicializar o printer com as dependências necessárias
    this.printer = new Printer(this, pacienteEspecialidadeDAO, especialidadeDAO,especialidades);
    
    setupEvents();
    setOpaque(false);
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
        lblPressaoArterial = new javax.swing.JLabel();
        lblFrequenciaCardiaca = new javax.swing.JLabel();
        lblFrequenciaRespiratoria = new javax.swing.JLabel();
        lblTemperatura = new javax.swing.JLabel();
        lblHemoglicoteste = new javax.swing.JLabel();
        lblSaturacaoOxigenio = new javax.swing.JLabel();
        lblPeso = new javax.swing.JLabel();
        lblAltura = new javax.swing.JLabel();
        lblImc = new javax.swing.JLabel();

        // Campos de texto
        txtPressaoArterial = new javax.swing.JTextField();
        txtFrequenciaCardiaca = new javax.swing.JTextField();
        txtFrequenciaRespiratoria = new javax.swing.JTextField();
        txtTemperatura = new javax.swing.JTextField();
        txtHemoglicoteste = new javax.swing.JTextField();
        txtSaturacaoOxigenio = new javax.swing.JTextField();
        txtPeso = new javax.swing.JTextField();
        txtAltura = new javax.swing.JTextField();
        txtImc = new javax.swing.JTextField();

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

        // Configurar título
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 20));
        lblTitulo.setForeground(new java.awt.Color(51, 51, 51));
        lblTitulo.setText("Registro de Sinais Vitais");

        // Configurar labels
        lblPressaoArterial.setFont(new java.awt.Font("Arial", 0, 12));
        lblPressaoArterial.setText("Pressão Arterial (mmHg):");

        lblFrequenciaCardiaca.setFont(new java.awt.Font("Arial", 0, 12));
        lblFrequenciaCardiaca.setText("Frequência Cardíaca (bpm):");

        lblFrequenciaRespiratoria.setFont(new java.awt.Font("Arial", 0, 12));
        lblFrequenciaRespiratoria.setText("Freq. Respiratória (irpm):");

        lblTemperatura.setFont(new java.awt.Font("Arial", 0, 12));
        lblTemperatura.setText("Temperatura (°C):");

        lblHemoglicoteste.setFont(new java.awt.Font("Arial", 0, 12));
        lblHemoglicoteste.setText("Hemoglicoteste (mg/dL):");

        lblSaturacaoOxigenio.setFont(new java.awt.Font("Arial", 0, 12));
        lblSaturacaoOxigenio.setText("Saturação O₂ (%):");

        lblPeso.setFont(new java.awt.Font("Arial", 0, 12));
        lblPeso.setText("Peso (kg):");

        lblAltura.setFont(new java.awt.Font("Arial", 0, 12));
        lblAltura.setText("Altura (m):");

        lblImc.setFont(new java.awt.Font("Arial", 0, 12));
        lblImc.setText("IMC (kg/m²):");

        // Configurar campos de texto com tamanhos otimizados
        
        // Campo grande (mais texto esperado)
        txtPressaoArterial.setFont(new java.awt.Font("Arial", 0, 12));
        txtPressaoArterial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Campos médios
        txtFrequenciaCardiaca.setFont(new java.awt.Font("Arial", 0, 12));
        txtFrequenciaCardiaca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtFrequenciaRespiratoria.setFont(new java.awt.Font("Arial", 0, 12));
        txtFrequenciaRespiratoria.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtHemoglicoteste.setFont(new java.awt.Font("Arial", 0, 12));
        txtHemoglicoteste.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Campos pequenos
        txtTemperatura.setFont(new java.awt.Font("Arial", 0, 12));
        txtTemperatura.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtSaturacaoOxigenio.setFont(new java.awt.Font("Arial", 0, 12));
        txtSaturacaoOxigenio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtPeso.setFont(new java.awt.Font("Arial", 0, 12));
        txtPeso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtAltura.setFont(new java.awt.Font("Arial", 0, 12));
        txtAltura.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Campo IMC não editável e com aparência diferenciada
        txtImc.setFont(new java.awt.Font("Arial", 0, 12));
        txtImc.setEditable(false);
        txtImc.setBackground(new java.awt.Color(240, 240, 240));
        txtImc.setBorder(BorderFactory.createCompoundBorder(
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
                                        
                                        // Campo grande (largura total - 440px)
                                        .addComponent(lblPressaoArterial)
                                        .addComponent(txtPressaoArterial, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        
                                        // Linha com FC e FR lado a lado
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblFrequenciaCardiaca)
                                                        .addComponent(txtFrequenciaCardiaca, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(20, 20, 20)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblFrequenciaRespiratoria)
                                                        .addComponent(txtFrequenciaRespiratoria, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        
                                        // Linha com Temperatura, Saturação e Hemoglicoteste lado a lado
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblTemperatura)
                                                        .addComponent(txtTemperatura, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(25, 25, 25)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblSaturacaoOxigenio)
                                                        .addComponent(txtSaturacaoOxigenio, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(25, 25, 25)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblHemoglicoteste)
                                                        .addComponent(txtHemoglicoteste, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        
                                        // Linha com Peso, Altura e IMC lado a lado
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblPeso)
                                                        .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(25, 25, 25)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblAltura)
                                                        .addComponent(txtAltura, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(25, 25, 25)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblImc)
                                                        .addComponent(txtImc, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        
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
                                
                                // Campo grande
                                .addComponent(lblPressaoArterial)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPressaoArterial, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                
                                // Linha com FC e FR
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblFrequenciaCardiaca)
                                        .addComponent(lblFrequenciaRespiratoria))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtFrequenciaCardiaca, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtFrequenciaRespiratoria, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                
                                // Linha com Temperatura, Saturação O2 e Hemoglicoteste
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblTemperatura)
                                        .addComponent(lblSaturacaoOxigenio)
                                        .addComponent(lblHemoglicoteste))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtTemperatura, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtSaturacaoOxigenio, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtHemoglicoteste, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                
                                // Linha com Peso, Altura e IMC
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblPeso)
                                        .addComponent(lblAltura)
                                        .addComponent(lblImc))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtAltura, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtImc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                
                                // Botões
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(30, Short.MAX_VALUE))
        );
        
        // Define tamanho preferido do painel - MESMO TAMANHO do FormularioDados2P
        this.setPreferredSize(new Dimension(500, 620));
    }// </editor-fold>//GEN-END:initComponents

    //Método para preencher os campos do formulário com dados da tabela
    public void preencherCamposComDadosTabela(Paciente patientData) {
        //System.out.println("preencherCamposComDadosTabela: " + patientData.toString());
        if (patientData != null) {
            // Preencher os campos com os dados da tabela
            txtPressaoArterial.setText(patientData.getPaXmmhg() != null ? patientData.getPaXmmhg() : "");
            txtFrequenciaCardiaca.setText(patientData.getFcBpm() != null ? patientData.getFcBpm().toString() : "");
            txtFrequenciaRespiratoria.setText(patientData.getFrIbpm() != null ? patientData.getFrIbpm().toString() : "");
            txtTemperatura.setText(patientData.getTemperaturaC() != null ? patientData.getTemperaturaC().toString() : "");
            txtHemoglicoteste.setText(patientData.getHgtMgld() != null ? patientData.getHgtMgld().toString() : "");
            txtSaturacaoOxigenio.setText(patientData.getSpo2() != null ? patientData.getSpo2().toString() : "");
            txtPeso.setText(patientData.getPeso() != null ? patientData.getPeso().toString() : "");
            txtAltura.setText(patientData.getAltura() != null ? patientData.getAltura().toString() : "");
            txtImc.setText(patientData.getImc() != null ? patientData.getImc().toString() : "");
            
            aplicarBloqueioCondicional();
            modoEdicao = false; // Garantir que não está em modo edição
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
    
    // Método para configurar eventos
    private void setupEvents() {
        
        adicionarListenersCamposVazios();
        
        // Eventos para calcular IMC automaticamente quando peso ou altura mudarem
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

    // Método para calcular IMC automaticamente
    private void calcularImcAutomaticamente() {
        try {
            String pesoStr = txtPeso.getText().replace(",", ".");
            String alturaStr = txtAltura.getText().replace(",", ".");

            // Verifica se ambos os campos têm valores válidos
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

    // Método para obter classificação do IMC
    private String getClassificacaoImc(float imc) {
        if (imc < 18.5) return "Abaixo do peso";
        else if (imc < 25) return "Peso normal";
        else if (imc < 30) return "Sobrepeso";
        else if (imc < 35) return "Obesidade grau I";
        else if (imc < 40) return "Obesidade grau II";
        else return "Obesidade grau III";
    }

    // Método para salvar paciente
    private void salvarPaciente() {
        
        // Verifica se há um paciente selecionado
        if (paciente == null || paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Paciente não selecionado!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Paciente pacienteSalvo = paciente;

            // Converter campos não vazios
            if (!txtPressaoArterial.getText().isEmpty()) {
                pacienteSalvo.setPaXmmhg(txtPressaoArterial.getText().replace(",", "."));
            }
            if (!txtFrequenciaCardiaca.getText().isEmpty()) {
                pacienteSalvo.setFcBpm(Float.parseFloat(txtFrequenciaCardiaca.getText().replace(",", ".")));
            }
            if (!txtFrequenciaRespiratoria.getText().isEmpty()) {
                pacienteSalvo.setFrIbpm(Float.parseFloat(txtFrequenciaRespiratoria.getText().replace(",", ".")));
            }
            if (!txtTemperatura.getText().isEmpty()) {
                pacienteSalvo.setTemperaturaC(Float.parseFloat(txtTemperatura.getText().replace(",", ".")));
            }
            if (!txtHemoglicoteste.getText().isEmpty()) {
                pacienteSalvo.setHgtMgld(Float.parseFloat(txtHemoglicoteste.getText().replace(",", ".")));
            }
            if (!txtSaturacaoOxigenio.getText().isEmpty()) {
                pacienteSalvo.setSpo2(Float.parseFloat(txtSaturacaoOxigenio.getText().replace(",", ".")));
            }
            if (!txtPeso.getText().isEmpty()) {
                pacienteSalvo.setPeso(Float.parseFloat(txtPeso.getText().replace(",", ".")));
            }
            if (!txtAltura.getText().isEmpty()) {
                pacienteSalvo.setAltura(Float.parseFloat(txtAltura.getText().replace(",", ".")));
            }
            if (!txtImc.getText().isEmpty()) {
                pacienteSalvo.setImc(Float.parseFloat(txtImc.getText().replace(",", ".")));
                // Mostrar classificação do IMC quando salvar
                String classificacao = getClassificacaoImc(pacienteSalvo.getImc());
                JOptionPane.showMessageDialog(this,
                    "Registro salvo com sucesso!\n" +
                    "IMC: " + df.format(pacienteSalvo.getImc()) + " - " + classificacao + "\n",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Registro salvo com sucesso!\nTotal de registros: " + (listaPacientes.size() + 1),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            
            //System.out.println("paciente Salvo:" + pacienteSalvo.toString());
            pacienteDAO.atualizar(pacienteSalvo);
            
            // Cancelar edição - voltar ao estado original
            modoEdicao = false;
            aplicarBloqueioCondicional();
            btnEditar.setText("Editar");
            btnEditar.setBackground(new Color(255, 152, 0));
            preencherCamposComDadosTabela(pacienteSalvo);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique se todos os valores numéricos estão corretos!",
                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            
            System.out.println("Erro ao salvar os dados:" + ex);
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
        txtPressaoArterial.setText("");
        txtFrequenciaCardiaca.setText("");
        txtFrequenciaRespiratoria.setText("");
        txtTemperatura.setText("");
        txtHemoglicoteste.setText("");
        txtSaturacaoOxigenio.setText("");
        txtPeso.setText("");
        txtAltura.setText("");
        txtImc.setText("");
        //txtPressaoArterial.requestFocus();
    }

    // Método para listar pacientes
    private void listarPacientes() {
        if (listaPacientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum registro de sinais vitais cadastrado.",
                    "Lista Vazia", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder lista = new StringBuilder("REGISTROS DE SINAIS VITAIS:\n\n");
        for (int i = 0; i < listaPacientes.size(); i++) {
            Paciente reg = listaPacientes.get(i);
            lista.append("Registro ").append(i + 1).append(":\n");

            if (reg.getPaXmmhg() != null) lista.append("  PA: ").append(reg.getPaXmmhg()).append(" mmHg\n");
            if (reg.getFcBpm() != null) lista.append("  FC: ").append(reg.getFcBpm()).append(" bpm\n");
            if (reg.getFrIbpm() != null) lista.append("  FR: ").append(reg.getFrIbpm()).append(" irpm\n");
            if (reg.getTemperaturaC() != null) lista.append("  Temp: ").append(reg.getTemperaturaC()).append(" °C\n");
            if (reg.getHgtMgld() != null) lista.append("  HGT: ").append(reg.getHgtMgld()).append(" mg/dL\n");
            if (reg.getSpo2() != null) lista.append("  SpO₂: ").append(reg.getSpo2()).append(" %\n");
            if (reg.getPeso() != null) lista.append("  Peso: ").append(reg.getPeso()).append(" kg\n");
            if (reg.getAltura() != null) lista.append("  Altura: ").append(reg.getAltura()).append(" m\n");
            if (reg.getImc() != null) {
                lista.append("  IMC: ").append(reg.getImc()).append(" kg/m² (")
                        .append(getClassificacaoImc(reg.getImc())).append(")\n");
            }

            lista.append("\n");
        }

        JTextArea textArea = new JTextArea(lista.toString());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", 0, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(520, 500));

        JOptionPane.showMessageDialog(this, scrollPane, "Lista de Registros",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onPatientSelected(Paciente patientData) {
        paciente = patientData;
        preencherCamposComDadosTabela(patientData);
    }
    
    @Override
    public void onPatientSelected(Paciente patientData, List<PacienteEspecialidade> pacienteEspecialidadeData) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;

    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblPressaoArterial;
    private javax.swing.JLabel lblFrequenciaCardiaca;
    private javax.swing.JLabel lblFrequenciaRespiratoria;
    private javax.swing.JLabel lblTemperatura;
    private javax.swing.JLabel lblHemoglicoteste;
    private javax.swing.JLabel lblSaturacaoOxigenio;
    private javax.swing.JLabel lblPeso;
    private javax.swing.JLabel lblAltura;
    private javax.swing.JLabel lblImc;

    private javax.swing.JTextField txtPressaoArterial;
    private javax.swing.JTextField txtFrequenciaCardiaca;
    private javax.swing.JTextField txtFrequenciaRespiratoria;
    private javax.swing.JTextField txtTemperatura;
    private javax.swing.JTextField txtHemoglicoteste;
    private javax.swing.JTextField txtSaturacaoOxigenio;
    private javax.swing.JTextField txtPeso;
    private javax.swing.JTextField txtAltura;
    private javax.swing.JTextField txtImc;

    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnImprimir;
    // End of variables declaration//GEN-END:variables
}