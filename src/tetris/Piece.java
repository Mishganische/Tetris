package tetris;

import java.awt.Point;
import java.io.Serial;
import java.io.Serializable;

//Represents a single falling tetromino on the board and stores its type, rotation, and position
public final class Piece implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    public Tetromino type;
    public int rotation;
    public int x, y;


    //Creates a new piece of the given tetromino type at the given position
    public Piece(Tetromino type, int x, int y) {
        this.type = type;
        this.rotation = 0;
        this.x = x;
        this.y = y;
    }

    //Computes the absolute board coordinates of all 4 cells that this piece currently occupies, based on its type,rotation, and center position
    public Point[] cells() {
        Point[] base = type.rotations[rotation];
        Point[] out = new Point[4];
        for (int i = 0; i < 4; i++) {
            out[i] = new Point(x + base[i].x, y + base[i].y);
        }
        return out;
    }

    public Piece moved(int dx, int dy) {
        Piece piece = new Piece(type, x + dx, y + dy);
        piece.rotation = rotation;
        return piece;
    }

    public Piece rotated(int dir) {
        Piece piece = new Piece(type, x, y);
        piece.rotation = Math.floorMod(rotation + dir, 4);
        return piece;
    }
}
