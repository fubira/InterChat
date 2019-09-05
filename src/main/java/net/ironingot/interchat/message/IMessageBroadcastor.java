package net.ironingot.interchat.message;

import java.util.Map;

public interface IMessageBroadcastor {
    public void broadcast(final Map<String, Object> message);
}
