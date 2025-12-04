package tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

// Main game panel: handles rendering, game loop, and input
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final int CELL_SIZE = 28;
    private static final int TICK_MS = 500;

    //timer to drive the game loop
    private final javax.swing.Timer timer = new javax.swing.Timer(TICK_MS, this);


    private Board board = new Board();
    private Piece currentPiece;
    private Tetromino nextPiece;
    private int score;
    private boolean paused = false;
    private boolean gameOver = false;

    // Queue for 7-bag tetromino randomization
    private final Deque<Tetromino> pieceQueue = new ArrayDeque<>();
    private final Random random = new Random();

    private static final Map<Tetromino, Color> COLORS = new EnumMap<>(Tetromino.class);

    // Color mapping for tetromino types
    static {
        COLORS.put(Tetromino.I, new Color(0, 240, 240));
        COLORS.put(Tetromino.O, new Color(240, 240, 0));
        COLORS.put(Tetromino.T, new Color(160, 0, 240));
        COLORS.put(Tetromino.S, new Color(0, 240, 0));
        COLORS.put(Tetromino.Z, new Color(240, 0, 0));
        COLORS.put(Tetromino.J, new Color(0, 0, 240));
        COLORS.put(Tetromino.L, new Color(240, 160, 0));
    }

    // Initializes the game panel, sets up input, pieces, and starts the timer
    public GamePanel() {
        setPreferredSize(new Dimension(Board.WIDTH * CELL_SIZE, (Board.HEIGHT - 2) * CELL_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        refillBag();
        spawnFirstPieces();
        timer.start();
    }

    // Fills the piece queue with a shuffled set of all 7 tetrominoes
    private void refillBag() {
        List<Tetromino> list = new ArrayList<>(Arrays.asList(Tetromino.values()));
        Collections.shuffle(list, random);
        pieceQueue.addAll(list);
    }

    // Returns the next tetromino from the bag, refilling when empty
    private Tetromino nextFromBag() {
        if (pieceQueue.isEmpty()) {
            refillBag();
        }
        return pieceQueue.removeFirst();
    }

    // Spawns the initial current and next pieces and checks for immediate game over
    private void spawnFirstPieces() {
        currentPiece = new Piece(nextFromBag(), Board.WIDTH / 2, 1);
        nextPiece = nextFromBag();
        if (!board.canPlace(currentPiece)) {
            gameOver = true;
            timer.stop();
        }
    }

    // Spawns the next falling piece and updates the preview, checking for game over
    private void spawnNext() {
        currentPiece = new Piece(nextPiece, Board.WIDTH / 2, 1);
        nextPiece = nextFromBag();
        if (!board.canPlace(currentPiece)) {
            gameOver = true;
            timer.stop();
        }
    }

    // Creates a serializable snapshot of the current game state
    public GameSave snapshot() {
        return new GameSave(board, currentPiece, nextPiece, score);
    }


    // Restores game state from a previously saved snapshot
    public void restore(GameSave save) {
        this.board = save.board;
        this.currentPiece = save.currentPiece;
        this.nextPiece = save.nextPiece;
        this.score = save.score;
        this.gameOver = false;
        this.paused = false;
        repaint();
    }


    // Returns the current score
    public int getScore() {
        return score;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused && !gameOver) {
            stepDown();
        }
    }


    // Moves the piece down one cell or locks it and clears lines if it cannot move
    private void stepDown() {
        Piece down = currentPiece.moved(0, 1);
        if (board.canPlace(down)) {
            currentPiece = down;
        } else {
            board.lock(currentPiece);
            int cleared = board.clearLines();
            if (cleared > 0) {
                score += switch (cleared) {
                    case 1 -> 100;
                    case 2 -> 300;
                    case 3 -> 500;
                    default -> 800;
                };
            }
            spawnNext();
        }
        repaint();
    }


    // Attempts to move the current piece by the given delta
    private void tryMove(int dx, int dy) {
        Piece moved = currentPiece.moved(dx, dy);
        if (board.canPlace(moved)) {
            currentPiece = moved;
            repaint();
        }
    }


    // Attempts to rotate the current piece with simple wall kicks
    private void tryRotate(int dir) {
        Piece rotated = currentPiece.rotated(dir);

        int[][] kicks = {
                {0, 0},
                {-1, 0},
                {1, 0},
                {0, -1}
        };

        for (int[] kick : kicks) {
            Piece candidate = new Piece(rotated.type, rotated.x + kick[0], rotated.y + kick[1]);
            candidate.rotation = rotated.rotation;
            if (board.canPlace(candidate)) {
                currentPiece = candidate;
                repaint();
                return;
            }
        }
    }


    // Instantly drops the piece to the lowest valid position and locks it
    private void hardDrop() {
        while (board.canPlace(currentPiece.moved(0, 1))) {
            currentPiece = currentPiece.moved(0, 1);
        }
        stepDown();
    }

    public void togglePause() {
        paused = !paused;
        repaint();
    }


    // Renders the board, current piece, next preview, score, and status text

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int y = 2; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                int value = board.get(x, y);
                drawCell(g2, x, y - 2, value);
            }
        }

        if (!gameOver) {
            for (var c : currentPiece.cells()) {
                if (c.y >= 2) {
                    drawCell(g2, c.x, c.y - 2, currentPiece.type.ordinal() + 1);
                }
            }
        }

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 5, 15);

        g.drawString("Next:", 5, 35);
        int boxX = Board.WIDTH * CELL_SIZE - 90;
        int boxY = 10;
        g.drawRect(boxX, boxY, 80, 80);

        Piece preview = new Piece(nextPiece, 4, 3);
        for (var c : preview.cells()) {
            int px = boxX + 8 + (c.x - 3) * 16;
            int py = boxY + 8 + (c.y - 2) * 16;
            drawSmall(g2, px, py, nextPiece.ordinal() + 1);
        }

        if (paused) {
            g.drawString("PAUSED", getWidth() / 2 - 20, 20);
        }
        if (gameOver) {
            g.drawString("GAME OVER", getWidth() / 2 - 30, 20);
        }
    }


    // Draws a single cell of the main board at the given grid position
    private void drawCell(Graphics2D g, int cx, int cy, int value) {
        int x = cx * CELL_SIZE;
        int y = cy * CELL_SIZE;
        if (value == 0) {
            g.setColor(new Color(25, 25, 25));
        } else {
            Tetromino t = Tetromino.values()[value - 1];
            g.setColor(COLORS.getOrDefault(t, new Color(60, 60, 60)));
        }
        g.fillRect(x, y, CELL_SIZE - 1, CELL_SIZE - 1);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, CELL_SIZE - 1, CELL_SIZE - 1);
    }



    private void drawSmall(Graphics2D g, int x, int y, int value) {
        Tetromino t = Tetromino.values()[value - 1];
        g.setColor(COLORS.getOrDefault(t, new Color(60, 60, 60)));
        g.fillRect(x, y, 16, 16);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, 16, 16);
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> tryMove(-1, 0);
            case KeyEvent.VK_RIGHT -> tryMove(1, 0);
            case KeyEvent.VK_DOWN -> tryMove(0, 1);
            case KeyEvent.VK_UP, KeyEvent.VK_X -> tryRotate(+1);
            case KeyEvent.VK_Z -> tryRotate(-1);
            case KeyEvent.VK_SPACE -> hardDrop();
            case KeyEvent.VK_P -> togglePause();
        }
    }

    @Override public void keyReleased(KeyEvent e) { }
    @Override public void keyTyped(KeyEvent e) { }
}
