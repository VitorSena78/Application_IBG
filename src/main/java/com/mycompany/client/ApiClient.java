package com.mycompany.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.client.dto.ApiResponse;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.apache.http.client.config.RequestConfig;

/**
 * Cliente HTTP melhorado para comunicação com a API Spring Boot
 * Agora suporta respostas encapsuladas em ApiResponse
 */
public class ApiClient {
    
    private static final Logger LOGGER = Logger.getLogger(ApiClient.class.getName());
    private final String baseUrl;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public ApiClient(String baseUrl) {
        this(baseUrl, 10000, 15000); // Timeouts maiores por padrão
    }
    
    public ApiClient(String baseUrl, int connectionTimeout, int readTimeout) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

        RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(connectionTimeout)
            .setSocketTimeout(readTimeout)
            .setConnectionRequestTimeout(connectionTimeout)
            .build();

        this.httpClient = HttpClients.custom()
            .setDefaultRequestConfig(config)
            .setMaxConnTotal(50)
            .setMaxConnPerRoute(20)
            .build();
        
        // Configurar ObjectMapper com suporte para LocalDateTime
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        
        LOGGER.info("ApiClient inicializado com base URL: " + this.baseUrl);
    }
    
    // Métodos genéricos para requisições HTTP
    
    /**
     * Executa uma requisição GET e extrai o dado do ApiResponse
     */
    public <T> T get(String endpoint, Class<T> responseType) throws ApiException {
        HttpGet request = new HttpGet(baseUrl + endpoint);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
        
        LOGGER.info("GET: " + baseUrl + endpoint);
        
        try {
            HttpResponse response = httpClient.execute(request);
            return processResponse(response, responseType);
        } catch (IOException e) {
            throw new ApiException("Erro na requisição GET para " + endpoint + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Executa uma requisição GET que retorna uma lista
     */
    public <T> List<T> getList(String endpoint, TypeReference<List<T>> typeReference) throws ApiException {
        HttpGet request = new HttpGet(baseUrl + endpoint);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
        
        LOGGER.info("GET LIST: " + baseUrl + endpoint);
        
        try {
            HttpResponse response = httpClient.execute(request);
            return processListResponse(response, typeReference);
        } catch (IOException e) {
            throw new ApiException("Erro na requisição GET para " + endpoint + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Executa uma requisição POST
     */
    public <T, R> R post(String endpoint, T requestBody, Class<R> responseType) throws ApiException {
        HttpPost request = new HttpPost(baseUrl + endpoint);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
        
        LOGGER.info("POST: " + baseUrl + endpoint);
        
        try {
            if (requestBody != null) {
                String json = objectMapper.writeValueAsString(requestBody);
                LOGGER.fine("POST Body: " + json);
                StringEntity entity = new StringEntity(json, "UTF-8");
                request.setEntity(entity);
            }
            
            HttpResponse response = httpClient.execute(request);
            return processResponse(response, responseType);
        } catch (IOException e) {
            throw new ApiException("Erro na requisição POST para " + endpoint + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Executa uma requisição POST que retorna uma lista
     */
    public <T, R> List<R> postList(String endpoint, T requestBody, TypeReference<List<R>> typeReference) throws ApiException {
        HttpPost request = new HttpPost(baseUrl + endpoint);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        LOGGER.info("POST LIST: " + baseUrl + endpoint);

        try {
            if (requestBody != null) {
                String json = objectMapper.writeValueAsString(requestBody);
                LOGGER.fine("POST Body: " + json);
                StringEntity entity = new StringEntity(json, "UTF-8");
                request.setEntity(entity);
            }

            HttpResponse response = httpClient.execute(request);
            return processListResponse(response, typeReference);
        } catch (IOException e) {
            throw new ApiException("Erro na requisição POST para " + endpoint + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Executa uma requisição PUT
     */
    public <T, R> R put(String endpoint, T requestBody, Class<R> responseType) throws ApiException {
        HttpPut request = new HttpPut(baseUrl + endpoint);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
        
        LOGGER.info("PUT: " + baseUrl + endpoint);
        
        try {
            if (requestBody != null) {
                String json = objectMapper.writeValueAsString(requestBody);
                LOGGER.fine("PUT Body: " + json);
                StringEntity entity = new StringEntity(json, "UTF-8");
                request.setEntity(entity);
            }
            
            HttpResponse response = httpClient.execute(request);
            return processResponse(response, responseType);
        } catch (IOException e) {
            throw new ApiException("Erro na requisição PUT para " + endpoint + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Executa uma requisição DELETE
     */
    public boolean delete(String endpoint) throws ApiException {
        HttpDelete request = new HttpDelete(baseUrl + endpoint);
        request.setHeader("Content-Type", "application/json");
        
        LOGGER.info("DELETE: " + baseUrl + endpoint);
        
        try {
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            
            if (response.getEntity() != null) {
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                
                if (statusCode >= 200 && statusCode < 300) {
                    try {
                        // Tenta parsear como ApiResponse
                        JsonNode rootNode = objectMapper.readTree(responseBody);
                        if (rootNode.has("success")) {
                            boolean success = rootNode.get("success").asBoolean();
                            LOGGER.info("DELETE response: " + statusCode + " - success: " + success);
                            return success;
                        }
                    } catch (Exception e) {
                        LOGGER.fine("Resposta DELETE não é JSON estruturado, assumindo sucesso pelo status code");
                    }
                    return true; // Se não conseguir parsear, assume sucesso pelo status code
                }
            }
            
            boolean success = statusCode >= 200 && statusCode < 300;
            LOGGER.info("DELETE response: " + statusCode + (success ? " (success)" : " (failed)"));
            return success;
            
        } catch (IOException e) {
            throw new ApiException("Erro na requisição DELETE para " + endpoint + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Processa a resposta HTTP para um objeto único
     * Agora suporta respostas ApiResponse da API Spring Boot
     */
    private <T> T processResponse(HttpResponse response, Class<T> responseType) throws ApiException {
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            
            LOGGER.fine("Response status: " + statusCode);
            
            if (entity != null) {
                String responseBody = EntityUtils.toString(entity, "UTF-8");
                LOGGER.fine("Response body: " + responseBody);
                
                if (statusCode >= 200 && statusCode < 300) {
                    if (responseBody.trim().isEmpty()) {
                        return null;
                    }
                    
                    // Primeiro, verifica se é uma resposta ApiResponse
                    try {
                        JsonNode rootNode = objectMapper.readTree(responseBody);
                        
                        if (rootNode.has("success") && rootNode.has("data")) {
                            // É uma ApiResponse - verifica se foi bem-sucedida
                            boolean success = rootNode.get("success").asBoolean();
                            
                            if (!success) {
                                String error = rootNode.has("error") ? rootNode.get("error").asText() : "Erro desconhecido";
                                throw new ApiException("API retornou erro: " + error);
                            }
                            
                            // Extrai os dados da resposta
                            JsonNode dataNode = rootNode.get("data");
                            
                            if (dataNode.isNull()) {
                                return null;
                            }
                            
                            // Deserializa os dados para o tipo esperado
                            if (responseType == String.class) {
                                return responseType.cast(dataNode.asText());
                            } else if (responseType == Integer.class) {
                                return responseType.cast(dataNode.asInt());
                            } else if (responseType == Boolean.class) {
                                return responseType.cast(dataNode.asBoolean());
                            } else if (responseType == Long.class) {
                                return responseType.cast(dataNode.asLong());
                            } else if (responseType == Double.class) {
                                return responseType.cast(dataNode.asDouble());
                            } else {
                                // Para objetos complexos, usa o ObjectMapper
                                return objectMapper.readValue(dataNode.toString(), responseType);
                            }
                        } else {
                            // Não é uma ApiResponse, tenta deserializar diretamente
                            LOGGER.fine("Resposta não é ApiResponse, deserializando diretamente");
                            return deserializeDirect(responseBody, responseType);
                        }
                        
                    } catch (Exception jsonException) {
                        LOGGER.fine("Erro ao parsear como JSON, tentando deserialização direta: " + jsonException.getMessage());
                        return deserializeDirect(responseBody, responseType);
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Erro HTTP {0}: {1}", new Object[]{statusCode, responseBody});
                    
                    // Tenta extrair mensagem de erro da ApiResponse
                    try {
                        JsonNode rootNode = objectMapper.readTree(responseBody);
                        if (rootNode.has("error")) {
                            throw new ApiException("Erro HTTP " + statusCode + ": " + rootNode.get("error").asText());
                        }
                    } catch (Exception e) {
                        // Se não conseguir parsear, usa a resposta raw
                    }
                    
                    throw new ApiException("Erro HTTP " + statusCode + ": " + responseBody);
                }
            } else if (statusCode >= 200 && statusCode < 300) {
                // Resposta vazia mas sucesso
                if (responseType == Boolean.class) {
                    return responseType.cast(Boolean.TRUE);
                } else if (responseType == Integer.class) {
                    return responseType.cast(0);
                }
                return null;
            } else {
                throw new ApiException("Erro HTTP " + statusCode + " - Resposta vazia");
            }
        } catch (IOException e) {
            throw new ApiException("Erro ao processar resposta: " + e.getMessage(), e);
        }
    }
    
    /**
     * Deserialização direta sem ApiResponse wrapper
     */
    private <T> T deserializeDirect(String responseBody, Class<T> responseType) throws ApiException {
        try {
            if (responseType == String.class) {
                return responseType.cast(responseBody);
            } else if (responseType == Boolean.class) {
                if ("true".equalsIgnoreCase(responseBody.trim()) || "false".equalsIgnoreCase(responseBody.trim())) {
                    return responseType.cast(Boolean.valueOf(responseBody.trim()));
                }
                return responseType.cast(Boolean.TRUE);
            } else if (responseType == Integer.class) {
                try {
                    return responseType.cast(Integer.valueOf(responseBody.trim()));
                } catch (NumberFormatException e) {
                    throw new ApiException("Não foi possível converter resposta para Integer: " + responseBody);
                }
            }
            
            return objectMapper.readValue(responseBody, responseType);
            
        } catch (IOException e) {
            throw new ApiException("Erro na deserialização direta: " + e.getMessage(), e);
        }
    }
    
    /**
     * Processa a resposta HTTP para uma lista
     * Agora suporta respostas paginadas e ApiResponse
     */
    private <T> List<T> processListResponse(HttpResponse response, TypeReference<List<T>> typeReference) throws ApiException {
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();

            LOGGER.fine("List response status: " + statusCode);

            if (entity != null) {
                String responseBody = EntityUtils.toString(entity, "UTF-8");
                LOGGER.fine("Response body length: " + responseBody.length());

                if (statusCode >= 200 && statusCode < 300) {
                    if (responseBody.trim().isEmpty() || "[]".equals(responseBody.trim())) {
                        return new ArrayList<>();
                    }

                    try {
                        JsonNode rootNode = objectMapper.readTree(responseBody);

                        // Verifica se é uma ApiResponse
                        if (rootNode.has("success") && rootNode.has("data")) {
                            boolean success = rootNode.get("success").asBoolean();
                            
                            if (!success) {
                                String error = rootNode.has("error") ? rootNode.get("error").asText() : "Erro desconhecido";
                                throw new ApiException("API retornou erro: " + error);
                            }
                            
                            JsonNode dataNode = rootNode.get("data");
                            
                            if (dataNode.isNull()) {
                                return new ArrayList<>();
                            }
                            
                            return extractListFromDataNode(dataNode, typeReference);
                        }
                        // Verifica se é uma resposta paginada (Spring Boot Page)
                        else if (rootNode.has("content")) {
                            LOGGER.fine("Detectada resposta paginada (Spring Boot Page)");
                            JsonNode contentNode = rootNode.get("content");
                            return objectMapper.readValue(contentNode.toString(), typeReference);
                        }
                        // Verifica se é um array direto
                        else if (rootNode.isArray()) {
                            LOGGER.fine("Detectado array direto");
                            return objectMapper.readValue(responseBody, typeReference);
                        }
                        // Formato desconhecido
                        else {
                            LOGGER.warning("Formato de resposta desconhecido, tentando como array: " + responseBody.substring(0, Math.min(200, responseBody.length())));
                            return objectMapper.readValue(responseBody, typeReference);
                        }
                        
                    } catch (Exception parseException) {
                        LOGGER.log(Level.WARNING, "Erro ao parsear resposta JSON: " + parseException.getMessage());
                        return new ArrayList<>();
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Erro HTTP {0}: {1}", new Object[]{statusCode, responseBody});
                    
                    // Tenta extrair mensagem de erro da ApiResponse
                    try {
                        JsonNode rootNode = objectMapper.readTree(responseBody);
                        if (rootNode.has("error")) {
                            throw new ApiException("Erro HTTP " + statusCode + ": " + rootNode.get("error").asText());
                        }
                    } catch (Exception e) {
                        // Se não conseguir parsear, usa a resposta raw
                    }
                    
                    throw new ApiException("Erro HTTP " + statusCode + ": " + responseBody);
                }
            } else {
                if (statusCode >= 200 && statusCode < 300) {
                    return new ArrayList<>();
                }
                throw new ApiException("Erro HTTP " + statusCode + " - Resposta vazia");
            }
        } catch (IOException e) {
            throw new ApiException("Erro ao processar resposta da lista: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extrai lista do nó de dados da ApiResponse
     */
    private <T> List<T> extractListFromDataNode(JsonNode dataNode, TypeReference<List<T>> typeReference) throws ApiException {
        try {
            if (dataNode.isArray()) {
                // Data é um array direto
                return objectMapper.readValue(dataNode.toString(), typeReference);
            } else if (dataNode.has("content")) {
                // Data é um objeto paginado
                JsonNode contentNode = dataNode.get("content");
                return objectMapper.readValue(contentNode.toString(), typeReference);
            } else {
                // Data é um objeto único, converte para lista
                LOGGER.fine("Convertendo objeto único em lista");
                String singleItemJson = "[" + dataNode.toString() + "]";
                return objectMapper.readValue(singleItemJson, typeReference);
            }
        } catch (IOException e) {
            throw new ApiException("Erro ao extrair lista do nó de dados: " + e.getMessage(), e);
        }
    }
    
    /**
     * Fecha o cliente HTTP
     */
    public void close() {
        try {
            httpClient.close();
            LOGGER.info("ApiClient fechado");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Erro ao fechar cliente HTTP", e);
        }
    }
    
    /**
     * Verifica se a API está disponível
     * Tenta múltiplos endpoints de health check
     */
    public boolean isApiAvailable() {
        // Primeiro tenta o endpoint personalizado (mais provável de funcionar)
        if (testEndpoint("/health")) {
            LOGGER.info("API disponível via endpoint: /health");
            return true;
        }

        // Depois tenta o endpoint de health do Actuator
        if (testEndpoint("/actuator/health")) {
            LOGGER.info("API disponível via endpoint: /actuator/health");
            return true;
        }

        // Como último recurso, testa um endpoint específico da aplicação
        if (testEndpoint("/pacientes/count")) {
            LOGGER.info("API disponível via endpoint: /pacientes/count");
            return true;
        }

        LOGGER.warning("API não está disponível - nenhum endpoint respondeu");
        return false;
    }
    
    /**
     * Testa um endpoint específico
     */
    private boolean testEndpoint(String endpoint) {
        HttpGet request = new HttpGet(baseUrl + endpoint);
        request.setHeader("Accept", "application/json");
        
        try {
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            
            // Consome a resposta
            if (response.getEntity() != null) {
                EntityUtils.consume(response.getEntity());
            }
            
            // Aceita qualquer código de sucesso (2xx)
            boolean available = (statusCode >= 200 && statusCode < 300);
            
            if (available) {
                LOGGER.fine("Endpoint " + endpoint + " respondeu com status: " + statusCode);
            } else {
                LOGGER.fine("Endpoint " + endpoint + " respondeu com erro: " + statusCode);
            }
            
            return available;
            
        } catch (Exception e) {
            LOGGER.fine("Endpoint " + endpoint + " não respondeu: " + e.getMessage());
            return false;
        }
    }
}