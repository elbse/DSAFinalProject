package com.example.finalproject;

public class CourseList {
    private Node<Course> head;

    public CourseList() {
        this.head = null;
    }

    public void add(Course course) {
        Node<Course> newNode = new Node<>(course);
        if (head == null) {
            head = newNode;
        } else {
            Node<Course> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    public void remove(Course course) {
        if (head == null) return;

        if (head.data.equals(course)) {
            head = head.next;
            return;
        }

        Node<Course> current = head;
        while (current.next != null) {
            if (current.next.data.equals(course)) {
                current.next = current.next.next;
                return;
            }
            current = current.next;
        }
    }

    public Course find(String courseCode) {
        Node<Course> current = head;
        while (current != null) {
            if (current.data.getCode().equals(courseCode)) {
                return current.data;
            }
            current = current.next;
        }
        return null; // Not found
    }

    public Course[] getCourses() {
        int size = size();
        Course[] courses = new Course[size];
        Node<Course> current = head;
        int index = 0;

        while (current != null) {
            courses[index++] = current.data;
            current = current.next;
        }
        return courses;
    }

    public int size() {
        int size = 0;
        Node<Course> current = head;
        while (current != null) {
            size++;
            current = current.next;
        }
        return size;
    }
}
