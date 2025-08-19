package com.mycompany.service;

import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.model.bean.Especialidade;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service para operações com Especialidade através da API
 * Substitui o EspecialidadeDAO
 */
public class EspecialidadeService {
    
    private static final Logger LOGGER = Logger.getLogger(EspecialidadeService.class.getName());
    private final ApiClient apiClient;
    private final String ESPECIALIDADE_ENDPOINT = "/especialidades";
    
    public EspecialidadeService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    /**
     * Insere uma nova especialidade
     */
    public boolean inserir(Especialidade especialidade) {
        if (especialidade == null || especialidade.getNome() == null || especialidade.getNome().trim().isEmpty()) {
            LOGGER.warning("Tentativa de inserir especialidade inválida");
            return false;
        }
        
        try {
            Especialidade especialidadeRetornada = apiClient.post(ESPECIALIDADE_ENDPOINT, especialidade, Especialidade.class);
            
            if (especialidadeRetornada != null && especialidadeRetornada.getId() != null) {
                // Atualiza o ID no objeto original
                especialidade.setId(especialidadeRetornada.getId());
                LOGGER.info("Especialidade inserida com sucesso: " + especialidade.getNome());
                return true;
            }
            return false;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir especialidade: " + especialidade.getNome(), e);
            return false;
        }
    }
    
    /**
     * Lista todas as especialidades cadastradas
     */
    public List<Especialidade> listarTodas() {
        try {
            List<Especialidade> especialidades = apiClient.getList(ESPECIALIDADE_ENDPOINT, new TypeReference<List<Especialidade>>(){});
            LOGGER.info("Listadas " + especialidades.size() + " especialidades");
            return especialidades;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar especialidades", e);
            return List.of(); // Retorna lista vazia
        }
    }
    
    /**
     * Busca uma especialidade pelo ID
     */
    public Especialidade buscarPorId(int id) {
        if (id <= 0) {
            LOGGER.warning("ID inválido para busca: " + id);
            return null;
        }
        
        try {
            Especialidade especialidade = apiClient.get(ESPECIALIDADE_ENDPOINT + "/" + id, Especialidade.class);
            
            if (especialidade != null) {
                LOGGER.info("Especialidade encontrada: ID " + id);
            } else {
                LOGGER.info("Especialidade não encontrada: ID " + id);
            }
            
            return especialidade;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar especialidade por ID: " + id, e);
            return null;
        }
    }
    
    /**
     * Atualiza uma especialidade existente
     */
    public boolean atualizar(Especialidade especialidade) {
        if (especialidade == null || especialidade.getId() == null || especialidade.getId() <= 0 || 
            especialidade.getNome() == null || especialidade.getNome().trim().isEmpty()) {
            LOGGER.warning("Dados inválidos para atualização de especialidade");
            return false;
        }
        
        try {
            String endpoint = ESPECIALIDADE_ENDPOINT + "/" + especialidade.getId();
            Especialidade especialidadeAtualizada = apiClient.put(endpoint, especialidade, Especialidade.class);
            
            if (especialidadeAtualizada != null) {
                LOGGER.info("Especialidade atualizada: ID " + especialidade.getId());
                return true;
            } else {
                LOGGER.warning("Nenhuma especialidade encontrada para atualizar: ID " + especialidade.getId());
                return false;
            }
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar especialidade: ID " + especialidade.getId(), e);
            return false;
        }
    }
    
    /**
     * Reduz em 1 o número de atendimentos restantes da especialidade
     */
    public boolean reduzirAtendimentoRestante(int especialidadeId) {
        if (especialidadeId <= 0) {
            LOGGER.warning("ID inválido para redução de atendimento: " + especialidadeId);
            return false;
        }
        
        try {
            String endpoint = ESPECIALIDADE_ENDPOINT + "/" + especialidadeId + "/reduzir-atendimento";
            Boolean sucesso = apiClient.put(endpoint, null, Boolean.class);
            
            if (sucesso != null && sucesso) {
                LOGGER.info("Atendimento reduzido para especialidade ID: " + especialidadeId);
                return true;
            } else {
                LOGGER.warning("Não foi possível reduzir atendimento - sem atendimentos restantes ou especialidade não encontrada: ID " + especialidadeId);
                return false;
            }
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao reduzir atendimento da especialidade: ID " + especialidadeId, e);
            return false;
        }
    }
    
    /**
     * Verifica se a especialidade tem atendimentos disponíveis
     */
    public boolean temAtendimentosDisponiveis(int especialidadeId) {
        if (especialidadeId <= 0) {
            return false;
        }
        
        try {
            String endpoint = ESPECIALIDADE_ENDPOINT + "/" + especialidadeId + "/tem-atendimentos";
            Boolean temAtendimentos = apiClient.get(endpoint, Boolean.class);
            return temAtendimentos != null && temAtendimentos;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar atendimentos disponíveis: ID " + especialidadeId, e);
            return false;
        }
    }
    
    /**
     * Obtém a numeração do próximo atendimento para uma especialidade
     */
    public String obterNumeracaoProximoAtendimento(int especialidadeId) {
        if (especialidadeId <= 0) {
            return null;
        }
        
        try {
            String endpoint = ESPECIALIDADE_ENDPOINT + "/" + especialidadeId + "/proximo-numero";
            String numeracao = apiClient.get(endpoint, String.class);
            return numeracao;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao obter numeração do atendimento: ID " + especialidadeId, e);
            return null;
        }
    }
    
    /**
     * Deleta uma especialidade pelo ID
     */
    public boolean deletar(int id) {
        if (id <= 0) {
            LOGGER.warning("ID inválido para exclusão: " + id);
            return false;
        }
        
        try {
            String endpoint = ESPECIALIDADE_ENDPOINT + "/" + id;
            boolean sucesso = apiClient.delete(endpoint);
            
            if (sucesso) {
                LOGGER.info("Especialidade deletada: ID " + id);
                return true;
            } else {
                LOGGER.warning("Nenhuma especialidade encontrada para deletar: ID " + id);
                return false;
            }
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar especialidade: ID " + id, e);
            return false;
        }
    }
    
    /**
     * Verifica se existe uma especialidade com o nome informado
     */
    public boolean existePorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        
        try {
            String endpoint = ESPECIALIDADE_ENDPOINT + "/existe/nome/" + nome.trim();
            Boolean existe = apiClient.get(endpoint, Boolean.class);
            return existe != null && existe;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar existência da especialidade: " + nome, e);
            return false;
        }
    }
    
    /**
     * Lista especialidades que têm atendimentos disponíveis
     */
    public List<Especialidade> listarComAtendimentosDisponiveis() {
        try {
            String endpoint = ESPECIALIDADE_ENDPOINT + "/com-atendimentos";
            List<Especialidade> especialidades = apiClient.getList(endpoint, new TypeReference<List<Especialidade>>(){});
            LOGGER.info("Listadas " + especialidades.size() + " especialidades com atendimentos disponíveis");
            return especialidades;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar especialidades com atendimentos", e);
            return List.of();
        }
    }
    
    /**
     * Reinicia os contadores diários de uma especialidade
     */
    public boolean reiniciarContadoresDiarios(int especialidadeId) {
        if (especialidadeId <= 0) {
            return false;
        }
        
        try {
            String endpoint = ESPECIALIDADE_ENDPOINT + "/" + especialidadeId + "/reiniciar-contadores";
            Boolean sucesso = apiClient.put(endpoint, null, Boolean.class);
            
            if (sucesso != null && sucesso) {
                LOGGER.info("Contadores reiniciados para especialidade ID: " + especialidadeId);
                return true;
            }
            return false;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao reiniciar contadores da especialidade: ID " + especialidadeId, e);
            return false;
        }
    }
    
    /**
     * Verifica se o serviço de API está disponível
     */
    public boolean isServicoDisponivel() {
        return apiClient.isApiAvailable();
    }
}