package com.mycompany.service;

import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * Service para operações com PacienteEspecialidade através da API
 * Substitui o PacienteEspecialidadeDAO
 */
public class PacienteEspecialidadeService {
    
    private static final Logger LOGGER = Logger.getLogger(PacienteEspecialidadeService.class.getName());
    private final ApiClient apiClient;
    private final String PACIENTE_ESPECIALIDADE_ENDPOINT = "/paciente_has_especialidade";
    
    public PacienteEspecialidadeService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    /**
     * Insere uma nova associação pacienteEspecialidade
     */
    public boolean inserir(PacienteEspecialidade pacienteEspecialidade) {
        if (pacienteEspecialidade == null || 
            pacienteEspecialidade.getPacienteId() == null || 
            pacienteEspecialidade.getEspecialidadeId() == null) {
            LOGGER.warning("Tentativa de inserir associação inválida");
            return false;
        }
        
        try {
            PacienteEspecialidade resultado = apiClient.post(PACIENTE_ESPECIALIDADE_ENDPOINT, pacienteEspecialidade, PacienteEspecialidade.class);
            
            if (resultado != null) {
                LOGGER.info("Associação inserida com sucesso: Paciente " + pacienteEspecialidade.getPacienteId() + 
                           " - Especialidade " + pacienteEspecialidade.getEspecialidadeId());
                JOptionPane.showMessageDialog(null, "Associação salva com sucesso");
                return true;
            }
            return false;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir associação", e);
            JOptionPane.showMessageDialog(null, "Erro ao salvar associação: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Insere uma lista de associações PacienteEspecialidade
     */
    public boolean inserirLista(List<PacienteEspecialidade> listaPacienteEspecialidade) {
        if (listaPacienteEspecialidade == null || listaPacienteEspecialidade.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Lista vazia ou nula. Nenhuma associação para inserir.");
            return false;
        }

        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/batch";
            // CORREÇÃO: Usar o novo método postList do ApiClient
            List<PacienteEspecialidade> resultado = apiClient.postList(endpoint, listaPacienteEspecialidade, 
                                                                       new TypeReference<List<PacienteEspecialidade>>(){});

            if (resultado != null && !resultado.isEmpty()) {
                LOGGER.info("Lista de associações inserida com sucesso: " + resultado.size() + " itens");
                JOptionPane.showMessageDialog(null, 
                    "Inserção em lote concluída!\n✓ " + resultado.size() + " associações inseridas com sucesso");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Nenhuma nova associação foi inserida.");
                return false;
            }

        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir lista de associações", e);
            JOptionPane.showMessageDialog(null, "Erro ao inserir lista de associações: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lista todas as associações
     */
    public List<PacienteEspecialidade> listarTodos() {
        try {
            List<PacienteEspecialidade> associacoes = apiClient.getList(PACIENTE_ESPECIALIDADE_ENDPOINT, 
                                                                       new TypeReference<List<PacienteEspecialidade>>(){});
            LOGGER.info("Listadas " + associacoes.size() + " associações");
            return associacoes;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar associações", e);
            return List.of();
        }
    }
    
    /**
     * Busca todas as especialidades de um paciente
     */
    public List<PacienteEspecialidade> buscarPorPacienteId(int pacienteId) {
        if (pacienteId <= 0) {
            return List.of();
        }
        
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/paciente/" + pacienteId;
            List<PacienteEspecialidade> associacoes = apiClient.getList(endpoint, 
                                                                       new TypeReference<List<PacienteEspecialidade>>(){});
            LOGGER.info("Encontradas " + associacoes.size() + " especialidades para paciente ID: " + pacienteId);
            return associacoes;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar associações por paciente ID: " + pacienteId, e);
            return List.of();
        }
    }
    
    /**
     * Busca todos os pacientes de uma especialidade
     */
    public List<PacienteEspecialidade> buscarPorEspecialidadeId(int especialidadeId) {
        if (especialidadeId <= 0) {
            return List.of();
        }
        
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/especialidade/" + especialidadeId;
            List<PacienteEspecialidade> associacoes = apiClient.getList(endpoint, 
                                                                       new TypeReference<List<PacienteEspecialidade>>(){});
            LOGGER.info("Encontrados " + associacoes.size() + " pacientes para especialidade ID: " + especialidadeId);
            return associacoes;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar associações por especialidade ID: " + especialidadeId, e);
            return List.of();
        }
    }
    
    /**
     * Busca uma associação específica por paciente e especialidade
     */
    public PacienteEspecialidade buscarPorPacienteEEspecialidade(int pacienteId, int especialidadeId) {
        if (pacienteId <= 0 || especialidadeId <= 0) {
            return null;
        }
        
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/paciente/" + pacienteId + "/especialidade/" + especialidadeId;
            PacienteEspecialidade associacao = apiClient.get(endpoint, PacienteEspecialidade.class);
            
            if (associacao != null) {
                LOGGER.info("Associação encontrada: Paciente " + pacienteId + " - Especialidade " + especialidadeId);
            } else {
                LOGGER.info("Associação não encontrada: Paciente " + pacienteId + " - Especialidade " + especialidadeId);
            }
            
            return associacao;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar associação específica", e);
            return null;
        }
    }
    
    /**
     * Atualiza a data de atendimento de uma associação
     */
    public boolean atualizar(PacienteEspecialidade pacienteEspecialidade) {
        if (pacienteEspecialidade == null || 
            pacienteEspecialidade.getPacienteId() == null || 
            pacienteEspecialidade.getEspecialidadeId() == null) {
            LOGGER.warning("Dados inválidos para atualização de associação");
            return false;
        }
        
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/paciente/" + pacienteEspecialidade.getPacienteId() + 
                             "/especialidade/" + pacienteEspecialidade.getEspecialidadeId();
            PacienteEspecialidade resultado = apiClient.put(endpoint, pacienteEspecialidade, PacienteEspecialidade.class);
            
            if (resultado != null) {
                LOGGER.info("Associação atualizada: Paciente " + pacienteEspecialidade.getPacienteId() + 
                           " - Especialidade " + pacienteEspecialidade.getEspecialidadeId());
                return true;
            }
            return false;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar associação", e);
            return false;
        }
    }
    
    /**
     * Remove uma associação específica
     */
    public boolean deletar(int pacienteId, int especialidadeId) {
        if (pacienteId <= 0 || especialidadeId <= 0) {
            LOGGER.warning("IDs inválidos para exclusão");
            return false;
        }
        
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/paciente/" + pacienteId + "/especialidade/" + especialidadeId;
            boolean sucesso = apiClient.delete(endpoint);
            
            if (sucesso) {
                LOGGER.info("Associação deletada: Paciente " + pacienteId + " - Especialidade " + especialidadeId);
                return true;
            } else {
                LOGGER.warning("Associação não encontrada para deletar: Paciente " + pacienteId + " - Especialidade " + especialidadeId);
                return false;
            }
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar associação", e);
            return false;
        }
    }
    
    /**
     * Remove todas as associações de um paciente
     */
    public boolean deletarPorPacienteId(int pacienteId) {
        if (pacienteId <= 0) {
            LOGGER.warning("ID inválido para exclusão");
            return false;
        }
        
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/paciente/" + pacienteId;
            boolean sucesso = apiClient.delete(endpoint);
            
            if (sucesso) {
                LOGGER.info("Todas as associações do paciente deletadas: ID " + pacienteId);
                return true;
            }
            return false;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar associações do paciente: ID " + pacienteId, e);
            return false;
        }
    }
    
    /**
     * Remove todas as associações de uma especialidade
     */
    public boolean deletarPorEspecialidadeId(int especialidadeId) {
        if (especialidadeId <= 0) {
            LOGGER.warning("ID inválido para exclusão");
            return false;
        }
        
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/especialidade/" + especialidadeId;
            boolean sucesso = apiClient.delete(endpoint);
            
            if (sucesso) {
                LOGGER.info("Todas as associações da especialidade deletadas: ID " + especialidadeId);
                return true;
            }
            return false;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar associações da especialidade: ID " + especialidadeId, e);
            return false;
        }
    }
    
    /**
     * Verifica se uma associação já existe
     */
    public boolean existeAssociacao(int pacienteId, int especialidadeId) {
        if (pacienteId <= 0 || especialidadeId <= 0) {
            return false;
        }
        
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/existe/paciente/" + pacienteId + "/especialidade/" + especialidadeId;
            Boolean existe = apiClient.get(endpoint, Boolean.class);
            return existe != null && existe;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar existência da associação", e);
            return false;
        }
    }
    
    /**
     * Busca associações por data de atendimento
     */
    public List<PacienteEspecialidade> buscarPorDataAtendimento(String dataAtendimento) {
        if (dataAtendimento == null || dataAtendimento.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/data/" + dataAtendimento.trim();
            List<PacienteEspecialidade> associacoes = apiClient.getList(endpoint, 
                                                                       new TypeReference<List<PacienteEspecialidade>>(){});
            LOGGER.info("Encontradas " + associacoes.size() + " associações para a data: " + dataAtendimento);
            return associacoes;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar associações por data: " + dataAtendimento, e);
            return List.of();
        }
    }
    
    /**
     * Conta o total de associações
     */
    public int contarTotal() {
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/count";
            Integer total = apiClient.get(endpoint, Integer.class);
            return total != null ? total : 0;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar associações", e);
            return 0;
        }
    }
    
    /**
     * Verifica se o serviço de API está disponível
     */
    public boolean isServicoDisponivel() {
        return apiClient.isApiAvailable();
    }
}