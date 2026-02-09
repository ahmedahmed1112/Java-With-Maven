package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static List<String> readAll(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) return lines;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void append(String filePath, String line) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(line);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update line where first column equals idKey (idKey|...)
    public static void updateById(String filePath, String idKey, String newLine) {
        List<String> lines = readAll(filePath);
        List<String> out = new ArrayList<>();

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length == 0) continue;

            String key = p[0].trim();
            if (key.equalsIgnoreCase(idKey)) {
                out.add(newLine);
            } else {
                out.add(line);
            }
        }

        writeAll(filePath, out);
    }

    // Delete line where first column equals idKey (idKey|...)
    public static void deleteById(String filePath, String idKey) {
        List<String> lines = readAll(filePath);
        List<String> out = new ArrayList<>();

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length == 0) continue;

            String key = p[0].trim();
            if (!key.equalsIgnoreCase(idKey)) {
                out.add(line);
            }
        }

        writeAll(filePath, out);
    }

    // ✅ NEW: overwrite entire file with lines
    public static void writeAll(String filePath, List<String> lines) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
                for (String s : lines) {
                    bw.write(s);
                    bw.newLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
