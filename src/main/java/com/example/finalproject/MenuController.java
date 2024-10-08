package com.example.finalproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Optional;

public class MenuController {

    @FXML
    private AnchorPane Homepage;

    private CourseList courseList; // Reference to the course list for checking course codes and titles

    public MenuController() {
        courseList = new CourseList(); // Initialize CourseList (make sure CourseList is accessible here)
    }

    @FXML
    private void goToCourseList() {
        try {
            Stage stage = (Stage) Homepage.getScene().getWindow();
            AnchorPane courseList = FXMLLoader.load(getClass().getResource("CourseList.fxml"));
            Scene scene = new Scene(courseList, 1920, 1080);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToStudentList() {
        // Ask for the course code
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Course Code");
        dialog.setHeaderText("Enter Course Code");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(code -> {
            // Check if the course code exists
            if (courseList.courseExists(code)) {
                try {
                    Stage stage = (Stage) Homepage.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentList.fxml"));
                    AnchorPane studentList = loader.load();
                    StudentListController controller = loader.getController();
                    controller.setCourseCode(code); // Set the course code for the student list
                    Scene scene = new Scene(studentList, 1920, 1080);
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Show error if the course code does not exist
                showErrorAlert("Invalid Course Code", "Please enter an existing course code.");
            }
        });// R
    }

    @FXML
    private void goToMergeCourse() {
        // Ask for the course title
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Course Title");
        dialog.setHeaderText("Enter Course Title");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            // Check if the course title exists
            if (courseList.courseTitleExists(title)) {
                try {
                    Stage stage = (Stage) Homepage.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MergeCourse.fxml"));
                    AnchorPane mergeCourse = loader.load();
                    MergeCourse controller = loader.getController();
                    controller.setCourseTitle(title); // Set the course title for merging
                    Scene scene = new Scene(mergeCourse, 1920, 1080);
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Show error if the course title does not exist
                showErrorAlert("Invalid Course Title", "Please enter an existing course title.");
            }
        });
    }

    // Method to show an error alert
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
    private void exitApplication() {
        Stage stage = (Stage) Homepage.getScene().getWindow();
        stage.close();
    }
}
