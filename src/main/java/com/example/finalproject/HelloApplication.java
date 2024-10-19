package com.example.finalproject;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // Load the hello-view.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene helloScene = new Scene(fxmlLoader.load());
            stage.setTitle("Anga University");
            stage.setScene(helloScene);
            stage.show();

            // Pause for 2 seconds before switching to the menu scene
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> {
                // Switch to the menu.fxml after 2 seconds
                try {
                    FXMLLoader menuLoader = new FXMLLoader(HelloApplication.class.getResource("menu.fxml"));
                    Scene menuScene = new Scene(menuLoader.load());
                    stage.setScene(menuScene);
                    stage.setResizable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            delay.play(); // Start the delay timer
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
