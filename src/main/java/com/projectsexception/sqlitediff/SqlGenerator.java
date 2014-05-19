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
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author FedeProEx <fede at projectsexception.com>
 */
public class SqlGenerator {
    
    public String generateDrop(String tableName) {
        return "DROP TABLE " + tableName;
    }
    
    public String generateCreate(Table table, boolean checkExists) {
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        if (checkExists) {
            sql.append("IF NOT EXISTS ");
        }
        sql.append("\"").append(table.getName()).append("\"");
        if (table.getColumns() != null && !table.getColumns().isEmpty()) {
            sql.append(generateColumnDefinition(table.getColumns()));
        }
        return sql.toString();
    }
    
    public String generateRename(Table table, String temporaryName) {
        return "ALTER TABLE \"" + table.getName() + "\" RENAME TO '" + temporaryName + "'";
    }
    
    public String generateInsert(Table source, Table target, String temporaryName) {
        Collection<String> columnNames = findSameColumns(source.getColumns(), target.getColumns());
        if (columnNames.isEmpty()) {
            return null;
        }
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append("\"").append(target.getName()).append("\"");
        sql.append(" ( ");
        sql.append(generateColumnNames(columnNames));
        sql.append(" ) SELECT ");
        sql.append(generateColumnNames(columnNames));
        sql.append(" FROM ");
        sql.append("\"").append(temporaryName).append("\"");
        return sql.toString();
    }

    private String generateColumnNames(Collection<String> columnNames) {
        StringBuilder sb = null;
        for (String columnName : columnNames) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append(", ");
            }
            sb.append("\"").append(columnName).append("\"");
        }
        return sb == null ? "" : sb.toString();
    }

    private String generateColumnDefinition(Collection<Column> columns) {
        StringBuilder sql = new StringBuilder(" (");
        String sep = "";
        for (Column column : columns) {
            sql
                    .append(sep)
                    .append("\"")
                    .append(column.getName())
                    .append("\"")
                    .append(" ")
                    .append(column.getType().name())
                    .append(generateConstraint(sql, column));
            sep = ", ";
        }
        sql.append(" )");
        return sql.toString();
    }

    private String generateConstraint(StringBuilder sql, Column column) {
        if (column.isAutoIncrement()) {
            return " PRIMARY KEY AUTOINCREMENT";
        } else if (column.isPrimaryKey()) {
            return " PRIMARY KEY";
        } else if (column.isNotNull()) {
            return " NOT NULL";
        } else {
            return "";
        }
    }

    private Collection<String> findSameColumns(Collection<Column> columns1, Collection<Column> columns2) {
        Collection<String> lst = new ArrayList<String>();
        if (columns1 != null && !columns1.isEmpty() && columns2 != null && !columns2.isEmpty()) {
            String name;
            for (Column column : columns1) {
                name = findColumn(column, columns2);
                if (name != null) {
                    lst.add(name);
                }
            }
        }
        return lst;
    }

    private String findColumn(Column column, Collection<Column> columns) {
        for (Column c : columns) {
            if (c.equals(column)) {
                return c.getName();
            }
        }
        return null;
    }
    
}
