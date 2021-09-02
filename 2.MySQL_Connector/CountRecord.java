package com.jayden.study.query;

import com.jayden.study.utils.JdbcUtils;

import java.sql.*;

/**
 * Cuenta tuplas
 *
 * @author jayden-lee
 */
public class CountRecord {
    //Atributos para conectarse a la BD
    //La url comienza por el driver JDBC, y termina con el parámetro de la zona horaria
    private static final String url = "jdbc:mysql://localhost:3306/kickstarter?serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "qwerty";

    private static Connection connection;

    public static void main(String[] args) {
        try {
            connection = JdbcUtils.getConnection(url, user, password);

            int totalRows = getTotalRows();
            System.out.println("Total Rows : " + totalRows);

            int totalRows2 = getTotalRows2();
            System.out.println("Total Rows : " + totalRows2);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            JdbcUtils.closeConnection(connection);
        }
    }

    /**
     * Cuenta las tuplas de una consulta.
     * No las cuenta, obtiene el número de la última tupla.
     * 
     * @return número de tuplas
     * @throws SQLException 
     */
    private static int getTotalRows() throws SQLException {
        //Guarda la consulta en un String
        String sql = "SELECT * FROM kickstarter.proyecto";

        int totalRows;
        //Crea una sentencia con una conexion
        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            //Guarda el resultado de la consulta guardada en el String
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                //Apunta a la útlima tupla
                resultSet.last();
                //Obtiene el número de la tupla a la que está apuntando (la útlima)
                totalRows = resultSet.getRow();
            } catch (SQLException e) {
                throw e;
            }
        }

        return totalRows;
    }
    /**
     * Cuenta las tuplas de una consulta.
     * 
     * @return número de tuplas
     * @throws SQLException
     */
    private static int getTotalRows2() throws SQLException {
        //Guarda la consulta en un String
        String sql = "SELECT count(*) FROM kickstarter.proyecto";

        int totalRows = 0;

        //Intenta establecer una conexion con la consulta guardada
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                //Ejecuta y guarda el resultado de la consulta 
                ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                //Guarda el número de tuplas de la consulta mediante un método directo
                totalRows = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw e;
        }

        return totalRows;
    }
}
