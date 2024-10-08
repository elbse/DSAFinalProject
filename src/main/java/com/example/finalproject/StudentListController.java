package com.example.finalproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
    private Button sortStudentsButton; // Added sort button
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
        try (BufferedReader br = new BufferedReader(new FileReader(courseCode + "_students.txt"))) {
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
        // Check if the student list has reached the maximum of 50 students
        if (studentTable.getItems().size() >= 50) {
            // Show a message if the list is full
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("List Full");
            alert.setHeaderText("Cannot Add Student");
            alert.setContentText("The student list is full. Maximum 50 students allowed.");
            alert.showAndWait();
            return; // Exit the method if the limit is reached
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Student");
        dialog.setHeaderText("Enter Student Details (ID, First Name, Last Name, Program)");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(details -> {
            String[] parts = details.split(",");
            if (parts.length == 4) {
                String id = parts[0].trim();
                String firstName = parts[1].trim();
                String lastName = parts[2].trim();
                String program = parts[3].trim();

                // Add student to the file and update the linked list
                Student newStudent = new Student(id, firstName, lastName, program);
                saveStudent(newStudent);
                updateCourseStudentCount(); // Update course student count after adding the student
                loadStudents(); // Refresh the student list
            }
        });
    }


    private void updateCourseStudentCount() {
        CourseList courseList = new CourseList(); // Get the CourseList
        for (Course course : courseList.getCourses()) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                course.incrementStudentCount(); // Increment student count in Course
                break;  // Break after finding the course
            }
        }
        // Save the updated course list back to the file
        courseList.saveCourses(); // Ensure you have a method to save courses
    }

    private void saveStudent(Student student) {
        try (FileWriter writer = new FileWriter(courseCode + "_students.txt", true)) {
            writer.write(student.getId() + "," + student.getFirstName() + "," + student.getLastName() + "," + student.getProgram() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            removeStudentFromFile(selectedStudent.getId());
            loadStudents(); // Refresh the student list
        } else {
            System.out.println("No student selected for deletion.");
        }
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




    private void removeStudentFromFile(String studentId) {
        LinkedList<Student> students = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(courseCode + "_students.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (!parts[0].equals(studentId)) {
                    students.add(new Student(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(courseCode + "_students.txt")) {
            for (Student student : students) {
                writer.write(student.getId() + "," + student.getFirstName() + "," + student.getLastName() + "," + student.getProgram() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sortStudents() {
        // Get current items from the TableView
        LinkedList<Student> students = new LinkedList<>(studentTable.getItems());

        // Sort the students by last name
        students.sort((s1, s2) -> s1.getLastName().compareToIgnoreCase(s2.getLastName()));

        // Update the TableView with the sorted students
        studentTable.getItems().setAll(students);

        // Save the sorted students back to the text file
        saveSortedStudents(students);

        // Update the student count in the course list
        updateStudentCountInCourse();
    }

    private void saveSortedStudents(LinkedList<Student> sortedStudents) {
        try (FileWriter writer = new FileWriter(courseCode + "_students.txt")) {
            for (Student student : sortedStudents) {
                writer.write(student.getId() + "," + student.getFirstName() + "," + student.getLastName() + "," + student.getProgram() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateStudentCountInCourse() {
        CourseList courseList = new CourseList(); // Get the CourseList
        for (Course course : courseList.getCourses()) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                course.setNumStudents(studentTable.getItems().size()); // Set the student count based on the current list size
                break;  // Break after finding the course
            }
        }
        // Save the updated course list back to the file
        courseList.saveCourses(); // Ensure you have a method to save courses
    }
}