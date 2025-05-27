import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Spanish21Swing extends JFrame {
    private int playerMoney = 1000;
    private int currentBet = 0;
    private ArrayList<Integer> playerCards = new ArrayList<>();
    private ArrayList<Integer> dealerCards = new ArrayList<>();

    private JLabel moneyLabel = new JLabel("Money: $1000");
    private JTextField betInput = new JTextField(5);
    private JButton betButton = new JButton("Place Bet");
    private JButton hitButton = new JButton("Hit");
    private JButton standButton = new JButton("Stand");
    private JButton restartButton = new JButton("Restart");

    private JTextArea playerArea = new JTextArea(3, 20);
    private JTextArea dealerArea = new JTextArea(3, 20);
    private JLabel resultLabel = new JLabel();

    private Random rand = new Random();

    public Spanish21Swing() {
        setTitle("Spanish 21 (Swing Version)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 350);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(moneyLabel);
        topPanel.add(new JLabel("Bet:"));
        topPanel.add(betInput);
        topPanel.add(betButton);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        playerArea.setEditable(false);
        dealerArea.setEditable(false);
        centerPanel.add(new JScrollPane(new JTextAreaWithLabel("Dealer Cards", dealerArea)));
        centerPanel.add(new JScrollPane(new JTextAreaWithLabel("Player Cards", playerArea)));
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(hitButton);
        bottomPanel.add(standButton);
        bottomPanel.add(restartButton);
        bottomPanel.add(resultLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        restartButton.setEnabled(false);

        betButton.addActionListener(e -> placeBet());
        hitButton.addActionListener(e -> playerHit());
        standButton.addActionListener(e -> playerStand());
        restartButton.addActionListener(e -> resetGame());

        setVisible(true);
    }

    private void placeBet() {
        try {
            currentBet = Integer.parseInt(betInput.getText());
            if (currentBet <= 0 || currentBet > playerMoney) {
                resultLabel.setText("Invalid bet.");
                return;
            }
        } catch (NumberFormatException ex) {
            resultLabel.setText("Enter a valid number.");
            return;
        }

        playerCards.clear();
        dealerCards.clear();
        playerArea.setText("");
        dealerArea.setText("");

        playerCards.add(drawCard());
        playerCards.add(drawCard());
        dealerCards.add(drawCard());
        dealerCards.add(drawCard());

        updateCardDisplays();
        resultLabel.setText("");
        betButton.setEnabled(false);
        hitButton.setEnabled(true);
        standButton.setEnabled(true);
    }

    private void playerHit() {
        playerCards.add(drawCard());
        updateCardDisplays();
        int total = calculateTotal(playerCards);
        if (total > 21) {
            endRound("You busted!", false);
        } else if (total == 21 && playerCards.size() >= 5) {
            endRound("5+ card 21! Bonus!", true);
        }
    }

    private void playerStand() {
        while (calculateTotal(dealerCards) < 17) {
            dealerCards.add(drawCard());
        }
        updateCardDisplays();

        int playerTotal = calculateTotal(playerCards);
        int dealerTotal = calculateTotal(dealerCards);

        if (dealerTotal > 21 || playerTotal > dealerTotal) {
            endRound("You win!", true);
        } else if (dealerTotal == playerTotal) {
            resultLabel.setText("Push. No one wins.");
        } else {
            endRound("Dealer wins.", false);
        }
    }

    private void endRound(String message, boolean playerWins) {
        int bonus = 1;
        int total = calculateTotal(playerCards);

        if (total == 21) {
            if (playerCards.size() == 5) {
                bonus = 2;
                message += " (5-card 21 Bonus)";
            } else if (playerCards.size() == 6) {
                bonus = 3;
                message += " (6-card 21 Bonus)";
            } else if (playerCards.size() >= 7) {
                bonus = 4;
                message += " (7+ card 21 Bonus)";
            }
        }

        if (playerCards.size() == 3 &&
            playerCards.get(0) == 7 &&
            playerCards.get(1) == 7 &&
            playerCards.get(2) == 7 &&
            dealerCards.get(0) == 7) {
            bonus = 10;
            message = "SUITED 7-7-7 with dealer 7! SUPER BONUS!";
        }

        resultLabel.setText(message);

        if (playerWins) {
            playerMoney += currentBet * bonus;
        } else {
            playerMoney -= currentBet;
        }

        moneyLabel.setText("Money: $" + playerMoney);
        betButton.setEnabled(true);
        hitButton.setEnabled(false);
        standButton.setEnabled(false);

        if (playerMoney <= 0) {
            resultLabel.setText("You're out of money. Game over!");
            betButton.setEnabled(false);
            hitButton.setEnabled(false);
            standButton.setEnabled(false);
            restartButton.setEnabled(true);
        }
    }

    private void resetGame() {
        playerMoney = 1000;
        moneyLabel.setText("Money: $1000");
        resultLabel.setText("");
        betInput.setText("");
        playerCards.clear();
        dealerCards.clear();
        playerArea.setText("");
        dealerArea.setText("");
        betButton.setEnabled(true);
        restartButton.setEnabled(false);
    }

    private int drawCard() {
        int card;
        do {
            card = rand.nextInt(13) + 1;
        } while (card == 10); // No 10s in Spanish 21
        return Math.min(card, 10);
    }

    private int calculateTotal(ArrayList<Integer> cards) {
        int total = 0;
        int aces = 0;
        for (int card : cards) {
            if (card == 1) {
                total += 11;
                aces++;
            } else {
                total += card;
            }
        }
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    private void updateCardDisplays() {
        playerArea.setText(cardsToString(playerCards));
        dealerArea.setText(cardsToString(dealerCards));
    }

    private String cardsToString(ArrayList<Integer> cards) {
        StringBuilder sb = new StringBuilder();
        for (int card : cards) {
            if (card == 1) sb.append("A ");
            else sb.append(card).append(" ");
        }
        return sb.toString();
    }

    private static class JTextAreaWithLabel extends JPanel {
        public JTextAreaWithLabel(String label, JTextArea area) {
            setLayout(new BorderLayout());
            add(new JLabel(label), BorderLayout.NORTH);
            add(area, BorderLayout.CENTER);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Spanish21Swing());
    }
}
