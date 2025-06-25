package com.mycompany.viewNaoUsadas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FormularioDados2 extends javax.swing.JPanel {

    // Lista para armazenar os registros de pacientes
    private List<Paciente> listaPacientes;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public FormularioDados2() {
        listaPacientes = new ArrayList<>();
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
        lblNome = new javax.swing.JLabel();
        lblDataNascimento = new javax.swing.JLabel();
        lblIdade = new javax.swing.JLabel();
        lblNomeDaMae = new javax.swing.JLabel();
        lblCpf = new javax.swing.JLabel();
        lblSus = new javax.swing.JLabel();
        lblTelefone = new javax.swing.JLabel();
        lblEndereco = new javax.swing.JLabel();

        // Campos de texto
        txtNome = new javax.swing.JTextField();
        txtDataNascimento = new javax.swing.JTextField();
        txtIdade = new javax.swing.JTextField();
        txtNomeDaMae = new javax.swing.JTextField();
        txtCpf = new javax.swing.JTextField();
        txtSus = new javax.swing.JTextField();
        txtTelefone = new javax.swing.JTextField();
        txtEndereco = new javax.swing.JTextField();

        // Botões
        btnSalvar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        btnListar = new javax.swing.JButton();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("jMenu3");

        // Configurar título
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 20));
        lblTitulo.setForeground(new java.awt.Color(51, 51, 51));
        lblTitulo.setText("Cadastro de Pacientes");

        // Configurar labels
        lblNome.setFont(new java.awt.Font("Arial", 0, 12));
        lblNome.setText("Nome Completo:");

        lblDataNascimento.setFont(new java.awt.Font("Arial", 0, 12));
        lblDataNascimento.setText("Data de Nascimento (dd/mm/aaaa):");

        lblIdade.setFont(new java.awt.Font("Arial", 0, 12));
        lblIdade.setText("Idade:");

        lblNomeDaMae.setFont(new java.awt.Font("Arial", 0, 12));
        lblNomeDaMae.setText("Nome da Mãe:");

        lblCpf.setFont(new java.awt.Font("Arial", 0, 12));
        lblCpf.setText("CPF:");

        lblSus.setFont(new java.awt.Font("Arial", 0, 12));
        lblSus.setText("Cartão do SUS:");

        lblTelefone.setFont(new java.awt.Font("Arial", 0, 12));
        lblTelefone.setText("Telefone:");

        lblEndereco.setFont(new java.awt.Font("Arial", 0, 12));
        lblEndereco.setText("Endereço:");

        // Configurar campos de texto
        txtNome.setFont(new java.awt.Font("Arial", 0, 12));
        txtNome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtDataNascimento.setFont(new java.awt.Font("Arial", 0, 12));
        txtDataNascimento.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Campo idade não editável e com aparência diferenciada
        txtIdade.setFont(new java.awt.Font("Arial", 0, 12));
        txtIdade.setEditable(false);
        txtIdade.setBackground(new java.awt.Color(240, 240, 240));
        txtIdade.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtNomeDaMae.setFont(new java.awt.Font("Arial", 0, 12));
        txtNomeDaMae.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtCpf.setFont(new java.awt.Font("Arial", 0, 12));
        txtCpf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtSus.setFont(new java.awt.Font("Arial", 0, 12));
        txtSus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtTelefone.setFont(new java.awt.Font("Arial", 0, 12));
        txtTelefone.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        txtEndereco.setFont(new java.awt.Font("Arial", 0, 12));
        txtEndereco.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Configurar botões
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
                                                        .addComponent(lblNome)
                                                        .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                                        .addComponent(lblDataNascimento)
                                                        .addComponent(txtDataNascimento)
                                                        .addComponent(lblIdade)
                                                        .addComponent(txtIdade)
                                                        .addComponent(lblNomeDaMae)
                                                        .addComponent(txtNomeDaMae))
                                                .addGap(50, 50, 50)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(lblCpf)
                                                        .addComponent(txtCpf, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                                        .addComponent(lblSus)
                                                        .addComponent(txtSus)
                                                        .addComponent(lblTelefone)
                                                        .addComponent(txtTelefone)
                                                        .addComponent(lblEndereco)
                                                        .addComponent(txtEndereco)))
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
                                        .addComponent(lblNome)
                                        .addComponent(lblCpf))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblDataNascimento)
                                        .addComponent(lblSus))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtSus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblIdade)
                                        .addComponent(lblTelefone))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtIdade, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblNomeDaMae)
                                        .addComponent(lblEndereco))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtNomeDaMae, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnListar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(50, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Método para configurar eventos
    private void setupEvents() {
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
                listarPacientes();
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

    // Método para salvar paciente
    private void salvarPaciente() {
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

            Paciente paciente = new Paciente();
            
            // Preenchimento dos campos
            paciente.nome = txtNome.getText().trim();
            paciente.cpf = txtCpf.getText().trim();
            
            // Campos opcionais
            if (!txtDataNascimento.getText().isEmpty()) {
                try {
                    paciente.dataNascimento = dateFormat.parse(txtDataNascimento.getText());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Formato de data inválido! Use dd/mm/aaaa",
                            "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                    txtDataNascimento.requestFocus();
                    return;
                }
            }
            
            if (!txtIdade.getText().isEmpty()) {
                try {
                    paciente.idade = Integer.parseInt(txtIdade.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Idade deve ser um número inteiro!",
                            "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!txtNomeDaMae.getText().trim().isEmpty()) {
                paciente.nomeDaMae = txtNomeDaMae.getText().trim();
            }
            
            if (!txtSus.getText().trim().isEmpty()) {
                paciente.sus = txtSus.getText().trim();
            }
            
            if (!txtTelefone.getText().trim().isEmpty()) {
                paciente.telefone = txtTelefone.getText().trim();
            }
            
            if (!txtEndereco.getText().trim().isEmpty()) {
                paciente.endereco = txtEndereco.getText().trim();
            }

            listaPacientes.add(paciente);
            
            JOptionPane.showMessageDialog(this,
                    "Paciente cadastrado com sucesso!\nTotal de pacientes: " + listaPacientes.size(),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            limparCampos();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar paciente: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
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
        txtNome.requestFocus();
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
            
            lista.append("  Nome: ").append(pac.nome).append("\n");
            if (pac.dataNascimento != null) {
                lista.append("  Data Nascimento: ").append(dateFormat.format(pac.dataNascimento)).append("\n");
            }
            if (pac.idade != null) {
                lista.append("  Idade: ").append(pac.idade).append(" anos\n");
            }
            if (pac.nomeDaMae != null) {
                lista.append("  Nome da Mãe: ").append(pac.nomeDaMae).append("\n");
            }
            lista.append("  CPF: ").append(pac.cpf).append("\n");
            if (pac.sus != null) {
                lista.append("  SUS: ").append(pac.sus).append("\n");
            }
            if (pac.telefone != null) {
                lista.append("  Telefone: ").append(pac.telefone).append("\n");
            }
            if (pac.endereco != null) {
                lista.append("  Endereço: ").append(pac.endereco).append("\n");
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

    // Classe interna para representar um paciente
    private static class Paciente {
        String nome;
        Date dataNascimento;
        Integer idade;
        String nomeDaMae;
        String cpf;
        String sus;
        String telefone;
        String endereco;
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
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnListar;
    // End of variables declaration//GEN-END:variables
}