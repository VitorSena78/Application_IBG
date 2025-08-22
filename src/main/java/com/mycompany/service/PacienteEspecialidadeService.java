package com.mycompany.service;

import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.dto.PacienteEspecialidadeDTO;
import com.mycompany.client.mapper.DtoMapper;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JOptionPane;

//Service para operações com PacienteEspecialidade através da API
public class PacienteEspecialidadeService {
    
    private static final Logger LOGGER = Logger.getLogger(PacienteEspecialidadeService.class.getName());
    private final ApiClient apiClient;
    private final String PACIENTE_ESPECIALIDADE_ENDPOINT = "/paciente_has_especialidade";
    
    public PacienteEspecialidadeService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    // Insere uma nova associação pacienteEspecialidade
    public boolean inserir(PacienteEspecialidade pacienteEspecialidade) {
        if (pacienteEspecialidade == null || 
            pacienteEspecialidade.getPacienteId() == null || 
            pacienteEspecialidade.getEspecialidadeId() == null) {
            LOGGER.warning("Tentativa de inserir associação inválida");
            return false;
        }
        
        try {
            // Converte o modelo para DTO antes de enviar
            PacienteEspecialidadeDTO pacienteEspecialidadeDto = DtoMapper.toDto(pacienteEspecialidade);
            
            // Envia o DTO para a API
            PacienteEspecialidadeDTO resultado = apiClient.post(PACIENTE_ESPECIALIDADE_ENDPOINT, pacienteEspecialidadeDto, PacienteEspecialidadeDTO.class);
            
            if (resultado != null) {
                LOGGER.info("Associação inserida com sucesso: Paciente " + pacienteEspecialidade.getPacienteId() + 
                           " - Especialidade " + pacienteEspecialidade.getEspecialidadeId());
                return true;
            }
            return false;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir associação", e);
            return false;
        }
    }
    
    //Insere uma lista de associações PacienteEspecialidade
    public boolean inserirLista(List<PacienteEspecialidade> listaPacienteEspecialidade) {
        if (listaPacienteEspecialidade == null || listaPacienteEspecialidade.isEmpty()) {
            LOGGER.info("Lista vazia ou nula. Nenhuma associação para inserir.");
            return false;
        }

        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/batch";
            
            // Converte a lista de modelos para DTOs antes de enviar
            List<PacienteEspecialidadeDTO> dtoList = DtoMapper.toPacienteEspecialidadeDtoList(listaPacienteEspecialidade);
            
            // Envia os DTOs para a API
            List<PacienteEspecialidadeDTO> resultadoDtos = apiClient.postList(endpoint, dtoList, 
                                                                               new TypeReference<List<PacienteEspecialidadeDTO>>(){});

            if (resultadoDtos != null && !resultadoDtos.isEmpty()) {
                LOGGER.info("Lista de associações inserida com sucesso: " + resultadoDtos.size() + " itens");
                return true;
            } else {
                LOGGER.info("Nenhuma nova associação foi inserida.");
                return false;
            }

        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir lista de associações", e);
            return false;
        }
    }
    
    // Lista todas as associações
    public List<PacienteEspecialidade> listarTodos() {
        try {
            // Recebe DTOs da API
            List<PacienteEspecialidadeDTO> associacaoDtos = apiClient.getList(PACIENTE_ESPECIALIDADE_ENDPOINT, 
                                                                               new TypeReference<List<PacienteEspecialidadeDTO>>(){});
            
            // Converte DTOs para modelos de domínio
            List<PacienteEspecialidade> associacoes = DtoMapper.toPacienteEspecialidadeModelList(associacaoDtos);
            
            LOGGER.info("Listadas " + associacoes.size() + " associações");
            return associacoes;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar associações", e);
            return new java.util.ArrayList<>(); // Retorna lista vazia mutável
        }
    }
    
    //Busca todas as especialidades de um paciente
    public List<PacienteEspecialidade> buscarPorPacienteId(int pacienteId) {
        if (pacienteId <= 0) {
            LOGGER.warning("ID de paciente inválido: " + pacienteId);
            return new java.util.ArrayList<>();
        }
        
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/paciente/" + pacienteId;
            
            // Recebe DTOs da API
            List<PacienteEspecialidadeDTO> associacaoDtos = apiClient.getList(endpoint, 
                                                                               new TypeReference<List<PacienteEspecialidadeDTO>>(){});
            
            // Converte DTOs para modelos de domínio
            List<PacienteEspecialidade> associacoes = DtoMapper.toPacienteEspecialidadeModelList(associacaoDtos);
            
            LOGGER.info("Encontradas " + associacoes.size() + " especialidades para paciente ID: " + pacienteId);
            return associacoes;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar associações por paciente ID: " + pacienteId, e);
            return new java.util.ArrayList<>();
        }
    }
    
    //Atualiza a data de atendimento de uma associação
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
            
            // Converte o modelo para DTO antes de enviar
            PacienteEspecialidadeDTO pacienteEspecialidadeDto = DtoMapper.toDto(pacienteEspecialidade);
            
            // Envia o DTO para a API
            PacienteEspecialidadeDTO resultado = apiClient.put(endpoint, pacienteEspecialidadeDto, PacienteEspecialidadeDTO.class);
            
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
    
    //Remove uma associação específica
    public boolean deletar(int pacienteId, int especialidadeId) {
        if (pacienteId <= 0 || especialidadeId <= 0) {
            LOGGER.warning("IDs inválidos para exclusão: pacienteId=" + pacienteId + ", especialidadeId=" + especialidadeId);
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
    
    // Remove todas as associações de um paciente
    public boolean deletarPorPacienteId(int pacienteId) {
        if (pacienteId <= 0) {
            LOGGER.warning("ID inválido para exclusão: " + pacienteId);
            return false;
        }
        
        try {
            String endpoint = PACIENTE_ESPECIALIDADE_ENDPOINT + "/paciente/" + pacienteId;
            boolean sucesso = apiClient.delete(endpoint);
            
            if (sucesso) {
                LOGGER.info("Todas as associações do paciente deletadas: ID " + pacienteId);
                return true;
            } else {
                LOGGER.warning("Nenhuma associação encontrada para deletar do paciente ID: " + pacienteId);
                return false; // Pode não ser erro se não houver associações
            }
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar associações do paciente: ID " + pacienteId, e);
            return false;
        }
    }
    
    //Conta o total de associações
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
}