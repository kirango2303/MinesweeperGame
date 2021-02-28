import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;

@SuppressWarnings("serial")
public class Game extends JFrame {

    private JButton buttonUndo;
    private JButton buttonRule;
    private JPanel buttonPanel;

    private JLabel status;
    private JTextArea textArea;
    private JPanel statusPanel;
    private GameBoard gameBoard;

    public Game() throws IOException {
        initiate();
    }

    public void initiate() throws IOException {

        // Button panel (on the top of the frame)
        // Left to right: Rule - Reset - Undo
        buttonUndo = new JButton("UNDO MOVE");
        buttonRule = new JButton("VIEW INSTRUCTIONS");
        buttonPanel = new JPanel();

        add(buttonPanel, BorderLayout.NORTH);
        buttonPanel.add(buttonRule, BorderLayout.NORTH);
        buttonPanel.add(buttonUndo, BorderLayout.SOUTH);

        // Status panel (at the bottom of the frame)
        // Number of flags left (left) + enter name of user (right)
        statusPanel = new JPanel();
        status = new JLabel("NUMBER OF FLAGS LEFT:");
        textArea = new JTextArea();
        textArea.setText("Enter Username");

        add(statusPanel, BorderLayout.SOUTH);
        statusPanel.add(status, BorderLayout.NORTH);
        statusPanel.add(textArea, BorderLayout.SOUTH);

        // Game board
        gameBoard = new GameBoard(status, buttonUndo, buttonRule, textArea);
        add(gameBoard);

        // Set up the top level frame in which game components live
        setResizable(false);
        pack();
        setTitle("MINESWEEPER");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * Main method run to start and run the game. IMPORTANT: Do NOT delete! You MUST
     * include this in final submission. 
     * Add method to save the game status + user-name when closing the game 
     * Use EventQueue instead of SwingUtilities (same effect)
     */

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Game prev;
            try {
                prev = new Game();
                // Auto-save the game when closing the application
                prev.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(WindowEvent wEvent) {
                        try {
                            GameBoard.save();
                        } catch (IOException e) {

                        }

                    }
                });
                prev.setVisible(true);

            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}