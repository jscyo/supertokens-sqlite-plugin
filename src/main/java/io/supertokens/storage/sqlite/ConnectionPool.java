/*
 *    Copyright (c) 2020, VRAI Labs and/or its affiliates. All rights reserved.
 *
 *    This software is licensed under the Apache License, Version 2.0 (the
 *    "License") as published by the Apache Software Foundation.
 *
 *    You may not use this file except in compliance with the License. You may
 *    obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 *
 */

package io.supertokens.storage.sqlite;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.supertokens.pluginInterface.exceptions.QuitProgramFromPluginException;
import io.supertokens.storage.sqlite.config.Config;
import io.supertokens.storage.sqlite.config.SQLiteConfig;
import io.supertokens.storage.sqlite.output.Logging;
import io.supertokens.storage.sqlite.utils.Utils;

import java.sql.Connection;
import java.sql.SQLException;

class ConnectionPool extends ResourceDistributor.SingletonResource {

    private static final String RESOURCE_KEY = "io.supertokens.storage.sqlite.ConnectionPool";
    private final HikariDataSource ds;

    private ConnectionPool(Start start) {
        if (!start.enabled) {
            throw new RuntimeException("Connection to refused");   // emulates exception thrown by Hikari
        }
        HikariConfig config = new HikariConfig();
        SQLiteConfig userConfig = Config.getConfig(start);

        config.setDriverClassName("org.sqlite.JDBC");
        //have to set the connection url to the file path of the database
        config.setJdbcUrl("jdbc:sqlite:" + Utils.getDatabasePath(userConfig));
        config.setMaximumPoolSize(userConfig.getConnectionPoolSize());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        // TODO: set maxLifetimeValue to lesser than 10 mins so that the following error doesnt happen:
        // io.supertokens.storage.postgresql.HikariLoggingAppender.doAppend(HikariLoggingAppender.java:117) |
        // SuperTokens
        // - Failed to validate connection org.mariadb.jdbc.MariaDbConnection@79af83ae (Connection.setNetworkTimeout
        // cannot be called on a closed connection). Possibly consider using a shorter maxLifetime value.
        config.setPoolName("SuperTokens");
        ds = new HikariDataSource(config);
    }

    private static ConnectionPool getInstance(Start start) {
        return (ConnectionPool) start.getResourceDistributor().getResource(RESOURCE_KEY);
    }

    static void initPool(Start start) {
        if (getInstance(start) != null) {
            return;
        }
        if (Thread.currentThread() != start.mainThread) {
            throw new QuitProgramFromPluginException("Should not come here");
        }
        Logging.info(start, "Setting up SQLite connection pool.");
        start.getResourceDistributor().setResource(RESOURCE_KEY, new ConnectionPool(start));
    }

    static Connection getConnection(Start start) throws SQLException {
        if (getInstance(start) == null) {
            throw new QuitProgramFromPluginException("Please call initPool before getConnection");
        }
        if (!start.enabled) {
            throw new SQLException("Storage layer disabled");
        }
        return getInstance(start).ds.getConnection();
    }

    static void close(Start start) {
        if (getInstance(start) == null) {
            return;
        }
        getInstance(start).ds.close();
    }
}
