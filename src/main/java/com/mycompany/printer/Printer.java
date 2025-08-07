package com.mycompany.printer;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
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

import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.mycompany.model.dao.EspecialidadeDAO;
import com.mycompany.model.dao.PacienteEspecialidadeDAO;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

/**
 * Classe responsável pela impressão de fichas de pacientes com iText PDF
 * Design profissional otimizado para uma página
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
    private static final String LOGO_PATH = "/home/vitor/NetBeansProjects/projeto_IBG/src/main/resources/imagens/logo_cecom.png";
    
    // Especialidades que não devem incluir Enfermagem
    private static final Set<String> ESPECIALIDADES_SEM_ENFERMAGEM = Set.of(
        "DENTISTA", "PSICOLOGIA", "TERAPEUTA"
    );
    
    private static final String ESPECIALIDADE_ENFERMAGEM = "ENFERMAGEM";
    private static final SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat FORMATO_DATA_HORA = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
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

    // Construtor
    public Printer(Component parent, PacienteEspecialidadeDAO pacienteEspecialidadeDAO, 
                     EspecialidadeDAO especialidadeDAO, List<Especialidade> especialidades) {
        this.parent = parent;
        this.pacienteEspecialidadeDAO = pacienteEspecialidadeDAO;
        this.especialidadeDAO = especialidadeDAO;
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
            
            boolean impressaoSucesso = executarImpressaoPDF(paciente, grupo, numeracoes);
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
     * Executa a impressão em PDF usando iText
     */
    private boolean executarImpressaoPDF(Paciente paciente, GrupoImpressao grupo, Map<Integer, String> numeracoes) {
        try {
            // Diretório onde os PDFs serão salvos
            File diretorio = new File(System.getProperty("user.home"), "Documents");

            
            if (!diretorio.exists()) {
                diretorio = new File(System.getProperty("user.home"), "Documentos");
            }
            
            // Criar o diretório se não existir
            if (!diretorio.exists()) {
                boolean criouDiretorio = diretorio.mkdirs();
                if (!criouDiretorio) {
                    throw new RuntimeException("Não foi possível criar o diretório: ");
                }
            }

            // Criar nome do arquivo único com timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String nomeArquivo = String.format("ficha_%s_%s.pdf", 
                    paciente.getNome().replaceAll("[^a-zA-Z0-9]", "_"),
                    timestamp);

            File arquivoPDF = new File(diretorio, nomeArquivo);

            // Criar o documento PDF
            PdfWriter writer = new PdfWriter(arquivoPDF.getAbsolutePath());
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);

            // Configurar margens menores para maximizar espaço
            document.setMargins(15, 15, 15, 15);

            // Criar o conteúdo do PDF
            criarConteudoPDF(document, paciente, grupo.getEspecialidades(), numeracoes);

            // Fechar o documento
            document.close();

            // Abrir o PDF automaticamente para visualização/impressão
            abrirPDF(arquivoPDF);

            return true;

        } catch (Exception ex) {
            throw new RuntimeException("Erro ao criar PDF: " + ex.getMessage(), ex);
        }
    }

    
    /**
     * Cria o conteúdo completo do PDF
     */
    private void criarConteudoPDF(Document document, Paciente paciente, 
                                 List<PacienteEspecialidade> especialidades,
                                 Map<Integer, String> numeracoes) throws IOException {
        
        // Fontes
        PdfFont fonteTitulo = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont fonteNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        // 1. CABEÇALHO (compacto)
        adicionarCabecalho(document, fonteTitulo, fonteNormal);
        
        // 2. DADOS PESSOAIS DO PACIENTE (mais compacto)
        adicionarDadosPessoais(document, paciente, fonteTitulo, fonteNormal);
        
        // 3. ATENDIMENTO DE TRIAGEM/ESPECIALIDADE
        adicionarAtendimentoEspecialidades(document, especialidades, numeracoes, fonteTitulo, fonteNormal);
        
        // 4. PARÂMETROS CLÍNICOS (mais compacto)
        adicionarParametrosClinicos(document, paciente, fonteTitulo, fonteNormal);
        
        // 5. PATOLOGIAS (uma linha só)
        adicionarPatologias(document, fonteTitulo, fonteNormal);
        
        // 6. AVALIAÇÃO MÉDICA (maior espaço com linhas)
        adicionarAvaliacaoMedica(document, fonteTitulo, fonteNormal);
    }
    
    /**
     * Adiciona o cabeçalho com logo e título (mais compacto)
     */
    private void adicionarCabecalho(Document document, PdfFont fonteTitulo, PdfFont fonteNormal) throws IOException {
        Table cabecalhoTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1}))
                .useAllAvailableWidth();
        
        // Logo 
        try {
            File logoFile = new File(LOGO_PATH);
            if (logoFile.exists()) {
                ImageData imageData = ImageDataFactory.create(LOGO_PATH);
                Image logo = new Image(imageData);
                logo.setWidth(95).setHeight(35); // Largura maior, altura menor
                cabecalhoTable.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER));
            } else {
                cabecalhoTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            }
        } catch (Exception e) {
            cabecalhoTable.addCell(new Cell().setBorder(Border.NO_BORDER));
        }
        
        // Título centralizado
        Cell tituloCell = new Cell()
                .add(new Paragraph("FICHA DO PACIENTE")
                        .setFont(fonteTitulo).setFontSize(14).setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(ColorConstants.DARK_GRAY))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER);
        
        cabecalhoTable.addCell(tituloCell);
        
        // Data
        String dataAtual = FORMATO_DATA.format(new Date());
        Cell dataCell = new Cell()
                .add(new Paragraph("Data:")
                        .setFont(fonteNormal).setFontSize(9))
                .add(new Paragraph(dataAtual)
                        .setFont(fonteTitulo).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
        
        cabecalhoTable.addCell(dataCell);
        
        document.add(cabecalhoTable);
        document.add(new Paragraph(" ").setFontSize(3)); // Espaço mínimo
    }
    
    /**
     * Adiciona seção de dados pessoais (mais compacta)
     */
    private void adicionarDadosPessoais(Document document, Paciente paciente, PdfFont fonteTitulo, PdfFont fonteNormal) {
        // Título da seção
        Paragraph titulo = new Paragraph("DADOS PESSOAIS DO PACIENTE")
                .setFont(fonteTitulo)
                .setFontSize(10)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(3)
                .setMarginBottom(2);
        document.add(titulo);
        
        // Tabela de dados pessoais - 3 colunas para compactar mais
        Table dadosTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}))
                .useAllAvailableWidth();
        
        // Linha 1: Nome (2 cols), Data Nasc. (1 col)
        dadosTable.addCell(new Cell(1, 2)
                .add(new Paragraph()
                        .add(new Text("Nome: ").setFont(fonteTitulo).setFontSize(9))
                        .add(new Text(obterValorOuVazio(paciente.getNome())).setFont(fonteNormal).setFontSize(9)))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(2)
                .setMinHeight(15));
        
        dadosTable.addCell(new Cell()
                .add(new Paragraph()
                        .add(new Text("Nasc.: ").setFont(fonteTitulo).setFontSize(9))
                        .add(new Text(obterValorOuVazio(paciente.getDataNascimento())).setFont(fonteNormal).setFontSize(9)))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(2)
                .setMinHeight(15));
        
        // Linha 2: Nome da Mãe (2 cols), Idade (1 col)
        dadosTable.addCell(new Cell(1, 2)
                .add(new Paragraph()
                        .add(new Text("Mãe: ").setFont(fonteTitulo).setFontSize(9))
                        .add(new Text(obterValorOuVazio(paciente.getNomeDaMae())).setFont(fonteNormal).setFontSize(8)))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(2)
                .setMinHeight(15));
        
        dadosTable.addCell(new Cell()
                .add(new Paragraph()
                        .add(new Text("Idade: ").setFont(fonteTitulo).setFontSize(9))
                        .add(new Text(paciente.getIdade() != null ? paciente.getIdade() + " anos" : "").setFont(fonteNormal).setFontSize(9)))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(2)
                .setMinHeight(15));
        
        // Linha 3: CPF, SUS, Telefone
        dadosTable.addCell(new Cell()
                .add(new Paragraph()
                        .add(new Text("CPF: ").setFont(fonteTitulo).setFontSize(9))
                        .add(new Text(obterValorOuVazio(paciente.getCpf())).setFont(fonteNormal).setFontSize(8)))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(2)
                .setMinHeight(15));
        
        dadosTable.addCell(new Cell()
                .add(new Paragraph()
                        .add(new Text("SUS: ").setFont(fonteTitulo).setFontSize(9))
                        .add(new Text(obterValorOuVazio(paciente.getSus())).setFont(fonteNormal).setFontSize(8)))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(2)
                .setMinHeight(15));
        
        dadosTable.addCell(new Cell()
                .add(new Paragraph()
                        .add(new Text("Tel.: ").setFont(fonteTitulo).setFontSize(9))
                        .add(new Text(obterValorOuVazio(paciente.getTelefone())).setFont(fonteNormal).setFontSize(8)))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(2)
                .setMinHeight(15));
        
        // Linha 4: Endereço (3 cols)
        dadosTable.addCell(new Cell(1, 3)
                .add(new Paragraph()
                        .add(new Text("Endereço: ").setFont(fonteTitulo).setFontSize(9))
                        .add(new Text(obterValorOuVazio(paciente.getEndereco())).setFont(fonteNormal).setFontSize(8)))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(2)
                .setMinHeight(15));
        
        document.add(dadosTable);
        document.add(new Paragraph(" ").setFontSize(2)); // Espaço mínimo
    }
    
    /**
     * Adiciona seção de atendimento/especialidades
     */
    private void adicionarAtendimentoEspecialidades(Document document, List<PacienteEspecialidade> especialidades,
                                                   Map<Integer, String> numeracoes, PdfFont fonteTitulo, PdfFont fonteNormal) {
        // Título da seção
        Paragraph titulo = new Paragraph("ATENDIMENTO DE TRIAGEM/ESPECIALIDADE")
                .setFont(fonteTitulo)
                .setFontSize(10)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(3)
                .setMarginBottom(2);
        document.add(titulo);
        
        // Tabela de especialidades
        Table especTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                .useAllAvailableWidth();
        
        for (PacienteEspecialidade pe : especialidades) {
            String nomeEspecialidade = obterNomeEspecialidade(pe.getEspecialidadeId());
            if (nomeEspecialidade != null) {
                // Nome da especialidade
                especTable.addCell(new Cell()
                        .add(new Paragraph(nomeEspecialidade).setFont(fonteNormal).setFontSize(9))
                        .setBorder(new SolidBorder(0.5f))
                        .setPadding(2)
                        .setMinHeight(15));
                
                // Numeração
                String numeracao = numeracoes.get(pe.getEspecialidadeId());
                String textoNumeracao = "";
                if (numeracao != null) {
                    if (STATUS_ESGOTADO.equals(numeracao)) {
                        textoNumeracao = "ESGOTADO";
                    } else if ("ACOMPANHAMENTO".equals(numeracao)) {
                        textoNumeracao = "ACOMP.";
                    } else {
                        textoNumeracao = "Nº " + numeracao;
                    }
                }
                
                especTable.addCell(new Cell()
                        .add(new Paragraph(textoNumeracao).setFont(fonteTitulo).setFontSize(9))
                        .setBorder(new SolidBorder(0.5f))
                        .setPadding(2)
                        .setMinHeight(15)
                        .setTextAlignment(TextAlignment.CENTER));
            }
        }
        
        document.add(especTable);
        document.add(new Paragraph(" ").setFontSize(2)); // Espaço mínimo
    }
    
    /**
     * Adiciona seção de parâmetros clínicos (mais compacta)
     */
    private void adicionarParametrosClinicos(Document document, Paciente paciente, 
                                            PdfFont fonteTitulo, PdfFont fonteNormal) {
        // Título da seção
        Paragraph titulo = new Paragraph("PARÂMETROS CLÍNICOS")
                .setFont(fonteTitulo)
                .setFontSize(10)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(3)
                .setMarginBottom(2);
        document.add(titulo);

        // Tabela de parâmetros - 4 colunas
        Table paramTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                .useAllAvailableWidth();

        // Linha 1: Peso, Altura, SPO2, FC
        paramTable.addCell(criarCelulParametro("Peso", 
                paciente.getPeso() != null ? String.format("%.1f kg", paciente.getPeso()) : "", 
                fonteTitulo, fonteNormal));

        paramTable.addCell(criarCelulParametro("Altura", 
                paciente.getAltura() != null ? String.format("%.2f m", paciente.getAltura()) : "", 
                fonteTitulo, fonteNormal));

        paramTable.addCell(criarCelulParametro("SPO2", 
                paciente.getSpo2() != null ? String.format("%.1f%%", paciente.getSpo2()) : "", 
                fonteTitulo, fonteNormal));

        paramTable.addCell(criarCelulParametro("FC", 
                paciente.getFcBpm() != null ? paciente.getFcBpm() + " bpm" : "", 
                fonteTitulo, fonteNormal));

        // Linha 2: PA, HGT, T, FR
        paramTable.addCell(criarCelulParametro("PA", 
                obterValorOuVazio(paciente.getPaXmmhg()), 
                fonteTitulo, fonteNormal));

        paramTable.addCell(criarCelulParametro("HGT", 
                paciente.getHgtMgld() != null ? paciente.getHgtMgld() + " mg/dL" : "", 
                fonteTitulo, fonteNormal));

        paramTable.addCell(criarCelulParametro("T", 
                paciente.getTemperaturaC() != null ? String.format("%.1f°C", paciente.getTemperaturaC()) : "", 
                fonteTitulo, fonteNormal));

        paramTable.addCell(criarCelulParametro("FR", 
                paciente.getFrIbpm() != null ? paciente.getFrIbpm() + " rpm" : "", 
                fonteTitulo, fonteNormal));

        document.add(paramTable);
        document.add(new Paragraph(" ").setFontSize(2)); // Espaço mínimo
    }
    
    /**
     * Cria célula para parâmetro clínico 
     */
    private Cell criarCelulParametro(String rotulo, String valor, PdfFont fonteTitulo, PdfFont fonteNormal) {
        return new Cell()
                .add(new Paragraph()
                        .add(new Text(rotulo + ": ").setFont(fonteTitulo).setFontSize(8))
                        .add(new Text(valor).setFont(fonteNormal).setFontSize(8)))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(2)
                .setMinHeight(18)
                .setVerticalAlignment(VerticalAlignment.MIDDLE); // Centraliza verticalmente
    }
    
    /**
     * Adiciona seção de patologias (uma linha só)
     */
    private void adicionarPatologias(Document document, PdfFont fonteTitulo, PdfFont fonteNormal) {
        // Título da seção
        Paragraph titulo = new Paragraph("PATOLOGIAS")
                .setFont(fonteTitulo)
                .setFontSize(10)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(3)
                .setMarginBottom(2);
        document.add(titulo);

        // Tabela de patologias - 6 colunas (uma linha só)
        Table patolTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1, 1, 1}))
                .useAllAvailableWidth();

        // Lista de patologias - todas em uma linha
        String[] patologias = {"HAS ( )", "DM ( )", "Alergia ( )", 
                              "Asma ( )", "D. Virais ( )", "D. Resp. ( )"};

        for (String patologia : patologias) {
            patolTable.addCell(new Cell()
                    .add(new Paragraph(patologia).setFont(fonteTitulo).setFontSize(8))
                    .setBorder(new SolidBorder(0.5f))
                    .setPadding(2)
                    .setMinHeight(16)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)); // Centraliza verticalmente
        }

        document.add(patolTable);
        document.add(new Paragraph(" ").setFontSize(3)); // Espaço pequeno
    }
    
    /**
     * Adiciona seção de avaliação médica 
     */
    private void adicionarAvaliacaoMedica(Document document, PdfFont fonteTitulo, PdfFont fonteNormal) {
        // Título da seção
        Paragraph titulo = new Paragraph("AVALIAÇÃO MÉDICA")
                .setFont(fonteTitulo)
                .setFontSize(10)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(3)
                .setMarginBottom(2);
        document.add(titulo);

        // Criar área com linhas para escrita manual - altura reduzida para caber na página
        Table avaliacaoTable = new Table(1).useAllAvailableWidth();

        // Criar célula com linhas horizontais para preenchimento manual
        Cell avaliacaoCell = new Cell()
                .setBorder(new SolidBorder(1f))
                .setPadding(5)
                .setMinHeight(370); // Altura de 370

        // Reduzir número de linhas e espaçamento para caber na página
        for (int i = 0; i < 20; i++) { // Reduzido de 25 para 20 linhas
            Paragraph linha = new Paragraph("_".repeat(120)) // Linha de underscores
                    .setFont(fonteNormal)
                    .setFontSize(8)
                    .setMarginBottom(6) // Espaçamento reduzido de 8 para 6
                    .setFontColor(ColorConstants.LIGHT_GRAY);
            avaliacaoCell.add(linha);
        }

        avaliacaoTable.addCell(avaliacaoCell);
        document.add(avaliacaoTable);

        // Rodapé compacto
        document.add(new Paragraph(" ").setFontSize(2)); // Espaço reduzido
        document.add(new Paragraph("Impresso em: " + FORMATO_DATA_HORA.format(new Date()))
                .setFont(fonteNormal)
                .setFontSize(7)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontColor(ColorConstants.GRAY));
    }
    
    /**
     * Abre o PDF automaticamente para visualização/impressão
     */
    private void abrirPDF(File arquivoPDF) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(arquivoPDF);
                }
            }
        } catch (IOException ex) {
            // Se não conseguir abrir automaticamente, mostrar mensagem com localização
            JOptionPane.showMessageDialog(parent, 
                    "PDF criado com sucesso!\nLocalização: " + arquivoPDF.getAbsolutePath(),
                    "PDF Gerado", 
                    JOptionPane.INFORMATION_MESSAGE);
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

            boolean sucesso = executarImpressaoPDF(paciente, grupo, numeracoes);

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
    
    private String obterValorOuVazio(String valor) {
        return valor != null && !valor.trim().isEmpty() ? valor : "";
    }
    
    private void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, TITULO_AVISO, JOptionPane.WARNING_MESSAGE);
    }
    
    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, TITULO_ERRO, JOptionPane.ERROR_MESSAGE);
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
}