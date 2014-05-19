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

import com.projectsexception.sqlitediff.model.Column;
import com.projectsexception.sqlitediff.model.Table;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author FedeProEx <fede at projectsexception.com>
 */
public class ModelGenerator {
    
    private static final String[] INTEGER_TYPES = {
        "INTEGER",
        "INT",
        "TINYINT",
        "SMALLINT",
        "MEDIUMINT",
        "BIGINT",
        "UNSIGNED BIG INT",
        "INT2",
        "INT8"
    };
    
    private static final String[] TEXT_TYPES = {
        "TEXT",
        "CHARACTER",
        "VARCHAR",
        "VARYING CHARACTER",
        "NCHAR",
        "NATIVE CHARACTER",
        "NVARCHAR",
        "CLOB"
    };
    
    private static final String[] REAL_TYPES = {
        "REAL",
        "DOUBLE",
        "DOUBLE PRECISION",
        "FLOAT"
    };
    
    private static final String[] NUMERIC_TYPES = {
        "NUMERIC",
        "DECIMAL",
        "BOOLEAN",
        "DATE",
        "DATETIME"
    };
    
    private static final String[][] TYPES = {
        INTEGER_TYPES,
        TEXT_TYPES,
        REAL_TYPES,
        NUMERIC_TYPES
    };
    
    public Collection<Table> readTables(Connection connection) throws SQLException {
        Statement statement = generateStatement(connection);
        Collection<Table> tables = queryTableNames(statement);
        if (tables != null && !tables.isEmpty()) {
            for (Table table : tables) {
                table.setColumns(queryColumns(statement, table.getName()));
            }
        }
        return tables;
    }
    
    private Collection<Table> queryTableNames(Statement statement) throws SQLException {
        Collection<Table> tables = new ArrayList<Table>();
        ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name <> 'sqlite_sequence'");
        while (rs.next()) {
            tables.add(new Table(rs.getString("name")));
        }
        rs.close();
        return tables;
    }

    private Collection<Column> queryColumns(Statement statement, String name) throws SQLException {
        List<Column> columns = new ArrayList<Column>();
        boolean autoincrement = verifyAutoincrement(statement, name);
        ResultSet rs = statement.executeQuery("PRAGMA table_info('" + name + "')");
        while (rs.next()) {
            columns.add(generateColumnFromPragma(rs, autoincrement));
        }
        Collections.sort(columns);
        return columns;
    }

    private boolean verifyAutoincrement(Statement statement, String name) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT sql FROM sqlite_master WHERE name = '" + name + "'");
        boolean autoincrement = false;
        if (rs.next()) {
            autoincrement = rs.getString(1).toUpperCase().contains("PRIMARY KEY AUTOINCREMENT");
        }
        rs.close();
        return autoincrement;
    }

    private Column generateColumnFromPragma(ResultSet rs, boolean autoincrement) throws SQLException {
        Column column = new Column(rs.getString("name"));
        column.setType(Column.Type.valueOf(normalizeType(rs.getString("type"))));
        column.setNotNull(rs.getInt("notnull") > 0);
        column.setPrimaryKey(rs.getInt("pk") > 0);
        if (column.isPrimaryKey()) {
            column.setAutoIncrement(autoincrement);
        }
        return column;
    }

    private Statement generateStatement(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);
        return statement;
    }

    private String normalizeType(String stringType) {
        for (String[] types : TYPES) {            
            for (String type : types) {
                if (stringType.startsWith(type)) {
                    return types[0];
                }
            }
        }
        throw new IllegalArgumentException("Unrecognized data type: " + stringType);
    }
    
}
