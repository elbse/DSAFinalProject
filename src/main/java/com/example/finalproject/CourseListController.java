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
    private TableColumn<Course, String> courseCodeColumn; 
    @FXML
    private TableColumn<Course, String> courseTitleColumn;
    @FXML
    private TableColumn<Course, String> courseDescriptionColumn;
    @FXML
    private TableColumn<Course, Integer> numStudentsColumn;

    private CourseList courseList; // Instance of CourseList
    private LinkedList<Course> linkedCourseList;


    public CourseListController() {
        courseList = new CourseList();
        linkedCourseList = new LinkedList<>(courseList.getCourses());
    }

    @FXML
    public void initialize() {
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        courseDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        numStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("numStudents"));

        loadCoursesToTable();

        courseTable.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
                if (selectedCourse != null) {
                    openStudentList(selectedCourse.getCode());
                }
            }
        });
    }// R

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

                for (Course course : linkedCourseList) {
                    if (course.getCode().equalsIgnoreCase(code)) {
                        System.out.println("Course code already exists.");
                        return;
                    }
                }

                int numStudents = new StudentList(code).getStudents().size();
                courseList.addCourse(code, title, numStudents, description);
                linkedCourseList.add(new Course(code, title, numStudents, description));
                loadCoursesToTable();
                clearFields();
            }
        });
    }

    @FXML
    private void deleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            courseList.deleteCourse(selectedCourse.getCode());
            linkedCourseList.remove(selectedCourse);
            loadCoursesToTable();
        } else {
            System.out.println("No course selected for deletion.");
        }
    }

    private void clearFields() {
        courseCodeField.clear();
        courseTitleField.clear();
        courseDescriptionField.clear();
    }

    private void openStudentList(String courseCode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentList.fxml"));
            Parent root = loader.load();
            StudentListController studentListController = loader.getController();
            studentListController.setCourseCode(courseCode);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Student List for " + courseCode);
            stage.show();

            // Close the current course list window
            Stage currentStage = (Stage) courseTable.getScene().getWindow(); // Get current stage
            currentStage.close(); // Close the current stage

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadCoursesToTable() {
        linkedCourseList.clear();
        linkedCourseList.addAll(courseList.getCourses());
        courseTable.setItems(FXCollections.observableArrayList(linkedCourseList));
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
