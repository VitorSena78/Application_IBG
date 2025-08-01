package com.mycompany.model.dao;

import com.mycompany.conection.ConnectionFactory;
import com.mycompany.model.bean.Especialidade;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//Classe DAO para operações com a tabela Especialidade
public class EspecialidadeDAO {
    
    private static final Logger LOGGER = Logger.getLogger(EspecialidadeDAO.class.getName());
    private final Connection connection;

    public EspecialidadeDAO() {
        this.connection = ConnectionFactory.getConection();
    }

    /**
     * Insere uma nova especialidade no banco de dados
     * @param especialidade A especialidade a ser inserida
     * @return true se inserida com sucesso, false caso contrário
     */
    public boolean inserir(Especialidade especialidade) {
        if (especialidade == null || especialidade.getNome() == null || especialidade.getNome().trim().isEmpty()) {
            LOGGER.warning("Tentativa de inserir especialidade inválida");
            return false;
        }

        String sql = "INSERT INTO Especialidade (nome) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, especialidade.getNome().trim());
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info("Especialidade inserida com sucesso: " + especialidade.getNome());
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir especialidade: " + especialidade.getNome(), e);
            return false;
        }
    }

    /**
     * Lista todas as especialidades cadastradas
     * @return Lista de especialidades ou lista vazia em caso de erro
     */
    public List<Especialidade> listarTodas() {
        List<Especialidade> lista = new ArrayList<>();
        String sql = "SELECT id, nome, atendimentos_restantes_hoje, atendimentos_totais_hoje FROM Especialidade ORDER BY nome";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Especialidade e = new Especialidade();
                e.setId(rs.getInt("id"));
                e.setNome(rs.getString("nome"));
                e.setAtendimentosRestantesHoje(rs.getInt("atendimentos_restantes_hoje"));
                e.setAtendimentosTotaisHoje(rs.getInt("atendimentos_totais_hoje"));
                lista.add(e);
            }
            
            LOGGER.info("Listadas " + lista.size() + " especialidades");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar especialidades", e);
        }
        
        return lista;
    }

    /**
     * Busca uma especialidade pelo ID
     * @param id ID da especialidade
     * @return A especialidade encontrada ou null se não encontrada/erro
     */
    public Especialidade buscarPorId(int id) {
        if (id <= 0) {
            LOGGER.warning("ID inválido para busca: " + id);
            return null;
        }

        String sql = "SELECT id, nome, fichas, atendimentos_restantes_hoje, atendimentos_totais_hoje FROM Especialidade WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Especialidade e = new Especialidade();
                    e.setId(rs.getInt("id"));
                    e.setNome(rs.getString("nome"));
                    e.setAtendimentosRestantesHoje(rs.getInt("atendimentos_restantes_hoje"));
                    e.setAtendimentosTotaisHoje(rs.getInt("atendimentos_totais_hoje"));
                    
                    LOGGER.info("Especialidade encontrada: ID " + id);
                    return e;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar especialidade por ID: " + id, e);
        }
        
        LOGGER.info("Especialidade não encontrada: ID " + id);
        return null;
    }

    /**
     * Atualiza uma especialidade existente
     * @param especialidade A especialidade a ser atualizada
     * @return true se atualizada com sucesso, false caso contrário
     */
    public boolean atualizar(Especialidade especialidade) {
        if (especialidade == null || especialidade.getId() <= 0 || 
            especialidade.getNome() == null || especialidade.getNome().trim().isEmpty()) {
            LOGGER.warning("Dados inválidos para atualização de especialidade");
            return false;
        }

        String sql = "UPDATE Especialidade SET nome = ?, fichas = ?, atendimentos_restantes_hoje = ?, atendimentos_totais_hoje = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, especialidade.getNome().trim());
            stmt.setInt(3, especialidade.getAtendimentosRestantesHoje());
            stmt.setInt(4, especialidade.getAtendimentosTotaisHoje());
            stmt.setInt(5, especialidade.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info("Especialidade atualizada: ID " + especialidade.getId());
                return true;
            } else {
                LOGGER.warning("Nenhuma especialidade encontrada para atualizar: ID " + especialidade.getId());
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar especialidade: ID " + especialidade.getId(), e);
            return false;
        }
    }

    /**
     * Reduz em 1 o número de atendimentos restantes da especialidade
     * @param especialidadeId ID da especialidade
     * @return true se atualizada com sucesso, false caso contrário
     */
    public boolean reduzirAtendimentoRestante(int especialidadeId) {
        if (especialidadeId <= 0) {
            LOGGER.warning("ID inválido para redução de atendimento: " + especialidadeId);
            return false;
        }

        String sql = "UPDATE Especialidade SET atendimentos_restantes_hoje = atendimentos_restantes_hoje - 1 " +
                    "WHERE id = ? AND atendimentos_restantes_hoje > 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, especialidadeId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info("Atendimento reduzido para especialidade ID: " + especialidadeId);
                return true;
            } else {
                LOGGER.warning("Não foi possível reduzir atendimento - sem atendimentos restantes ou especialidade não encontrada: ID " + especialidadeId);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao reduzir atendimento da especialidade: ID " + especialidadeId, e);
            return false;
        }
    }

    /**
     * Verifica se a especialidade tem atendimentos disponíveis
     * @param especialidadeId ID da especialidade
     * @return true se tem atendimentos disponíveis
     */
    public boolean temAtendimentosDisponiveis(int especialidadeId) {
        if (especialidadeId <= 0) {
            return false;
        }

        String sql = "SELECT atendimentos_restantes_hoje FROM Especialidade WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, especialidadeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("atendimentos_restantes_hoje") > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar atendimentos disponíveis: ID " + especialidadeId, e);
        }
        
        return false;
    }

    /**
     * Obtém a numeração do próximo atendimento para uma especialidade
     * @param especialidadeId ID da especialidade
     * @return String formatada com a numeração (ex: "3/15") ou null em caso de erro
     */
    public String obterNumeracaoProximoAtendimento(int especialidadeId) {
        if (especialidadeId <= 0) {
            return null;
        }

        String sql = "SELECT atendimentos_restantes_hoje, atendimentos_totais_hoje FROM Especialidade WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, especialidadeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int restantes = rs.getInt("atendimentos_restantes_hoje");
                    int totais = rs.getInt("atendimentos_totais_hoje");
                    
                    if (restantes > 0 && totais > 0) {
                        int numeroAtendimento = totais - restantes + 1;
                        return numeroAtendimento + "/" + totais;
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao obter numeração do atendimento: ID " + especialidadeId, e);
        }
        
        return null;
    }

    /**
     * Deleta uma especialidade pelo ID
     * @param id ID da especialidade a ser deletada
     * @return true se deletada com sucesso, false caso contrário
     */
    public boolean deletar(int id) {
        if (id <= 0) {
            LOGGER.warning("ID inválido para exclusão: " + id);
            return false;
        }

        String sql = "DELETE FROM Especialidade WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info("Especialidade deletada: ID " + id);
                return true;
            } else {
                LOGGER.warning("Nenhuma especialidade encontrada para deletar: ID " + id);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar especialidade: ID " + id, e);
            return false;
        }
    }

    /**
     * Verifica se existe uma especialidade com o nome informado
     * @param nome Nome da especialidade
     * @return true se existe, false caso contrário
     */
    public boolean existePorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM Especialidade WHERE LOWER(nome) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nome.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar existência da especialidade: " + nome, e);
        }
        
        return false;
    }

    /**
     * Fecha a conexão com o banco de dados
     */
    public void fecharConexao() {
        if (connection != null) {
            ConnectionFactory.closeConnection(connection);
            LOGGER.info("Conexão fechada com sucesso");
        }
    }
}