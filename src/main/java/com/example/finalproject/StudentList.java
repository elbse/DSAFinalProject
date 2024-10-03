package com.example.finalproject;

public class StudentList {
    private Node<Student> head;

    public StudentList() {
        this.head = null;
    }

    public void add(Student student) {
        Node<Student> newNode = new Node<>(student);
        if (head == null) {
            head = newNode;
        } else {
            Node<Student> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    public void remove(Student student) {
        if (head == null) return;

        if (head.data.equals(student)) {
            head = head.next;
            return;
        }

        Node<Student> current = head;
        while (current.next != null) {
            if (current.next.data.equals(student)) {
                current.next = current.next.next;
                return;
            }
            current = current.next;
        }
    }

    public Student[] getStudents() {
        int size = size();
        Student[] students = new Student[size];
        Node<Student> current = head;
        int index = 0;

        while (current != null) {
            students[index++] = current.data;
            current = current.next;
        }
        return students;
    }

    public int size() {
        int size = 0;
        Node<Student> current = head;
        while (current != null) {
            size++;
            current = current.next;
        }
        return size;
    }

    public void sortStudents() {
        // Implement sorting logic here if needed
    }
}
