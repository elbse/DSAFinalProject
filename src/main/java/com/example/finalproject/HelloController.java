package com.example.finalproject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        System.out.println("Button clicked!");
        navigateToMenu();
    }

    @FXML
    protected void handleMouseClick(MouseEvent event) {
        System.out.println("Mouse clicked!");
        navigateToMenu();
    }

    private void navigateToMenu() {
        System.out.println("Navigating to menu...");
        try {
            Stage stage = (Stage) welcomeText.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Parent menuPanel = fxmlLoader.load();
            Scene scene = new Scene(menuPanel);
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            System.err.println("Error loading menu.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }// R
}
