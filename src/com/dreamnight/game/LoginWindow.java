package com.dreamnight.game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.util.Map;
import javax.swing.border.Border;

import com.dreamnight.game.EnhancedJSONParser.JSONData;

/**
 * 主登录窗口类 - 半透明效果版本 包含半透明文本框和统一背景图片
 */
public class LoginWindow extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private String currentCaptcha;

    // 颜色方案
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color ERROR_COLOR = new Color(231, 76, 60);

    // 背景图片路径
    private final String BACKGROUND_IMAGE_PATH = "C:\\Users\\DreamNight\\Documents\\java\\doudizhu\\poker\\img\\IMG_8582.jpg";

    public LoginWindow() {
        initComponents();
        generateCaptcha();
    }

    private void initComponents() {
        setTitle("登录登录喵~");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 主面板 - 使用统一背景图片
        JPanel mainPanel = new BackgroundPanel(BACKGROUND_IMAGE_PATH);
        mainPanel.setLayout(new BorderLayout(0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // 标题
        JLabel titleLabel = new JLabel("欢迎登录喵~", JLabel.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // 表单面板
        JPanel formPanel = createFormPanel();

        // 按钮面板
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 12, 12));
        panel.setOpaque(false);

        // 用户名
        JLabel userLabel = createLabel("用户名:");
        usernameField = createEnhancedTextField();

        // 密码
        JLabel passLabel = createLabel("密码:");
        passwordField = createEnhancedPasswordField();

        // 验证码
        JLabel captchaTextLabel = createLabel("验证码:");
        JPanel captchaPanel = createCaptchaPanel();

        panel.add(userLabel);
        panel.add(usernameField);
        panel.add(passLabel);
        panel.add(passwordField);
        panel.add(captchaTextLabel);
        panel.add(captchaPanel);

        return panel;
    }

    private JPanel createCaptchaPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false); // 确保面板透明

        captchaField = createEnhancedTextField();
        captchaLabel = new JLabel();
        captchaLabel.setFont(new Font("Arial", Font.BOLD, 18));
        captchaLabel.setForeground(Color.WHITE);
        captchaLabel.setBackground(new Color(255, 255, 255, 150)); // 半透明白色背景
        captchaLabel.setOpaque(true); // 验证码标签需要不透明以显示背景色
        captchaLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 200), 1));
        captchaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        captchaLabel.setPreferredSize(new Dimension(70, 30));
        captchaLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        captchaLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                generateCaptcha();
            }
        });

        panel.add(captchaField, BorderLayout.CENTER);
        panel.add(captchaLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 8, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton registerButton = createButton("注册", new Color(149, 165, 166));
        JButton forgotButton = createButton("忘记密码", SECONDARY_COLOR);
        JButton loginButton = createButton("登录", PRIMARY_COLOR);

        registerButton.addActionListener(e -> handleRegistration());
        forgotButton.addActionListener(e -> handleForgotPassword());
        loginButton.addActionListener(e -> attemptLogin());

        getRootPane().setDefaultButton(loginButton);

        panel.add(registerButton);
        panel.add(forgotButton);
        panel.add(loginButton);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);

        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        label.setForeground(Color.WHITE);

        return label;
    }

    /**
     * 创建增强的半透明文本框 - 带焦点效果
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
                // 空实现，因为我们自定义了边框
            }

            {
                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        isFocused = true;
                        startFocusAnimation();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
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

        field.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        return field;
    }

    /**
     * 创建增强的半透明密码框 - 带焦点效果
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
                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        isFocused = true;
                        startFocusAnimation();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
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

        field.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        return field;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 40));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void generateCaptcha() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            captcha.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        currentCaptcha = captcha.toString();
        captchaLabel.setText(currentCaptcha);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String captcha = captchaField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || captcha.isEmpty()) {
            showMessage("请填写所有字段", ERROR_COLOR);
            return;
        }

        if (!captcha.equalsIgnoreCase(currentCaptcha)) {
            showMessage("验证码错误", ERROR_COLOR);
            generateCaptcha();
            captchaField.setText("");
            return;
        }

        if (validateCredentials(username, password)) {
            showMessage("登录成功喵！", SUCCESS_COLOR);
            Timer timer = new Timer(1000, e -> {
                dispose();
                new GameJFrame().setVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            showMessage("用户名或密码错误", ERROR_COLOR);
            generateCaptcha();
            passwordField.setText("");
            captchaField.setText("");
        }
    }

    private boolean validateCredentials(String username, String password) {
        try {
            // 使用修复后的JSON解析器
            JSONData userData = EnhancedJSONParser.parseJSONFile("password.json");

            System.out.println("验证用户: " + username);
            System.out.println("现有用户: " + userData.keySet());

            // 检查用户名是否存在
            if (userData.has(username)) {
                String storedPassword = userData.getString(username); // 现在可以使用getString方法了
                boolean isValid = storedPassword.equals(password);
                System.out.println("密码验证结果: " + isValid);
                return isValid;
            } else {
                System.out.println("用户不存在: " + username);
                return false;
            }

        } catch (Exception e) {
            System.err.println("密码验证过程中出现异常: " + e.getMessage());
            e.printStackTrace();
            showMessage("系统错误: " + e.getMessage(), ERROR_COLOR);
            return false;
        }
    }

    private void handleForgotPassword() {
        ForgotPasswordDialog dialog = new ForgotPasswordDialog(this, BACKGROUND_IMAGE_PATH);
        dialog.setVisible(true);
    }

    private void handleRegistration() {
        RegisterDialog dialog = new RegisterDialog(this, BACKGROUND_IMAGE_PATH);
        dialog.setVisible(true);
    }

    private void showMessage(String message, Color color) {
        JOptionPane.showMessageDialog(this, message, "提示",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 背景面板类 - 修复版本 只对背景图片添加遮罩，不影响前景组件
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginWindow().setVisible(true);
        });
    }
}
