/*
 * Copyright 2014 FedeProEx <fede at projectsexception.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.projectsexception.sqlitediff;

import com.projectsexception.sqlitediff.model.Table;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FedeProEx <fede at projectsexception.com>
 */
public class DBReader {
    
    public Collection<Table> readTables(String dbPath) throws SQLException {        
        checkDriver();
        checkDbPath(dbPath);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            ModelGenerator modelGenerator = new ModelGenerator();
            return modelGenerator.readTables(connection);            
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    private void checkDriver() throws IllegalStateException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void checkDbPath(String dbPath) {
        File file = new File(dbPath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Can't read database: " + dbPath);
        }
    }
    
}
