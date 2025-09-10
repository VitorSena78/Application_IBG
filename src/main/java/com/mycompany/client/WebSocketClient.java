package com.mycompany.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.client.dto.PacienteDTO;
import com.mycompany.client.dto.PacienteEspecialidadeDTO;
import com.mycompany.client.mapper.DtoMapper;
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
 * Versão adaptada para trabalhar com DTOs e notificações em lote
 */
@ClientEndpoint
public class WebSocketClient {
    
    private static final Logger LOGGER = Logger.getLogger(WebSocketClient.class.getName());
    private Session session;
    private final ObjectMapper objectMapper;
    private final String serverUrl;
    
    private final List<PacienteChangeListener> pacienteListeners = new ArrayList<>();
    private final List<PacienteEspecialidadeChangeListener> pacienteEspecialidadeListeners = new ArrayList<>();
    
    private volatile boolean connected = false;
    private volatile boolean reconnecting = false;
    private volatile boolean shouldReconnect = true;
    
    private CountDownLatch connectionLatch;
    
    public WebSocketClient(String serverUrl) {
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
    
    public boolean connect() {
        return connect(10000); // 10 segundos de timeout padrão
    }
    
    public boolean connect(long timeoutMs) {
        try {
            LOGGER.info("Tentando conectar ao WebSocket...");
            shouldReconnect = true;
            
            connectionLatch = new CountDownLatch(1);
            
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            
            container.setDefaultMaxSessionIdleTimeout(30000);
            
            String wsUrl = serverUrl;
            if (!wsUrl.endsWith("/")) {
                wsUrl += "/";
            }
            wsUrl += "ws/notifications";
            
            URI serverEndpoint = new URI(wsUrl);
            
            LOGGER.info("Conectando ao WebSocket: " + serverEndpoint);
            
            session = container.connectToServer(this, serverEndpoint);
            
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
    
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        this.connected = true;
        this.reconnecting = false;
        
        LOGGER.info("✓ Conexão WebSocket estabelecida: " + session.getId());
        
        if (connectionLatch != null) {
            connectionLatch.countDown();
        }
        
        sendMessage("{\"action\":\"subscribe\",\"types\":[\"paciente\",\"paciente_especialidade\"]}");
    }
    
    @OnMessage
    public void onMessage(String message) {
        LOGGER.info("← Mensagem recebida via WebSocket: " + message);
        
        SwingUtilities.invokeLater(() -> processWebSocketMessage(message));
    }
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        this.connected = false;
        LOGGER.info("✗ Conexão WebSocket fechada: " + closeReason.getReasonPhrase() + 
                     " (código: " + closeReason.getCloseCode() + ")");
        
        if (connectionLatch != null) {
            connectionLatch.countDown();
        }
        
        if (shouldReconnect && !reconnecting && 
            closeReason.getCloseCode() != CloseReason.CloseCodes.NORMAL_CLOSURE) {
            scheduleReconnect();
        }
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        this.connected = false;
        LOGGER.log(Level.SEVERE, "⚠ Erro na conexão WebSocket", throwable);
        
        if (connectionLatch != null) {
            connectionLatch.countDown();
        }
        
        if (shouldReconnect && !reconnecting) {
            scheduleReconnect();
        }
    }
    
    private void processWebSocketMessage(String message) {
        try {
            LOGGER.info("=== PROCESSANDO MENSAGEM WEBSOCKET ===");
            LOGGER.info("Mensagem completa: " + message);

            JsonNode messageNode = objectMapper.readTree(message);

            if (messageNode.has("type") && "system".equals(messageNode.get("type").asText())) {
                LOGGER.info("Mensagem do sistema: " + messageNode.get("message").asText());
                return;
            }

            if (!messageNode.has("type") || !messageNode.has("action") || !messageNode.has("data") && !"DELETED_BATCH".equals(messageNode.get("action").asText())) {
                LOGGER.warning("Mensagem WebSocket inválida - campos obrigatórios ausentes");
                LOGGER.warning("Campos disponíveis: " + messageNode.fieldNames());
                return;
            }

            String type = messageNode.get("type").asText();
            String action = messageNode.get("action").asText();
            JsonNode dataNode = messageNode.get("data");

            LOGGER.info("Type: " + type);
            LOGGER.info("Action: " + action);
            LOGGER.info("Data presente: " + (dataNode != null && !dataNode.isNull()));
            LOGGER.info("Listeners paciente registrados: " + pacienteListeners.size());

            switch (type) {
                case "paciente":
                    LOGGER.info("Processando notificação de paciente...");
                    processPacienteNotification(action, dataNode);
                    break;

                case "paciente_especialidade":
                    LOGGER.info("Processando notificação de associação...");
                    processPacienteEspecialidadeNotification(action, dataNode, messageNode);
                    break;

                default:
                    LOGGER.warning("Tipo de notificação desconhecido: " + type);
            }

            LOGGER.info("=== FIM PROCESSAMENTO MENSAGEM ===");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao processar mensagem WebSocket: " + message, e);
        }
    }
    
    private void processPacienteNotification(String action, JsonNode dataNode) {
        try {
            String actionLower = action.toLowerCase();
            
            switch (actionLower) {
                case "created":
                case "create":
                    if (dataNode != null && !dataNode.isNull()) {
                        PacienteDTO pacienteDto = objectMapper.treeToValue(dataNode, PacienteDTO.class);
                        Paciente novoPaciente = DtoMapper.toModel(pacienteDto);
                        if (novoPaciente != null) {
                            notifyPacienteAdded(novoPaciente);
                        }
                    }
                    break;
                case "updated":
                case "update":
                    if (dataNode != null && !dataNode.isNull()) {
                        PacienteDTO pacienteDto = objectMapper.treeToValue(dataNode, PacienteDTO.class);
                        Paciente pacienteAtualizado = DtoMapper.toModel(pacienteDto);
                        if (pacienteAtualizado != null) {
                            LOGGER.info("✅ Processando atualização de paciente: " + pacienteAtualizado.getNome() + " (ID: " + pacienteAtualizado.getId() + ")");
                            notifyPacienteUpdated(pacienteAtualizado);
                        } else {
                            LOGGER.warning("⚠️ Falha ao converter DTO para modelo - paciente nulo");
                        }
                    } else {
                        LOGGER.warning("⚠️ DataNode nulo ou vazio para ação de atualização");
                    }
                    break;
                case "deleted":
                case "delete":
                    if (dataNode != null && dataNode.has("id")) {
                        int pacienteId = dataNode.get("id").asInt();
                        LOGGER.info("✅ Processando remoção de paciente ID: " + pacienteId);
                        notifyPacienteDeleted(pacienteId);
                    } else {
                        LOGGER.warning("⚠️ ID não encontrado nos dados para remoção");
                    }
                    break;
                default:
                    LOGGER.warning("❌ Ação desconhecida para paciente: '" + action + "' (original) / '" + actionLower + "' (lowercase)");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Erro ao processar notificação de paciente: " + action, e);
        }
    }
    
    /**
     * Processa notificações de mudanças em PacienteEspecialidade, incluindo operações em lote.
     * Remove duplicatas e sincroniza corretamente com a interface
     */
    private void processPacienteEspecialidadeNotification(String action, JsonNode dataNode, JsonNode messageNode) {
        try {
            LOGGER.info("=== PROCESSANDO NOTIFICAÇÃO PACIENTE-ESPECIALIDADE ===");
            LOGGER.info("Action: " + action);
            LOGGER.info("DataNode disponível: " + (dataNode != null && !dataNode.isNull()));

            switch (action.toLowerCase()) {
                case "created" -> {
                    LOGGER.info("Processando CREATED...");
                    // ✅ CORREÇÃO CRÍTICA: Processar corretamente dados CREATED
                    if (dataNode != null) {
                        if (dataNode.isArray()) {
                            // Múltiplas associações
                            LOGGER.info("CREATED com array de " + dataNode.size() + " associações");
                            List<PacienteEspecialidadeDTO> dtos = objectMapper.convertValue(
                                dataNode, new TypeReference<List<PacienteEspecialidadeDTO>>() {});
                            List<PacienteEspecialidade> associacoes = DtoMapper.toPacienteEspecialidadeModelList(dtos);

                            // Extrair pacienteId da primeira associação
                            Integer pacienteId = associacoes.isEmpty() ? null : associacoes.get(0).getPacienteId();

                            if (pacienteId != null && !associacoes.isEmpty()) {
                                LOGGER.info("✅ Notificando batch created: " + associacoes.size() + " associações para paciente " + pacienteId);
                                notifyPacienteEspecialidadeBatchCreated(pacienteId, associacoes);
                            }
                        } else if (dataNode.isObject()) {
                            // Associação única
                            LOGGER.info("CREATED com objeto único");
                            PacienteEspecialidadeDTO dto = objectMapper.treeToValue(dataNode, PacienteEspecialidadeDTO.class);
                            PacienteEspecialidade associacao = DtoMapper.toModel(dto);
                            if (associacao != null) {
                                LOGGER.info("✅ Notificando associação individual criada");
                                notifyPacienteEspecialidadeAdded(associacao);
                            }
                        }
                    }
                }

                case "created_batch" -> {
                    LOGGER.info("Processando CREATED_BATCH...");
                    if (dataNode != null && dataNode.isArray()) {
                        List<PacienteEspecialidadeDTO> dtos = objectMapper.convertValue(
                            dataNode, new TypeReference<List<PacienteEspecialidadeDTO>>() {});
                        List<PacienteEspecialidade> associacoes = DtoMapper.toPacienteEspecialidadeModelList(dtos);

                        Integer pacienteId = messageNode.has("pacienteId") ? messageNode.get("pacienteId").asInt() : null;

                        if (pacienteId != null) {
                            LOGGER.info("✅ Notificando batch created: " + associacoes.size() + " associações");
                            notifyPacienteEspecialidadeBatchCreated(pacienteId, associacoes);
                        }
                    }
                }

                case "updated" -> {
                    LOGGER.info("Processando UPDATED...");
                    if (dataNode != null && dataNode.isObject()) {
                        PacienteEspecialidadeDTO dto = objectMapper.treeToValue(dataNode, PacienteEspecialidadeDTO.class);
                        PacienteEspecialidade associacao = DtoMapper.toModel(dto);
                        if (associacao != null) {
                            notifyPacienteEspecialidadeUpdated(associacao);
                        }
                    }
                }

                case "deleted" -> {
                    LOGGER.info("Processando DELETED individual...");
                    if (dataNode != null && dataNode.has("pacienteId") && dataNode.has("especialidadeId")) {
                        int pacienteId = dataNode.get("pacienteId").asInt();
                        int especialidadeId = dataNode.get("especialidadeId").asInt();
                        notifyPacienteEspecialidadeDeleted(pacienteId, especialidadeId);
                    }
                }

                case "deleted_batch" -> {
                    LOGGER.info("Processando DELETED_BATCH...");
                    Integer pacienteId = messageNode.has("pacienteId") ? messageNode.get("pacienteId").asInt() : null;

                    if (pacienteId != null) {
                        List<PacienteEspecialidade> associacoesDeletadas = null;

                        if (dataNode != null && dataNode.isArray() && dataNode.size() > 0) {
                            try {
                                List<PacienteEspecialidadeDTO> dtosRemovidos = objectMapper.convertValue(
                                    dataNode, new TypeReference<List<PacienteEspecialidadeDTO>>() {});
                                associacoesDeletadas = DtoMapper.toPacienteEspecialidadeModelList(dtosRemovidos);
                                LOGGER.info("Associações específicas a serem removidas: " + associacoesDeletadas.size());
                            } catch (Exception e) {
                                LOGGER.log(Level.WARNING, "Erro ao processar lista de associações deletadas", e);
                            }
                        }

                        LOGGER.info("✅ Notificando batch deleted para paciente " + pacienteId);
                        notifyPacienteEspecialidadeBatchDeleted(pacienteId, associacoesDeletadas);
                    }
                }

                case "complete_update" -> {
                    LOGGER.info("Processando COMPLETE_UPDATE...");
                    Integer pacienteId = messageNode.has("pacienteId") ? messageNode.get("pacienteId").asInt() : null;

                    if (pacienteId != null && dataNode != null && dataNode.isArray()) {
                        List<PacienteEspecialidadeDTO> dtos = objectMapper.convertValue(
                            dataNode, new TypeReference<List<PacienteEspecialidadeDTO>>() {});
                        List<PacienteEspecialidade> novasAssociacoes = DtoMapper.toPacienteEspecialidadeModelList(dtos);

                        LOGGER.info("✅ Notificando complete update: " + novasAssociacoes.size() + " associações");
                        notifyPacienteEspecialidadeCompleteUpdate(pacienteId, novasAssociacoes);
                    }
                }

                default -> {
                    LOGGER.warning("❌ Ação desconhecida para paciente_especialidade: " + action);
                }
            }

            LOGGER.info("=== FIM PROCESSAMENTO PACIENTE-ESPECIALIDADE ===");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Erro ao processar notificação de paciente_especialidade: " + action, e);
        }
    }
    
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
    
    private void scheduleReconnect() {
        if (reconnecting || !shouldReconnect) {
            return;
        }
        
        reconnecting = true;
        LOGGER.info("Agendando tentativas de reconexão...");
        
        Thread reconnectThread = new Thread(() -> {
            int tentativas = 0;
            int maxTentativas = 5;
            long intervalo = 5000; // 5 segundos
            
            while (tentativas < maxTentativas && !connected && shouldReconnect) {
                try {
                    Thread.sleep(intervalo);
                    tentativas++;
                    
                    LOGGER.info("⟳ Tentativa de reconexão " + tentativas + "/" + maxTentativas);
                    
                    if (connect(5000)) { 
                        LOGGER.info("✓ Reconexão bem-sucedida!");
                        return;
                    }
                    
                    intervalo = Math.min(intervalo * 2, 60000); 
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
    
    // Método para notificar a criação em lote
    private void notifyPacienteEspecialidadeBatchCreated(Integer pacienteId, List<PacienteEspecialidade> associacoes) {
        LOGGER.info("=== NOVAS ASSOCIAÇÕES (LOTE) PACIENTE-ESPECIALIDADE (WebSocket) ===");
        LOGGER.info("Notificando criação de lote para paciente ID: " + pacienteId + " com " + associacoes.size() + " associações.");
        
        synchronized (pacienteEspecialidadeListeners) {
            for (PacienteEspecialidadeChangeListener listener : pacienteEspecialidadeListeners) {
                try {
                    listener.onPacienteEspecialidadeBatchCreated(pacienteId, associacoes);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro ao notificar listener sobre criação em lote de associação", e);
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
    
    // Método para notificar a deleção em lote
    private void notifyPacienteEspecialidadeBatchDeleted(Integer pacienteId, List<PacienteEspecialidade> associacoesDeletadas) {
        LOGGER.info("=== ASSOCIAÇÕES (LOTE) REMOVIDAS (WebSocket) ===");

        if (associacoesDeletadas != null && !associacoesDeletadas.isEmpty()) {
            LOGGER.info("Removendo " + associacoesDeletadas.size() + " associações específicas para paciente " + pacienteId);
        } else {
            LOGGER.info("Removendo TODAS as associações para paciente " + pacienteId);
        }

        synchronized (pacienteEspecialidadeListeners) {
            for (PacienteEspecialidadeChangeListener listener : pacienteEspecialidadeListeners) {
                try {
                    listener.onPacienteEspecialidadeBatchDeleted(pacienteId, associacoesDeletadas);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro ao notificar listener sobre deleção em lote", e);
                }
            }
        }
    }
    
  // Atualização completa das associações
    private void notifyPacienteEspecialidadeCompleteUpdate(Integer pacienteId, List<PacienteEspecialidade> novasAssociacoes) {
        LOGGER.info("=== ATUALIZAÇÃO COMPLETA DE ASSOCIAÇÕES (WebSocket) ===");
        LOGGER.info("Atualizando completamente associações para paciente " + pacienteId + 
                   " com " + (novasAssociacoes != null ? novasAssociacoes.size() : 0) + " associações");

        synchronized (pacienteEspecialidadeListeners) {
            for (PacienteEspecialidadeChangeListener listener : pacienteEspecialidadeListeners) {
                try {
                    listener.onPacienteEspecialidadeCompleteUpdate(pacienteId, novasAssociacoes);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro ao notificar listener sobre atualização completa", e);
                }
            }
        }
    }  
    
    public boolean isConnected() {
        return connected && session != null && session.isOpen();
    }
    
    public boolean isReconnecting() {
        return reconnecting;
    }
}