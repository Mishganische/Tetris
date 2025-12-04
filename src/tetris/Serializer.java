package tetris;

import java.io.*;


//Utility class for saving and loading serializable objects to and from disk using Java's built-in object serialization
public final class Serializer {

    private Serializer() {
    }

    public static <T> void save(File file, T obj) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(obj);
        }
    }

    public static <T> T load(File file, Class<T> clazz)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            return (T) clazz.cast(obj);
        }
    }
}
