package net.ironingot.interchat.interfaces;

import java.util.Map;

public interface IChatStorage {
    public void post(final Map<String, Object> data);
    public void receive(final IChatReceiveCallback callback);
}
