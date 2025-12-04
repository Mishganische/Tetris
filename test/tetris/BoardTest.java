package tetris;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



// Tests for board logic line clearing and piece placement

public class BoardTest {


    // Tests that a full line is removed and rows above fall down correctly
    @Test
    void fullLineIsCleared() {
        Board board = new Board();
        int y = Board.HEIGHT - 1;

        // Fill the bottom row completely
        for (int x = 0; x < Board.WIDTH; x++) {
            board.set(x, y, 1);
        }

        // Clear lines and verify 1 line was removed
        int cleared = board.clearLines();
        assertEquals(1, cleared);

        // The bottom row have to be empty
        for (int x = 0; x < Board.WIDTH; x++) {
            assertEquals(0, board.get(x, y));
        }
    }

    // Tests placing and locking a piece on the board
    @Test
    void canPlaceAndLockPiece() {
        Board board = new Board();

        // Create a piece high on the board
        Piece piece = new Piece(Tetromino.O, Board.WIDTH / 2, 1);

        // Should be placeable initially
        assertTrue(board.canPlace(piece));

        // Lock the piece into the board
        board.lock(piece);

        // Now the same spot is no longer empty
        assertFalse(board.canPlace(piece));
    }
}
