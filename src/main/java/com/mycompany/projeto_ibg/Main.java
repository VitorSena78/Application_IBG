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
    
    /**
     * Configurações básicas da janela principal
     */
    private void configurarJanela() {
        setTitle("Sistema IBG - Gestão de Saúde");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        // Registrar como listener do menu
        menu31.addMenuListener(this);
        
        LOGGER.info("Janela principal configurada");
    }
    
    /**
     * Inicialização completa do sistema
     */
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
    
    /**
     * Inicializa o painel padrão (Saúde) durante a inicialização do sistema
     */
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
    
    /**
     * Inicializa o ApiManager
     */
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
    
     /**
     * Carrega configurações da aplicação
     */
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
    
    /**
     * Inicializa os services
     */
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
    
    /**
     * Inicializa modo offline
     */
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
    
    /**
     * Trata erros de inicialização
     */
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
    
    /**
     * Registra listeners para notificações em tempo real
     */
    private void registrarListeners() {
        try {
            if (apiManager != null && apiManager.isWebSocketConectado()) {
                apiManager.addPacienteChangeListener(this);
                apiManager.addPacienteEspecialidadeChangeListener(this);
                LOGGER.info("✅ Listeners registrados para notificações em tempo real");
            } else {
                LOGGER.warning("⚠️ WebSocket não disponível - listeners não registrados");
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
            LOGGER.info("🔄 Paciente atualizado via WebSocket: " + paciente.getNome());
            
            synchronized (pacientes) {
                // Encontrar e atualizar na lista local
                boolean atualizado = false;
                for (int i = 0; i < pacientes.size(); i++) {
                    if (pacientes.get(i).getId() != null && pacientes.get(i).getId().equals(paciente.getId())) {
                        pacientes.set(i, paciente);
                        atualizado = true;
                        break;
                    }
                }
                
                if (atualizado) {
                    // Atualizar painéis ativos
                    if (painelSaudeAtivo != null) {
                        painelSaudeAtivo.atualizarPaciente(paciente);
                    }
                    if (painelDadosAtivo != null) {
                        painelDadosAtivo.atualizarPaciente(paciente);
                    }
                    
                    showNotification("🔄 Paciente atualizado: " + paciente.getNome());
                    LOGGER.info("Paciente atualizado no cache local: " + paciente.getNome());
                } else {
                    LOGGER.warning("Paciente para atualização não encontrado no cache: " + paciente.getId());
                    // Adicionar se não existe
                    pacientes.add(paciente);
                }
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
                    apiManager.reconectar();
                    
                    SwingUtilities.invokeLater(() -> {
                        if (apiManager.isApiDisponivel()) {
                            // Reinicializar services
                            inicializarServices();
                            showNotification("✅ Reconexão bem-sucedida!");
                            
                            // Recarregar dados
                            onRecarregarClicked();
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
        if (painel != null) {
            painel.setSize(800, 640);
            painel.setLocation(0, 0);
            
            content.removeAll();
            content.add(painel, BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        }
    }
    
    /**
     * Atualiza o painel do formulário
     */
    private void refreshContentFormulario(Component painel) {
        LOGGER.info("=== refreshContentFormulario INICIADO ===");
        LOGGER.info("Painel recebido: " + (painel != null ? painel.getClass().getSimpleName() : "NULL"));
        LOGGER.info("content2 disponível: " + (content2 != null ? "SIM" : "NÃO"));

        if (painel != null && content2 != null) {
            // Debug: Estado antes da atualização
            LOGGER.info("content2 components antes: " + content2.getComponentCount());

            // Configurar tamanho e posição
            painel.setSize(500, 640);
            painel.setLocation(0, 0);

            // Remover conteúdo anterior
            content2.removeAll();
            LOGGER.info("content2 limpo");

            // Adicionar novo painel
            content2.add(painel, BorderLayout.CENTER);
            LOGGER.info("Painel adicionado ao content2");

            // Forçar revalidação e repaint
            content2.revalidate();
            content2.repaint();

            // Verificar se o painel foi adicionado
            LOGGER.info("content2 components depois: " + content2.getComponentCount());

            // Tornar visível se necessário
            if (!painel.isVisible()) {
                painel.setVisible(true);
                LOGGER.info("Painel tornado visível");
            }

            // Debug adicional - verificar hierarquia
            LOGGER.info("Painel visible: " + painel.isVisible());
            LOGGER.info("Painel size: " + painel.getSize());
            LOGGER.info("content2 visible: " + content2.isVisible());
            LOGGER.info("content2 size: " + content2.getSize());

            LOGGER.info("=== refreshContentFormulario CONCLUÍDO ===");

        } else {
            LOGGER.warning("refreshContentFormulario: painel ou content2 é NULL");
            if (painel == null) LOGGER.warning("  - painel é NULL");
            if (content2 == null) LOGGER.warning("  - content2 é NULL");
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

        content.setBackground(new java.awt.Color(255, 255, 255));
        content.setPreferredSize(new java.awt.Dimension(800, 0));

        javax.swing.GroupLayout contentLayout = new javax.swing.GroupLayout(content);
        content.setLayout(contentLayout);
        contentLayout.setHorizontalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        content2.setPreferredSize(new java.awt.Dimension(500, 0));

        javax.swing.GroupLayout content2Layout = new javax.swing.GroupLayout(content2);
        content2.setLayout(content2Layout);
        content2Layout.setHorizontalGroup(
            content2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );
        content2Layout.setVerticalGroup(
            content2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(menu31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(content, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(content2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menu31, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
            .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
            .addComponent(content2, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

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
