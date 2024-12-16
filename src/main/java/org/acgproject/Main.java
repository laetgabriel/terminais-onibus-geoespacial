package org.acgproject;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        CRUD crud = new CRUD();
        try {
/*
            List<Terminal> terminais = crud.obterTerminalPorRaio(15000);
            for (Terminal t : terminais) {
                System.out.println(t);
            }
*/

            crud.excluirTerminal(39);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
