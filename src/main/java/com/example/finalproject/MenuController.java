package com.example.finalproject;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.util.Optional;

public class MenuController {
    @FXML
    private AnchorPane Homepage;

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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Course Code");
        dialog.setHeaderText("Enter Course Code");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(code -> {
            try {
                Stage stage = (Stage) Homepage.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentList.fxml"));
                AnchorPane studentList = loader.load();
                StudentListController controller = loader.getController();
                controller.setCourseCode(code); // Set course code for student list
                Scene scene = new Scene(studentList, 1920, 1080);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void goToMergeCourse() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Course Title");
        dialog.setHeaderText("Enter Course Title");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            try {
                Stage stage = (Stage) Homepage.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MergeCourse.fxml"));
                AnchorPane mergeCourse = loader.load();
                MergeCourse controller = loader.getController();
                controller.setCourseTitle(title); // Set course title for merging
                Scene scene = new Scene(mergeCourse, 1920, 1080);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @FXML
    private void exitApplication() {
        Stage stage = (Stage) Homepage.getScene().getWindow();
        stage.close();
    }



};