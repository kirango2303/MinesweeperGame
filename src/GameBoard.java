import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Stack;

@SuppressWarnings("serial")
public class GameBoard extends JPanel implements ActionListener {

    // Fields: game buttons, labels and status
    private static JLabel status;
    private static JButton buttonUndo;
    private static JButton buttonRule;
    private static JTextArea textArea;

    // Game constants
    private static int size = 8; // the number of rows & columns

    private final int dimension = 60; // the width and height of each cell

    private final int numBomb = 10;
    private final int boardWidth = 483;
    private final int boardHeight = 483;

    // Number of flags left (if the player remove flag this number has to increase)
    private int numberFlag;

    // 2D array - used to initialize the game board
    private static CellHandling[][] gameBoard;

    // Map used to store the image and their meanings
    private java.util.Map<String, Image> images;

    // Fields related to game status
    private boolean playing;

    // File to store the state of the game + string characters to split information
    private static String fileToLoad = "SaveGameStatus.txt";
    private static String s1 = "@";
    private static String s2 = "-";

    /**
     * Use collection (stack) to keep track of moves Allows user to undo moves 
     * When a bomb is clicked, the stack is cleared (cannot undo anymore)
     **/
    private Stack<Integer> moves = new Stack<Integer>();

    // Constructor
    public GameBoard(JLabel status, JButton buttonUndo,
            JButton buttonRule, JTextArea textArea) throws IOException {
        GameBoard.status = status;
        GameBoard.buttonUndo = buttonUndo;
        GameBoard.buttonRule = buttonRule;
        GameBoard.textArea = textArea;

        GameBoard.buttonRule.addActionListener(this);
        GameBoard.buttonUndo.addActionListener(this);
        board();

    }

    /**
     * Message pop-up to show the instructions to players. Users can return to the
     * instructions in the middle of the game (Instruction JButton)
     */
    private void instruction() {
        JOptionPane.showMessageDialog(null,
                "INSTRUCTIONS: \n" + "\n" + "Welcome back to your favorite childhood game! \n"
                        + "This is the easy version of Minesweeper with the 8x8 board "
                        + "containing 10 bombs. \n" + "\n"
                        + "Your role is to discover all cells which do not contain bombs"
                        + "using hints from the number cells \n" 
                        + "indicating the number of bombs around. \n"
                        + "If you click a cell with no surrounding bomb, the entire region "
                        + "of empty cells around will be uncovered. \n"
                        + "You win by uncovering all the number cells and empty cells! \n"
                        + "You lose if you click a bomb cell! \n" + "\n" + "GAME FEATURES: \n"
                        + "- Flag or unflag with right click (10 flags total) \n"
                        + "- Autosave function (you can close the game anytime and "
                        + "continue later) \n"
                        + "- Enable username (Default name: CIS-LOVER) \n"
                        + "- Option to resume most recent game status or start new game \n"
                        + "- Start a new game by clicking anywhere on the board \n" + "\n Have "
                        + "fun playing!");
    }

    // Set-up the initial state of the Game
    private void setUp() {

        playing = true;
        numberFlag = numBomb;

        // Set up the game board
        gameBoard = new CellHandling[size][size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                gameBoard[x][y] = new Empty();
            }
        }

        status.setText("NUMBER OF FLAGS LEFT: " + Integer.toString(numberFlag));

        int i = 0;

        // Set up the cells randomly
        while (i < numBomb) {
            Random random = new Random();
            int x = (int) (random.nextInt(7 - 0 + 1) + 0);
            int y = (int) (random.nextInt(7 - 0 + 1) + 0);

            // Locate 10 bombs at random places
            if (gameBoard[x][y].getCellType() != CellType.Bomb) {
                gameBoard[x][y] = new Bomb();

                // Set up the number for cells that are next to bomb/ bombs
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        if ((m != 0 || n != 0) && x + m < size &&
                                y + n < size && x + m >= 0 && y + n >= 0) {
                            CellType type = gameBoard[x + m][y + n].getCellType();
                            if (type != CellType.Bomb) {
                                // case 1: if cell is not yet type NextToBomb -> change type
                                if (type != CellType.NextToBomb) {
                                    NextToBomb num = new NextToBomb();
                                    num.countAdjacentBomb();
                                    gameBoard[x + m][y + n] = num;
                                } else {
                                    // case 2: if cell is type NextToBomb, update the number
                                    gameBoard[x + m][y + n].countAdjacentBomb();
                                }
                            }

                        }
                    }
                }
                i++;

            }

        }
    }

    /**
     * Draws the game board and set up the game logic. Insert the images into their
     * right places in the 2-D array
     * 
     * DRAW: When user clicks a cell, draw the image of the corresponding cell type
     * 
     * LOSE: 
     * 1. lose when click a bomb 
     * 2. change game status (boolean playing) 
     * 3.reveal all bombs (BombImage) 
     * 4. check flagged cells: if incorrectly flagged then change to FlaggedButWrongImage 
     * 5. For cells that are not yet revealed then use CoveredImage 
     * 6. Disable undo button 
     * 7. Update losing status
     * 
     * WIN: 
     * 1. All cells except bomb cells are revealed 
     * 2. Update winning status
     * 
     * UNDO DISABLED: 
     * 1. When a bomb is clicked: clear Stack, cannot undo 
     * 2. When all moves are undone: no more move left in Stack, cannot undo
     */

    @Override
    public void paintComponent(Graphics g) {

        int toBeRevealed = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                CellHandling where = gameBoard[i][j];
                String img = where.getImageMeaning();

                // CASE 1: LOSE
                if (playing && where.getCellType() == CellType.Bomb && !where.isCoveredCell()) {
                    playing = false;
                }
                // DRAWING
                if (!playing) {
                    // CASE 2: WIN
                    if (where.getCellType() == CellType.Bomb && !where.isFlaggedCell()) {
                        where.reveal();
                        img = ImageName.BombImage.toString();
                    } else if (where.isCoveredCell() && where.getCellType() == CellType.Bomb
                            && where.isFlaggedCell()) {
                        img = ImageName.FlaggedImage.toString();
                    } else if (where.isCoveredCell() && where.getCellType() != CellType.Bomb 
                            && where.isFlaggedCell()) {
                        img = ImageName.FlaggedButWrongImage.toString();
                    } else if (where.isCoveredCell()) {
                        img = ImageName.CoveredImage.toString();
                    }

                } else {
                    if (where.isFlaggedCell()) {
                        img = ImageName.FlaggedImage.toString();
                    } else if (where.isCoveredCell()) {
                        img = ImageName.CoveredImage.toString();
                        toBeRevealed++;
                    }

                }

                g.drawImage(images.get(img), (j * dimension), (i * dimension), this);
            }
        }
        if (toBeRevealed == 0 && playing) {
            playing = false;
            status.setText("YAY! YOU ARE THE CHAMPION!");
        } else if (!playing) {
            moves.clear();
            status.setText("OH NO! GOOD LUCK NEXT TIME!");
        }

        // Undo disabled case 2
        if (moves.empty()) {
            GameBoard.buttonUndo.setEnabled(false);
        } else {
            GameBoard.buttonUndo.setEnabled(true);
        }
    }

// Initializes the game board
    private void board() throws IOException {

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        images = new java.util.HashMap<>();

        /**
         * Put all images and their respective names into the map 
         * NextToBomb cells are named with respective integer numbers.
         *  Other images' names: BombImage, EmptyImage, FlaggedImage, FlaggedButWrongImage
         */

        for (int i = 1; i < 9; i++) {
            String path = "src/images/" + i + ".png";
            images.put(Integer.toString(i), (new ImageIcon(path)).getImage());
        }

        images.put("BombImage", (new ImageIcon("src/images/BombImage.png")).getImage());
        images.put("CoveredImage", (new ImageIcon("src/images/CoveredImage.png")).getImage());
        images.put("EmptyImage", (new ImageIcon("src/images/EmptyImage.png")).getImage());
        images.put("FlaggedImage", (new ImageIcon("src/images/FlaggedImage.png")).getImage());
        images.put("FlaggedButWrongImage", (new ImageIcon(
                "src/images/FlaggedButWrong.png")).getImage());

        addMouseListener(new UserAction()); // The private class UserAction is written below
        instruction();

        // Resume the game when reopening the application

        File file = new File(fileToLoad);
        if (file.exists()) {

            String[] options = { "Yes", "No" };
            int mess = JOptionPane.showOptionDialog(null,
                    "Do you want to resume?", "", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (mess == 1) {
                setUp();
            } else {
                resume();
                repaint();
            }

        } else {
            setUp();
        }
    }

    /**
     * Method to handle the empty cell If the user clicks to uncover an empty cell:
     * The whole surrounding region of empty cells will be revealed
     */

    public void revealEmptyRegion(int x, int y) {

        gameBoard[x][y].reveal();
        moves.push(x * size + y);
        for (int m = -1; m <= 1; m++) {
            for (int n = -1; n <= 1; n++) {
                if ((m != 0 || n != 0) && x + m < size 
                        && y + n < size && x + m >= 0 && y + n >= 0) {

                    CellType type = gameBoard[x + m][y + n].getCellType();
                    if (type == CellType.Empty && gameBoard[x + m][y + n].isCoveredCell()) {
                        int a = x + m;
                        int b = y + n;
                        revealEmptyRegion(a, b);

                    }
                }

            }
        }
    }

    // Method to save user-name when closing the application
    protected static void save() throws IOException {
        String name = "";

        /**
         * Give user a default name (CIS-LOVER) if they didn't enter a name Send a
         * message to notify the user that they are given a default name
         */
        if ("".equals(textArea.getText()) || textArea.getText().equals("Enter Username")) {
            JOptionPane.showMessageDialog(null,
                    "You didn't enter a username, we will remember you as CIS-LOVER then!");
            name = "CIS-LOVER";
            // if the user has already entered a name, save that name
        } else {
            name = textArea.getText();
        }
        if (gameBoard.length == 0) {
            System.exit(0);
        }

        // Writes and stores game status in a text file
        FileWriter writer = new FileWriter(fileToLoad, false);

        try (PrintWriter printLine = new PrintWriter(writer)) {
            printLine.println(s1 + "Name" + s1);
            printLine.println(name);
            printLine.println(s1 + "Cell" + s1);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (null != gameBoard[i][j].getCellType()) {
                        switch (gameBoard[i][j].getCellType()) {
                            case NextToBomb:
                                printLine.println(CellType.NextToBomb.toString() + s2
                                    + Boolean.toString(gameBoard[i][j].isCoveredCell()) + s2
                                    + Boolean.toString(gameBoard[i][j].isFlaggedCell()) + s2
                                    + gameBoard[i][j].getImageMeaning());
                                break;
                            case Empty:
                                printLine.println(
                                    CellType.Empty.toString() + s2 
                                    + Boolean.toString(gameBoard[i][j].isCoveredCell()) + s2 
                                    + Boolean.toString(gameBoard[i][j].isFlaggedCell()) + s2 + "0");
                                break;

                            case Bomb:
                                printLine.println(
                                    CellType.Bomb.toString() + s2 
                                    + Boolean.toString(gameBoard[i][j].isCoveredCell()) + s2
                                    + Boolean.toString(gameBoard[i][j].isFlaggedCell()) + s2 + "0");
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

        JOptionPane.showMessageDialog(null, "You can resume your game later!");

    }

    // When user chooses to resume: call the game status from the file
    private void resume() throws IOException {
        try {
            // 2D Array of cells in game board
            gameBoard = new CellHandling[size][size];

            try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
                playing = true;
                numberFlag = numBomb;
                // Parse user name
                String line = reader.readLine();
                if (line != null) {
                    if (line.startsWith(s1) && line.endsWith(s1) && line.contains("Name")) {
                        line = reader.readLine();
                        if (line != null) {
                            textArea.setText(line);
                        }
                    }

                }
                line = reader.readLine();
                if (line != null) {
                    if (line.startsWith(s1) && line.endsWith(s1) && line.contains("Cell")) {
                        line = reader.readLine();
                        int i = 0;
                        while (line != null) {
                            String[] val = line.split(s2);
                            if (val.length == 4) {
                                if (null != val[0]) {
                                    switch (val[0]) {
                                        case "Empty":
                                            gameBoard[i / size][i % size] = new Empty(val[1],
                                                    val[2]);

                                            break;
                                        case "Bomb":
                                            gameBoard[i / size][i % size] = new Bomb(val[1],
                                                    val[2]);

                                            break;
                                        case "NextToBomb":
                                            gameBoard[i / size][i % size] = new NextToBomb(val[1],
                                                val[2], Integer.valueOf(val[3]));
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                            if (gameBoard[i / size][i % size].isFlaggedCell()) {
                                this.numberFlag--;
                            }
                            line = reader.readLine();
                            i++;
                        }
                    }
                }

                String mess = Integer.toString(numberFlag);
                GameBoard.status.setText("NUMBER OF FLAGS LEFT:" + mess);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// Method to undo 
    private void undo() {
        if (!moves.empty()) {
            // Get the most recent move that the user clicked
            int i = moves.pop();

            // Find the location of the cell that the user click
            CellHandling where = gameBoard[i / size][i % size];

            /**
             * Case 1: Flag If flag then cell not yet revealed Update the number of flags
             * left
             */
            if (where.isCoveredCell()) {
                where.changeFlagStatus();
                if (where.isFlaggedCell()) {
                    numberFlag--;
                } else {
                    numberFlag++;
                    if (!playing) {
                        playing = true;
                    }
                }
                /**
                 * Case 2: Bomb 
                 * If bomb then undo button is disabled
                 */
            } else if (where.getCellType() == CellType.Bomb) {
                where.setCoveredSetter(true);
            
                /**
                 * Case 3: NextToBomb (number cells) 
                 * Change that cell from revealed to covered again
                 */
                playing = true;
            } else if (where.getCellType() == CellType.NextToBomb) {
                where.setCoveredSetter(true);
           
           
            }

            // Update the number of flags at the status bar
            String numFlag = Integer.toString(numberFlag);
            GameBoard.status.setText("NUMBER OF FLAGS LEFT: " + numFlag);

            /**
             * Case 4: Empty 
             * If the cell is empty then the whole empty region is revealed
             * Undo must change back the status of the whole region to covered again
             */
            if (where.getCellType() == CellType.Empty) {
                where.setCoveredSetter(true);
                while (!moves.empty()) {
                    int j = moves.pop();
                    CellHandling next = gameBoard[j / size][j % size];

                    // if the next cell is a number cell
                    if (next.getCellType().equals(CellType.NextToBomb)) {
                        moves.push(j);
                        break;
                    } else { // if the next cell is still an empty cell
                        next.setCoveredSetter(true);
                    }
                }

            }

            repaint();
        }
    }

    @Override
// Action performed (in case the user wants to undo or see instructions
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getActionCommand().equals("VIEW INSTRUCTIONS")) {
                this.instruction();
            } else if (e.getActionCommand().equals("UNDO MOVE")) {
                this.undo();
            }
        } catch (Exception oe) {
            oe.printStackTrace();
        }
    }

//Private class UserAction: Makes changes based on user action 
    private class UserAction extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {

            // Find the location of the mouse click on the board
            // (convert to a cell location)
            int x = e.getX();
            int y = e.getY();

            int col = x / dimension;
            int row = y / dimension;

            boolean handle = false;

            if (!playing) {

                setUp();
                repaint();
                moves.clear();
            }

            // Condition: user must click inside the game playing board
            if ((x < size * dimension) && (y < size * dimension)) {

                /**
                 * RIGHT CLICK 
                 * If the cell is covered: flag the cell 
                 * If the cell is flagged: remove flag 
                 * If the cell is revealed to be empty/ number cell: do nothing
                 */
                if (e.getButton() == MouseEvent.BUTTON3) {

                    if (gameBoard[row][col].isCoveredCell()) {
                        handle = true;

                        /**
                         * Case 1: right click on covered and not yet flagged cell 
                         * Flag that cell 
                         * Reduce the number of flags left If no more flag left: show status
                         */

                        if (!gameBoard[row][col].isFlaggedCell() && numberFlag > 0) {
                            CellHandling board = gameBoard[row][col];
                            board.changeFlagStatus();
                            numberFlag--;
                            if (numberFlag > 0) {
                                String numFlag = Integer.toString(numberFlag);
                                status.setText("NUMBER OF FLAGS LEFT: " + numFlag);
                            } else {
                                status.setText("YOU ARE OUT OF FLAGS!");
                             // add that move to the Stack
                            } 
                            moves.push(row * size + col);
                            /**
                             * Case 2: right click on covered and flagged cell 
                             * Remove flag from that cell
                             * Increase the number of flags left
                             */    
                            
                        } else if (gameBoard[row][col].isFlaggedCell()) {
                            gameBoard[row][col].changeFlagStatus();
                            numberFlag++;
                            String numFlag = Integer.toString(numberFlag);
                            status.setText("NUMBER OF FLAGS LEFT: " + numFlag);
                        }
                        

                    }

                    /**
                     * LEFT CLICK If the cell is flagged: do nothing 
                     * If the cell is covered and number: reveal the cell 
                     * If the cell is covered and empty: reveal the empty region 
                     * If the cell is covered and bomb: change the boolean playing to false
                     * If the cell is revealed: do nothing
                     */
                } else {
                    if (gameBoard[row][col].isFlaggedCell()) {
                        return;
                    }

                    if (gameBoard[row][col].isCoveredCell()) {
                        gameBoard[row][col].reveal();
                        handle = true;
                        moves.push(row * size + col);

                        if (gameBoard[row][col].getCellType() == CellType.Bomb
                                && !gameBoard[row][col].isCoveredCell()) {
                            playing = false;
                        }

                        if (gameBoard[row][col].getCellType() == CellType.Empty) {
                            revealEmptyRegion(row, col);
                        }
                    }
                }

                if (handle) {
                    repaint();
                }

            }
        }
    }

}
