package dev.stevenposterick.data.message;

import dev.stevenposterick.data.account.ChatUser;

public enum MessageType {
    DISCONNECTED("$(DISCONNECTED)", (listener, line) -> {
        ChatUser chatUser = ChatUser.createFromString(line);

        if (chatUser != null)
            listener.onPlayerLeft(chatUser);
        else
            System.out.println("Failed to parse chat user");

    }),
    CONNECTED("$(CONNECTED)", ((listener, line) -> {
        ChatUser chatUser = ChatUser.createFromString(line);

        if (chatUser != null)
            listener.onPlayerJoined(chatUser);
         else
            System.out.println("Failed to parse chat user");

    })),
    MESSAGE("$(MESSAGE)", ((listener, line) -> {
        Message message = Message.createFromString(line);

        if (message != null)
            listener.onChatMessage(message);
        else
            System.out.println("Failed to parse message.");
    }));

    private final String message;
    private final MessageHandler handler;

    MessageType(String message, MessageHandler handler) {
        this.message = message;
        this.handler = handler;
    }

    public String getMessageStart() {
        return message;
    }

    public MessageHandler getHandler() {
        return handler;
    }
}
