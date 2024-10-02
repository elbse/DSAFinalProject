package com.example.finalproject;

public class Course {
    private String code;
    private String title;
    private String description;
    private int studentCount; // Keep track of number of students

    public Course(String code, String title, String description) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.studentCount = 0; // Initialize student count to 0
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getStudentCount() {
        return studentCount; // Return the student count
    }

    public void addStudent() {
        studentCount++; // Increment the student count
    }

    public void removeStudent() {
        if (studentCount > 0) {
            studentCount--; // Decrement the student count if greater than 0
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Course)) return false;
        Course course = (Course) obj;
        return code.equals(course.code);
    }
}
