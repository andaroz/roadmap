package com.roadmap.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesLoader {
    private PropertiesLoader(){
    }
    public static Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = PropertiesLoader.class
                .getClassLoader()
                .getResourceAsStream(fileName);
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }
}
