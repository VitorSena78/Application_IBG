package com.mycompany.projeto_ibg;

import com.mycompany.kafka.PacienteChangeListener;
import com.mycompany.kafka.PacienteEspecialidadeChangeListener;
import com.mycompany.kafka.PacienteEspecialidadeNotificationManager;
import com.mycompany.kafka.PacienteNotificationManager;
import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.mycompany.model.dao.EspecialidadeDAO;
import com.mycompany.model.dao.PacienteDAO;
import com.mycompany.model.dao.PacienteEspecialidadeDAO;
import com.mycompany.view.FormularioSaude2P;
import com.mycompany.view.FormularioDados2P;
import com.mycompany.view.MenuListener;
import com.mycompany.view.PainelSaude2;
import com.mycompany.view.PainelDados2;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Timer;

public class Main extends javax.swing.JFrame implements MenuListener, PacienteChangeListener, PacienteEspecialidadeChangeListener {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Main.class.getName());
    PacienteDAO pacienteDAO;
    List<Paciente> pacientes;
    EspecialidadeDAO especialidadeDAO;
    List<Especialidade> especialidades;
    PacienteEspecialidadeDAO pacienteEspecialidadeDAO;
    List<PacienteEspecialidade> pacienteEspecialidades;
    
    // Referências para os painéis ativos
    private PainelSaude2 painelSaudeAtivo;
    private PainelDados2 painelDadosAtivo;
    private FormularioSaude2P formularioSaudeAtivo;
    private FormularioDados2P formularioDadosAtivo;


    public Main() {
        initComponents();
        
        // Registrar como listener do menu
        menu31.addMenuListener(this);
        
        //carega o pacienteDAO
        pacienteDAO = new PacienteDAO();
        //carega a Lista de pacientes
        pacientes = pacienteDAO.listarTodos();
        
        //carega o especialidadeDAO
        especialidadeDAO = new EspecialidadeDAO();
        //carega a Lista de especialidades
        especialidades = especialidadeDAO.listarTodas();
        
        //carega o pacienteEspecialidadeDAO
        pacienteEspecialidadeDAO = new PacienteEspecialidadeDAO();
        //carega a Lista de pacienteEspecialidades
        pacienteEspecialidades = pacienteEspecialidadeDAO.listarTodos();
        
        // Registrar como listener de mudanças de pacientes e pacienteEspecialidade
        PacienteNotificationManager.getInstance().addListener(this);
        PacienteEspecialidadeNotificationManager.getInstance().addListener(this);
        
        // Inicializa com o painel padrão
        onSaudeSelected(); 
    }
    
    @Override
    public void onSaudeSelected() {
        
        painelSaudeAtivo = new PainelSaude2(pacientes);
        formularioSaudeAtivo = new FormularioSaude2P(pacienteDAO, pacienteEspecialidadeDAO, especialidadeDAO, especialidades);
        
        refreshContentPainel(painelSaudeAtivo);
        refreshContentFormulario(formularioSaudeAtivo);
        
        // Conectar os painéis painelSaude ao formularioSaude
        painelSaudeAtivo.setPatientSelectionListener(formularioSaudeAtivo);
        
        // Limpar referências dos outros painéis
        painelDadosAtivo = null;
        formularioDadosAtivo = null;
    }

    @Override
    public void onDadosSelected() {
        painelDadosAtivo = new PainelDados2(pacientes, pacienteEspecialidades);
        formularioDadosAtivo = new FormularioDados2P(pacienteDAO, pacienteEspecialidadeDAO, especialidadeDAO, especialidades);
        
        refreshContentPainel(painelDadosAtivo);
        refreshContentFormulario(formularioDadosAtivo);
        
        // Conectar os painéis painelDados ao formularioDados
        painelDadosAtivo.setPatientSelectionListener(formularioDadosAtivo);
       
        // Limpar referências dos outros painéis
        painelSaudeAtivo = null;
        formularioSaudeAtivo = null;
    }
    
    // Implementação dos métodos PacienteEspecialidadeChangeListener
     @Override
    public void onPacienteEspecialidadeAdded(PacienteEspecialidade pacienteEspecialidade) {
        System.out.println("Nova associação paciente-especialidade adicionada: " + 
                      "Paciente ID: " + pacienteEspecialidade.getPacienteId() + 
                      ", Especialidade ID: " + pacienteEspecialidade.getEspecialidadeId());

        // Atualizar a lista local
        this.pacienteEspecialidades.add(pacienteEspecialidade);

        // Atualizar apenas o painel de dados (que trabalha com especialidades)
        if (painelDadosAtivo != null) {
            // Recarregar a lista completa para garantir consistência
            //this.pacienteEspecialidades = pacienteEspecialidadeDAO.listarTodos();
            painelDadosAtivo.atualizarPacienteEspecialidade(this.pacienteEspecialidades);
        }

    }

    @Override
    public void onPacienteEspecialidadeUpdated(PacienteEspecialidade pacienteEspecialidade) {
        System.out.println("Associação paciente-especialidade atualizada: " + 
                      "Paciente ID: " + pacienteEspecialidade.getPacienteId() + 
                      ", Especialidade ID: " + pacienteEspecialidade.getEspecialidadeId());

        // Atualizar na lista local
        for (int i = 0; i < this.pacienteEspecialidades.size(); i++) {
            PacienteEspecialidade pe = this.pacienteEspecialidades.get(i);
            if (pe.getPacienteId() == pacienteEspecialidade.getPacienteId() && pe.getEspecialidadeId() == pacienteEspecialidade.getEspecialidadeId()) {
                this.pacienteEspecialidades.set(i, pacienteEspecialidade);
                break;
            }
        }

        // Atualizar painéis ativos
        if (painelDadosAtivo != null) {
            painelDadosAtivo.atualizarPacienteEspecialidade(this.pacienteEspecialidades);
        }
    }

    @Override
    public void onPacienteEspecialidadeDeleted(Integer pacienteId, Integer especialidadeId) {
        System.out.println("Associação paciente-especialidade removida: " + 
                      "Paciente ID: " + pacienteId + 
                      ", Especialidade ID: " + especialidadeId);
    
        // Remover da lista local
        this.pacienteEspecialidades.removeIf(pe -> 
            pe.getPacienteId()== pacienteId && 
            pe.getEspecialidadeId()== especialidadeId
        );
        
        System.out.println("pacienteEspecialidades removido: ");
        System.out.println(pacienteEspecialidades);

        // Atualizar painéis ativos
        if (painelDadosAtivo != null) {
            painelDadosAtivo.atualizarPacienteEspecialidade(this.pacienteEspecialidades);
        }
    }
    
    // Implementação dos métodos PacienteChangeListener
    @Override
    public void onPacienteAdded(Paciente paciente) {
        System.out.println("Novo paciente adicionado: " + paciente.getNome());
        System.out.println("onPacienteAdded: "+paciente.toString());
        
        // Atualizar a lista local
        pacientes.add(paciente);
        
        // Atualizar painéis ativos
        if (painelSaudeAtivo != null) {  
            painelSaudeAtivo.adicionarPaciente(paciente);
        }
        if (painelDadosAtivo != null) {
            painelDadosAtivo.adicionarPaciente(paciente);
        }
        
        // Mostrar notificação (opcional)
        showNotification("Novo paciente adicionado: " + paciente.getNome());
    }

    @Override
    public void onPacienteUpdated(Paciente paciente) {
        System.out.println("Paciente atualizado: " + paciente.getNome());
        System.out.println("onPacienteUpdated: "+paciente.toString());
        
        // Atualizar na lista local
        for (int i = 0; i < pacientes.size(); i++) {
            if (pacientes.get(i).getId() == paciente.getId()) {
                pacientes.set(i, paciente);
                break;
            }
        }
        
        // Atualizar painéis ativos
        if (painelSaudeAtivo != null) {
            painelSaudeAtivo.atualizarPaciente(paciente);
        }
        if (painelDadosAtivo != null) {
            painelDadosAtivo.atualizarPaciente(paciente);
        }
        
        // Mostrar notificação (opcional)
        showNotification("Paciente atualizado: " + paciente.getNome());    
    }

    @Override
    public void onPacienteDeleted(int pacienteId) {
        System.out.println("Paciente removido. ID: " + pacienteId);
        
        //System.out.println("com.mycompany.projeto_ibg.Main.onPacienteDeleted() Antes: ");
        //System.out.println(pacientes);
        //System.out.println("Id a ser removido: " + pacienteId);
        
        // Remover da lista local
        pacientes.removeIf(p -> p.getId() == pacienteId);
        
        //System.out.println("com.mycompany.projeto_ibg.Main.onPacienteDeleted() Depois: ");
        //System.out.println(pacientes);
        
        // Atualizar painéis ativos
        if (painelSaudeAtivo != null) {
            painelSaudeAtivo.removerPaciente(pacienteId);
        }
        if (painelDadosAtivo != null) {
            painelDadosAtivo.removerPaciente(pacienteId);
        }
        
        // Mostrar notificação (opcional)
        showNotification("Paciente removido. ID: " + pacienteId);
    }
    
    private void showNotification(String message) {
        // Cria um JWindow para a notificação
        JWindow window = new JWindow();
        JLabel label = new JLabel(message);
        label.setOpaque(true);
        label.setBackground(new Color(60, 63, 65)); // Cor discreta (cinza escuro)
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        window.getContentPane().add(label);
        window.pack();

        // Define a posição no canto inferior direito
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - window.getWidth() - 20;
        int y = screenSize.height - window.getHeight() - 50;
        window.setLocation(x, y);

        // Exibe a notificação
        window.setVisible(true);

        // Fecha automaticamente após 2 segundos
        new Timer(2000, e -> window.dispose()).start(); 
        //log no console
        System.out.println("Notificação: " + message);
    }
    
    private void refreshContentPainel(Component painel){
        
        painel.setSize(800, 640);
        painel.setLocation(0, 0);
        
        content.removeAll();
        content.add(painel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }
    
    private void refreshContentFormulario(Component painel){
        
        painel.setSize(500, 640);
        painel.setLocation(0, 0);
        
        content2.removeAll();
        content2.add(painel, BorderLayout.CENTER);
        content2.revalidate();
        content2.repaint();
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menu31 = new com.mycompany.view.Menu3();
        content = new javax.swing.JPanel();
        content2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        content.setBackground(new java.awt.Color(255, 255, 255));
        content.setPreferredSize(new java.awt.Dimension(800, 0));

        javax.swing.GroupLayout contentLayout = new javax.swing.GroupLayout(content);
        content.setLayout(contentLayout);
        contentLayout.setHorizontalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        content2.setPreferredSize(new java.awt.Dimension(500, 0));

        javax.swing.GroupLayout content2Layout = new javax.swing.GroupLayout(content2);
        content2.setLayout(content2Layout);
        content2Layout.setHorizontalGroup(
            content2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );
        content2Layout.setVerticalGroup(
            content2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(menu31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(content, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(content2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menu31, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
            .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
            .addComponent(content2, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Main().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel content;
    private javax.swing.JPanel content2;
    private com.mycompany.view.Menu3 menu31;
    // End of variables declaration//GEN-END:variables

   
 
}
