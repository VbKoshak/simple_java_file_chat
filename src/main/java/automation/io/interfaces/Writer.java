package automation.io.interfaces;

import automation.io.exception.UnableToWriteException;

public interface Writer {
    void write(String path, String text) throws UnableToWriteException;
    void append(String path, String text) throws UnableToWriteException;
}
