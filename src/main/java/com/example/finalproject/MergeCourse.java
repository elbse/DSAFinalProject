package com.example.finalproject;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MergeCourse {
    @FXML
    private TableView<Course> courseTable; // Table to display courses
    @FXML
    private TableColumn<Course, String> courseCodeColumn; // Column for Course Code
    @FXML
    private TableColumn<Course, String> courseTitleColumn; // Column for Course Title
    @FXML
    private TableColumn<Course, Integer> numStudentsColumn; // Column for Number of Students

    private CourseList courseList; // Instance of CourseList

    public MergeCourse() {
        courseList = new CourseList(); // Initialize the CourseList instance
    }

    @FXML
    public void initialize() {
        // Initialize columns
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        numStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("numStudents"));

        // Load courses into the table
        courseTable.setItems(FXCollections.observableArrayList(courseList.getCourses()));

        // Handle double-click on course code
        courseTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                onCourseCodeDoubleClicked();
            }
        });
    }

    // Method called on double-clicking a course code
    private void onCourseCodeDoubleClicked() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            List<Course> mergeableCourses = getMergeableCourses(selectedCourse);
            showMergeableCoursesDialog(mergeableCourses, selectedCourse);
        }
    }

    // Function to check mergeable courses
    private List<Course> getMergeableCourses(Course course) {
        List<Course> mergeableCourses = new ArrayList<>();
        for (Course c : courseList.getCourses()) {
            if (!c.getCode().equals(course.getCode()) && c.getNumStudents() < 50) {
                mergeableCourses.add(c);
            }
        }
        return mergeableCourses;
    }

    // Method to show mergeable courses in a dialog
    private void showMergeableCoursesDialog(List<Course> mergeableCourses, Course selectedCourse) {
        if (mergeableCourses.isEmpty()) {
            showAlert("No Mergeable Courses", "There are no courses available for merging with " + selectedCourse.getCode());
            return;
        }

        ListView<Course> listView = new ListView<>();
        listView.getItems().addAll(mergeableCourses);

        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Select a Course to Merge");
        dialog.setHeaderText("Select a mergeable course with " + selectedCourse.getCode());

        ButtonType mergeButtonType = new ButtonType("Merge", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(mergeButtonType, ButtonType.CANCEL);

        VBox dialogPane = new VBox(10);
        dialogPane.getChildren().add(listView);
        dialog.getDialogPane().setContent(dialogPane);

        // Handle merge action
        dialog.setResultConverter(button -> {
            if (button == mergeButtonType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        // Show dialog and handle the result
        dialog.showAndWait().ifPresent(courseToMerge -> mergeCourses(selectedCourse, courseToMerge));
    }

    // Method to merge selected courses
    private void mergeCourses(Course course1, Course course2) {
        int totalStudents = course1.getNumStudents() + course2.getNumStudents();

        // Check if total is 50 or less for merging
        if (totalStudents <= 50) {
            // Update the first course to have the new number of students
            course1.setNumStudents(totalStudents);

            // Optionally, handle the data from course2 (e.g., remove it from the list)
            courseList.deleteCourse(course2.getCode());

            // Save changes to file or update the UI accordingly
            updateCourseList();
        } else {
            showAlert("Merge Not Possible", "The total number of students exceeds 50. Merging not allowed.");
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

    // Method to update the course list display (if needed)
    private void updateCourseList() {
        courseTable.setItems(FXCollections.observableArrayList(courseList.getCourses()));
    }

    // Method to set the course title
    public void setCourseTitle(String title) {
        // You can implement logic here to use the title if necessary
        System.out.println("Course Title: " + title); // For debugging
    }
}
