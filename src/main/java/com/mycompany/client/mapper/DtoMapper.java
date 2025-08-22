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
 * Versão melhorada com prevenção robusta de NullPointerException
 */
public class DtoMapper {
    
    private static final Logger LOGGER = Logger.getLogger(DtoMapper.class.getName());
    
    // ===== CONVERSÕES DTO → MODEL =====
    
    /**
     * Converte PacienteDTO para Paciente com validação robusta
     */
    public static Paciente toModel(PacienteDTO dto) {
        if (dto == null) {
            LOGGER.warning("PacienteDTO é null");
            return null;
        }

        if (dto.getId() == null) {
            LOGGER.warning("PacienteDTO com ID nulo será ignorado: " + safeGetString(dto.getNome()));
            return null;
        }

        try {
            Paciente paciente = new Paciente();

            // Campos obrigatórios com validação
            paciente.setId(dto.getId());
            paciente.setNome(safeGetString(dto.getNome()));
            paciente.setDataNascimento(safeGetString(dto.getDataNascimento()));
            paciente.setIdade(dto.getIdade());
            paciente.setNomeDaMae(safeGetString(dto.getNomeDaMae()));
            paciente.setCpf(safeGetString(dto.getCpf()));
            paciente.setSus(safeGetString(dto.getSus()));
            paciente.setTelefone(safeGetString(dto.getTelefone()));
            paciente.setEndereco(safeGetString(dto.getEndereco()));
            paciente.setPaXMmhg(safeGetString(dto.getPaXMmhg()));

            // Campos Float com conversão segura
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
            LOGGER.log(Level.SEVERE, "Erro ao converter PacienteDTO para Paciente: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Converte EspecialidadeDTO para Especialidade com validação robusta
     */
    public static Especialidade toModel(EspecialidadeDTO dto) {
        if (dto == null) {
            LOGGER.warning("EspecialidadeDTO é null");
            return null;
        }
        
        if (dto.getId() == null) {
            LOGGER.warning("EspecialidadeDTO com ID nulo será ignorada: " + safeGetString(dto.getNome()));
            return null;
        }
        
        try {
            Especialidade especialidade = new Especialidade();
            
            especialidade.setId(dto.getId());
            especialidade.setNome(safeGetString(dto.getNome()));
            especialidade.setAtendimentosRestantesHoje(safeGetInteger(dto.getAtendimentosRestantesHoje()));
            especialidade.setAtendimentosTotaisHoje(safeGetInteger(dto.getAtendimentosTotaisHoje()));
            
            return especialidade;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao converter EspecialidadeDTO para Especialidade: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Converte PacienteEspecialidadeDTO para PacienteEspecialidade com validação robusta
     */
    public static PacienteEspecialidade toModel(PacienteEspecialidadeDTO dto) {
        if (dto == null) {
            LOGGER.warning("PacienteEspecialidadeDTO é null");
            return null;
        }
        
        if (dto.getPacienteId() == null || dto.getEspecialidadeId() == null) {
            LOGGER.warning("PacienteEspecialidadeDTO com IDs nulos será ignorado");
            return null;
        }
        
        try {
            PacienteEspecialidade pacienteEspecialidade = new PacienteEspecialidade();
            
            pacienteEspecialidade.setPacienteId(dto.getPacienteId());
            pacienteEspecialidade.setEspecialidadeId(dto.getEspecialidadeId());
            pacienteEspecialidade.setDataAtendimento(safeGetString(dto.getDataAtendimento()));
            
            return pacienteEspecialidade;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao converter PacienteEspecialidadeDTO: " + e.getMessage(), e);
            return null;
        }
    }
    
    // ===== MÉTODOS UTILITÁRIOS SEGUROS =====
    
    /**
     * Retorna string segura, nunca null
     */
    private static String safeGetString(String value) {
        return value != null ? value.trim() : "";
    }
    
    /**
     * Retorna integer seguro, nunca null para primitivos
     */
    private static int safeGetInteger(Integer value) {
        return value != null ? value : 0;
    }
    
    /**
     * Converte Double para Float de forma ultra-segura
     */
    private static Float safeConvertDoubleToFloat(Double value) {
        if (value == null) {
            return 0.0f; // Valor padrão para primitivos
        }
        
        // Verifica se o valor está dentro do range válido para Float
        if (value > Float.MAX_VALUE) {
            LOGGER.warning("Valor Double muito grande para Float: " + value);
            return Float.MAX_VALUE;
        }
        
        if (value < -Float.MAX_VALUE) {
            LOGGER.warning("Valor Double muito pequeno para Float: " + value);
            return -Float.MAX_VALUE;
        }
        
        return value.floatValue();
    }
    
    /**
     * Converte Float para Double de forma ultra-segura
     */
    private static Double safeConvertFloatToDouble(Float value) {
        if (value == null) {
            return 0.0; // Valor padrão
        }
        return value.doubleValue();
    }
    
    /**
     * Validação robusta de lista
     */
    public static boolean isValidList(List<?> list) {
        return list != null && !list.isEmpty();
    }
    
    /**
     * Conversão segura de listas com logging detalhado
     */
    public static List<Paciente> toModelList(List<PacienteDTO> dtoList) {
        List<Paciente> result = new ArrayList<>();
        
        if (!isValidList(dtoList)) {
            LOGGER.info("Lista de PacienteDTO vazia ou nula");
            return result;
        }
        
        int sucessos = 0;
        int falhas = 0;
        
        for (int i = 0; i < dtoList.size(); i++) {
            try {
                PacienteDTO dto = dtoList.get(i);
                if (dto == null) {
                    LOGGER.warning("PacienteDTO na posição " + i + " é null");
                    falhas++;
                    continue;
                }
                
                Paciente paciente = toModel(dto);
                if (paciente != null && paciente.isValid()) {
                    result.add(paciente);
                    sucessos++;
                } else {
                    LOGGER.warning("Falha ao converter ou validar PacienteDTO na posição " + i);
                    falhas++;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erro ao processar PacienteDTO na posição " + i, e);
                falhas++;
            }
        }
        
        LOGGER.info(String.format("Conversão concluída: %d sucessos, %d falhas de %d total", 
                                 sucessos, falhas, dtoList.size()));
        return result;
    }
    
    // Método similar para EspecialidadeDTO
    public static List<Especialidade> toEspecialidadeModelList(List<EspecialidadeDTO> dtoList) {
        List<Especialidade> result = new ArrayList<>();
        
        if (!isValidList(dtoList)) {
            LOGGER.info("Lista de EspecialidadeDTO vazia ou nula");
            return result;
        }
        
        int sucessos = 0;
        int falhas = 0;
        
        for (int i = 0; i < dtoList.size(); i++) {
            try {
                EspecialidadeDTO dto = dtoList.get(i);
                if (dto == null) {
                    LOGGER.warning("EspecialidadeDTO na posição " + i + " é null");
                    falhas++;
                    continue;
                }
                
                Especialidade especialidade = toModel(dto);
                if (especialidade != null) {
                    result.add(especialidade);
                    sucessos++;
                } else {
                    falhas++;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erro ao processar EspecialidadeDTO na posição " + i, e);
                falhas++;
            }
        }
        
        LOGGER.info(String.format("Conversão Especialidades: %d sucessos, %d falhas de %d total", 
                                 sucessos, falhas, dtoList.size()));
        return result;
    }
    
    // Método para PacienteEspecialidadeDTO
    public static List<PacienteEspecialidade> toPacienteEspecialidadeModelList(List<PacienteEspecialidadeDTO> dtoList) {
        List<PacienteEspecialidade> result = new ArrayList<>();
        
        if (!isValidList(dtoList)) {
            LOGGER.info("Lista de PacienteEspecialidadeDTO vazia ou nula");
            return result;
        }
        
        int sucessos = 0;
        int falhas = 0;
        
        for (int i = 0; i < dtoList.size(); i++) {
            try {
                PacienteEspecialidadeDTO dto = dtoList.get(i);
                if (dto == null) {
                    LOGGER.warning("PacienteEspecialidadeDTO na posição " + i + " é null");
                    falhas++;
                    continue;
                }
                
                PacienteEspecialidade associacao = toModel(dto);
                if (associacao != null && associacao.isValid()) {
                    result.add(associacao);
                    sucessos++;
                } else {
                    falhas++;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erro ao processar PacienteEspecialidadeDTO na posição " + i, e);
                falhas++;
            }
        }
        
        LOGGER.info(String.format("Conversão PacienteEspecialidade: %d sucessos, %d falhas de %d total", 
                                 sucessos, falhas, dtoList.size()));
        return result;
    }
    
    // ===== CONVERSÕES MODEL → DTO (MÉTODOS FALTANTES) =====
    
    /**
     * Converte Paciente para PacienteDTO (para envio à API)
     */
    public static PacienteDTO toDto(Paciente model) {
        if (model == null) {
            LOGGER.warning("Paciente é null para conversão");
            return null;
        }
        
        try {
            PacienteDTO dto = new PacienteDTO();
            
            dto.setId(model.getId());
            dto.setNome(safeGetString(model.getNome()));
            dto.setDataNascimento(safeGetString(model.getDataNascimento()));
            dto.setIdade(model.getIdade());
            dto.setNomeDaMae(safeGetString(model.getNomeDaMae()));
            dto.setCpf(safeGetString(model.getCpf()));
            dto.setSus(safeGetString(model.getSus()));
            dto.setTelefone(safeGetString(model.getTelefone()));
            dto.setEndereco(safeGetString(model.getEndereco()));
            dto.setPaXMmhg(safeGetString(model.getPaXMmhg()));
            
            // Conversão de Float para Double
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
            LOGGER.log(Level.SEVERE, "Erro ao converter Paciente para PacienteDTO: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Converte Especialidade para EspecialidadeDTO (para envio à API)
     */
    public static EspecialidadeDTO toDto(Especialidade model) {
        if (model == null) {
            LOGGER.warning("Especialidade é null para conversão");
            return null;
        }
        
        try {
            EspecialidadeDTO dto = new EspecialidadeDTO();
            
            dto.setId(model.getId());
            dto.setNome(safeGetString(model.getNome()));
            dto.setAtendimentosRestantesHoje(model.getAtendimentosRestantesHoje());
            dto.setAtendimentosTotaisHoje(model.getAtendimentosTotaisHoje());
            
            return dto;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao converter Especialidade para EspecialidadeDTO: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Converte PacienteEspecialidade para PacienteEspecialidadeDTO (para envio à API)
     */
    public static PacienteEspecialidadeDTO toDto(PacienteEspecialidade model) {
        if (model == null) {
            LOGGER.warning("PacienteEspecialidade é null para conversão");
            return null;
        }
        
        try {
            PacienteEspecialidadeDTO dto = new PacienteEspecialidadeDTO();
            
            dto.setPacienteId(model.getPacienteId());
            dto.setEspecialidadeId(model.getEspecialidadeId());
            dto.setDataAtendimento(safeGetString(model.getDataAtendimento()));
            
            return dto;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao converter PacienteEspecialidade para PacienteEspecialidadeDTO: " + e.getMessage(), e);
            return null;
        }
    }
    
    // ===== MÉTODOS DE CONVERSÃO DE LISTA MODEL → DTO =====
    
    /**
     * Converte lista de Paciente para lista de PacienteDTO
     */
    public static List<PacienteDTO> toPacienteDtoList(List<Paciente> modelList) {
        List<PacienteDTO> result = new ArrayList<>();
        
        if (!isValidList(modelList)) {
            LOGGER.info("Lista de Paciente vazia ou nula");
            return result;
        }
        
        int sucessos = 0;
        int falhas = 0;
        
        for (int i = 0; i < modelList.size(); i++) {
            try {
                Paciente model = modelList.get(i);
                if (model == null) {
                    LOGGER.warning("Paciente na posição " + i + " é null");
                    falhas++;
                    continue;
                }
                
                PacienteDTO dto = toDto(model);
                if (dto != null) {
                    result.add(dto);
                    sucessos++;
                } else {
                    falhas++;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erro ao processar Paciente na posição " + i, e);
                falhas++;
            }
        }
        
        LOGGER.info(String.format("Conversão Paciente para DTO: %d sucessos, %d falhas de %d total", 
                                 sucessos, falhas, modelList.size()));
        return result;
    }
    
    /**
     * Converte lista de Especialidade para lista de EspecialidadeDTO
     */
    public static List<EspecialidadeDTO> toEspecialidadeDtoList(List<Especialidade> modelList) {
        List<EspecialidadeDTO> result = new ArrayList<>();
        
        if (!isValidList(modelList)) {
            LOGGER.info("Lista de Especialidade vazia ou nula");
            return result;
        }
        
        int sucessos = 0;
        int falhas = 0;
        
        for (int i = 0; i < modelList.size(); i++) {
            try {
                Especialidade model = modelList.get(i);
                if (model == null) {
                    LOGGER.warning("Especialidade na posição " + i + " é null");
                    falhas++;
                    continue;
                }
                
                EspecialidadeDTO dto = toDto(model);
                if (dto != null) {
                    result.add(dto);
                    sucessos++;
                } else {
                    falhas++;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erro ao processar Especialidade na posição " + i, e);
                falhas++;
            }
        }
        
        LOGGER.info(String.format("Conversão Especialidade para DTO: %d sucessos, %d falhas de %d total", 
                                 sucessos, falhas, modelList.size()));
        return result;
    }
    
    /**
     * Converte lista de PacienteEspecialidade para lista de PacienteEspecialidadeDTO
     */
    public static List<PacienteEspecialidadeDTO> toPacienteEspecialidadeDtoList(List<PacienteEspecialidade> modelList) {
        List<PacienteEspecialidadeDTO> result = new ArrayList<>();
        
        if (!isValidList(modelList)) {
            LOGGER.info("Lista de PacienteEspecialidade vazia ou nula");
            return result;
        }
        
        int sucessos = 0;
        int falhas = 0;
        
        for (int i = 0; i < modelList.size(); i++) {
            try {
                PacienteEspecialidade model = modelList.get(i);
                if (model == null) {
                    LOGGER.warning("PacienteEspecialidade na posição " + i + " é null");
                    falhas++;
                    continue;
                }
                
                PacienteEspecialidadeDTO dto = toDto(model);
                if (dto != null) {
                    result.add(dto);
                    sucessos++;
                } else {
                    falhas++;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erro ao processar PacienteEspecialidade na posição " + i, e);
                falhas++;
            }
        }
        
        LOGGER.info(String.format("Conversão PacienteEspecialidade para DTO: %d sucessos, %d falhas de %d total", 
                                 sucessos, falhas, modelList.size()));
        return result;
    }
    
    // ===== MÉTODOS UTILITÁRIOS ADICIONAIS =====
    
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
    
    /**
     * Copia dados entre objetos Paciente
     */
    public static void copyPacienteData(Paciente origem, Paciente destino) {
        if (origem == null || destino == null) {
            return;
        }
        
        destino.setId(origem.getId());
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
    
    /**
     * Copia dados entre objetos Especialidade
     */
    public static void copyEspecialidadeData(Especialidade origem, Especialidade destino) {
        if (origem == null || destino == null) {
            return;
        }
        
        destino.setId(origem.getId());
        destino.setNome(origem.getNome());
        destino.setAtendimentosRestantesHoje(origem.getAtendimentosRestantesHoje());
        destino.setAtendimentosTotaisHoje(origem.getAtendimentosTotaisHoje());
    }
}