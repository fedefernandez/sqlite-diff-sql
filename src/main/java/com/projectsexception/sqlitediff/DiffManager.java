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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author FedeProEx <fede at projectsexception.com>
 */
public class DiffManager {
    
    private final Collection<Table> previousTables;
    private final Collection<Table> newTables;
    private final SqlGenerator sqlGenerator;
    
    public DiffManager(String previousDb, String newDb) throws SQLException {
        DBReader dBReader = new DBReader();
        previousTables = dBReader.readTables(previousDb);
        newTables = dBReader.readTables(newDb);
        sqlGenerator = new SqlGenerator();
    }
    
    public String generateUpdateScript() {
        StringBuilder sql = new StringBuilder();
        if (previousTables != null && newTables != null) {
            sql.append(generateDropTables());
            sql.append(generateCreateTables());
            sql.append(generateUpdateTables());
        }
        return sql.toString();
    }

    private String generateUpdateTables() {
        StringBuilder sql = new StringBuilder();
        Collection<Table> tablesToUpdate = findTablesToUpdate(previousTables, newTables);
        for (Table table : tablesToUpdate) {
            sql.append(sqlGenerator.generateRename(table, "temp")).append(";\n");
            Table newTable = find(table, newTables);
            sql.append(sqlGenerator.generateCreate(newTable, true)).append(";\n");
            sql.append(sqlGenerator.generateInsert(table, newTable, "temp")).append(";\n");
            sql.append(sqlGenerator.generateDrop("temp")).append(";\n");
        }
        return sql.toString();
    }
    
    private String generateCreateTables() {
        StringBuilder sql = new StringBuilder();
        Collection<Table> tablesToAdd = findNonExistentTables(newTables, previousTables);
        for (Table table : tablesToAdd) {
            sql.append(sqlGenerator.generateCreate(table, true)).append(";\n");
        }
        return sql.toString();
    }

    private String generateDropTables() {
        StringBuilder sql = new StringBuilder();
        Collection<Table> tablesToRemove = findNonExistentTables(previousTables, newTables);
        for (Table table : tablesToRemove) {
            sql.append(sqlGenerator.generateDrop(table.getName())).append(";\n");
        }
        return sql.toString();
    }

    private Collection<Table> findNonExistentTables(Collection<Table> what, Collection<Table> inGroup) {
        Collection<Table> tables = new ArrayList<Table>();
        for (Table table : what) {
            if (find(table, inGroup) == null) {
                tables.add(table);
            }
        }
        return tables;
    }

    private Table find(Table table, Collection<Table> tables) {
        for (Table t : tables) {
            if (table.getName().equalsIgnoreCase(t.getName())) {
                return t;
            }
        }
        return null;
    }

    private Collection<Table> findTablesToUpdate(Collection<Table> group1, Collection<Table> group2) {
        Collection<Table> tables = new ArrayList<Table>();
        Table table;
        for (Table t : group1) {
            table = find(t, group2);
            if (!t.equals(table)) {
                tables.add(t);
            }
        }
        return tables;
    }
    
}
