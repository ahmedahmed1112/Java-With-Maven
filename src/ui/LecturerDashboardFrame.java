package ui;

import javax.swing.*;
import java.awt.*;

public class LecturerDashboardFrame extends JFrame {

    public LecturerDashboardFrame() {
        setTitle("Lecturer Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        setContentPane(root);

        JLabel label = new JLabel("Lecturer Dashboard (Placeholder)", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        root.add(label, BorderLayout.CENTER);
    }
}
