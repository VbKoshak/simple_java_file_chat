package automation.io.interfaces;

import automation.io.exception.UnableToReadException;

public interface StreamReader {
    Packable read() throws UnableToReadException;
}
