package com.example.finalproject;

import com.example.finalproject.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;

public class StudentListController {

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, String> studentIdColumn;  // Column for Student ID
    @FXML
    private TableColumn<Student, String> fullNameColumn;   // Column for Full Name
    @FXML
    private TableColumn<Student, String> programColumn;    // Column for Program

    @FXML
    private Button addStudentButton;
    @FXML
    private Button deleteStudentButton;
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Text CourseDEs; // Reference to the Text element for course details
    private String courseCode;

    @FXML
    private void initialize() {
        // Initialize the table columns and bind them to the respective fields/methods
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        programColumn.setCellValueFactory(new PropertyValueFactory<>("program"));

        loadStudents();
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
        loadStudents();
        updateCourseDetails(); // Call to update the displayed course details
    }

    private void loadStudents() {
        LinkedList<Student> students = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(courseCode + ".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    students.add(new Student(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        studentTable.getItems().setAll(students);
    }

    private void updateCourseDetails() {
        // Update the course text based on the course code
        CourseList courseList = new CourseList(); // Ensure this retrieves the correct course list
        for (Course course : courseList.getCourses()) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                // Set the text of the CourseDEs Text element
                CourseDEs.setText(course.getTitle() + " - " + course.getDescription() + " (" + course.getCode() + ")");
                break;  // Break after finding the course
            }
        }
    }

    @FXML
    private void addStudent() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Student");
        dialog.setHeaderText("Enter Student Details (ID, First Name, Last Name, Program)");
        dialog.setContentText("Format: Student ID, First Name, Last Name, Program");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(details -> {
            String[] parts = details.split(",");
            if (parts.length == 4) {
                String id = parts[0].trim();
                String firstName = parts[1].trim();
                String lastName = parts[2].trim();
                String program = parts[3].trim();

                // Create a new student object
                Student newStudent = new Student(id, firstName, lastName, program);
                addStudentToFile(newStudent);
                updateCourseStudentCount(); // Update the student count for the course
                loadStudents(); // Reload the student list to show the newly added student
            } else {
                System.out.println("Please enter exactly 4 details.");
            }
        });
    }

    private void updateCourseStudentCount() {
        CourseList courseList = new CourseList(); // Retrieve the course list
        for (Course course : courseList.getCourses()) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                course.setNumStudents(course.getNumStudents() + 1); // Increment student count
                break; // Break after updating the course
            }
        }
    }


    private void addStudentToFile(Student student) {
        // Append the new student data to the file
        try (FileWriter fw = new FileWriter(courseCode + ".txt", true)) {
            fw.write(student.getId() + "," + student.getFirstName() + "," + student.getLastName() + "," + student.getProgram() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            removeStudentFromFile(selectedStudent);
            loadStudents(); // Reload the student list after deletion
        } else {
            System.out.println("No student selected for deletion.");
        }
    }

    private void removeStudentFromFile(Student student) {
        LinkedList<Student> students = new LinkedList<>();
        CourseList courseList = new CourseList(); // Retrieve the course list

        try (BufferedReader br = new BufferedReader(new FileReader(courseCode + ".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && !parts[0].trim().equals(student.getId())) {
                    students.add(new Student(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the updated list back to the file
        try (FileWriter fw = new FileWriter(courseCode + ".txt")) {
            for (Student s : students) {
                fw.write(s.getId() + "," + s.getFirstName() + "," + s.getLastName() + "," + s.getProgram() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Decrement the student count in the course
        for (Course course : courseList.getCourses()) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                course.setNumStudents(course.getNumStudents() - 1); // Decrement student count
                break; // Break after updating the course
            }
        }


    }


}

