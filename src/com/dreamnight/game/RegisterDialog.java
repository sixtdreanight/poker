package com.dreamnight.game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.Border;

import com.dreamnight.game.EnhancedJSONParser.JSONData;

/**
 * 用户注册对话框 - 半透明效果版本
 */
public class RegisterDialog extends JDialog {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JButton registerButton;
    private JButton cancelButton;

    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color ERROR_COLOR = new Color(231, 76, 60);

    private JFrame parentFrame;
    private String backgroundImagePath;

    public RegisterDialog(JFrame parent, String backgroundImagePath) {
        super(parent, "注册注册喵", true);
        this.parentFrame = parent;
        this.backgroundImagePath = backgroundImagePath;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // 创建主面板 - 使用统一背景
        JPanel mainPanel = new BackgroundPanel(backgroundImagePath);
        mainPanel.setLayout(new BorderLayout(0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // 创建标题
        JLabel titleLabel = new JLabel("创建新账户喵~", JLabel.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 创建表单面板
        JPanel formPanel = createFormPanel();

        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(parentFrame);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 12, 12));
        panel.setOpaque(false);

        // 用户名
        JLabel userLabel = createFormLabel("用户名:");
        usernameField = createEnhancedTextField();

        // 密码
        JLabel passLabel = createFormLabel("密码:");
        passwordField = createEnhancedPasswordField();

        // 确认密码
        JLabel confirmPassLabel = createFormLabel("确认密码:");
        confirmPasswordField = createEnhancedPasswordField();

        // 邮箱
        JLabel emailLabel = createFormLabel("邮箱:");
        emailField = createEnhancedTextField();

        panel.add(userLabel);
        panel.add(usernameField);
        panel.add(passLabel);
        panel.add(passwordField);
        panel.add(confirmPassLabel);
        panel.add(confirmPasswordField);
        panel.add(emailLabel);
        panel.add(emailField);

        return panel;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setOpaque(false); // 确保标签不透明为false
        return label;
    }

    /**
     * 创建增强的半透明文本框
     */
    private JTextField createEnhancedTextField() {
        JTextField field = new JTextField(16) {
            private boolean isFocused = false;
            private float animationProgress = 0.0f;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                // 绘制半透明背景
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2d.setColor(new Color(255, 255, 255, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // 如果获得焦点，绘制焦点边框
                if (isFocused) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animationProgress));
                    g2d.setColor(new Color(52, 152, 219, 200));
                    g2d.setStroke(new BasicStroke(2.0f));
                    g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                } else {
                    // 普通状态的边框
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2d.setColor(new Color(189, 195, 199, 150));
                    g2d.setStroke(new BasicStroke(1.0f));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }

                // 恢复不透明度绘制文本
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                super.paintComponent(g2d);
                g2d.dispose();
            }

            @Override
            public void setBorder(Border border) {
                // 空实现
            }

            {
                addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        isFocused = true;
                        startFocusAnimation();
                    }

                    public void focusLost(java.awt.event.FocusEvent evt) {
                        isFocused = false;
                        startFocusAnimation();
                    }
                });
            }

            private void startFocusAnimation() {
                Timer timer = new Timer(16, null);
                timer.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (isFocused) {
                            animationProgress = Math.min(1.0f, animationProgress + 0.1f);
                        } else {
                            animationProgress = Math.max(0.0f, animationProgress - 0.1f);
                        }

                        repaint();

                        if ((isFocused && animationProgress >= 1.0f)
                                || (!isFocused && animationProgress <= 0.0f)) {
                            ((Timer) e.getSource()).stop();
                        }
                    }
                });
                timer.start();
            }
        };

        field.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        return field;
    }

    /**
     * 创建增强的半透明密码框
     */
    private JPasswordField createEnhancedPasswordField() {
        JPasswordField field = new JPasswordField(16) {
            private boolean isFocused = false;
            private float animationProgress = 0.0f;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                // 绘制半透明背景
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2d.setColor(new Color(255, 255, 255, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // 如果获得焦点，绘制焦点边框
                if (isFocused) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animationProgress));
                    g2d.setColor(new Color(52, 152, 219, 200));
                    g2d.setStroke(new BasicStroke(2.0f));
                    g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                } else {
                    // 普通状态的边框
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2d.setColor(new Color(189, 195, 199, 150));
                    g2d.setStroke(new BasicStroke(1.0f));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }

                // 恢复不透明度绘制文本
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                super.paintComponent(g2d);
                g2d.dispose();
            }

            @Override
            public void setBorder(Border border) {
                // 空实现
            }

            {
                addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        isFocused = true;
                        startFocusAnimation();
                    }

                    public void focusLost(java.awt.event.FocusEvent evt) {
                        isFocused = false;
                        startFocusAnimation();
                    }
                });
            }

            private void startFocusAnimation() {
                Timer timer = new Timer(16, null);
                timer.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (isFocused) {
                            animationProgress = Math.min(1.0f, animationProgress + 0.1f);
                        } else {
                            animationProgress = Math.max(0.0f, animationProgress - 0.1f);
                        }

                        repaint();

                        if ((isFocused && animationProgress >= 1.0f)
                                || (!isFocused && animationProgress <= 0.0f)) {
                            ((Timer) e.getSource()).stop();
                        }
                    }
                });
                timer.start();
            }
        };

        field.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        return field;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        cancelButton = createButton("取消", new Color(149, 165, 166));
        registerButton = createButton("注册", PRIMARY_COLOR);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptRegistration();
            }
        });

        panel.add(cancelButton);
        panel.add(registerButton);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 38));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void attemptRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();

        String validationError = validateInput(username, password, confirmPassword, email);
        if (validationError != null) {
            showErrorMessage(validationError);
            return;
        }

        if (isUsernameExists(username)) {
            showErrorMessage("用户名已存在，请选择其他用户名");
            usernameField.requestFocus();
            return;
        }

        if (registerUser(username, password, email)) {
            showSuccessMessage("注册成功！您现在可以登录了喵~");
            dispose();
        } else {
            showErrorMessage("注册失败，请稍后重试");
        }
    }

    private String validateInput(String username, String password, String confirmPassword, String email) {
        if (username.isEmpty()) {
            return "用户名不能为空";
        }
        if (username.length() < 3 || username.length() > 20) {
            return "用户名长度必须在3-20个字符之间";
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "用户名只能包含字母、数字和下划线";
        }

        if (password.isEmpty()) {
            return "密码不能为空";
        }
        if (password.length() < 6) {
            return "密码长度不能少于6个字符";
        }
        if (password.length() > 30) {
            return "密码长度不能超过30个字符";
        }

        if (!password.equals(confirmPassword)) {
            return "两次输入的密码不一致";
        }

        if (!email.isEmpty() && !isValidEmail(email)) {
            return "邮箱格式不正确";
        }

        return null;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isUsernameExists(String username) {
        try {
            JSONData userData = EnhancedJSONParser.parseJSONFile("password.json");
            boolean exists = userData.has(username);
            System.out.println("检查用户名是否存在 '" + username + "': " + exists);
            return exists;
        } catch (Exception e) {
            System.err.println("检查用户名时出错: " + e.getMessage());
            // 如果文件不存在或其他错误，视为用户名不存在
            return false;
        }
    }

    private boolean registerUser(String username, String password, String email) {
        try {
            // 读取现有的用户数据
            JSONData userData;
            try {
                userData = EnhancedJSONParser.parseJSONFile("password.json");
            } catch (Exception e) {
                // 如果文件不存在，创建新的Map
                userData = new JSONData(new HashMap<>());
            }

            // 创建新的Map来存储所有用户数据
            Map<String, String> userMap = new HashMap<>();

            // 将现有数据复制到新Map
            for (String key : userData.keySet()) {
                userMap.put(key, userData.getString(key));
            }

            // 添加新用户
            userMap.put(username, password);

            // 将用户数据写回文件
            writeUserDataToFile(userMap);

            // 如果提供了邮箱，可以保存到另一个文件（可选功能）
            if (!email.isEmpty()) {
                saveEmailToFile(username, email);
            }

            return true;

        } catch (Exception e) {
            System.err.println("注册用户失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void writeUserDataToFile(Map<String, String> userMap) {
        try (FileWriter file = new FileWriter("password.json", StandardCharsets.UTF_8)) {
            file.write("{\n");

            int count = 0;
            for (Map.Entry<String, String> entry : userMap.entrySet()) {
                String escapedKey = escapeJsonString(entry.getKey());
                String escapedValue = escapeJsonString(entry.getValue());

                String line = "  \"" + escapedKey + "\": \"" + escapedValue + "\"";
                if (count < userMap.size() - 1) {
                    line += ",";
                }
                file.write(line + "\n");
                count++;
            }

            file.write("}");
            file.flush();

        } catch (IOException e) {
            throw new RuntimeException("写入用户数据文件失败: " + e.getMessage(), e);
        }
    }

    private String escapeJsonString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void saveEmailToFile(String username, String email) {
        try {
            Map<String, String> emailMap;
            try {
                emailMap = (Map<String, String>) EnhancedJSONParser.parseJSONFile("user_emails.json");
            } catch (Exception e) {
                emailMap = new HashMap<>();
            }

            emailMap.put(username, email);

            try (FileWriter file = new FileWriter("user_emails.json", StandardCharsets.UTF_8)) {
                file.write("{\n");

                int count = 0;
                for (Map.Entry<String, String> entry : emailMap.entrySet()) {
                    String line = "  \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"";
                    if (count < emailMap.size() - 1) {
                        line += ",";
                    }
                    file.write(line + "\n");
                    count++;
                }

                file.write("}");
                file.flush();
            }

        } catch (Exception e) {
            System.err.println("保存用户邮箱失败: " + e.getMessage());
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "注册错误",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "注册成功",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * 背景面板类
     */
    class BackgroundPanel extends JPanel {

        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                backgroundImage = null;
                System.err.println("无法加载背景图片: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // 创建临时的缓冲图像来添加遮罩效果
                Image bufferImage = createImage(getWidth(), getHeight());
                Graphics2D bufferGraphics = (Graphics2D) bufferImage.getGraphics();

                // 在缓冲图像上绘制背景图片
                bufferGraphics.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                // 在缓冲图像上添加半透明遮罩
                bufferGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                bufferGraphics.setColor(Color.BLACK);
                bufferGraphics.fillRect(0, 0, getWidth(), getHeight());

                // 将处理后的图像绘制到实际面板
                g.drawImage(bufferImage, 0, 0, this);

                bufferGraphics.dispose();
            } else {
                // 备用渐变背景
                Graphics2D g2d = (Graphics2D) g;
                Color BACKGROUND_COLOR = new Color(44, 62, 80);
                GradientPaint gradient = new GradientPaint(0, 0, BACKGROUND_COLOR,
                        getWidth(), getHeight(), new Color(189, 195, 199));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(480, 420);
        }
    }
}
