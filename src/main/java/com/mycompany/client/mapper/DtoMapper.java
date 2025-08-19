package com.mycompany.client.mapper;

import com.mycompany.client.dto.PacienteDTO;
import com.mycompany.client.dto.EspecialidadeDTO;
import com.mycompany.client.dto.PacienteEspecialidadeDTO;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.PacienteEspecialidade;

import java.util.List;
import java.util.ArrayList;

/**
 * Classe utilitária para converter DTOs da API para modelos de domínio
 * Versão corrigida com tratamento adequado de tipos
 */
public class DtoMapper {
    
    /**
     * Converte PacienteDTO para Paciente
     */
    public static Paciente toModel(PacienteDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Paciente paciente = new Paciente();
        
        paciente.setId(dto.getId());
        paciente.setNome(dto.getNome());
        paciente.setDataNascimento(dto.getDataNascimento());
        paciente.setIdade(dto.getIdade());
        paciente.setNomeDaMae(dto.getNomeDaMae());
        paciente.setCpf(dto.getCpf());
        paciente.setSus(dto.getSus());
        paciente.setTelefone(dto.getTelefone());
        paciente.setEndereco(dto.getEndereco());
        paciente.setPaXmmhg(dto.getPaXmmhg());
        
        // Tratamento seguro para campos Float - corrigindo inconsistência de tipos
        paciente.setFcBpm(dto.getFcBpm() != null ? dto.getFcBpm() : 0.0f);
        paciente.setFrIbpm(dto.getFrIbpm() != null ? dto.getFrIbpm() : 0.0f);
        paciente.setTemperaturaC(dto.getTemperaturaC() != null ? dto.getTemperaturaC() : 0.0f);
        paciente.setHgtMgld(dto.getHgtMgld() != null ? dto.getHgtMgld() : 0.0f);
        paciente.setSpo2(dto.getSpo2() != null ? dto.getSpo2() : 0.0f);
        paciente.setPeso(dto.getPeso() != null ? dto.getPeso() : 0.0f);
        paciente.setAltura(dto.getAltura() != null ? dto.getAltura() : 0.0f);
        paciente.setImc(dto.getImc() != null ? dto.getImc() : 0.0f);
        
        return paciente;
    }
    
    /**
     * Converte lista de PacienteDTO para lista de Paciente
     */
    public static List<Paciente> toModelList(List<PacienteDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Paciente> pacientes = new ArrayList<>();
        for (PacienteDTO dto : dtoList) {
            Paciente paciente = toModel(dto);
            if (paciente != null) {
                pacientes.add(paciente);
            }
        }
        
        return pacientes;
    }
    
    /**
     * Converte EspecialidadeDTO para Especialidade
     */
    public static Especialidade toModel(EspecialidadeDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Especialidade especialidade = new Especialidade();
        
        especialidade.setId(dto.getId() != null ? dto.getId() : 0);
        especialidade.setNome(dto.getNome());
        
        // Mapeia com valores padrão se nulo - corrigindo inconsistência de tipos
        especialidade.setAtendimentosRestantesHoje(
            dto.getAtendimentosRestantesHoje() != null ? dto.getAtendimentosRestantesHoje() : 0
        );
        especialidade.setAtendimentosTotaisHoje(
            dto.getAtendimentosTotaisHoje() != null ? dto.getAtendimentosTotaisHoje() : 0
        );
        
        return especialidade;
    }
    
    /**
     * Converte lista de EspecialidadeDTO para lista de Especialidade
     */
    public static List<Especialidade> toEspecialidadeModelList(List<EspecialidadeDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Especialidade> especialidades = new ArrayList<>();
        for (EspecialidadeDTO dto : dtoList) {
            Especialidade especialidade = toModel(dto);
            if (especialidade != null) {
                especialidades.add(especialidade);
            }
        }
        
        return especialidades;
    }
    
    /**
     * Converte PacienteEspecialidadeDTO para PacienteEspecialidade
     */
    public static PacienteEspecialidade toModel(PacienteEspecialidadeDTO dto) {
        if (dto == null) {
            return null;
        }
        
        PacienteEspecialidade pacienteEspecialidade = new PacienteEspecialidade();
        
        pacienteEspecialidade.setPacienteId(dto.getPacienteId() != null ? dto.getPacienteId() : 0);
        pacienteEspecialidade.setEspecialidadeId(dto.getEspecialidadeId() != null ? dto.getEspecialidadeId() : 0);
        pacienteEspecialidade.setDataAtendimento(dto.getDataAtendimento());
        
        return pacienteEspecialidade;
    }
    
    /**
     * Converte lista de PacienteEspecialidadeDTO para lista de PacienteEspecialidade
     */
    public static List<PacienteEspecialidade> toPacienteEspecialidadeModelList(List<PacienteEspecialidadeDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<PacienteEspecialidade> associacoes = new ArrayList<>();
        for (PacienteEspecialidadeDTO dto : dtoList) {
            PacienteEspecialidade associacao = toModel(dto);
            if (associacao != null) {
                associacoes.add(associacao);
            }
        }
        
        return associacoes;
    }
    
    // ===== MÉTODOS PARA CONVERSÃO REVERSA (Model → DTO) =====
    
    /**
     * Converte Paciente para PacienteDTO (para envio à API)
     */
    public static PacienteDTO toDto(Paciente model) {
        if (model == null) {
            return null;
        }
        
        PacienteDTO dto = new PacienteDTO();
        
        dto.setId(model.getId());
        dto.setNome(model.getNome());
        dto.setDataNascimento(model.getDataNascimento());
        dto.setIdade(model.getIdade());
        dto.setNomeDaMae(model.getNomeDaMae());
        dto.setCpf(model.getCpf());
        dto.setSus(model.getSus());
        dto.setTelefone(model.getTelefone());
        dto.setEndereco(model.getEndereco());
        dto.setPaXmmhg(model.getPaXmmhg());
        dto.setFcBpm(model.getFcBpm());
        dto.setFrIbpm(model.getFrIbpm());
        dto.setTemperaturaC(model.getTemperaturaC());
        dto.setHgtMgld(model.getHgtMgld());
        dto.setSpo2(model.getSpo2());
        dto.setPeso(model.getPeso());
        dto.setAltura(model.getAltura());
        dto.setImc(model.getImc());
        
        return dto;
    }
    
    /**
     * Converte Especialidade para EspecialidadeDTO (para envio à API)
     */
    public static EspecialidadeDTO toDto(Especialidade model) {
        if (model == null) {
            return null;
        }
        
        EspecialidadeDTO dto = new EspecialidadeDTO();
        
        dto.setId(model.getId());
        dto.setNome(model.getNome());
        dto.setAtendimentosRestantesHoje(model.getAtendimentosRestantesHoje());
        dto.setAtendimentosTotaisHoje(model.getAtendimentosTotaisHoje());
        
        return dto;
    }
    
    /**
     * Converte PacienteEspecialidade para PacienteEspecialidadeDTO (para envio à API)
     */
    public static PacienteEspecialidadeDTO toDto(PacienteEspecialidade model) {
        if (model == null) {
            return null;
        }
        
        PacienteEspecialidadeDTO dto = new PacienteEspecialidadeDTO();
        
        dto.setPacienteId(model.getPacienteId());
        dto.setEspecialidadeId(model.getEspecialidadeId());
        dto.setDataAtendimento(model.getDataAtendimento());
        
        return dto;
    }
    
    /**
     * Converte lista de Paciente para lista de PacienteDTO
     */
    public static List<PacienteDTO> toPacienteDtoList(List<Paciente> modelList) {
        if (modelList == null || modelList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<PacienteDTO> dtoList = new ArrayList<>();
        for (Paciente model : modelList) {
            PacienteDTO dto = toDto(model);
            if (dto != null) {
                dtoList.add(dto);
            }
        }
        
        return dtoList;
    }
    
    /**
     * Converte lista de Especialidade para lista de EspecialidadeDTO
     */
    public static List<EspecialidadeDTO> toEspecialidadeDtoList(List<Especialidade> modelList) {
        if (modelList == null || modelList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<EspecialidadeDTO> dtoList = new ArrayList<>();
        for (Especialidade model : modelList) {
            EspecialidadeDTO dto = toDto(model);
            if (dto != null) {
                dtoList.add(dto);
            }
        }
        
        return dtoList;
    }
    
    /**
     * Converte lista de PacienteEspecialidade para lista de PacienteEspecialidadeDTO
     */
    public static List<PacienteEspecialidadeDTO> toPacienteEspecialidadeDtoList(List<PacienteEspecialidade> modelList) {
        if (modelList == null || modelList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<PacienteEspecialidadeDTO> dtoList = new ArrayList<>();
        for (PacienteEspecialidade model : modelList) {
            PacienteEspecialidadeDTO dto = toDto(model);
            if (dto != null) {
                dtoList.add(dto);
            }
        }
        
        return dtoList;
    }
}