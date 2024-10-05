package com.example.finalproject;

import com.example.finalproject.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StudentListController {
    @FXML
    private TableView<Student> studentTable;

    @FXML
    private Button addStudentButton;

    @FXML
    private Button deleteStudentButton;

    @FXML
    private AnchorPane rootPane;

    private String courseCode;

    @FXML
    private void initialize() {
        loadStudents();
        // Add sorting mechanism
        studentTable.getItems().sort(Comparator.comparing(Student::getLastName)); // Sort by last name
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
        loadStudents();
    }

    private void loadStudents() {
        List<Student> students = new ArrayList<>();
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

    @FXML
    private void addStudent() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Student");
        dialog.setHeaderText("Enter Student Details (ID, First Name, Last Name, Program)");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(details -> {
            String[] parts = details.split(",");
            if (parts.length == 4) {
                Student newStudent = new Student(parts[0], parts[1], parts[2], parts[3]);
                studentTable.getItems().add(newStudent);
                saveStudent(newStudent);
            } else {
                System.out.println("Please enter exactly 4 details.");
            }
        });
    }

    @FXML
    private void deleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            studentTable.getItems().remove(selectedStudent);
            updateStudentFile();
        }
    }

    private void saveStudent(Student student) {
        try (FileWriter fw = new FileWriter(courseCode + ".txt", true)) {
            fw.write(student.getId() + "," + student.getFirstName() + "," + student.getLastName() + "," + student.getProgram() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateStudentFile() {
        try (FileWriter fw = new FileWriter(courseCode + ".txt")) {
            for (Student student : studentTable.getItems()) {
                fw.write(student.getId() + "," + student.getFirstName() + "," + student.getLastName() + "," + student.getProgram() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
