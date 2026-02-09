package ui;

import model.User;
import util.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManageUsersFrame extends JFrame {

    private static final String USERS_FILE = "data/users.txt";

    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField txtId, txtUsername, txtName, txtGender, txtEmail, txtPhone, txtAge, txtRole;
    private JPasswordField txtPassword;

    private JTextField txtSearch;

    private JLabel lblTotal;
    private JLabel lblAdmins;

    private final List<User> allUsers = new ArrayList<>();

    private static final Set<String> ALLOWED_ROLES = new HashSet<>();
    static {
        ALLOWED_ROLES.add("ADMIN");
        ALLOWED_ROLES.add("STUDENT");
        ALLOWED_ROLES.add("LECTURER");
        ALLOWED_ROLES.add("LEADER");
    }

    public ManageUsersFrame() {
        setTitle("AFS Admin - Manage Users");
        setSize(1250, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(root);

        // ===== Header =====
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        titles.add(UIUtils.title("Manage Users"));
        titles.add(UIUtils.muted("CRUD users stored in data/users.txt (full schema)"));

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);

        txtSearch = UIUtils.modernTextField();
        txtSearch.setPreferredSize(new Dimension(340, 38));
        txtSearch.setToolTipText("Search by id / username / name / role / email / phone");

        JButton btnSearch = UIUtils.primaryButton("Search");
        JButton btnClearSearch = UIUtils.ghostButton("Clear");

        rightTop.add(txtSearch);
        rightTop.add(btnSearch);
        rightTop.add(btnClearSearch);

        header.add(titles, BorderLayout.WEST);
        header.add(rightTop, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // ===== Main Content =====
        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(14, 0, 0, 0));
        root.add(content, BorderLayout.CENTER);

        // Stats
        JPanel stats = new JPanel(new GridLayout(1, 2, 14, 0));
        stats.setOpaque(false);

        lblTotal = new JLabel("0");
        lblAdmins = new JLabel("0");
        stats.add(statCard("Total Users", "All roles in users.txt", lblTotal));
        stats.add(statCard("Admins", "Users with role ADMIN", lblAdmins));

        content.add(stats, BorderLayout.NORTH);

        // Split
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerSize(10);
        split.setContinuousLayout(true);

        split.setLeftComponent(buildFormCard());
        split.setRightComponent(buildTableCard());
        split.setResizeWeight(0.40);

        content.add(split, BorderLayout.CENTER);

        // Actions
        btnSearch.addActionListener(e -> applySearch());
        btnClearSearch.addActionListener(e -> {
            txtSearch.setText("");
            refreshTable(allUsers);
        });

        // Load
        loadUsersFromFile();
        refreshTable(allUsers);
        updateStats();
    }

    // ---------- UI Parts ----------
    private JPanel statCard(String title, String desc, JLabel valueLabel) {
        JPanel card = UIUtils.cardPanel();
        card.setLayout(new BorderLayout(0, 8));

        JLabel t = new JLabel(title);
        t.setForeground(Theme.TEXT);
        t.setFont(UIUtils.font(14, Font.BOLD));

        JLabel d = new JLabel(desc);
        d.setForeground(Theme.MUTED);
        d.setFont(UIUtils.font(12, Font.PLAIN));

        valueLabel.setForeground(Theme.TEXT);
        valueLabel.setFont(UIUtils.font(26, Font.BOLD));

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(t);
        top.add(d);

        card.add(top, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFormCard() {
        JPanel card = UIUtils.cardPanel();
        card.setLayout(new BorderLayout(0, 12));

        JLabel t = new JLabel("User Details");
        t.setForeground(Theme.TEXT);
        t.setFont(UIUtils.font(16, Font.BOLD));
        card.add(t, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.insets = new Insets(6, 0, 6, 0);

        txtId = UIUtils.modernTextField();
        txtName = UIUtils.modernTextField();
        txtUsername = UIUtils.modernTextField();
        txtPassword = UIUtils.modernPasswordField();
        txtGender = UIUtils.modernTextField();
        txtEmail = UIUtils.modernTextField();
        txtPhone = UIUtils.modernTextField();
        txtAge = UIUtils.modernTextField();
        txtRole = UIUtils.modernTextField();
        txtRole.setText("ADMIN");

        addField(form, gc, 0, "User ID (select a row to update/delete)", txtId);
        addField(form, gc, 2, "Username", txtUsername);
        addField(form, gc, 4, "Password (leave empty to keep old on update)", txtPassword);
        addField(form, gc, 6, "Full Name", txtName);
        addField(form, gc, 8, "Gender", txtGender);
        addField(form, gc, 10, "Email", txtEmail);
        addField(form, gc, 12, "Phone", txtPhone);
        addField(form, gc, 14, "Age", txtAge);
        addField(form, gc, 16, "Role (ADMIN / STUDENT / LECTURER / LEADER)", txtRole);

        JPanel btnRow = new JPanel(new GridLayout(2, 2, 10, 10));
        btnRow.setOpaque(false);

        JButton btnAdd = UIUtils.primaryButton("Add");
        JButton btnUpdate = UIUtils.primaryButton("Update");
        JButton btnDelete = UIUtils.dangerButton("Delete");
        JButton btnClear = UIUtils.ghostButton("Clear Form");

        btnRow.add(btnAdd);
        btnRow.add(btnUpdate);
        btnRow.add(btnDelete);
        btnRow.add(btnClear);

        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearForm());

        card.add(form, BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private void addField(JPanel panel, GridBagConstraints gc, int y, String labelText, JComponent field) {
        JLabel label = UIUtils.muted(labelText);
        gc.gridy = y;
        panel.add(label, gc);

        gc.gridy = y + 1;
        panel.add(field, gc);
    }

    private JPanel buildTableCard() {
        JPanel card = UIUtils.cardPanel();
        card.setLayout(new BorderLayout(0, 10));

        JLabel t = new JLabel("Users List");
        t.setForeground(Theme.TEXT);
        t.setFont(UIUtils.font(16, Font.BOLD));
        card.add(t, BorderLayout.NORTH);

        String[] cols = {"ID", "Username", "Name", "Gender", "Email", "Phone", "Age", "Role"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(UIUtils.font(13, Font.PLAIN));
        table.setBackground(Theme.CARD);
        table.setForeground(Theme.TEXT);
        table.setGridColor(Theme.BORDER);

        table.setSelectionBackground(new Color(40, 70, 140));
        table.setSelectionForeground(Color.WHITE);

        // Prevent squeezing + enable horizontal scrolling
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Apply widths and Age center
        applyColumnWidths();

        table.setDefaultRenderer(Object.class, usersCellRenderer());
        styleHeader(table.getTableHeader());

        JScrollPane sp = new JScrollPane(table);
        UIUtils.styleScrollPane(sp);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        card.add(sp, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelected());

        return card;
    }

    private void applyColumnWidths() {
        if (table.getColumnModel().getColumnCount() < 8) return;

        table.getColumnModel().getColumn(0).setPreferredWidth(80);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(140);  // Username
        table.getColumnModel().getColumn(2).setPreferredWidth(160);  // Name
        table.getColumnModel().getColumn(3).setPreferredWidth(110);  // Gender
        table.getColumnModel().getColumn(4).setPreferredWidth(260);  // Email
        table.getColumnModel().getColumn(5).setPreferredWidth(150);  // Phone
        table.getColumnModel().getColumn(6).setPreferredWidth(80);   // Age
        table.getColumnModel().getColumn(7).setPreferredWidth(140);  // Role

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(6).setCellRenderer(center);
    }

    // ----- Header color fix (high contrast) -----
    private void styleHeader(JTableHeader header) {
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setOpaque(true);

        header.setBackground(new Color(30, 34, 42));
        header.setForeground(Color.WHITE);
        header.setFont(UIUtils.font(14, Font.BOLD));

        header.setBorder(BorderFactory.createMatteBorder(
                0, 0, 2, 0, new Color(90, 120, 200)
        ));

        header.setDefaultRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JLabel label = new JLabel(value == null ? "" : value.toString());
            label.setOpaque(true);
            label.setBackground(new Color(30, 34, 42));
            label.setForeground(Color.WHITE);
            label.setFont(UIUtils.font(14, Font.BOLD));
            label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            return label;
        });
    }

    private TableCellRenderer usersCellRenderer() {
        return (tbl, value, isSelected, hasFocus, row, col) -> {
            String text = value == null ? "" : value.toString();
            JLabel cell = new JLabel(text);
            cell.setOpaque(true);
            cell.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            cell.setFont(UIUtils.font(13, Font.BOLD));

            if (isSelected) {
                cell.setBackground(tbl.getSelectionBackground());
                cell.setForeground(Color.WHITE);
                return cell;
            }

            cell.setBackground(Theme.CARD);

            if (col == 0) {
                cell.setForeground(Theme.PRIMARY);
            } else if (col == 7) {
                String role = text.toUpperCase();
                if (role.equals("ADMIN")) cell.setForeground(Theme.PRIMARY);
                else if (role.equals("LEADER")) cell.setForeground(new Color(95, 200, 140));
                else if (role.equals("LECTURER")) cell.setForeground(new Color(240, 190, 85));
                else cell.setForeground(Theme.TEXT);
            } else {
                cell.setForeground(Theme.TEXT);
            }

            return cell;
        };
    }

    // ---------- Data ----------
    private void loadUsersFromFile() {
        allUsers.clear();
        List<String> lines = FileManager.readAll(USERS_FILE);

        for (String line : lines) {
            if (line == null) continue;
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] p = line.split("\\|");

            // New schema: id|username|password|name|gender|email|phone|age|role
            if (p.length >= 9) {
                String id = safe(p, 0);
                String username = safe(p, 1);
                String password = safe(p, 2);
                String name = safe(p, 3);
                String gender = safe(p, 4);
                String email = safe(p, 5);
                String phone = safe(p, 6);
                int age = parseIntSafe(safe(p, 7), 0);
                String role = safe(p, 8);

                allUsers.add(new User(id, username, password, name, gender, email, phone, age, role));
                continue;
            }

            // Old schema: id|name|username|password|role
            if (p.length >= 5) {
                String id = safe(p, 0);
                String name = safe(p, 1);
                String username = safe(p, 2);
                String password = safe(p, 3);
                String role = safe(p, 4);

                allUsers.add(new User(id, name, username, password, role));
            }
        }
    }

    private void refreshTable(List<User> list) {
        tableModel.setRowCount(0);
        for (User u : list) {
            tableModel.addRow(new Object[]{
                    u.getUserId(),
                    u.getUsername(),
                    u.getName(),
                    u.getGender(),
                    u.getEmail(),
                    u.getPhone(),
                    u.getAge(),
                    u.getRole()
            });
        }
        applyColumnWidths();
    }

    private void updateStats() {
        lblTotal.setText(String.valueOf(allUsers.size()));
        long admins = allUsers.stream().filter(u -> u.getRole() != null && u.getRole().equalsIgnoreCase("ADMIN")).count();
        lblAdmins.setText(String.valueOf(admins));
    }

    // ---------- Actions ----------
    private void applySearch() {
        String q = txtSearch.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            refreshTable(allUsers);
            return;
        }

        List<User> filtered = new ArrayList<>();
        for (User u : allUsers) {
            if (contains(u.getUserId(), q) ||
                contains(u.getUsername(), q) ||
                contains(u.getName(), q) ||
                contains(u.getRole(), q) ||
                contains(u.getEmail(), q) ||
                contains(u.getPhone(), q)) {
                filtered.add(u);
            }
        }
        refreshTable(filtered);
    }

    private void addUser() {
        String id = txtId.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String name = txtName.getText().trim();
        String gender = txtGender.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String role = txtRole.getText().trim().toUpperCase();

        int age = parseAgeOrShowError(txtAge.getText().trim());
        if (age < 0) return;

        if (id.isEmpty() || username.isEmpty() || password.isEmpty() || name.isEmpty() || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill: ID, Username, Password, Name, Role.");
            return;
        }

        if (!ALLOWED_ROLES.contains(role)) {
            JOptionPane.showMessageDialog(this, "Invalid role. Allowed: ADMIN, STUDENT, LECTURER, LEADER.");
            return;
        }

        if (!email.isEmpty() && !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Invalid email.");
            return;
        }

        if (!phone.isEmpty() && !phone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Phone must be numeric.");
            return;
        }

        for (User u : allUsers) {
            if (u.getUserId() != null && u.getUserId().equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(this, "User ID already exists.");
                return;
            }
            if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
                return;
            }
        }

        User newUser = new User(id, username, password, name, gender, email, phone, age, role);
        FileManager.append(USERS_FILE, newUser.toString());

        loadUsersFromFile();
        refreshTable(allUsers);
        updateStats();
        clearForm();

        JOptionPane.showMessageDialog(this, "User added successfully.");
    }

    private void updateUser() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a user from the table first.");
            return;
        }

        User existing = findById(id);
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "User not found in file. Reload and try again.");
            return;
        }

        String username = txtUsername.getText().trim();
        String passwordInput = new String(txtPassword.getPassword()).trim();
        String password = passwordInput.isEmpty() ? safeStr(existing.getPassword()) : passwordInput;

        String name = txtName.getText().trim();
        String gender = txtGender.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String role = txtRole.getText().trim().toUpperCase();

        int age = parseAgeOrShowError(txtAge.getText().trim());
        if (age < 0) return;

        if (username.isEmpty() || name.isEmpty() || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill: Username, Name, Role.");
            return;
        }

        if (!ALLOWED_ROLES.contains(role)) {
            JOptionPane.showMessageDialog(this, "Invalid role. Allowed: ADMIN, STUDENT, LECTURER, LEADER.");
            return;
        }

        if (!email.isEmpty() && !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Invalid email.");
            return;
        }

        if (!phone.isEmpty() && !phone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Phone must be numeric.");
            return;
        }

        for (User u : allUsers) {
            if (u.getUserId() != null && u.getUserId().equalsIgnoreCase(id)) continue;
            if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
                return;
            }
        }

        User updated = new User(id, username, password, name, gender, email, phone, age, role);
        FileManager.updateById(USERS_FILE, id, updated.toString());

        loadUsersFromFile();
        refreshTable(allUsers);
        updateStats();
        clearForm();

        JOptionPane.showMessageDialog(this, "User updated successfully.");
    }

    private void deleteUser() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a user from the table first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete selected user: " + id + " ?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        FileManager.deleteById(USERS_FILE, id);

        loadUsersFromFile();
        refreshTable(allUsers);
        updateStats();
        clearForm();

        JOptionPane.showMessageDialog(this, "User deleted successfully.");
    }

    private void fillFormFromSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtUsername.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txtName.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtGender.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtEmail.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtPhone.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        txtAge.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        txtRole.setText(String.valueOf(tableModel.getValueAt(row, 7)));
        txtPassword.setText("");
    }

    private void clearForm() {
        txtId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtName.setText("");
        txtGender.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAge.setText("");
        txtRole.setText("ADMIN");
    }

    // ---------- Helpers ----------
    private User findById(String id) {
        for (User u : allUsers) {
            if (u.getUserId() != null && u.getUserId().equalsIgnoreCase(id)) return u;
        }
        return null;
    }

    private boolean contains(String s, String q) {
        if (s == null) return false;
        return s.toLowerCase().contains(q);
    }

    private String safe(String[] arr, int idx) {
        if (arr == null || idx < 0 || idx >= arr.length || arr[idx] == null) return "";
        return arr[idx].trim();
    }

    private String safeStr(String s) {
        return s == null ? "" : s.trim();
    }

    private int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return def; }
    }

    private int parseAgeOrShowError(String ageText) {
        if (ageText == null || ageText.trim().isEmpty()) return 0;
        try {
            int a = Integer.parseInt(ageText.trim());
            if (a < 0) {
                JOptionPane.showMessageDialog(this, "Age must be >= 0.");
                return -1;
            }
            return a;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Age must be a number.");
            return -1;
        }
    }
}
