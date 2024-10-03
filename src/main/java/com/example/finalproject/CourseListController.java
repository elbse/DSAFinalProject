package com.example.finalproject;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class CourseListController {

    @FXML
    private TableView<Course> courseTableView; // Your TableView for courses
    private CourseList courseList; // Your CourseList instance

    // Method to add a course
    @FXML
    private void addCourseButtonClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Course");
        dialog.setHeaderText("Enter Course Details");

        dialog.setContentText("Course Code:");
        Optional<String> courseCode = dialog.showAndWait();
        if (courseCode.isPresent()) {
            String code = courseCode.get();

            dialog.setContentText("Course Title:");
            Optional<String> courseTitle = dialog.showAndWait();
            dialog.setContentText("Course Description:");
            Optional<String> courseDescription = dialog.showAndWait();

            if (courseTitle.isPresent() && courseDescription.isPresent()) {
                Course newCourse = new Course(code, courseTitle.get(), courseDescription.get());
                courseList.add(newCourse); // Add the course to your course list

                // Show success message
                showAlert("Success", "Course added successfully!");
            }
        }
    }

    // Method to remove a course
    @FXML
    private void deleteCourseButtonClicked() {
        Course selectedCourse = courseTableView.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            courseList.remove(selectedCourse); // Remove from course list

            // Show success message
            showAlert("Success", "Course removed successfully!");
        } else {
            showAlert("Error", "Please select a course to delete.");
        }
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


