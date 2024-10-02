package com.example.finalproject;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class MergeCourseController {

    @FXML
    private TableView<Course> courseTable;

    private CourseList courseList = new CourseList();

    public void checkMergability() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            int studentCount = selectedCourse.getStudentCount();

            String message = studentCount < 50 ? "Mergable" : "Not Mergable";
            // Display message to user
        }
    }

    public void mergeCourses() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Enter Course Code to Merge With");

            Optional<String> courseCode = dialog.showAndWait();
            if (courseCode.isPresent()) {
                Course mergeCourse = courseList.find(courseCode.get());
                if (mergeCourse != null) {
                    int totalStudents = selectedCourse.getStudentCount() + mergeCourse.getStudentCount();
                    if (totalStudents <= 50) {
                        // Merge logic
                    } else {
                        // Show "Not Mergable" message
                    }
                }
            }
        }
    }
}
