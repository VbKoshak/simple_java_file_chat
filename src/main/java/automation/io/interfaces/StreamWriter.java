package automation.io.interfaces;

import automation.io.exception.UnableToWriteException;

public interface StreamWriter {
    void write(String path, Packable pkg) throws UnableToWriteException;
}
