package dev.stevenposterick.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Create loader.
        FXMLLoader loader = new FXMLLoader();

        // Set the fxml file location.
        loader.setLocation(getClass().getResource("display.fxml"));

        // Load the main pane.
        GridPane borderPane = loader.load();

        // Create and set the scene
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Chat Application");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
