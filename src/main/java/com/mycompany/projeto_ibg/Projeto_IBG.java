package com.mycompany.projeto_ibg;

import com.mycompany.kafka.PacienteConsumer;
import com.mycompany.kafka.PacienteEspecialidadeConsumer;

public class Projeto_IBG {
    
    private static PacienteConsumer pacienteConsumer;
    private static PacienteEspecialidadeConsumer pacienteEspecialidadeConsumer;
    private static Thread pacienteConsumerThread;
    private static Thread pacienteEspecialidadeConsumerThread;

    public static void main(String[] args) {
        System.out.println("Iniciando aplicação IBG...");
        
        // Configurar shutdown hook para limpar recursos
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Encerrando aplicação...");
            
            // Parar o consumer da tabela Paciente
            if (pacienteConsumer != null) {
                pacienteConsumer.shutdown();
                System.out.println("Consumer Paciente parado.");
            }
            
            // Parar o consumer da tabela Paciente_has_Especialidade
            if (pacienteEspecialidadeConsumer != null) {
                pacienteEspecialidadeConsumer.shutdown();
                System.out.println("Consumer Paciente_has_Especialidade parado.");
            }
            
            // Aguardar threads terminarem
            if (pacienteConsumerThread != null && pacienteConsumerThread.isAlive()) {
                try {
                    pacienteConsumerThread.join(3000); // Aguarda até 3 segundos
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            if (pacienteEspecialidadeConsumerThread != null && pacienteEspecialidadeConsumerThread.isAlive()) {
                try {
                    pacienteEspecialidadeConsumerThread.join(3000); // Aguarda até 3 segundos
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            System.out.println("Todos os consumers foram encerrados.");
        }));
        
        // Inicia a interface gráfica
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                new Main().setVisible(true);
                System.out.println("Interface gráfica iniciada.");
            } catch (Exception e) {
                System.err.println("Erro ao iniciar interface gráfica: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // Inicia os consumidores Kafka em threads separadas
        startKafkaConsumers();
        
        System.out.println("Aplicação IBG iniciada com sucesso!");
    }
    
    private static void startKafkaConsumers() {
        // Iniciar consumer para tabela Paciente
        try {
            pacienteConsumer = new PacienteConsumer();
            pacienteConsumerThread = new Thread(pacienteConsumer);
            pacienteConsumerThread.setDaemon(true); // Thread daemon não impede o JVM de encerrar
            pacienteConsumerThread.setName("PacienteKafkaConsumerThread");
            pacienteConsumerThread.start();
            
            System.out.println("✓ Consumer Paciente iniciado.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar consumer Paciente: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Iniciar consumer para tabela Paciente_has_Especialidade
        try {
            pacienteEspecialidadeConsumer = new PacienteEspecialidadeConsumer();
            pacienteEspecialidadeConsumerThread = new Thread(pacienteEspecialidadeConsumer);
            pacienteEspecialidadeConsumerThread.setDaemon(true); // Thread daemon não impede o JVM de encerrar
            pacienteEspecialidadeConsumerThread.setName("PacienteEspecialidadeKafkaConsumerThread");
            pacienteEspecialidadeConsumerThread.start();
            
            System.out.println("✓ Consumer Paciente_has_Especialidade iniciado.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar consumer Paciente_has_Especialidade: " + e.getMessage());
            e.printStackTrace();
        }
    }
}