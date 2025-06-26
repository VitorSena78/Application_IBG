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
     * Converte java.util.Date para java.sql.Date
     */
    private java.sql.Date convertUtilDateToSqlDate(java.util.Date utilDate) {
        if (utilDate == null) {
            return null;
        }
        return new java.sql.Date(utilDate.getTime());
    }

    // INSERT - Associa um paciente a uma especialidade
    public boolean inserir(PacienteEspecialidade pe) {
        String sql = "INSERT INTO PacienteEspecialidade (paciente_id, especialidade_id, data_atendimento) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, pe.getPacienteId());
            stmt.setInt(2, pe.getEspecialidadeId());
            
            // Converte java.util.Date para java.sql.Date
            java.sql.Date sqlDate = convertUtilDateToSqlDate(pe.getDataAtendimento());
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
        String sql = "SELECT * FROM PacienteEspecialidade";
        
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
        String sql = "SELECT * FROM PacienteEspecialidade WHERE paciente_id = ?";
        
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
        String sql = "SELECT * FROM PacienteEspecialidade WHERE especialidade_id = ?";
        
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
        String sql = "SELECT * FROM PacienteEspecialidade WHERE paciente_id = ? AND especialidade_id = ?";
        
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
        String sql = "UPDATE PacienteEspecialidade SET data_atendimento = ? WHERE paciente_id = ? AND especialidade_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            // Converte java.util.Date para java.sql.Date
            java.sql.Date sqlDate = convertUtilDateToSqlDate(pe.getDataAtendimento());
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
        String sql = "DELETE FROM PacienteEspecialidade WHERE paciente_id = ? AND especialidade_id = ?";
        
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
        String sql = "DELETE FROM PacienteEspecialidade WHERE paciente_id = ?";
        
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
        String sql = "DELETE FROM PacienteEspecialidade WHERE especialidade_id = ?";
        
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
        String sql = "SELECT COUNT(*) FROM PacienteEspecialidade WHERE paciente_id = ? AND especialidade_id = ?";
        
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
        pe.setPacienteId(rs.getInt("paciente_id"));
        pe.setEspecialidadeId(rs.getInt("especialidade_id"));
        
        // Converte java.sql.Date para java.util.Date
        java.sql.Date sqlDate = rs.getDate("data_atendimento");
        if (sqlDate != null) {
            pe.setDataAtendimento(new java.util.Date(sqlDate.getTime()));
        }
        
        return pe;
    }
}