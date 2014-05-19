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

package com.projectsexception.sqlitediff.model;

/**
 *
 * @author FedeProEx <fede at projectsexception.com>
 */
public class Column implements Comparable<Column> {

    public static enum Type {        
        INTEGER, 
        TEXT,
        REAL,
        NUMERIC        
    }
    
    private String name;
    private Type type;
    private boolean primaryKey;
    private boolean autoIncrement;
    private boolean notNull;

    public Column() {
    }

    public Column(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    @Override
    public int compareTo(Column o) {
        if (primaryKey && !o.isPrimaryKey()) {
            return -1;
        } else if (o.primaryKey && !primaryKey) {
            return 1;
        } else if (name == null) {
            return o.getName() == null ? 0 : 1;
        } else {
            return name.compareTo(o.getName());
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 53 * hash + (this.primaryKey ? 1 : 0);
        hash = 53 * hash + (this.autoIncrement ? 1 : 0);
        hash = 53 * hash + (this.notNull ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Column other = (Column) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.primaryKey != other.primaryKey) {
            return false;
        }
        if (this.autoIncrement != other.autoIncrement) {
            return false;
        }
        if (this.notNull != other.notNull) {
            return false;
        }
        return true;
    }
    
}
