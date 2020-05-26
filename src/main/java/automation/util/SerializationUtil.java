package automation.util;

import automation.io.exception.UnableToReadException;
import automation.io.exception.UnableToWriteException;
import automation.io.impl.stream.ObjectReader;
import automation.io.impl.stream.ObjectWriter;
import automation.io.interfaces.Packable;

public class SerializationUtil {

    public static void writeObject(Packable obj, String localPath) {
        String path = System.getProperty("user.dir") + localPath;
        try {
            new ObjectWriter().write(path, obj);
        } catch (UnableToWriteException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("%s is unable to write!", path));
        }
    }

    public static Packable readObject(String localPath) {
        String path = System.getProperty("user.dir") + localPath;
        try {
            ObjectReader reader = new ObjectReader(path); //serial
            return reader.read();
        } catch (UnableToReadException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("%s is unable to read!", path));
        }

    }

}
