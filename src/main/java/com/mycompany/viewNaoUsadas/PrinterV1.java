package com.mycompany.viewNaoUsadas;

// Imports específicos - escolha qual List você quer usar por padrão
import com.mycompany.printer.*;
import java.util.List; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Optional;
import java.util.Date;
import java.util.stream.Collectors;

// Para iText, use nome totalmente qualificado quando necessário
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.io.font.constants.StandardFonts;

// Seus imports de modelo
import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.mycompany.model.dao.EspecialidadeDAO;
import com.mycompany.model.dao.PacienteEspecialidadeDAO;

// Imports do AWT/Swing
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

/**
 * Sistema de impressão de fichas médicas em PDF
 * 
 * Responsabilidades:
 * - Gerenciar impressão de fichas de pacientes
 * - Aplicar regras de negócio para especialidades
 * - Gerar PDFs com layout profissional
 * - Controlar numeração de atendimentos
 * 
 * @author vitor
 * @version 2.0
 */
public class PrinterV1 {
    
    // ===== CONFIGURAÇÕES E CONSTANTES =====
    
    private static final PrinterConfig CONFIG = new PrinterConfig();
    private static final PDFStyle PDF_STYLE = new PDFStyle();
    
    // ===== DEPENDÊNCIAS =====
    
    private final Component parent;
    private final PacienteEspecialidadeDAO pacienteEspecialidadeDAO;
    private final EspecialidadeDAO especialidadeDAO;
    private final EspecialidadeCache especialidadeCache;
    private final UserInterface userInterface;
    private final PDFGenerator pdfGenerator;
    private final AtendimentoManager atendimentoManager;

    // ===== CONSTRUTOR =====
    
    public PrinterV1(Component parent, 
                   PacienteEspecialidadeDAO pacienteEspecialidadeDAO, 
                   EspecialidadeDAO especialidadeDAO, 
                   List<Especialidade> especialidades) {
        
        this.parent = parent;
        this.pacienteEspecialidadeDAO = pacienteEspecialidadeDAO;
        this.especialidadeDAO = especialidadeDAO;
        this.especialidadeCache = new EspecialidadeCache(especialidades);
        this.userInterface = new UserInterface(parent);
        this.pdfGenerator = new PDFGenerator();
        this.atendimentoManager = new AtendimentoManager(especialidadeDAO, userInterface);
    }
    
    // ===== MÉTODOS PÚBLICOS PRINCIPAIS =====
    
    /**
     * Imprime todas as fichas necessárias para um paciente
     */
    public void imprimirDadosPaciente(Paciente paciente) {
        try {
            // Validação inicial
            var validacao = ValidationHelper.validarPaciente(paciente);
            if (!validacao.isValido()) {
                userInterface.mostrarAviso(validacao.getMensagem());
                return;
            }

            // Buscar especialidades do paciente
            var especialidadesPaciente = buscarEspecialidadesPaciente(paciente);
            if (especialidadesPaciente.isEmpty()) {
                userInterface.mostrarAviso("O paciente não possui especialidades associadas!");
                return;
            }

            // Criar grupos de impressão
            var gruposImpressao = GrupoImpressaoFactory.criarGrupos(
                especialidadesPaciente, 
                especialidadeCache
            );
            
            if (!userInterface.confirmarImpressaoMultipla(gruposImpressao.size())) {
                return;
            }

            // Processar impressão
            var processador = new ProcessadorImpressao(
                atendimentoManager, 
                pdfGenerator, 
                especialidadeCache,
                userInterface
            );
            
            var resultado = processador.processar(paciente, gruposImpressao);
            userInterface.mostrarResumoImpressao(resultado);

        } catch (Exception ex) {
            userInterface.mostrarErro("Erro geral ao imprimir: " + ex.getMessage());
        }
    }
    
    /**
     * Imprime ficha para uma especialidade específica
     */
    public void imprimirDadosPacienteEspecialidade(Paciente paciente, int especialidadeId) {
        try {
            // Validação
            var validacao = ValidationHelper.validarPacienteEspecialidade(paciente, especialidadeId);
            if (!validacao.isValido()) {
                userInterface.mostrarErro(validacao.getMensagem());
                return;
            }

            // Criar grupo único
            var pe = PacienteEspecialidadeBuilder.criar(paciente.getId(), especialidadeId);
            var grupos = GrupoImpressaoFactory.criarGrupos(
                Collections.singletonList(pe), 
                especialidadeCache
            );
            
            if (grupos.isEmpty()) {
                userInterface.mostrarErro("Erro ao criar grupo de impressão!");
                return;
            }
            
            // Processar impressão
            var processador = new ProcessadorImpressao(
                atendimentoManager, 
                pdfGenerator, 
                especialidadeCache,
                userInterface
            );
            
            var resultado = processador.processar(paciente, grupos);
            
            if (resultado.temSucesso()) {
                var grupo = grupos.get(0);
                var mensagem = String.format(
                    "PDF gerado com sucesso!\n%s\n\nArquivo salvo na área de trabalho.",
                    grupo.obterDescricaoDetalhada(especialidadeCache, Collections.emptyMap())
                );
                userInterface.mostrarSucesso(mensagem);
            }

        } catch (Exception ex) {
            userInterface.mostrarErro("Erro ao gerar PDF: " + ex.getMessage());
        }
    }
    
    /**
     * Verifica status dos atendimentos de uma especialidade
     */
    public String verificarStatusAtendimentos(int especialidadeId) {
        try {
            return atendimentoManager.obterStatusDetalhado(especialidadeId, especialidadeCache);
        } catch (Exception ex) {
            return "Erro ao verificar status: " + ex.getMessage();
        }
    }
    
    // ===== MÉTODOS PRIVADOS =====
    
    private List<PacienteEspecialidade> buscarEspecialidadesPaciente(Paciente paciente) {
        if (paciente.getId() <= 0 || pacienteEspecialidadeDAO == null) {
            return Collections.emptyList();
        }
        
        var especialidades = pacienteEspecialidadeDAO.buscarPorPacienteId(paciente.getId());
        return especialidades != null ? especialidades : Collections.emptyList();
    }
    
    // ===== CLASSES DE CONFIGURAÇÃO =====
    
    /**
     * Configurações gerais do sistema de impressão
     */
    private static class PrinterConfig {
        public static final String TITULO_AVISO = "Aviso";
        public static final String TITULO_CONFIRMACAO = "Confirmar Impressão";
        public static final String TITULO_ERRO = "Erro de Impressão";
        public static final String STATUS_ESGOTADO = "ESGOTADO";
        public static final String STATUS_CANCELADA = "CANCELADA";
        public static final String STATUS_ERRO = "ERRO";
        public static final String ESPECIALIDADE_ENFERMAGEM = "ENFERMAGEM";
        
        public static final Set<String> ESPECIALIDADES_SEM_ENFERMAGEM = Set.of(
            "DENTISTA", "PSICOLOGIA", "TERAPEUTA"
        );
        
        public static final SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy");
        public static final SimpleDateFormat FORMATO_DATA_HORA = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        public static final String DIRETORIO_PDFS = System.getProperty("user.home") + 
            "/Área de trabalho/projeto_IBG/pdfs/";
    }
    
    /**
     * Estilos para geração de PDF
     */
    private static class PDFStyle {
        public static final DeviceRgb COR_CABECALHO = new DeviceRgb(41, 128, 185);
        public static final DeviceRgb COR_SECAO = new DeviceRgb(52, 73, 94);
        public static final DeviceRgb COR_TEXTO_CLARO = new DeviceRgb(127, 140, 141);
        public static final DeviceRgb COR_BORDA = new DeviceRgb(189, 195, 199);
        
        public static final int TAMANHO_FONTE_TITULO = 20;
        public static final int TAMANHO_FONTE_SECAO = 14;
        public static final int TAMANHO_FONTE_NORMAL = 11;
        public static final int TAMANHO_FONTE_PEQUENA = 10;
        public static final int TAMANHO_FONTE_RODAPE = 9;
        
        public static final int LINHAS_RELATORIO = 15;
        public static final int MARGEM_PAGINA = 40;
        public static final int PADDING_CABECALHO = 15;
        public static final int PADDING_CELULA = 8;
    }
    
    // ===== CLASSES DE NEGÓCIO =====
    
    /**
     * Cache otimizado para especialidades
     */
    private static class EspecialidadeCache {
        private final Map<Integer, String> idParaNome;
        private final Map<String, Integer> nomeParaId;
        
        public EspecialidadeCache(List<Especialidade> especialidades) {
            this.idParaNome = criarCacheIdParaNome(especialidades);
            this.nomeParaId = criarCacheNomeParaId(especialidades);
        }
        
        private Map<Integer, String> criarCacheIdParaNome(List<Especialidade> especialidades) {
            return Optional.ofNullable(especialidades)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(
                    Especialidade::getId, 
                    Especialidade::getNome,
                    (existing, replacement) -> existing
                ));
        }
        
        private Map<String, Integer> criarCacheNomeParaId(List<Especialidade> especialidades) {
            return Optional.ofNullable(especialidades)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(
                    esp -> esp.getNome().toUpperCase(), 
                    Especialidade::getId,
                    (existing, replacement) -> existing
                ));
        }
        
        public String obterNome(int id) {
            return idParaNome.get(id);
        }
        
        public Integer obterIdPorNome(String nome) {
            return nomeParaId.get(nome.toUpperCase());
        }
        
        public Map<Integer, String> obterCacheCompleto() {
            return Collections.unmodifiableMap(idParaNome);
        }
    }
    
    /**
     * Gerenciador de atendimentos e numerações
     */
    private static class AtendimentoManager {
        private final EspecialidadeDAO especialidadeDAO;
        private final UserInterface userInterface;
        
        public AtendimentoManager(EspecialidadeDAO especialidadeDAO, UserInterface userInterface) {
            this.especialidadeDAO = especialidadeDAO;
            this.userInterface = userInterface;
        }
        
        public StatusAtendimento verificarStatusAtendimento(int especialidadeId, String nomeEspecialidade) {
            boolean temAtendimentos = especialidadeDAO.temAtendimentosDisponiveis(especialidadeId);
            String numeracao = PrinterConfig.STATUS_ESGOTADO;
            
            if (!temAtendimentos) {
                if (!userInterface.confirmarImpressaoEsgotada(nomeEspecialidade)) {
                    return StatusAtendimento.cancelado();
                }
            } else {
                numeracao = especialidadeDAO.obterNumeracaoProximoAtendimento(especialidadeId);
                if (numeracao != null) {
                    especialidadeDAO.reduzirAtendimentoRestante(especialidadeId);
                }
            }
            
            return StatusAtendimento.sucesso(numeracao);
        }
        
        public String obterStatusDetalhado(int especialidadeId, EspecialidadeCache cache) {
            if (especialidadeId <= 0) {
                return "ID inválido";
            }
            
            String nomeEspecialidade = cache.obterNome(especialidadeId);
            if (nomeEspecialidade == null) {
                return "Especialidade não encontrada";
            }
            
            Especialidade especialidade = especialidadeDAO.buscarPorId(especialidadeId);
            if (especialidade == null) {
                return "Erro ao buscar dados da especialidade";
            }
            
            return String.format(
                "Especialidade: %s\nAtendimentos restantes: %d\nTotal do dia: %d\nPróximo atendimento seria: %s",
                especialidade.getNome(),
                especialidade.getAtendimentosRestantesHoje(),
                especialidade.getAtendimentosTotaisHoje(),
                especialidade.temAtendimentosDisponiveis() ? 
                    especialidade.formatarNumeracaoAtendimento() : PrinterConfig.STATUS_ESGOTADO
            );
        }
    }
    
    /**
     * Interface unificada para interação com usuário
     */
    private static class UserInterface {
        private final Component parent;
        
        public UserInterface(Component parent) {
            this.parent = parent;
        }
        
        public void mostrarAviso(String mensagem) {
            JOptionPane.showMessageDialog(parent, mensagem, 
                PrinterConfig.TITULO_AVISO, JOptionPane.WARNING_MESSAGE);
        }
        
        public void mostrarErro(String mensagem) {
            JOptionPane.showMessageDialog(parent, mensagem, 
                PrinterConfig.TITULO_ERRO, JOptionPane.ERROR_MESSAGE);
        }
        
        public void mostrarSucesso(String mensagem) {
            JOptionPane.showMessageDialog(parent, mensagem, 
                "PDF Gerado", JOptionPane.INFORMATION_MESSAGE);
        }
        
        public boolean confirmarImpressaoMultipla(int totalGrupos) {
            String mensagem = totalGrupos == 1 
                ? "Será impressa 1 ficha para o paciente.\nDeseja continuar?"
                : String.format("Serão impressas %d fichas para o paciente.\nDeseja continuar?", totalGrupos);
            
            return JOptionPane.showConfirmDialog(parent, mensagem, 
                PrinterConfig.TITULO_CONFIRMACAO, JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
        }
        
        public boolean confirmarImpressaoEsgotada(String nomeEspecialidade) {
            String mensagem = String.format(
                "A especialidade '%s' não possui mais atendimentos disponíveis hoje.\n" +
                "Deseja imprimir esta ficha mesmo assim?", nomeEspecialidade);
            
            return JOptionPane.showConfirmDialog(parent, mensagem, 
                "Atendimentos Esgotados - " + nomeEspecialidade, 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
        }
        
        public void mostrarResumoImpressao(ResultadoImpressao resultado) {
            var resumo = new ResumoImpressao(resultado);
            JOptionPane.showMessageDialog(parent, 
                resumo.gerarMensagem(), 
                resumo.obterTitulo(), 
                resumo.obterTipoMensagem());
        }
    }
    
    // ===== CLASSES DE DADOS =====
    
    /**
     * Status de um atendimento
     */
    private static class StatusAtendimento {
        private final String numeracao;
        private final boolean cancelado;
        
        private StatusAtendimento(String numeracao, boolean cancelado) {
            this.numeracao = numeracao;
            this.cancelado = cancelado;
        }
        
        public static StatusAtendimento sucesso(String numeracao) {
            return new StatusAtendimento(numeracao, false);
        }
        
        public static StatusAtendimento cancelado() {
            return new StatusAtendimento(null, true);
        }
        
        public String getNumeracao() { return numeracao; }
        public boolean isCancelado() { return cancelado; }
    }
    
    /**
     * Representa um grupo de especialidades para impressão conjunta
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
        
        // Getters e métodos utilitários
        public List<PacienteEspecialidade> getEspecialidades() { return especialidades; }
        public boolean isTemEnfermagemAutomatica() { return temEnfermagemAutomatica; }
        
        public boolean isEspecialidadePrincipal(PacienteEspecialidade pe) {
            return pe.getEspecialidadeId() == especialidadePrincipal.getEspecialidadeId();
        }
        
        public String obterNomeGrupo(EspecialidadeCache cache) {
            if (especialidades.size() == 1) {
                return cache.obterNome(especialidades.get(0).getEspecialidadeId());
            } else {
                String principal = cache.obterNome(especialidadePrincipal.getEspecialidadeId());
                return principal + " + Enfermagem";
            }
        }
        
        public String obterDescricaoDetalhada(EspecialidadeCache cache, Map<Integer, String> numeracoes) {
            return especialidades.stream()
                .map(pe -> {
                    String nome = cache.obterNome(pe.getEspecialidadeId());
                    String numeracao = numeracoes.get(pe.getEspecialidadeId());
                    return "• " + nome + (numeracao != null ? " - " + numeracao : "");
                })
                .collect(Collectors.joining("\n"));
        }
    }
    
    /**
     * Resultado de uma operação de impressão
     */
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
        
        public boolean temSucesso() { return fichasImpressas > 0; }
        
        // Getters
        public int getFichasImpressas() { return fichasImpressas; }
        public int getFichasComErro() { return fichasComErro; }
        public Map<String, String> getDetalhes() { return detalhes; }
        public Map<String, StatusImpressao> getStatus() { return status; }
        
        public int getCanceladas() {
            return (int) detalhes.values().stream()
                .filter(PrinterConfig.STATUS_CANCELADA::equals)
                .count();
        }
    }
    
    /**
     * Status de impressão com representação visual
     */
    public enum StatusImpressao {
        SUCESSO("✅"),
        ERRO("❌"),
        CANCELADA("🚫"),
        ESGOTADO("⚠️");
        
        private final String emoji;
        
        StatusImpressao(String emoji) {
            this.emoji = emoji;
        }
        
        public String getEmoji() { return emoji; }
    }
    
    // ===== FACTORIES E PROCESSADORES =====
    
    /**
     * Factory para criação de grupos de impressão
     */
    private static class GrupoImpressaoFactory {
        
        public static List<GrupoImpressao> criarGrupos(List<PacienteEspecialidade> especialidadesPaciente, 
                                                      EspecialidadeCache cache) {
            List<GrupoImpressao> grupos = new ArrayList<>();
            Integer enfermagemId = cache.obterIdPorNome(PrinterConfig.ESPECIALIDADE_ENFERMAGEM);
            
            // Separar especialidades por tipo
            var classificacao = classificarEspecialidades(especialidadesPaciente, cache, enfermagemId);
            
            // Criar grupos para especialidades que devem incluir enfermagem
            for (PacienteEspecialidade pe : classificacao.especialidadesComEnfermagem) {
                List<PacienteEspecialidade> grupo = new ArrayList<>();
                grupo.add(pe);
                
                // Adicionar enfermagem se existir e não for a própria especialidade
                if (enfermagemId != null && pe.getEspecialidadeId() != enfermagemId.intValue()) {
                    grupo.add(PacienteEspecialidadeBuilder.criar(pe.getPacienteId(), enfermagemId));
                }
                
                grupos.add(new GrupoImpressao(grupo, true));
            }
            
            // Criar grupos para especialidades independentes
            for (PacienteEspecialidade pe : classificacao.especialidadesSemEnfermagem) {
                grupos.add(new GrupoImpressao(Collections.singletonList(pe), false));
            }
            
            // Enfermagem sozinha, se necessário
            if (classificacao.enfermagem != null && classificacao.especialidadesComEnfermagem.isEmpty()) {
                grupos.add(new GrupoImpressao(Collections.singletonList(classificacao.enfermagem), false));
            }
            
            return grupos;
        }
        
        private static ClassificacaoEspecialidades classificarEspecialidades(
                List<PacienteEspecialidade> especialidades, 
                EspecialidadeCache cache,
                Integer enfermagemId) {
            
            var classificacao = new ClassificacaoEspecialidades();
            
            for (PacienteEspecialidade pe : especialidades) {
                String nomeEspecialidade = cache.obterNome(pe.getEspecialidadeId());
                
                if (PrinterConfig.ESPECIALIDADE_ENFERMAGEM.equalsIgnoreCase(nomeEspecialidade)) {
                    classificacao.enfermagem = pe;
                } else if (nomeEspecialidade != null && 
                          PrinterConfig.ESPECIALIDADES_SEM_ENFERMAGEM.contains(nomeEspecialidade.toUpperCase())) {
                    classificacao.especialidadesSemEnfermagem.add(pe);
                } else {
                    classificacao.especialidadesComEnfermagem.add(pe);
                }
            }
            
            return classificacao;
        }
        
        private static class ClassificacaoEspecialidades {
            List<PacienteEspecialidade> especialidadesSemEnfermagem = new ArrayList<>();
            List<PacienteEspecialidade> especialidadesComEnfermagem = new ArrayList<>();
            PacienteEspecialidade enfermagem = null;
        }
    }
    
    /**
     * Processador principal de impressão
     */
    private static class ProcessadorImpressao {
        private final AtendimentoManager atendimentoManager;
        private final PDFGenerator pdfGenerator;
        private final EspecialidadeCache especialidadeCache;
        private final UserInterface userInterface;
        
        public ProcessadorImpressao(AtendimentoManager atendimentoManager, 
                                   PDFGenerator pdfGenerator,
                                   EspecialidadeCache especialidadeCache,
                                   UserInterface userInterface) {
            this.atendimentoManager = atendimentoManager;
            this.pdfGenerator = pdfGenerator;
            this.especialidadeCache = especialidadeCache;
            this.userInterface = userInterface;
        }
        
        public ResultadoImpressao processar(Paciente paciente, List<GrupoImpressao> grupos) {
            ResultadoImpressao resultado = new ResultadoImpressao();
            
            for (GrupoImpressao grupo : grupos) {
                processarGrupo(paciente, grupo, resultado);
            }
            
            return resultado;
        }
        
        private void processarGrupo(Paciente paciente, GrupoImpressao grupo, ResultadoImpressao resultado) {
            try {
                // Verificar status dos atendimentos
                var verificacao = verificarStatusGrupo(grupo);
                if (verificacao.temCancelamento()) {
                    String nomeGrupo = grupo.obterNomeGrupo(especialidadeCache);
                    resultado.adicionarResultado(nomeGrupo, PrinterConfig.STATUS_CANCELADA, StatusImpressao.CANCELADA);
                    return;
                }
                
                // Gerar PDF
                boolean sucesso = pdfGenerator.gerarPDF(paciente, grupo, verificacao.numeracoes, especialidadeCache);
                String nomeGrupo = grupo.obterNomeGrupo(especialidadeCache);
                
                if (sucesso) {
                    resultado.incrementarSucesso();
                    resultado.adicionarResultado(nomeGrupo, "PDF GERADO COM SUCESSO", StatusImpressao.SUCESSO);
                } else {
                    resultado.incrementarErro();
                    resultado.adicionarResultado(nomeGrupo, "FALHA NA GERAÇÃO DO PDF", StatusImpressao.ERRO);
                }
                
            } catch (Exception ex) {
                String nomeGrupo = grupo.obterNomeGrupo(especialidadeCache);
                resultado.incrementarErro();
                resultado.adicionarResultado(nomeGrupo, PrinterConfig.STATUS_ERRO + ": " + ex.getMessage(), StatusImpressao.ERRO);
            }
        }
        
        private VerificacaoAtendimento verificarStatusGrupo(GrupoImpressao grupo) {
            Map<Integer, String> numeracoes = new HashMap<>();
            
            for (PacienteEspecialidade pe : grupo.getEspecialidades()) {
                String nomeEspecialidade = especialidadeCache.obterNome(pe.getEspecialidadeId());
                
                // Para enfermagem adicionada automaticamente
                if (grupo.isTemEnfermagemAutomatica() && 
                    PrinterConfig.ESPECIALIDADE_ENFERMAGEM.equalsIgnoreCase(nomeEspecialidade) && 
                    !grupo.isEspecialidadePrincipal(pe)) {
                    numeracoes.put(pe.getEspecialidadeId(), "ACOMPANHAMENTO");
                    continue;
                }
                
                StatusAtendimento status = atendimentoManager.verificarStatusAtendimento(
                    pe.getEspecialidadeId(), nomeEspecialidade);
                
                if (status.isCancelado()) {
                    return VerificacaoAtendimento.cancelado();
                }
                
                numeracoes.put(pe.getEspecialidadeId(), status.getNumeracao());
            }
            
            return VerificacaoAtendimento.sucesso(numeracoes);
        }
        
        private static class VerificacaoAtendimento {
            final Map<Integer, String> numeracoes;
            final boolean cancelado;
            
            private VerificacaoAtendimento(Map<Integer, String> numeracoes, boolean cancelado) {
                this.numeracoes = numeracoes != null ? numeracoes : Collections.emptyMap();
                this.cancelado = cancelado;
            }
            
            static VerificacaoAtendimento sucesso(Map<Integer, String> numeracoes) {
                return new VerificacaoAtendimento(numeracoes, false);
            }
            
            static VerificacaoAtendimento cancelado() {
                return new VerificacaoAtendimento(null, true);
            }
            
            boolean temCancelamento() { return cancelado; }
        }
    }
    
    /**
     * Gerador de PDFs com layout profissional
     */
    private static class PDFGenerator {
        
        public boolean gerarPDF(Paciente paciente, GrupoImpressao grupo, 
                               Map<Integer, String> numeracoes, EspecialidadeCache cache) {
            try {
                String nomeArquivo = FileNameGenerator.gerar(paciente, grupo, cache);
                String caminhoCompleto = PrinterConfig.DIRETORIO_PDFS + nomeArquivo;
                
                // Criar diretório se não existir
                new File(PrinterConfig.DIRETORIO_PDFS).mkdirs();
                
                try (PdfWriter writer = new PdfWriter(caminhoCompleto);
                     PdfDocument pdfDoc = new PdfDocument(writer);
                     Document document = new Document(pdfDoc, PageSize.A4)) {
                    
                    configurarDocumento(document);
                    criarConteudoPDF(document, paciente, grupo.getEspecialidades(), numeracoes, cache);
                    
                    // Tentar abrir automaticamente
                    FileUtils.tentarAbrirPDF(caminhoCompleto);
                    
                    return true;
                }
                
            } catch (Exception ex) {
                throw new RuntimeException("Erro ao gerar PDF: " + ex.getMessage(), ex);
            }
        }
        
        private void configurarDocumento(Document document) {
            document.setMargins(
                PDFStyle.MARGEM_PAGINA, 
                PDFStyle.MARGEM_PAGINA, 
                PDFStyle.MARGEM_PAGINA, 
                PDFStyle.MARGEM_PAGINA
            );
        }
        
        private void criarConteudoPDF(Document document, Paciente paciente, 
                                     List<PacienteEspecialidade> especialidades,
                                     Map<Integer, String> numeracoes, 
                                     EspecialidadeCache cache) throws IOException {
            
            var fontManager = new FontManager();
            var layoutBuilder = new PDFLayoutBuilder(fontManager);
            
            // Construir seções do documento
            layoutBuilder.adicionarCabecalho(document);
            layoutBuilder.adicionarDadosPaciente(document, paciente);
            layoutBuilder.adicionarEspecialidades(document, especialidades, numeracoes, cache);
            layoutBuilder.adicionarEspacoRelatorio(document);
            layoutBuilder.adicionarRodape(document);
        }
    }
    
    /**
     * Gerenciador de fontes para PDF
     */
    private static class FontManager {
        private final PdfFont fonteTitulo;
        private final PdfFont fonteNormal;
        private final PdfFont fonteNegrito;
        
        public FontManager() throws IOException {
            this.fonteTitulo = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            this.fonteNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            this.fonteNegrito = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        }
        
        public PdfFont getTitulo() { return fonteTitulo; }
        public PdfFont getNormal() { return fonteNormal; }
        public PdfFont getNegrito() { return fonteNegrito; }
    }
    
    /**
     * Construtor de layout para PDF
     */
    private static class PDFLayoutBuilder {
        private final FontManager fontManager;
        
        public PDFLayoutBuilder(FontManager fontManager) {
            this.fontManager = fontManager;
        }
        
        public void adicionarCabecalho(Document document) {
            Table cabecalho = new Table(1);
            cabecalho.setWidth(UnitValue.createPercentValue(100));
            
            Cell cellCabecalho = new Cell()
                    .add(new Paragraph("FICHA DO PACIENTE")
                            .setFont(fontManager.getTitulo())
                            .setFontSize(PDFStyle.TAMANHO_FONTE_TITULO)
                            .setFontColor(ColorConstants.WHITE)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMargin(0))
                    .setBackgroundColor(PDFStyle.COR_CABECALHO)
                    .setPadding(PDFStyle.PADDING_CABECALHO)
                    .setBorder(Border.NO_BORDER);
            
            cabecalho.addCell(cellCabecalho);
            document.add(cabecalho);
            document.add(new Paragraph("\n").setMargin(0).setPadding(0));
        }
        
        public void adicionarDadosPaciente(Document document, Paciente paciente) {
            // Título da seção
            adicionarTituloSecao(document, "DADOS DO PACIENTE");
            
            // Tabela com dados
            Table tabelaDados = criarTabelaDados();
            
            if (StringUtils.isPresente(paciente.getNome())) {
                adicionarLinhaDados(tabelaDados, "Nome:", paciente.getNome());
            }
            
            if (paciente.getIdade() != null) {
                adicionarLinhaDados(tabelaDados, "Idade:", paciente.getIdade() + " anos");
            }
            
            if (StringUtils.isPresente(paciente.getDataNascimento())) {
                adicionarLinhaDados(tabelaDados, "Data de Nascimento:", paciente.getDataNascimento());
            }
            
            document.add(tabelaDados);
        }
        
        public void adicionarEspecialidades(Document document, List<PacienteEspecialidade> especialidades,
                                           Map<Integer, String> numeracoes, EspecialidadeCache cache) {
            if (especialidades == null || especialidades.isEmpty()) {
                return;
            }
            
            adicionarTituloSecao(document, "ESPECIALIDADES MÉDICAS");
            
            Table tabelaEspec = criarTabelaDados();
            
            for (PacienteEspecialidade pe : especialidades) {
                String nomeEspecialidade = cache.obterNome(pe.getEspecialidadeId());
                if (nomeEspecialidade != null) {
                    String numeracao = numeracoes.get(pe.getEspecialidadeId());
                    String valorExibicao = formatarNumeracao(numeracao);
                    adicionarLinhaDados(tabelaEspec, nomeEspecialidade + ":", valorExibicao);
                }
            }
            
            document.add(tabelaEspec);
        }
        
        public void adicionarEspacoRelatorio(Document document) {
            adicionarTituloSecao(document, "RELATÓRIO MÉDICO");
            
            // Instruções
            Paragraph instrucoes = new Paragraph("Espaço reservado para anotações e observações médicas:")
                    .setFont(fontManager.getNormal())
                    .setFontSize(PDFStyle.TAMANHO_FONTE_PEQUENA)
                    .setFontColor(PDFStyle.COR_TEXTO_CLARO)
                    .setMarginBottom(15);
            document.add(instrucoes);
            
            // Linhas para escrita
            for (int i = 0; i < PDFStyle.LINHAS_RELATORIO; i++) {
                Table linhaEscrita = new Table(1);
                linhaEscrita.setWidth(UnitValue.createPercentValue(100));
                linhaEscrita.setMarginBottom(8);
                
                Cell cellLinha = new Cell()
                        .add(new Paragraph(" ").setMargin(0))
                        .setBorder(Border.NO_BORDER)
                        .setBorderBottom(new SolidBorder(PDFStyle.COR_BORDA, 1))
                        .setPadding(5);
                
                linhaEscrita.addCell(cellLinha);
                document.add(linhaEscrita);
            }
            
            document.add(new Paragraph("\n"));
        }
        
        public void adicionarRodape(Document document) {
            // Linha separadora
            Table linhaSeparadora = new Table(1);
            linhaSeparadora.setWidth(UnitValue.createPercentValue(100));
            linhaSeparadora.setMarginTop(20);
            
            Cell cellLinha = new Cell()
                    .add(new Paragraph(" ").setMargin(0))
                    .setBorder(Border.NO_BORDER)
                    .setBorderTop(new SolidBorder(PDFStyle.COR_BORDA, 1))
                    .setPadding(0);
            
            linhaSeparadora.addCell(cellLinha);
            document.add(linhaSeparadora);
            
            // Informações do rodapé
            Paragraph dataImpressao = new Paragraph("Data/Hora da impressão: " + 
                    PrinterConfig.FORMATO_DATA_HORA.format(new Date()))
                    .setFont(fontManager.getNormal())
                    .setFontSize(PDFStyle.TAMANHO_FONTE_RODAPE)
                    .setFontColor(PDFStyle.COR_TEXTO_CLARO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10);
            
            Paragraph sistema = new Paragraph("Sistema de Gestão de Pacientes")
                    .setFont(fontManager.getNormal())
                    .setFontSize(PDFStyle.TAMANHO_FONTE_RODAPE)
                    .setFontColor(PDFStyle.COR_TEXTO_CLARO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(5);
            
            document.add(dataImpressao);
            document.add(sistema);
        }
        
        private void adicionarTituloSecao(Document document, String titulo) {
            Paragraph tituloSecao = new Paragraph(titulo)
                    .setFont(fontManager.getNegrito())
                    .setFontSize(PDFStyle.TAMANHO_FONTE_SECAO)
                    .setFontColor(PDFStyle.COR_SECAO)
                    .setMarginBottom(10);
            document.add(tituloSecao);
        }
        
        private Table criarTabelaDados() {
            Table tabela = new Table(2);
            tabela.setWidth(UnitValue.createPercentValue(100));
            tabela.setMarginBottom(20);
            return tabela;
        }
        
        private void adicionarLinhaDados(Table tabela, String rotulo, String valor) {
            // Célula do rótulo
            Cell cellRotulo = new Cell()
                    .add(new Paragraph(rotulo)
                            .setFont(fontManager.getNegrito())
                            .setFontSize(PDFStyle.TAMANHO_FONTE_NORMAL)
                            .setMargin(0))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(PDFStyle.PADDING_CELULA)
                    .setWidth(UnitValue.createPercentValue(30));
            
            // Célula do valor
            Cell cellValor = new Cell()
                    .add(new Paragraph(valor != null ? valor : "")
                            .setFont(fontManager.getNormal())
                            .setFontSize(PDFStyle.TAMANHO_FONTE_NORMAL)
                            .setMargin(0))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(PDFStyle.PADDING_CELULA)
                    .setBorderBottom(new SolidBorder(PDFStyle.COR_BORDA, 0.5f));
            
            tabela.addCell(cellRotulo);
            tabela.addCell(cellValor);
        }
        
        private String formatarNumeracao(String numeracao) {
            if (numeracao == null) return "";
            
            if (PrinterConfig.STATUS_ESGOTADO.equals(numeracao)) {
                return "ATENDIMENTOS ESGOTADOS";
            } else if ("ACOMPANHAMENTO".equals(numeracao)) {
                return "ACOMPANHAMENTO";
            } else {
                return "Atendimento Nº " + numeracao;
            }
        }
    }
    
    /**
     * Gerador de nomes de arquivo
     */
    private static class FileNameGenerator {
        public static String gerar(Paciente paciente, GrupoImpressao grupo, EspecialidadeCache cache) {
            String nomePaciente = sanitizarNome(paciente.getNome());
            String nomeGrupo = sanitizarNome(grupo.obterNomeGrupo(cache));
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            
            return String.format("Ficha_%s_%s_%s.pdf", nomePaciente, nomeGrupo, timestamp);
        }
        
        private static String sanitizarNome(String nome) {
            return nome.replaceAll("[^a-zA-Z0-9]", "_");
        }
    }
    
    /**
     * Utilitários para manipulação de arquivos
     */
    private static class FileUtils {
        public static void tentarAbrirPDF(String caminhoArquivo) {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(new File(caminhoArquivo));
                }
            } catch (IOException ex) {
                // Falha silenciosa - o arquivo foi criado mesmo que não possa ser aberto
            }
        }
    }
    
    /**
     * Gerador de resumo de impressão
     */
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
                resumo.append("✅ Todos os PDFs foram gerados com sucesso!\n\n");
            } else if (resultado.getFichasImpressas() > 0 && resultado.getFichasComErro() > 0) {
                resumo.append("⚠️ Geração de PDFs parcialmente concluída!\n\n");
            } else {
                resumo.append("❌ Nenhum PDF foi gerado!\n\n");
            }
        }
        
        private void adicionarEstatisticas(StringBuilder resumo) {
            resumo.append("📊 RESUMO:\n");
            resumo.append("• PDFs gerados: ").append(resultado.getFichasImpressas()).append("\n");
            resumo.append("• PDFs com erro: ").append(resultado.getFichasComErro()).append("\n");
            
            int canceladas = resultado.getCanceladas();
            if (canceladas > 0) {
                resumo.append("• PDFs cancelados: ").append(canceladas).append("\n");
            }
            
            resumo.append("\n📁 Arquivos salvos na área de trabalho\n\n");
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
                return "PDFs Gerados com Sucesso";
            } else if (resultado.getFichasImpressas() > 0) {
                return "Geração Parcial de PDFs";
            } else {
                return "Erro na Geração de PDFs";
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
    
    // ===== CLASSES DE UTILIDADE =====
    
    /**
     * Helper para validações
     */
    private static class ValidationHelper {
        
        public static ValidationResult validarPaciente(Paciente paciente) {
            if (paciente == null || StringUtils.isNullOrEmpty(paciente.getNome())) {
                return ValidationResult.erro("Não há dados de paciente para imprimir!");
            }
            return ValidationResult.sucesso();
        }
        
        public static ValidationResult validarPacienteEspecialidade(Paciente paciente, int especialidadeId) {
            if (paciente == null || especialidadeId <= 0) {
                return ValidationResult.erro("Dados inválidos para impressão!");
            }
            return ValidationResult.sucesso();
        }
    }
    
    /**
     * Resultado de validação
     */
    private static class ValidationResult {
        private final boolean valido;
        private final String mensagem;
        
        private ValidationResult(boolean valido, String mensagem) {
            this.valido = valido;
            this.mensagem = mensagem;
        }
        
        public static ValidationResult sucesso() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult erro(String mensagem) {
            return new ValidationResult(false, mensagem);
        }
        
        public boolean isValido() { return valido; }
        public String getMensagem() { return mensagem; }
    }
    
    /**
     * Utilitários para strings
     */
    private static class StringUtils {
        public static boolean isNullOrEmpty(String str) {
            return str == null || str.trim().isEmpty();
        }
        
        public static boolean isPresente(String valor) {
            return valor != null && !valor.trim().isEmpty();
        }
    }
    
    /**
     * Builder para PacienteEspecialidade
     */
    private static class PacienteEspecialidadeBuilder {
        public static PacienteEspecialidade criar(int pacienteId, int especialidadeId) {
            PacienteEspecialidade pe = new PacienteEspecialidade();
            pe.setPacienteId(pacienteId);
            pe.setEspecialidadeId(especialidadeId);
            return pe;
        }
    }
}