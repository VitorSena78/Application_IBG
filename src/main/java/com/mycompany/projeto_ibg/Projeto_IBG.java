package com.mycompany.projeto_ibg;

import com.mycompany.kafka.PacienteConsumer;

public class Projeto_IBG {
    
    private static PacienteConsumer pacienteConsumer;
    private static Thread consumidorThread;

    public static void main(String[] args) {
        System.out.println("Iniciando aplicação IBG...");
        
        // Configurar shutdown hook para limpar recursos
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Encerrando aplicação...");
            if (pacienteConsumer != null) {
                pacienteConsumer.shutdown();
            }
            if (consumidorThread != null && consumidorThread.isAlive()) {
                try {
                    consumidorThread.join(3000); // Aguarda até 3 segundos
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
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
        
        // Inicia o consumidor Kafka em uma thread separada
        try {
            pacienteConsumer = new PacienteConsumer();
            consumidorThread = new Thread(pacienteConsumer);
            consumidorThread.setDaemon(true); // Thread daemon não impede o JVM de encerrar
            consumidorThread.setName("KafkaConsumerThread");
            consumidorThread.start();
            
            System.out.println("Consumidor Kafka iniciado.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar consumidor Kafka: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Aplicação IBG iniciada com sucesso!");
    
    }
}
