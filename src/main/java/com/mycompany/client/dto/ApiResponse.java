package com.mycompany.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

/**
 * Classe para receber respostas padronizadas da API Spring Boot
 * Espelha a estrutura da ApiResponse do servidor
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    
    private boolean success;
    private T data;
    private String message;
    private String error;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    // Construtor padrão necessário para Jackson
    public ApiResponse() {
    }
    
    // Construtor com parâmetros para facilitar testes
    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters e Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Verifica se a resposta indica sucesso e tem dados
     */
    public boolean hasData() {
        return success && data != null;
    }
    
    /**
     * Obtém os dados se a resposta for bem-sucedida, caso contrário retorna null
     */
    public T getDataOrNull() {
        return (success && data != null) ? data : null;
    }
    
    /**
     * Obtém uma mensagem descritiva do estado da resposta
     */
    public String getStatusMessage() {
        if (success) {
            return message != null ? message : "Operação realizada com sucesso";
        } else {
            return error != null ? error : "Erro desconhecido";
        }
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}