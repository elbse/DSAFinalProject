package com.example.finalproject;

import java.io.*;
import java.util.LinkedList;

public class StudentList {
    private LinkedList<Student> students;
    private String courseCode;

    public StudentList(String courseCode) {
        this.courseCode = courseCode;
        this.students = new LinkedList<>();
        loadStudents();
    }

    // Load students from a file associated with this.courseCode
    private void loadStudents() {
        try (BufferedReader br = new BufferedReader(new FileReader(courseCode + "_students.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    Student student = new Student(data[0], data[1], data[2], data[3]);
                    students.add(student);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addStudent(String id, String firstName, String lastName, String program) {
        Student student = new Student(id, firstName, lastName, program);
        students.add(student);
        saveStudents(); // Save to file
    }

    public void deleteStudent(String id) {
        students.removeIf(student -> student.getId().equals(id));
        saveStudents(); // Save to file
    }

    private void saveStudents() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(courseCode + "_students.txt"))) {
            for (Student student : students) {
                bw.write(student.getId() + "," + student.getFirstName() + "," + student.getLastName() + "," + student.getProgram());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<Student> getStudents() {
        return students;
    }
}
