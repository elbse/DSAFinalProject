package com.example.finalproject;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class StudentListController {

    @FXML
    private TableView<Student> studentTableView; // Your TableView for students
    private StudentList studentList; // Your StudentList instance
    private Course selectedCourse; // The selected course

    // Method to add a student
    @FXML
    private void addStudentButtonClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Student");
        dialog.setHeaderText("Enter Student Details");

        // Create a dialog for student ID
        dialog.setContentText("Student ID:");
        Optional<String> studentId = dialog.showAndWait();
        if (studentId.isPresent()) {
            String id = studentId.get();

            // Prompt for first and last name and program
            dialog.setContentText("First Name:");
            Optional<String> firstName = dialog.showAndWait();
            dialog.setContentText("Last Name:");
            Optional<String> lastName = dialog.showAndWait();
            dialog.setContentText("Program:");
            Optional<String> program = dialog.showAndWait();

            if (firstName.isPresent() && lastName.isPresent() && program.isPresent()) {
                Student newStudent = new Student(id, firstName.get(), lastName.get(), program.get());
                studentList.add(newStudent); // Add the student to your student list
                selectedCourse.addStudent(); // Update student count in the course

                // Show success message
                showAlert("Success", "Student added successfully!");
            }
        }
    }

    // Method to remove a student
    @FXML
    private void deleteStudentButtonClicked() {
        Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            studentList.remove(selectedStudent); // Remove from student list
            selectedCourse.removeStudent(); // Update student count in the course

            // Show success message
            showAlert("Success", "Student removed successfully!");
        } else {
            showAlert("Error", "Please select a student to delete.");
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

    // Add sort functionality
    @FXML
    private void sortStudentsButtonClicked() {
        studentList.sortStudents(); // Assuming sortStudents is implemented in StudentList
        studentTableView.refresh(); // Refresh the table view
    }
}
