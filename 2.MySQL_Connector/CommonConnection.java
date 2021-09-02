package com.jayden.study.connection;

import com.jayden.study.utils.JdbcUtils;

import java.sql.*;

/**
 * JDBC Common Connection
 *
 * <p>Typical flow of using JDBC</p>
 *
 * 1. Load the JDBC driver
 * 2. Create a Connection
 * 3. Create a Statement and Get a ResultSet
 * 4. Traverse and Process the ResultSet
 * 5. Close ResultSet, Statement and Connection
 *
 * @author jayden-lee
 */
public class CommonConnection {
    //Atributos para conectarse a la BD
    //La url comienza por el driver JDBC, y termina con el parámetro de la zona horaria
    private static final String url = "jdbc:mysql://localhost:3306/kickstarter?serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "qwerty";

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public static void main(String[] args) {
        try {
            loadJdbcDriver();

            createConnection();

            createStatementAndGetResultSet();

            traverseAndProcessResultSet();

            closeResources();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * El driver se conecta al motor de Base de datos
     * 
     * @throws ClassNotFoundException
     */
    private static void loadJdbcDriver() throws ClassNotFoundException {
        // 5.X -> com.mysql.jdbc.Driver
        // 8.X -> com.mysql.cj.jdbc.Driver
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    /**
     * Se conecta la aplicación a la BD usando los atributos
     *
     * @throws SQLException
     */
    private static void createConnection() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    /**
     * Se crea una sentencia con la conexión, se ejecuta y se guarda en resultSet
     *
     * @throws SQLException
     */
    private static void createStatementAndGetResultSet() throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT id, nombre FROM patrocinador WHERE mail LIKE '%gmail.com';");
    }

    /**
     * Imprime los datos de la consulta
     *
     * @throws SQLException
     */
    private static void traverseAndProcessResultSet() throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        // Se almacena el número de tuplas que tiene la consulta
        int columnCount = resultSetMetaData.getColumnCount();

        // Imprime el nombre de las columnas
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(resultSetMetaData.getColumnName(i) + "\t");
        }

        System.out.println();

        // Imprime las tuplas 
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(resultSet.getString(i) + " \t");
            }

            System.out.println();
        }
    }

    /**
     * Se cierran los recursos y conexiones
     */
    private static void closeResources() {
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        JdbcUtils.closeConnection(connection);
    }
}
