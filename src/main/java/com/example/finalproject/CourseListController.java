package com.example.finalproject;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;

public class CourseListController {

    @FXML
    private TextField courseCodeField; // TextField for Course Code input
    @FXML
    private TextField courseTitleField; // TextField for Course Title input
    @FXML
    private TextField courseDescriptionField; // TextField for Course Description input

    @FXML
    private Button addCourseButton; // Button to add a course
    @FXML
    private Button deleteCourseButton; // Button to delete a course
    @FXML
    private TableView<Course> courseTable; // Table to display courses

    @FXML
    private TableColumn<Course, String> courseCodeColumn; // Column for Course Code
    @FXML
    private TableColumn<Course, String> courseTitleColumn; // Column for Course Title
    @FXML
    private TableColumn<Course, String> courseDescriptionColumn; // Column for Course Description
    @FXML
    private TableColumn<Course, Integer> numStudentsColumn; // Column for Number of Students

    private CourseList courseList; // Instance of CourseList
    private LinkedList<Course> linkedCourseList; // LinkedList for TableView


    public CourseListController() {
        courseList = new CourseList(); // Initialize the CourseList instance
        linkedCourseList = new LinkedList<>(courseList.getCourses()); // Initialize linked list
    }

    @FXML
    public void initialize() {
        // Initialize columns
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        courseDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        numStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("numStudents"));

        loadCoursesToTable();

        // Add a double-click event handler for the course table
        courseTable.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
                if (selectedCourse != null) {
                    openStudentList(selectedCourse.getCode());
                }
            }
        });
    }// R

    // Method to add a course
    @FXML
    private void addCourse() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Course");
        dialog.setHeaderText("Enter Course Details (Code, Title, Description)");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(details -> {
            String[] parts = details.split(",");
            if (parts.length == 3) {
                String code = parts[0].trim();
                String title = parts[1].trim();
                String description = parts[2].trim();

                // Validate unique course code
                for (Course course : linkedCourseList) {
                    if (course.getCode().equalsIgnoreCase(code)) {
                        System.out.println("Course code already exists.");
                        return;
                    }
                }

                // Get student count from StudentList
                int numStudents = new StudentList(code).getStudents().size();
                courseList.addCourse(code, title, numStudents, description);
                linkedCourseList.add(new Course(code, title, numStudents, description)); // Update linked list
                loadCoursesToTable(); // Refresh table items
                clearFields();
            }
        });
    }

    // Method to delete a course
    @FXML
    private void deleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            courseList.deleteCourse(selectedCourse.getCode());
            linkedCourseList.remove(selectedCourse); // Update the LinkedList
            loadCoursesToTable(); // Refresh table items
        } else {
            System.out.println("No course selected for deletion.");
        }
    }

    // Method to clear input fields
    private void clearFields() {
        courseCodeField.clear();
        courseTitleField.clear();
        courseDescriptionField.clear();
    }

    // Method to open the student list for the selected course
    private void openStudentList(String courseCode) {
        try {
            // Load the StudentList view and controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentList.fxml"));
            Parent root = loader.load();
            StudentListController studentListController = loader.getController();
            studentListController.setCourseCode(courseCode); // Pass the course code to the controller

            // Show the student list scene
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Student List for " + courseCode);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCoursesToTable() {
        linkedCourseList.clear(); // Clear previous entries
        linkedCourseList.addAll(courseList.getCourses()); // Load courses from the CourseList
        courseTable.setItems(FXCollections.observableArrayList(linkedCourseList)); // Update table
    }

    @FXML
    private Button backButton;
    @FXML
    private void handleBackButton(ActionEvent event) throws IOException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
        Parent menuParent = loader.load();

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();

        Scene menuScene = new Scene(menuParent);
        stage.setScene(menuScene);
        stage.show();;
    }





}
