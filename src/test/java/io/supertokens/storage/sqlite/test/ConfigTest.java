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

package io.supertokens.storage.sqlite.test;

import io.supertokens.ProcessState;
import io.supertokens.storage.sqlite.Start;
import io.supertokens.storage.sqlite.config.Config;
import io.supertokens.storage.sqlite.config.SQLiteConfig;
import io.supertokens.storageLayer.StorageLayer;
import junit.framework.TestCase;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigTest {

    @Rule
    public TestRule watchman = Utils.getOnFailure();

    @AfterClass
    public static void afterTesting() {
        Utils.afterTesting();
    }

    @Before
    public void beforeEach() {
        Utils.reset();
    }


    @Test
    public void testThatDefaultConfigLoadsCorrectly() throws Exception {
        String[] args = {"../", "DEV"};

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));

        SQLiteConfig config = Config.getConfig((Start) StorageLayer.getStorageLayer(process.getProcess()));

        checkConfig(config);

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));

    }

    @Test
    public void testThatCustomConfigLoadsCorrectly() throws Exception {
        String[] args = {"../", "DEV"};

        Utils.setValueInConfig("sqlite_connection_pool_size", "5");
        Utils.setValueInConfig("sqlite_past_tokens_table_name", "\"temp_name\"");

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));

        SQLiteConfig config = Config.getConfig((Start) StorageLayer.getStorageLayer(process.getProcess()));
        assertEquals(config.getConnectionPoolSize(), 5);
        assertEquals(config.getPastTokensTable(), "temp_name");

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));
    }

    @Test
    public void testThatInvalidConfigThrowsRightError() throws Exception {
        String[] args = {"../", "DEV"};


        //sqlite_connection_pool_size is not set properly in the config file

        Utils.setValueInConfig("sqlite_connection_pool_size", "-1");
        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);

        ProcessState.EventAndException e = process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.INIT_FAILURE);
        assertNotNull(e);
        TestCase.assertEquals(e.exception.getMessage(),
                "'sqlite_connection_pool_size' in the config.yaml file must be > 0");

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));

    }

    @Test
    public void testSqliteDatabaseLocation() throws Exception {
        String[] args = {"../", "DEV"};

        Utils.commentConfigValue("sqlite_database_folder_location");

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        ProcessState.EventAndException e = process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.INIT_FAILURE);
        assertNotNull(e);
        TestCase.assertEquals(e.exception.getMessage(),
                "The database location set in 'sqlite_database_folder_location' does not exist, Please set a valid " +
                        "location and restart SuperTokens");

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));

        Utils.reset();

        //give invalid file path
        Utils.setValueInConfig("sqlite_database_folder_location", "randomFilePath123");

        process = TestingProcessManager.start(args);
        e = process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.INIT_FAILURE);
        assertNotNull(e);
        TestCase.assertEquals(e.exception.getMessage(),
                "The database location set in 'sqlite_database_folder_location' does not exist, Please set a valid " +
                        "location and restart SuperTokens");

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));

        Utils.reset();

        //set a file path with ~

        Utils.setValueInConfig("sqlite_database_folder_location", "~/");

        process = TestingProcessManager.start(args);
        e = process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.INIT_FAILURE);
        assertNotNull(e);
        TestCase.assertEquals(e.exception.getMessage(),
                "The database location set in 'sqlite_database_folder_location' cannot use '~', Please set a " +
                        "valid location and restart SuperTokens");

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));


    }

    @Test
    public void testThatMissingConfigFileThrowsError() throws Exception {
        String[] args = {"../", "DEV"};

        ProcessBuilder pb = new ProcessBuilder("rm", "-r", "config.yaml");
        pb.directory(new File(args[0]));
        Process process1 = pb.start();
        process1.waitFor();

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);

        ProcessState.EventAndException e = process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.INIT_FAILURE);
        assertNotNull(e);
        TestCase.assertEquals(e.exception.getMessage(),
                "java.io.FileNotFoundException: ../config.yaml (No such file or directory)");

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));


    }

    @Test
    public void testCustomLocationForConfigLoadsCorrectly() throws Exception {
        String[] args = {"../", "DEV", "configFile=../temp/config.yaml"};

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        ProcessState.EventAndException e = process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.INIT_FAILURE);
        assertNotNull(e);
        TestCase.assertEquals(e.exception.getMessage(), "configPath option must be an absolute path only");

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));

        //absolute path
        File f = new File("../temp/config.yaml");
        args = new String[]{"../", "DEV", "configFile=" + f.getAbsolutePath()};

        process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));

        SQLiteConfig config = Config.getConfig((Start) StorageLayer.getStorageLayer(process.getProcess()));
        checkConfig(config);

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));
    }


    @Test
    public void testThatChangeInTableNameIsCorrect() throws Exception {
        String[] args = {"../", "DEV"};

        Utils.setValueInConfig("sqlite_key_value_table_name", "key_value_table");
        Utils.setValueInConfig("sqlite_session_info_table_name", "session_info_table");
        Utils.setValueInConfig("sqlite_past_tokens_table_name", "past_tokens_table");

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));
        SQLiteConfig config = Config.getConfig((Start) StorageLayer.getStorageLayer(process.getProcess()));

        assertEquals("change in PastTokensTable name not reflected", config.getPastTokensTable(), "past_tokens_table");
        assertEquals("change in KeyValueTable name not reflected", config.getKeyValueTable(), "key_value_table");
        assertEquals("change in SessionInfoTable name not reflected", config.getSessionInfoTable(),
                "session_info_table");

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));
    }

    private static void checkConfig(SQLiteConfig config) {

        assertEquals("Config connectionPoolSize did not match default", config.getConnectionPoolSize(), 10);
        assertEquals("Config databaseName does not match default", config.getDatabaseName(), "auth_session");
        assertEquals("Config keyValue table does not match default", config.getKeyValueTable(), "key_value");
        assertEquals("Config pastTokensTable does not match default", config.getPastTokensTable(), "past_tokens");
        assertEquals("Config sessionInfoTable does not match default", config.getSessionInfoTable(), "session_info");
    }

}
