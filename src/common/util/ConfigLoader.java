package common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private Properties properties;

    public ConfigLoader() {
        properties = new Properties();
        try {
            InputStream input = new FileInputStream("config.properties");
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Error loading config file:\n" + e.getMessage());
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
    
    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}