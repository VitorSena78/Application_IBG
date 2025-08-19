package com.mycompany.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.listener.PacienteChangeListener;
import com.mycompany.listener.PacienteEspecialidadeChangeListener;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import jakarta.websocket.*;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.SwingUtilities;

/**
 * Cliente WebSocket para receber notificações em tempo real da API
 * Versão melhorada com tratamento robusto de conexão e reconexão
 */
@ClientEndpoint
public class WebSocketClient {
    
    private static final Logger LOGGER = Logger.getLogger(WebSocketClient.class.getName());
    private Session session;
    private final ObjectMapper objectMapper;
    private final String serverUrl;
    
    // Listeners para diferentes tipos de mudanças
    private final List<PacienteChangeListener> pacienteListeners = new ArrayList<>();
    private final List<PacienteEspecialidadeChangeListener> pacienteEspecialidadeListeners = new ArrayList<>();
    
    // Status da conexão
    private volatile boolean connected = false;
    private volatile boolean reconnecting = false;
    private volatile boolean shouldReconnect = true;
    
    // Para controle de conexão síncrona
    private CountDownLatch connectionLatch;
    
    public WebSocketClient(String serverUrl) {
        // Garantir que a URL está no formato correto
        if (serverUrl.startsWith("http://")) {
            this.serverUrl = serverUrl.replace("http://", "ws://");
        } else if (serverUrl.startsWith("https://")) {
            this.serverUrl = serverUrl.replace("https://", "wss://");
        } else if (!serverUrl.startsWith("ws://") && !serverUrl.startsWith("wss://")) {
            this.serverUrl = "ws://" + serverUrl;
        } else {
            this.serverUrl = serverUrl;
        }
        
        this.objectMapper = new ObjectMapper();
        
        LOGGER.info("WebSocketClient criado com URL: " + this.serverUrl);
    }
    
    /**
     * Conecta ao servidor WebSocket com timeout
     */
    public boolean connect() {
        return connect(10000); // 10 segundos de timeout padrão
    }
    
    /**
     * Conecta ao servidor WebSocket com timeout específico
     */
    public boolean connect(long timeoutMs) {
        try {
            LOGGER.info("Tentando conectar ao WebSocket...");
            shouldReconnect = true;
            
            // Preparar latch para aguardar conexão
            connectionLatch = new CountDownLatch(1);
            
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            
            // Configurar timeout do container
            container.setDefaultMaxSessionIdleTimeout(30000);
            
            // URL para o endpoint WebSocket
            String wsUrl = serverUrl;
            if (!wsUrl.endsWith("/")) {
                wsUrl += "/";
            }
            wsUrl += "ws/notifications";
            
            URI serverEndpoint = new URI(wsUrl);
            
            LOGGER.info("Conectando ao WebSocket: " + serverEndpoint);
            
            // Conecta ao servidor
            session = container.connectToServer(this, serverEndpoint);
            
            // Aguarda a conexão ser estabelecida ou timeout
            boolean connectionEstablished = connectionLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
            
            if (connectionEstablished && connected) {
                LOGGER.info("Conexão WebSocket estabelecida com sucesso");
                return true;
            } else {
                LOGGER.warning("Timeout ao estabelecer conexão WebSocket");
                disconnect();
                return false;
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao conectar WebSocket: " + e.getMessage(), e);
            connected = false;
            return false;
        }
    }
    
    /**
     * Desconecta do servidor WebSocket
     */
    public void disconnect() {
        shouldReconnect = false;
        reconnecting = false;
        
        if (session != null && session.isOpen()) {
            try {
                session.close();
                LOGGER.info("WebSocket desconectado");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erro ao desconectar WebSocket", e);
            }
        }
        connected = false;
    }
    
    // Eventos do WebSocket
    
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        this.connected = true;
        this.reconnecting = false;
        
        LOGGER.info("✓ Conexão WebSocket estabelecida: " + session.getId());
        
        // Libera o latch para indicar que a conexão foi estabelecida
        if (connectionLatch != null) {
            connectionLatch.countDown();
        }
        
        // Envia mensagem de registro para receber notificações
        sendMessage("{\"action\":\"subscribe\",\"types\":[\"paciente\",\"paciente_especialidade\"]}");
    }
    
    @OnMessage
    public void onMessage(String message) {
        LOGGER.info("← Mensagem recebida via WebSocket: " + message);
        
        // Processa a mensagem em thread separada para não bloquear o WebSocket
        SwingUtilities.invokeLater(() -> processWebSocketMessage(message));
    }
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        this.connected = false;
        LOGGER.info("✗ Conexão WebSocket fechada: " + closeReason.getReasonPhrase() + 
                   " (código: " + closeReason.getCloseCode() + ")");
        
        // Libera o latch em caso de falha na conexão
        if (connectionLatch != null) {
            connectionLatch.countDown();
        }
        
        // Tenta reconectar automaticamente se não foi fechamento intencional
        if (shouldReconnect && !reconnecting && 
            closeReason.getCloseCode() != CloseReason.CloseCodes.NORMAL_CLOSURE) {
            scheduleReconnect();
        }
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        this.connected = false;
        LOGGER.log(Level.SEVERE, "⚠ Erro na conexão WebSocket", throwable);
        
        // Libera o latch em caso de erro na conexão
        if (connectionLatch != null) {
            connectionLatch.countDown();
        }
        
        if (shouldReconnect && !reconnecting) {
            scheduleReconnect();
        }
    }
    
    /**
     * Processa mensagens recebidas via WebSocket
     */
    private void processWebSocketMessage(String message) {
        try {
            JsonNode messageNode = objectMapper.readTree(message);
            
            // Se for mensagem do sistema, apenas loga
            if (messageNode.has("type") && "system".equals(messageNode.get("type").asText())) {
                LOGGER.info("Mensagem do sistema: " + messageNode.get("message").asText());
                return;
            }
            
            // Verifica se tem os campos obrigatórios para notificações
            if (!messageNode.has("type") || !messageNode.has("action") || !messageNode.has("data")) {
                LOGGER.warning("Mensagem WebSocket inválida - campos obrigatórios ausentes: " + message);
                return;
            }
            
            String type = messageNode.get("type").asText();
            String action = messageNode.get("action").asText();
            JsonNode dataNode = messageNode.get("data");
            
            LOGGER.info("Processando notificação: type=" + type + ", action=" + action);
            
            switch (type) {
                case "paciente":
                    processPacienteNotification(action, dataNode);
                    break;
                    
                case "paciente_especialidade":
                    processPacienteEspecialidadeNotification(action, dataNode);
                    break;
                    
                default:
                    LOGGER.warning("Tipo de notificação desconhecido: " + type);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao processar mensagem WebSocket: " + message, e);
        }
    }
    
    /**
     * Processa notificações de mudanças em Paciente
     */
    private void processPacienteNotification(String action, JsonNode dataNode) {
        try {
            switch (action) {
                case "created":
                    if (dataNode != null && !dataNode.isNull()) {
                        Paciente novoPaciente = objectMapper.treeToValue(dataNode, Paciente.class);
                        notifyPacienteAdded(novoPaciente);
                    }
                    break;
                    
                case "updated":
                    if (dataNode != null && !dataNode.isNull()) {
                        Paciente pacienteAtualizado = objectMapper.treeToValue(dataNode, Paciente.class);
                        notifyPacienteUpdated(pacienteAtualizado);
                    }
                    break;
                    
                case "deleted":
                    if (dataNode != null && dataNode.has("id")) {
                        int pacienteId = dataNode.get("id").asInt();
                        notifyPacienteDeleted(pacienteId);
                    }
                    break;
                    
                default:
                    LOGGER.warning("Ação desconhecida para paciente: " + action);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao processar notificação de paciente", e);
        }
    }
    
    /**
     * Processa notificações de mudanças em PacienteEspecialidade
     */
    private void processPacienteEspecialidadeNotification(String action, JsonNode dataNode) {
        try {
            switch (action) {
                case "created":
                    if (dataNode != null && !dataNode.isNull()) {
                        PacienteEspecialidade novaAssociacao = objectMapper.treeToValue(dataNode, PacienteEspecialidade.class);
                        notifyPacienteEspecialidadeAdded(novaAssociacao);
                    }
                    break;
                    
                case "updated":
                    if (dataNode != null && !dataNode.isNull()) {
                        PacienteEspecialidade associacaoAtualizada = objectMapper.treeToValue(dataNode, PacienteEspecialidade.class);
                        notifyPacienteEspecialidadeUpdated(associacaoAtualizada);
                    }
                    break;
                    
                case "deleted":
                    if (dataNode != null && dataNode.has("pacienteId") && dataNode.has("especialidadeId")) {
                        int pacienteId = dataNode.get("pacienteId").asInt();
                        int especialidadeId = dataNode.get("especialidadeId").asInt();
                        notifyPacienteEspecialidadeDeleted(pacienteId, especialidadeId);
                    }
                    break;
                    
                default:
                    LOGGER.warning("Ação desconhecida para paciente_especialidade: " + action);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao processar notificação de paciente_especialidade", e);
        }
    }
    
    /**
     * Envia mensagem para o servidor WebSocket
     */
    private void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                LOGGER.info("→ Mensagem enviada: " + message);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao enviar mensagem WebSocket", e);
            }
        } else {
            LOGGER.warning("Não foi possível enviar mensagem - sessão não disponível");
        }
    }
    
    /**
     * Agenda tentativa de reconexão
     */
    private void scheduleReconnect() {
        if (reconnecting || !shouldReconnect) {
            return;
        }
        
        reconnecting = true;
        LOGGER.info("Agendando tentativas de reconexão...");
        
        // Executa reconexão em thread separada
        Thread reconnectThread = new Thread(() -> {
            int tentativas = 0;
            int maxTentativas = 5;
            long intervalo = 5000; // 5 segundos
            
            while (tentativas < maxTentativas && !connected && shouldReconnect) {
                try {
                    Thread.sleep(intervalo);
                    tentativas++;
                    
                    LOGGER.info("⟳ Tentativa de reconexão " + tentativas + "/" + maxTentativas);
                    
                    if (connect(5000)) { // 5 segundos de timeout para reconexão
                        LOGGER.info("✓ Reconexão bem-sucedida!");
                        return;
                    }
                    
                    // Aumenta o intervalo progressivamente
                    intervalo = Math.min(intervalo * 2, 60000); // Máximo 1 minuto
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            reconnecting = false;
            LOGGER.warning("✗ Falha na reconexão após " + maxTentativas + " tentativas");
        });
        
        reconnectThread.setDaemon(true);
        reconnectThread.setName("WebSocket-Reconnect");
        reconnectThread.start();
    }
    
    // Métodos para gerenciar listeners
    
    public void addPacienteListener(PacienteChangeListener listener) {
        synchronized (pacienteListeners) {
            pacienteListeners.add(listener);
            LOGGER.info("PacienteChangeListener adicionado - total: " + pacienteListeners.size());
        }
    }
    
    public void removePacienteListener(PacienteChangeListener listener) {
        synchronized (pacienteListeners) {
            pacienteListeners.remove(listener);
            LOGGER.info("PacienteChangeListener removido - total: " + pacienteListeners.size());
        }
    }
    
    public void addPacienteEspecialidadeListener(PacienteEspecialidadeChangeListener listener) {
        synchronized (pacienteEspecialidadeListeners) {
            pacienteEspecialidadeListeners.add(listener);
            LOGGER.info("PacienteEspecialidadeChangeListener adicionado - total: " + pacienteEspecialidadeListeners.size());
        }
    }
    
    public void removePacienteEspecialidadeListener(PacienteEspecialidadeChangeListener listener) {
        synchronized (pacienteEspecialidadeListeners) {
            pacienteEspecialidadeListeners.remove(listener);
            LOGGER.info("PacienteEspecialidadeChangeListener removido - total: " + pacienteEspecialidadeListeners.size());
        }
    }
    
    // Métodos para notificar listeners
    
    private void notifyPacienteAdded(Paciente paciente) {
        LOGGER.info("=== NOVO PACIENTE (WebSocket) ===");
        LOGGER.info("ID: " + paciente.getId() + ", Nome: " + paciente.getNome());
        
        synchronized (pacienteListeners) {
            for (PacienteChangeListener listener : pacienteListeners) {
                try {
                    listener.onPacienteAdded(paciente);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro ao notificar listener sobre adição de paciente", e);
                }
            }
        }
    }
    
    private void notifyPacienteUpdated(Paciente paciente) {
        LOGGER.info("=== PACIENTE ATUALIZADO (WebSocket) ===");
        LOGGER.info("ID: " + paciente.getId() + ", Nome: " + paciente.getNome());
        
        synchronized (pacienteListeners) {
            for (PacienteChangeListener listener : pacienteListeners) {
                try {
                    listener.onPacienteUpdated(paciente);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro ao notificar listener sobre atualização de paciente", e);
                }
            }
        }
    }
    
    private void notifyPacienteDeleted(int pacienteId) {
        LOGGER.info("=== PACIENTE REMOVIDO (WebSocket) ===");
        LOGGER.info("ID: " + pacienteId);
        
        synchronized (pacienteListeners) {
            for (PacienteChangeListener listener : pacienteListeners) {
                try {
                    listener.onPacienteDeleted(pacienteId);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro ao notificar listener sobre remoção de paciente", e);
                }
            }
        }
    }
    
    private void notifyPacienteEspecialidadeAdded(PacienteEspecialidade pacienteEspecialidade) {
        LOGGER.info("=== NOVA ASSOCIAÇÃO PACIENTE-ESPECIALIDADE (WebSocket) ===");
        LOGGER.info("Paciente ID: " + pacienteEspecialidade.getPacienteId() + 
                   ", Especialidade ID: " + pacienteEspecialidade.getEspecialidadeId());
        
        synchronized (pacienteEspecialidadeListeners) {
            for (PacienteEspecialidadeChangeListener listener : pacienteEspecialidadeListeners) {
                try {
                    listener.onPacienteEspecialidadeAdded(pacienteEspecialidade);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro ao notificar listener sobre adição de associação", e);
                }
            }
        }
    }
    
    private void notifyPacienteEspecialidadeUpdated(PacienteEspecialidade pacienteEspecialidade) {
        LOGGER.info("=== ASSOCIAÇÃO PACIENTE-ESPECIALIDADE ATUALIZADA (WebSocket) ===");
        LOGGER.info("Paciente ID: " + pacienteEspecialidade.getPacienteId() + 
                   ", Especialidade ID: " + pacienteEspecialidade.getEspecialidadeId());
        
        synchronized (pacienteEspecialidadeListeners) {
            for (PacienteEspecialidadeChangeListener listener : pacienteEspecialidadeListeners) {
                try {
                    listener.onPacienteEspecialidadeUpdated(pacienteEspecialidade);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro ao notificar listener sobre atualização de associação", e);
                }
            }
        }
    }
    
    private void notifyPacienteEspecialidadeDeleted(int pacienteId, int especialidadeId) {
        LOGGER.info("=== ASSOCIAÇÃO PACIENTE-ESPECIALIDADE REMOVIDA (WebSocket) ===");
        LOGGER.info("Paciente ID: " + pacienteId + ", Especialidade ID: " + especialidadeId);
        
        synchronized (pacienteEspecialidadeListeners) {
            for (PacienteEspecialidadeChangeListener listener : pacienteEspecialidadeListeners) {
                try {
                    listener.onPacienteEspecialidadeDeleted(pacienteId, especialidadeId);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro ao notificar listener sobre remoção de associação", e);
                }
            }
        }
    }
    
    // Getters para status
    
    public boolean isConnected() {
        return connected && session != null && session.isOpen();
    }
    
    public boolean isReconnecting() {
        return reconnecting;
    }
    
    public String getConnectionStatus() {
        if (isConnected()) {
            return "✓ Conectado";
        } else if (isReconnecting()) {
            return "⟳ Reconectando...";
        } else {
            return "✗ Desconectado";
        }
    }
    
    /**
     * Força uma tentativa de reconexão imediata
     */
    public boolean forceReconnect() {
        if (connected) {
            disconnect();
        }
        
        // Aguarda um pouco para garantir que a desconexão foi processada
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return connect();
    }
}