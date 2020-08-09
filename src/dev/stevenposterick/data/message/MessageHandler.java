package dev.stevenposterick.data.message;

import dev.stevenposterick.utils.listeners.ServerListener;

public interface MessageHandler {

    void sendMessage(ServerListener listener, String line);

}
