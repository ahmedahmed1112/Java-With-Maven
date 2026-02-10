package ui;

import model.User;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * LeaderDashboardFrame
 * -------------------
 * Academic Leader dashboard (styled like AdminDashboard).
 *
 * Requirements implemented here:
 * - Same dark theme layout: sidebar + main cards
 * - Logout button -> returns to LoginFrame
 * - Window X close -> also returns to LoginFrame (not exit)
 *
 * OOP pillars:
 * - Encapsulation: stores loggedInUser privately
 * - Abstraction: openFrameSafely(...) hides opening logic
 */
public class LeaderDashboardFrame extends JFrame {

    private final User loggedInUser;

    // Keep no-arg constructor for your current routeByRole() usage
    public LeaderDashboardFrame() {
        this(null);
    }

    // Optional constructor if later you want to pass the user
    public LeaderDashboardFrame(User user) {
        this.loggedInUser = user;

        setTitle("AFS Leader Dashboard");
        setSize(1020, 620);
        setLocationRelativeTo(null);

        // When user clicks X: go back to login (not exit)
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                goToLogin();
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        // ===== Sidebar (like AdminDashboard) =====
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Theme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(260, 620));
        sidebar.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel brand = preventBlueFocus(UIUtils.title("AFS Leader"));
        brand.setForeground(Theme.TEXT);
        brand.setFont(UIUtils.font(16, Font.BOLD));

        String leaderName = (loggedInUser == null || loggedInUser.getName() == null)
                ? "Leader"
                : loggedInUser.getName();

        JLabel role = new JLabel("Signed in as: " + leaderName);
        role.setForeground(Theme.MUTED);
        role.setFont(UIUtils.font(12, Font.PLAIN));

        JPanel brandBox = new JPanel(new GridLayout(2, 1, 0, 6));
        brandBox.setOpaque(false);
        brandBox.add(brand);
        brandBox.add(role);

        JPanel nav = new JPanel(new GridLayout(12, 1, 10, 10));
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(18, 0, 18, 0));

        // Button order (as you requested)
        JButton btnProfile = UIUtils.ghostButton("👤  Edit Profile");
        JButton btnModules = UIUtils.ghostButton("📚  Manage Modules");
        JButton btnAssign = UIUtils.ghostButton("🔗  Assign Lecturers");
        JButton btnReports = UIUtils.ghostButton("📊  Analyzed Reports");
        JButton btnLogout = UIUtils.dangerButton("Logout");

        nav.add(btnProfile);
        nav.add(btnModules);
        nav.add(btnAssign);
        nav.add(btnReports);

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
        JLabel pageSub = UIUtils.muted("Choose a leader task from the sidebar");

        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        titles.add(pageTitle);
        titles.add(pageSub);

        JButton quickReports = UIUtils.primaryButton("Open Reports");
        topbar.add(titles, BorderLayout.WEST);
        topbar.add(quickReports, BorderLayout.EAST);

        content.add(topbar, BorderLayout.NORTH);

        // Cards area (2x2 like AdminDashboard)
        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);

        grid.add(statCard(
                "Profile",
                "Update your own details (users.txt)",
                "Open Profile",
                () -> openFrameSafely("ui.LeaderProfileFrame")
        ));

        grid.add(statCard(
                "Modules",
                "Create / update / delete modules (modules.txt)",
                "Open Modules",
                () -> openFrameSafely("ui.ManageModulesFrame")
        ));

        grid.add(statCard(
                "Assign Lecturers",
                "Assign lecturers to modules (modules.txt)",
                "Open Assign",
                () -> openFrameSafely("ui.AssignLecturersToModulesFrame")
        ));

        grid.add(statCard(
                "Reports",
                "View analyzed reports (sample data)",
                "Open Reports",
                () -> openFrameSafely("ui.LeaderReportsFrame")
        ));

        content.add(grid, BorderLayout.CENTER);

        // ===== Actions =====
        btnProfile.addActionListener(e -> openFrameSafely("ui.LeaderProfileFrame"));
        btnModules.addActionListener(e -> openFrameSafely("ui.ManageModulesFrame"));
        btnAssign.addActionListener(e -> openFrameSafely("ui.AssignLecturersToModulesFrame"));
        btnReports.addActionListener(e -> openFrameSafely("ui.LeaderReportsFrame"));
        quickReports.addActionListener(e -> openFrameSafely("ui.LeaderReportsFrame"));

        btnLogout.addActionListener(e -> goToLogin());
    }

    // Small helper to match AdminDashboard card style
    private JPanel statCard(String title, String desc, String buttonText, Runnable action) {
        JPanel card = UIUtils.cardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel h = new JLabel(title);
        h.setForeground(Theme.TEXT);
        h.setFont(UIUtils.font(15, Font.BOLD));

        JLabel d = new JLabel("<html><div style='width:320px;'>" + desc + "</div></html>");
        d.setForeground(Theme.MUTED);
        d.setFont(UIUtils.font(12, Font.PLAIN));

        JPanel top = new JPanel(new GridLayout(2, 1, 0, 8));
        top.setOpaque(false);
        top.add(h);
        top.add(d);

        JButton btn = UIUtils.primaryButton(buttonText);
        btn.addActionListener(e -> action.run());

        card.add(top, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Opens a JFrame by class name safely.
     * If the class doesn't exist yet, show a friendly popup (no crash).
     * Tries constructor(User) first, then no-arg.
     */
    private void openFrameSafely(String fullyQualifiedClassName) {
        try {
            Class<?> clazz = Class.forName(fullyQualifiedClassName);

            // try (User) constructor first
            try {
                Object frameObj = clazz.getConstructor(User.class).newInstance(loggedInUser);
                if (frameObj instanceof JFrame) {
                    ((JFrame) frameObj).setVisible(true);
                    return;
                }
            } catch (NoSuchMethodException ignored) {
                // fall back to no-arg
            }

            Object frameObj = clazz.getConstructor().newInstance();
            if (frameObj instanceof JFrame) {
                ((JFrame) frameObj).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Class exists but is not a JFrame:\n" + fullyQualifiedClassName,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "This screen is not implemented yet:\n" + fullyQualifiedClassName,
                    "Not ready", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to open screen:\n" + fullyQualifiedClassName + "\n\nReason: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goToLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }

    // Avoid any strange focus highlight on some LookAndFeels
    private JLabel preventBlueFocus(JLabel l) {
        l.setFocusable(false);
        return l;
    }
}
