package com.mycompany.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.PartitionInfo;
import java.time.Duration;
import java.util.*;

/**
 * Classe para testar conectividade e listar mensagens do Kafka
 */
public class KafkaDebugger {

    public KafkaDebugger() {
    }
    
    
    public static void testKafkaConnection() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "debug-consumer-group-" + System.currentTimeMillis());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
                 "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
                 "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Lê desde o início
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            
            // Lista todos os tópicos disponíveis
            System.out.println("=== TÓPICOS DISPONÍVEIS ===");
            Map<String, List<PartitionInfo>> topics = consumer.listTopics();
            for (String topicName : topics.keySet()) {
                if (topicName.contains("clinica") || topicName.contains("Paciente")) {
                    System.out.println("Tópico encontrado: " + topicName);
                    List<PartitionInfo> partitions = topics.get(topicName);
                    System.out.println("  Partições: " + partitions.size());
                }
            }
            
            // Testa especificamente o tópico de Paciente
            String pacienteTopic = "clinica.clinica.Paciente";
            System.out.println("\n=== TESTANDO TÓPICO: " + pacienteTopic + " ===");
            
            try {
                consumer.subscribe(Collections.singletonList(pacienteTopic));
                
                // Tenta consumir mensagens por 30 segundos
                long endTime = System.currentTimeMillis() + 30000;
                int messageCount = 0;
                
                while (System.currentTimeMillis() < endTime) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    
                    for (ConsumerRecord<String, String> record : records) {
                        messageCount++;
                        System.out.println("\n--- MENSAGEM " + messageCount + " ---");
                        System.out.println("Offset: " + record.offset());
                        System.out.println("Chave: " + record.key());
                        System.out.println("Valor: " + record.value());
                        System.out.println("Timestamp: " + new Date(record.timestamp()));
                        
                        // Limita para não spammar
                        if (messageCount >= 10) {
                            System.out.println("Limitando a 10 mensagens para este teste...");
                            return;
                        }
                    }
                    
                    if (messageCount == 0) {
                        System.out.print(".");
                    }
                }
                
                if (messageCount == 0) {
                    System.out.println("\nNENHUMA mensagem encontrada no tópico " + pacienteTopic);
                    System.out.println("Isso pode indicar:");
                    System.out.println("1. Debezium não está configurado corretamente");
                    System.out.println("2. O tópico não existe");
                    System.out.println("3. Não houve mudanças no banco desde a configuração");
                } else {
                    System.out.println("\nTotal de mensagens processadas: " + messageCount);
                }
                
            } catch (Exception e) {
                System.err.println("Erro ao acessar o tópico " + pacienteTopic + ": " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Erro na conexão com Kafka: " + e.getMessage());
            e.printStackTrace();
        }
    }
}