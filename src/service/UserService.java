package service;

import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static final String USERS_PATH = "src/data/users.txt";
    private static final String SPLIT_REGEX = "\\|";

    public List<User> getUsersByRole(String role) {
        String r = role == null ? "" : role.trim().toUpperCase();
        List<User> all = getAllUsers();
        List<User> out = new ArrayList<>();
        for (User u : all) {
            if (u.getRole() != null && u.getRole().trim().toUpperCase().equals(r)) {
                out.add(u);
            }
        }
        return out;
    }

    public User getById(String userId) {
        String id = userId == null ? "" : userId.trim();
        if (id.isEmpty()) return null;

        for (User u : getAllUsers()) {
            if (u.getUserId() != null && u.getUserId().trim().equalsIgnoreCase(id)) {
                return u;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        File f = new File(USERS_PATH);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // if you have header in users.txt, skip it (optional safety)
                if (line.toLowerCase().startsWith("id|")) continue;

                String[] p = line.split(SPLIT_REGEX);
                if (p.length < 9) continue;

                // expected order:
                // 0 id, 1 username, 2 password, 3 name, 4 gender, 5 email, 6 phone, 7 age, 8 role
                User u = new User();
                u.setUserId(p[0].trim());
                u.setUsername(p[1].trim());
                u.setPassword(p[2].trim());
                u.setName(p[3].trim());
                u.setGender(p[4].trim());
                u.setEmail(p[5].trim());
                u.setPhone(p[6].trim());
                try {
                    u.setAge(Integer.parseInt(p[7].trim()));
                } catch (Exception e) {
                    u.setAge(0);
                }
                u.setRole(p[8].trim());

                list.add(u);
            }
        } catch (IOException ignored) {
        }

        return list;
    }
}
