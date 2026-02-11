package afs.student.model;

public class Student extends User {
    private final String studentId;
    private final String intakeCode;

    public Student(String studentId, String intakeCode,
                   String userId, String username, String password,
                   String name, String gender, String email, String phone, int age) {
        super(userId, "STUDENT", username, password, name, gender, email, phone, age);
        this.studentId = studentId;
        this.intakeCode = intakeCode;
    }

    public String getStudentId() { return studentId; }
    public String getIntakeCode() { return intakeCode; }
}
