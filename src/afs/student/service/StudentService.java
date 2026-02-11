package afs.student.service;

import afs.student.data.StudentRepo;
import afs.student.data.TextFile;
import afs.student.model.Student;
import afs.student.util.AppConfig;
import afs.student.util.Validators;

import java.time.LocalDate;
import java.util.*;

public class StudentService {

    private final StudentRepo studentRepo = new StudentRepo();

    // ---------- 1) Login ----------
    public Optional<Student> login(String username, String password) {
        return studentRepo.loginStudent(username, password);
    }

    // ---------- 2) Edit Profile (self-service) ----------
    public void updateProfile(Student s,
                              String newPassword,
                              String name,
                              String gender,
                              String email,
                              String phone,
                              String ageStr) {

        if (!Validators.notBlank(newPassword) || !Validators.notBlank(name) || !Validators.notBlank(gender))
            throw new IllegalArgumentException("Required fields missing.");

        if (!Validators.isEmail(email))
            throw new IllegalArgumentException("Invalid email.");

        if (!Validators.isPhone(phone))
            throw new IllegalArgumentException("Invalid phone (digits only).");

        int age = Validators.positiveInt(ageStr, "Age must be positive.");

        s.setPassword(newPassword.trim());
        s.setName(name.trim());
        s.setGender(gender.trim());
        s.setEmail(email.trim());
        s.setPhone(phone.trim());
        s.setAge(age);

        studentRepo.updateProfile(s);
    }

    // ===============================
    // ✅ REGISTER STUDENT (NEW)
    // ===============================

    private boolean studentIdExists(String studentId) {
        for (String line : TextFile.readAll("students.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 1) continue;
            if (p[0].equalsIgnoreCase(studentId.trim())) return true;
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

    // ✅ اسمها register عشان يطابق استدعاء الـUI
    public boolean register(String studentId, String password) {
        String sid = studentId.trim();
        String pass = password.trim();

        if (sid.isEmpty() || pass.isEmpty())
            throw new IllegalArgumentException("Student ID and Password are required.");

        if (studentIdExists(sid)) return false;

        String userId = nextUserId();

        // users.txt: userId|role|username|password|name|gender|email|phone|age
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

    // ---------- 3) Register Classes (many-to-many) ----------
    public List<String[]> listClasses() {
        List<String[]> out = new ArrayList<>();
        for (String line : TextFile.readAll("classes.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 3) continue;
            out.add(new String[]{p[0], p[1], p[2]});
        }
        return out;
    }

    private boolean classExists(String classId) {
        for (String[] c : listClasses()) {
            if (c[0].equals(classId)) return true;
        }
        return false;
    }

    private Set<String> registeredClassIds(String studentId) {
        Set<String> set = new HashSet<>();
        for (String line : TextFile.readAll("student_classes.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 2) continue;
            if (p[0].equals(studentId)) set.add(p[1]);
        }
        return set;
    }

    public void registerClass(String studentId, String classId) {
        if (!Validators.notBlank(classId))
            throw new IllegalArgumentException("Select a class.");

        if (!classExists(classId))
            throw new IllegalArgumentException("Class does not exist.");

        if (registeredClassIds(studentId).contains(classId))
            throw new IllegalArgumentException("Duplicate registration is not allowed.");

        TextFile.appendLine("student_classes.txt", studentId + AppConfig.WRITE_DELIM + classId);
    }

    public Set<String> allowedModuleIds(String studentId) {
        Set<String> reg = registeredClassIds(studentId);
        Set<String> mods = new HashSet<>();
        for (String line : TextFile.readAll("classes.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 3) continue;
            if (reg.contains(p[0])) mods.add(p[2]);
        }
        return mods;
    }

    // ---------- 4) View Results + Feedback ----------
    public static class ResultRow {
        public final String assessmentName, type, totalMarks, weightage;
        public final String marks, grade, feedbackText;

        public ResultRow(String assessmentName, String type, String totalMarks, String weightage,
                         String marks, String grade, String feedbackText) {
            this.assessmentName = assessmentName;
            this.type = type;
            this.totalMarks = totalMarks;
            this.weightage = weightage;
            this.marks = marks;
            this.grade = grade;
            this.feedbackText = feedbackText;
        }
    }

    private Map<String, String[]> assessmentsById() {
        Map<String, String[]> map = new HashMap<>();
        for (String line : TextFile.readAll("assessments.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 7) continue;
            map.put(p[0], p);
        }
        return map;
    }

    private Map<String, String> feedbackByAssessment(String studentId) {
        Map<String, String> map = new HashMap<>();
        for (String line : TextFile.readAll("feedback.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 6) continue;
            if (p[2].equals(studentId)) map.put(p[1], p[4]);
        }
        return map;
    }

    public List<ResultRow> myResults(String studentId) {
        Set<String> allowedMods = allowedModuleIds(studentId);
        Map<String, String[]> assess = assessmentsById();
        Map<String, String> fb = feedbackByAssessment(studentId);

        List<ResultRow> out = new ArrayList<>();

        for (String line : TextFile.readAll("grades.txt")) {
            String[] g = line.split(AppConfig.SPLIT_DELIM, -1);
            if (g.length < 7) continue;
            if (!g[2].equals(studentId)) continue;

            String assessmentId = g[1];
            String[] a = assess.get(assessmentId);
            if (a == null) continue;

            String moduleId = a[1];
            if (!allowedMods.contains(moduleId)) continue;

            out.add(new ResultRow(
                    a[2], a[3], a[4], a[5],
                    g[3], g[4],
                    fb.getOrDefault(assessmentId, "")
            ));
        }
        return out;
    }

    // ---------- 5) Comments to Lecturer ----------
    public List<String[]> modulesForComment(String studentId) {
        Set<String> allowed = allowedModuleIds(studentId);
        List<String[]> out = new ArrayList<>();

        for (String line : TextFile.readAll("modules.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 6) continue;
            if (allowed.contains(p[0])) out.add(new String[]{p[0], p[1], p[5]});
        }
        return out;
    }

    private String nextCommentId() {
        int max = 0;
        for (String line : TextFile.readAll("comments.txt")) {
            String[] p = line.split(AppConfig.SPLIT_DELIM, -1);
            if (p.length < 1) continue;
            String n = p[0].replace("CM", "");
            try { max = Math.max(max, Integer.parseInt(n)); } catch (Exception ignored) {}
        }
        return String.format("CM%04d", max + 1);
    }

    public void submitComment(String studentId, String moduleId, String lecturerId, String comment) {
        if (!Validators.notBlank(comment))
            throw new IllegalArgumentException("Comment cannot be empty.");

        if (!allowedModuleIds(studentId).contains(moduleId))
            throw new IllegalArgumentException("You can only comment on registered modules.");

        String id = nextCommentId();
        String date = LocalDate.now().toString();
        String clean = comment.replace("\n", " ").trim();

        TextFile.appendLine("comments.txt",
                id + AppConfig.WRITE_DELIM + studentId + AppConfig.WRITE_DELIM + lecturerId + AppConfig.WRITE_DELIM +
                moduleId + AppConfig.WRITE_DELIM + clean + AppConfig.WRITE_DELIM + date);
    }
}
