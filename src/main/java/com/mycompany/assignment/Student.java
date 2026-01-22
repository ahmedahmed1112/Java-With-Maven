package com.mycompany.assignment;

public class Student {
    private String studentId;
    private String name;
    private String program;

    public Student(String studentId, String name, String program) {
        this.studentId = studentId;
        this.name = name;
        this.program = program;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getProgram() {
        return program;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    // شكل السطر داخل students.txt
    public String toFileString() {
        return studentId + "," + name + "," + program;
    }

    // قراءة سطر من students.txt وتحويله لكائن Student
    public static Student fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",", -1);
        if (parts.length < 3) return null;
        return new Student(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    @Override
    public String toString() {
        return studentId + " - " + name + " (" + program + ")";
    }
}
