package com.mycompany.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class PacienteEspecialidadeConsumer implements Runnable {
    private final String topic = "clinica.clinica.Paciente_has_Especialidade";
    private volatile boolean running = true;
    
    @Override
    public void run() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "paciente-especialidade-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
                 "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
                 "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            
            System.out.println("Kafka Consumer para Paciente_has_Especialidade iniciado. Aguardando mensagens...");
            
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Mensagem recebida (Paciente-Especialidade): " + record.value());
                    
                    // Delegar processamento para o NotificationManager específico
                    PacienteEspecialidadeNotificationManager.getInstance()
                        .processKafkaMessage(record.value());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro no PacienteEspecialidadeConsumer: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void shutdown() {
        running = false;
    }
}