package afs.student.model;

public abstract class User {
    private final String userId;
    private final String role;
    private final String username;

    private String password;
    private String name;
    private String gender;
    private String email;
    private String phone;
    private int age;

    protected User(String userId, String role, String username, String password,
                   String name, String gender, String email, String phone, int age) {
        this.userId = userId;
        this.role = role;
        this.username = username;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.age = age;
    }

    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getUsername() { return username; }

    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public int getAge() { return age; }

    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setGender(String gender) { this.gender = gender; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAge(int age) { this.age = age; }
}
