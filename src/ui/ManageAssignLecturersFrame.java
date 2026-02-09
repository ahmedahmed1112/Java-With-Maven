package ui;

import model.LeaderLecturerAssignment;
import model.User;
import service.LeaderLecturerService;
import service.UserService;
import service.LeaderLecturerService.AddResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageAssignLecturersFrame extends JFrame {

    private final LeaderLecturerService service;
    private final UserService userService;

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<UserItem> leaderCombo;
    private JComboBox<UserItem> lecturerCombo;

    private JButton addBtn;
    private JButton deleteBtn;
    private JButton refreshBtn;
    private JButton closeBtn;

    public ManageAssignLecturersFrame() {
        this(new LeaderLecturerService(), new UserService());
    }

    public ManageAssignLecturersFrame(LeaderLecturerService service, UserService userService) {
        this.service = service;
        this.userService = userService;

        setTitle("Manage Leader-Lecturer Assignments");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        initUI();
        loadCombos();
        loadTable();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.setBackground(Theme.BG);
        setContentPane(root);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createTitledBorder("Assign Lecturer to Leader"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        leaderCombo = new JComboBox<>();
        lecturerCombo = new JComboBox<>();

        addBtn = UIUtils.primaryButton("Assign");
        deleteBtn = UIUtils.primaryButton("Delete Selected");
        refreshBtn = UIUtils.primaryButton("Refresh");
        closeBtn = UIUtils.primaryButton("Close");

        c.gridx = 0; c.gridy = 0;
        form.add(UIUtils.muted("Leader:"), c);

        c.gridx = 1; c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        form.add(leaderCombo, c);

        c.gridx = 0; c.gridy = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        form.add(UIUtils.muted("Lecturer:"), c);

        c.gridx = 1; c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        form.add(lecturerCombo, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(addBtn);
        buttons.add(deleteBtn);
        buttons.add(refreshBtn);
        buttons.add(closeBtn);

        c.gridx = 1; c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        form.add(buttons, c);

        root.add(form, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Leader ID", "Leader Name", "Lecturer ID", "Lecturer Name"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Current Assignments"));
        root.add(sp, BorderLayout.CENTER);

        addBtn.addActionListener(e -> onAdd());
        deleteBtn.addActionListener(e -> onDeleteSelected());
        refreshBtn.addActionListener(e -> { loadCombos(); loadTable(); });
        closeBtn.addActionListener(e -> dispose());
    }

    private void loadCombos() {
        leaderCombo.removeAllItems();
        lecturerCombo.removeAllItems();

        List<User> leaders = userService.getUsersByRole("LEADER");
        List<User> lecturers = userService.getUsersByRole("LECTURER");

        for (User u : leaders) {
            leaderCombo.addItem(new UserItem(u.getUserId(), u.getName()));
        }
        for (User u : lecturers) {
            lecturerCombo.addItem(new UserItem(u.getUserId(), u.getName()));
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);

        List<LeaderLecturerAssignment> list = service.getAll();
        for (LeaderLecturerAssignment a : list) {
            String leaderId = safe(a.getLeaderId());
            String lecturerId = safe(a.getLecturerId());

            User leader = userService.getById(leaderId);
            User lecturer = userService.getById(lecturerId);

            String leaderName = (leader == null) ? "(unknown)" : safe(leader.getName());
            String lecturerName = (lecturer == null) ? "(unknown)" : safe(lecturer.getName());

            tableModel.addRow(new Object[]{leaderId, leaderName, lecturerId, lecturerName});
        }
    }

    private void onAdd() {
        UserItem leader = (UserItem) leaderCombo.getSelectedItem();
        UserItem lecturer = (UserItem) lecturerCombo.getSelectedItem();

        if (leader == null || lecturer == null) {
            JOptionPane.showMessageDialog(this, "Please select both a leader and a lecturer.");
            return;
        }

        AddResult result = service.add(leader.id, lecturer.id);

        switch (result) {
            case SUCCESS:
                JOptionPane.showMessageDialog(this, "Assignment added successfully.");
                loadTable();
                break;
            case DUPLICATE:
                JOptionPane.showMessageDialog(this, "This assignment already exists.");
                break;
            case LECTURER_ALREADY_ASSIGNED:
                JOptionPane.showMessageDialog(this, "This lecturer is already assigned to another leader.");
                break;
            case LEADER_MAX_LECTURERS:
                JOptionPane.showMessageDialog(this, "This leader already has 3 lecturers (maximum).");
                break;
            case INVALID_IDS:
                JOptionPane.showMessageDialog(this, "Invalid leader or lecturer ID.");
                break;
            default:
                JOptionPane.showMessageDialog(this, "Failed to add assignment (IO error).");
                break;
        }
    }

    private void onDeleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        String leaderId = safe(tableModel.getValueAt(row, 0));
        String lecturerId = safe(tableModel.getValueAt(row, 2));

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete assignment?\nLeader: " + leaderId + "\nLecturer: " + lecturerId,
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = service.deletePair(leaderId, lecturerId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Assignment deleted.");
            loadTable();
        } else {
            JOptionPane.showMessageDialog(this, "Delete failed (assignment not found).");
        }
    }

    private String safe(Object o) {
        return o == null ? "" : String.valueOf(o).trim();
    }

    private static class UserItem {
        final String id;
        final String name;

        UserItem(String id, String name) {
            this.id = id == null ? "" : id.trim();
            this.name = name == null ? "" : name.trim();
        }

        @Override
        public String toString() {
            if (name.isEmpty()) return id;
            if (id.isEmpty()) return name;
            return name + " (" + id + ")";
        }
    }
}
