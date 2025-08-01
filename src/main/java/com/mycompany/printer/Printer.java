package com.mycompany.printer;

import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.mycompany.model.dao.EspecialidadeDAO;
import com.mycompany.model.dao.PacienteEspecialidadeDAO;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author vitor
 */
public class Printer {
    
    private final Component parent;
    private final PacienteEspecialidadeDAO pacienteEspecialidadeDAO;
    private final EspecialidadeDAO especialidadeDAO;
    private final List<Especialidade> especialidades;

    //Construtor da classe Printer
    public Printer(Component parent, PacienteEspecialidadeDAO pacienteEspecialidadeDAO, EspecialidadeDAO especialidadeDAO, List<Especialidade> especialidades) {
        this.parent = parent;
        this.pacienteEspecialidadeDAO = pacienteEspecialidadeDAO;
        this.especialidadeDAO = especialidadeDAO;
        this.especialidades = especialidades;
    }
    
    //Método público para imprimir dados do paciente
    public void imprimirDadosPaciente(Paciente paciente) {
        // Verifica se há um paciente válido
        if (paciente == null || paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Não há dados de paciente para imprimir!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Busca as especialidades do paciente no banco de dados
            List<PacienteEspecialidade> pacienteEspecialidades = null;
            if (paciente.getId() > 0 && pacienteEspecialidadeDAO != null) {
                pacienteEspecialidades = pacienteEspecialidadeDAO.buscarPorPacienteId(paciente.getId());
            }

            // Verifica disponibilidade de atendimentos e obtém numerações
            Map<Integer, String> numeracoesAtendimento = new HashMap<>();
            if (pacienteEspecialidades != null && !pacienteEspecialidades.isEmpty()) {
                for (PacienteEspecialidade pe : pacienteEspecialidades) {
                    int especialidadeId = pe.getEspecialidadeId();
                    
                    // Verifica se há atendimentos disponíveis
                    if (!especialidadeDAO.temAtendimentosDisponiveis(especialidadeId)) {
                        String nomeEspecialidade = buscarNomeEspecialidadePorId(especialidadeId);
                        int opcao = JOptionPane.showConfirmDialog(parent, 
                            "A especialidade '" + nomeEspecialidade + "' não possui mais atendimentos disponíveis hoje.\n" +
                            "Deseja continuar mesmo assim?", 
                            "Atendimentos Esgotados", 
                            JOptionPane.YES_NO_OPTION, 
                            JOptionPane.WARNING_MESSAGE);
                        
                        if (opcao != JOptionPane.YES_OPTION) {
                            return; // Cancela a impressão
                        }
                        numeracoesAtendimento.put(especialidadeId, "ESGOTADO");
                    } else {
                        // Obtém a numeração antes de reduzir
                        String numeracao = especialidadeDAO.obterNumeracaoProximoAtendimento(especialidadeId);
                        if (numeracao != null) {
                            numeracoesAtendimento.put(especialidadeId, numeracao);
                            // Reduz o contador de atendimentos
                            especialidadeDAO.reduzirAtendimentoRestante(especialidadeId);
                        }
                    }
                }
            }

            // Cria o documento de impressão
            StringBuilder dadosImpressao = criarDocumentoImpressao(paciente, pacienteEspecialidades, numeracoesAtendimento);

            // Cria uma área de texto para impressão
            JTextArea areaImpressao = new JTextArea(dadosImpressao.toString());
            areaImpressao.setFont(new Font("Courier New", Font.PLAIN, 10));
            areaImpressao.setMargin(new Insets(30, 30, 30, 30));

            // Tenta imprimir
            boolean impresso = areaImpressao.print();

            if (impresso) {
                // Mostra resumo dos atendimentos impressos
                StringBuilder resumo = new StringBuilder("Ficha do paciente enviada para impressão com sucesso!\n\n");
                if (!numeracoesAtendimento.isEmpty()) {
                    resumo.append("Atendimentos registrados:\n");
                    for (Map.Entry<Integer, String> entry : numeracoesAtendimento.entrySet()) {
                        String nomeEsp = buscarNomeEspecialidadePorId(entry.getKey());
                        resumo.append("• ").append(nomeEsp).append(": ").append(entry.getValue()).append("\n");
                    }
                }
                
                JOptionPane.showMessageDialog(parent, resumo.toString(),
                        "Impressão Realizada", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Erro ao imprimir: " + ex.getMessage(),
                    "Erro de Impressão", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cria o documento de impressão completo
     * @param paciente Dados do paciente
     * @param pacienteEspecialidades Lista de especialidades do paciente (pode ser null)
     * @param numeracoesAtendimento Map com as numerações de atendimento por especialidade
     * @return StringBuilder com o documento formatado
     */
    private StringBuilder criarDocumentoImpressao(Paciente paciente, 
                                                  List<PacienteEspecialidade> pacienteEspecialidades,
                                                  Map<Integer, String> numeracoesAtendimento) {
        StringBuilder dadosImpressao = new StringBuilder();
        
        // Cabeçalho
        dadosImpressao.append("===============================================\n");
        dadosImpressao.append("           FICHA DO PACIENTE\n");
        dadosImpressao.append("===============================================\n\n");

        // Adicionar seções
        adicionarDadosPessoais(dadosImpressao, paciente);
        adicionarSinaisVitais(dadosImpressao, paciente);
        adicionarDadosAntropometricos(dadosImpressao, paciente);
        adicionarEspecialidadesComNumeracao(dadosImpressao, pacienteEspecialidades, numeracoesAtendimento);
        adicionarRodape(dadosImpressao);

        return dadosImpressao;
    }

    /**
     * Adiciona a seção de dados pessoais ao documento
     */
    private void adicionarDadosPessoais(StringBuilder dadosImpressao, Paciente paciente) {
        dadosImpressao.append("DADOS PESSOAIS:\n");
        dadosImpressao.append("-----------------------------------------------\n");

        if (paciente.getNome() != null && !paciente.getNome().trim().isEmpty()) {
            dadosImpressao.append("Nome: ").append(paciente.getNome()).append("\n");
        }

        if (paciente.getDataNascimento() != null && !paciente.getDataNascimento().trim().isEmpty()) {
            dadosImpressao.append("Data de Nascimento: ").append(paciente.getDataNascimento()).append("\n");
        }

        if (paciente.getIdade() != null) {
            dadosImpressao.append("Idade: ").append(paciente.getIdade()).append(" anos\n");
        }

        if (paciente.getNomeDaMae() != null && !paciente.getNomeDaMae().trim().isEmpty()) {
            dadosImpressao.append("Nome da Mãe: ").append(paciente.getNomeDaMae()).append("\n");
        }

        if (paciente.getCpf() != null && !paciente.getCpf().trim().isEmpty()) {
            dadosImpressao.append("CPF: ").append(paciente.getCpf()).append("\n");
        }

        if (paciente.getSus() != null && !paciente.getSus().trim().isEmpty()) {
            dadosImpressao.append("Cartão SUS: ").append(paciente.getSus()).append("\n");
        }

        if (paciente.getTelefone() != null && !paciente.getTelefone().trim().isEmpty()) {
            dadosImpressao.append("Telefone: ").append(paciente.getTelefone()).append("\n");
        }

        if (paciente.getEndereco() != null && !paciente.getEndereco().trim().isEmpty()) {
            dadosImpressao.append("Endereço: ").append(paciente.getEndereco()).append("\n");
        }
    }

    /**
     * Adiciona a seção de sinais vitais ao documento
     */
    private void adicionarSinaisVitais(StringBuilder dadosImpressao, Paciente paciente) {
        boolean temSinaisVitais = false;
        StringBuilder sinaisVitais = new StringBuilder();
        sinaisVitais.append("\n\nSINAIS VITAIS:\n");
        sinaisVitais.append("-----------------------------------------------\n");

        if (paciente.getPaXmmhg() != null && !paciente.getPaXmmhg().trim().isEmpty()) {
            sinaisVitais.append("Pressão Arterial: ").append(paciente.getPaXmmhg()).append("\n");
            temSinaisVitais = true;
        }

        if (paciente.getFcBpm() != null && paciente.getFcBpm() > 0) {
            sinaisVitais.append("Frequência Cardíaca: ").append(paciente.getFcBpm()).append(" bpm\n");
            temSinaisVitais = true;
        }

        if (paciente.getFrIbpm() != null && paciente.getFrIbpm() > 0) {
            sinaisVitais.append("Frequência Respiratória: ").append(paciente.getFrIbpm()).append(" rpm\n");
            temSinaisVitais = true;
        }

        if (paciente.getTemperaturaC() != null && paciente.getTemperaturaC() > 0) {
            sinaisVitais.append("Temperatura: ").append(String.format("%.1f", paciente.getTemperaturaC())).append(" °C\n");
            temSinaisVitais = true;
        }

        if (paciente.getHgtMgld() != null && paciente.getHgtMgld() > 0) {
            sinaisVitais.append("Glicemia: ").append(paciente.getHgtMgld()).append(" mg/dL\n");
            temSinaisVitais = true;
        }

        if (paciente.getSpo2() != null && paciente.getSpo2() > 0) {
            sinaisVitais.append("Saturação O2: ").append(String.format("%.1f", paciente.getSpo2())).append(" %\n");
            temSinaisVitais = true;
        }

        if (temSinaisVitais) {
            dadosImpressao.append(sinaisVitais);
        }
    }

    /**
     * Adiciona a seção de dados antropométricos ao documento
     */
    private void adicionarDadosAntropometricos(StringBuilder dadosImpressao, Paciente paciente) {
        boolean temDadosAntro = false;
        StringBuilder dadosAntro = new StringBuilder();
        dadosAntro.append("\n\nDADOS ANTROPOMÉTRICOS:\n");
        dadosAntro.append("-----------------------------------------------\n");

        if (paciente.getPeso() != null && paciente.getPeso() > 0) {
            dadosAntro.append("Peso: ").append(String.format("%.2f", paciente.getPeso())).append(" kg\n");
            temDadosAntro = true;
        }

        if (paciente.getAltura() != null && paciente.getAltura() > 0) {
            dadosAntro.append("Altura: ").append(String.format("%.2f", paciente.getAltura())).append(" m\n");
            temDadosAntro = true;
        }

        if (paciente.getImc() != null && paciente.getImc() > 0) {
            dadosAntro.append("IMC: ").append(String.format("%.2f", paciente.getImc())).append(" kg/m²\n");

            // Classificação do IMC
            String classificacao = obterClassificacaoIMC(paciente.getImc());
            dadosAntro.append("Classificação IMC: ").append(classificacao).append("\n");
            temDadosAntro = true;
        }

        if (temDadosAntro) {
            dadosImpressao.append(dadosAntro);
        }
    }

    /**
     * Adiciona a seção de especialidades médicas ao documento com numeração de atendimentos
     */
    private void adicionarEspecialidadesComNumeracao(StringBuilder dadosImpressao, 
                                                     List<PacienteEspecialidade> pacienteEspecialidades,
                                                     Map<Integer, String> numeracoesAtendimento) {
        if (pacienteEspecialidades != null && !pacienteEspecialidades.isEmpty()) {
            dadosImpressao.append("\n\nESPECIALIDADES MÉDICAS:\n");
            dadosImpressao.append("-----------------------------------------------\n");

            for (PacienteEspecialidade pe : pacienteEspecialidades) {
                String nomeEspecialidade = buscarNomeEspecialidadePorId(pe.getEspecialidadeId());
                if (nomeEspecialidade != null) {
                    dadosImpressao.append("• ").append(nomeEspecialidade);
                    
                    // Adiciona a numeração do atendimento se disponível
                    String numeracao = numeracoesAtendimento.get(pe.getEspecialidadeId());
                    if (numeracao != null) {
                        if ("ESGOTADO".equals(numeracao)) {
                            dadosImpressao.append(" - ATENDIMENTOS ESGOTADOS");
                        } else {
                            dadosImpressao.append(" - Atendimento Nº ").append(numeracao);
                        }
                    }
                    dadosImpressao.append("\n");
                }
            }
        }
    }

    /**
     * Adiciona o rodapé ao documento
     */
    private void adicionarRodape(StringBuilder dadosImpressao) {
        dadosImpressao.append("\n\n");
        dadosImpressao.append("===============================================\n");
        dadosImpressao.append("Data/Hora da impressão: ")
                .append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
        dadosImpressao.append("Sistema de Gestão de Pacientes\n");
        dadosImpressao.append("===============================================");
    }

    /**
     * Busca o nome da especialidade pelo ID
     */
    private String buscarNomeEspecialidadePorId(int especialidadeId) {
        if (especialidades != null) {
            for (Especialidade esp : especialidades) {
                if (esp.getId() == especialidadeId) {
                    return esp.getNome();
                }
            }
        }
        return null;
    }

    /**
     * Obtém a classificação do IMC baseada no valor
     */
    private String obterClassificacaoIMC(float imc) {
        if (imc < 18.5) {
            return "Abaixo do peso";
        } else if (imc < 25) {
            return "Peso normal";
        } else if (imc < 30) {
            return "Sobrepeso";
        } else if (imc < 35) {
            return "Obesidade Grau I";
        } else if (imc < 40) {
            return "Obesidade Grau II";
        } else {
            return "Obesidade Grau III";
        }
    }

    /**
     * Método para imprimir apenas uma especialidade específica (útil para casos especiais)
     */
    public void imprimirDadosPacienteEspecialidade(Paciente paciente, int especialidadeId) {
        if (paciente == null || especialidadeId <= 0) {
            JOptionPane.showMessageDialog(parent, "Dados inválidos para impressão!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Verifica disponibilidade de atendimentos
            if (!especialidadeDAO.temAtendimentosDisponiveis(especialidadeId)) {
                String nomeEspecialidade = buscarNomeEspecialidadePorId(especialidadeId);
                int opcao = JOptionPane.showConfirmDialog(parent, 
                    "A especialidade '" + nomeEspecialidade + "' não possui mais atendimentos disponíveis hoje.\n" +
                    "Deseja continuar mesmo assim?", 
                    "Atendimentos Esgotados", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE);
                
                if (opcao != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Cria uma lista com apenas a especialidade especificada
            PacienteEspecialidade pe = new PacienteEspecialidade();
            pe.setPacienteId(paciente.getId());
            pe.setEspecialidadeId(especialidadeId);
            
            List<PacienteEspecialidade> especialidadeUnica = List.of(pe);
            
            // Obtém numeração e reduz contador
            Map<Integer, String> numeracoesAtendimento = new HashMap<>();
            String numeracao = especialidadeDAO.obterNumeracaoProximoAtendimento(especialidadeId);
            if (numeracao != null) {
                numeracoesAtendimento.put(especialidadeId, numeracao);
                especialidadeDAO.reduzirAtendimentoRestante(especialidadeId);
            } else {
                numeracoesAtendimento.put(especialidadeId, "ESGOTADO");
            }

            // Cria e imprime o documento
            StringBuilder dadosImpressao = criarDocumentoImpressao(paciente, especialidadeUnica, numeracoesAtendimento);
            
            JTextArea areaImpressao = new JTextArea(dadosImpressao.toString());
            areaImpressao.setFont(new Font("Courier New", Font.PLAIN, 10));
            areaImpressao.setMargin(new Insets(30, 30, 30, 30));

            boolean impresso = areaImpressao.print();

            if (impresso) {
                String nomeEsp = buscarNomeEspecialidadePorId(especialidadeId);
                String numeroAtendimento = numeracoesAtendimento.get(especialidadeId);
                JOptionPane.showMessageDialog(parent, 
                    "Ficha impressa com sucesso!\n" +
                    "Especialidade: " + nomeEsp + "\n" +
                    "Atendimento: " + numeroAtendimento,
                    "Impressão Realizada", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Erro ao imprimir: " + ex.getMessage(),
                    "Erro de Impressão", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para verificar status dos atendimentos de uma especialidade
     */
    public String verificarStatusAtendimentos(int especialidadeId) {
        if (especialidadeId <= 0) {
            return "ID inválido";
        }

        try {
            Especialidade esp = null;
            for (Especialidade e : especialidades) {
                if (e.getId() == especialidadeId) {
                    esp = e;
                    break;
                }
            }

            if (esp == null) {
                return "Especialidade não encontrada";
            }

            // Busca dados atualizados do banco
            Especialidade espAtualizada = especialidadeDAO.buscarPorId(especialidadeId);
            if (espAtualizada == null) {
                return "Erro ao buscar dados da especialidade";
            }

            return String.format("Especialidade: %s\nAtendimentos restantes: %d\nTotal do dia: %d\nPróximo atendimento seria: %s",
                    espAtualizada.getNome(),
                    espAtualizada.getAtendimentosRestantesHoje(),
                    espAtualizada.getAtendimentosTotaisHoje(),
                    espAtualizada.temAtendimentosDisponiveis() ? 
                        espAtualizada.formatarNumeracaoAtendimento() : "ESGOTADO");

        } catch (Exception ex) {
            return "Erro ao verificar status: " + ex.getMessage();
        }
    }
}