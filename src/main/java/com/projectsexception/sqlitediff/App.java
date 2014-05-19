package com.projectsexception.sqlitediff;

import java.sql.SQLException;

public class App {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        if (args != null && args.length == 2) {
            DiffManager diffManager = new DiffManager();
            System.out.println(diffManager.generateUpdateScript(args[0], args[1]));
        }
    }
}
