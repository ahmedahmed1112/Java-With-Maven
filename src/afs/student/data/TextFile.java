package afs.student.data;

import afs.student.util.AppConfig;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class TextFile {
    private TextFile() {}

    private static Path path(String filename) {
        return Paths.get(AppConfig.DATA_DIR, filename);
    }

    public static List<String> readAll(String filename) {
        try {
            Path p = path(filename);
            if (!Files.exists(p)) return new ArrayList<>();
            return Files.readAllLines(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Read failed: " + filename + " -> " + e.getMessage(), e);
        }
    }

    public static void writeAll(String filename, List<String> lines) {
        try {
            Path p = path(filename);
            Files.createDirectories(p.getParent());
            Files.write(p, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Write failed: " + filename + " -> " + e.getMessage(), e);
        }
    }

    public static void appendLine(String filename, String line) {
        try {
            Path p = path(filename);
            Files.createDirectories(p.getParent());
            Files.write(p, Arrays.asList(line), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Append failed: " + filename + " -> " + e.getMessage(), e);
        }
    }
}
