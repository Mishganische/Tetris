package tetris;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;



// Tests saving and loading a GameSave object via Serializer
public class SerializationTest {

    // Ensures that a saved GameSave can be loaded back correctly
    @Test
    void gameSaveRoundTrip() throws Exception {

        // Create a simple board and piece to store
        Board board = new Board();
        Piece piece = new Piece(Tetromino.I, 5, 1);
        GameSave save = new GameSave(board, piece, Tetromino.T, 1234);

        File tmp = File.createTempFile("tetris-save", ".ser");
        Serializer.save(tmp, save);

        // Create test save object
        GameSave loaded = Serializer.load(tmp, GameSave.class);

        // Verify score and next piece match
        assertEquals(1234, loaded.score);
        assertEquals(save.currentPiece.type, loaded.currentPiece.type);
    }
}
