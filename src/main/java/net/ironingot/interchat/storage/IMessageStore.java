package net.ironingot.interchat.storage;

import java.util.Map;

public interface IMessageStore {
    public void post(final Map<String, Object> message);
    public void receive(final IMessageReceiver receiver);
}
