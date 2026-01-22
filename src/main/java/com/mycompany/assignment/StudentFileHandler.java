package com.mycompany.assignment;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StudentFileHandler {

    // مسار الملف داخل resources
    private static final String FILE_PATH = "data/students.txt";

    private InputStream openResourceFile() throws IOException {
        InputStream in = StudentFileHandler.class.getClassLoader().getResourceAsStream(FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + FILE_PATH);
        }
        return in;
    }

    /**
     * قراءة كل الطلاب من students.txt
     */
    public List<Student> loadAllStudents() {
        List<Student> students = new ArrayList<>();

        try (InputStream in = openResourceFile();
             BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                Student s = Student.fromFileString(line);
                if (s != null) students.add(s);
            }

        } catch (IOException e) {
            // إذا الملف فاضي/غير موجود أثناء التشغيل، نرجع قائمة فاضية بدون ما نكسر البرنامج
            System.out.println("Warning: could not read students file: " + e.getMessage());
        }

        return students;
    }

    /**
     * إضافة طالب جديد (ملاحظة مهمة: الكتابة على resources داخل jar ليست مضمونة بعد build)
     * الآن نخليها مجرد مثال للتخزين.
     */
    public void appendStudentLine(String lineToWrite) {
        // هذه الطريقة تكتب على مسار المشروع أثناء التطوير فقط
        // (داخل NetBeans) وليس داخل jar بعد build.
        File projectFile = new File("src/main/resources/" + FILE_PATH);

        projectFile.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(projectFile, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {

            bw.write(lineToWrite);
            bw.newLine();

        } catch (IOException e) {
            System.out.println("Error writing students file: " + e.getMessage());
        }
    }

    public void saveStudent(Student student) {
        appendStudentLine(student.toFileString());
    }
}
