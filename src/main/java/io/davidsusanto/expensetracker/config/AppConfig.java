package io.davidsusanto.expensetracker.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

public class AppConfig {

    private final Properties props;

    private AppConfig(Properties props) {
        this.props = props;
    }

    public static AppConfig load(Path path) throws Exception {
        Objects.requireNonNull(path, "path");
        Properties p = new Properties();

        try (InputStream in = Files.newInputStream(path)) {
            p.load(in);
        }

        return new AppConfig(p);
    }
    
    public String get(String key) {
        String value = props.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config: " + key);
        }
        
        return value.trim();
    }

    public String getOrDefault(String key, String fallback) {
        String value = props.getProperty(key);
        
        return (value == null || value.isBlank()) ? fallback : value.trim();
    }
}
