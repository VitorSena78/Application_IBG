package com.mycompany.kafka;

import com.mycompany.listener.PacienteChangeListener;
import com.mycompany.model.bean.Paciente;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author vitor
 */
public class PacienteNotificationManager {
    
    private static PacienteNotificationManager instance;
    private List<PacienteChangeListener> listeners = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private PacienteNotificationManager() {
    
    }
    
    public static synchronized PacienteNotificationManager getInstance() {
        if (instance == null) {
            instance = new PacienteNotificationManager();
        }
        return instance;
    }
    
    public void addListener(PacienteChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(PacienteChangeListener listener) {
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
            
            System.out.println("Operação: " + operation);
            System.out.println("JsonNode afterNode: " + afterNode);
            System.out.println("JsonNode beforeNode: " + beforeNode);
            
            // Executar na EDT (Event Dispatch Thread) do Swing
            SwingUtilities.invokeLater(() -> {
                try {
                    switch (operation) {
                        case "c": // CREATE
                            if (afterNode != null && !afterNode.isNull()) {
                                Paciente novoPaciente = parseJsonToPaciente(afterNode);
                                notifyPacienteAdded(novoPaciente);
                            } else {
                                System.out.println("Operação CREATE sem dados 'after' válidos");
                            }
                            break;
                            
                        case "u": // UPDATE
                            if (afterNode != null && !afterNode.isNull()) {
                                Paciente pacienteAtualizado = parseJsonToPaciente(afterNode);
                                notifyPacienteUpdated(pacienteAtualizado);
                            } else {
                                System.out.println("Operação UPDATE sem dados 'after' válidos");
                            }
                            break;
                            
                        case "d": // DELETE
                            if (beforeNode != null && !beforeNode.isNull()) {
                                // Verifica se o campo ID existe antes de tentar acessá-lo
                                if (beforeNode.has("id")) {
                                    int pacienteId = beforeNode.get("id").asInt();
                                    notifyPacienteDeleted(pacienteId);
                                } else {
                                    System.out.println("Operação DELETE sem campo 'id' nos dados 'before'");
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
    
    private Paciente parseJsonToPaciente(JsonNode node) throws Exception {
        // Validação inicial
        if (node == null || node.isNull()) {
            throw new IllegalArgumentException("JsonNode é null ou vazio");
        }
        
        // Cria uma nova instância de Paciente
        Paciente paciente = new Paciente();

        // Campos básicos
        if (node.has("id") && !node.get("id").isNull()) {
            paciente.setId(node.get("id").asInt()); 
        }
        if (node.has("nome") && !node.get("nome").isNull()) {
            paciente.setNome(node.get("nome").asText()); 
        }
        
        // Conversão especial para data_nascimento (Debezium converte DATE para dias desde epoch)
        if (node.has("data_nascimento") && !node.get("data_nascimento").isNull()) {
            try {
                int daysSinceEpoch = node.get("data_nascimento").asInt();
                LocalDate date = LocalDate.ofEpochDay(daysSinceEpoch);
                String formattedDate = date.format(DATE_FORMATTER);
                paciente.setDataNascimento(formattedDate);
                System.out.println("Data convertida: " + daysSinceEpoch + " dias -> " + formattedDate);
            } catch (Exception e) {
                System.out.println("Erro ao converter data: " + e.getMessage());
                // Se falhar a conversão, tenta como string
                paciente.setDataNascimento(node.get("data_nascimento").asText());
            }
        }
        
        if (node.has("idade") && !node.get("idade").isNull()) {
            paciente.setIdade(node.get("idade").asInt());
        }
        if (node.has("nome_da_mae") && !node.get("nome_da_mae").isNull()) {
            paciente.setNomeDaMae(node.get("nome_da_mae").asText()); 
        }
        if (node.has("cpf") && !node.get("cpf").isNull()) {
            paciente.setCpf(node.get("cpf").asText()); 
        }
        if (node.has("sus") && !node.get("sus").isNull()) {
            paciente.setSus(node.get("sus").asText());
        }
        if (node.has("telefone") && !node.get("telefone").isNull()) {
            paciente.setTelefone(node.get("telefone").asText()); 
        }
        if (node.has("endereço") && !node.get("endereço").isNull()) {
            paciente.setEndereco(node.get("endereço").asText()); 
        }

        // Sinais vitais
        if (node.has("pa_x_mmhg") && !node.get("pa_x_mmhg").isNull()) {
            paciente.setPaXMmhg(node.get("pa_x_mmhg").asText()); 
        }
        if (node.has("fc_bpm") && !node.get("fc_bpm").isNull()) {
            double fcValue = node.get("fc_bpm").asDouble();
            if (fcValue != 0.0) paciente.setFcBpm((float) fcValue);
        }
        if (node.has("fr_ibpm") && !node.get("fr_ibpm").isNull()) {
            double frValue = node.get("fr_ibpm").asDouble();
            if (frValue != 0.0) paciente.setFrIbpm((float) frValue);
        }
        if (node.has("temperatura_c") && !node.get("temperatura_c").isNull()) {
            double tempValue = node.get("temperatura_c").asDouble();
            if (tempValue != 0.0) paciente.setTemperaturaC((float) tempValue);
        }
        if (node.has("hgt_mgld") && !node.get("hgt_mgld").isNull()) {
            double hgtValue = node.get("hgt_mgld").asDouble();
            if (hgtValue != 0.0) paciente.setHgtMgld((float) hgtValue);
        }
        if (node.has("spo2") && !node.get("spo2").isNull()) {
            double spo2Value = node.get("spo2").asDouble();
            if (spo2Value != 0.0) paciente.setSpo2((float) spo2Value);
        }

        // Dados antropométricos
        if (node.has("peso") && !node.get("peso").isNull()) {
            double pesoValue = node.get("peso").asDouble();
            if (pesoValue != 0.0) paciente.setPeso((float) pesoValue);
        }
        if (node.has("altura") && !node.get("altura").isNull()) {
            double alturaValue = node.get("altura").asDouble();
            if (alturaValue != 0.0) paciente.setAltura((float) alturaValue);
        }
        if (node.has("imc") && !node.get("imc").isNull()) {
            double imcValue = node.get("imc").asDouble();
            if (imcValue != 0.0) paciente.setImc((float) imcValue);
        }

        return paciente;
    }
    
    private void notifyPacienteAdded(Paciente paciente) {
        for (PacienteChangeListener listener : listeners) {
            try {
                listener.onPacienteAdded(paciente);
            } catch (Exception e) {
                System.out.println("Erro ao notificar listener sobre adição: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void notifyPacienteUpdated(Paciente paciente) {
        
        System.out.println("notifyPacienteUpdated: " + paciente.toString());
        
        for (PacienteChangeListener listener : listeners) {
            try {
                listener.onPacienteUpdated(paciente);
            } catch (Exception e) {
                System.out.println("Erro ao notificar listener sobre atualização: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void notifyPacienteDeleted(int pacienteId) {
        for (PacienteChangeListener listener : listeners) {
            try {
                listener.onPacienteDeleted(pacienteId);
            } catch (Exception e) {
                System.out.println("Erro ao notificar listener sobre remoção: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}