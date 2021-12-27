package com.rosspaffett.mattercraft;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.proxy.ListenerBoundEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import org.slf4j.Logger;

public class ChatEventListener {
    private static final String INCOMING_MESSAGE_THREAD_NAME = "Mattercraft/IncomingMessageThread";
    private static final String OUTGOING_MESSAGE_THREAD_NAME = "Mattercraft/OutgoingMessageThread";

    private final MattercraftPlugin plugin;

    private ChatMessageReceiver incomingMessageReceiver;
    private ChatMessageSender outgoingMessageSender;

    public ChatEventListener(MattercraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onListenerBound(ListenerBoundEvent event) {
        startReceivingMessages();
        startSendingMessages();

        getLogger().info("Mattercraft is relaying chat to Matterbridge gateway \"{}\" at {}",
            getConfig().getGateway(), getConfig().getBaseUrl());
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        sendOutgoingChatMessage(event.getPlayer().getUsername(), event.getMessage());
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        stopReceivingMessages();
        stopSendingMessages();
    }

    private MattercraftConfig getConfig() {
        return getPlugin().getConfig();
    }

    private Logger getLogger() {
        return getPlugin().getLogger();
    }

    private MattercraftPlugin getPlugin() {
        return plugin;
    }

    private void sendOutgoingChatMessage(String username, String body) {
        ChatMessage message = new ChatMessage(username, body);
        this.outgoingMessageSender.enqueue(message);
    }

    private void startIncomingMessageThread() {
        Thread incomingMessageThread = new Thread(this.incomingMessageReceiver, INCOMING_MESSAGE_THREAD_NAME);
        incomingMessageThread.start();
    }

    private void startOutgoingMessageThread() {
        Thread outgoingMessageThread = new Thread(this.outgoingMessageSender, OUTGOING_MESSAGE_THREAD_NAME);
        outgoingMessageThread.start();
    }

    private void startReceivingMessages() {
        this.incomingMessageReceiver = new ChatMessageReceiver(getPlugin());
        startIncomingMessageThread();
    }

    private void startSendingMessages() {
        this.outgoingMessageSender = new ChatMessageSender(getPlugin());
        startOutgoingMessageThread();
    }

    private void stopReceivingMessages() {
        incomingMessageReceiver.stop();
    }

    private void stopSendingMessages() {
        outgoingMessageSender.stop();
    }
}
