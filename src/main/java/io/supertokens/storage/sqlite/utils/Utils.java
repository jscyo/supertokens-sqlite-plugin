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

package io.supertokens.storage.sqlite.utils;

import io.supertokens.storage.sqlite.config.SQLiteConfig;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;

public class Utils {
    public static String exceptionStacktraceToString(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        ps.close();
        return baos.toString();
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getDatabasePath(SQLiteConfig config) {
        return config.getDatabaseLocation() + config.getDatabaseName() + ".db";

    }

    public static String normaliseLocationPath(String dir) {
        if (dir == null) {
            return null;
        }
        if (getOS() == OS.WINDOWS) {
            if (!dir.endsWith("\\")) {
                return dir + "\\";
            }
        } else {
            if (!dir.endsWith("/")) {
                return dir + "/";
            }
        }
        return dir;
    }

    public enum OS {
        MAC, LINUX, WINDOWS
    }

    private static OS getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return OS.WINDOWS;
        } else if (os.contains("mac")) {
            return OS.MAC;
        } else {
            return OS.LINUX;
        }
    }
}
