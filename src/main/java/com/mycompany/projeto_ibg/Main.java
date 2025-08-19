package com.mycompany.projeto_ibg;

import com.mycompany.listener.PacienteChangeListener;
import com.mycompany.listener.PacienteEspecialidadeChangeListener;
import com.mycompany.manager.ApiManager;
import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.mycompany.service.EspecialidadeService;
import com.mycompany.service.PacienteEspecialidadeService;
import com.mycompany.service.PacienteService;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main extends javax.swing.JFrame implements MenuListener, PacienteChangeListener, PacienteEspecialidadeChangeListener {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Main.class.getName());
    private boolean recarregandoDados = false;
    
    // CONFIGURAÇÕES DA API
    private String apiBaseUrl = "http://meuservidor.local/api"; // URL da sua API Spring Boot
    private String webSocketUrl = "ws://meuservidor.local"; // URL do WebSocket
    
    private ApiManager apiManager;
    
    // Services que substituem os DAOs
    private PacienteService pacienteService;
    private EspecialidadeService especialidadeService;
    private PacienteEspecialidadeService pacienteEspecialidadeService;
    
    // Listas de dados em memória (cache local)
    List<Paciente> pacientes = new ArrayList<>();
    List<Especialidade> especialidades = new ArrayList<>();
    List<PacienteEspecialidade> pacienteEspecialidades = new ArrayList<>();
    
    // Referências para os painéis ativos
    private PainelSaude2 painelSaudeAtivo;
    private PainelDados2 painelDadosAtivo;
    private FormularioSaude2P formularioSaudeAtivo;
    private FormularioDados2P formularioDadosAtivo;


    public Main() {
        initComponents();

        // Registrar como listener do menu
        menu31.addMenuListener(this);
        
        // Inicializa o gerenciador ApiManager COM AS URLs
        inicializarApiManager();

        // Carregar dados iniciais (só se API estiver disponível)
        carregarDados();

        // Registrar listeners para notificações em tempo real
        registrarListeners();
        
        // Inicializa com o painel padrão
        onSaudeSelected(); 
    }
    
    @Override
    public void onSaudeSelected() {
        // Usa os Services em vez de DAOs
        painelSaudeAtivo = new PainelSaude2(pacientes);
        formularioSaudeAtivo = new FormularioSaude2P(pacienteService, pacienteEspecialidadeService, especialidadeService, especialidades);
        
        refreshContentPainel(painelSaudeAtivo);
        refreshContentFormulario(formularioSaudeAtivo);
        
        // Conectar os painéis
        painelSaudeAtivo.setPatientSelectionListener(formularioSaudeAtivo);
        
        // Limpar referências dos outros painéis
        painelDadosAtivo = null;
        formularioDadosAtivo = null;
    }

    @Override
    public void onDadosSelected() {
        // Usa os Services em vez de DAOs
        painelDadosAtivo = new PainelDados2(pacientes, pacienteEspecialidades);
        formularioDadosAtivo = new FormularioDados2P(pacienteService, pacienteEspecialidadeService, especialidadeService, especialidades);
        
        refreshContentPainel(painelDadosAtivo);
        refreshContentFormulario(formularioDadosAtivo);
        
        // Conectar os painéis
        painelDadosAtivo.setPatientSelectionListener(formularioDadosAtivo);
       
        // Limpar referências dos outros painéis
        painelSaudeAtivo = null;
        formularioSaudeAtivo = null;
    }
    
    /**
     * Método para recarregar dados e atualizar painéis ativos
     */
    @Override
    public void onRecarregarClicked() {
        if (recarregandoDados) {
            System.out.println("Recarregamento já em andamento...");
            return;
        }

        recarregandoDados = true;
        showNotification("Recarregando dados...");

        // Executar recarregamento em thread separada
        SwingUtilities.invokeLater(() -> {
            try {
                // Verificar se API está disponível
                if (!apiManager.isApiDisponivel()) {
                    showNotification("API não disponível para recarregamento");
                    return;
                }

                // Recarregar todos os dados
                carregarDados();

                // Atualizar painéis ativos conforme a aba selecionada
                int abaSelecionada = menu31.getSelectedTab();
                if (abaSelecionada == 0) {
                    onSaudeSelected();
                } else {
                    onDadosSelected();
                }

                showNotification("✅ Dados recarregados com sucesso!");
                System.out.println("Recarregamento de dados concluído com sucesso!");

            } catch (Exception e) {
                logger.severe("Erro durante recarregamento de dados: " + e.getMessage());
                e.printStackTrace();
                showNotification("❌ Erro ao recarregar dados: " + e.getMessage());
            } finally {
                recarregandoDados = false;
            }
        });
    }
     
     
    
    // Implementação dos métodos PacienteEspecialidadeChangeListener
    @Override
    public void onPacienteEspecialidadeAdded(PacienteEspecialidade pacienteEspecialidade) {
        System.out.println("Nova associação paciente_has_especialidade adicionada: " + 
                      "Paciente ID: " + pacienteEspecialidade.getPacienteId() + 
                      ", Especialidade ID: " + pacienteEspecialidade.getEspecialidadeId());

        // Atualizar a lista local
        this.pacienteEspecialidades.add(pacienteEspecialidade);

        // Atualizar apenas o painel de dados
        if (painelDadosAtivo != null) {
            painelDadosAtivo.atualizarPacienteEspecialidade(this.pacienteEspecialidades);
        }
    }

    @Override
    public void onPacienteEspecialidadeUpdated(PacienteEspecialidade pacienteEspecialidade) {
        System.out.println("Associação paciente_has_especialidade atualizada: " + 
                      "Paciente ID: " + pacienteEspecialidade.getPacienteId() + 
                      ", Especialidade ID: " + pacienteEspecialidade.getEspecialidadeId());

        // Atualizar na lista local
        for (int i = 0; i < this.pacienteEspecialidades.size(); i++) {
            PacienteEspecialidade pe = this.pacienteEspecialidades.get(i);
            if (pe.getPacienteId() == pacienteEspecialidade.getPacienteId() && 
                pe.getEspecialidadeId() == pacienteEspecialidade.getEspecialidadeId()) {
                this.pacienteEspecialidades.set(i, pacienteEspecialidade);
                break;
            }
        }

        if (painelDadosAtivo != null) {
            painelDadosAtivo.atualizarPacienteEspecialidade(this.pacienteEspecialidades);
        }
    }

    @Override
    public void onPacienteEspecialidadeDeleted(Integer pacienteId, Integer especialidadeId) {
        System.out.println("Associação paciente_has_especialidade removida: " + 
                      "Paciente ID: " + pacienteId + ", Especialidade ID: " + especialidadeId);
    
        // Remover da lista local
        this.pacienteEspecialidades.removeIf(pe -> 
            pe.getPacienteId() == pacienteId && pe.getEspecialidadeId() == especialidadeId);

        if (painelDadosAtivo != null) {
            painelDadosAtivo.atualizarPacienteEspecialidade(this.pacienteEspecialidades);
        }
    }
    
    // Implementação dos métodos PacienteChangeListener
    @Override
    public void onPacienteAdded(Paciente paciente) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Novo paciente adicionado: " + paciente.getNome());
            
            // Atualizar a lista local
            pacientes.add(paciente);
            
            // Atualizar painéis ativos
            if (painelSaudeAtivo != null) {  
                painelSaudeAtivo.adicionarPaciente(paciente);
            }
            if (painelDadosAtivo != null) {
                painelDadosAtivo.adicionarPaciente(paciente);
            }
            
            showNotification("Novo paciente adicionado: " + paciente.getNome());
        });
    }

    @Override
    public void onPacienteUpdated(Paciente paciente) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Paciente atualizado: " + paciente.getNome());
            
            // Atualizar na lista local
            for (int i = 0; i < pacientes.size(); i++) {
                if (pacientes.get(i).getId().equals(paciente.getId())) {
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
            
            showNotification("Paciente atualizado: " + paciente.getNome());
        });
    }

    @Override
    public void onPacienteDeleted(int pacienteId) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Paciente removido. ID: " + pacienteId);
            
            // Remover da lista local
            pacientes.removeIf(p -> p.getId().equals(pacienteId));
            
            // Atualizar painéis ativos
            if (painelSaudeAtivo != null) {
                painelSaudeAtivo.removerPaciente(pacienteId);
            }
            if (painelDadosAtivo != null) {
                painelDadosAtivo.removerPaciente(pacienteId);
            }
            
            showNotification("Paciente removido. ID: " + pacienteId);
        });
    }
    
    /**
     * Método para carregar/recarregar todos os dados do sistema
     */
    private void carregarDados() {
        try {
            System.out.println("Carregando dados do sistema via API...");

            if (!apiManager.isApiDisponivel()) {
                System.out.println("API não disponível - usando dados em cache");
                showNotification("Modo offline - API não disponível");
                return;
            }

            // Carregar dados via Services
            pacientes = pacienteService.listarTodos();
            System.out.println("Pacientes carregados: " + pacientes.size());

            especialidades = especialidadeService.listarTodas();
            System.out.println("Especialidades carregadas: " + especialidades.size());

            pacienteEspecialidades = pacienteEspecialidadeService.listarTodos();
            System.out.println("Associações paciente_has_especialidade carregadas: " + pacienteEspecialidades.size());

            System.out.println("Todos os dados carregados com sucesso via API!");

        } catch (Exception e) {
            logger.severe("Erro ao carregar dados: " + e.getMessage());
            e.printStackTrace();
            showNotification("Erro ao carregar dados: " + e.getMessage());
            
            // Em caso de erro, inicializar com listas vazias se necessário
            if (pacientes == null) pacientes = new ArrayList<>();
            if (especialidades == null) especialidades = new ArrayList<>();
            if (pacienteEspecialidades == null) pacienteEspecialidades = new ArrayList<>();
        }
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
    
    private void inicializarApiManager() {
        try {
            carregarConfiguracoes(); // Carregar configurações primeiro

            System.out.println("Inicializando ApiManager...");
            System.out.println("API URL: " + apiBaseUrl);
            System.out.println("WebSocket URL: " + webSocketUrl);

            apiManager = ApiManager.getInstance(apiBaseUrl, webSocketUrl);
            apiManager.configurarShutdownHook();

            if (apiManager.isApiDisponivel()) {
                inicializarServices();
                System.out.println("Sistema inicializado com sucesso!");
            } else {
                System.err.println("API não disponível - modo offline");
                initializeOfflineMode();
            }

        } catch (Exception e) {
            System.err.println("Erro crítico ao inicializar ApiManager: " + e.getMessage());
            e.printStackTrace();
            initializeOfflineMode();
        }
    }
    
    private void carregarConfiguracoes() {
        Properties config = new Properties();
        try {
            // Tentar carregar arquivo config.properties
            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
            if (input != null) {
                config.load(input);
                apiBaseUrl = config.getProperty("api.base.url", "http://meuservidor.local/api");
                webSocketUrl = config.getProperty("websocket.url", "ws://meuservidor.local");
                System.out.println("Configurações carregadas do arquivo config.properties");
            } else {
                System.out.println("Arquivo config.properties não encontrado - usando configurações padrão");
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar config.properties - usando configurações padrão: " + e.getMessage());
        }
    }
    
    private void inicializarServices() {
        try {
            pacienteService = apiManager.getPacienteService();
            especialidadeService = apiManager.getEspecialidadeService();
            pacienteEspecialidadeService = apiManager.getPacienteEspecialidadeService();

            System.out.println("Services inicializados com sucesso!");
            apiManager.executarDiagnostico();

        } catch (Exception e) {
            System.err.println("Erro ao inicializar services: " + e.getMessage());
            throw e; // Re-lançar para tratamento no método pai
        }
    }
    
    /**
     * Inicializa modo offline quando API não está disponível
     */
    private void initializeOfflineMode() {
        System.out.println("🔄 Inicializando modo offline...");
        
        // Inicializar listas vazias
        if (pacientes == null) pacientes = new ArrayList<>();
        if (especialidades == null) especialidades = new ArrayList<>();
        if (pacienteEspecialidades == null) pacienteEspecialidades = new ArrayList<>();
        
        // Mostrar aviso ao usuário
        JOptionPane.showMessageDialog(this, 
            "Modo Offline\n\n" +
            "A API não está disponível no momento.\n" +
            "Verifique se o servidor Spring Boot está rodando.\n\n" +
            "Algumas funcionalidades podem não estar disponíveis.",
            "Aviso de Conectividade", 
            JOptionPane.WARNING_MESSAGE);
    }
    
    private void registrarListeners() {
        try {
            if (apiManager != null) {
                // Adiciona this como listener para mudanças
                apiManager.addPacienteChangeListener(this);
                apiManager.addPacienteEspecialidadeChangeListener(this);
                
                System.out.println("✅ Listeners registrados para notificações em tempo real!");
            } else {
                System.out.println("⚠️ ApiManager não disponível - listeners não registrados");
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao registrar listeners: " + e.getMessage());
        }
    }
    
     /**
     * Método para testar conectividade (pode ser chamado por um botão de diagnóstico)
     */
    public void testarConectividade() {
        if (apiManager != null) {
            apiManager.executarDiagnostico();
            String status = apiManager.getStatusCompleto();
            JOptionPane.showMessageDialog(this, status, "Status da Conectividade", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Método para tentar reconectar
     */
    public void tentarReconexao() {
        if (apiManager != null) {
            showNotification("🔄 Tentando reconectar...");
            apiManager.reconectar();
            
            if (apiManager.isApiDisponivel()) {
                // Reconfigurar services
                try {
                    pacienteService = apiManager.getPacienteService();
                    especialidadeService = apiManager.getEspecialidadeService();
                    pacienteEspecialidadeService = apiManager.getPacienteEspecialidadeService();
                    showNotification("Reconexão bem-sucedida!");
                } catch (Exception e) {
                    showNotification("Erro na reconexão: " + e.getMessage());
                }
            } else {
                showNotification("Falha na reconexão");
            }
        }
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
