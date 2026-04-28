package io.davidsusanto.config;

import java.util.Properties;

public class AppConfig {

    private final Properties props;

    private AppConfig(Properties props) {
        this.props = props;
    }
    
    public String get(String key) {
        String value = props.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config: " + key);
        }
        return value.trim();
    }
}
