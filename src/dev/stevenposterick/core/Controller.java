package dev.stevenposterick.core;

import dev.stevenposterick.data.account.ChatUser;
import dev.stevenposterick.data.message.Message;
import dev.stevenposterick.data.message.MessageType;
import dev.stevenposterick.utils.client.SocketClient;
import dev.stevenposterick.utils.listeners.ServerListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Controller implements Initializable, ServerListener {

    @FXML
    public TextField addressTextBox;

    @FXML
    public TextField userTextBox;

    @FXML
    public TextField portTextBox;

    @FXML
    public ListView<ChatUser> userListView;

    @FXML
    public ListView<Message> chatListView;

    @FXML
    public TextField messageTextBox;

    private ClientState clientState = ClientState.NOT_CONNECTED;

    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    private SocketClient socketClient;

    private enum ClientState {
        NOT_CONNECTED,
        CONNECTED
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set a custom chat display format.
        chatListView.setCellFactory(parameter -> new ListCell<Message>(){
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null){
                    setText(null);
                } else {
                    setText(
                            item.getFrom() + " " + item.getTime() +
                            System.lineSeparator() + item.getMessage());
                }
            }
        });

        // Set a custom user display format.
        userListView.setCellFactory(param -> new ListCell<ChatUser>(){
            @Override
            protected void updateItem(ChatUser item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null){
                    setText(null);
                } else {
                    setText("[" + item.getName() + "]");
                }
            }
        });
    }

    // Method is called when disconnect button is clicked.
    public void onDisconnect(ActionEvent actionEvent) {
        actionEvent.consume();

        if (socketClient != null){
            socketClient.setRunning(false);
        }

        setClientState(ClientState.NOT_CONNECTED);
    }

    // Method is called when connect button is clicked.
    public void onConnect(ActionEvent actionEvent) {
        actionEvent.consume();

        Platform.runLater(()-> {
            if (getClientState() != ClientState.NOT_CONNECTED){
                showAlert(Alert.AlertType.ERROR,
                        "Already connected",
                        "You must disconnect from the server before trying to connect to another.");
            } else {
                // Parse text fields.
                String host = addressTextBox.getText();
                String port = portTextBox.getText();
                String user = userTextBox.getText();

                // Don't try to connect if a field is empty.
                if (!validateAllTextFields(host, port, user)){
                    showAlert(Alert.AlertType.ERROR, "Empty text field",
                            "One or more of the text fields is empty.");
                    return;
                }

                // Start up socket connection.
                socketClient = new SocketClient(this, host, Integer.parseInt(port), user);
                socketClient.start();

                sendClientMessage("Attempting to connect to server.");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        
        // Set title
        alert.setTitle(title);
        
        // Display without header text.
        alert.setHeaderText(null);
        
        // Set message information. 
        alert.setContentText(message);
        
        // Finally display. 
        alert.showAndWait();
    }

    public void onSendMessage(ActionEvent actionEvent) {
        actionEvent.consume();

        Platform.runLater(()-> {
            if (getClientState() == ClientState.CONNECTED){
                String message = messageTextBox.getText();
                if (validateAllTextFields(message)){
                    // Clear the textbox.
                    messageTextBox.clear();

                    // Create the message.
                    Message sendMessage = new Message(socketClient.getChatUser().getName(), message);

                    // Send message to server.
                    socketClient.sendMessageToServer(
                            MessageType.MESSAGE.getMessageStart() + sendMessage.toString()
                    );

                    // Update our messages.
                    chatListView.getItems().add(sendMessage);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Empty message",
                            "The message you tried to send was empty.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Not connected",
                        "Connect to server before attempting to send message.");
            }
        });
    }

    private void sendClientMessage(String message){
        chatListView.getItems().add(
                new Message("Client", message));
    }

    private synchronized ClientState getClientState(){
        return clientState;
    }

    private synchronized void setClientState(ClientState state) {
        this.clientState = state;
    }

    private boolean validateAllTextFields(String... textFields){
        // Check if the value is empty.
        return Arrays.stream(textFields)
                .noneMatch(s-> s != null && s.equals(""));
    }

    // Server listener implementations.
    @Override
    public void onPlayerJoined(ChatUser user) {
        if (user == null)
            return;

        // Add the character.
        userListView.getItems().add(user);
    }

    @Override
    public void onPlayerLeft(ChatUser user) {
        if (user == null)
            return;

        // Remove the character.
        userListView.getItems().stream()
                .filter(u-> u.getName().equals(user.getName()))
                .findFirst()
                .ifPresent(chatUser -> userListView.getItems().remove(chatUser));

    }

    @Override
    public void onChatMessage(Message message) {
        if (message == null)
            return;

        chatListView.getItems().add(message);
    }

    @Override
    public void onDisconnected() {
        Platform.runLater(()-> {
            setClientState(ClientState.NOT_CONNECTED);
            sendClientMessage("Disconnected from server.");
        });
    }

    @Override
    public void onFailedConnection() {
        Platform.runLater(()-> {
            setClientState(ClientState.NOT_CONNECTED);
            sendClientMessage("Failed to connect to server.");

            showAlert(Alert.AlertType.ERROR, "Failed to connect",
                    "Failed to connect to the server with the address and port provided.");
        });
    }

    @Override
    public void onSuccessfulConnection() {
        sendClientMessage("Successfully connected to server.");
        setClientState(ClientState.CONNECTED);
    }
}
