package net.ironingot.interchat.interfaces;

import java.util.Map;

public interface IChatReceiveCallback {
    public void message(Map<String, Object> message);
}
