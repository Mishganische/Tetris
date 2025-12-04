package tetris;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//Stores top player results with name score
public class HighScores implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    // Single high score entry
    public static class Entry implements Serializable {
        @Serial private static final long serialVersionUID = 1L;
        public final String name;
        public final int score;
        public final LocalDateTime createdAt;

        // Creates a new score entry
        public Entry(String name, int score) {
            this.name = name;
            this.score = score;
            this.createdAt = LocalDateTime.now();
        }
    }

    private final List<Entry> entries = new ArrayList<>();


    //Adds a new result and sorts entries
    public void add(String name, int score) {
        entries.add(new Entry(name, score));
        entries.sort(Comparator.comparingInt((Entry e) -> e.score).reversed());
        if (entries.size() > 10) {
            entries.remove(entries.size() - 1);
        }
    }

    public List<Entry> top() {
        return List.copyOf(entries);
    }
}
