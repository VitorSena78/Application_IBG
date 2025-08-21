package com.mycompany.viewNaoUsadas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Formulario2 extends javax.swing.JPanel {

    // Lista para armazenar os registros de sinais vitais
    private List<SinaisVitais> listaRegistros;
    private DecimalFormat df = new DecimalFormat("#.##");

    public Formulario2() {
        listaRegistros = new ArrayList<>();
        initComponents();
        setupEvents();
        setOpaque(false); // Importante para o efeito de borda funcionar
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

        // Botões (removido btnCalcularImc)
        btnSalvar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        btnListar = new javax.swing.JButton();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("jMenu3");

        // Removido setBackground pois agora é feito no paintComponent
        // setBackground(new java.awt.Color(245, 248, 250));

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
        lblFrequenciaRespiratoria.setText("Frequência Respiratória (irpm):");

        lblTemperatura.setFont(new java.awt.Font("Arial", 0, 12));
        lblTemperatura.setText("Temperatura (°C):");

        lblHemoglicoteste.setFont(new java.awt.Font("Arial", 0, 12));
        lblHemoglicoteste.setText("Hemoglicoteste (mg/dL):");

        lblSaturacaoOxigenio.setFont(new java.awt.Font("Arial", 0, 12));
        lblSaturacaoOxigenio.setText("Saturação de O₂ (%):");

        lblPeso.setFont(new java.awt.Font("Arial", 0, 12));
        lblPeso.setText("Peso (kg):");

        lblAltura.setFont(new java.awt.Font("Arial", 0, 12));
        lblAltura.setText("Altura (m):");

        lblImc.setFont(new java.awt.Font("Arial", 0, 12));
        lblImc.setText("IMC (kg/m²):");

        // Configurar campos de texto
        txtPressaoArterial.setFont(new java.awt.Font("Arial", 0, 12));
        txtPressaoArterial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtFrequenciaCardiaca.setFont(new java.awt.Font("Arial", 0, 12));
        txtFrequenciaCardiaca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtFrequenciaRespiratoria.setFont(new java.awt.Font("Arial", 0, 12));
        txtFrequenciaRespiratoria.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtTemperatura.setFont(new java.awt.Font("Arial", 0, 12));
        txtTemperatura.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtHemoglicoteste.setFont(new java.awt.Font("Arial", 0, 12));
        txtHemoglicoteste.setBorder(BorderFactory.createCompoundBorder(
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

        // Configurar botões (removido btnCalcularImc)
        btnSalvar.setBackground(new java.awt.Color(76, 175, 80));
        btnSalvar.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvar.setFont(new java.awt.Font("Arial", 1, 12));
        btnSalvar.setText("Salvar");

        btnLimpar.setBackground(new java.awt.Color(255, 152, 0));
        btnLimpar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpar.setFont(new java.awt.Font("Arial", 1, 12));
        btnLimpar.setText("Limpar");

        btnListar.setBackground(new java.awt.Color(33, 150, 243));
        btnListar.setForeground(new java.awt.Color(255, 255, 255));
        btnListar.setFont(new java.awt.Font("Arial", 1, 12));
        btnListar.setText("Listar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblTitulo)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(lblPressaoArterial)
                                                        .addComponent(txtPressaoArterial, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                                        .addComponent(lblFrequenciaCardiaca)
                                                        .addComponent(txtFrequenciaCardiaca)
                                                        .addComponent(lblFrequenciaRespiratoria)
                                                        .addComponent(txtFrequenciaRespiratoria)
                                                        .addComponent(lblTemperatura)
                                                        .addComponent(txtTemperatura)
                                                        .addComponent(lblHemoglicoteste)
                                                        .addComponent(txtHemoglicoteste))
                                                .addGap(50, 50, 50)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(lblSaturacaoOxigenio)
                                                        .addComponent(txtSaturacaoOxigenio, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                                        .addComponent(lblPeso)
                                                        .addComponent(txtPeso)
                                                        .addComponent(lblAltura)
                                                        .addComponent(txtAltura)
                                                        .addComponent(lblImc)
                                                        .addComponent(txtImc)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnListar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(lblTitulo)
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblPressaoArterial)
                                        .addComponent(lblSaturacaoOxigenio))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtPressaoArterial, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtSaturacaoOxigenio, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblFrequenciaCardiaca)
                                        .addComponent(lblPeso))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtFrequenciaCardiaca, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblFrequenciaRespiratoria)
                                        .addComponent(lblAltura))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtFrequenciaRespiratoria, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtAltura, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblTemperatura)
                                        .addComponent(lblImc))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtTemperatura, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtImc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addComponent(lblHemoglicoteste)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtHemoglicoteste, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnListar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(30, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Método para configurar eventos
    private void setupEvents() {
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
                salvarRegistro();
            }
        });

        // Evento para limpar
        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparCampos();
            }
        });

        // Evento para listar
        btnListar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarRegistros();
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

    // Método para salvar registro
    private void salvarRegistro() {
        try {
            SinaisVitais registro = new SinaisVitais();

            // Converter campos não vazios
            if (!txtPressaoArterial.getText().isEmpty()) {
                registro.paXMmhg = Float.parseFloat(txtPressaoArterial.getText().replace(",", "."));
            }
            if (!txtFrequenciaCardiaca.getText().isEmpty()) {
                registro.fcBpm = Float.parseFloat(txtFrequenciaCardiaca.getText().replace(",", "."));
            }
            if (!txtFrequenciaRespiratoria.getText().isEmpty()) {
                registro.frIbpm = Float.parseFloat(txtFrequenciaRespiratoria.getText().replace(",", "."));
            }
            if (!txtTemperatura.getText().isEmpty()) {
                registro.temperaturaC = Float.parseFloat(txtTemperatura.getText().replace(",", "."));
            }
            if (!txtHemoglicoteste.getText().isEmpty()) {
                registro.hgtMgld = Float.parseFloat(txtHemoglicoteste.getText().replace(",", "."));
            }
            if (!txtSaturacaoOxigenio.getText().isEmpty()) {
                registro.spo2 = Float.parseFloat(txtSaturacaoOxigenio.getText().replace(",", "."));
            }
            if (!txtPeso.getText().isEmpty()) {
                registro.peso = Float.parseFloat(txtPeso.getText().replace(",", "."));
            }
            if (!txtAltura.getText().isEmpty()) {
                registro.altura = Float.parseFloat(txtAltura.getText().replace(",", "."));
            }
            if (!txtImc.getText().isEmpty()) {
                registro.imc = Float.parseFloat(txtImc.getText().replace(",", "."));
                // Mostrar classificação do IMC quando salvar
                String classificacao = getClassificacaoImc(registro.imc);
                JOptionPane.showMessageDialog(this,
                        "Registro salvo com sucesso!\n" +
                                "IMC: " + df.format(registro.imc) + " - " + classificacao + "\n" +
                                "Total de registros: " + (listaRegistros.size() + 1),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Registro salvo com sucesso!\nTotal de registros: " + (listaRegistros.size() + 1),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }

            listaRegistros.add(registro);
            limparCampos();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique se todos os valores numéricos estão corretos!",
                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
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
        txtPressaoArterial.requestFocus();
    }

    // Método para listar registros
    private void listarRegistros() {
        if (listaRegistros.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum registro de sinais vitais cadastrado.",
                    "Lista Vazia", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder lista = new StringBuilder("REGISTROS DE SINAIS VITAIS:\n\n");
        for (int i = 0; i < listaRegistros.size(); i++) {
            SinaisVitais reg = listaRegistros.get(i);
            lista.append("Registro ").append(i + 1).append(":\n");

            if (reg.paXMmhg != null) lista.append("  PA: ").append(reg.paXMmhg).append(" mmHg\n");
            if (reg.fcBpm != null) lista.append("  FC: ").append(reg.fcBpm).append(" bpm\n");
            if (reg.frIbpm != null) lista.append("  FR: ").append(reg.frIbpm).append(" irpm\n");
            if (reg.temperaturaC != null) lista.append("  Temp: ").append(reg.temperaturaC).append(" °C\n");
            if (reg.hgtMgld != null) lista.append("  HGT: ").append(reg.hgtMgld).append(" mg/dL\n");
            if (reg.spo2 != null) lista.append("  SpO₂: ").append(reg.spo2).append(" %\n");
            if (reg.peso != null) lista.append("  Peso: ").append(reg.peso).append(" kg\n");
            if (reg.altura != null) lista.append("  Altura: ").append(reg.altura).append(" m\n");
            if (reg.imc != null) {
                lista.append("  IMC: ").append(reg.imc).append(" kg/m² (")
                        .append(getClassificacaoImc(reg.imc)).append(")\n");
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

    // Classe interna para representar sinais vitais
    private static class SinaisVitais {
        Float paXMmhg;          // Pressão Arterial
        Float fcBpm;            // Frequência Cardíaca
        Float frIbpm;           // Frequência Respiratória
        Float temperaturaC;     // Temperatura
        Float hgtMgld;          // Hemoglicoteste
        Float spo2;             // Saturação de Oxigênio
        Float peso;             // Peso
        Float altura;           // Altura
        Float imc;              // IMC
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
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnListar;
    // End of variables declaration//GEN-END:variables
}