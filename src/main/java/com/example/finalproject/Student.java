package com.example.finalproject;

public class Student {
    private String id;
    private String firstName;
    private String lastName;
    private String program;

    public Student(String id, String firstName, String lastName, String program) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.program = program;
    }
    // R
    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProgram() {
        return program;
    }

    // Method to get full name (First Name + Last Name)
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
