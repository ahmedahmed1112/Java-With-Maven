package service;

import model.ClassRecord;
import util.FileManager;

import java.util.ArrayList;
import java.util.List;

public class ClassService {

    private static final String CLASSES_FILE = "data/classes.txt";

    public static List<ClassRecord> getAll() {
        List<ClassRecord> list = new ArrayList<>();
        List<String> lines = FileManager.readAll(CLASSES_FILE);

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;

            String[] p = line.split("\\|");
            if (p.length < 3) continue;

            list.add(new ClassRecord(p[0], p[1], p[2]));
        }
        return list;
    }

    public static boolean existsClassId(String classId) {
        for (ClassRecord c : getAll()) {
            if (c.getClassId().equalsIgnoreCase(classId)) return true;
        }
        return false;
    }

    public static void add(ClassRecord rec) {
        FileManager.append(CLASSES_FILE, rec.toString());
    }

    public static void update(String classId, ClassRecord newRec) {
        FileManager.updateById(CLASSES_FILE, classId, newRec.toString());
    }

    public static void delete(String classId) {
        FileManager.deleteById(CLASSES_FILE, classId);
    }
}
