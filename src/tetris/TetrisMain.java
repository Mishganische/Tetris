package tetris;

import javax.swing.SwingUtilities;

public class TetrisMain {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new TetrisFrame().setVisible(true));
    }
}
