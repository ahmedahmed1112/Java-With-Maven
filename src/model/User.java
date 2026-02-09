package model;

public class User {

    // ===== Attributes =====
    protected String userId;
    protected String username;
    protected String password;
    protected String name;
    protected String gender;
    protected String email;
    protected String phone;
    protected int age;
    protected String role;

    // ===== Constructors =====
    // New schema constructor
    public User(String userId, String username, String password, String name,
                String gender, String email, String phone, int age, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.role = role;
    }

    // Backward-compatible constructor (old schema)
    public User(String userId, String name, String username, String password, String role) {
        this(userId, username, password, name, "", "", "", 0, role);
    }

    // ===== Getters & Setters =====
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // ===== Utility =====
    @Override
    public String toString() {
        return safe(userId) + "|" +
               safe(username) + "|" +
               safe(password) + "|" +
               safe(name) + "|" +
               safe(gender) + "|" +
               safe(email) + "|" +
               safe(phone) + "|" +
               age + "|" +
               safe(role);
    }

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
