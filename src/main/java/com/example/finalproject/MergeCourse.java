package com.example.finalproject;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.LinkedList;

public class MergeCourse {
    @FXML
    private Text courseTitleText; // Reference to the Text component for course title
    @FXML
    private TableView<Course> courseTable; // Table to display courses
    @FXML
    private TableColumn<Course, String> courseCodeColumn; // Column for Course Code
    @FXML
    private TableColumn<Course, Integer> numStudentsColumn; // Column for Number of Students
    @FXML
    private TableColumn<Course, String> courseStatusColumn; // Column for Course Status

    private CourseList courseList; // Instance of CourseList
    private String courseTitle; // Store the input course title

    public MergeCourse() {
        courseList = new CourseList(); // Initialize the CourseList instance
    }

    @FXML
    public void initialize() {
        // Initialize columns
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        numStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("numStudents"));
        courseStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status")); // Connect to the status method

        // Load courses into the table
        loadCourses();

        // Set the custom cell factory for the status column
        courseStatusColumn.setCellFactory(column -> new TableCell<Course, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    setStyle("Mergable".equals(status) ? "-fx-text-fill: green;" : "-fx-text-fill: red;"); // Set colors for status
                }
            }
        });

        // Handle double-click on course code
        courseTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                onCourseCodeDoubleClicked();
            }
        });

        // Handle selection change to update course title and description
        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateCourseTitleText(newSelection);
            }
        });
    }

    // Method to load courses based on the title
    public void loadCourses() {
        // Filter courses with the given title
        LinkedList<Course> filteredCourses = new LinkedList<>();
        for (Course course : courseList.getCourses()) {
            if (course.getTitle().equals(courseTitle)) {
                filteredCourses.add(course);
            }
        }
        courseTable.setItems(FXCollections.observableArrayList(filteredCourses));
    }

    // Method to set the course title and description
    public void setCourseTitle(String title) {
        this.courseTitle = title; // Store the title
        loadCourses(); // Load the courses for this title

        // Find the course that matches the title to get the description
        Course matchingCourse = null;
        for (Course course : courseList.getCourses()) {
            if (course.getTitle().equals(title)) {
                matchingCourse = course;
                break;
            }
        }

        // Update displayed course title and description if found
        if (matchingCourse != null) {
            courseTitleText.setText("Courses for: " + title + " - " + matchingCourse.getDescription());
        } else {
            courseTitleText.setText("Courses for: " + title + " - Description not found");
        }
    }

    // Method called on double-clicking a course code
    private void onCourseCodeDoubleClicked() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            LinkedList<Course> mergeableCourses = getMergeableCourses(selectedCourse);
            showMergeableCoursesDialog(mergeableCourses, selectedCourse);
        }
    }

    // New method to update the Text with course title and description
    private void updateCourseTitleText(Course course) {
        String titleText = course.getCode() + " - " + course.getTitle() + " (" + course.getNumStudents() + " Students)";
        courseTitleText.setText(titleText); // Update the Text component
    }

    // Function to check mergeable courses
    private LinkedList<Course> getMergeableCourses(Course course) {
        LinkedList<Course> mergeableCourses = new LinkedList<>();
        for (Course c : courseList.getCourses()) {
            if (!c.getCode().equals(course.getCode()) && c.getNumStudents() < 50 && c.getTitle().equals(course.getTitle())) {
                mergeableCourses.add(c);
            }
        }
        return mergeableCourses;
    }

    // Method to show mergeable courses in a dialog
    private void showMergeableCoursesDialog(LinkedList<Course> mergeableCourses, Course selectedCourse) {
        if (mergeableCourses.isEmpty()) {
            showAlert("No Mergeable Courses", "There are no courses available for merging with " + selectedCourse.getCode());
            return;
        }

        ListView<String> listView = new ListView<>();
        for (Course course : mergeableCourses) {
            listView.getItems().add(course.getTitle()); // Add only the course title to the list
        }

        Dialog<String> dialog = new Dialog<>();
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
                return listView.getSelectionModel().getSelectedItem(); // Return selected course title
            }
            return null;
        });

        // Show dialog and handle the result
        dialog.showAndWait().ifPresent(courseTitle -> {
            // Find the selected course based on the title
            Course courseToMerge = mergeableCourses.stream()
                    .filter(c -> c.getTitle().equals(courseTitle))
                    .findFirst()
                    .orElse(null);
            mergeCourses(selectedCourse, courseToMerge);
        });
    }

    // Method to merge selected courses
    private void mergeCourses(Course course1, Course course2) {
        if (course2 == null) return; // No course selected

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
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to update the course list after merging
    private void updateCourseList() {
        loadCourses(); // Reload the courses after merging
    }
}
