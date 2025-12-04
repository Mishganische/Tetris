package tetris;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

// Represents the logical game board grid and line-clearing logic
public class Board implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    public static final int WIDTH = 10;
    public static final int HEIGHT = 22;

    private final int[][] grid = new int[HEIGHT][WIDTH];

    public int get(int x, int y) {
        return grid[y][x];
    }

    public void set(int x, int y, int value) {
        grid[y][x] = value;
    }

    // Checks if the given coordinates are inside the board
    public boolean inBounds(int x, int y) {
        return (x >= 0) && (x < WIDTH) && (y >= 0) && (y < HEIGHT);
    }

    // Checks if the given piece can be placed on the board without collisions
    public boolean canPlace(Piece piece) {
        for (var cell : piece.cells()) {
            if (!inBounds(cell.x, cell.y) || get(cell.x, cell.y) != 0) {
                return false;
            }
        }
        return true;
    }


    // Locks the given piece cells into the board grid
    public void lock(Piece piece) {
        int value = piece.type.ordinal() + 1;
        for (var cell : piece.cells()) {
            if (inBounds(cell.x, cell.y)) {
                set(cell.x, cell.y, value);
            }
        }
    }


    // Clears all full lines and compacts the board down. Returns count of cleared lines
    public int clearLines() {
        int cleared = 0;
        int write = HEIGHT - 1;

        for (int y = HEIGHT - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] == 0) {
                    full = false;
                    break;
                }
            }
            if (!full) {
                if (write != y) {
                    grid[write] = Arrays.copyOf(grid[y], WIDTH);
                }
                write--;
            } else {
                cleared++;
            }
        }
        while (write >= 0) {
            Arrays.fill(grid[write--], 0);
        }
        return cleared;
    }

    // Returns a copy of the board grid
    public int[][] snapshot() {
        int[][] copy = new int[HEIGHT][WIDTH];
        for (int y = 0; y < HEIGHT; y++) {
            copy[y] = Arrays.copyOf(grid[y], WIDTH);
        }
        return copy;
    }
}
