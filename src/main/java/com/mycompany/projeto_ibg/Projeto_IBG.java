package com.mycompany.projeto_ibg;

import com.mycompany.manager.ApiManager;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JOptionPane;

public class Projeto_IBG {
    
    private static final Logger LOGGER = Logger.getLogger(Projeto_IBG.class.getName());
    private static ApiManager apiManager;
    
    // Configurações da API (podem ser carregadas de arquivo de configuração)
    private static final String DEFAULT_API_URL = "http://meuservidor.local/api";
    private static final String DEFAULT_WEBSOCKET_URL = "ws://meuservidor.local";

    public static void main(String[] args) {
        System.out.println("=== Iniciando aplicação IBG ===");
        LOGGER.info("Iniciando aplicação IBG com nova arquitetura API");
        
        // Configurar shutdown hook para limpar recursos
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("=== Encerrando aplicação ===");
            LOGGER.info("Aplicação fechando - limpando recursos...");
            
            // Finalizar o ApiManager (isso vai fechar HTTP client e WebSocket)
            if (apiManager != null) {
                apiManager.finalizar();
                System.out.println("✓ ApiManager finalizado.");
            }
            
            LOGGER.info("Todos os recursos foram liberados.");
            System.out.println("=== Aplicação encerrada ===");
        }));
        
        try {
            // Inicializar o ApiManager
            inicializarApiManager();
            
            // Iniciar a interface gráfica
            iniciarInterfaceGrafica();
            
            System.out.println("✅ Aplicação IBG iniciada com sucesso!");
            LOGGER.info("Aplicação IBG iniciada com sucesso!");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro crítico ao iniciar aplicação", e);
            System.err.println("❌ Erro crítico ao iniciar aplicação: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar erro ao usuário
            JOptionPane.showMessageDialog(null, 
                "Erro crítico ao iniciar aplicação:\n" + e.getMessage() + 
                "\n\nVerifique se a API Spring Boot está rodando.", 
                "Erro de Inicialização", 
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
        }
    }
    
    /**
     * Inicializa o ApiManager com as configurações necessárias
     */
    private static void inicializarApiManager() {
        try {
            System.out.println("Inicializando comunicação com API...");
            LOGGER.info("Inicializando ApiManager...");
            
            // Carregar URLs de configuração (você pode implementar carregamento de arquivo)
            String apiUrl = carregarConfiguracaoApi();
            String websocketUrl = carregarConfiguracaoWebSocket();
            
            System.out.println("API URL: " + apiUrl);
            System.out.println("WebSocket URL: " + websocketUrl);
            
            // Inicializar o ApiManager
            apiManager = ApiManager.getInstance(apiUrl, websocketUrl);
            
            // Executar diagnóstico inicial
            apiManager.executarDiagnostico();
            
            if (apiManager.isApiDisponivel()) {
                System.out.println("✅ API disponível e funcionando");
                
                if (apiManager.isWebSocketConectado()) {
                    System.out.println("✅ WebSocket conectado - notificações em tempo real ativas");
                } else {
                    System.out.println("⚠️ WebSocket não conectado - notificações em tempo real indisponíveis");
                }
            } else {
                System.out.println("❌ API não disponível - modo offline");
                JOptionPane.showMessageDialog(null, 
                    "⚠️ API não disponível\n\n" +
                    "A aplicação funcionará em modo offline.\n" +
                    "Algumas funcionalidades podem não estar disponíveis.\n\n" +
                    "Verifique se o servidor Spring Boot está rodando em:\n" + apiUrl, 
                    "Aviso de Conectividade", 
                    JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar ApiManager", e);
            throw new RuntimeException("Falha na inicialização do ApiManager", e);
        }
    }
    
    /**
     * Carrega a URL da API de configuração
     */
    private static String carregarConfiguracaoApi() {
        // Prioridade: variável de ambiente > propriedade do sistema > padrão
        String apiUrl = System.getenv("IBG_API_URL");
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            apiUrl = System.getProperty("ibg.api.url", DEFAULT_API_URL);
        }
        
        LOGGER.info("URL da API carregada: " + apiUrl);
        return apiUrl;
    }
    
    /**
     * Carrega a URL do WebSocket de configuração
     */
    private static String carregarConfiguracaoWebSocket() {
        // Prioridade: variável de ambiente > propriedade do sistema > padrão
        String websocketUrl = System.getenv("IBG_WEBSOCKET_URL");
        if (websocketUrl == null || websocketUrl.trim().isEmpty()) {
            websocketUrl = System.getProperty("ibg.websocket.url", DEFAULT_WEBSOCKET_URL);
        }
        
        LOGGER.info("URL do WebSocket carregada: " + websocketUrl);
        return websocketUrl;
    }
    
    /**
     * Inicia a interface gráfica Swing
     */
    private static void iniciarInterfaceGrafica() {
        System.out.println("Iniciando interface gráfica...");
        
        // Configurar Look and Feel (opcional - pode mover para método separado)
        configurarLookAndFeel();
        
        // Iniciar a interface gráfica no Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                Main mainFrame = new Main();
                mainFrame.setVisible(true);
                
                System.out.println("✅ Interface gráfica iniciada.");
                LOGGER.info("Interface gráfica iniciada com sucesso");
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao iniciar interface gráfica", e);
                System.err.println("❌ Erro ao iniciar interface gráfica: " + e.getMessage());
                e.printStackTrace();
                
                JOptionPane.showMessageDialog(null, 
                    "Erro ao iniciar interface gráfica:\n" + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
                
                // Em caso de erro na UI, encerrar aplicação
                System.exit(1);
            }
        });
    }
    
    /**
     * Configura o Look and Feel da aplicação
     */
    private static void configurarLookAndFeel() {
        try {
            // Procurar por Nimbus Look and Feel
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    LOGGER.info("Look and Feel configurado: Nimbus");
                    return;
                }
            }
            
            // Se Nimbus não estiver disponível, usar o padrão do sistema
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            LOGGER.info("Look and Feel configurado: Sistema");
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao configurar Look and Feel - usando padrão", e);
        }
    }
    
    /**
     * Método público para obter o ApiManager (usado por outras classes)
     */
    public static ApiManager getApiManager() {
        if (apiManager == null) {
            throw new IllegalStateException("ApiManager não foi inicializado. A aplicação deve ser iniciada pelo método main.");
        }
        return apiManager;
    }
    
    /**
     * Método para verificar se a aplicação está em modo offline
     */
    public static boolean isModoOffline() {
        return apiManager == null || !apiManager.isApiDisponivel();
    }
    
    /**
     * Método para tentar reconectar à API (pode ser chamado pela UI)
     */
    public static void tentarReconexao() {
        if (apiManager != null) {
            System.out.println("Tentando reconexão...");
            apiManager.reconectar();
            apiManager.executarDiagnostico();
        }
    }
    
    /**
     * Método para obter status da aplicação
     */
    public static String getStatusAplicacao() {
        if (apiManager == null) {
            return "ApiManager não inicializado";
        }
        return apiManager.getStatusCompleto();
    }
}