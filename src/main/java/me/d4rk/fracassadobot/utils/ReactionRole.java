package me.d4rk.fracassadobot.utils;

import java.util.HashMap;

public class ReactionRole {

    private final String channelId;
    private final String messageId;
    private HashMap<String, String> entries;

    public ReactionRole(String messageId, String channelId) {
        this.channelId = channelId;
        this.messageId = messageId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getMessageId() {
        return messageId;
    }

    public HashMap<String, String> getEntries() {
        return entries;
    }

    public ReactionRole setEntries(HashMap<String, String> entries) {
        this.entries = entries;
        return this;
    }
}
