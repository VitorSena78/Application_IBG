package com.mycompany.conection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionFactory {
    
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver"; // << CORRIGIDO
    private static final String URL = "jdbc:mysql://localhost:3306/clinica";
    private static final String USER = "root";
    private static final String PASS = "Senha@123";
    
    public static Connection getConection(){
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException ex) {
            System.getLogger(ConnectionFactory.class.getName()).log(System.Logger.Level.ERROR, "Driver não encontrado", ex);
            throw new RuntimeException("Erro: Driver JDBC não encontrado", ex);
        } catch (SQLException ex) {
            System.getLogger(ConnectionFactory.class.getName()).log(System.Logger.Level.ERROR, "Erro ao conectar ao banco", ex);
            throw new RuntimeException("Erro: Não foi possível conectar ao banco de dados", ex);
        }
    }
    
    public static void closeConnection(Connection con){
        try {
            if(con!=null){
                con.close();
            }
        } catch (SQLException ex) {
            System.getLogger(ConnectionFactory.class.getName()).log(System.Logger.Level.ERROR, "Erro ao fechar conexão", ex);
        }
    }
    
    public static void closeConnection(Connection con, PreparedStatement stmt){
        closeConnection(con);
        try {
            if (stmt!=null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            System.getLogger(ConnectionFactory.class.getName()).log(System.Logger.Level.ERROR, "Erro ao fechar statement", ex);
        }
    }
    
    public static void closeConnection(Connection con, PreparedStatement stmt, ResultSet rs){
        closeConnection(con, stmt);
        try {
            if (rs!=null) {
                rs.close();
            }
        } catch (SQLException ex) {
            System.getLogger(ConnectionFactory.class.getName()).log(System.Logger.Level.ERROR, "Erro ao fechar result set", ex);
        }
    }
}
