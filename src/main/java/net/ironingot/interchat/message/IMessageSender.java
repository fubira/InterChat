package net.ironingot.interchat.message;

import java.util.Map;

public interface IMessageSender {
    public void post(final Map<String, Object> message);
}
