package com.rosspaffett.mattercraft;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatMessageSender implements Runnable {
    private final ConcurrentLinkedQueue<ChatMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private final MattercraftPlugin plugin;

    private volatile boolean shouldStop = false;

    protected ChatMessageSender(MattercraftPlugin plugin) {
        this.plugin = plugin;
    }

    private Logger getLogger() {
        return plugin.getLogger();
    }

    private MatterbridgeApiClient getMatterbridge() {
        return plugin.getMatterbridge();
    }

    public void enqueue(ChatMessage message) {
        this.messageQueue.add(message);
    }

    public void run() {
        ChatMessage message;

        while (shouldContinueRunning()) {
            message = this.messageQueue.poll();

            if (message != null) {
                sendMessageToMatterbridge(message);
            }
        }
    }

    private void sendMessageToMatterbridge(ChatMessage message) {
        try {
            getMatterbridge().sendChatMessage(message);
        } catch (IOException exception) {
            getLogger().error("Error connecting to Matterbridge API: " + exception.getMessage());
        } catch (MatterbridgeApiClient.MatterbridgeApiErrorException exception) {
            getLogger().error(exception.getMessage());
        }
    }

    private boolean shouldContinueRunning() {
        return !this.shouldStop;
    }

    public void stop() {
        this.shouldStop = true;
    }
}
