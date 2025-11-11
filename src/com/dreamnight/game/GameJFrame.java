package com.dreamnight.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * 扑克游戏图形化界面 - 出牌区版本
 */
public class GameJFrame extends JFrame {

    private HashMap<Integer, String> pokerHands;
    private ArrayList<Integer> cardList;

    private TreeSet<Integer> player1;
    private TreeSet<Integer> player2;
    private TreeSet<Integer> player3;
    private TreeSet<Integer> dipai;

    // 出牌区
    private TreeSet<Integer> playArea;
    private int currentPlayer; // 1, 2, 3 分别代表三个玩家
    private JLabel currentPlayerLabel;

    private JPanel mainPanel;
    private JPanel player1Panel;
    private JPanel player2Panel;
    private JPanel player3Panel;
    private JPanel dipaiPanel;
    private JPanel playAreaPanel;
    private JButton restartButton;
    private JButton exitButton;
    private JButton playCardsButton;
    private JButton passButton;

    // 颜色方案 - 使用更柔和的颜色
    private final Color PLAYER1_COLOR = new Color(180, 200, 220, 200);  // 柔和的蓝色
    private final Color PLAYER2_COLOR = new Color(200, 220, 180, 200);  // 柔和的绿色
    private final Color PLAYER3_COLOR = new Color(220, 200, 180, 200);  // 柔和的橙色
    private final Color DIPAI_COLOR = new Color(200, 180, 200, 200);    // 柔和的紫色
    private final Color PLAY_AREA_COLOR = new Color(220, 220, 220, 180); // 柔和的灰色

    public GameJFrame() {
        initGameData();
        initComponents();
        displayCards();
        updateCurrentPlayerDisplay();
    }

    private void initGameData() {
        pokerHands = new HashMap<>();
        cardList = new ArrayList<>();
        playArea = new TreeSet<>();
        currentPlayer = 1; // 从玩家1开始

        String[] colors = {"♦", "♥", "♣", "♠"};
        String[] numbers = {"3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2"};

        int index = 1;
        for (String number : numbers) {
            for (String color : colors) {
                pokerHands.put(index, color + number);
                cardList.add(index);
                index++;
            }
        }

        cardList.add(index);
        pokerHands.put(index++, "Small Joker");
        cardList.add(index);
        pokerHands.put(index, "Big Joker");

        Collections.shuffle(cardList);

        player1 = new TreeSet<>();
        player2 = new TreeSet<>();
        player3 = new TreeSet<>();
        dipai = new TreeSet<>();

        for (int i = 0; i < cardList.size(); i++) {
            int pokerIndex = cardList.get(i);
            if (i <= 2) {
                dipai.add(pokerIndex);
            } else if (i % 3 == 0) {
                player1.add(pokerIndex);
            } else if (i % 3 == 1) {
                player2.add(pokerIndex);
            } else {
                player3.add(pokerIndex);
            }
        }
    }

    private void initComponents() {
        setTitle("梦夜的小游戏喵~");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900); // 固定窗口大小
        setLocationRelativeTo(null); // 居中显示
        setResizable(true);

        // 创建主面板
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240)); // 柔和的灰色背景

        // 创建玩家面板和出牌区
        createPlayerPanels();
        createPlayArea();
        createControlPanel();

        add(mainPanel);
    }

    private void createPlayerPanels() {
        JPanel playersPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        playersPanel.setOpaque(false);

        player1Panel = createPlayerPanel("Dream", PLAYER1_COLOR, player1);
        player2Panel = createPlayerPanel("Night", PLAYER2_COLOR, player2);
        player3Panel = createPlayerPanel("ST", PLAYER3_COLOR, player3);
        dipaiPanel = createPlayerPanel("底牌", DIPAI_COLOR, dipai);

        playersPanel.add(player1Panel);
        playersPanel.add(player2Panel);
        playersPanel.add(player3Panel);
        playersPanel.add(dipaiPanel);

        mainPanel.add(playersPanel, BorderLayout.CENTER);
    }

    private JPanel createPlayerPanel(String playerName, Color backgroundColor, TreeSet<Integer> cards) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 创建玩家名称标签
        JLabel nameLabel = new JLabel(playerName, JLabel.CENTER);
        nameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        nameLabel.setForeground(new Color(60, 60, 60));

        // 创建手牌面板
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 将组件添加到玩家面板
        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(cardsPanel), BorderLayout.CENTER);

        return panel;
    }

    private void createPlayArea() {
        JPanel playAreaContainer = new JPanel(new BorderLayout(5, 5));
        playAreaContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        playAreaContainer.setOpaque(false);

        // 当前玩家显示
        currentPlayerLabel = new JLabel("当前出牌玩家: Dream", JLabel.CENTER);
        currentPlayerLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        currentPlayerLabel.setForeground(new Color(80, 80, 80));
        currentPlayerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 出牌区面板
        playAreaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        playAreaPanel.setBackground(PLAY_AREA_COLOR);
        playAreaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("出牌区"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        playAreaPanel.setPreferredSize(new Dimension(0, 120));

        playAreaContainer.add(currentPlayerLabel, BorderLayout.NORTH);
        playAreaContainer.add(playAreaPanel, BorderLayout.CENTER);

        mainPanel.add(playAreaContainer, BorderLayout.SOUTH);
    }

    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(new Color(220, 220, 220));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 创建按钮
        playCardsButton = createButton("出牌", new Color(100, 150, 200));
        passButton = createButton("不出", new Color(200, 150, 100));
        restartButton = createButton("重新发牌", new Color(150, 180, 150));
        exitButton = createButton("退出游戏", new Color(200, 150, 150));

        playCardsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSelectedCards();
            }
        });

        passButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passTurn();
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });

        controlPanel.add(playCardsButton);
        controlPanel.add(passButton);
        controlPanel.add(restartButton);
        controlPanel.add(exitButton);

        mainPanel.add(controlPanel, BorderLayout.NORTH);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 添加鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void displayCards() {
        displayPlayerCards(player1Panel, player1, "Dream");
        displayPlayerCards(player2Panel, player2, "Night");
        displayPlayerCards(player3Panel, player3, "ST");
        displayPlayerCards(dipaiPanel, dipai, "底牌");
        updatePlayArea();
    }

    private void displayPlayerCards(JPanel panel, TreeSet<Integer> cards, String playerName) {
        JScrollPane scrollPane = (JScrollPane) panel.getComponent(1);
        JPanel cardsPanel = (JPanel) scrollPane.getViewport().getView();
        cardsPanel.removeAll();

        for (Integer cardIndex : cards) {
            String cardText = pokerHands.get(cardIndex);
            JPanel cardPanel = createCardPanel(cardText, cardIndex);
            cardsPanel.add(cardPanel);
        }

        JLabel nameLabel = (JLabel) panel.getComponent(0);
        nameLabel.setText(playerName + " (" + cards.size() + "张)");

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createCardPanel(String cardText, Integer cardIndex) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 3, 5, 3)
        ));
        cardPanel.setPreferredSize(new Dimension(60, 90));

        // 解析扑克牌信息
        boolean isJoker = cardText.contains("Joker");
        String suit = "";
        String value = "";
        Color suitColor = Color.BLACK;

        if (!isJoker) {
            suit = cardText.substring(0, 1);
            value = cardText.substring(1);

            if (suit.equals("♥") || suit.equals("♦")) {
                suitColor = new Color(200, 0, 0); // 暗红色
            }
        } else {
            value = cardText;
            suitColor = new Color(200, 0, 0); // 暗红色
        }

        // 创建数值标签
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        valueLabel.setForeground(Color.BLACK);

        // 创建花色标签
        JLabel suitLabel = new JLabel(suit, JLabel.CENTER);
        suitLabel.setFont(new Font("SimSun", Font.PLAIN, 14));
        suitLabel.setForeground(suitColor);

        cardPanel.add(valueLabel, BorderLayout.NORTH);
        cardPanel.add(suitLabel, BorderLayout.CENTER);

        if (isJoker) {
            JLabel jokerLabel = new JLabel("王", JLabel.CENTER);
            jokerLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            jokerLabel.setForeground(suitColor);
            cardPanel.add(jokerLabel, BorderLayout.CENTER);
        }

        // 添加选择功能（只有当前玩家可以选牌）
        cardPanel.putClientProperty("cardIndex", cardIndex);
        cardPanel.putClientProperty("selected", false);

        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // 只有当前玩家的牌可以选择
                TreeSet<Integer> currentPlayerCards = getCurrentPlayerCards();
                if (currentPlayerCards.contains(cardIndex)) {
                    boolean selected = (Boolean) cardPanel.getClientProperty("selected");
                    cardPanel.putClientProperty("selected", !selected);

                    if (!selected) {
                        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.BLUE, 2),
                                BorderFactory.createEmptyBorder(4, 2, 4, 2)
                        ));
                    } else {
                        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.GRAY, 1),
                                BorderFactory.createEmptyBorder(5, 3, 5, 3)
                        ));
                    }
                }
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (getCurrentPlayerCards().contains(cardIndex)) {
                    cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }
        });

        return cardPanel;
    }

    private TreeSet<Integer> getCurrentPlayerCards() {
        switch (currentPlayer) {
            case 1:
                return player1;
            case 2:
                return player2;
            case 3:
                return player3;
            default:
                return player1;
        }
    }

    private void playSelectedCards() {
        TreeSet<Integer> currentCards = getCurrentPlayerCards();
        TreeSet<Integer> selectedCards = new TreeSet<>();

        // 获取当前玩家面板中的所有卡牌组件
        JPanel currentPlayerPanel = getCurrentPlayerPanel();
        JScrollPane scrollPane = (JScrollPane) currentPlayerPanel.getComponent(1);
        JPanel cardsPanel = (JPanel) scrollPane.getViewport().getView();

        for (Component comp : cardsPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel cardPanel = (JPanel) comp;
                Boolean selected = (Boolean) cardPanel.getClientProperty("selected");
                if (selected != null && selected) {
                    Integer cardIndex = (Integer) cardPanel.getClientProperty("cardIndex");
                    selectedCards.add(cardIndex);
                }
            }
        }

        if (selectedCards.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择要出的牌喵~", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 从当前玩家手牌中移除选中的牌，并添加到出牌区
        currentCards.removeAll(selectedCards);
        playArea.clear();
        playArea.addAll(selectedCards);

        // 更新显示
        displayCards();
        nextPlayer();
    }

    private void passTurn() {
        playArea.clear();
        updatePlayArea();
        nextPlayer();
        JOptionPane.showMessageDialog(this, getCurrentPlayerName() + "选择不出牌", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void nextPlayer() {
        currentPlayer = currentPlayer % 3 + 1; // 循环切换玩家
        updateCurrentPlayerDisplay();
        updatePlayerPanelsVisibility();
    }

    private void updateCurrentPlayerDisplay() {
        String playerName = getCurrentPlayerName();
        currentPlayerLabel.setText("当前出牌玩家: " + playerName);
    }

    private String getCurrentPlayerName() {
        switch (currentPlayer) {
            case 1:
                return "Dream";
            case 2:
                return "Night";
            case 3:
                return "ST";
            default:
                return "Dream";
        }
    }

    private JPanel getCurrentPlayerPanel() {
        switch (currentPlayer) {
            case 1:
                return player1Panel;
            case 2:
                return player2Panel;
            case 3:
                return player3Panel;
            default:
                return player1Panel;
        }
    }

    private void updatePlayerPanelsVisibility() {
        // 在实际游戏中，这里应该隐藏其他玩家的手牌
        // 但为了演示，我们只是改变边框颜色来指示当前玩家
        setPanelBorder(player1Panel, currentPlayer == 1 ? Color.BLUE : Color.GRAY);
        setPanelBorder(player2Panel, currentPlayer == 2 ? Color.BLUE : Color.GRAY);
        setPanelBorder(player3Panel, currentPlayer == 3 ? Color.BLUE : Color.GRAY);
    }

    private void setPanelBorder(JPanel panel, Color color) {
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(9, 9, 9, 9)
        ));
    }

    private void updatePlayArea() {
        playAreaPanel.removeAll();

        for (Integer cardIndex : playArea) {
            String cardText = pokerHands.get(cardIndex);
            JPanel cardPanel = createCardPanel(cardText, cardIndex);
            // 移除出牌区卡牌的点击事件
            for (MouseListener listener : cardPanel.getMouseListeners()) {
                cardPanel.removeMouseListener(listener);
            }
            playAreaPanel.add(cardPanel);
        }

        if (playArea.isEmpty()) {
            JLabel emptyLabel = new JLabel("暂无出牌", JLabel.CENTER);
            emptyLabel.setFont(new Font("Microsoft YaHei", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            playAreaPanel.add(emptyLabel);
        }

        playAreaPanel.revalidate();
        playAreaPanel.repaint();
    }

    private void restartGame() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "确定重新开始游戏吗？",
                "重新开始",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            initGameData();
            playArea.clear();
            displayCards();
            updateCurrentPlayerDisplay();
            updatePlayerPanelsVisibility();

            JOptionPane.showMessageDialog(
                    this,
                    "游戏已重新开始喵！",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void exitGame() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "确定要退出游戏吗？",
                "退出游戏",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    "谢谢游玩喵~",
                    "再见",
                    JOptionPane.INFORMATION_MESSAGE
            );
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameJFrame().setVisible(true);
            }
        });
    }
}
