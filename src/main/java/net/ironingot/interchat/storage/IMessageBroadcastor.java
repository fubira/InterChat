package net.ironingot.interchat.storage;

import java.util.Map;

public interface IMessageBroadcastor {
    public void broadcast(final Map<String, Object> message);
}
