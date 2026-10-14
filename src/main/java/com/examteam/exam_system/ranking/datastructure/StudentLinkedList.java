
package com.examteam.exam_system.ranking.datastructure;

import com.examteam.exam_system.user.model.Student;

public class StudentLinkedList {

    private static class Node {

        Student student;
        Node next;

        Node(Student student) {
            this.student = student;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public StudentLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    // Add student to the end of the list
    public void add(Student student) {

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student cannot be null"
            );
        }

        Node newNode = new Node(student);

        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }

        size++;
    }

    // Find a student using their database ID
    public Student findById(Long studentId) {

        Node current = head;

        while (current != null) {

            if (current.student.getId()
                    .equals(studentId)) {

                return current.student;
            }

            current = current.next;
        }

        return null;
    }

    // Remove student from the linked list
    public boolean remove(Long studentId) {

        if (head == null) {
            return false;
        }

        if (head.student.getId().equals(studentId)) {

            head = head.next;

            if (head == null) {
                tail = null;
            }

            size--;
            return true;
        }

        Node current = head;

        while (current.next != null) {

            if (current.next.student.getId()
                    .equals(studentId)) {

                if (current.next == tail) {
                    tail = current;
                }

                current.next = current.next.next;

                size--;
                return true;
            }

            current = current.next;
        }

        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
