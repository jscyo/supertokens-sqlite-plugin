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

package io.supertokens.storage.sqlite.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.supertokens.pluginInterface.PluginInterfaceTesting;
import io.supertokens.pluginInterface.exceptions.QuitProgramFromPluginException;
import io.supertokens.storage.sqlite.utils.Utils;

import java.io.File;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SQLiteConfig {

    @JsonProperty
    private int sqlite_config_version = -1;

    @JsonProperty
    private int sqlite_connection_pool_size = 10;

    @JsonProperty
    private String sqlite_database_folder_location = null;

    @JsonProperty
    private String sqlite_database_name = "auth_session";

    @JsonProperty
    private String sqlite_key_value_table_name = "key_value";

    @JsonProperty
    private String sqlite_session_info_table_name = "session_info";

    @JsonProperty
    private String sqlite_past_tokens_table_name = "past_tokens";

    public int getConnectionPoolSize() {
        return sqlite_connection_pool_size;
    }


    public String getDatabaseName() {
        return sqlite_database_name;
    }

    public String getKeyValueTable() {
        return sqlite_key_value_table_name;
    }

    public String getSessionInfoTable() {
        return sqlite_session_info_table_name;
    }

    public String getPastTokensTable() {
        return sqlite_past_tokens_table_name;
    }

    public String getDatabaseLocation() {
        if (PluginInterfaceTesting.isTesting) {
            return Utils.normaliseLocationPath("../" + sqlite_database_folder_location);
        }
        return Utils.normaliseLocationPath(sqlite_database_folder_location);
    }

    void validateAndInitialise() {

        if (getDatabaseLocation() == null) {
            throw new QuitProgramFromPluginException(
                    "'sqlite_database_folder_location' is not set in the config.yaml file. Please set this value and " +
                            "restart" +
                            " SuperTokens");
        }

        if (getDatabaseLocation().contains("~")) {
            throw new QuitProgramFromPluginException(
                    "The database location set in 'sqlite_database_folder_location' cannot begin with '~', Please set" +
                            " a " +
                            "valid location and restart SuperTokens");
        }

        if (!(new File(getDatabaseLocation()).exists())) {
            throw new QuitProgramFromPluginException(
                    "The database location set in 'sqlite_database_folder_location' does not exist, Please set a " +
                            "valid " +
                            "location and restart SuperTokens");
        }

        if (getConnectionPoolSize() <= 0) {
            throw new QuitProgramFromPluginException(
                    "'sqlite_connection_pool_size' in the config.yaml file must be > 0");
        }
    }

}