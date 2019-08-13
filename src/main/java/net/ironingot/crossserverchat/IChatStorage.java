package net.ironingot.crossserverchat;

import java.util.Map;

interface IChatStorage {
    public void post(final Map<String, Object> data);
    public void receive(final IChatReceiveCallback callback);
}
