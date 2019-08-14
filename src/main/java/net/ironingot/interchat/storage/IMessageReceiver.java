package net.ironingot.interchat.storage;

import java.util.Map;

public interface IMessageReceiver {
    public void receive(Map<String, Object> message);
}
