package net.ironingot.interchat.storage;

public interface IMessageStoreReceiver {
    public void receive(final IMessageBroadcastor broadcastor);
}
