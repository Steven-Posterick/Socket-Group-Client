package dev.stevenposterick.utils.listeners;

import dev.stevenposterick.data.account.ChatUser;
import dev.stevenposterick.data.message.Message;

public interface ServerListener {

    void onPlayerJoined(ChatUser user);

    void onPlayerLeft(ChatUser user);

    void onChatMessage(Message message);

    void onDisconnected();

    void onFailedConnection();

    void onSuccessfulConnection(String name);

}
