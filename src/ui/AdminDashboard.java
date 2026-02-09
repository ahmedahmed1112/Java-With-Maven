package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;

public class AdminDashboard extends JFrame {

    private final User adminUser;

    public AdminDashboard(User adminUser) {
        this.adminUser = adminUser;

        setTitle("AFS Admin Dashboard");
        setSize(1020, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        // ===== Sidebar =====
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Theme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(260, 620));
        sidebar.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel brand = new JLabel("AFS Admin");
        brand.setForeground(Theme.TEXT);
        brand.setFont(UIUtils.font(16, Font.BOLD));

        JLabel role = new JLabel("Signed in as: " + adminUser.getName());
        role.setForeground(Theme.MUTED);
        role.setFont(UIUtils.font(12, Font.PLAIN));

        JPanel brandBox = new JPanel(new GridLayout(2, 1, 0, 6));
        brandBox.setOpaque(false);
        brandBox.add(brand);
        brandBox.add(role);

        JPanel nav = new JPanel(new GridLayout(12, 1, 10, 10));
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(18, 0, 18, 0));

        JButton btnUsers = UIUtils.ghostButton("👤  Manage Users");
        JButton btnClasses = UIUtils.ghostButton("🏫  Manage Classes");
        JButton btnGrading = UIUtils.ghostButton("🧮  Manage Grading");
        JButton btnAssign = UIUtils.ghostButton("🔗  Assign Lecturers");
        JButton btnLogout = UIUtils.dangerButton("Logout");

        nav.add(btnUsers);
        nav.add(btnClasses);
        nav.add(btnGrading);
        nav.add(btnAssign);

        sidebar.add(brandBox, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);
        sidebar.add(btnLogout, BorderLayout.SOUTH);

        root.add(sidebar, BorderLayout.WEST);

        // ===== Main Content =====
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.add(content, BorderLayout.CENTER);

        // Topbar
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setOpaque(false);
        topbar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel pageTitle = UIUtils.title("Dashboard");
        JLabel pageSub = UIUtils.muted("Choose an admin task from the sidebar");

        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        titles.add(pageTitle);
        titles.add(pageSub);

        JButton quickAssign = UIUtils.primaryButton("Open Assign Lecturers");
        topbar.add(titles, BorderLayout.WEST);
        topbar.add(quickAssign, BorderLayout.EAST);

        content.add(topbar, BorderLayout.NORTH);

        // Cards area
        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);

        grid.add(statCard(
                "Users",
                "Create / update / delete users (users.txt)",
                "Open Users",
                () -> new ManageUsersFrame().setVisible(true)
        ));

        grid.add(statCard(
                "Classes",
                "Manage classes for modules (classes.txt)",
                "Open Classes",
                () -> new ManageClassesFrame().setVisible(true)
        ));

        grid.add(statCard(
                "Grading",
                "Define grading rules (grading.txt)",
                "Open Grading",
                () -> new ManageGradingFrame().setVisible(true)
        ));

        // ✅ ASSIGN CARD (NEW)
        grid.add(statCard(
                "Assign Lecturers",
                "Assign Lecturer to Academic Leader (leader_lecturer.txt)",
                "Open Assign",
                () -> new ManageAssignLecturersFrame().setVisible(true)
        ));

        content.add(grid, BorderLayout.CENTER);

        // ===== Actions =====
        btnUsers.addActionListener(e -> new ManageUsersFrame().setVisible(true));
        btnClasses.addActionListener(e -> new ManageClassesFrame().setVisible(true));
        btnGrading.addActionListener(e -> new ManageGradingFrame().setVisible(true));

        // ✅ IMPORTANT: Link Assign button
        btnAssign.addActionListener(e -> new ManageAssignLecturersFrame().setVisible(true));
        quickAssign.addActionListener(e -> new ManageAssignLecturersFrame().setVisible(true));

        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private JPanel statCard(String title, String desc, String actionText, Runnable action) {
        JPanel card = UIUtils.cardPanel();
        card.setLayout(new BorderLayout(0, 10));

        JLabel t = new JLabel(title);
        t.setForeground(Theme.TEXT);
        t.setFont(UIUtils.font(16, Font.BOLD));

        JLabel d = new JLabel("<html><div style='width:260px;'>" + desc + "</div></html>");
        d.setForeground(Theme.MUTED);
        d.setFont(UIUtils.font(12, Font.PLAIN));

        JButton a = UIUtils.primaryButton(actionText);
        a.addActionListener(e -> action.run());

        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);
        card.add(a, BorderLayout.SOUTH);

        return card;
    }
}
