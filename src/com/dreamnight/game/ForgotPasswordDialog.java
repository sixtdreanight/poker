package com.dreamnight.game;
// 导入必要的包

import java.awt.*; // Swing GUI组件
import java.awt.event.*; // AWT图形和布局
import javax.swing.*; // 事件处理

/**
 * 忘记密码对话框类 提供密码找回功能的对话框界面
 */
public class ForgotPasswordDialog extends JDialog {

    // 界面组件声明
    private JTextField usernameField; // 用户名输入框
    private JTextField emailField; // 邮箱输入框

    // 颜色定义
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // 主色调

    /**
     * 构造函数 - 初始化忘记密码对话框
     *
     * @param parent 父窗口，用于对话框居中显示
     */
    public ForgotPasswordDialog(JFrame parent, String BACKGROUND_IMAGE_PATH) {
        super(parent, "找回密码", true); // 调用父类构造函数，设置标题和模态属性
        initComponents(); // 初始化对话框组件
    }

    /**
     * 初始化对话框的所有组件
     */
    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 设置关闭操仅关闭对话框
        setResizable(false); // 禁止调整对话框大小

        // 创建主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 20)); // 使用边界布局，组件间垂直间距20
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25)); // 设置边距
        mainPanel.setBackground(Color.WHITE); // 设置白色背景

        // 创建标题标签
        JLabel titleLabel = new JLabel("找回密码", JLabel.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 20)); // 设置字体
        titleLabel.setForeground(new Color(44, 62, 80)); // 设置文字颜色

        // 创建表单面板
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15)); // 2行2列网格布局
        formPanel.setOpaque(false); // 设置面板透明

        // 创建用户名标签和输入框
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        usernameField = createTextField();

        // 创建邮箱标签和输入框
        JLabel emailLabel = new JLabel("注册邮箱:");
        emailLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        emailField = createTextField();

        // 将组件添加到表单面板
        formPanel.add(userLabel);
        formPanel.add(usernameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout()); // 使用流式布局
        buttonPanel.setOpaque(false); // 设置面板透明

        // 创建提交和取消按钮
        JButton submitButton = createButton("提交", PRIMARY_COLOR);
        JButton cancelButton = createButton("取消", new Color(149, 165, 166));

        // 为按钮添加事件监听器
        submitButton.addActionListener(e -> handleSubmit()); // 提交按钮点击事件
        cancelButton.addActionListener(e -> dispose()); // 取消按钮点击事件 - 关闭对话框

        // 将按钮添加到按钮面板
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

        // 将各个面板添加到主面板
        mainPanel.add(titleLabel, BorderLayout.NORTH); // 标题在顶部
        mainPanel.add(formPanel, BorderLayout.CENTER); // 表单在中间
        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // 按钮在底部

        // 将主面板添加到对话框
        add(mainPanel);

        // 自动调整对话框大小以适应内容
        pack();
        // 将对话框显示在父窗口中央
        setLocationRelativeTo(getParent());
    }

    /**
     * 创建标准化的文本输入框
     *
     * @return 配置好的文本输入框
     */
    private JTextField createTextField() {
        JTextField field = new JTextField(20); // 设置初始列数为20
        field.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14)); // 设置字体
        // 设置复合边框
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1), // 灰色边框
                BorderFactory.createEmptyBorder(8, 10, 8, 10) // 内边距
        ));
        return field;
    }

    /**
     * 创建标准化的扁平化按钮
     *
     * @param text 按钮文字
     * @param color 按钮背景色
     * @return 配置好的按钮
     */
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 14)); // 设置粗体字体
        button.setForeground(Color.WHITE); // 设置文字颜色为白色
        button.setBackground(color); // 设置背景颜色
        button.setFocusPainted(false); // 取消焦点绘制
        button.setBorderPainted(false); // 取消边框绘制
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 设置手型光标
        button.setPreferredSize(new Dimension(100, 40)); // 设置首选大小

        // 添加鼠标悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker()); // 鼠标进入时变暗
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color); // 鼠标离开时恢复
            }
        });

        return button;
    }

    /**
     * 处理提交按钮点击事件 验证输入并模拟密码找回流程
     */
    private void handleSubmit() {
        // 获取用户输入
        String username = usernameField.getText().trim(); // 用户名
        String email = emailField.getText().trim(); // 邮箱

        // 输入验证 - 检查是否所有字段都已填写
        if (username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写所有字段", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return; // 如果有空字段，终止处理
        }

        // 在实际应用中，这里应该实现真正的密码找回逻辑，例如：
        // 1. 验证用户名和邮箱是否匹配
        // 2. 发送密码重置邮件
        // 3. 或显示安全问题
        // 当前实现为模拟成功消息
        JOptionPane.showMessageDialog(this,
                "密码重置链接已发送到您的邮箱\n请查看邮箱完成密码重置",
                "提交成功",
                JOptionPane.INFORMATION_MESSAGE);

        // 关闭对话框
        dispose();
    }
}
