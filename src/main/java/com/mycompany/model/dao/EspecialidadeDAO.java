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

/**
 * Classe DAO para operações com a tabela Especialidade
 */
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
        String sql = "SELECT idEspecialidade, nome FROM Especialidade ORDER BY nome";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Especialidade e = new Especialidade();
                e.setIdEspecialidade(rs.getInt("idEspecialidade"));
                e.setNome(rs.getString("nome"));
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

        String sql = "SELECT idEspecialidade, nome FROM Especialidade WHERE idEspecialidade = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Especialidade e = new Especialidade();
                    e.setIdEspecialidade(rs.getInt("idEspecialidade"));
                    e.setNome(rs.getString("nome"));
                    
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
        if (especialidade == null || especialidade.getIdEspecialidade() <= 0 || 
            especialidade.getNome() == null || especialidade.getNome().trim().isEmpty()) {
            LOGGER.warning("Dados inválidos para atualização de especialidade");
            return false;
        }

        String sql = "UPDATE Especialidade SET nome = ? WHERE idEspecialidade = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, especialidade.getNome().trim());
            stmt.setInt(2, especialidade.getIdEspecialidade());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info("Especialidade atualizada: ID " + especialidade.getIdEspecialidade());
                return true;
            } else {
                LOGGER.warning("Nenhuma especialidade encontrada para atualizar: ID " + especialidade.getIdEspecialidade());
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar especialidade: ID " + especialidade.getIdEspecialidade(), e);
            return false;
        }
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

        String sql = "DELETE FROM Especialidade WHERE idEspecialidade = ?";
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