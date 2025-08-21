package com.mycompany.client.mapper;

import com.mycompany.client.dto.PacienteDTO;
import com.mycompany.client.dto.EspecialidadeDTO;
import com.mycompany.client.dto.PacienteEspecialidadeDTO;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.PacienteEspecialidade;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Classe utilitária para converter DTOs da API para modelos de domínio
 * Versão corrigida com tratamento adequado de tipos primitivos/wrapper
 */
public class DtoMapper {
    
    private static final Logger LOGGER = Logger.getLogger(DtoMapper.class.getName());
    
    // ===== CONVERSÕES DTO → MODEL =====
    
    /**
     * Converte PacienteDTO para Paciente
     */
    public static Paciente toModel(PacienteDTO dto) {
        if (dto == null) {
            return null;
        }

        // VALIDAÇÃO CRÍTICA: Verificar se o DTO tem ID
        if (dto.getId() == null) {
            LOGGER.warning("PacienteDTO com ID nulo será ignorado: " + dto.getNome());
            return null;
        }

        try {
            Paciente paciente = new Paciente();

            // Campos obrigatórios - CORRIGIDO: usar intValue() para evitar NPE
            paciente.setId(dto.getId().intValue());
            paciente.setNome(dto.getNome());
            paciente.setDataNascimento(dto.getDataNascimento());
            paciente.setIdade(dto.getIdade());
            paciente.setNomeDaMae(dto.getNomeDaMae());
            paciente.setCpf(dto.getCpf());
            paciente.setSus(dto.getSus());
            paciente.setTelefone(dto.getTelefone());
            paciente.setEndereco(dto.getEndereco());
            paciente.setPaXMmhg(dto.getPaXMmhg());

            // Campos Float - CORRIGIDO: conversão direta de Double para Float
            paciente.setFcBpm(safeConvertDoubleToFloat(dto.getFcBpm()));
            paciente.setFrIbpm(safeConvertDoubleToFloat(dto.getFrIbpm()));
            paciente.setTemperaturaC(safeConvertDoubleToFloat(dto.getTemperaturaC()));
            paciente.setHgtMgld(safeConvertDoubleToFloat(dto.getHgtMgld()));
            paciente.setSpo2(safeConvertDoubleToFloat(dto.getSpo2()));
            paciente.setPeso(safeConvertDoubleToFloat(dto.getPeso()));
            paciente.setAltura(safeConvertDoubleToFloat(dto.getAltura()));
            paciente.setImc(safeConvertDoubleToFloat(dto.getImc()));

            return paciente;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao converter PacienteDTO para Paciente", e);
            return null;
        }
    }
    
    /**
     * Converte EspecialidadeDTO para Especialidade
     */
    public static Especialidade toModel(EspecialidadeDTO dto) {
        if (dto == null) {
            return null;
        }
        
        try {
            Especialidade especialidade = new Especialidade();
            
            // CORRIGIDO: verificar null antes de converter
            if (dto.getId() != null) {
                especialidade.setId(dto.getId().intValue());
            }
            
            especialidade.setNome(dto.getNome());
            
            // CORRIGIDO: usar conversão segura para primitivos
            especialidade.setAtendimentosRestantesHoje(
                safeConvertIntegerToPrimitive(dto.getAtendimentosRestantesHoje())
            );
            especialidade.setAtendimentosTotaisHoje(
                safeConvertIntegerToPrimitive(dto.getAtendimentosTotaisHoje())
            );
            
            return especialidade;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao converter EspecialidadeDTO para Especialidade", e);
            return null;
        }
    }
    
    /**
     * Converte PacienteEspecialidadeDTO para PacienteEspecialidade
     */
    public static PacienteEspecialidade toModel(PacienteEspecialidadeDTO dto) {
        if (dto == null) {
            return null;
        }
        
        try {
            PacienteEspecialidade pacienteEspecialidade = new PacienteEspecialidade();
            
            // CORRIGIDO: verificar null antes de converter
            if (dto.getPacienteId() != null) {
                pacienteEspecialidade.setPacienteId(dto.getPacienteId().intValue());
            }
            
            if (dto.getEspecialidadeId() != null) {
                pacienteEspecialidade.setEspecialidadeId(dto.getEspecialidadeId().intValue());
            }
            
            pacienteEspecialidade.setDataAtendimento(dto.getDataAtendimento());
            
            return pacienteEspecialidade;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao converter PacienteEspecialidadeDTO para PacienteEspecialidade", e);
            return null;
        }
    }
    
    // ===== CONVERSÕES MODEL → DTO =====
    
    /**
     * Converte Paciente para PacienteDTO (para envio à API)
     */
    public static PacienteDTO toDto(Paciente model) {
        if (model == null) {
            return null;
        }
        
        try {
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
            dto.setPaXMmhg(model.getPaXMmhg());
            
            // CORRIGIDO: conversão direta de Float para Double
            dto.setFcBpm(safeConvertFloatToDouble(model.getFcBpm()));
            dto.setFrIbpm(safeConvertFloatToDouble(model.getFrIbpm()));
            dto.setTemperaturaC(safeConvertFloatToDouble(model.getTemperaturaC()));
            dto.setHgtMgld(safeConvertFloatToDouble(model.getHgtMgld()));   
            dto.setSpo2(safeConvertFloatToDouble(model.getSpo2()));  
            dto.setPeso(safeConvertFloatToDouble(model.getPeso()));
            dto.setAltura(safeConvertFloatToDouble(model.getAltura()));
            dto.setImc(safeConvertFloatToDouble(model.getImc()));
            
            return dto;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao converter Paciente para PacienteDTO", e);
            return null;
        }
    }
    
    /**
     * Converte Especialidade para EspecialidadeDTO (para envio à API)
     */
    public static EspecialidadeDTO toDto(Especialidade model) {
        if (model == null) {
            return null;
        }
        
        try {
            EspecialidadeDTO dto = new EspecialidadeDTO();
            
            dto.setId(model.getId());
            dto.setNome(model.getNome());
            dto.setAtendimentosRestantesHoje(model.getAtendimentosRestantesHoje());
            dto.setAtendimentosTotaisHoje(model.getAtendimentosTotaisHoje());
            
            return dto;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao converter Especialidade para EspecialidadeDTO", e);
            return null;
        }
    }
    
    /**
     * Converte PacienteEspecialidade para PacienteEspecialidadeDTO (para envio à API)
     */
    public static PacienteEspecialidadeDTO toDto(PacienteEspecialidade model) {
        if (model == null) {
            return null;
        }
        
        try {
            PacienteEspecialidadeDTO dto = new PacienteEspecialidadeDTO();
            
            dto.setPacienteId(model.getPacienteId());
            dto.setEspecialidadeId(model.getEspecialidadeId());
            dto.setDataAtendimento(model.getDataAtendimento());
            
            return dto;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao converter PacienteEspecialidade para PacienteEspecialidadeDTO", e);
            return null;
        }
    }
    
    // ===== MÉTODOS DE CONVERSÃO DE LISTA DTO → MODEL =====
    
    public static List<Paciente> toModelList(List<PacienteDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Paciente> pacientes = new ArrayList<>();
        int sucessos = 0;
        
        for (PacienteDTO dto : dtoList) {
            Paciente paciente = toModel(dto);
            if (paciente != null) {
                pacientes.add(paciente);
                sucessos++;
            } else {
                LOGGER.warning("Falha ao converter PacienteDTO: " + dto);
            }
        }
        
        LOGGER.info("Convertidos " + sucessos + " pacientes de " + dtoList.size() + " DTOs");
        return pacientes;
    }
    
    public static List<Especialidade> toEspecialidadeModelList(List<EspecialidadeDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Especialidade> especialidades = new ArrayList<>();
        int sucessos = 0;
        
        for (EspecialidadeDTO dto : dtoList) {
            Especialidade especialidade = toModel(dto);
            if (especialidade != null) {
                especialidades.add(especialidade);
                sucessos++;
            } else {
                LOGGER.warning("Falha ao converter EspecialidadeDTO: " + dto);
            }
        }
        
        LOGGER.info("Convertidas " + sucessos + " especialidades de " + dtoList.size() + " DTOs");
        return especialidades;
    }
    
    public static List<PacienteEspecialidade> toPacienteEspecialidadeModelList(List<PacienteEspecialidadeDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<PacienteEspecialidade> associacoes = new ArrayList<>();
        int sucessos = 0;
        
        for (PacienteEspecialidadeDTO dto : dtoList) {
            PacienteEspecialidade associacao = toModel(dto);
            if (associacao != null) {
                associacoes.add(associacao);
                sucessos++;
            } else {
                LOGGER.warning("Falha ao converter PacienteEspecialidadeDTO: " + dto);
            }
        }
        
        LOGGER.info("Convertidas " + sucessos + " associações de " + dtoList.size() + " DTOs");
        return associacoes;
    }
    
    // ===== MÉTODOS DE CONVERSÃO DE LISTA MODEL → DTO =====
    
    /**
     * Converte lista de Paciente para lista de PacienteDTO
     */
    public static List<PacienteDTO> toPacienteDtoList(List<Paciente> modelList) {
        if (modelList == null || modelList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<PacienteDTO> dtoList = new ArrayList<>();
        int sucessos = 0;
        
        for (Paciente model : modelList) {
            PacienteDTO dto = toDto(model);
            if (dto != null) {
                dtoList.add(dto);
                sucessos++;
            } else {
                LOGGER.warning("Falha ao converter Paciente: " + model);
            }
        }
        
        LOGGER.info("Convertidos " + sucessos + " pacientes para DTO de " + modelList.size() + " modelos");
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
        int sucessos = 0;
        
        for (Especialidade model : modelList) {
            EspecialidadeDTO dto = toDto(model);
            if (dto != null) {
                dtoList.add(dto);
                sucessos++;
            } else {
                LOGGER.warning("Falha ao converter Especialidade: " + model);
            }
        }
        
        LOGGER.info("Convertidas " + sucessos + " especialidades para DTO de " + modelList.size() + " modelos");
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
        int sucessos = 0;
        
        for (PacienteEspecialidade model : modelList) {
            PacienteEspecialidadeDTO dto = toDto(model);
            if (dto != null) {
                dtoList.add(dto);
                sucessos++;
            } else {
                LOGGER.warning("Falha ao converter PacienteEspecialidade: " + model);
            }
        }
        
        LOGGER.info("Convertidas " + sucessos + " associações para DTO de " + modelList.size() + " modelos");
        return dtoList;
    }
    
    // ===== MÉTODOS UTILITÁRIOS CORRIGIDOS =====
    
    /**
     * Converte Double para Float de forma segura
     * Retorna null se o valor for null (não força um valor padrão)
     */
    private static Float safeConvertDoubleToFloat(Double value) {
        if (value == null) {
            return null; // Mantém null se a entrada for null
        }
        return value.floatValue();
    }
    
    /**
     * Converte Float para Double de forma segura
     * Retorna null se o valor for null (não força um valor padrão)
     */
    private static Double safeConvertFloatToDouble(Float value) {
        if (value == null) {
            return null; // Mantém null se a entrada for null
        }
        return value.doubleValue();
    }
    
    /**
     * Converte Integer wrapper para int primitivo de forma segura
     * Retorna 0 se o valor for null (valor padrão seguro para primitivos)
     */
    private static int safeConvertIntegerToPrimitive(Integer value) {
        return value != null ? value.intValue() : 0;
    }
    
    /**
     * Valida se um PacienteDTO tem campos mínimos necessários
     */
    public static boolean isValidPacienteDto(PacienteDTO dto) {
        return dto != null && 
               dto.getId() != null &&
               dto.getNome() != null && 
               !dto.getNome().trim().isEmpty();
    }
    
    /**
     * Valida se um EspecialidadeDTO tem campos mínimos necessários
     */
    public static boolean isValidEspecialidadeDto(EspecialidadeDTO dto) {
        return dto != null && 
               dto.getId() != null &&
               dto.getNome() != null && 
               !dto.getNome().trim().isEmpty();
    }
    
    /**
     * Valida se um PacienteEspecialidadeDTO tem campos mínimos necessários
     */
    public static boolean isValidPacienteEspecialidadeDto(PacienteEspecialidadeDTO dto) {
        return dto != null && 
               dto.getPacienteId() != null && 
               dto.getEspecialidadeId() != null &&
               dto.getPacienteId() > 0 &&
               dto.getEspecialidadeId() > 0;
    }
    
    // Demais métodos utilitários mantidos iguais...
    
    public static void copyPacienteData(Paciente origem, Paciente destino) {
        if (origem == null || destino == null) {
            return;
        }
        
        if (origem.getId() != null) {
            destino.setId(origem.getId().intValue());
        }
        destino.setNome(origem.getNome());
        destino.setDataNascimento(origem.getDataNascimento());
        destino.setIdade(origem.getIdade());
        destino.setNomeDaMae(origem.getNomeDaMae());
        destino.setCpf(origem.getCpf());
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
    
    public static void copyEspecialidadeData(Especialidade origem, Especialidade destino) {
        if (origem == null || destino == null) {
            return;
        }
        
        if (origem.getId() != null) {
            destino.setId(origem.getId().intValue());
        }
        destino.setNome(origem.getNome());
        destino.setAtendimentosRestantesHoje(origem.getAtendimentosRestantesHoje());
        destino.setAtendimentosTotaisHoje(origem.getAtendimentosTotaisHoje());
    }
}