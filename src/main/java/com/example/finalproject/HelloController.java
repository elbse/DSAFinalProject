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
    private Label welcomeText; // Ensure this is linked to an element in your FXML

    @FXML
    protected void onHelloButtonClick() {
        System.out.println("Button clicked!");  // Debug output
        navigateToMenu();
    }

    @FXML
    protected void handleMouseClick(MouseEvent event) {
        System.out.println("Mouse clicked!");  // Debug output
        navigateToMenu();
    }

    private void navigateToMenu() {
        System.out.println("Navigating to menu..."); // Debug output
        try {
            Stage stage = (Stage) welcomeText.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml")); // Ensure this path is correct
            Parent menuPanel = fxmlLoader.load();
            Scene scene = new Scene(menuPanel);
            stage.setScene(scene);
            stage.setResizable(false); // Optional: Prevent resizing if needed
        } catch (IOException e) {
            System.err.println("Error loading menu.fxml: " + e.getMessage()); // More specific error message
            e.printStackTrace();
        }
    }
}
