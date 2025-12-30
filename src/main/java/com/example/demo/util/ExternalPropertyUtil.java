package com.example.demo.util;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class ExternalPropertyUtil {

    public static String get(String key) throws IOException {
        Properties props = new Properties();

        // Read from classpath inside JAR or IDE
        try (InputStream inputStream = ExternalPropertyUtil.class
                .getClassLoader()
                .getResourceAsStream("config/SiqConfig.properties")) {

            if (inputStream == null) {
                throw new IOException(
                    "config/SiqConfig.properties not found in classpath. " +
                    "Make sure it's inside src/main/resources/config/"
                );
            }

            props.load(inputStream);
        }

        return props.getProperty(key);
    }
}
