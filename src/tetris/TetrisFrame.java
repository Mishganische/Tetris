package tetris;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

//Hosts the GamePanel and provides a menu bar for user actions
public class TetrisFrame extends JFrame {

    private final GamePanel gamePanel = new GamePanel();

    private final File saveFile = new File("tetris.save");
    private final File scoresFile = new File("scores.ser");

    private HighScores highScores;
//Constructor the main game window,sets up the menu bar, attaches the GamePanel, and loads high scores.
    public TetrisFrame() {
        super("Tetris Game (Swing)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        loadHighScores();
    }
//Builds the main menu bar with File, Game, and Help menus. Each menu item is wired to the corresponding game action.
    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.add(new JMenuItem(new AbstractAction("New") {
            //Starts a new game
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new TetrisFrame().setVisible(true);
            }
        }));
        file.add(new JMenuItem(new AbstractAction("Save") {
            //Saves the current game state to disk using serialization
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Serializer.save(saveFile, gamePanel.snapshot());
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        }));
        file.add(new JMenuItem(new AbstractAction("Load") {
            //Loads a previously saved game state from disk
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    GameSave save = Serializer.load(saveFile, GameSave.class);
                    gamePanel.restore(save);
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        }));
        file.addSeparator();
        file.add(new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }));
        bar.add(file);

        JMenu gameMenu = new JMenu("Game");
        gameMenu.add(new JMenuItem(new AbstractAction("Pause/Resume") {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.togglePause();
            }
        }));
        bar.add(gameMenu);

        JMenu help = new JMenu("Help");
        help.add(new JMenuItem(new AbstractAction("High Scores") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showScoresDialog();
            }
        }));
        help.add(new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        TetrisFrame.this,
                        "Tetris Game\n",
                        "About",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }));
        bar.add(help);

        return bar;
    }

    private void loadHighScores() {
        try {
            highScores = Serializer.load(scoresFile, HighScores.class);
        } catch (Exception e) {
            highScores = new HighScores();
        }

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                String defaultName = System.getProperty("user.name", "Player");
                String name = JOptionPane.showInputDialog(
                        TetrisFrame.this,
                        "Enter your name for the high scores:",
                        defaultName
                );
                if (name == null || name.isBlank()) {
                    name = defaultName;
                }
                highScores.add(name, gamePanel.getScore());
                try {
                    Serializer.save(scoresFile, highScores);
                } catch (Exception ignored) {
                }
            }
        });
    }

    //Builds and shows a simple text dialog with the top 10 scores
    private void showScoresDialog() {
        StringBuilder sb = new StringBuilder("Top 10 scores:\n");
        int i = 1;
        for (HighScores.Entry e : highScores.top()) {
            sb.append(i++)
                    .append(". ")
                    .append(e.name)
                    .append(" : ")
                    .append(e.score)
                    .append("\n");
        }
        JOptionPane.showMessageDialog(
                this,
                sb.toString(),
                "High Scores",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    //Shows an error dialog for any caught exception during I/O or game actions
    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
