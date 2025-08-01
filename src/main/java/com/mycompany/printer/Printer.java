package com.mycompany.printer;

import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.mycompany.model.dao.EspecialidadeDAO;
import com.mycompany.model.dao.PacienteEspecialidadeDAO;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * Classe responsável pela impressão de fichas de pacientes
 * Otimizada para melhor performance e manutenibilidade
 * 
 * @author vitor
 */
public class Printer {
    
    // Constantes para melhor legibilidade
    private static final String TITULO_AVISO = "Aviso";
    private static final String TITULO_CONFIRMACAO = "Confirmar Impressão";
    private static final String TITULO_ERRO = "Erro de Impressão";
    private static final String STATUS_ESGOTADO = "ESGOTADO";
    private static final String STATUS_CANCELADA = "CANCELADA";
    private static final String STATUS_ERRO = "ERRO";
    
    // Especialidades que não devem incluir Enfermagem
    private static final Set<String> ESPECIALIDADES_SEM_ENFERMAGEM = Set.of(
        "DENTISTA", "PSICOLOGIA", "TERAPEUTA"
    );
    
    private static final String ESPECIALIDADE_ENFERMAGEM = "ENFERMAGEM";
    
    private static final Font FONTE_IMPRESSAO = new Font("Courier New", Font.PLAIN, 10);
    private static final Insets MARGEM_IMPRESSAO = new Insets(30, 30, 30, 30);
    private static final SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    // Enum para status de impressão
    public enum StatusImpressao {
        SUCESSO("✅"),
        ERRO("❌"),
        CANCELADA("🚫"),
        ESGOTADO("⚠️");
        
        private final String emoji;
        
        StatusImpressao(String emoji) {
            this.emoji = emoji;
        }
        
        public String getEmoji() {
            return emoji;
        }
    }
    
    // Campos da classe
    private final Component parent;
    private final PacienteEspecialidadeDAO pacienteEspecialidadeDAO;
    private final EspecialidadeDAO especialidadeDAO;
    private final Map<Integer, String> especialidadesCache;
    private final Map<String, Integer> nomeParaIdCache;

    // Construtor otimizado
    public Printer(Component parent, PacienteEspecialidadeDAO pacienteEspecialidadeDAO, 
                   EspecialidadeDAO especialidadeDAO, List<Especialidade> especialidades) {
        this.parent = parent;
        this.pacienteEspecialidadeDAO = pacienteEspecialidadeDAO;
        this.especialidadeDAO = especialidadeDAO;
        // Cache das especialidades para evitar buscas repetidas
        this.especialidadesCache = criarCacheEspecialidades(especialidades);
        this.nomeParaIdCache = criarCacheNomeParaId(especialidades);
    }
    
    /**
     * Cria um cache das especialidades para otimizar buscas
     */
    private Map<Integer, String> criarCacheEspecialidades(List<Especialidade> especialidades) {
        if (especialidades == null) {
            return new HashMap<>();
        }
        return especialidades.stream()
                .collect(Collectors.toMap(Especialidade::getId, Especialidade::getNome));
    }
    
    /**
     * Cria um cache reverso (nome -> id) para buscar IDs por nome
     */
    private Map<String, Integer> criarCacheNomeParaId(List<Especialidade> especialidades) {
        if (especialidades == null) {
            return new HashMap<>();
        }
        return especialidades.stream()
                .collect(Collectors.toMap(
                    esp -> esp.getNome().toUpperCase(), 
                    Especialidade::getId,
                    (existing, replacement) -> existing
                ));
    }
    
    /**
     * Método principal para impressão de dados do paciente
     */
    public void imprimirDadosPaciente(Paciente paciente) {
        // Validação inicial
        if (!validarPaciente(paciente)) {
            return;
        }

        try {
            List<PacienteEspecialidade> especialidadesPaciente = buscarEspecialidadesPaciente(paciente);
            if (especialidadesPaciente.isEmpty()) {
                mostrarAviso("O paciente não possui especialidades associadas!");
                return;
            }

            // Agrupar especialidades para impressão
            List<GrupoImpressao> gruposImpressao = criarGruposImpressao(especialidadesPaciente);
            
            if (!confirmarImpressaoMultipla(gruposImpressao.size())) {
                return;
            }

            processarImpressaoGrupos(paciente, gruposImpressao);

        } catch (Exception ex) {
            mostrarErro("Erro geral ao imprimir: " + ex.getMessage());
        }
    }
    
    /**
     * Cria grupos de especialidades para impressão considerando a regra da Enfermagem
     */
    private List<GrupoImpressao> criarGruposImpressao(List<PacienteEspecialidade> especialidadesPaciente) {
        List<GrupoImpressao> grupos = new ArrayList<>();
        Integer enfermagemId = nomeParaIdCache.get(ESPECIALIDADE_ENFERMAGEM);
        
        // Separar especialidades por tipo
        List<PacienteEspecialidade> especialidadesSemEnfermagem = new ArrayList<>();
        List<PacienteEspecialidade> especialidadesComEnfermagem = new ArrayList<>();
        PacienteEspecialidade enfermagem = null;
        
        for (PacienteEspecialidade pe : especialidadesPaciente) {
            String nomeEspecialidade = obterNomeEspecialidade(pe.getEspecialidadeId());
            
            if (ESPECIALIDADE_ENFERMAGEM.equalsIgnoreCase(nomeEspecialidade)) {
                enfermagem = pe;
            } else if (nomeEspecialidade != null && 
                      ESPECIALIDADES_SEM_ENFERMAGEM.contains(nomeEspecialidade.toUpperCase())) {
                especialidadesSemEnfermagem.add(pe);
            } else {
                especialidadesComEnfermagem.add(pe);
            }
        }
        
        // Criar grupos para especialidades que devem incluir enfermagem
        for (PacienteEspecialidade pe : especialidadesComEnfermagem) {
            List<PacienteEspecialidade> grupo = new ArrayList<>();
            grupo.add(pe);
            
            // Adicionar enfermagem se existir no sistema e não for a própria especialidade
            if (enfermagemId != null && pe.getEspecialidadeId() != enfermagemId.intValue()) {
                PacienteEspecialidade enfermagemParaGrupo = new PacienteEspecialidade();
                enfermagemParaGrupo.setPacienteId(pe.getPacienteId());
                enfermagemParaGrupo.setEspecialidadeId(enfermagemId);
                grupo.add(enfermagemParaGrupo);
            }
            
            grupos.add(new GrupoImpressao(grupo, true));
        }
        
        // Criar grupos para especialidades que não devem incluir enfermagem
        for (PacienteEspecialidade pe : especialidadesSemEnfermagem) {
            grupos.add(new GrupoImpressao(Collections.singletonList(pe), false));
        }
        
        // Se enfermagem foi selecionada sozinha, criar um grupo só para ela
        if (enfermagem != null && especialidadesComEnfermagem.isEmpty()) {
            grupos.add(new GrupoImpressao(Collections.singletonList(enfermagem), false));
        }
        
        return grupos;
    }
    
    /**
     * Valida se o paciente possui dados válidos para impressão
     */
    private boolean validarPaciente(Paciente paciente) {
        if (paciente == null || isNullOrEmpty(paciente.getNome())) {
            mostrarAviso("Não há dados de paciente para imprimir!");
            return false;
        }
        return true;
    }
    
    /**
     * Busca as especialidades do paciente
     */
    private List<PacienteEspecialidade> buscarEspecialidadesPaciente(Paciente paciente) {
        if (paciente.getId() <= 0 || pacienteEspecialidadeDAO == null) {
            return Collections.emptyList();
        }
        
        List<PacienteEspecialidade> especialidades = pacienteEspecialidadeDAO.buscarPorPacienteId(paciente.getId());
        return especialidades != null ? especialidades : Collections.emptyList();
    }
    
    /**
     * Confirma a impressão de múltiplas fichas
     */
    private boolean confirmarImpressaoMultipla(int totalGrupos) {
        String mensagem = totalGrupos == 1 
            ? "Será impressa 1 ficha para o paciente.\nDeseja continuar?"
            : String.format("Serão impressas %d fichas para o paciente.\nDeseja continuar?", totalGrupos);
        
        return JOptionPane.showConfirmDialog(parent, mensagem, TITULO_CONFIRMACAO, 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
    
    /**
     * Processa a impressão de múltiplos grupos
     */
    private void processarImpressaoGrupos(Paciente paciente, List<GrupoImpressao> grupos) {
        ResultadoImpressao resultado = new ResultadoImpressao();
        
        for (GrupoImpressao grupo : grupos) {
            processarImpressaoGrupo(paciente, grupo, resultado);
        }
        
        mostrarResumoImpressao(resultado);
    }
    
    /**
     * Processa a impressão de um grupo específico
     */
    private void processarImpressaoGrupo(Paciente paciente, GrupoImpressao grupo, ResultadoImpressao resultado) {
        try {
            // Obter numerações e verificar status para especialidades que contam
            Map<Integer, String> numeracoes = new HashMap<>();
            boolean algumaCancelada = false;
            
            for (PacienteEspecialidade pe : grupo.getEspecialidades()) {
                String nomeEspecialidade = obterNomeEspecialidade(pe.getEspecialidadeId());
                
                // Para enfermagem adicionada automaticamente, não verificar status nem contar
                if (grupo.isTemEnfermagemAutomatica() && 
                    ESPECIALIDADE_ENFERMAGEM.equalsIgnoreCase(nomeEspecialidade) && 
                    !grupo.isEspecialidadePrincipal(pe)) {
                    numeracoes.put(pe.getEspecialidadeId(), "ACOMPANHAMENTO");
                    continue;
                }
                
                StatusAtendimento status = verificarStatusAtendimento(pe.getEspecialidadeId(), nomeEspecialidade);
                
                if (status.isCancelado()) {
                    algumaCancelada = true;
                    break;
                }
                
                numeracoes.put(pe.getEspecialidadeId(), status.getNumeracao());
            }
            
            if (algumaCancelada) {
                String nomeGrupo = grupo.obterNomeGrupo(especialidadesCache);
                resultado.adicionarResultado(nomeGrupo, STATUS_CANCELADA, StatusImpressao.CANCELADA);
                return;
            }
            
            boolean impressaoSucesso = executarImpressaoGrupo(paciente, grupo, numeracoes);
            String nomeGrupo = grupo.obterNomeGrupo(especialidadesCache);
            
            if (impressaoSucesso) {
                resultado.incrementarSucesso();
                resultado.adicionarResultado(nomeGrupo, "IMPRESSA COM SUCESSO", StatusImpressao.SUCESSO);
            } else {
                resultado.incrementarErro();
                resultado.adicionarResultado(nomeGrupo, "FALHA NA IMPRESSÃO", StatusImpressao.ERRO);
            }
            
        } catch (Exception ex) {
            String nomeGrupo = grupo.obterNomeGrupo(especialidadesCache);
            resultado.incrementarErro();
            resultado.adicionarResultado(nomeGrupo, STATUS_ERRO + ": " + ex.getMessage(), StatusImpressao.ERRO);
        }
    }
    
    /**
     * Verifica o status de atendimento de uma especialidade
     */
    private StatusAtendimento verificarStatusAtendimento(int especialidadeId, String nomeEspecialidade) {
        boolean temAtendimentos = especialidadeDAO.temAtendimentosDisponiveis(especialidadeId);
        String numeracao = STATUS_ESGOTADO;
        
        if (!temAtendimentos) {
            String mensagem = String.format("A especialidade '%s' não possui mais atendimentos disponíveis hoje.\n" +
                    "Deseja imprimir esta ficha mesmo assim?", nomeEspecialidade);
            
            int opcao = JOptionPane.showConfirmDialog(parent, mensagem, 
                    "Atendimentos Esgotados - " + nomeEspecialidade, 
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (opcao != JOptionPane.YES_OPTION) {
                return new StatusAtendimento(null, true);
            }
        } else {
            numeracao = especialidadeDAO.obterNumeracaoProximoAtendimento(especialidadeId);
            if (numeracao != null) {
                especialidadeDAO.reduzirAtendimentoRestante(especialidadeId);
            }
        }
        
        return new StatusAtendimento(numeracao, false);
    }
    
    /**
     * Executa a impressão física do documento para um grupo
     */
    private boolean executarImpressaoGrupo(Paciente paciente, GrupoImpressao grupo, Map<Integer, String> numeracoes) {
        try {
            String conteudoImpressao = criarDocumentoImpressao(paciente, grupo.getEspecialidades(), numeracoes);
            
            JTextArea areaImpressao = new JTextArea(conteudoImpressao);
            areaImpressao.setFont(FONTE_IMPRESSAO);
            areaImpressao.setMargin(MARGEM_IMPRESSAO);
            
            return areaImpressao.print();
        } catch (PrinterException ex) {
            throw new RuntimeException("Erro ao executar impressão: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Cria o documento de impressão formatado
     */
    private String criarDocumentoImpressao(Paciente paciente, 
                                          List<PacienteEspecialidade> especialidades,
                                          Map<Integer, String> numeracoes) {
        DocumentBuilder builder = new DocumentBuilder();
        
        builder.adicionarCabecalho()
               .adicionarDadosPessoais(paciente)
               .adicionarSinaisVitais(paciente)
               .adicionarDadosAntropometricos(paciente)
               .adicionarEspecialidades(especialidades, numeracoes, especialidadesCache)
               .adicionarRodape();
        
        return builder.toString();
    }
    
    /**
     * Mostra o resumo da impressão
     */
    private void mostrarResumoImpressao(ResultadoImpressao resultado) {
        ResumoImpressao resumo = new ResumoImpressao(resultado);
        
        JOptionPane.showMessageDialog(parent, 
                resumo.gerarMensagem(), 
                resumo.obterTitulo(), 
                resumo.obterTipoMensagem());
    }
    
    /**
     * Método para imprimir uma especialidade específica
     */
    public void imprimirDadosPacienteEspecialidade(Paciente paciente, int especialidadeId) {
        if (paciente == null || especialidadeId <= 0) {
            mostrarErro("Dados inválidos para impressão!");
            return;
        }

        try {
            String nomeEspecialidade = obterNomeEspecialidade(especialidadeId);
            
            // Criar grupo considerando a regra da enfermagem
            PacienteEspecialidade pe = new PacienteEspecialidade();
            pe.setPacienteId(paciente.getId());
            pe.setEspecialidadeId(especialidadeId);
            
            List<GrupoImpressao> grupos = criarGruposImpressao(Collections.singletonList(pe));
            
            if (grupos.isEmpty()) {
                mostrarErro("Erro ao criar grupo de impressão!");
                return;
            }
            
            GrupoImpressao grupo = grupos.get(0);
            Map<Integer, String> numeracoes = new HashMap<>();
            boolean algumaCancelada = false;
            
            for (PacienteEspecialidade peGrupo : grupo.getEspecialidades()) {
                String nomeEsp = obterNomeEspecialidade(peGrupo.getEspecialidadeId());
                
                // Para enfermagem adicionada automaticamente
                if (grupo.isTemEnfermagemAutomatica() && 
                    ESPECIALIDADE_ENFERMAGEM.equalsIgnoreCase(nomeEsp) && 
                    !grupo.isEspecialidadePrincipal(peGrupo)) {
                    numeracoes.put(peGrupo.getEspecialidadeId(), "ACOMPANHAMENTO");
                    continue;
                }
                
                StatusAtendimento status = verificarStatusAtendimento(peGrupo.getEspecialidadeId(), nomeEsp);
                
                if (status.isCancelado()) {
                    algumaCancelada = true;
                    break;
                }
                
                numeracoes.put(peGrupo.getEspecialidadeId(), status.getNumeracao());
            }
            
            if (algumaCancelada) {
                return;
            }

            boolean sucesso = executarImpressaoGrupo(paciente, grupo, numeracoes);

            if (sucesso) {
                String mensagem = String.format("Ficha impressa com sucesso!\n%s", 
                        grupo.obterDescricaoDetalhada(especialidadesCache, numeracoes));
                JOptionPane.showMessageDialog(parent, mensagem, "Impressão Realizada", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            mostrarErro("Erro ao imprimir: " + ex.getMessage());
        }
    }
    
    /**
     * Verifica o status dos atendimentos de uma especialidade
     */
    public String verificarStatusAtendimentos(int especialidadeId) {
        if (especialidadeId <= 0) {
            return "ID inválido";
        }

        try {
            String nomeEspecialidade = obterNomeEspecialidade(especialidadeId);
            if (nomeEspecialidade == null) {
                return "Especialidade não encontrada";
            }

            Especialidade especialidade = especialidadeDAO.buscarPorId(especialidadeId);
            if (especialidade == null) {
                return "Erro ao buscar dados da especialidade";
            }

            return String.format("Especialidade: %s\nAtendimentos restantes: %d\nTotal do dia: %d\nPróximo atendimento seria: %s",
                    especialidade.getNome(),
                    especialidade.getAtendimentosRestantesHoje(),
                    especialidade.getAtendimentosTotaisHoje(),
                    especialidade.temAtendimentosDisponiveis() ? 
                        especialidade.formatarNumeracaoAtendimento() : STATUS_ESGOTADO);

        } catch (Exception ex) {
            return "Erro ao verificar status: " + ex.getMessage();
        }
    }
    
    // Métodos utilitários
    private String obterNomeEspecialidade(int especialidadeId) {
        return especialidadesCache.get(especialidadeId);
    }
    
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    private void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, TITULO_AVISO, JOptionPane.WARNING_MESSAGE);
    }
    
    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, TITULO_ERRO, JOptionPane.ERROR_MESSAGE);
    }
    
    // Classes internas para organização
    private static class StatusAtendimento {
        private final String numeracao;
        private final boolean cancelado;
        
        public StatusAtendimento(String numeracao, boolean cancelado) {
            this.numeracao = numeracao;
            this.cancelado = cancelado;
        }
        
        public String getNumeracao() { return numeracao; }
        public boolean isCancelado() { return cancelado; }
    }
    
    /**
     * Classe para representar um grupo de especialidades que serão impressas juntas
     */
    private static class GrupoImpressao {
        private final List<PacienteEspecialidade> especialidades;
        private final boolean temEnfermagemAutomatica;
        private final PacienteEspecialidade especialidadePrincipal;
        
        public GrupoImpressao(List<PacienteEspecialidade> especialidades, boolean temEnfermagemAutomatica) {
            this.especialidades = new ArrayList<>(especialidades);
            this.temEnfermagemAutomatica = temEnfermagemAutomatica;
            this.especialidadePrincipal = especialidades.get(0); // Primeira é sempre a principal
        }
        
        public List<PacienteEspecialidade> getEspecialidades() {
            return especialidades;
        }
        
        public boolean isTemEnfermagemAutomatica() {
            return temEnfermagemAutomatica;
        }
        
        public boolean isEspecialidadePrincipal(PacienteEspecialidade pe) {
            return pe.getEspecialidadeId() == especialidadePrincipal.getEspecialidadeId();
        }
        
        public String obterNomeGrupo(Map<Integer, String> cache) {
            if (especialidades.size() == 1) {
                return cache.get(especialidades.get(0).getEspecialidadeId());
            } else {
                // Para grupos com múltiplas especialidades, mostrar a principal
                String principal = cache.get(especialidadePrincipal.getEspecialidadeId());
                return principal + " + Enfermagem";
            }
        }
        
        public String obterDescricaoDetalhada(Map<Integer, String> cache, Map<Integer, String> numeracoes) {
            StringBuilder desc = new StringBuilder();
            for (int i = 0; i < especialidades.size(); i++) {
                PacienteEspecialidade pe = especialidades.get(i);
                String nome = cache.get(pe.getEspecialidadeId());
                String numeracao = numeracoes.get(pe.getEspecialidadeId());
                
                if (i > 0) desc.append("\n");
                desc.append("• ").append(nome);
                if (numeracao != null) {
                    desc.append(" - ").append(numeracao);
                }
            }
            return desc.toString();
        }
    }
    
    private static class ResultadoImpressao {
        private int fichasImpressas = 0;
        private int fichasComErro = 0;
        private final Map<String, String> detalhes = new LinkedHashMap<>();
        private final Map<String, StatusImpressao> status = new LinkedHashMap<>();
        
        public void incrementarSucesso() { fichasImpressas++; }
        public void incrementarErro() { fichasComErro++; }
        
        public void adicionarResultado(String especialidade, String resultado, StatusImpressao statusImp) {
            detalhes.put(especialidade, resultado);
            status.put(especialidade, statusImp);
        }
        
        public int getFichasImpressas() { return fichasImpressas; }
        public int getFichasComErro() { return fichasComErro; }
        public Map<String, String> getDetalhes() { return detalhes; }
        public Map<String, StatusImpressao> getStatus() { return status; }
        
        public int getCanceladas() {
            return (int) detalhes.values().stream().filter(STATUS_CANCELADA::equals).count();
        }
    }
    
    private static class ResumoImpressao {
        private final ResultadoImpressao resultado;
        
        public ResumoImpressao(ResultadoImpressao resultado) {
            this.resultado = resultado;
        }
        
        public String gerarMensagem() {
            StringBuilder resumo = new StringBuilder();
            
            // Cabeçalho
            if (resultado.getFichasImpressas() > 0 && resultado.getFichasComErro() == 0) {
                resumo.append("✅ Todas as fichas foram impressas com sucesso!\n\n");
            } else if (resultado.getFichasImpressas() > 0 && resultado.getFichasComErro() > 0) {
                resumo.append("⚠️ Impressão parcialmente concluída!\n\n");
            } else {
                resumo.append("❌ Nenhuma ficha foi impressa!\n\n");
            }
            
            // Estatísticas
            resumo.append("📊 RESUMO:\n");
            resumo.append("• Fichas impressas: ").append(resultado.getFichasImpressas()).append("\n");
            resumo.append("• Fichas com erro: ").append(resultado.getFichasComErro()).append("\n");
            
            int canceladas = resultado.getCanceladas();
            if (canceladas > 0) {
                resumo.append("• Fichas canceladas: ").append(canceladas).append("\n");
            }
            resumo.append("\n");
            
            // Detalhes por especialidade
            resumo.append("📋 DETALHES POR FICHA:\n");
            for (Map.Entry<String, String> entry : resultado.getDetalhes().entrySet()) {
                StatusImpressao status = resultado.getStatus().get(entry.getKey());
                resumo.append(status.getEmoji()).append(" ")
                      .append(entry.getKey()).append(": ")
                      .append(entry.getValue()).append("\n");
            }
            
            return resumo.toString();
        }
        
        public String obterTitulo() {
            if (resultado.getFichasComErro() == 0 && resultado.getFichasImpressas() > 0) {
                return "Impressão Concluída";
            } else if (resultado.getFichasImpressas() > 0) {
                return "Impressão Parcial";
            } else {
                return "Erro na Impressão";
            }
        }
        
        public int obterTipoMensagem() {
            if (resultado.getFichasComErro() == 0 && resultado.getFichasImpressas() > 0) {
                return JOptionPane.INFORMATION_MESSAGE;
            } else if (resultado.getFichasImpressas() > 0) {
                return JOptionPane.WARNING_MESSAGE;
            } else {
                return JOptionPane.ERROR_MESSAGE;
            }
        }
    }
    
    /**
     * Builder para construção do documento de impressão
     */
    private static class DocumentBuilder {
        private final StringBuilder documento = new StringBuilder();
        
        public DocumentBuilder adicionarCabecalho() {
            documento.append("===============================================\n");
            documento.append("           FICHA DO PACIENTE\n");
            documento.append("===============================================\n\n");
            return this;
        }
        
        public DocumentBuilder adicionarDadosPessoais(Paciente paciente) {
            documento.append("DADOS PESSOAIS:\n");
            documento.append("-----------------------------------------------\n");
            
            adicionarCampoSePresente("Nome", paciente.getNome());
            adicionarCampoSePresente("Data de Nascimento", paciente.getDataNascimento());
            if (paciente.getIdade() != null) {
                documento.append("Idade: ").append(paciente.getIdade()).append(" anos\n");
            }
            adicionarCampoSePresente("Nome da Mãe", paciente.getNomeDaMae());
            adicionarCampoSePresente("CPF", paciente.getCpf());
            adicionarCampoSePresente("Cartão SUS", paciente.getSus());
            adicionarCampoSePresente("Telefone", paciente.getTelefone());
            adicionarCampoSePresente("Endereço", paciente.getEndereco());
            
            return this;
        }
        
        public DocumentBuilder adicionarSinaisVitais(Paciente paciente) {
            StringBuilder secao = new StringBuilder();
            secao.append("\n\nSINAIS VITAIS:\n");
            secao.append("-----------------------------------------------\n");
            
            boolean temDados = false;
            
            if (isPresente(paciente.getPaXmmhg())) {
                secao.append("Pressão Arterial: ").append(paciente.getPaXmmhg()).append("\n");
                temDados = true;
            }
            
            if (paciente.getFcBpm() != null && paciente.getFcBpm() > 0) {
                secao.append("Frequência Cardíaca: ").append(paciente.getFcBpm()).append(" bpm\n");
                temDados = true;
            }
            
            if (paciente.getFrIbpm() != null && paciente.getFrIbpm() > 0) {
                secao.append("Frequência Respiratória: ").append(paciente.getFrIbpm()).append(" rpm\n");
                temDados = true;
            }
            
            if (paciente.getTemperaturaC() != null && paciente.getTemperaturaC() > 0) {
                secao.append("Temperatura: ").append(String.format("%.1f", paciente.getTemperaturaC())).append(" °C\n");
                temDados = true;
            }
            
            if (paciente.getHgtMgld() != null && paciente.getHgtMgld() > 0) {
                secao.append("Glicemia: ").append(paciente.getHgtMgld()).append(" mg/dL\n");
                temDados = true;
            }
            
            if (paciente.getSpo2() != null && paciente.getSpo2() > 0) {
                secao.append("Saturação O2: ").append(String.format("%.1f", paciente.getSpo2())).append(" %\n");
                temDados = true;
            }
            
            if (temDados) {
                documento.append(secao);
            }
            
            return this;
        }
        
        public DocumentBuilder adicionarDadosAntropometricos(Paciente paciente) {
            StringBuilder secao = new StringBuilder();
            secao.append("\n\nDADOS ANTROPOMÉTRICOS:\n");
            secao.append("-----------------------------------------------\n");
            
            boolean temDados = false;
            
            if (paciente.getPeso() != null && paciente.getPeso() > 0) {
                secao.append("Peso: ").append(String.format("%.2f", paciente.getPeso())).append(" kg\n");
                temDados = true;
            }
            
            if (paciente.getAltura() != null && paciente.getAltura() > 0) {
                secao.append("Altura: ").append(String.format("%.2f", paciente.getAltura())).append(" m\n");
                temDados = true;
            }
            
            if (paciente.getImc() != null && paciente.getImc() > 0) {
                secao.append("IMC: ").append(String.format("%.2f", paciente.getImc())).append(" kg/m²\n");
                secao.append("Classificação IMC: ").append(obterClassificacaoIMC(paciente.getImc())).append("\n");
                temDados = true;
            }
            
            if (temDados) {
                documento.append(secao);
            }
            
            return this;
        }
        
        public DocumentBuilder adicionarEspecialidades(List<PacienteEspecialidade> especialidades,
                                                      Map<Integer, String> numeracoes,
                                                      Map<Integer, String> cache) {
            if (especialidades != null && !especialidades.isEmpty()) {
                documento.append("\n\nESPECIALIDADES MÉDICAS:\n");
                documento.append("-----------------------------------------------\n");
                
                for (PacienteEspecialidade pe : especialidades) {
                    String nomeEspecialidade = cache.get(pe.getEspecialidadeId());
                    if (nomeEspecialidade != null) {
                        documento.append("• ").append(nomeEspecialidade);
                        
                        String numeracao = numeracoes.get(pe.getEspecialidadeId());
                        if (numeracao != null) {
                            if (STATUS_ESGOTADO.equals(numeracao)) {
                                documento.append(" - ATENDIMENTOS ESGOTADOS");
                            } else if ("ACOMPANHAMENTO".equals(numeracao)) {
                                documento.append(" - ACOMPANHAMENTO");
                            } else {
                                documento.append(" - Atendimento Nº ").append(numeracao);
                            }
                        }
                        documento.append("\n");
                    }
                }
            }
            return this;
        }
        
        public DocumentBuilder adicionarRodape() {
            documento.append("\n\n");
            documento.append("===============================================\n");
            documento.append("Data/Hora da impressão: ")
                    .append(FORMATO_DATA.format(new Date())).append("\n");
            documento.append("Sistema de Gestão de Pacientes\n");
            documento.append("===============================================");
            return this;
        }
        
        private void adicionarCampoSePresente(String rotulo, String valor) {
            if (isPresente(valor)) {
                documento.append(rotulo).append(": ").append(valor).append("\n");
            }
        }
        
        private boolean isPresente(String valor) {
            return valor != null && !valor.trim().isEmpty();
        }
        
        private String obterClassificacaoIMC(float imc) {
            if (imc < 18.5) return "Abaixo do peso";
            if (imc < 25) return "Peso normal";
            if (imc < 30) return "Sobrepeso";
            if (imc < 35) return "Obesidade Grau I";
            if (imc < 40) return "Obesidade Grau II";
            return "Obesidade Grau III";
        }
        
        @Override
        public String toString() {
            return documento.toString();
        }
    }
}