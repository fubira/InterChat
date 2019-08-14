package net.ironingot.interchat.storage;

import java.util.Map;

public interface IMessageStoreSender {
    public void post(final Map<String, Object> message);
}
