package com.rosspaffett.mattercraft;

import net.kyori.adventure.text.Component;

public class ChatMessage {
    private final String text;
    private final String username;

    ChatMessage(String username, String text) {
        this.text = text;
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public String getUsername() {
        return username;
    }

    public boolean isValid() {
        return getText() != null && !getText().isBlank() &&
            getUsername() != null && !getUsername().isBlank();
    }

    public Component toComponent() {
        return Component.text(toString());
    }

    public String toString() {
        return "<" + getUsername() + "> " + getText();
    }
}
