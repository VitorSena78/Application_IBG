package com.mycompany.model.dao;

import com.mycompany.conection.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.mycompany.model.bean.Paciente;
import javax.swing.JOptionPane;

/**
 *
 * @author vitor
 */
public class PacienteDAO {
    private Connection con;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Formato para o banco
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Formato de entrada comum

    public PacienteDAO() {
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
            // Tenta primeiro o formato dd/MM/yyyy
            if (dateString.contains("/")) {
                parsedDate = inputDateFormat.parse(dateString);
            } else {
                // Assume formato yyyy-MM-dd
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

    // INSERT
    public boolean inserir(Paciente p) {
        String sql = "INSERT INTO Paciente (nome, data_nascimento, idade, nome_da_mae, cpf, sus, telefone, endereço, pa_x_mmhg, fc_bpm, fr_ibpm, temperatura_c, hgt_mgld, spo2, peso, altura, imc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, p.getNome());

            // Converte String para java.sql.Date
            java.sql.Date sqlDate = convertStringToSqlDate(p.getDataNascimento());
            stmt.setDate(2, sqlDate);

            stmt.setObject(3, p.getIdade(), Types.INTEGER);
            stmt.setString(4, p.getNomeDaMae());
            stmt.setString(5, p.getCpf());
            stmt.setString(6, p.getSus());
            stmt.setString(7, p.getTelefone());
            stmt.setString(8, p.getEndereco());
            stmt.setObject(9, p.getPaXMmhg());
            stmt.setObject(10, p.getFcBpm(), Types.FLOAT);
            stmt.setObject(11, p.getFrIbpm(), Types.FLOAT);
            stmt.setObject(12, p.getTemperaturaC(), Types.FLOAT);
            stmt.setObject(13, p.getHgtMgld(), Types.FLOAT);
            stmt.setObject(14, p.getSpo2(), Types.FLOAT);
            stmt.setObject(15, p.getPeso(), Types.FLOAT);
            stmt.setObject(16, p.getAltura(), Types.FLOAT);
            stmt.setObject(17, p.getImc(), Types.FLOAT);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                JOptionPane.showMessageDialog(null, "Salvo com sucesso");
                return true;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // SELECT * (listar todos)
    public List<Paciente> listarTodos() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM Paciente";
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {//rs onde sera guardado os resultados
            while (rs.next()) {
                Paciente p = carregarDoResultSet(rs);
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // SELECT por ID
    public Paciente buscarPorId(int id) {
    String sql = "SELECT * FROM Paciente WHERE id = ?";

    try (
        PreparedStatement stmt = con.prepareStatement(sql)
    ) {
        stmt.setInt(1, id);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return carregarDoResultSet(rs);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace(); // ou logue corretamente
    }

    return null;
}

    // UPDATE
    public boolean atualizar(Paciente p) {
        String sql = "UPDATE Paciente SET nome = ?, data_nascimento = ?, idade = ?, nome_da_mae = ?, cpf = ?, sus = ?, telefone = ?, endereço = ?, pa_x_mmhg = ?, fc_bpm = ?, fr_ibpm = ?, temperatura_c = ?, hgt_mgld = ?, spo2 = ?, peso = ?, altura = ?, imc = ? WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            
            // Converte String para java.sql.Date
            java.sql.Date sqlDate = convertStringToSqlDate(p.getDataNascimento());
            stmt.setDate(2, sqlDate);
            
            stmt.setObject(3, p.getIdade(), Types.INTEGER);
            stmt.setString(4, p.getNomeDaMae());
            stmt.setString(5, p.getCpf());
            stmt.setString(6, p.getSus());
            stmt.setString(7, p.getTelefone());
            stmt.setString(8, p.getEndereco());
            stmt.setObject(9, p.getPaXMmhg());
            stmt.setObject(10, p.getFcBpm(), Types.FLOAT);
            stmt.setObject(11, p.getFrIbpm(), Types.FLOAT);
            stmt.setObject(12, p.getTemperaturaC(), Types.FLOAT);
            stmt.setObject(13, p.getHgtMgld(), Types.FLOAT);
            stmt.setObject(14, p.getSpo2(), Types.FLOAT);
            stmt.setObject(15, p.getPeso(), Types.FLOAT);
            stmt.setObject(16, p.getAltura(), Types.FLOAT);
            stmt.setObject(17, p.getImc(), Types.FLOAT);
            stmt.setInt(18, p.getId());
            
            System.err.println("stmt enviado: " + stmt);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    
    

    // DELETE
    public boolean deletar(int id) {
        String sql = "DELETE FROM Paciente WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método auxiliar para montar objeto Paciente a partir do ResultSet
    private Paciente carregarDoResultSet(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));

        // Converte java.sql.Date do banco para String
        java.sql.Date sqlDate = rs.getDate("data_nascimento");
        p.setDataNascimento(convertSqlDateToString(sqlDate));

        Object idadeObj = rs.getObject("idade");
        if (idadeObj != null) {
            p.setIdade(((Number) idadeObj).intValue());
        }

        p.setNomeDaMae(rs.getString("nome_da_mae"));
        p.setCpf(rs.getString("cpf"));
        p.setSus(rs.getString("sus"));
        p.setTelefone(rs.getString("telefone"));
        p.setEndereco(rs.getString("endereço"));
        p.setPaXMmhg(rs.getString("pa_x_mmhg"));

        // Conversões seguras para Float
        Object fcObj = rs.getObject("fc_bpm");
        if (fcObj != null) {
            p.setFcBpm(((Number) fcObj).floatValue());
        }

        Object frObj = rs.getObject("fr_ibpm");
        if (frObj != null) {
            p.setFrIbpm(((Number) frObj).floatValue());
        }

        Object tempObj = rs.getObject("temperatura_c");
        if (tempObj != null) {
            p.setTemperaturaC(((Number) tempObj).floatValue());
        }

        Object hgtObj = rs.getObject("hgt_mgld");
        if (hgtObj != null) {
            p.setHgtMgld(((Number) hgtObj).floatValue());
        }

        Object spo2Obj = rs.getObject("spo2");
        if (spo2Obj != null) {
            p.setSpo2(((Number) spo2Obj).floatValue());
        }

        Object pesoObj = rs.getObject("peso");
        if (pesoObj != null) {
            p.setPeso(((Number) pesoObj).floatValue());
        }

        Object alturaObj = rs.getObject("altura");
        if (alturaObj != null) {
            p.setAltura(((Number) alturaObj).floatValue());
        }

        Object imcObj = rs.getObject("imc");
        if (imcObj != null) {
            p.setImc(((Number) imcObj).floatValue());
        }

        return p;
    }
}