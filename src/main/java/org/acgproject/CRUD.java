package org.acgproject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CRUD {

    public List<Terminal> obterTodosOsTerminais() throws SQLException {
        String query = "SELECT nome, municipio, ST_AsText(localizacao) FROM terminal";

        try (Statement stmt = UploudCSV.getDatabaseConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            List<Terminal> terminais = new ArrayList<>();
            while (rs.next()) {
                String nome = rs.getString("nome");
                String municipio = rs.getString("municipio");
                terminais.add(new Terminal(null, nome, municipio));
            }
            return terminais;
        }
    }

    public void addTerminal(String nome, String municipio, double latitude, double longitude) throws SQLException {
        Locale.setDefault(Locale.US);
        String query = """
                    INSERT INTO terminal (nome, municipio, localizacao)
                    VALUES (?, ?, ST_GeomFromText(?, 4326))
                """;

        try (PreparedStatement stmt = UploudCSV.getDatabaseConnection().prepareStatement(query)) {
            stmt.setString(1, nome);
            stmt.setString(2, municipio);
            stmt.setString(3, String.format("POINT(%f %f)", latitude, longitude));
            stmt.executeUpdate();
        }
    }

    public List<Terminal> obterTerminalPorRaio(double raiosEmMetros) throws SQLException {
        Locale.setDefault(Locale.US);
        double latitudePadrao = -9.71094613;
        double longitudePadrao = -35.8949368;

        String query = """
                SELECT nome, municipio, ST_AsText(localizacao)
                FROM terminal
                WHERE ST_Distance(localizacao, ST_GeomFromText(?, 4326)) <= ?
                """;

        String point = String.format("POINT(%f %f)", latitudePadrao, longitudePadrao);

        try (PreparedStatement stmt = UploudCSV.getDatabaseConnection().prepareStatement(query)) {

            stmt.setString(1, point);
            stmt.setDouble(2, raiosEmMetros);

            ResultSet rs = stmt.executeQuery();
            List<Terminal> terminais = new ArrayList<>();
            while (rs.next()) {
                String nome = rs.getString("nome");
                String municipio = rs.getString("municipio");
                terminais.add(new Terminal(null, nome, municipio));
            }
            return terminais;
        }
    }

    public void excluirTerminal(int id) throws SQLException {
        Locale.setDefault(Locale.US);
        String query = """
                DELETE FROM terminal
                WHERE id = ?
                """;

        try (PreparedStatement stmt = UploudCSV.getDatabaseConnection().prepareStatement(query)) {
            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
    }


    public void atualizarTerminal(int id, String nome, String municipio, double latitude, double longitude) throws SQLException {
        Locale.setDefault(Locale.US);
        String query = """
                UPDATE terminal
                SET nome = ?, municipio = ?, localizacao = ST_GeomFromText(?, 4326)
                WHERE id = ?
                """;

        try (PreparedStatement stmt = UploudCSV.getDatabaseConnection().prepareStatement(query)) {
            stmt.setString(1, nome);
            stmt.setString(2, municipio);
            stmt.setString(3, String.format("POINT(%f %f)", latitude, longitude ));
            stmt.setInt(4, id);

            stmt.executeUpdate();
        }
    }


}
