package com.mycompany.service;

import com.mycompany.client.ApiException;
import com.mycompany.client.dto.PacienteDTO;
import com.mycompany.client.mapper.DtoMapper;
import com.mycompany.model.bean.Paciente;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mycompany.client.ApiClient;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * Service para operações com Paciente através da API
 * Versão adaptada para trabalhar com DTOs
 */
public class PacienteService {
    
    private static final Logger LOGGER = Logger.getLogger(PacienteService.class.getName());
    private final ApiClient apiClient;
    private final String PACIENTE_ENDPOINT = "/pacientes";
    
    public PacienteService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    /**
     * Insere um novo paciente
     */
    public boolean inserir(Paciente paciente) {
        if (paciente == null || paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            LOGGER.warning("Tentativa de inserir paciente inválido");
            return false;
        }
        
        try {
            // Converte o modelo para DTO antes de enviar
            PacienteDTO pacienteDto = DtoMapper.toDto(paciente);
            
            // Envia o DTO para a API
            PacienteDTO pacienteDtoRetornado = apiClient.post(PACIENTE_ENDPOINT, pacienteDto, PacienteDTO.class);
            
            if (pacienteDtoRetornado != null && pacienteDtoRetornado.getId() != null) {
                // Atualiza o ID no objeto original
                paciente.setId(pacienteDtoRetornado.getId());
                JOptionPane.showMessageDialog(null, "Paciente salvo com sucesso");
                LOGGER.info("Paciente inserido com sucesso: " + paciente.getNome());
                return true;
            }
            return false;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir paciente: " + paciente.getNome(), e);
            JOptionPane.showMessageDialog(null, "Erro ao salvar paciente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lista todos os pacientes
     */
    public List<Paciente> listarTodos() {
        try {
            String endpoint = PACIENTE_ENDPOINT + "/todos";
            
            // Recebe DTOs da API
            List<PacienteDTO> pacienteDtos = apiClient.getList(endpoint, new TypeReference<List<PacienteDTO>>(){});
            
            // Converte DTOs para modelos de domínio
            List<Paciente> pacientes = DtoMapper.toModelList(pacienteDtos);
            
            LOGGER.info("Listados " + pacientes.size() + " pacientes");
            return pacientes;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar pacientes", e);
            JOptionPane.showMessageDialog(null, "Erro ao buscar pacientes: " + e.getMessage());
            return List.of(); // Retorna lista vazia
        }
    }
    
    /**
     * Busca um paciente pelo ID
     */
    public Paciente buscarPorId(int id) {
        if (id <= 0) {
            LOGGER.warning("ID inválido para busca: " + id);
            return null;
        }
        
        try {
            // Recebe DTO da API
            PacienteDTO pacienteDto = apiClient.get(PACIENTE_ENDPOINT + "/" + id, PacienteDTO.class);
            
            if (pacienteDto != null) {
                LOGGER.info("Paciente encontrado: ID " + id);
                // Converte DTO para modelo de domínio
                return DtoMapper.toModel(pacienteDto);
            } else {
                LOGGER.info("Paciente não encontrado: ID " + id);
                return null;
            }
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar paciente por ID: " + id, e);
            return null;
        }
    }
    
    /**
     * Busca pacientes por nome (busca parcial)
     */
    public List<Paciente> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            String endpoint = PACIENTE_ENDPOINT + "/buscar?nome=" + nome.trim();
            
            // Recebe DTOs da API
            List<PacienteDTO> pacienteDtos = apiClient.getList(endpoint, new TypeReference<List<PacienteDTO>>(){});
            
            // Converte DTOs para modelos de domínio
            List<Paciente> pacientes = DtoMapper.toModelList(pacienteDtos);
            
            LOGGER.info("Encontrados " + pacientes.size() + " pacientes com nome contendo: " + nome);
            return pacientes;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar pacientes por nome: " + nome, e);
            return List.of();
        }
    }
    
    /**
     * Busca paciente por CPF
     */
    public Paciente buscarPorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return null;
        }
        
        try {
            String endpoint = PACIENTE_ENDPOINT + "/cpf/" + cpf.trim();
            
            // Recebe DTO da API
            PacienteDTO pacienteDto = apiClient.get(endpoint, PacienteDTO.class);
            
            if (pacienteDto != null) {
                // Converte DTO para modelo de domínio
                return DtoMapper.toModel(pacienteDto);
            }
            return null;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar paciente por CPF: " + cpf, e);
            return null;
        }
    }
    
    /**
     * Atualiza um paciente existente
     */
    public boolean atualizar(Paciente paciente) {
        if (paciente == null || paciente.getId() == null || paciente.getId() <= 0 || 
            paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            LOGGER.warning("Dados inválidos para atualização de paciente");
            return false;
        }

        try {
            String endpoint = PACIENTE_ENDPOINT + "/" + paciente.getId();
            
            // Converte o modelo para DTO antes de enviar
            PacienteDTO pacienteDto = DtoMapper.toDto(paciente);
            
            // Envia o DTO para a API
            PacienteDTO pacienteDtoAtualizado = apiClient.put(endpoint, pacienteDto, PacienteDTO.class);

            if (pacienteDtoAtualizado != null) {
                LOGGER.info("Paciente atualizado: ID " + paciente.getId());
                JOptionPane.showMessageDialog(null, "Paciente atualizado com sucesso");
                
                // Converte o DTO retornado para modelo e copia os dados atualizados
                Paciente pacienteAtualizado = DtoMapper.toModel(pacienteDtoAtualizado);
                if (pacienteAtualizado != null) {
                    copiarDados(pacienteAtualizado, paciente);
                }
                return true;
            } else {
                LOGGER.warning("Nenhum paciente encontrado para atualizar: ID " + paciente.getId());
                return false;
            }

        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar paciente: ID " + paciente.getId(), e);
            JOptionPane.showMessageDialog(null, "Erro ao atualizar paciente: " + e.getMessage());
            return false;
        }
    }
    
    // Método auxiliar para copiar dados
    private void copiarDados(Paciente origem, Paciente destino) {
        destino.setId(origem.getId());
        destino.setNome(origem.getNome());
        destino.setCpf(origem.getCpf());
        destino.setDataNascimento(origem.getDataNascimento());
        destino.setIdade(origem.getIdade());
        destino.setNomeDaMae(origem.getNomeDaMae());
        destino.setSus(origem.getSus());
        destino.setTelefone(origem.getTelefone());
        destino.setEndereco(origem.getEndereco());
        destino.setPaXMmhg(origem.getPaXMmhg());
        destino.setFcBpm(origem.getFcBpm());
        destino.setFrIbpm(origem.getFrIbpm());
        destino.setTemperaturaC(origem.getTemperaturaC());
        destino.setHgtMgld(origem.getHgtMgld());
        destino.setSpo2(origem.getSpo2());
        destino.setPeso(origem.getPeso());
        destino.setAltura(origem.getAltura());
        destino.setImc(origem.getImc());
    }
    
    /**
     * Deleta um paciente pelo ID
     */
    public boolean deletar(int id) {
        if (id <= 0) {
            LOGGER.warning("ID inválido para exclusão: " + id);
            return false;
        }
        
        try {
            String endpoint = PACIENTE_ENDPOINT + "/" + id;
            boolean sucesso = apiClient.delete(endpoint);
            
            if (sucesso) {
                LOGGER.info("Paciente deletado: ID " + id);
                JOptionPane.showMessageDialog(null, "Paciente excluído com sucesso");
                return true;
            } else {
                LOGGER.warning("Nenhum paciente encontrado para deletar: ID " + id);
                return false;
            }
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar paciente: ID " + id, e);
            JOptionPane.showMessageDialog(null, "Erro ao excluir paciente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica se existe um paciente com o CPF informado
     */
    public boolean existePorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        
        try {
            String endpoint = PACIENTE_ENDPOINT + "/existe/cpf/" + cpf.trim();
            Boolean existe = apiClient.get(endpoint, Boolean.class);
            return existe != null && existe;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar existência por CPF: " + cpf, e);
            return false;
        }
    }
    
    /**
     * Conta o total de pacientes cadastrados
     */
    public int contarTotal() {
        try {
            String endpoint = PACIENTE_ENDPOINT + "/count";
            Integer total = apiClient.get(endpoint, Integer.class);
            return total != null ? total : 0;
            
        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar pacientes", e);
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