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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.supertokens.pluginInterface.exceptions.QuitProgramFromPluginException;
import io.supertokens.storage.sqlite.ResourceDistributor;
import io.supertokens.storage.sqlite.Start;
import io.supertokens.storage.sqlite.output.Logging;

import java.io.File;
import java.io.IOException;


public class Config extends ResourceDistributor.SingletonResource {

    private static final String RESOURCE_KEY = "io.supertokens.storage.sqlite.config.Config";
    private final SQLiteConfig config;
    private final Start start;

    private Config(Start start, String configFilePath) {
        this.start = start;
        try {
            config = loadSQLiteConfig(configFilePath);
        } catch (IOException e) {
            throw new QuitProgramFromPluginException(e);
        }
    }

    private static Config getInstance(Start start) {
        return (Config) start.getResourceDistributor().getResource(RESOURCE_KEY);
    }

    public static void loadConfig(Start start, String configFilePath) {
        if (getInstance(start) != null) {
            return;
        }
        Logging.info(start, "Loading SQLite config.");
        start.getResourceDistributor().setResource(RESOURCE_KEY, new Config(start, configFilePath));
    }

    public static SQLiteConfig getConfig(Start start) {
        if (getInstance(start) == null) {
            throw new QuitProgramFromPluginException("Please call loadConfig() before calling getConfig()");
        }
        return getInstance(start).config;
    }

    private SQLiteConfig loadSQLiteConfig(String configFilePath) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        SQLiteConfig config = mapper.readValue(new File(configFilePath), SQLiteConfig.class);
        config.validateAndInitialise();
        return config;
    }

    public static boolean canBeUsed(Start start, String configFilePath) {
        try {
            new Config(start, configFilePath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
