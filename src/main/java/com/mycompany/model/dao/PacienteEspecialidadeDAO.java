package com.mycompany.model.dao;

import com.mycompany.conection.ConnectionFactory;
import com.mycompany.model.bean.PacienteEspecialidade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

/**
 * DAO para gerenciar a tabela de relacionamento PacienteEspecialidade (N:N)
 * @author vitor
 */
public class PacienteEspecialidadeDAO {
    private Connection con;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Formato para o banco
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Formato de entrada comum

    public PacienteEspecialidadeDAO() {
        this.con = ConnectionFactory.getConection();
    }
    
    /**
     * Converte uma string de data para java.sql.Date
     * Aceita formatos: dd/MM/yyyy ou yyyy-MM-dd
     */
    private java.sql.Date convertStringToSqlDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            java.util.Date parsedDate;
            if (dateString.contains("/")) {
                parsedDate = inputDateFormat.parse(dateString);
            } else {
                parsedDate = dateFormat.parse(dateString);
            }
            return new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {
            System.err.println("Erro ao converter data: " + dateString + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Converte java.sql.Date para String no formato dd/MM/yyyy
     */
    private String convertSqlDateToString(java.sql.Date sqlDate) {
        if (sqlDate == null) {
            return null;
        }
        return inputDateFormat.format(sqlDate);
    }

    /**
     * Obtém a data atual formatada como String
     */
    private String getDataAtual() {
        return inputDateFormat.format(new java.util.Date());
    }
    
    //Insere uma lista de associações PacienteEspecialidade na tabela
    public boolean inserirLista(List<PacienteEspecialidade> listaPacienteEspecialidade) {
        if (listaPacienteEspecialidade == null || listaPacienteEspecialidade.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Lista vazia ou nula. Nenhuma associação para inserir.");
            return false;
        }

        String sql = "INSERT INTO Paciente_has_Especialidade (Paciente_id, Especialidade_id, data_atendimento) VALUES (?, ?, ?)";

        // Desabilita o autocommit para usar transação
        boolean autoCommitOriginal = false;
        try {
            autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false);

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                int insercoesRealizadas = 0;
                int duplicatasEncontradas = 0;

                for (PacienteEspecialidade pe : listaPacienteEspecialidade) {
                    // Verifica se a associação já existe antes de inserir
                    if (existeAssociacao(pe.getPacienteId(), pe.getEspecialidadeId())) {
                        duplicatasEncontradas++;
                        System.out.println("Associação já existe: Paciente ID " + pe.getPacienteId() + 
                                         " - Especialidade ID " + pe.getEspecialidadeId());
                        continue; // Pula para o próximo item
                    }

                    stmt.setInt(1, pe.getPacienteId());
                    stmt.setInt(2, pe.getEspecialidadeId());

                    // Converte String para java.sql.Date
                    java.sql.Date sqlDate = convertStringToSqlDate(pe.getDataAtendimento());
                    stmt.setDate(3, sqlDate);

                    stmt.addBatch(); // Adiciona ao lote
                    insercoesRealizadas++;
                }

                if (insercoesRealizadas > 0) {
                    // Executa todas as inserções em lote
                    int[] resultados = stmt.executeBatch();

                    // Verifica se todas as inserções foram bem-sucedidas
                    for (int resultado : resultados) {
                        if (resultado == PreparedStatement.EXECUTE_FAILED) {
                            throw new SQLException("Falha em uma das inserções do lote");
                        }
                    }

                    // Confirma a transação
                    con.commit();

                    System.out.printf(
                        "Inserção em lote concluída!\n✓ %d associações inseridas com sucesso\n%s",
                        insercoesRealizadas,
                        duplicatasEncontradas > 0 
                            ? duplicatasEncontradas + " associações já existiam e foram ignoradas.\n" 
                            : ""
                    );
                    
                    return true;

                } else {
                    con.rollback();
                    JOptionPane.showMessageDialog(null, 
                        "Nenhuma nova associação foi inserida.\n" +
                        (duplicatasEncontradas > 0 ? 
                            "Todas as " + duplicatasEncontradas + " associações já existiam." : 
                            "Lista estava vazia."));
                    return false;
                }

            } catch (SQLException e) {
                // Desfaz a transação em caso de erro
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erro ao inserir lista de associações: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            // Restaura o autocommit original
            try {
                con.setAutoCommit(autoCommitOriginal);
            } catch (SQLException e) {
                System.err.println("Erro ao restaurar autocommit: " + e.getMessage());
            }
        }
    }

    // INSERT - Associa um paciente a uma especialidade
    public boolean inserir(PacienteEspecialidade pe) {
        String sql = "INSERT INTO Paciente_has_Especialidade (Paciente_id, Especialidade_id, data_atendimento) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, pe.getPacienteId());
            stmt.setInt(2, pe.getEspecialidadeId());
            
            // Converte String para java.sql.Date
            java.sql.Date sqlDate = convertStringToSqlDate(pe.getDataAtendimento());
            stmt.setDate(3, sqlDate);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Associação salva com sucesso");
                return true;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar associação: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // SELECT * - Lista todas as associações
    public List<PacienteEspecialidade> listarTodos() {
        List<PacienteEspecialidade> lista = new ArrayList<>();
        String sql = "SELECT * FROM Paciente_has_Especialidade";
        
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                PacienteEspecialidade pe = carregarDoResultSet(rs);
                lista.add(pe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // SELECT por paciente_id - Lista todas as especialidades de um paciente
    public List<PacienteEspecialidade> buscarPorPacienteId(int pacienteId) {
        List<PacienteEspecialidade> lista = new ArrayList<>();
        String sql = "SELECT * FROM Paciente_has_Especialidade WHERE Paciente_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, pacienteId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PacienteEspecialidade pe = carregarDoResultSet(rs);
                    lista.add(pe);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // SELECT por especialidade_id - Lista todos os pacientes de uma especialidade
    public List<PacienteEspecialidade> buscarPorEspecialidadeId(int especialidadeId) {
        List<PacienteEspecialidade> lista = new ArrayList<>();
        String sql = "SELECT * FROM Paciente_has_Especialidade WHERE Especialidade_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, especialidadeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PacienteEspecialidade pe = carregarDoResultSet(rs);
                    lista.add(pe);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // SELECT específico - Busca uma associação específica por paciente e especialidade
    public PacienteEspecialidade buscarPorPacienteEEspecialidade(int pacienteId, int especialidadeId) {
        String sql = "SELECT * FROM Paciente_has_Especialidade WHERE Paciente_id = ? AND Especialidade_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, pacienteId);
            stmt.setInt(2, especialidadeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return carregarDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE - Atualiza a data de atendimento de uma associação
    public boolean atualizar(PacienteEspecialidade pe) {
        String sql = "UPDATE Paciente_has_Especialidade SET data_atendimento = ? WHERE Paciente_id = ? AND Especialidade_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            // Converte String para java.sql.Date
            java.sql.Date sqlDate = convertStringToSqlDate(pe.getDataAtendimento());
            stmt.setDate(1, sqlDate);
            stmt.setInt(2, pe.getPacienteId());
            stmt.setInt(3, pe.getEspecialidadeId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE - Remove uma associação específica
    public boolean deletar(int pacienteId, int especialidadeId) {
        String sql = "DELETE FROM Paciente_has_Especialidade WHERE Paciente_id = ? AND Especialidade_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, pacienteId);
            stmt.setInt(2, especialidadeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE todas as associações de um paciente
    public boolean deletarPorPacienteId(int pacienteId) {
        String sql = "DELETE FROM Paciente_has_Especialidade WHERE Paciente_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, pacienteId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE todas as associações de uma especialidade
    public boolean deletarPorEspecialidadeId(int especialidadeId) {
        String sql = "DELETE FROM Paciente_has_Especialidade WHERE Especialidade_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, especialidadeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Verifica se uma associação já existe
    public boolean existeAssociacao(int pacienteId, int especialidadeId) {
        String sql = "SELECT COUNT(*) FROM Paciente_has_Especialidade WHERE Paciente_id = ? AND Especialidade_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, pacienteId);
            stmt.setInt(2, especialidadeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método auxiliar para montar objeto PacienteEspecialidade a partir do ResultSet
    private PacienteEspecialidade carregarDoResultSet(ResultSet rs) throws SQLException {
        PacienteEspecialidade pe = new PacienteEspecialidade();
        pe.setPacienteId(rs.getInt("Paciente_id"));
        pe.setEspecialidadeId(rs.getInt("Especialidade_id"));
        
        // Converte java.sql.Date para String
        java.sql.Date sqlDate = rs.getDate("data_atendimento");
        String dataString = convertSqlDateToString(sqlDate);
        pe.setDataAtendimento(dataString);
        
        return pe;
    }
}