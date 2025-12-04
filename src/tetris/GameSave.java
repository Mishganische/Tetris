package tetris;

import java.io.Serial;
import java.io.Serializable;

// Contains a complete snapshot of the game state for saving/loading
public class GameSave implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    public final Board board;
    public final Piece currentPiece;
    public final Tetromino nextPiece;
    public final int score;

    //// Stores all required components of the game state.
    public GameSave(Board board, Piece currentPiece, Tetromino nextPiece, int score) {
        this.board = board;
        this.currentPiece = currentPiece;
        this.nextPiece = nextPiece;
        this.score = score;
    }
}
