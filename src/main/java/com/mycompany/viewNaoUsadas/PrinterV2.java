package com.mycompany.viewNaoUsadas;

import com.mycompany.printer.*;
import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.mycompany.model.dao.EspecialidadeDAO;
import com.mycompany.model.dao.PacienteEspecialidadeDAO;

import java.awt.*;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * Classe responsável pela impressão de fichas de pacientes
 * Otimizada para melhor performance e manutenibilidade
 * 
 * @author vitor
 */
public class PrinterV2 {
    
    // ========== CONSTANTES ==========
    private static final class Constants {
        // Mensagens de diálogo
        static final String TITULO_AVISO = "Aviso";
        static final String TITULO_CONFIRMACAO = "Confirmar Impressão";
        static final String TITULO_ERRO = "Erro de Impressão";
        
        // Status de impressão
        static final String STATUS_ESGOTADO = "ESGOTADO";
        static final String STATUS_CANCELADA = "CANCELADA";
        static final String STATUS_ERRO = "ERRO";
        static final String STATUS_ACOMPANHAMENTO = "ACOMPANHAMENTO";
        
        // Especialidades
        static final String ESPECIALIDADE_ENFERMAGEM = "ENFERMAGEM";
        static final Set<String> ESPECIALIDADES_SEM_ENFERMAGEM = Set.of(
            "DENTISTA", "PSICOLOGIA", "TERAPEUTA"
        );
        
        // Layout e fontes
        static final Font FONTE_TITULO = new Font("Arial", Font.BOLD, 14);
        static final Font FONTE_SECAO = new Font("Arial", Font.BOLD, 11);
        static final Font FONTE_NORMAL = new Font("Arial", Font.PLAIN, 9);
        static final Font FONTE_PEQUENA = new Font("Arial", Font.PLAIN, 8);
        
        static final String CAMINHO_LOGO = "/home/vitor/NetBeansProjects/projeto_IBG/src/main/resources/imagens/logo_cecom.png";
        
        // Formatadores de data
        static final SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy");
        static final SimpleDateFormat FORMATO_DATA_HORA = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }
    
    // ========== ENUMS ==========
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
    
    // ========== CAMPOS DA CLASSE ==========
    private final Component parent;
    private final PacienteEspecialidadeDAO pacienteEspecialidadeDAO;
    private final EspecialidadeDAO especialidadeDAO;
    private final Map<Integer, String> especialidadesCache;
    private final Map<String, Integer> nomeParaIdCache;

    // ========== CONSTRUTOR ==========
    public PrinterV2(Component parent, 
                   PacienteEspecialidadeDAO pacienteEspecialidadeDAO, 
                   EspecialidadeDAO especialidadeDAO, 
                   List<Especialidade> especialidades) {
        this.parent = parent;
        this.pacienteEspecialidadeDAO = pacienteEspecialidadeDAO;
        this.especialidadeDAO = especialidadeDAO;
        this.especialidadesCache = EspecialidadeHelper.criarCacheEspecialidades(especialidades);
        this.nomeParaIdCache = EspecialidadeHelper.criarCacheNomeParaId(especialidades);
    }
    
    // ========== MÉTODOS PÚBLICOS PRINCIPAIS ==========
    
    /**
     * Método principal para impressão de todas as especialidades do paciente
     */
    public void imprimirDadosPaciente(Paciente paciente) {
        if (!Validator.validarPaciente(paciente)) {
            DialogHelper.mostrarAviso(parent, "Não há dados válidos de paciente para imprimir!");
            return;
        }

        try {
            List<PacienteEspecialidade> especialidadesPaciente = buscarEspecialidadesPaciente(paciente);
            if (especialidadesPaciente.isEmpty()) {
                DialogHelper.mostrarAviso(parent, "O paciente não possui especialidades associadas!");
                return;
            }

            List<GrupoImpressao> gruposImpressao = GrupoHelper.criarGruposImpressao(
                especialidadesPaciente, nomeParaIdCache, especialidadesCache
            );
            
            if (!DialogHelper.confirmarImpressaoMultipla(parent, gruposImpressao.size())) {
                return;
            }

            processarImpressaoGrupos(paciente, gruposImpressao);

        } catch (Exception ex) {
            DialogHelper.mostrarErro(parent, "Erro geral ao imprimir: " + ex.getMessage());
        }
    }
    
    /**
     * Método para imprimir uma especialidade específica
     */
    public void imprimirDadosPacienteEspecialidade(Paciente paciente, int especialidadeId) {
        if (!Validator.validarPacienteEspecialidade(paciente, especialidadeId)) {
            DialogHelper.mostrarErro(parent, "Dados inválidos para impressão!");
            return;
        }

        try {
            PacienteEspecialidade pe = new PacienteEspecialidade();
            pe.setPacienteId(paciente.getId());
            pe.setEspecialidadeId(especialidadeId);
            
            List<GrupoImpressao> grupos = GrupoHelper.criarGruposImpressao(
                Collections.singletonList(pe), nomeParaIdCache, especialidadesCache
            );
            
            if (grupos.isEmpty()) {
                DialogHelper.mostrarErro(parent, "Erro ao criar grupo de impressão!");
                return;
            }
            
            processarImpressaoGrupoUnico(paciente, grupos.get(0));

        } catch (Exception ex) {
            DialogHelper.mostrarErro(parent, "Erro ao imprimir: " + ex.getMessage());
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
            String nomeEspecialidade = especialidadesCache.get(especialidadeId);
            if (nomeEspecialidade == null) {
                return "Especialidade não encontrada";
            }

            Especialidade especialidade = especialidadeDAO.buscarPorId(especialidadeId);
            if (especialidade == null) {
                return "Erro ao buscar dados da especialidade";
            }

            return StatusHelper.formatarStatusAtendimento(especialidade);

        } catch (Exception ex) {
            return "Erro ao verificar status: " + ex.getMessage();
        }
    }
    
    // ========== MÉTODOS PRIVADOS DE PROCESSAMENTO ==========
    
    private List<PacienteEspecialidade> buscarEspecialidadesPaciente(Paciente paciente) {
        if (paciente.getId() <= 0 || pacienteEspecialidadeDAO == null) {
            return Collections.emptyList();
        }
        
        List<PacienteEspecialidade> especialidades = pacienteEspecialidadeDAO.buscarPorPacienteId(paciente.getId());
        return especialidades != null ? especialidades : Collections.emptyList();
    }
    
    private void processarImpressaoGrupos(Paciente paciente, List<GrupoImpressao> grupos) {
        ResultadoImpressao resultado = new ResultadoImpressao();
        
        for (GrupoImpressao grupo : grupos) {
            processarImpressaoGrupo(paciente, grupo, resultado);
        }
        
        DialogHelper.mostrarResumoImpressao(parent, resultado);
    }
    
    private void processarImpressaoGrupoUnico(Paciente paciente, GrupoImpressao grupo) {
        Map<Integer, String> numeracoes = new HashMap<>();
        
        if (!AtendimentoProcessor.processarNumeracoes(grupo, numeracoes, especialidadesCache, 
                                                      especialidadeDAO, parent)) {
            return; // Cancelado pelo usuário
        }

        boolean sucesso = ImpressaoExecutor.executarImpressao(paciente, grupo, numeracoes, especialidadesCache);

        if (sucesso) {
            String mensagem = String.format("Ficha impressa com sucesso!\n%s", 
                    grupo.obterDescricaoDetalhada(especialidadesCache, numeracoes));
            JOptionPane.showMessageDialog(parent, mensagem, "Impressão Realizada", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void processarImpressaoGrupo(Paciente paciente, GrupoImpressao grupo, ResultadoImpressao resultado) {
        try {
            Map<Integer, String> numeracoes = new HashMap<>();
            
            if (!AtendimentoProcessor.processarNumeracoes(grupo, numeracoes, especialidadesCache, 
                                                          especialidadeDAO, parent)) {
                String nomeGrupo = grupo.obterNomeGrupo(especialidadesCache);
                resultado.adicionarResultado(nomeGrupo, Constants.STATUS_CANCELADA, StatusImpressao.CANCELADA);
                return;
            }
            
            boolean impressaoSucesso = ImpressaoExecutor.executarImpressao(paciente, grupo, numeracoes, especialidadesCache);
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
            resultado.adicionarResultado(nomeGrupo, Constants.STATUS_ERRO + ": " + ex.getMessage(), StatusImpressao.ERRO);
        }
    }
    
    // ========== CLASSES HELPER ESTÁTICAS ==========
    
    /**
     * Helper para operações com especialidades
     */
    private static class EspecialidadeHelper {
        static Map<Integer, String> criarCacheEspecialidades(List<Especialidade> especialidades) {
            if (especialidades == null) {
                return new HashMap<>();
            }
            return especialidades.stream()
                    .collect(Collectors.toMap(Especialidade::getId, Especialidade::getNome));
        }
        
        static Map<String, Integer> criarCacheNomeParaId(List<Especialidade> especialidades) {
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
    }
    
    /**
     * Helper para validações
     */
    private static class Validator {
        static boolean validarPaciente(Paciente paciente) {
            return paciente != null && !isNullOrEmpty(paciente.getNome());
        }
        
        static boolean validarPacienteEspecialidade(Paciente paciente, int especialidadeId) {
            return paciente != null && especialidadeId > 0;
        }
        
        private static boolean isNullOrEmpty(String str) {
            return str == null || str.trim().isEmpty();
        }
    }
    
    /**
     * Helper para diálogos
     */
    private static class DialogHelper {
        static void mostrarAviso(Component parent, String mensagem) {
            JOptionPane.showMessageDialog(parent, mensagem, Constants.TITULO_AVISO, JOptionPane.WARNING_MESSAGE);
        }
        
        static void mostrarErro(Component parent, String mensagem) {
            JOptionPane.showMessageDialog(parent, mensagem, Constants.TITULO_ERRO, JOptionPane.ERROR_MESSAGE);
        }
        
        static boolean confirmarImpressaoMultipla(Component parent, int totalGrupos) {
            String mensagem = totalGrupos == 1 
                ? "Será impressa 1 ficha para o paciente.\nDeseja continuar?"
                : String.format("Serão impressas %d fichas para o paciente.\nDeseja continuar?", totalGrupos);
            
            return JOptionPane.showConfirmDialog(parent, mensagem, Constants.TITULO_CONFIRMACAO, 
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
        }
        
        static void mostrarResumoImpressao(Component parent, ResultadoImpressao resultado) {
            ResumoImpressao resumo = new ResumoImpressao(resultado);
            
            JOptionPane.showMessageDialog(parent, 
                    resumo.gerarMensagem(), 
                    resumo.obterTitulo(), 
                    resumo.obterTipoMensagem());
        }
    }
    
    /**
     * Helper para formatação de status
     */
    private static class StatusHelper {
        static String formatarStatusAtendimento(Especialidade especialidade) {
            return String.format("Especialidade: %s\nAtendimentos restantes: %d\nTotal do dia: %d\nPróximo atendimento seria: %s",
                    especialidade.getNome(),
                    especialidade.getAtendimentosRestantesHoje(),
                    especialidade.getAtendimentosTotaisHoje(),
                    especialidade.temAtendimentosDisponiveis() ? 
                        especialidade.formatarNumeracaoAtendimento() : Constants.STATUS_ESGOTADO);
        }
    }
    
    /**
     * Helper para criação de grupos de impressão
     */
    private static class GrupoHelper {
        static List<GrupoImpressao> criarGruposImpressao(List<PacienteEspecialidade> especialidadesPaciente,
                                                          Map<String, Integer> nomeParaIdCache,
                                                          Map<Integer, String> especialidadesCache) {
            List<GrupoImpressao> grupos = new ArrayList<>();
            Integer enfermagemId = nomeParaIdCache.get(Constants.ESPECIALIDADE_ENFERMAGEM);
            
            // Separar especialidades por tipo
            Map<String, List<PacienteEspecialidade>> especialidadesSeparadas = separarEspecialidades(
                especialidadesPaciente, especialidadesCache, enfermagemId
            );
            
            // Criar grupos para especialidades que devem incluir enfermagem
            for (PacienteEspecialidade pe : especialidadesSeparadas.get("comEnfermagem")) {
                grupos.add(criarGrupoComEnfermagem(pe, enfermagemId));
            }
            
            // Criar grupos para especialidades que não devem incluir enfermagem
            for (PacienteEspecialidade pe : especialidadesSeparadas.get("semEnfermagem")) {
                grupos.add(new GrupoImpressao(Collections.singletonList(pe), false));
            }
            
            // Se enfermagem foi selecionada sozinha
            PacienteEspecialidade enfermagem = especialidadesSeparadas.get("enfermagem").stream().findFirst().orElse(null);
            if (enfermagem != null && especialidadesSeparadas.get("comEnfermagem").isEmpty()) {
                grupos.add(new GrupoImpressao(Collections.singletonList(enfermagem), false));
            }
            
            return grupos;
        }
        
        private static Map<String, List<PacienteEspecialidade>> separarEspecialidades(
                List<PacienteEspecialidade> especialidadesPaciente,
                Map<Integer, String> especialidadesCache,
                Integer enfermagemId) {
            
            Map<String, List<PacienteEspecialidade>> resultado = Map.of(
                "semEnfermagem", new ArrayList<>(),
                "comEnfermagem", new ArrayList<>(),
                "enfermagem", new ArrayList<>()
            );
            
            for (PacienteEspecialidade pe : especialidadesPaciente) {
                String nomeEspecialidade = especialidadesCache.get(pe.getEspecialidadeId());
                
                if (Constants.ESPECIALIDADE_ENFERMAGEM.equalsIgnoreCase(nomeEspecialidade)) {
                    resultado.get("enfermagem").add(pe);
                } else if (nomeEspecialidade != null && 
                          Constants.ESPECIALIDADES_SEM_ENFERMAGEM.contains(nomeEspecialidade.toUpperCase())) {
                    resultado.get("semEnfermagem").add(pe);
                } else {
                    resultado.get("comEnfermagem").add(pe);
                }
            }
            
            return resultado;
        }
        
        private static GrupoImpressao criarGrupoComEnfermagem(PacienteEspecialidade pe, Integer enfermagemId) {
            List<PacienteEspecialidade> grupo = new ArrayList<>();
            grupo.add(pe);
            
            // Adicionar enfermagem se existir no sistema e não for a própria especialidade
            if (enfermagemId != null && pe.getEspecialidadeId() != enfermagemId.intValue()) {
                PacienteEspecialidade enfermagemParaGrupo = new PacienteEspecialidade();
                enfermagemParaGrupo.setPacienteId(pe.getPacienteId());
                enfermagemParaGrupo.setEspecialidadeId(enfermagemId);
                grupo.add(enfermagemParaGrupo);
            }
            
            return new GrupoImpressao(grupo, true);
        }
    }
    
    /**
     * Processador de atendimentos
     */
    private static class AtendimentoProcessor {
        static boolean processarNumeracoes(GrupoImpressao grupo, 
                                           Map<Integer, String> numeracoes,
                                           Map<Integer, String> especialidadesCache,
                                           EspecialidadeDAO especialidadeDAO,
                                           Component parent) {
            
            for (PacienteEspecialidade pe : grupo.getEspecialidades()) {
                String nomeEspecialidade = especialidadesCache.get(pe.getEspecialidadeId());
                
                // Para enfermagem adicionada automaticamente
                if (grupo.isTemEnfermagemAutomatica() && 
                    Constants.ESPECIALIDADE_ENFERMAGEM.equalsIgnoreCase(nomeEspecialidade) && 
                    !grupo.isEspecialidadePrincipal(pe)) {
                    numeracoes.put(pe.getEspecialidadeId(), Constants.STATUS_ACOMPANHAMENTO);
                    continue;
                }
                
                StatusAtendimento status = verificarStatusAtendimento(pe.getEspecialidadeId(), 
                                                                     nomeEspecialidade, 
                                                                     especialidadeDAO, 
                                                                     parent);
                
                if (status.isCancelado()) {
                    return false;
                }
                
                numeracoes.put(pe.getEspecialidadeId(), status.getNumeracao());
            }
            
            return true;
        }
        
        private static StatusAtendimento verificarStatusAtendimento(int especialidadeId, 
                                                                   String nomeEspecialidade,
                                                                   EspecialidadeDAO especialidadeDAO,
                                                                   Component parent) {
            boolean temAtendimentos = especialidadeDAO.temAtendimentosDisponiveis(especialidadeId);
            String numeracao = Constants.STATUS_ESGOTADO;
            
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
    }
    
    /**
     * Executor de impressão
     */
    private static class ImpressaoExecutor {
        static boolean executarImpressao(Paciente paciente, 
                                        GrupoImpressao grupo, 
                                        Map<Integer, String> numeracoes,
                                        Map<Integer, String> especialidadesCache) {
            try {
                PrinterJob printJob = PrinterJob.getPrinterJob();
                
                FichaPacientePrintable fichaPrintable = new FichaPacientePrintable(
                    paciente, grupo.getEspecialidades(), numeracoes, especialidadesCache
                );
                
                printJob.setPrintable(fichaPrintable);
                printJob.print();
                
                return true;
            } catch (PrinterException ex) {
                throw new RuntimeException("Erro ao executar impressão: " + ex.getMessage(), ex);
            }
        }
    }
    
    // ========== CLASSES DE DADOS ==========
    
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
            this.especialidadePrincipal = especialidades.get(0);
        }
        
        public List<PacienteEspecialidade> getEspecialidades() {
            return Collections.unmodifiableList(especialidades);
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
            return (int) detalhes.values().stream().filter(Constants.STATUS_CANCELADA::equals).count();
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
            adicionarCabecalho(resumo);
            
            // Estatísticas
            adicionarEstatisticas(resumo);
            
            // Detalhes por especialidade
            adicionarDetalhes(resumo);
            
            return resumo.toString();
        }
        
        private void adicionarCabecalho(StringBuilder resumo) {
            if (resultado.getFichasImpressas() > 0 && resultado.getFichasComErro() == 0) {
                resumo.append("✅ Todas as fichas foram impressas com sucesso!\n\n");
            } else if (resultado.getFichasImpressas() > 0 && resultado.getFichasComErro() > 0) {
                resumo.append("⚠️ Impressão parcialmente concluída!\n\n");
            } else {
                resumo.append("❌ Nenhuma ficha foi impressa!\n\n");
            }
        }
        
        private void adicionarEstatisticas(StringBuilder resumo) {
            resumo.append("📊 RESUMO:\n");
            resumo.append("• Fichas impressas: ").append(resultado.getFichasImpressas()).append("\n");
            resumo.append("• Fichas com erro: ").append(resultado.getFichasComErro()).append("\n");
            
            int canceladas = resultado.getCanceladas();
            if (canceladas > 0) {
                resumo.append("• Fichas canceladas: ").append(canceladas).append("\n");
            }
            resumo.append("\n");
        }
        
        private void adicionarDetalhes(StringBuilder resumo) {
            resumo.append("📋 DETALHES POR FICHA:\n");
            for (Map.Entry<String, String> entry : resultado.getDetalhes().entrySet()) {
                StatusImpressao status = resultado.getStatus().get(entry.getKey());
                resumo.append(status.getEmoji()).append(" ")
                      .append(entry.getKey()).append(": ")
                      .append(entry.getValue()).append("\n");
            }
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
     * Classe Printable personalizada para criar o layout da ficha do paciente
     */
    private static class FichaPacientePrintable implements Printable {
        private final Paciente paciente;
        private final List<PacienteEspecialidade> especialidades;
        private final Map<Integer, String> numeracoes;
        private final Map<Integer, String> especialidadesCache;
        
        public FichaPacientePrintable(Paciente paciente, 
                                    List<PacienteEspecialidade> especialidades,
                                    Map<Integer, String> numeracoes,
                                    Map<Integer, String> especialidadesCache) {
            this.paciente = paciente;
            this.especialidades = especialidades;
            this.numeracoes = numeracoes;
            this.especialidadesCache = especialidadesCache;
        }
        
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }
            
            Graphics2D g2d = (Graphics2D) graphics;
            LayoutManager layout = new LayoutManager(pageFormat);
            
            int posY = layout.getInicioY();
            
            // Renderizar seções do documento
            posY = SectionRenderer.renderCabecalho(g2d, layout, posY);
            posY = SectionRenderer.renderDadosPessoais(g2d, layout, posY, paciente);
            posY = SectionRenderer.renderEspecialidades(g2d, layout, posY, especialidades, numeracoes, especialidadesCache);
            posY = SectionRenderer.renderParametrosClinicos(g2d, layout, posY, paciente);
            posY = SectionRenderer.renderPatologias(g2d, layout, posY);
            SectionRenderer.renderAvaliacaoMedica(g2d, layout, posY);
            
            return PAGE_EXISTS;
        }
    }
    
    // ========== CLASSES DE LAYOUT E RENDERIZAÇÃO ==========
    
    /**
     * Gerenciador de layout da página
     */
    private static class LayoutManager {
        private final int x;
        private final int y;
        private final int largura;
        private final int altura;
        private final int alturaMaxima;
        
        public LayoutManager(PageFormat pageFormat) {
            this.x = (int) pageFormat.getImageableX() + 10;
            this.y = (int) pageFormat.getImageableY() + 10;
            this.largura = (int) pageFormat.getImageableWidth() - 20;
            this.altura = (int) pageFormat.getImageableHeight() - 20;
            this.alturaMaxima = y + altura;
        }
        
        public int getX() { return x; }
        public int getInicioY() { return y + 10; }
        public int getLargura() { return largura; }
        public int getAlturaMaxima() { return alturaMaxima; }
        
        public int calcularColuna(int numeroColuna, int totalColunas) {
            return x + (largura * numeroColuna / totalColunas);
        }
    }
    
    /**
     * Renderizador de seções do documento
     */
    private static class SectionRenderer {
        
        static int renderCabecalho(Graphics2D g2d, LayoutManager layout, int posY) {
            // Logo
            posY = renderLogo(g2d, layout.getX(), posY);
            
            // Textos do cabeçalho
            g2d.setFont(Constants.FONTE_TITULO);
            g2d.drawString("CENTRO DE SAÚDE COMUNITÁRIA", layout.getX() + 55, posY - 30);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("FICHA DO PACIENTE", layout.getX() + 55, posY - 13);
            
            // Data no canto direito
            renderDataAtual(g2d, layout);
            
            // Linha divisória
            g2d.drawLine(layout.getX(), posY, layout.getX() + layout.getLargura(), posY);
            
            return posY + 8;
        }
        
        private static int renderLogo(Graphics2D g2d, int x, int y) {
            try {
                File logoFile = new File(Constants.CAMINHO_LOGO);
                if (logoFile.exists()) {
                    Image logo = ImageIO.read(logoFile);
                    g2d.drawImage(logo, x, y, 45, 45, null);
                }
            } catch (IOException e) {
                g2d.drawRect(x, y, 45, 45);
                g2d.setFont(Constants.FONTE_PEQUENA);
                g2d.drawString("LOGO", x + 10, y + 25);
            }
            return y + 55;
        }
        
        private static void renderDataAtual(Graphics2D g2d, LayoutManager layout) {
            g2d.setFont(Constants.FONTE_PEQUENA);
            String dataAtual = "Data: " + Constants.FORMATO_DATA.format(new Date());
            FontMetrics fm = g2d.getFontMetrics();
            int larguraTexto = fm.stringWidth(dataAtual);
            g2d.drawString(dataAtual, layout.getX() + layout.getLargura() - larguraTexto, layout.getInicioY() + 5);
        }
        
        static int renderDadosPessoais(Graphics2D g2d, LayoutManager layout, int posY, Paciente paciente) {
            posY = renderTituloSecao(g2d, layout, posY, "DADOS PESSOAIS");
            
            g2d.setFont(Constants.FONTE_NORMAL);
            FontMetrics fm = g2d.getFontMetrics();
            int alturaLinha = fm.getHeight() + 2;
            
            // Renderizar campos em layout otimizado
            posY = renderCamposPessoais(g2d, layout, posY, alturaLinha, paciente);
            
            return posY + 8;
        }
        
        private static int renderCamposPessoais(Graphics2D g2d, LayoutManager layout, int posY, int alturaLinha, Paciente paciente) {
            int col1 = layout.getX();
            int col2 = layout.calcularColuna(2, 3);
            
            // Nome (linha completa)
            String nome = "Nome: " + StringHelper.orDefault(paciente.getNome(), "");
            g2d.drawString(nome, col1, posY);
            if (StringHelper.isEmpty(paciente.getNome())) {
                g2d.drawLine(col1 + 40, posY, layout.getX() + layout.getLargura(), posY);
            }
            posY += alturaLinha;
            
            // Data nascimento e Idade
            String dataNasc = "Nasc.: " + StringHelper.orDefault(paciente.getDataNascimento(), "__/__/____");
            g2d.drawString(dataNasc, col1, posY);
            
            String idade = "Idade: " + (paciente.getIdade() != null ? paciente.getIdade() + "a" : "___a");
            g2d.drawString(idade, col1 + 150, posY);
            posY += alturaLinha;
            
            // CPF e SUS
            String cpf = "CPF: " + StringHelper.orDefault(paciente.getCpf(), "___.___.___-__");
            g2d.drawString(cpf, col1, posY);
            
            String sus = "SUS: " + StringHelper.orDefault(paciente.getSus(), "_____________");
            g2d.drawString(sus, col1 + 150, posY);
            posY += alturaLinha;
            
            // Mãe (linha completa)
            String mae = "Mãe: " + StringHelper.orDefault(paciente.getNomeDaMae(), "");
            g2d.drawString(mae, col1, posY);
            if (StringHelper.isEmpty(paciente.getNomeDaMae())) {
                g2d.drawLine(col1 + 35, posY, layout.getX() + layout.getLargura(), posY);
            }
            posY += alturaLinha;
            
            // Telefone e Endereço
            String telefone = "Tel: " + StringHelper.orDefault(paciente.getTelefone(), "(__) _____-____");
            g2d.drawString(telefone, col1, posY);
            
            String endereco = "End: " + StringHelper.orDefault(paciente.getEndereco(), "");
            g2d.drawString(endereco, col1 + 150, posY);
            if (StringHelper.isEmpty(paciente.getEndereco())) {
                g2d.drawLine(col1 + 180, posY, layout.getX() + layout.getLargura(), posY);
            }
            posY += alturaLinha;
            
            return posY;
        }
        
        static int renderEspecialidades(Graphics2D g2d, LayoutManager layout, int posY, 
                                       List<PacienteEspecialidade> especialidades,
                                       Map<Integer, String> numeracoes,
                                       Map<Integer, String> especialidadesCache) {
            posY = renderTituloSecao(g2d, layout, posY, "ESPECIALIDADES");
            
            g2d.setFont(Constants.FONTE_NORMAL);
            FontMetrics fm = g2d.getFontMetrics();
            int alturaLinha = fm.getHeight() + 2;
            
            for (PacienteEspecialidade pe : especialidades) {
                String nomeEspecialidade = especialidadesCache.get(pe.getEspecialidadeId());
                if (nomeEspecialidade != null) {
                    String linha = formatarLinhaEspecialidade(nomeEspecialidade, numeracoes.get(pe.getEspecialidadeId()));
                    g2d.drawString(linha, layout.getX(), posY);
                    posY += alturaLinha;
                }
            }
            
            return posY + 8;
        }
        
        private static String formatarLinhaEspecialidade(String nomeEspecialidade, String numeracao) {
            String linha = "• " + nomeEspecialidade;
            
            if (numeracao != null) {
                if (Constants.STATUS_ESGOTADO.equals(numeracao)) {
                    linha += " - ESGOTADO";
                } else if (Constants.STATUS_ACOMPANHAMENTO.equals(numeracao)) {
                    linha += " - ACOMPANHAMENTO";
                } else {
                    linha += " - Nº " + numeracao;
                }
            }
            
            return linha;
        }
        
        static int renderParametrosClinicos(Graphics2D g2d, LayoutManager layout, int posY, Paciente paciente) {
            posY = renderTituloSecao(g2d, layout, posY, "PARÂMETROS CLÍNICOS");
            
            g2d.setFont(Constants.FONTE_NORMAL);
            FontMetrics fm = g2d.getFontMetrics();
            int alturaLinha = fm.getHeight() + 3;
            
            // Layout em 4 colunas
            int[] colunas = {
                layout.getX(),
                layout.calcularColuna(1, 4),
                layout.calcularColuna(2, 4),
                layout.calcularColuna(3, 4)
            };
            
            // Primeira linha de parâmetros
            String[] linha1 = {
                ParametroHelper.formatarPeso(paciente.getPeso()),
                ParametroHelper.formatarAltura(paciente.getAltura()),
                ParametroHelper.formatarPA(paciente.getPaXMmhg()),
                ParametroHelper.formatarFC(paciente.getFcBpm())
            };
            
            for (int i = 0; i < linha1.length; i++) {
                g2d.drawString(linha1[i], colunas[i], posY);
            }
            posY += alturaLinha;
            
            // Segunda linha de parâmetros
            String[] linha2 = {
                ParametroHelper.formatarSPO2(paciente.getSpo2()),
                ParametroHelper.formatarHGT(paciente.getHgtMgld()),
                ParametroHelper.formatarTemperatura(paciente.getTemperaturaC()),
                ParametroHelper.formatarFR(paciente.getFrIbpm())
            };
            
            for (int i = 0; i < linha2.length; i++) {
                g2d.drawString(linha2[i], colunas[i], posY);
            }
            
            return posY + alturaLinha + 8;
        }
        
        static int renderPatologias(Graphics2D g2d, LayoutManager layout, int posY) {
            posY = renderTituloSecao(g2d, layout, posY, "PATOLOGIAS");
            
            g2d.setFont(Constants.FONTE_NORMAL);
            FontMetrics fm = g2d.getFontMetrics();
            int alturaLinha = fm.getHeight() + 3;
            
            // Layout em 3 colunas
            int[] colunas = {
                layout.getX(),
                layout.calcularColuna(1, 3),
                layout.calcularColuna(2, 3)
            };
            
            // Primeira linha
            String[] linha1 = {"HAS ( )", "DM ( )", "Asma ( )"};
            for (int i = 0; i < linha1.length; i++) {
                g2d.drawString(linha1[i], colunas[i], posY);
            }
            posY += alturaLinha;
            
            // Segunda linha
            String[] linha2 = {"Alergia ( )", "D. Virais ( )", "D. Respiratórias ( )"};
            for (int i = 0; i < linha2.length; i++) {
                g2d.drawString(linha2[i], colunas[i], posY);
            }
            
            return posY + alturaLinha + 8;
        }
        
        static int renderAvaliacaoMedica(Graphics2D g2d, LayoutManager layout, int posY) {
            renderTituloSecao(g2d, layout, posY, "AVALIAÇÃO MÉDICA");
            posY += 23; // Espaço após título
            
            // Calcular número de linhas disponíveis
            int espacoRestante = layout.getAlturaMaxima() - posY - 15;
            int alturaLinha = 16;
            int numeroLinhas = Math.max(12, espacoRestante / alturaLinha);
            
            // Desenhar linhas para preenchimento manual
            for (int i = 0; i < numeroLinhas; i++) {
                g2d.drawLine(layout.getX(), posY, layout.getX() + layout.getLargura(), posY);
                posY += alturaLinha;
            }
            
            return posY;
        }
        
        private static int renderTituloSecao(Graphics2D g2d, LayoutManager layout, int posY, String titulo) {
            g2d.setFont(Constants.FONTE_SECAO);
            g2d.drawString(titulo, layout.getX(), posY);
            posY += 15;
            
            g2d.drawLine(layout.getX(), posY, layout.getX() + layout.getLargura(), posY);
            return posY + 8;
        }
    }
    
    // ========== CLASSES UTILITÁRIAS ==========
    
    /**
     * Helper para manipulação de strings
     */
    private static class StringHelper {
        static String orDefault(String value, String defaultValue) {
            return isEmpty(value) ? defaultValue : value;
        }
        
        static boolean isEmpty(String str) {
            return str == null || str.trim().isEmpty();
        }
    }
    
    /**
     * Helper para formatação de parâmetros clínicos
     */
    private static class ParametroHelper {
        static String formatarPeso(Float peso) {
            return "Peso: " + (peso != null && peso > 0 ? 
                String.format("%.1fkg", peso) : "___kg");
        }
        
        static String formatarAltura(Float altura) {
            return "Alt: " + (altura != null && altura > 0 ? 
                String.format("%.2fm", altura) : "___m");
        }
        
        static String formatarPA(String pa) {
            return "PA: " + (!StringHelper.isEmpty(pa) ? pa : "___x___");
        }
        
        static String formatarFC(Float fc) {
            return "FC: " + (fc != null && fc > 0 ? fc + "bpm" : "___bpm");
        }
        
        static String formatarSPO2(Float spo2) {
            return "SPO2: " + (spo2 != null && spo2 > 0 ? 
                String.format("%.1f%%", spo2) : "___%");
        }
        
        static String formatarHGT(Float hgt) {
            return "HGT: " + (hgt != null && hgt > 0 ? hgt + "mg/dL" : "___mg/dL");
        }
        
        static String formatarTemperatura(Float temp) {
            return "T: " + (temp != null && temp > 0 ? 
                String.format("%.1f°C", temp) : "___°C");
        }
        
        static String formatarFR(Float fr) {
            return "FR: " + (fr != null && fr > 0 ? fr + "rpm" : "___rpm");
        }
    }
}