package com.rosspaffett.mattercraft;

import org.snakeyaml.engine.v2.api.*;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

public class MattercraftConfig {
    private static final String EXAMPLE_CONFIG = "---\n" +
        "api_key: ''\n" +
        "base_url: ''\n" +
        "gateway: ''\n";

    private final File file;

    private Map<String, String> config;

    protected MattercraftConfig(Path filePath) {
        this.file = filePath.toFile();
    }

    protected String getApiKey() {
        return config.get("api_key");
    }

    protected String getBaseUrl() {
        return config.get("base_url");
    }

    protected File getFile() {
        return file;
    }

    protected String getGateway() {
        return config.get("gateway");
    }

    protected boolean isValid() {
        return getApiKey() != null && !getApiKey().isBlank() &&
            getBaseUrl() != null && !getBaseUrl().isBlank() &&
            getGateway() != null && !getGateway().isBlank();
    }

    protected void load() throws ConfigException {
        if (file.exists()) {
            readConfigFile();
        } else {
            writeExampleConfigFile();
        }
    }

    private void readConfigFile() throws CouldNotReadConfigException {
        try (FileReader reader = new FileReader(file)) {
            LoadSettings yamlLoaderSettings = LoadSettings.builder().build();
            Load yamlLoader = new Load(yamlLoaderSettings);
            this.config = (Map<String, String>) yamlLoader.loadFromReader(reader);
        } catch (IOException exception) {
            throw new CouldNotReadConfigException(file);
        }
    }

    private void writeExampleConfigFile() throws CouldNotWriteExampleConfigException {
        try (FileWriter writer = new FileWriter(file)) {
            new File(file.getParent()).mkdirs();
            writer.write(EXAMPLE_CONFIG);
        } catch (IOException exception) {
            throw new CouldNotWriteExampleConfigException(file);
        }
    }

    protected class ConfigException extends Exception {
        private final File configFile;

        protected ConfigException(File configFile) {
            this.configFile = configFile;
        }

        File getConfigFile() {
            return configFile;
        }
    }

    protected class CouldNotReadConfigException extends ConfigException {
        protected CouldNotReadConfigException(File configFile) {
            super(configFile);
        }

        @Override
        public String getMessage() {
            return "Couldn't read config file from " + getConfigFile().getPath();
        }
    }

    protected class CouldNotWriteExampleConfigException extends ConfigException {
        protected CouldNotWriteExampleConfigException(File configFile) {
            super(configFile);
        }

        @Override
        public String getMessage() {
            return "Couldn't write example config file to " + getConfigFile().getPath();
        }
    }
}
