package dev.stevenposterick.utils.client;

import dev.stevenposterick.data.account.ChatUser;
import dev.stevenposterick.data.message.MessageType;
import dev.stevenposterick.utils.listeners.ServerListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketClient extends Thread {

    private final String ip;
    private final int port;
    private final ServerListener listener;
    private final ChatUser chatUser;
    private Socket socket;
    private volatile boolean running = true;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public SocketClient(ServerListener listener, String ip, int port, String user) {
        this.ip = ip;
        this.port = port;
        this.listener = listener;
        this.chatUser = new ChatUser(user);
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ip, port);
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            setRunning(false);
            e.printStackTrace();
            listener.onFailedConnection();
            return;
        }
        listener.onSuccessfulConnection(chatUser.getName());

        // Tell server that you have connected.
        sendMessageToServer(MessageType.CONNECTED.getMessageStart() + chatUser.toString());

        try {
            while (isRunning()) {
                while (dataIn.available() == 0) {
                    if (!isRunning()){
                        break;
                    }
                    Thread.sleep(50);
                }
                String line = dataIn.readUTF();

                System.out.println(line);

                for (MessageType messageType : MessageType.values()){
                    if (line.startsWith(messageType.getMessageStart())) {
                        String messageLine = line.replace(messageType.getMessageStart(), "");
                        messageType.getHandler().sendMessage(listener, messageLine);
                        break;
                    }
                }

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Tell server you have disconnected.
        sendMessageToServer(MessageType.DISCONNECTED.getMessageStart() + chatUser.toString());

        // When finished send disconnected alert.
        listener.onDisconnected();
    }

    private synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running){
        this.running = running;
    }

    public synchronized void sendMessageToServer(String message){
        if (dataOut == null)
            return;

        try {
            dataOut.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatUser getChatUser(){
        return chatUser;
    }
}
