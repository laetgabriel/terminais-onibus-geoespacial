package org.acgproject;

import java.io.*;
import java.sql.*;
import java.util.Locale;

import org.apache.commons.csv.*;

import static org.acgproject.ProcessCSV.removeAcentos;

public class UploudCSV {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        String csvFilePath = "basededados/terminais-de-onibus-al-ibge-2022.csv";

        String dbUrl = "jdbc:mysql://localhost:3306/terminais_onibus";
        String user = "root";
        String password = "123321";

        String insertQuery = """
            INSERT INTO terminais (nome, municipio, localizacao)
            VALUES (?, ?, ST_GeomFromText(?, 4326))
        """;

        try (
                Connection conn = DriverManager.getConnection(dbUrl, user, password);
                PreparedStatement stmt = conn.prepareStatement(insertQuery);

                InputStream csvStream = UploudCSV.class.getClassLoader().getResourceAsStream(csvFilePath);
                Reader reader = new InputStreamReader(csvStream, "UTF-8");

                CSVParser csvParser = new CSVParser(reader,
                        CSVFormat.Builder.create()
                                .setHeader()
                                .setDelimiter(';')
                                .setTrim(true)
                                .setQuote(null)
                                .build()
                )
        ) {
            for (CSVRecord record : csvParser) {
                try {
                    String nome = removeAcentos(record.get("nome").replaceAll("^\"|\"$", ""));
                    String municipio = removeAcentos(record.get("municipio").trim().replaceAll("^\"|\"$", ""));
                    double latitude = Double.parseDouble(record.get("Latitude").replace(",", ".").replaceAll("^\"|\"$", ""));
                    double longitude = Double.parseDouble(record.get("Longitude").replace(",", ".").replaceAll("^\"|\"$", ""));

                    stmt.setString(1, nome);
                    stmt.setString(2, municipio);
                    stmt.setString(3, String.format("POINT(%f %f)", latitude, longitude));

                    stmt.executeUpdate();
                } catch (NumberFormatException | SQLException e) {
                    System.err.printf("Erro ao processar registro: %s. Erro: %s%n", record, e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao importar dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
