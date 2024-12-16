package org.acgproject;

import java.io.*;
import java.sql.*;
import java.util.Locale;
import org.apache.commons.csv.*;

import static org.acgproject.ProcessCSV.removeAcentos;

public class UploudCSV {

    private static final String CSV_FILE_PATH = "basededados/terminais-de-onibus-al-ibge-2022.csv";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/terminais_onibus";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123321";
    private static final String INSERT_QUERY = """
        INSERT INTO terminal (nome, municipio, localizacao)
        VALUES (?, ?, ST_GeomFromText(?, 4326))
    """;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        try (Connection conn = getDatabaseConnection()) {
            processCSV(conn);
        } catch (Exception e) {
            System.err.println("Erro ao importar dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static void processCSV(Connection conn) throws IOException, SQLException {
        try (
                InputStream csvStream = UploudCSV.class.getClassLoader().getResourceAsStream(CSV_FILE_PATH);
                Reader reader = new InputStreamReader(csvStream, "UTF-8");
                CSVParser csvParser = new CSVParser(reader, criarFormatoCSV())
        ) {
            for (CSVRecord record : csvParser) {
                processRecord(conn, record);
            }
        }
    }

    private static CSVFormat criarFormatoCSV() {
        return CSVFormat.Builder.create()
                .setHeader()
                .setDelimiter(';')
                .setTrim(true)
                .setQuote(null)
                .build();
    }

    private static void processRecord(Connection conn, CSVRecord record) {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY)) {
            String nome = processarNome(record.get("nome"));
            String municipio = processarMunicipio(record.get("municipio"));
            double latitude = processarLatitude(record.get("Latitude"));
            double longitude = processarLongitude(record.get("Longitude"));

            stmt.setString(1, nome);
            stmt.setString(2, municipio);
            stmt.setString(3, String.format("POINT(%f %f)", latitude, longitude));

            stmt.executeUpdate();
        } catch (NumberFormatException | SQLException e) {
            System.err.printf("Erro ao processar registro: %s. Erro: %s%n", record, e.getMessage());
        }
    }

    private static String processarNome(String nome) {
        return removeAcentos(nome.replaceAll("^\"|\"$", ""));
    }

    private static String processarMunicipio(String municipio) {
        return removeAcentos(municipio.trim().replaceAll("^\"|\"$", ""));
    }

    private static double processarLatitude(String latitude) {
        return Double.parseDouble(latitude.replace(",", ".").replaceAll("^\"|\"$", ""));
    }

    private static double processarLongitude(String longitude) {
        return Double.parseDouble(longitude.replace(",", ".").replaceAll("^\"|\"$", ""));
    }
}
