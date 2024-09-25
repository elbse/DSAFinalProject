package com.example.finalproject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;



public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Anga University");
            stage.setScene(scene);
            stage.show();

            Timeline timeline;
            timeline = new Timeline(new KeyFrame(Duration.seconds(4), e -> switchToMenuPanel(stage)));
            timeline.setCycleCount(1);
            timeline.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchToMenuPanel(Stage stage){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("menu.fxml"));
            Object menupanel = fxmlLoader.load();
            Scene scene = new Scene((Parent) menupanel);
            stage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }
    }




    public static void main(String[] args) {
        launch();
    }
}
