package com.rosspaffett.mattercraft;

import com.google.gson.Gson;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

public class ChatMessageReceiver implements Runnable {
    private static final Gson GSON = new Gson();

    private final MattercraftPlugin plugin;
    private volatile boolean shouldStop = false;

    protected ChatMessageReceiver(MattercraftPlugin plugin) {
        this.plugin = plugin;
    }

    private void broadcast(ChatMessage message) {
        getVelocity().sendMessage(message.toComponent());
    }

    private Logger getLogger() {
        return getPlugin().getLogger();
    }

    private MatterbridgeApiClient getMatterbridge() {
        return getPlugin().getMatterbridge();
    }

    private MattercraftPlugin getPlugin() {
        return plugin;
    }

    private ProxyServer getVelocity() {
        return getPlugin().getVelocity();
    }

    private void parseChatMessage(String json) {
        ChatMessage message = GSON.fromJson(json, ChatMessage.class);

        if (message.isValid()) {
            broadcast(message);
        } else {
            getLogger().debug("Discarding message: {}", json);
        }
    }

    public void run() {
        try {
            HttpURLConnection connection = getMatterbridge().streamMessagesConnection();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;

                while (shouldContinueRunning()) {
                    try {
                        line = reader.readLine();
                        if (line == null) break;
                        parseChatMessage(line);
                    } catch (SocketTimeoutException e) {
                        // Try reading again
                    }
                }
            }
        } catch (IOException e) {
            getLogger().error("Couldn't connect to Matterbridge: {}", e.getMessage());
        }
    }

    private boolean shouldContinueRunning() {
        return !this.shouldStop;
    }

    public void stop() {
        this.shouldStop = true;
    }
}
