package com.example.finalproject;

import java.io.*;
import java.util.LinkedList;

public class CourseList {
    private LinkedList<Course> courses;

    public CourseList() {
        courses = new LinkedList<>();
        loadCourses();
    }

    // Load courses from a file
    private void loadCourses() {
        try (BufferedReader br = new BufferedReader(new FileReader("courses.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    Course course = new Course(data[0], data[1], Integer.parseInt(data[2]), data[3]);
                    courses.add(course);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCourse(String code, String title, int numStudents, String description) {
        Course course = new Course(code, title, numStudents, description);
        courses.add(course);
        saveCourses(); // Save to file
    }

    public void deleteCourse(String code) {
        courses.removeIf(course -> course.getCode().equals(code));
        saveCourses(); // Save to file
    }

    private void saveCourses() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("courses.txt"))) {
            for (Course course : courses) {
                bw.write(course.getCode() + "," + course.getTitle() + "," + course.getNumStudents() + "," + course.getDescription());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<Course> getCourses() {
        return courses;
    }

    // Method to check if a course code exists
    public boolean courseExists(String courseCode) {
        for (Course course : courses) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                return true;
            }
        }
        return false;
    }

    // Method to check if a course title exists
    public boolean courseTitleExists(String courseTitle) {
        for (Course course : courses) {
            if (course.getTitle().equalsIgnoreCase(courseTitle)) {
                return true;
            }
        }
        return false;
    }
}
