package automation.io.impl.stream;

import automation.io.base.BaseReader;
import automation.io.exception.UnableToReadException;
import automation.io.interfaces.Packable;
import automation.io.interfaces.StreamReader;

import java.io.*;

public class ObjectReader extends BaseReader implements StreamReader {
    public ObjectReader(File file) {
        super(file);
    }

    public ObjectReader(String path) {
        super(path);
    }

    @Override
    public Packable read() throws UnableToReadException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.file.getAbsoluteFile()))) {
            return (Packable) ois.readObject();
        } catch (EOFException e) {
            // do nothing :)
            return null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new UnableToReadException("Could not read object!");
        }
    }
}
