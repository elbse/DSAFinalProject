package com.example.finalproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void goToCourseList() throws IOException {
        System.out.println("Course List button clicked"); // Debug message
        loadScene("course-list.fxml");
    }

    public void goToStudentList() throws IOException {
        System.out.println("Navigating to Student List"); // Debug message
        loadScene("student-list.fxml");
    }

    public void goToMergeCourse() throws IOException {
        System.out.println("Merging courses..."); // Debug message
        loadScene("merge-course.fxml");
    }

    public void exitApp() {
        System.out.println("Exiting application..."); // Debug message
        stage.close();
    }

    private void loadScene(String fxmlFile) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



};