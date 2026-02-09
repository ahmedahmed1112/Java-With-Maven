package service;

import java.util.List;
import model.AdminStaff;
import model.User;
import util.FileManager;

public class AuthService {

    private static final String USERS_FILE = "data/users.txt";

    public static User login(String username, String password) {
        if (username == null || password == null) return null;

        String u = username.trim();
        String p = password.trim();
        if (u.isEmpty() || p.isEmpty()) return null;

        List<String> lines = FileManager.readAll(USERS_FILE);

        for (String line : lines) {
            if (line == null) continue;
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\|");

            // New schema: id|username|password|name|gender|email|phone|age|role
            if (parts.length >= 9) {
                String userId = parts[0].trim();
                String fileUsername = parts[1].trim();
                String filePassword = parts[2].trim();
                String name = parts[3].trim();
                String gender = parts[4].trim();
                String email = parts[5].trim();
                String phone = parts[6].trim();

                int age = 0;
                try { age = Integer.parseInt(parts[7].trim()); } catch (Exception ignored) {}

                String role = parts[8].trim();

                if (fileUsername.equalsIgnoreCase(u) && filePassword.equals(p)) {
                    if (role.equalsIgnoreCase("ADMIN")) {
                        return new AdminStaff(userId, fileUsername, filePassword, name, gender, email, phone, age);
                    }
                    return new User(userId, fileUsername, filePassword, name, gender, email, phone, age, role);
                }
                continue;
            }

            // Old schema: id|name|username|password|role
            if (parts.length >= 5) {
                String userId = parts[0].trim();
                String name = parts[1].trim();
                String fileUsername = parts[2].trim();
                String filePassword = parts[3].trim();
                String role = parts[4].trim();

                if (fileUsername.equalsIgnoreCase(u) && filePassword.equals(p)) {
                    if (role.equalsIgnoreCase("ADMIN")) {
                        return new AdminStaff(userId, name, fileUsername, filePassword);
                    }
                    return new User(userId, name, fileUsername, filePassword, role);
                }
            }
        }

        return null;
    }
}
