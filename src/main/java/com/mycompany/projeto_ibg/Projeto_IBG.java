package com.mycompany.projeto_ibg;

import com.mycompany.manager.ApiManager;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Classe principal da aplicação IBG
 * Versão adaptada para trabalhar com DTOs, Services e ApiManager
 */
public class Projeto_IBG {
    
    private static final Logger LOGGER = Logger.getLogger(Projeto_IBG.class.getName());
    private static volatile ApiManager apiManager;
    
    // Configurações da API (podem ser carregadas de variáveis de ambiente)
    private static final String DEFAULT_API_URL = "http://meuservidor.local/api";
    private static final String DEFAULT_WEBSOCKET_URL = "ws://meuservidor.local";
    
    // Status da inicialização
    private static volatile boolean sistemaInicializado = false;
    private static volatile boolean apiDisponivel = false;

    public static void main(String[] args) {
        System.out.println("=== Iniciando aplicação IBG ===");
        LOGGER.info("Iniciando aplicação IBG com arquitetura baseada em DTOs e Services");
        
        // Configurar logging
        configurarLogging();
        
        // Configurar shutdown hook para limpar recursos
        configurarShutdownHook();
        
        try {
            // Fase 1: Inicializar o ApiManager
            if (!inicializarApiManager()) {
                LOGGER.severe("Falha crítica na inicialização do ApiManager");
                exibirErroInicializacao("Falha ao inicializar comunicação com API");
                return;
            }
            
            // Fase 2: Iniciar a interface gráfica
            iniciarInterfaceGrafica();
            
            // Fase 3: Verificação final
            verificarInicializacao();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro crítico durante inicialização", e);
            exibirErroInicializacao("Erro crítico: " + e.getMessage());
        }
    }
    
    /**
     * Configura o sistema de logging
     */
    private static void configurarLogging() {
        try {
            // Configurar nível de logging
            Logger rootLogger = Logger.getLogger("");
            rootLogger.setLevel(Level.INFO);
            
            LOGGER.info("Sistema de logging configurado");
        } catch (Exception e) {
            System.err.println("Erro ao configurar logging: " + e.getMessage());
        }
    }
    
    /**
     * Configura shutdown hook para limpeza de recursos
     */
    private static void configurarShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("=== Encerrando aplicação ===");
            LOGGER.info("Aplicação fechando - iniciando limpeza de recursos...");
            
            try {
                // Finalizar o ApiManager (HTTP client e WebSocket)
                if (apiManager != null) {
                    apiManager.finalizar();
                    System.out.println("✓ ApiManager finalizado.");
                }
                
                // Aguardar um pouco para finalização completa
                Thread.sleep(1000);
                
                LOGGER.info("Todos os recursos foram liberados com sucesso");
                System.out.println("=== Aplicação encerrada com sucesso ===");
                
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erro durante finalização", e);
                System.err.println("⚠️ Erro durante finalização: " + e.getMessage());
            }
        }, "ShutdownHook-IBG"));
    }
    
    /**
     * Inicializa o ApiManager com timeout e retry
     */
    private static boolean inicializarApiManager() {
        try {
            System.out.println("🔄 Inicializando comunicação com API...");
            LOGGER.info("Iniciando inicialização do ApiManager...");
            
            // Carregar URLs de configuração
            String apiUrl = carregarConfiguracaoApi();
            String websocketUrl = carregarConfiguracaoWebSocket();
            
            System.out.println("📡 API URL: " + apiUrl);
            System.out.println("🔌 WebSocket URL: " + websocketUrl);
            
            // Inicializar o ApiManager com timeout
            CompletableFuture<Boolean> initFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    apiManager = ApiManager.getInstance(apiUrl, websocketUrl);
                    return true;
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro na inicialização do ApiManager", e);
                    return false;
                }
            });
            
            // Aguardar inicialização com timeout de 15 segundos
            boolean inicializado = initFuture.get(15, TimeUnit.SECONDS);
            
            if (!inicializado || apiManager == null) {
                throw new RuntimeException("Falha na inicialização do ApiManager");
            }
            
            // Executar diagnóstico inicial
            executarDiagnosticoInicial();
            
            // Verificar status final
            apiDisponivel = apiManager.isApiDisponivel();
            sistemaInicializado = true;
            
            if (apiDisponivel) {
                System.out.println("✅ API disponível e funcionando");
                
                if (apiManager.isWebSocketConectado()) {
                    System.out.println("✅ WebSocket conectado - notificações em tempo real ativas");
                } else {
                    System.out.println("⚠️ WebSocket não conectado - notificações em tempo real indisponíveis");
                }
            } else {
                System.out.println("⚠️ API não disponível - sistema funcionará em modo limitado");
            }
            
            return true;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Falha crítica na inicialização do ApiManager", e);
            System.err.println("❌ Falha na inicialização: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Executa diagnóstico inicial do sistema
     */
    private static void executarDiagnosticoInicial() {
        try {
            LOGGER.info("Executando diagnóstico inicial...");
            apiManager.executarDiagnostico();
            
            // Log do status detalhado
            String status = apiManager.getStatusCompleto();
            LOGGER.info("Status do sistema:\n" + status);
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro no diagnóstico inicial", e);
        }
    }
    
    /**
     * Carrega configuração da URL da API
     */
    private static String carregarConfiguracaoApi() {
        // Prioridade: variável de ambiente > propriedade do sistema > padrão
        String apiUrl = System.getenv("IBG_API_URL");
        if (isStringValida(apiUrl)) {
            LOGGER.info("URL da API carregada de variável de ambiente");
            return apiUrl;
        }
        
        apiUrl = System.getProperty("ibg.api.url");
        if (isStringValida(apiUrl)) {
            LOGGER.info("URL da API carregada de propriedade do sistema");
            return apiUrl;
        }
        
        LOGGER.info("URL da API usando valor padrão");
        return DEFAULT_API_URL;
    }
    
    /**
     * Carrega configuração da URL do WebSocket
     */
    private static String carregarConfiguracaoWebSocket() {
        // Prioridade: variável de ambiente > propriedade do sistema > padrão
        String websocketUrl = System.getenv("IBG_WEBSOCKET_URL");
        if (isStringValida(websocketUrl)) {
            LOGGER.info("URL do WebSocket carregada de variável de ambiente");
            return websocketUrl;
        }
        
        websocketUrl = System.getProperty("ibg.websocket.url");
        if (isStringValida(websocketUrl)) {
            LOGGER.info("URL do WebSocket carregada de propriedade do sistema");
            return websocketUrl;
        }
        
        LOGGER.info("URL do WebSocket usando valor padrão");
        return DEFAULT_WEBSOCKET_URL;
    }
    
    /**
     * Verifica se uma string é válida (não nula e não vazia)
     */
    private static boolean isStringValida(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Inicia a interface gráfica Swing
     */
    private static void iniciarInterfaceGrafica() {
        System.out.println("🖥️ Iniciando interface gráfica...");
        LOGGER.info("Inicializando interface gráfica Swing");
        
        // Configurar Look and Feel
        configurarLookAndFeel();
        
        // Iniciar a interface gráfica no Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Criar e exibir a janela principal
                Main mainFrame = new Main();
                mainFrame.setVisible(true);
                
                System.out.println("✅ Interface gráfica iniciada com sucesso");
                LOGGER.info("Interface gráfica iniciada e visível");
                
                // Verificar status final após alguns segundos
                Timer verificacaoFinal = new Timer(3000, () -> {
                    verificarStatusFinal(mainFrame);
                });
                verificacaoFinal.setRepeats(false);
                verificacaoFinal.start();
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro crítico ao iniciar interface gráfica", e);
                exibirErroInicializacao("Erro na interface gráfica: " + e.getMessage());
            }
        });
    }
    
    /**
     * Configura o Look and Feel da aplicação
     */
    private static void configurarLookAndFeel() {
        try {
            // Tentar Nimbus primeiro
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    LOGGER.info("Look and Feel configurado: Nimbus");
                    return;
                }
            }
            
            // Fallback para o padrão do sistema
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            LOGGER.info("Look and Feel configurado: Sistema");
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao configurar Look and Feel - usando padrão", e);
        }
    }
    
    /**
     * Verifica o status final da inicialização
     */
    private static void verificarStatusFinal(Main mainFrame) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (sistemaInicializado && apiManager != null) {
                    String statusFinal = mainFrame.getStatusSistema();
                    LOGGER.info("Status final da inicialização:\n" + statusFinal);
                    
                    if (!apiDisponivel) {
                        // Mostrar opção de tentar reconectar
                        int opcao = JOptionPane.showConfirmDialog(mainFrame,
                            "⚠️ Sistema iniciado em modo offline\n\n" +
                            "A API não está disponível no momento.\n" +
                            "Deseja tentar reconectar agora?",
                            "Sistema Offline",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                        
                        if (opcao == JOptionPane.YES_OPTION) {
                            mainFrame.tentarReconexao();
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erro na verificação final", e);
            }
        });
    }
    
    /**
     * Verificação final da inicialização
     */
    private static void verificarInicializacao() {
        try {
            Thread.sleep(2000); // Aguardar estabilização
            
            if (sistemaInicializado) {
                System.out.println("✅ Aplicação IBG iniciada com sucesso!");
                LOGGER.info("✅ Inicialização completa da aplicação IBG");
                
                // Log do status final
                if (apiManager != null) {
                    LOGGER.info("Status final:\n" + apiManager.getStatusCompleto());
                }
            } else {
                System.out.println("⚠️ Aplicação iniciada com limitações");
                LOGGER.warning("Sistema inicializado com limitações");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro na verificação de inicialização", e);
        }
    }
    
    /**
     * Exibe erro de inicialização para o usuário
     */
    private static void exibirErroInicializacao(String mensagem) {
        System.err.println("❌ " + mensagem);
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, 
                "❌ Erro de Inicialização\n\n" + mensagem + 
                "\n\nVerificações recomendadas:" +
                "\n• API Spring Boot está rodando?" +
                "\n• URLs de configuração estão corretas?" +
                "\n• Conectividade de rede está funcionando?" +
                "\n\nA aplicação será encerrada.", 
                "Erro Crítico", 
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
        });
    }
    
    // ===== MÉTODOS PÚBLICOS PARA ACESSO EXTERNO =====
    
    /**
     * Obtém o ApiManager (thread-safe)
     */
    public static ApiManager getApiManager() {
        if (apiManager == null || !sistemaInicializado) {
            throw new IllegalStateException(
                "ApiManager não foi inicializado. Execute a aplicação via método main primeiro.");
        }
        return apiManager;
    }
    
    /**
     * Verifica se a aplicação está em modo offline
     */
    public static boolean isModoOffline() {
        return !apiDisponivel || apiManager == null || !apiManager.isApiDisponivel();
    }
    
    /**
     * Verifica se o sistema foi inicializado
     */
    public static boolean isSistemaInicializado() {
        return sistemaInicializado;
    }
    
    /**
     * Tenta reconexão à API (pode ser chamado pela UI)
     */
    public static void tentarReconexao() {
        if (apiManager != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    System.out.println("🔄 Tentando reconexão...");
                    LOGGER.info("Iniciando tentativa de reconexão");
                    
                    apiManager.reconectar();
                    apiManager.executarDiagnostico();
                    
                    apiDisponivel = apiManager.isApiDisponivel();
                    
                    if (apiDisponivel) {
                        System.out.println("✅ Reconexão bem-sucedida!");
                        LOGGER.info("Reconexão à API bem-sucedida");
                    } else {
                        System.out.println("❌ Falha na reconexão");
                        LOGGER.warning("Falha na tentativa de reconexão");
                    }
                    
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erro durante tentativa de reconexão", e);
                    System.err.println("❌ Erro na reconexão: " + e.getMessage());
                }
            });
        } else {
            LOGGER.warning("Tentativa de reconexão sem ApiManager inicializado");
        }
    }
    
    /**
     * Obtém status completo da aplicação
     */
    public static String getStatusAplicacao() {
        StringBuilder status = new StringBuilder();
        status.append("=== STATUS DA APLICAÇÃO IBG ===\n");
        status.append("Sistema Inicializado: ").append(sistemaInicializado ? "✅ SIM" : "❌ NÃO").append("\n");
        status.append("API Disponível: ").append(apiDisponivel ? "✅ SIM" : "❌ NÃO").append("\n");
        status.append("Modo Offline: ").append(isModoOffline() ? "SIM" : "NÃO").append("\n");
        status.append("\n");
        
        if (apiManager != null) {
            status.append(apiManager.getStatusCompleto());
        } else {
            status.append("ApiManager: NÃO INICIALIZADO\n");
        }
        
        return status.toString();
    }
    
    /**
     * Executa diagnóstico completo do sistema
     */
    public static void executarDiagnosticoCompleto() {
        if (apiManager != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    LOGGER.info("Executando diagnóstico completo do sistema");
                    apiManager.executarDiagnostico();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erro no diagnóstico completo", e);
                }
            });
        } else {
            LOGGER.warning("Diagnóstico solicitado mas ApiManager não inicializado");
        }
    }
    
    /**
     * Força uma reinicialização completa do ApiManager
     */
    public static boolean forcarReinicializacao() {
        try {
            LOGGER.info("Iniciando reinicialização forçada do sistema");
            
            // Finalizar ApiManager atual
            if (apiManager != null) {
                apiManager.finalizar();
                apiManager = null;
            }
            
            sistemaInicializado = false;
            apiDisponivel = false;
            
            // Aguardar limpeza
            Thread.sleep(2000);
            
            // Reinicializar
            boolean sucesso = inicializarApiManager();
            
            if (sucesso) {
                LOGGER.info("Reinicialização bem-sucedida");
                System.out.println("✅ Sistema reinicializado com sucesso");
            } else {
                LOGGER.warning("Falha na reinicialização");
                System.out.println("❌ Falha na reinicialização");
            }
            
            return sucesso;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro durante reinicialização forçada", e);
            System.err.println("❌ Erro na reinicialização: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Classe Timer simples para tarefas de verificação
     */
    private static class Timer {
        private final int delay;
        private final Runnable task;
        private boolean repeats = true;
        
        public Timer(int delay, Runnable task) {
            this.delay = delay;
            this.task = task;
        }
        
        public void setRepeats(boolean repeats) {
            this.repeats = repeats;
        }
        
        public void start() {
            CompletableFuture.runAsync(() -> {
                try {
                    do {
                        Thread.sleep(delay);
                        task.run();
                    } while (repeats);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
}