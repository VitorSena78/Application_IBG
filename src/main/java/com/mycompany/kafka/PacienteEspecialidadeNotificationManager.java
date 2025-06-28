package com.mycompany.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.model.bean.PacienteEspecialidade;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Gerenciador de notificações para mudanças na tabela Paciente_has_Especialidade
 * @author vitor
 */
public class PacienteEspecialidadeNotificationManager {
    private static PacienteEspecialidadeNotificationManager instance;
    private List<PacienteEspecialidadeChangeListener> listeners = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private PacienteEspecialidadeNotificationManager() {
    }
    
    public static synchronized PacienteEspecialidadeNotificationManager getInstance() {
        if (instance == null) {
            instance = new PacienteEspecialidadeNotificationManager();
        }
        return instance;
    }
    
    public void addListener(PacienteEspecialidadeChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(PacienteEspecialidadeChangeListener listener) {
        listeners.remove(listener);
    }
    
    public void processKafkaMessage(String message) {
        // Validação inicial - verifica se a mensagem não é null ou vazia
        if (message == null) {
            System.out.println("Mensagem Kafka é null - ignorando processamento");
            return;
        }
        
        if (message.trim().isEmpty()) {
            System.out.println("Mensagem Kafka está vazia - ignorando processamento");
            return;
        }
        
        try {
            // Parse da mensagem JSON do Kafka (Debezium format)
            JsonNode messageNode = objectMapper.readTree(message);
            
            // Validação adicional - verifica se o JSON foi parseado corretamente
            if (messageNode == null) {
                System.out.println("Erro no parse do JSON - messageNode é null");
                return;
            }
            
            // Verifica se os campos obrigatórios existem
            if (!messageNode.has("op")) {
                System.out.println("Mensagem Kafka não contém campo 'op' - ignorando");
                return;
            }
            
            String operation = messageNode.get("op").asText();
            JsonNode afterNode = messageNode.get("after");
            JsonNode beforeNode = messageNode.get("before");
            
            System.out.println("Operação PacienteEspecialidade: " + operation);
            System.out.println("JsonNode afterNode: " + afterNode);
            System.out.println("JsonNode beforeNode: " + beforeNode);
            
            // Executar na EDT (Event Dispatch Thread) do Swing
            SwingUtilities.invokeLater(() -> {
                try {
                    switch (operation) {
                        case "c": // CREATE
                            if (afterNode != null && !afterNode.isNull()) {
                                PacienteEspecialidade novaAssociacao = parseJsonToPacienteEspecialidade(afterNode);
                                notifyPacienteEspecialidadeAdded(novaAssociacao);
                            } else {
                                System.out.println("Operação CREATE sem dados 'after' válidos");
                            }
                            break;
                            
                        case "u": // UPDATE
                            if (afterNode != null && !afterNode.isNull()) {
                                PacienteEspecialidade associacaoAtualizada = parseJsonToPacienteEspecialidade(afterNode);
                                notifyPacienteEspecialidadeUpdated(associacaoAtualizada);
                            } else {
                                System.out.println("Operação UPDATE sem dados 'after' válidos");
                            }
                            break;
                            
                        case "d": // DELETE
                            if (beforeNode != null && !beforeNode.isNull()) {
                                // Verifica se os campos ID existem antes de tentar acessá-los
                                if (beforeNode.has("Paciente_id") && beforeNode.has("Especialidade_id")) {
                                    Integer pacienteId = beforeNode.get("Paciente_id").asInt();
                                    Integer especialidadeId = beforeNode.get("Especialidade_id").asInt();
                                    notifyPacienteEspecialidadeDeleted(pacienteId, especialidadeId);
                                } else {
                                    System.out.println("Operação DELETE sem campos de ID nos dados 'before'");
                                }
                            } else {
                                System.out.println("Operação DELETE sem dados 'before' válidos");
                            }
                            break;
                            
                        default:
                            System.out.println("Operação desconhecida: " + operation);
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao processar operação '" + operation + "': " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            System.out.println("Erro ao processar mensagem Kafka: " + e.getMessage());
            System.out.println("Mensagem problemática: " + message);
            e.printStackTrace();
        }
    }
    
    private PacienteEspecialidade parseJsonToPacienteEspecialidade(JsonNode node) throws Exception {
        // Validação inicial
        if (node == null || node.isNull()) {
            throw new IllegalArgumentException("JsonNode é null ou vazio");
        }
        
        // Cria uma nova instância de PacienteEspecialidade
        PacienteEspecialidade pacienteEspecialidade = new PacienteEspecialidade();

        // Campos obrigatórios
        if (node.has("Paciente_id") && !node.get("Paciente_id").isNull()) {
            pacienteEspecialidade.setPacienteId(node.get("Paciente_id").asInt());
        }
        
        if (node.has("Especialidade_id") && !node.get("Especialidade_id").isNull()) {
            pacienteEspecialidade.setEspecialidadeId(node.get("Especialidade_id").asInt());
        }
        
        // Conversão especial para data_atendimento (Debezium converte DATE para dias desde epoch)
        if (node.has("data_atendimento") && !node.get("data_atendimento").isNull()) {
            try {
                int daysSinceEpoch = node.get("data_atendimento").asInt();
                LocalDate date = LocalDate.ofEpochDay(daysSinceEpoch);
                String formattedDate = date.format(DATE_FORMATTER);
                pacienteEspecialidade.setDataAtendimento(formattedDate);
                System.out.println("Data de atendimento convertida: " + daysSinceEpoch + " dias -> " + formattedDate);
            } catch (Exception e) {
                System.out.println("Erro ao converter data de atendimento: " + e.getMessage());
                // Se falhar a conversão, tenta como string
                String dataStr = node.get("data_atendimento").asText();
                if (!"null".equals(dataStr)) {
                    pacienteEspecialidade.setDataAtendimento(dataStr);
                } else {
                    pacienteEspecialidade.setDataAtendimento("Não informada");
                }
            }
        } else {
            pacienteEspecialidade.setDataAtendimento("Não informada");
        }

        return pacienteEspecialidade;
    }
    
    private void notifyPacienteEspecialidadeAdded(PacienteEspecialidade pacienteEspecialidade) {
        System.out.println("=== NOVA ASSOCIAÇÃO PACIENTE-ESPECIALIDADE ===");
        System.out.println("Paciente ID: " + pacienteEspecialidade.getPacienteId());
        System.out.println("Especialidade ID: " + pacienteEspecialidade.getEspecialidadeId());
        System.out.println("Data de Atendimento: " + pacienteEspecialidade.getDataAtendimento());
        
        for (PacienteEspecialidadeChangeListener listener : listeners) {
            try {
                listener.onPacienteEspecialidadeAdded(pacienteEspecialidade);
            } catch (Exception e) {
                System.out.println("Erro ao notificar listener sobre adição: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void notifyPacienteEspecialidadeUpdated(PacienteEspecialidade pacienteEspecialidade) {
        System.out.println("=== ASSOCIAÇÃO PACIENTE-ESPECIALIDADE ATUALIZADA ===");
        System.out.println("Paciente ID: " + pacienteEspecialidade.getPacienteId());
        System.out.println("Especialidade ID: " + pacienteEspecialidade.getEspecialidadeId());
        System.out.println("Data de Atendimento: " + pacienteEspecialidade.getDataAtendimento());
        
        
        for (PacienteEspecialidadeChangeListener listener : listeners) {
            try {
                listener.onPacienteEspecialidadeUpdated(pacienteEspecialidade);
            } catch (Exception e) {
                System.out.println("Erro ao notificar listener sobre atualização: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void notifyPacienteEspecialidadeDeleted(Integer pacienteId, Integer especialidadeId) {
        System.out.println("=== ASSOCIAÇÃO PACIENTE-ESPECIALIDADE REMOVIDA ===");
        System.out.println("Paciente ID: " + pacienteId);
        System.out.println("Especialidade ID: " + especialidadeId);
        
        for (PacienteEspecialidadeChangeListener listener : listeners) {
            try {
                listener.onPacienteEspecialidadeDeleted(pacienteId, especialidadeId);
            } catch (Exception e) {
                System.out.println("Erro ao notificar listener sobre remoção: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}