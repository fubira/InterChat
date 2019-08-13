package net.ironingot.crossserverchat;

import java.util.Map;

public interface IChatReceiveCallback {
    public void message(Map<String, Object> message);
}
