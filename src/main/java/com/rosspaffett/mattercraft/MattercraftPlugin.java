package com.rosspaffett.mattercraft;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
    id = "mattercraft",
    name = "Mattercraft",
    version = "1.0.0+3.1.1-SNAPSHOT",
    url = "https://github.com/raws/minecraft-velocity-mattercraft",
    description = "Connect a Velocity Minecraft proxy server to a Matterbridge chat server",
    authors = {"Ross Paffett"}
)
public class MattercraftPlugin {
    private static final String CONFIG_FILE_PATH = "mattercraft.yml";

    private final MattercraftConfig config;
    private final Logger logger;
    private final ProxyServer velocity;

    private MatterbridgeApiClient matterbridge;

    @Inject
    public MattercraftPlugin(ProxyServer velocity, Logger logger, @DataDirectory Path dataDirectory) {
        Path configPath = Path.of(dataDirectory.toString(), CONFIG_FILE_PATH);
        this.config = new MattercraftConfig(configPath);
        this.logger = logger;
        this.velocity = velocity;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            config.load();
        } catch (MattercraftConfig.ConfigException exception) {
            logger.error(exception.getMessage(), exception);
            return;
        }

        if (!config.isValid()) {
            logger.warn("Please set api_key, base_url and gateway in " + getConfig().getFile().getPath());
            return;
        }

        this.matterbridge = new MatterbridgeApiClient(getConfig().getBaseUrl(), getConfig().getGateway(),
            getConfig().getApiKey());

        ChatEventListener chatEventListener = new ChatEventListener(this);
        velocity.getEventManager().register(this, chatEventListener);
    }

    protected MattercraftConfig getConfig() {
        return config;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected MatterbridgeApiClient getMatterbridge() {
        return matterbridge;
    }

    protected ProxyServer getVelocity() {
        return velocity;
    }
}
