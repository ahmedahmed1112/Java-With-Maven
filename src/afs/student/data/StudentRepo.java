package afs.student.data;

import afs.student.model.Student;
import afs.student.util.AppConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepo {

    // users.txt: userId|role|username|password|name|gender|email|phone|age
    // students.txt: studentId|userId|intakeCode

    public Optional<Student> loginStudent(String username, String password) {
        String userId = null, role = null, name = null, gender = null, email = null, phone = null;
        int age = 0;

        for (String line : TextFile.readAll("users.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 9) continue;

            if (p[2].equals(username) && p[3].equals(password)) {
                userId = p[0];
                role = p[1];
                name = p[4];
                gender = p[5];
                email = p[6];
                phone = p[7];
                try { age = Integer.parseInt(p[8].trim()); } catch (Exception ignored) {}
                break;
            }
        }

        if (userId == null || !"STUDENT".equalsIgnoreCase(role)) return Optional.empty();

        for (String line : TextFile.readAll("students.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 3) continue;

            if (p[1].equals(userId)) { // match by userId
                return Optional.of(new Student(
                        p[0], p[2],           // studentId, intake
                        userId, username, password,
                        name, gender, email, phone, age
                ));
            }
        }
        return Optional.empty();
    }

    public void updateProfile(Student s) {
        List<String> users = TextFile.readAll("users.txt");
        List<String> out = new ArrayList<>();

        for (String line : users) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 9) { out.add(line); continue; }

            if (p[0].equals(s.getUserId())) {
                String newLine =
                        s.getUserId() + AppConfig.WRITE_DELIM + "STUDENT" + AppConfig.WRITE_DELIM +
                        s.getUsername() + AppConfig.WRITE_DELIM + s.getPassword() + AppConfig.WRITE_DELIM +
                        s.getName() + AppConfig.WRITE_DELIM + s.getGender() + AppConfig.WRITE_DELIM +
                        s.getEmail() + AppConfig.WRITE_DELIM + s.getPhone() + AppConfig.WRITE_DELIM +
                        s.getAge();

                out.add(newLine);
            } else {
                out.add(line);
            }
        }

        TextFile.writeAll("users.txt", out);
    }

    // ---------- Register helpers ----------
    public boolean studentIdExists(String studentId) {
        String sid = studentId == null ? "" : studentId.trim();

        for (String line : TextFile.readAll("students.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 1) continue;
            if (p[0].equalsIgnoreCase(sid)) return true; // studentId
        }
        return false;
    }

    private String nextUserId() {
        int max = 0;

        for (String line : TextFile.readAll("users.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 1) continue;

            String raw = p[0].trim(); // U0001
            if (raw.startsWith("U")) {
                try {
                    int n = Integer.parseInt(raw.substring(1));
                    if (n > max) max = n;
                } catch (Exception ignored) {}
            }
        }

        return String.format("U%04d", max + 1);
    }

    // يضيف سطر في users.txt وسطر في students.txt
    public boolean registerStudent(String studentId, String password) {
        String sid = studentId == null ? "" : studentId.trim();
        String pass = password == null ? "" : password.trim();

        if (sid.isEmpty() || pass.isEmpty())
            throw new IllegalArgumentException("Student ID and Password are required.");

        if (studentIdExists(sid)) return false;

        String userId = nextUserId();

        // users.txt: userId|role|username|password|name|gender|email|phone|age
        // نخلي username = studentId
        String userLine =
                userId + AppConfig.WRITE_DELIM + "STUDENT" + AppConfig.WRITE_DELIM +
                sid    + AppConfig.WRITE_DELIM + pass     + AppConfig.WRITE_DELIM +
                ""     + AppConfig.WRITE_DELIM + ""       + AppConfig.WRITE_DELIM +
                ""     + AppConfig.WRITE_DELIM + ""       + AppConfig.WRITE_DELIM +
                "0";

        // students.txt: studentId|userId|intakeCode
        String studentLine =
                sid + AppConfig.WRITE_DELIM + userId + AppConfig.WRITE_DELIM + "";

        TextFile.appendLine("users.txt", userLine);
        TextFile.appendLine("students.txt", studentLine);

        return true;
    }
}
