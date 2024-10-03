package com.example.finalproject;

public class Student {
    private String id;        // Student ID
    private String firstName; // First Name
    private String lastName;  // Last Name
    private String program;    // Program

    // Constructor to initialize Student attributes
    public Student(String id, String firstName, String lastName, String program) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.program = program;
    }

    // Getter for Student ID
    public String getId() {
        return id;
    }

    // Getter for First Name
    public String getFirstName() {
        return firstName;
    }

    // Getter for Last Name
    public String getLastName() {
        return lastName;
    }

    // Getter for Program
    public String getProgram() {
        return program;
    }

    // Override toString method for easy display of Student details
    @Override
    public String toString() {
        return "Student ID: " + id + ", Name: " + firstName + " " + lastName + ", Program: " + program;
    }
}
