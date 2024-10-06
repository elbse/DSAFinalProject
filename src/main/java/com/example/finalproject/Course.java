package com.example.finalproject;

public class Course {
    private String code;
    private String title;
    private int numStudents;
    private String description;

    public Course(String code, String title, int numStudents, String description) {
        this.code = code;
        this.title = title;
        this.numStudents = numStudents;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    public String getDescription() {
        return description;
    }
    public void incrementStudentCount() {
        this.numStudents++;
    }

    public void decrementStudentCount() {
        this.numStudents--;
    }
}


