package com.recording.manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file.upload")
public class AppProperties {
    private String path = System.getProperty("user.home") + "/uploads/";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}