package ui;

import model.Module;
import model.User;
import service.ModuleService;
import util.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ManageModulesFrame extends JFrame {

    private final User loggedInUser;
    private final String leaderId;

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtModuleName;
    private JTextField txtModuleCode;
    private JTextField txtCreditHours;
    private JTextField txtLecturerId;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;

    private static final String LEADER_LECTURER_FILE = "data/leader_lecturer.txt";

    // ✅ Increased height for better text visibility
    private static final int FIELD_HEIGHT = 45;

    public ManageModulesFrame() {
        this(null);
    }

    public ManageModulesFrame(User user) {
        this.loggedInUser = user;

        if (loggedInUser != null && loggedInUser.getUserId() != null) {
            this.leaderId = loggedInUser.getUserId().trim();
        } else {
            this.leaderId = "";
        }

        setTitle("Manage Modules (Leader)");
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(root);

        // ===== Top bar =====
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);

        JLabel title = UIUtils.title("Modules");
        JLabel sub = UIUtils.muted("Create / update / delete your modules (modules.txt)");

        titles.add(title);
        titles.add(sub);

        JButton btnRefresh = UIUtils.primaryButton("Refresh");
        btnRefresh.addActionListener(e -> loadTable());

        top.add(titles, BorderLayout.WEST);
        top.add(btnRefresh, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        // ===== Center area: table + form =====
        JPanel center = new JPanel(new GridLayout(1, 2, 14, 14));
        center.setOpaque(false);

        // ---- Table Card ----
        JPanel tableCard = UIUtils.cardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel tableTitle = new JLabel("Your Modules (Leader ID: " + (leaderId.isEmpty() ? "Unknown" : leaderId) + ")");
        tableTitle.setForeground(Theme.TEXT);
        tableTitle.setFont(UIUtils.font(14, Font.BOLD));
        tableCard.add(tableTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Module ID", "Module Name", "Module Code", "Credits", "Lecturer ID"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        styleTableToMatchDarkUI(table);

        JScrollPane sp = new JScrollPane(table);
        UIUtils.styleScrollPane(sp);
        sp.setBorder(new EmptyBorder(10, 0, 0, 0));
        tableCard.add(sp, BorderLayout.CENTER);

        // ---- Form Card ----
        JPanel formCard = UIUtils.cardPanel();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel formTitle = new JLabel("Module Details");
        formTitle.setForeground(Theme.TEXT);
        formTitle.setFont(UIUtils.font(14, Font.BOLD));
        formCard.add(formTitle, BorderLayout.NORTH);

        // ✅ Switched to GridBagLayout for better control over field sizes
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(12, 0, 12, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0); // Vertical spacing between fields

        txtModuleName = UIUtils.modernTextField();
        makeFieldTaller(txtModuleName);

        txtModuleCode = UIUtils.modernTextField();
        makeFieldTaller(txtModuleCode);

        txtCreditHours = UIUtils.modernTextField();
        makeFieldTaller(txtCreditHours);

        txtLecturerId = UIUtils.modernTextField();
        makeFieldTaller(txtLecturerId);
        txtLecturerId.setEditable(false);
        txtLecturerId.setEnabled(true);
        txtLecturerId.setBackground(Color.WHITE);
        txtLecturerId.setForeground(Color.BLACK);
        txtLecturerId.setCaretColor(Color.BLACK);

        // Adding labeled fields using GridBag
        gbc.gridy = 0; form.add(labeled("Module Name", txtModuleName), gbc);
        gbc.gridy = 1; form.add(labeled("Module Code", txtModuleCode), gbc);
        gbc.gridy = 2; form.add(labeled("Credit Hours", txtCreditHours), gbc);
        gbc.gridy = 3; form.add(labeled("Lecturer ID (Read Only)", txtLecturerId), gbc);

        // Use NORTH to prevent fields from stretching vertically to fill the card
        formCard.add(form, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(2, 2, 10, 10));
        actions.setOpaque(false);

        btnAdd = UIUtils.primaryButton("Add Module");
        btnUpdate = UIUtils.primaryButton("Update Selected");
        btnDelete = UIUtils.dangerButton("Delete Selected");
        JButton btnClose = UIUtils.ghostButton("Close");

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClose.addActionListener(e -> dispose());

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnClose);

        formCard.add(actions, BorderLayout.SOUTH);

        center.add(tableCard);
        center.add(formCard);
        root.add(center, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = table.getSelectedRow();
            if (r < 0) return;

            txtModuleName.setText(valueAt(r, 1));
            txtModuleCode.setText(valueAt(r, 2));
            txtCreditHours.setText(valueAt(r, 3));

            String lec = valueAt(r, 4).replace(" (INVALID)", "").trim();
            txtLecturerId.setText(lec);
        });

        loadTable();

        if (leaderId.isEmpty()) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);

            JOptionPane.showMessageDialog(this,
                    "Leader ID is missing.",
                    "Missing Leader ID",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void makeFieldTaller(JTextField field) {
        // Enforce the taller height
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, FIELD_HEIGHT));
        field.setMinimumSize(new Dimension(10, FIELD_HEIGHT));
    }

    private void loadTable() {
        model.setRowCount(0);
        if (leaderId.isEmpty()) return;

        Set<String> allowedLecturers = readAllowedLecturersForLeader(leaderId);
        List<Module> modules = ModuleService.getByLeader(leaderId);
        for (Module m : modules) {
            String lecturer = (m.getLecturerId() == null) ? "" : m.getLecturerId().trim();
            if (!lecturer.isEmpty() && !allowedLecturers.contains(lecturer.toUpperCase())) {
                lecturer = lecturer + " (INVALID)";
            }
            model.addRow(new Object[]{
                    m.getModuleId(),
                    m.getModuleName(),
                    m.getModuleCode(),
                    String.valueOf(m.getCreditHours()),
                    lecturer
            });
        }
        clearForm();
    }

    private Set<String> readAllowedLecturersForLeader(String leaderId) {
        Set<String> set = new LinkedHashSet<>();
        List<String> lines = FileManager.readAll(LEADER_LECTURER_FILE);
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;
            String[] p = line.trim().split("\\|", -1);
            if (p.length < 2) continue;
            if (leaderId.equalsIgnoreCase(p[0].trim())) {
                set.add(p[1].trim().toUpperCase());
            }
        }
        return set;
    }

    private void onAdd() {
        try {
            String name = txtModuleName.getText().trim();
            String code = txtModuleCode.getText().trim();
            int credits = parseCredits();
            Module created = ModuleService.createModule(leaderId, name, code, credits);
            JOptionPane.showMessageDialog(this, "Module created: " + created.getModuleId());
            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Create failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        String moduleId = valueAt(r, 0);
        try {
            String name = txtModuleName.getText().trim();
            String code = txtModuleCode.getText().trim();
            int credits = parseCredits();
            ModuleService.updateModule(leaderId, moduleId, name, code, credits);
            JOptionPane.showMessageDialog(this, "Module updated: " + moduleId);
            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Update failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        String moduleId = valueAt(r, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete module " + moduleId + "?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            ModuleService.deleteModule(leaderId, moduleId);
            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int parseCredits() {
        String raw = txtCreditHours.getText().trim();
        if (raw.isEmpty()) throw new IllegalArgumentException("Credit Hours is required.");
        int val = Integer.parseInt(raw);
        if (val <= 0) throw new IllegalArgumentException("Credit Hours must be positive.");
        return val;
    }

    private void clearForm() {
        txtModuleName.setText("");
        txtModuleCode.setText("");
        txtCreditHours.setText("");
        txtLecturerId.setText("");
        table.clearSelection();
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(Theme.MUTED);
        l.setFont(UIUtils.font(12, Font.PLAIN));
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private String valueAt(int row, int col) {
        Object v = model.getValueAt(row, col);
        return v == null ? "" : String.valueOf(v);
    }

    private void styleTableToMatchDarkUI(JTable t) {
        t.setBackground(Theme.BG);
        t.setForeground(Theme.TEXT);
        t.setSelectionBackground(Theme.PRIMARY);
        t.setSelectionForeground(Color.WHITE);
        JTableHeader h = t.getTableHeader();
        h.setBackground(Theme.SIDEBAR);
        h.setForeground(Theme.TEXT);
        DefaultTableCellRenderer hr = new DefaultTableCellRenderer();
        hr.setBackground(Theme.SIDEBAR);
        hr.setForeground(Theme.TEXT);
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setHeaderRenderer(hr);
        }
    }
}