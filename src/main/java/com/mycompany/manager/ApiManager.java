package com.mycompany.manager;

import com.mycompany.client.ApiClient;
import com.mycompany.client.WebSocketClient;
import com.mycompany.service.PacienteService;
import com.mycompany.service.EspecialidadeService;
import com.mycompany.service.PacienteEspecialidadeService;
import com.mycompany.listener.PacienteChangeListener;
import com.mycompany.listener.PacienteEspecialidadeChangeListener;

import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * Gerenciador central para coordenar toda a comunicação com a API
 * Substitui o sistema Kafka e gerencia os Services que substituem os DAOs
 */
public class ApiManager {
    
    private static final Logger LOGGER = Logger.getLogger(ApiManager.class.getName());
    private static ApiManager instance;
    
    // Configurações da API
    private String apiBaseUrl = "http://meuservidor.local/api"; // URL da sua API REST
    private String webSocketUrl = "ws://meuservidor.local"; // URL do seu WebSocket
    
    // Clientes de comunicação
    private ApiClient apiClient;
    private WebSocketClient webSocketClient;
    
    // Services que substituem os DAOs
    private PacienteService pacienteService;
    private EspecialidadeService especialidadeService;
    private PacienteEspecialidadeService pacienteEspecialidadeService;
    
    // Status de conexão
    private volatile boolean apiDisponivel = false;
    private volatile boolean webSocketConectado = false;
    
    // Construtor privado para Singleton
    private ApiManager(String apiBaseUrl, String webSocketUrl) {
        this.apiBaseUrl = apiBaseUrl;
        this.webSocketUrl = webSocketUrl;
        inicializar();
    }
    
    /**
     * Obtém a instância singleton do ApiManager
     */
    public static synchronized ApiManager getInstance(String apiBaseUrl, String webSocketUrl) {
        if (instance == null) {
            instance = new ApiManager(apiBaseUrl, webSocketUrl);
        }
        return instance;
    }
    
    /**
     * Obtém a instância já configurada 
     */
    public static ApiManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ApiManager não foi inicializado. Chame getInstance(apiBaseUrl, webSocketUrl) primeiro.");
        }
        return instance;
    }
    
    /**
     * Inicializa todos os componentes
     */
    private void inicializar() {
        try {
            LOGGER.info("Inicializando ApiManager...");
            
            // Inicializa o cliente HTTP
            apiClient = new ApiClient(apiBaseUrl, 5000, 10000); 
            
            // Verifica se a API está disponível
            verificarDisponibilidadeApi();
            
            if (apiDisponivel) {
                // Inicializa os services
                inicializarServices();
                
                // Inicializa o WebSocket para notificações em tempo real
                inicializarWebSocket();
                
                LOGGER.info("ApiManager inicializado com sucesso!");
            } else {
                LOGGER.warning("API não disponível. Alguns recursos podem não funcionar.");
                JOptionPane.showMessageDialog(null, 
                    "⚠️ API não disponível\n\nVerifique se o servidor Spring Boot está rodando.\n" +
                    "Alguns recursos podem não funcionar corretamente.", 
                    "Aviso de Conectividade", 
                    JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar ApiManager", e);
        }
    }
    
    /**
     * Verifica se a API está disponível
     */
    private void verificarDisponibilidadeApi() {
        try {
            apiDisponivel = apiClient.isApiAvailable();
            if (apiDisponivel) {
                LOGGER.info("✓ API disponível em: " + apiBaseUrl);
            } else {
                LOGGER.warning("✗ API não disponível em: " + apiBaseUrl);
            }
        } catch (Exception e) {
            apiDisponivel = false;
            LOGGER.log(Level.WARNING, "Erro ao verificar disponibilidade da API", e);
        }
    }
    
    /**
     * Inicializa todos os services
     */
    private void inicializarServices() {
        LOGGER.info("Inicializando services...");
        
        pacienteService = new PacienteService(apiClient);
        especialidadeService = new EspecialidadeService(apiClient);
        pacienteEspecialidadeService = new PacienteEspecialidadeService(apiClient);
        
        LOGGER.info("✓ Services inicializados");
    }
    
    /**
     * Inicializa o cliente WebSocket
     */
    private void inicializarWebSocket() {
        try {
            LOGGER.info("Inicializando WebSocket...");
            
            webSocketClient = new WebSocketClient(webSocketUrl);
            boolean conectado = webSocketClient.connect();
            
            if (conectado) {
                webSocketConectado = true;
                LOGGER.info("✓ WebSocket conectado para notificações em tempo real");
            } else {
                webSocketConectado = false;
                LOGGER.warning("✗ Falha ao conectar WebSocket. Notificações em tempo real não funcionarão.");
            }
            
        } catch (Exception e) {
            webSocketConectado = false;
            LOGGER.log(Level.WARNING, "Erro ao inicializar WebSocket", e);
        }
    }
    
    /**
     * Reconecta à API e WebSocket se necessário
     */
    public void reconectar() {
        LOGGER.info("Tentando reconectar...");
        
        // Verifica API
        verificarDisponibilidadeApi();
        
        // Reconecta WebSocket se necessário
        if (apiDisponivel && (!webSocketConectado || !webSocketClient.isConnected())) {
            inicializarWebSocket();
        }
        
        if (apiDisponivel && webSocketConectado) {
            LOGGER.info("✓ Reconexão bem-sucedida!");
        } else {
            LOGGER.warning("✗ Reconexão parcial ou falhou");
        }
    }
    
    /**
     * Finaliza todas as conexões
     */
    public void finalizar() {
        LOGGER.info("Finalizando ApiManager...");
        
        try {
            if (webSocketClient != null) {
                webSocketClient.disconnect();
                webSocketConectado = false;
                LOGGER.info("✓ WebSocket desconectado");
            }
            
            if (apiClient != null) {
                apiClient.close();
                apiDisponivel = false;
                LOGGER.info("✓ ApiClient fechado");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao finalizar ApiManager", e);
        }
        
        LOGGER.info("ApiManager finalizado");
    }
    
    // Getters para os Services (substituem os DAOs)
    
    public PacienteService getPacienteService() {
        if (pacienteService == null) {
            throw new IllegalStateException("PacienteService não inicializado. Verifique se a API está disponível.");
        }
        return pacienteService;
    }
    
    public EspecialidadeService getEspecialidadeService() {
        if (especialidadeService == null) {
            throw new IllegalStateException("EspecialidadeService não inicializado. Verifique se a API está disponível.");
        }
        return especialidadeService;
    }
    
    public PacienteEspecialidadeService getPacienteEspecialidadeService() {
        if (pacienteEspecialidadeService == null) {
            throw new IllegalStateException("PacienteEspecialidadeService não inicializado. Verifique se a API está disponível.");
        }
        return pacienteEspecialidadeService;
    }
    
    // Métodos para gerenciar listeners do WebSocket (substitui o sistema Kafka)
    
    /**
     * Adiciona listener para mudanças em Paciente
     */
    public void addPacienteChangeListener(PacienteChangeListener listener) {
        if (webSocketClient != null) {
            webSocketClient.addPacienteListener(listener);
            LOGGER.info("PacienteChangeListener adicionado");
        } else {
            LOGGER.warning("WebSocket não disponível. Listener não será adicionado.");
        }
    }
    
    /**
     * Remove listener para mudanças em Paciente
     */
    public void removePacienteChangeListener(PacienteChangeListener listener) {
        if (webSocketClient != null) {
            webSocketClient.removePacienteListener(listener);
            LOGGER.info("PacienteChangeListener removido");
        }
    }
    
    /**
     * Adiciona listener para mudanças em PacienteEspecialidade
     */
    public void addPacienteEspecialidadeChangeListener(PacienteEspecialidadeChangeListener listener) {
        if (webSocketClient != null) {
            webSocketClient.addPacienteEspecialidadeListener(listener);
            LOGGER.info("PacienteEspecialidadeChangeListener adicionado");
        } else {
            LOGGER.warning("WebSocket não disponível. Listener não será adicionado.");
        }
    }
    
    /**
     * Remove listener para mudanças em PacienteEspecialidade
     */
    public void removePacienteEspecialidadeChangeListener(PacienteEspecialidadeChangeListener listener) {
        if (webSocketClient != null) {
            webSocketClient.removePacienteEspecialidadeListener(listener);
            LOGGER.info("PacienteEspecialidadeChangeListener removido");
        }
    }
    
    // Métodos de status e diagnóstico
    
    public boolean isApiDisponivel() {
        return apiDisponivel;
    }
    
    public boolean isWebSocketConectado() {
        return webSocketConectado && webSocketClient != null && webSocketClient.isConnected();
    }
    
    public String getStatusCompleto() {
        StringBuilder status = new StringBuilder();
        status.append("=== STATUS DO APIMANAGER ===\n");
        status.append("API Base URL: ").append(apiBaseUrl).append("\n");
        status.append("WebSocket URL: ").append(webSocketUrl).append("\n");
        status.append("API Disponível: ").append(apiDisponivel ? "✓ SIM" : "✗ NÃO").append("\n");
        status.append("WebSocket Conectado: ").append(isWebSocketConectado() ? "✓ SIM" : "✗ NÃO").append("\n");
        
        if (webSocketClient != null) {
            status.append("WebSocket Reconectando: ").append(webSocketClient.isReconnecting() ? "SIM" : "NÃO").append("\n");
        }
        
        status.append("Services Inicializados: ").append(
            (pacienteService != null && especialidadeService != null && pacienteEspecialidadeService != null) 
            ? "✓ SIM" : "✗ NÃO"
        ).append("\n");
        
        return status.toString();
    }
    
    /**
     * Executa diagnóstico completo da conectividade
     */
    public void executarDiagnostico() {
        LOGGER.info("=== DIAGNÓSTICO DE CONECTIVIDADE ===");
        
        // Testa API
        LOGGER.info("Testando API...");
        verificarDisponibilidadeApi();
        
        if (apiDisponivel && pacienteService != null) {
            try {
                int totalPacientes = pacienteService.contarTotal();
                LOGGER.info("✓ API funcional - Total de pacientes: " + totalPacientes);
            } catch (Exception e) {
                LOGGER.warning("✗ API disponível mas com problemas: " + e.getMessage());
            }
        }
        
        // Testa WebSocket
        LOGGER.info("Testando WebSocket...");
        if (isWebSocketConectado()) {
            LOGGER.info("✓ WebSocket conectado e funcional");
        } else if (webSocketClient != null && webSocketClient.isReconnecting()) {
            LOGGER.info("⟳ WebSocket tentando reconectar...");
        } else {
            LOGGER.warning("✗ WebSocket não conectado");
        }
        
        // Exibe status completo
        LOGGER.info(getStatusCompleto());
    }
    
    /**
     * Configura um shutdown hook para finalizar conexões ao fechar a aplicação
     */
    public void configurarShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Aplicação fechando - finalizando ApiManager...");
            finalizar();
        }));
    }
}