package com.mycompany.projeto_ibg;

import com.mycompany.listener.PacienteChangeListener;
import com.mycompany.listener.PacienteEspecialidadeChangeListener;
import com.mycompany.listener.PatientUpdateListener;
import com.mycompany.manager.ApiManager;
import com.mycompany.model.bean.Especialidade;
import com.mycompany.model.bean.Paciente;
import com.mycompany.model.bean.PacienteEspecialidade;
import com.mycompany.service.EspecialidadeService;
import com.mycompany.service.PacienteEspecialidadeService;
import com.mycompany.service.PacienteService;
import com.mycompany.view.FormularioSaude2P;
import com.mycompany.view.FormularioDados2P;
import com.mycompany.view.MenuListener;
import com.mycompany.view.PainelSaude2;
import com.mycompany.view.PainelDados2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class Main extends javax.swing.JFrame implements MenuListener, PacienteChangeListener, PacienteEspecialidadeChangeListener, PatientUpdateListener {
    
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private volatile boolean recarregandoDados = false;
    private volatile boolean inicializando = true;
    
    // CONFIGURAÇÕES DA API
    private String apiBaseUrl = "http://meuservidor.local/api";
    private String webSocketUrl = "ws://meuservidor.local";
    
    // Gerenciador principal da API
    private ApiManager apiManager;
    
    // Services que substituem os DAOs
    private PacienteService pacienteService;
    private EspecialidadeService especialidadeService;
    private PacienteEspecialidadeService pacienteEspecialidadeService;
    
    // Cache local de dados (sincronizado com a API)
    private volatile List<Paciente> pacientes = new ArrayList<>();
    private volatile List<Especialidade> especialidades = new ArrayList<>();
    private volatile List<PacienteEspecialidade> pacienteEspecialidades = new ArrayList<>();
    
    // Referências para os painéis ativos
    private PainelSaude2 painelSaudeAtivo;
    private PainelDados2 painelDadosAtivo;
    private FormularioSaude2P formularioSaudeAtivo;
    private FormularioDados2P formularioDadosAtivo;

    
    public Main() {
        initComponents();
        configurarJanela();
        inicializarSistema();
    }
    
    //Configurações básicas da janela principal
    private void configurarJanela() {
        setTitle("Sistema IBG - Gestão de Saúde");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Configurações de responsividade recomendadas pelo guia
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximiza automaticamente
        setMinimumSize(new Dimension(1200, 600)); // Tamanho mínimo

        // Registrar como listener do menu
        menu31.addMenuListener(this);

        // Configurar redimensionamento dos painéis
        configurarPaineisResponsivos();

        LOGGER.info("Janela principal configurada");
    }
    
    //Configura painéis para serem responsivos
    private void configurarPaineisResponsivos() {
        // Configurar painel principal (tabelas)
        if (content != null) {
            content.setLayout(new BorderLayout());
            content.setMinimumSize(new Dimension(400, 300));
        }

        // Configurar painel de formulários
        if (content2 != null) {
            content2.setLayout(new BorderLayout());
            content2.setMinimumSize(new Dimension(300, 400));
            content2.setPreferredSize(new Dimension(350, 500));
        }

        // Adicionar listener para ajustes dinâmicos
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustLayout();
            }
        });
    }
    
    //Ajusta layout dinamicamente conforme redimensionamento
    private void adjustLayout() {
        SwingUtilities.invokeLater(() -> {
            Dimension windowSize = getSize();

            // Se janela muito pequena, ajustar painéis
            if (windowSize.width < 1000) {
                if (content2 != null) {
                    content2.setPreferredSize(new Dimension(300, content2.getHeight()));
                }
            } else {
                if (content2 != null) {
                    content2.setPreferredSize(new Dimension(350, content2.getHeight()));
                }
            }

            revalidate();
            repaint();
        });
    }
    
    //Inicialização completa do sistema
    private void inicializarSistema() {
        SwingUtilities.invokeLater(() -> {
            try {
                showNotification("🔄 Inicializando sistema...");

                // Inicializar ApiManager
                inicializarApiManager();

                // Carregar dados iniciais de forma sincronizada
                carregarDadosCompletos();

                // Registrar listeners para notificações em tempo real
                registrarListeners();

                // Inicializar painel padrão sem verificação de flag
                inicializarPainelPadrao();

                inicializando = false;
                showNotification("✅ Sistema inicializado com sucesso!");

                LOGGER.info("Sistema IBG inicializado completamente");

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro durante inicialização", e);
                handleInitializationError(e);
            }
        });
    }
    
    //Inicializa o painel padrão (Saúde) durante a inicialização do sistema
    private void inicializarPainelPadrao() {
        try {
            LOGGER.info("=== INICIALIZANDO PAINEL PADRÃO (Saúde) ===");

            // Criar painéis com dados atualizados
            painelSaudeAtivo = new PainelSaude2(new ArrayList<>(pacientes));

            formularioSaudeAtivo = new FormularioSaude2P(
                pacienteService, 
                pacienteEspecialidadeService, 
                especialidadeService, 
                new ArrayList<>(especialidades)
            );

            // Forçar tamanho se necessário
            if (formularioSaudeAtivo != null) {
                formularioSaudeAtivo.setPreferredSize(new Dimension(500, 640));
            }

            // **CORREÇÃO PRINCIPAL: Conectar os listeners ANTES de atualizar a interface**
            if (painelSaudeAtivo != null && formularioSaudeAtivo != null) {
                painelSaudeAtivo.setPatientSelectionListener(formularioSaudeAtivo);
                formularioSaudeAtivo.setPatientUpdateListener(this); // CRUCIAL: Conectar o listener de atualização
                LOGGER.info("Painéis conectados com sucesso");
            }

            // Atualizar interface
            refreshContentPainel(painelSaudeAtivo);
            refreshContentFormulario(formularioSaudeAtivo);

            // Forçar repaint
            if (content2 != null) {
                content2.invalidate();
                content2.validate();
                content2.repaint();
            }

            LOGGER.info("=== PAINEL PADRÃO INICIALIZADO COM SUCESSO ===");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar painel padrão", e);
            showNotification("❌ Erro ao carregar painel inicial");
        }
    }
    
    //Inicializa o ApiManager
    private void inicializarApiManager() {
        try {
            // Carregar configurações
            carregarConfiguracoes();
            
            LOGGER.info("Inicializando ApiManager com URLs: API=" + apiBaseUrl + ", WebSocket=" + webSocketUrl);
            
            // Obter instância do ApiManager (já inicializado no Projeto_IBG.main)
            apiManager = ApiManager.getInstance();
            
            // Verificar se foi inicializado corretamente
            if (apiManager == null) {
                throw new IllegalStateException("ApiManager não foi inicializado pelo main da aplicação");
            }
            
            // Configurar shutdown hook
            apiManager.configurarShutdownHook();
            
            // Inicializar services se API estiver disponível
            if (apiManager.isApiDisponivel()) {
                inicializarServices();
                LOGGER.info("✅ ApiManager e Services inicializados com sucesso");
            } else {
                LOGGER.warning("⚠️ API não disponível - modo offline");
                initializeOfflineMode();
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro crítico ao inicializar ApiManager", e);
            initializeOfflineMode();
            throw new RuntimeException("Falha na inicialização do sistema", e);
        }
    }
    
    //Carrega configurações da aplicação
    private void carregarConfiguracoes() {
        Properties config = new Properties();
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
            if (input != null) {
                config.load(input);
                apiBaseUrl = config.getProperty("api.base.url", apiBaseUrl);
                webSocketUrl = config.getProperty("websocket.url", webSocketUrl);
                LOGGER.info("Configurações carregadas do arquivo config.properties");
            } else {
                LOGGER.info("Arquivo config.properties não encontrado - usando configurações padrão");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao carregar config.properties", e);
        }
    }
    
    //Inicializa os services
    private void inicializarServices() {
        try {
            pacienteService = apiManager.getPacienteService();
            especialidadeService = apiManager.getEspecialidadeService();
            pacienteEspecialidadeService = apiManager.getPacienteEspecialidadeService();
            
            LOGGER.info("✅ Services inicializados com sucesso");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar services", e);
            throw new RuntimeException("Falha ao inicializar services", e);
        }
    }
    
    //Carrega dados de forma completa e sincronizada
    private void carregarDadosCompletos() {
        try {
            LOGGER.info("🔄 Iniciando carregamento completo dos dados...");

            if (!isApiDisponivel()) {
                LOGGER.warning("⚠️ API não disponível - usando dados em cache ou modo offline");
                return;
            }

            // Carregar todos os dados de forma sequencial
            LOGGER.info("Carregando pacientes...");
            List<Paciente> novosPacientes = pacienteService.listarTodos();
            LOGGER.info("✓ Pacientes carregados: " + novosPacientes.size());

            LOGGER.info("Carregando especialidades...");
            List<Especialidade> novasEspecialidades = especialidadeService.listarTodas();
            LOGGER.info("✓ Especialidades carregadas: " + novasEspecialidades.size());

            LOGGER.info("Carregando associações paciente-especialidade...");
            List<PacienteEspecialidade> novasAssociacoes = pacienteEspecialidadeService.listarTodos();
            LOGGER.info("✓ Associações carregadas: " + novasAssociacoes.size());

            //  Validar consistência dos dados
            validarConsistenciaDados(novosPacientes, novasEspecialidades, novasAssociacoes);

            //  Atualizar cache local de forma atômica
            synchronized (this) {
                synchronized (pacientes) {
                    pacientes.clear();
                    pacientes.addAll(novosPacientes);
                }
                
                synchronized (especialidades) {
                    especialidades.clear();
                    especialidades.addAll(novasEspecialidades);
                }
                
                synchronized (pacienteEspecialidades) {
                    pacienteEspecialidades.clear();
                    pacienteEspecialidades.addAll(novasAssociacoes);
                }
            }

            LOGGER.info("✅ Dados carregados com sucesso: " + 
                       pacientes.size() + " pacientes, " + 
                       especialidades.size() + " especialidades, " + 
                       pacienteEspecialidades.size() + " associações");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar dados completos", e);
            throw new RuntimeException("Falha no carregamento completo de dados", e);
        }
    }
    
    //Valida consistência dos dados carregados
    private void validarConsistenciaDados(List<Paciente> pacientes, 
                                         List<Especialidade> especialidades, 
                                         List<PacienteEspecialidade> associacoes) {
        
        LOGGER.info("🔍 Validando consistência dos dados...");
        
        // Validar se associações referenciam pacientes e especialidades existentes
        int associacoesInvalidas = 0;
        
        for (PacienteEspecialidade assoc : associacoes) {
            boolean pacienteExiste = pacientes.stream()
                .anyMatch(p -> p.getId() != null && p.getId().equals(assoc.getPacienteId()));
            
            boolean especialidadeExiste = especialidades.stream()
                .anyMatch(e -> e.getId() != null && e.getId().equals(assoc.getEspecialidadeId()));
            
            if (!pacienteExiste || !especialidadeExiste) {
                associacoesInvalidas++;
                LOGGER.warning("Associação inválida encontrada: Paciente=" + 
                             assoc.getPacienteId() + ", Especialidade=" + assoc.getEspecialidadeId());
            }
        }
        
        if (associacoesInvalidas > 0) {
            LOGGER.warning("⚠️ Encontradas " + associacoesInvalidas + " associações com referências inválidas");
        } else {
            LOGGER.info("✅ Validação de consistência aprovada");
        }
    }
    
    //Inicializa modo offline
    private void initializeOfflineMode() {
        LOGGER.info("🔄 Inicializando modo offline...");
        
        // Inicializar listas vazias
        pacientes = new ArrayList<>();
        especialidades = new ArrayList<>();
        pacienteEspecialidades = new ArrayList<>();
        
        // Mostrar aviso ao usuário
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Modo Offline\n\n" +
                "A API não está disponível no momento.\n" +
                "Verifique se o servidor Spring Boot está rodando em:\n" + 
                apiBaseUrl + "\n\n" +
                "Algumas funcionalidades podem não estar disponíveis.",
                "Aviso de Conectividade", 
                JOptionPane.WARNING_MESSAGE);
        });
    }
    
    //Trata erros de inicialização
    private void handleInitializationError(Exception e) {
        inicializando = false;
        
        LOGGER.log(Level.SEVERE, "Erro crítico na inicialização", e);
        
        JOptionPane.showMessageDialog(this, 
            "❌ Erro Crítico\n\n" +
            "Falha ao inicializar o sistema:\n" + 
            e.getMessage() + "\n\n" +
            "A aplicação será encerrada.",
            "Erro Crítico", 
            JOptionPane.ERROR_MESSAGE);
        
        System.exit(1);
    }
    
    //Registra listeners para notificações em tempo real
    private void registrarListeners() {
        try {
            if (apiManager != null) {
                // Registrar independente do status da conexão
                apiManager.addPacienteChangeListener(this);
                apiManager.addPacienteEspecialidadeChangeListener(this);

                // Se não estiver conectado, tentar conectar
                if (!apiManager.isWebSocketConectado()) {
                    apiManager.reconectar();
                }

                LOGGER.info("Listeners registrados");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao registrar listeners", e);
        }
    }
    
    // ===== IMPLEMENTAÇÃO DOS MÉTODOS MenuListener =====
    
    @Override
    public void onSaudeSelected() {
        if (inicializando) return;
        
        SwingUtilities.invokeLater(() -> {
            try {
                LOGGER.info("=== INICIANDO onSaudeSelected ===");

                // Verificar se temos dados
                if (pacientes == null || pacientes.isEmpty()) {
                    LOGGER.warning("ATENÇÃO: Sem pacientes para exibir!");
                    showNotification("⚠️ Nenhum paciente encontrado. Carregando dados...");

                    // Tentar carregar dados se não temos
                    if (isApiDisponivel()) {
                        carregarDadosCompletos();
                    }
                }

                // Criar painéis com cópias thread-safe dos dados
                List<Paciente> pacientesParaPainel;
                List<Especialidade> especialidadesParaFormulario;
                
                synchronized (this) {
                    pacientesParaPainel = new ArrayList<>(pacientes);
                    especialidadesParaFormulario = new ArrayList<>(especialidades);
                }

                LOGGER.info("Criando PainelSaude2 com " + pacientesParaPainel.size() + " pacientes...");
                painelSaudeAtivo = new PainelSaude2(pacientesParaPainel);

                LOGGER.info("Criando FormularioSaude2P com " + especialidadesParaFormulario.size() + " especialidades...");
                formularioSaudeAtivo = new FormularioSaude2P(
                    pacienteService, 
                    pacienteEspecialidadeService, 
                    especialidadeService, 
                    especialidadesParaFormulario
                );

                // Configurar componentes
                if (formularioSaudeAtivo != null) {
                    formularioSaudeAtivo.setPreferredSize(new Dimension(500, 640));
                    formularioSaudeAtivo.setPatientUpdateListener(this);
                }
                
                if (painelSaudeAtivo != null && formularioSaudeAtivo != null) {
                    painelSaudeAtivo.setPatientSelectionListener(formularioSaudeAtivo);
                    LOGGER.info("Painéis conectados com sucesso");
                }

                // Atualizar interface
                refreshContentPainel(painelSaudeAtivo);
                refreshContentFormulario(formularioSaudeAtivo);

                // Limpar referências dos outros painéis
                painelDadosAtivo = null;
                formularioDadosAtivo = null;

                // Forçar repaint
                if (content2 != null) {
                    content2.invalidate();
                    content2.validate();
                    content2.repaint();
                }

                LOGGER.info("=== onSaudeSelected CONCLUÍDO ===");

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "ERRO em onSaudeSelected", e);
                showNotification("❌ Erro ao carregar painel Saúde: " + e.getMessage());
            }
        });
    }

    @Override
    public void onDadosSelected() {
        if (inicializando) return;
        
        SwingUtilities.invokeLater(() -> {
            try {
                LOGGER.info("=== INICIANDO onDadosSelected ===");

                // **CORREÇÃO: Criar cópias thread-safe dos dados**
                List<Paciente> pacientesParaPainel;
                List<Especialidade> especialidadesParaFormulario;
                List<PacienteEspecialidade> associacoesParaPainel;
                
                synchronized (this) {
                    pacientesParaPainel = new ArrayList<>(pacientes);
                    especialidadesParaFormulario = new ArrayList<>(especialidades);
                    associacoesParaPainel = new ArrayList<>(pacienteEspecialidades);
                }

                LOGGER.info("Criando PainelDados2 com " + pacientesParaPainel.size() + 
                          " pacientes e " + associacoesParaPainel.size() + " associações...");
                
                painelDadosAtivo = new PainelDados2(pacientesParaPainel, associacoesParaPainel);
                
                LOGGER.info("Criando FormularioDados2P com " + especialidadesParaFormulario.size() + " especialidades...");
                formularioDadosAtivo = new FormularioDados2P(
                    pacienteService, 
                    pacienteEspecialidadeService, 
                    especialidadeService, 
                    especialidadesParaFormulario
                );
                
                // **CORREÇÃO: Conectar os listeners ANTES de atualizar a interface**
                if (formularioDadosAtivo != null) {
                    formularioDadosAtivo.setPatientUpdateListener(this);
                }
                if (painelDadosAtivo != null && formularioDadosAtivo != null) {
                    painelDadosAtivo.setPatientSelectionListener(formularioDadosAtivo);
                    LOGGER.info("Painéis conectados com sucesso");
                }
                
                // Atualizar interface
                refreshContentPainel(painelDadosAtivo);
                refreshContentFormulario(formularioDadosAtivo);
                
                // Limpar referências dos outros painéis
                painelSaudeAtivo = null;
                formularioSaudeAtivo = null;
                
                LOGGER.info("=== onDadosSelected CONCLUÍDO ===");
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao ativar painel Dados", e);
                showNotification("❌ Erro ao carregar painel Dados");
            }
        });
    }
    
    @Override
    public void onRecarregarClicked() {
        if (recarregandoDados || inicializando) {
            LOGGER.info("Recarregamento já em andamento ou sistema inicializando...");
            return;
        }

        recarregandoDados = true;
        showNotification("Recarregando sistema completo...");

        LOGGER.info("Iniciando recarregamento completo do sistema");

        try {
            // Verificar conectividade
            if (!isApiDisponivel()) {
                LOGGER.warning("API não disponível para recarregamento");
                showNotification("API não disponível para recarregamento");
                tentarReconexaoAutomatica();
                return;
            }

            // 1. REINICIALIZAR SERVICES
            LOGGER.info("1/4 - Reinicializando services...");
            showNotification("Reinicializando conexões...");
            inicializarServices();
            
            // Re-registrar Listeners 
            registrarListeners();

            // 2. RECARREGAR TODOS OS DADOS
            LOGGER.info("2/4 - Carregando dados completos...");
            showNotification("Carregando dados do servidor...");
            carregarDadosCompletos();

            // 3. RECRIAR TODOS OS PAINÉIS
            LOGGER.info("3/4 - Recriando painéis...");
            showNotification("Atualizando interface...");
            recriarTodosPaineis();

            // 4. ATUALIZAR INTERFACE ATUAL
            LOGGER.info("4/4 - Atualizando interface atual...");
            int abaSelecionada = menu31.getSelectedTab();
            if (abaSelecionada == 0) {
                showNotification("Carregando painel Saúde...");
                onSaudeSelected();
            } else {
                showNotification("Carregando painel Dados...");
                onDadosSelected();
            }

            showNotification("Sistema recarregado com sucesso!");
            LOGGER.info("Recarregamento completo do sistema concluído com sucesso");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro durante recarregamento completo do sistema", e);
            showNotification("Erro ao recarregar: " + e.getMessage());

            // Oferecer opção de reconexão
            int opcao = JOptionPane.showConfirmDialog(this,
                "Erro ao recarregar sistema:\n" + e.getMessage() + 
                "\n\nDeseja tentar reconectar à API?",
                "Erro de Recarregamento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (opcao == JOptionPane.YES_OPTION) {
                tentarReconexao();
            }
        } finally {
            recarregandoDados = false;
        }
    }
    
    private void recriarTodosPaineis() {
        try {
            LOGGER.info("=== RECRIANDO TODOS OS PAINÉIS ===");

            // Criar cópias thread-safe dos dados atualizados
            List<Paciente> pacientesAtualizados;
            List<Especialidade> especialidadesAtualizadas;
            List<PacienteEspecialidade> associacoesAtualizadas;

            synchronized (this) {
                pacientesAtualizados = new ArrayList<>(pacientes);
                especialidadesAtualizadas = new ArrayList<>(especialidades);
                associacoesAtualizadas = new ArrayList<>(pacienteEspecialidades);
            }

            // RECRIAR PAINÉIS SAÚDE
            LOGGER.info("Recriando painéis Saúde...");
            painelSaudeAtivo = new PainelSaude2(pacientesAtualizados);
            formularioSaudeAtivo = new FormularioSaude2P(
                pacienteService, 
                pacienteEspecialidadeService, 
                especialidadeService, 
                especialidadesAtualizadas
            );

            // Configurar painéis Saúde
            if (formularioSaudeAtivo != null) {
                formularioSaudeAtivo.setPreferredSize(new Dimension(500, 640));
                formularioSaudeAtivo.setPatientUpdateListener(this);
            }

            if (painelSaudeAtivo != null && formularioSaudeAtivo != null) {
                painelSaudeAtivo.setPatientSelectionListener(formularioSaudeAtivo);
            }

            // RECRIAR PAINÉIS DADOS
            LOGGER.info("Recriando painéis Dados...");
            painelDadosAtivo = new PainelDados2(pacientesAtualizados, associacoesAtualizadas);
            formularioDadosAtivo = new FormularioDados2P(
                pacienteService, 
                pacienteEspecialidadeService, 
                especialidadeService, 
                especialidadesAtualizadas
            );

            // Configurar painéis Dados
            if (formularioDadosAtivo != null) {
                formularioDadosAtivo.setPatientUpdateListener(this);
            }

            if (painelDadosAtivo != null && formularioDadosAtivo != null) {
                painelDadosAtivo.setPatientSelectionListener(formularioDadosAtivo);
            }

            LOGGER.info("Todos os painéis recriados com sucesso");
            LOGGER.info("Painéis Saúde: " + (painelSaudeAtivo != null ? "OK" : "NULL") + 
                       " | " + (formularioSaudeAtivo != null ? "OK" : "NULL"));
            LOGGER.info("Painéis Dados: " + (painelDadosAtivo != null ? "OK" : "NULL") + 
                       " | " + (formularioDadosAtivo != null ? "OK" : "NULL"));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao recriar painéis", e);
            throw new RuntimeException("Falha ao recriar painéis", e);
        }
    }

   
    
    // Implementar os métodos da interface PatientUpdateListener:
    @Override
    public void onPatientUpdated(Paciente pacienteAtualizado) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("🔄 Paciente atualizado via formulário: " + pacienteAtualizado.getNome());
            
            synchronized (pacientes) {
                // Atualizar na lista local
                boolean encontrado = false;
                for (int i = 0; i < pacientes.size(); i++) {
                    if (pacientes.get(i).getId() != null && 
                        pacientes.get(i).getId().equals(pacienteAtualizado.getId())) {
                        pacientes.set(i, pacienteAtualizado);
                        encontrado = true;
                        LOGGER.info("Paciente atualizado na lista local: posição " + i);
                        break;
                    }
                }
                
                if (!encontrado) {
                    LOGGER.warning("Paciente não encontrado na lista local para atualização");
                }
            }
            
            // **CORREÇÃO CRÍTICA**: Atualizar AMBOS os painéis, independente de qual está ativo
            if (painelSaudeAtivo != null) {
                painelSaudeAtivo.atualizarPaciente(pacienteAtualizado);
                LOGGER.info("PainelSaude2 atualizado");
            }
            if (painelDadosAtivo != null) {
                painelDadosAtivo.atualizarPaciente(pacienteAtualizado);
                LOGGER.info("PainelDados2 atualizado");
            }
            
            showNotification("✅ Tabela atualizada: " + pacienteAtualizado.getNome());
        });
    }

    @Override
    public void onPatientDeleted(int pacienteId) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("🗑️ Paciente removido via formulário: ID " + pacienteId);
            
            String nomePaciente = "ID " + pacienteId;
            
            synchronized (pacientes) {
                // Encontrar o nome antes de remover (para notificação)
                nomePaciente = pacientes.stream()
                    .filter(p -> p.getId() != null && p.getId().equals(pacienteId))
                    .map(Paciente::getNome)
                    .findFirst()
                    .orElse("ID " + pacienteId);
                
                // Remover da lista local
                boolean removido = pacientes.removeIf(p -> p.getId() != null && p.getId().equals(pacienteId));
                
                if (removido) {
                    LOGGER.info("Paciente removido da lista local");
                } else {
                    LOGGER.warning("Paciente não encontrado na lista local para remoção");
                }
            }
            
            // **CORREÇÃO CRÍTICA**: Atualizar AMBOS os painéis, independente de qual está ativo
            if (painelSaudeAtivo != null) {
                painelSaudeAtivo.removerPaciente(pacienteId);
                LOGGER.info("Paciente removido do PainelSaude2");
            }
            if (painelDadosAtivo != null) {
                painelDadosAtivo.removerPaciente(pacienteId);
                LOGGER.info("Paciente removido do PainelDados2");
            }
            
            showNotification("🗑️ Paciente removido da tabela: " + nomePaciente);
        });
    }
    
    // ===== IMPLEMENTAÇÃO DOS LISTENERS DE MUDANÇAS =====
     
    @Override
    public void onPacienteEspecialidadeAdded(PacienteEspecialidade pacienteEspecialidade) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("🆕 Nova associação via WebSocket: Paciente " + 
                       pacienteEspecialidade.getPacienteId() + " - Especialidade " + 
                       pacienteEspecialidade.getEspecialidadeId());

            synchronized (pacienteEspecialidades) {
                // Verificar se já existe
                boolean jaExiste = pacienteEspecialidades.stream()
                    .anyMatch(pe -> pe.getPacienteId().equals(pacienteEspecialidade.getPacienteId()) && 
                                   pe.getEspecialidadeId().equals(pacienteEspecialidade.getEspecialidadeId()));

                if (!jaExiste) {
                    pacienteEspecialidades.add(pacienteEspecialidade);

                    // **CORREÇÃO: Atualizar com cópia thread-safe**
                    List<PacienteEspecialidade> associacoesAtualizadas = new ArrayList<>(pacienteEspecialidades);
                    
                    // Atualizar apenas o painel de dados se estiver ativo
                    if (painelDadosAtivo != null) {
                        painelDadosAtivo.atualizarPacienteEspecialidade(associacoesAtualizadas);
                        LOGGER.info("PainelDados2 atualizado com nova associação");
                    }

                    showNotification("➕ Nova associação criada");
                    LOGGER.info("Associação adicionada ao cache local");
                } else {
                    LOGGER.info("Associação já existe no cache local");
                }
            }
        });
    }

    @Override
    public void onPacienteEspecialidadeUpdated(PacienteEspecialidade pacienteEspecialidade) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("🔄 Associação atualizada via WebSocket: Paciente " + 
                       pacienteEspecialidade.getPacienteId() + " - Especialidade " + 
                       pacienteEspecialidade.getEspecialidadeId());

            synchronized (pacienteEspecialidades) {
                // Encontrar e atualizar na lista local
                boolean atualizado = false;
                for (int i = 0; i < pacienteEspecialidades.size(); i++) {
                    PacienteEspecialidade pe = pacienteEspecialidades.get(i);
                    if (pe.getPacienteId().equals(pacienteEspecialidade.getPacienteId()) && 
                        pe.getEspecialidadeId().equals(pacienteEspecialidade.getEspecialidadeId())) {
                        pacienteEspecialidades.set(i, pacienteEspecialidade);
                        atualizado = true;
                        break;
                    }
                }

                if (atualizado) {
                    // **CORREÇÃO: Atualizar com cópia thread-safe**
                    List<PacienteEspecialidade> associacoesAtualizadas = new ArrayList<>(pacienteEspecialidades);
                    
                    if (painelDadosAtivo != null) {
                        painelDadosAtivo.atualizarPacienteEspecialidade(associacoesAtualizadas);
                        LOGGER.info("PainelDados2 atualizado com associação modificada");
                    }
                    showNotification("🔄 Associação atualizada");
                    LOGGER.info("Associação atualizada no cache local");
                } else {
                    LOGGER.warning("Associação para atualização não encontrada no cache");
                }
            }
        });
    }


    @Override
    public void onPacienteEspecialidadeDeleted(Integer pacienteId, Integer especialidadeId) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("🗑️ Associação removida via WebSocket: Paciente " + pacienteId + " - Especialidade " + especialidadeId);

            synchronized (pacienteEspecialidades) {
                // Remover da lista local
                boolean removido = pacienteEspecialidades.removeIf(pe -> 
                    pe.getPacienteId().equals(pacienteId) && pe.getEspecialidadeId().equals(especialidadeId));

                if (removido) {
                    // **CORREÇÃO: Atualizar com cópia thread-safe**
                    List<PacienteEspecialidade> associacoesAtualizadas = new ArrayList<>(pacienteEspecialidades);
                    
                    if (painelDadosAtivo != null) {
                        painelDadosAtivo.atualizarPacienteEspecialidade(associacoesAtualizadas);
                        LOGGER.info("PainelDados2 atualizado após remoção de associação");
                    }
                    showNotification("🗑️ Associação removida");
                    LOGGER.info("Associação removida do cache local");
                } else {
                    LOGGER.warning("Associação para remoção não encontrada no cache");
                }
            }
        });
    }
    
    @Override
    public void onPacienteEspecialidadeBatchCreated(Integer pacienteId, List<PacienteEspecialidade> novasAssociacoes) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("=== BATCH CREATED - Main ===");
            LOGGER.info("🆕 Batch de associações criadas via WebSocket para paciente " + pacienteId + 
                       ": " + (novasAssociacoes != null ? novasAssociacoes.size() : 0) + " associações");

            if (novasAssociacoes != null && !novasAssociacoes.isEmpty()) {
                synchronized (pacienteEspecialidades) {
                    int adicionadas = 0;

                    for (PacienteEspecialidade associacao : novasAssociacoes) {
                        // ✅ CORREÇÃO: Verificar duplicatas antes de adicionar
                        boolean jaExiste = pacienteEspecialidades.stream()
                            .anyMatch(pe -> pe.getPacienteId().equals(associacao.getPacienteId()) && 
                                           pe.getEspecialidadeId().equals(associacao.getEspecialidadeId()));

                        if (!jaExiste) {
                            pacienteEspecialidades.add(associacao);
                            adicionadas++;
                            LOGGER.info("✅ Nova associação adicionada: Paciente " + associacao.getPacienteId() + 
                                       " - Especialidade " + associacao.getEspecialidadeId());
                        } else {
                            LOGGER.info("⚠️ Associação já existe: Paciente " + associacao.getPacienteId() + 
                                       " - Especialidade " + associacao.getEspecialidadeId());
                        }
                    }

                    LOGGER.info("📊 Resultado: " + adicionadas + " associações novas adicionadas de " + novasAssociacoes.size());
                    LOGGER.info("📊 Total de associações no cache: " + pacienteEspecialidades.size());

                    if (adicionadas > 0) {
                        atualizarPaineisComAssociacoes();
                        showNotification("➕ " + adicionadas + " associações criadas para paciente " + pacienteId);
                        validarConsistenciaAssociacoes(pacienteId);
                    } else {
                        LOGGER.info("⚠️ Nenhuma associação nova foi adicionada (todas já existiam)");
                        showNotification("⚠️ Associações já existentes - nada foi alterado");
                    }
                }
            } else {
                LOGGER.warning("⚠️ Lista de novas associações está vazia ou nula");
            }
        });
    }

    @Override
    public void onPacienteEspecialidadeBatchDeleted(Integer pacienteId, List<PacienteEspecialidade> associacoesDeletadas) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("=== BATCH DELETED - Main ===");
            LOGGER.info("🗑️ Batch de associações deletadas via WebSocket para paciente " + pacienteId);

            synchronized (pacienteEspecialidades) {
                if (associacoesDeletadas != null && !associacoesDeletadas.isEmpty()) {
                    // ✅ CORREÇÃO: Remover associações específicas
                    LOGGER.info("Removendo " + associacoesDeletadas.size() + " associações específicas");

                    int removidas = 0;
                    for (PacienteEspecialidade associacao : associacoesDeletadas) {
                        boolean removido = pacienteEspecialidades.removeIf(pe -> 
                            pe.getPacienteId().equals(associacao.getPacienteId()) && 
                            pe.getEspecialidadeId().equals(associacao.getEspecialidadeId()));

                        if (removido) {
                            removidas++;
                            LOGGER.info("✅ Associação removida: Paciente " + associacao.getPacienteId() + 
                                       " - Especialidade " + associacao.getEspecialidadeId());
                        } else {
                            LOGGER.warning("⚠️ Associação não encontrada para remover: Paciente " + 
                                         associacao.getPacienteId() + " - Especialidade " + associacao.getEspecialidadeId());
                        }
                    }

                    LOGGER.info("📊 Resultado: " + removidas + " associações removidas de " + associacoesDeletadas.size());

                    if (removidas > 0) {
                        atualizarPaineisComAssociacoes();
                        showNotification("🗑️ " + removidas + " associações removidas do paciente " + pacienteId);
                    }

                } else {
                    // ✅ CORREÇÃO: Remover TODAS as associações do paciente
                    LOGGER.info("Removendo TODAS as associações do paciente " + pacienteId);

                    long removidasTotal = pacienteEspecialidades.stream()
                        .filter(pe -> pe.getPacienteId().equals(pacienteId))
                        .count();

                    boolean removido = pacienteEspecialidades.removeIf(pe -> pe.getPacienteId().equals(pacienteId));

                    LOGGER.info("📊 Associações encontradas para remoção: " + removidasTotal);
                    LOGGER.info("📊 Remoção executada: " + removido);

                    if (removido && removidasTotal > 0) {
                        LOGGER.info("✅ Todas as " + removidasTotal + " associações do paciente foram removidas");
                        atualizarPaineisComAssociacoes();
                        showNotification("🗑️ Todas as " + removidasTotal + " associações removidas do paciente " + pacienteId);
                    } else {
                        LOGGER.info("⚠️ Nenhuma associação encontrada para remover do paciente " + pacienteId);
                    }
                }

                LOGGER.info("📊 Total de associações no cache após remoção: " + pacienteEspecialidades.size());
            }
        });
    }

    @Override
    public void onPacienteEspecialidadeCompleteUpdate(Integer pacienteId, List<PacienteEspecialidade> novasAssociacoes) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("=== COMPLETE UPDATE - Main ===");
            LOGGER.info("🔄 Atualização completa das associações via WebSocket para paciente " + pacienteId + 
                       ": " + (novasAssociacoes != null ? novasAssociacoes.size() : 0) + " associações");

            synchronized (pacienteEspecialidades) {
                // ✅ PASSO 1: Remover TODAS as associações existentes do paciente
                long removidasAnteriormente = pacienteEspecialidades.stream()
                    .filter(pe -> pe.getPacienteId().equals(pacienteId))
                    .count();

                boolean removido = pacienteEspecialidades.removeIf(pe -> pe.getPacienteId().equals(pacienteId));
                LOGGER.info("📊 Associações anteriores removidas: " + removidasAnteriormente + " (operação: " + removido + ")");

                // ✅ PASSO 2: Adicionar TODAS as novas associações
                int adicionadas = 0;
                if (novasAssociacoes != null && !novasAssociacoes.isEmpty()) {
                    pacienteEspecialidades.addAll(novasAssociacoes);
                    adicionadas = novasAssociacoes.size();
                    LOGGER.info("📊 Novas associações adicionadas: " + adicionadas);

                    // Log detalhado das associações adicionadas
                    for (PacienteEspecialidade assoc : novasAssociacoes) {
                        LOGGER.info("  → Associação: Paciente " + assoc.getPacienteId() + 
                                   " - Especialidade " + assoc.getEspecialidadeId());
                    }
                }

                // ✅ PASSO 3: Atualizar interface
                atualizarPaineisComAssociacoes();

                showNotification("🔄 Associações atualizadas completamente para paciente " + pacienteId + 
                               ": " + adicionadas + " associações ativas");

                LOGGER.info("✅ Atualização completa concluída: " + removidasAnteriormente + 
                           " removidas, " + adicionadas + " adicionadas");
                LOGGER.info("📊 Total final de associações no cache: " + pacienteEspecialidades.size());
            }
            
            validarConsistenciaAssociacoes(pacienteId);
        });
    }
    
    // Método para atualizar painéis com associações de forma thread-safe
    private void atualizarPaineisComAssociacoes() {
        try {
            // Criar cópia thread-safe das associações atualizadas
            List<PacienteEspecialidade> associacoesAtualizadas;
            synchronized (pacienteEspecialidades) {
                associacoesAtualizadas = new ArrayList<>(pacienteEspecialidades);
            }

            LOGGER.info("📊 Atualizando painéis com " + associacoesAtualizadas.size() + " associações");

            // Atualizar apenas o painel de dados se estiver ativo
            if (painelDadosAtivo != null) {
                painelDadosAtivo.atualizarPacienteEspecialidade(associacoesAtualizadas);
                LOGGER.info("✅ PainelDados2 atualizado com associações");

                // ✅ ADICIONAL: Forçar repaint para garantir que a UI seja atualizada
                painelDadosAtivo.repaint();
                painelDadosAtivo.revalidate();
            } else {
                LOGGER.info("⚠️ PainelDados2 não está ativo - não foi atualizado");
            }

            // Atualizar formulários se estiverem ativos
            if (formularioDadosAtivo != null) {
                try {
                    // Tentar chamar método de atualização do formulário se existir
                    LOGGER.info("📝 Notificando formulário sobre mudanças nas associações");
                    // formularioDadosAtivo.atualizarAssociacoes(); // Implementar se necessário
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erro ao atualizar formulário", e);
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Erro ao atualizar painéis com associações", e);
        }
    }
    
    private void validarConsistenciaAssociacoes(Integer pacienteId) {
        if (!isApiDisponivel()) {
            LOGGER.info("API não disponível - pulando validação de consistência");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                // Buscar associações do servidor
                List<PacienteEspecialidade> associacoesServidor = 
                    pacienteEspecialidadeService.buscarPorPacienteId(pacienteId);

                // Buscar associações locais
                List<PacienteEspecialidade> associacoesLocais;
                synchronized (pacienteEspecialidades) {
                    associacoesLocais = pacienteEspecialidades.stream()
                        .filter(pe -> pe.getPacienteId().equals(pacienteId))
                        .collect(Collectors.toList());
                }

                // Comparar
                if (associacoesServidor.size() != associacoesLocais.size()) {
                    LOGGER.warning("⚠️ INCONSISTÊNCIA DETECTADA: Servidor tem " + associacoesServidor.size() + 
                                 " associações, local tem " + associacoesLocais.size());

                    SwingUtilities.invokeLater(() -> {
                        int opcao = JOptionPane.showConfirmDialog(this,
                            "Inconsistência detectada entre dados locais e servidor.\n" +
                            "Servidor: " + associacoesServidor.size() + " associações\n" +
                            "Local: " + associacoesLocais.size() + " associações\n\n" +
                            "Deseja recarregar os dados?",
                            "Inconsistência de Dados",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                        if (opcao == JOptionPane.YES_OPTION) {
                            onRecarregarClicked();
                        }
                    });
                } else {
                    LOGGER.info("✅ Consistência validada: " + associacoesServidor.size() + " associações");
                }

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erro na validação de consistência", e);
            }
        });
    }
    
    @Override
    public void onPacienteAdded(Paciente paciente) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("🆕 Novo paciente via WebSocket: " + paciente.getNome());
            
            synchronized (pacientes) {
                // Verificar se já existe (evitar duplicatas)
                boolean jaExiste = pacientes.stream()
                    .anyMatch(p -> p.getId() != null && p.getId().equals(paciente.getId()));
                
                if (!jaExiste) {
                    pacientes.add(paciente);
                    
                    // Atualizar painéis ativos
                    if (painelSaudeAtivo != null) {
                        painelSaudeAtivo.adicionarPaciente(paciente);
                    }
                    if (painelDadosAtivo != null) {
                        painelDadosAtivo.adicionarPaciente(paciente);
                    }
                    
                    showNotification("➕ Novo paciente: " + paciente.getNome());
                    LOGGER.info("Paciente adicionado ao cache local: " + paciente.getNome());
                } else {
                    LOGGER.info("Paciente já existe no cache local: " + paciente.getId());
                }
            }
        });
    }

    @Override
    public void onPacienteUpdated(Paciente paciente) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("=== onPacienteUpdated WebSocket - Main ===");
            LOGGER.info("Paciente recebido: " + 
                       (paciente != null ? paciente.getNome() + " (ID: " + paciente.getId() + ")" : "NULL"));

            if (paciente == null) {
                LOGGER.severe("❌ Paciente NULL recebido via WebSocket");
                return;
            }

            // Atualizar cache local de forma thread-safe
            synchronized (pacientes) {
                boolean atualizado = false;
                for (int i = 0; i < pacientes.size(); i++) {
                    if (pacientes.get(i).getId() != null && pacientes.get(i).getId().equals(paciente.getId())) {
                        LOGGER.info("Atualizando paciente na posição " + i + " da lista local");
                        pacientes.set(i, paciente);
                        atualizado = true;
                        break;
                    }
                }

                if (!atualizado) {
                    LOGGER.warning("⚠️ Paciente não encontrado na lista local - adicionando");
                    pacientes.add(paciente);
                }
            }

            // Atualizar AMBOS os painéis de forma robusta
            try {
                boolean painelAtualizado = false;

                if (painelSaudeAtivo != null) {
                    LOGGER.info("Atualizando PainelSaude2...");
                    painelSaudeAtivo.atualizarPaciente(paciente);
                    painelAtualizado = true;
                    LOGGER.info("✅ PainelSaude2 atualizado");
                }

                if (painelDadosAtivo != null) {
                    LOGGER.info("Atualizando PainelDados2...");
                    painelDadosAtivo.atualizarPaciente(paciente);
                    painelAtualizado = true;
                    LOGGER.info("✅ PainelDados2 atualizado");
                }

                if (painelAtualizado) {
                    showNotification("🔄 Dados atualizados via WebSocket: " + paciente.getNome());
                    LOGGER.info("✅ Notificação de atualização WebSocket concluída");
                } else {
                    LOGGER.warning("⚠️ Nenhum painel ativo para atualizar");
                }

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "❌ Erro ao atualizar painéis via WebSocket", e);
            }
        });
    }

    @Override
    public void onPacienteDeleted(int pacienteId) {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("🗑️ Paciente removido via WebSocket: ID " + pacienteId);
            
            synchronized (pacientes) {
                // Encontrar o nome antes de remover (para notificação)
                String nomePaciente = pacientes.stream()
                    .filter(p -> p.getId() != null && p.getId().equals(pacienteId))
                    .map(Paciente::getNome)
                    .findFirst()
                    .orElse("ID " + pacienteId);
                
                // Remover da lista local
                boolean removido = pacientes.removeIf(p -> p.getId() != null && p.getId().equals(pacienteId));
                
                if (removido) {
                    // Atualizar painéis ativos
                    if (painelSaudeAtivo != null) {
                        painelSaudeAtivo.removerPaciente(pacienteId);
                    }
                    if (painelDadosAtivo != null) {
                        painelDadosAtivo.removerPaciente(pacienteId);
                    }
                    
                    showNotification("🗑️ Paciente removido: " + nomePaciente);
                    LOGGER.info("Paciente removido do cache local: " + nomePaciente);
                } else {
                    LOGGER.warning("Paciente para remoção não encontrado no cache: " + pacienteId);
                }
            }
        });
    }
    
    // ===== MÉTODOS DE GERENCIAMENTO DE DADOS =====
   
    
    /**
     * Verifica se a API está disponível
     */
    private boolean isApiDisponivel() {
        return apiManager != null && apiManager.isApiDisponivel();
    }
   
    // ===== MÉTODOS DE CONECTIVIDADE =====
    
    /**
     * Tenta reconexão automática
     */
    private void tentarReconexaoAutomatica() {
        if (apiManager != null && !recarregandoDados) {
            LOGGER.info("🔄 Tentando reconexão automática...");
            
            CompletableFuture.runAsync(() -> {
                try {
                    apiManager.reconectar();
                    
                    if (apiManager.isApiDisponivel()) {
                        SwingUtilities.invokeLater(() -> {
                            showNotification("✅ Reconexão bem-sucedida!");
                            
                            // Tentar recarregar dados após reconexão
                            onRecarregarClicked();
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            showNotification("❌ Falha na reconexão automática");
                        });
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erro na reconexão automática", e);
                    SwingUtilities.invokeLater(() -> {
                        showNotification("❌ Erro na reconexão: " + e.getMessage());
                    });
                }
            });
        }
    }
    
    /**
     * Método público para tentar reconexão (pode ser chamado por botões da UI)
     */
    public void tentarReconexao() {
        if (apiManager != null) {
            showNotification("🔄 Tentando reconectar...");

            CompletableFuture.runAsync(() -> {
                try {
                    LOGGER.info("Iniciando processo de reconexão...");
                    apiManager.reconectar();

                    SwingUtilities.invokeLater(() -> {
                        if (apiManager.isApiDisponivel()) {
                            // ✅ CORREÇÃO: Re-inicializar services E re-registrar listeners
                            try {
                                inicializarServices();
                                registrarListeners(); // CRÍTICO: Re-registrar listeners

                                showNotification("✅ Reconexão bem-sucedida!");

                                // Opcionalmente, recarregar dados
                                int opcao = JOptionPane.showConfirmDialog(this,
                                    "Reconexão bem-sucedida!\n\nDeseja recarregar os dados?",
                                    "Reconexão Bem-sucedida",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);

                                if (opcao == JOptionPane.YES_OPTION) {
                                    onRecarregarClicked();
                                }
                            } catch (Exception e) {
                                LOGGER.log(Level.SEVERE, "Erro ao reinicializar após reconexão", e);
                                showNotification("❌ Erro na reinicialização após reconexão");
                            }
                        } else {
                            showNotification("❌ Falha na reconexão");
                        }
                    });

                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erro na tentativa de reconexão", e);
                    SwingUtilities.invokeLater(() -> {
                        showNotification("❌ Erro na reconexão: " + e.getMessage());
                    });
                }
            });
        }
    }
    
    /**
     * Executa diagnóstico completo do sistema
     */
    public void executarDiagnostico() {
        if (apiManager != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    apiManager.executarDiagnostico();
                    String status = apiManager.getStatusCompleto();
                    
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, 
                            status, 
                            "Diagnóstico do Sistema", 
                            JOptionPane.INFORMATION_MESSAGE);
                    });
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erro no diagnóstico", e);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, 
                            "Erro ao executar diagnóstico:\n" + e.getMessage(), 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, 
                "ApiManager não inicializado", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ===== MÉTODOS DE INTERFACE =====
    
    /**
     * Exibe notificação temporária
     */
    private void showNotification(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Cria um JWindow para a notificação
                JWindow window = new JWindow();
                JLabel label = new JLabel(message);
                label.setOpaque(true);
                label.setBackground(new Color(60, 63, 65));
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                window.getContentPane().add(label);
                window.pack();

                // Define a posição no canto inferior direito
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = screenSize.width - window.getWidth() - 20;
                int y = screenSize.height - window.getHeight() - 50;
                window.setLocation(x, y);

                // Exibe a notificação
                window.setVisible(true);

                // Fecha automaticamente após 3 segundos
                Timer timer = new Timer(3000, e -> window.dispose());
                timer.setRepeats(false);
                timer.start();
                
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erro ao exibir notificação", e);
            }
        });
        
        // Log no console
        System.out.println("📢 " + message);
    }
    
    /**
     * Atualiza o painel principal
     */
    private void refreshContentPainel(Component painel) {
        if (painel != null && content != null) {
            content.removeAll();

            // Usar BorderLayout conforme guia
            if (painel instanceof JPanel) {
                // Se for tabela, adicionar JScrollPane
                JScrollPane scrollPane = new JScrollPane(painel);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                content.add(scrollPane, BorderLayout.CENTER);
            } else {
                content.add(painel, BorderLayout.CENTER);
            }

            content.revalidate();
            content.repaint();
        }
    }
    
    /**
     * Atualiza o painel do formulário
     */
    private void refreshContentFormulario(Component painel) {
        LOGGER.info("=== refreshContentFormulario INICIADO ===");

        if (painel != null && content2 != null) {
            content2.removeAll();

            // Usar BorderLayout conforme guia
            content2.add(painel, BorderLayout.CENTER);

            // Garantir tamanhos mínimos
            painel.setMinimumSize(new Dimension(300, 400));

            content2.revalidate();
            content2.repaint();

            LOGGER.info("=== refreshContentFormulario CONCLUÍDO ===");
        }
    }
    
    // ===== MÉTODOS PÚBLICOS PARA ACESSO EXTERNO =====
    
    /**
     * Obtém a lista atual de pacientes (thread-safe)
     */
    public List<Paciente> getPacientes() {
        synchronized (pacientes) {
            return new ArrayList<>(pacientes);
        }
    }
    
    /**
     * Obtém a lista atual de especialidades (thread-safe)
     */
    public List<Especialidade> getEspecialidades() {
        synchronized (especialidades) {
            return new ArrayList<>(especialidades);
        }
    }
    
    /**
     * Obtém a lista atual de associações (thread-safe)
     */
    public List<PacienteEspecialidade> getPacienteEspecialidades() {
        synchronized (pacienteEspecialidades) {
            return new ArrayList<>(pacienteEspecialidades);
        }
    }
    
    /**
     * Obtém o status completo do sistema
     */
    public String getStatusSistema() {
        if (apiManager == null) {
            return "Sistema não inicializado";
        }
        
        StringBuilder status = new StringBuilder();
        status.append("=== STATUS DO SISTEMA IBG ===\n");
        status.append("Inicializando: ").append(inicializando ? "SIM" : "NÃO").append("\n");
        status.append("Recarregando: ").append(recarregandoDados ? "SIM" : "NÃO").append("\n");
        status.append("Pacientes em cache: ").append(pacientes.size()).append("\n");
        status.append("Especialidades em cache: ").append(especialidades.size()).append("\n");
        status.append("Associações em cache: ").append(pacienteEspecialidades.size()).append("\n");
        status.append("\n");
        status.append(apiManager.getStatusCompleto());
        
        return status.toString();
    }
    
    /**
     * Obtém referência para o service de pacientes
     */
    public PacienteService getPacienteService() {
        return pacienteService;
    }
    
    /**
     * Obtém referência para o service de especialidades
     */
    public EspecialidadeService getEspecialidadeService() {
        return especialidadeService;
    }
    
    /**
     * Obtém referência para o service de associações
     */
    public PacienteEspecialidadeService getPacienteEspecialidadeService() {
        return pacienteEspecialidadeService;
    }
    
    // ===== CLEANUP E FINALIZAÇÃO =====
    
    /**
     * Limpa recursos ao fechar a aplicação
     */
    @Override
    public void dispose() {
        try {
            LOGGER.info("🔄 Finalizando aplicação...");
            
            // Remover listeners
            if (apiManager != null) {
                apiManager.removePacienteChangeListener(this);
                apiManager.removePacienteEspecialidadeChangeListener(this);
            }
            
            // Finalizar ApiManager
            if (apiManager != null) {
                apiManager.finalizar();
            }
            
            LOGGER.info("✅ Recursos liberados com sucesso");
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao finalizar aplicação", e);
        } finally {
            super.dispose();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menu31 = new com.mycompany.view.Menu3();
        content = new javax.swing.JPanel();
        content2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        menu31.setPreferredSize(new java.awt.Dimension(215, 700));
        getContentPane().add(menu31, java.awt.BorderLayout.LINE_START);

        content.setBackground(new java.awt.Color(255, 255, 255));
        content.setMaximumSize(null);
        content.setName(""); // NOI18N
        content.setPreferredSize(new java.awt.Dimension(600, 400));

        javax.swing.GroupLayout contentLayout = new javax.swing.GroupLayout(content);
        content.setLayout(contentLayout);
        contentLayout.setHorizontalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 750, Short.MAX_VALUE)
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        getContentPane().add(content, java.awt.BorderLayout.CENTER);

        content2.setMinimumSize(new java.awt.Dimension(300, 400));
        content2.setPreferredSize(new java.awt.Dimension(550, 500));

        javax.swing.GroupLayout content2Layout = new javax.swing.GroupLayout(content2);
        content2.setLayout(content2Layout);
        content2Layout.setHorizontalGroup(
            content2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
        );
        content2Layout.setVerticalGroup(
            content2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        getContentPane().add(content2, java.awt.BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Configurar Look and Feel do sistema para melhor integração
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback para Nimbus se disponível
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Erro ao configurar Look and Feel", ex);
            }
        }

        /* Create and display the form */
        SwingUtilities.invokeLater(() -> {
            try {
                // Primeiro inicializar o sistema via Projeto_IBG
                // (isso garante que o ApiManager seja criado corretamente)
                
                Main mainFrame = new Main();
                mainFrame.setVisible(true);
                
                System.out.println("✅ Interface gráfica iniciada com sucesso");
                
            } catch (Exception e) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Erro ao iniciar interface", e);
                
                JOptionPane.showMessageDialog(null, 
                    "Erro crítico ao iniciar interface:\n" + e.getMessage(), 
                    "Erro de Inicialização", 
                    JOptionPane.ERROR_MESSAGE);
                
                System.exit(1);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel content;
    private javax.swing.JPanel content2;
    private com.mycompany.view.Menu3 menu31;
    // End of variables declaration//GEN-END:variables

   
 
}
