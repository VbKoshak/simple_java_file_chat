package automation.io.impl.file;

import automation.io.base.BaseReader;
import automation.io.exception.UnableToReadException;
import automation.io.interfaces.Reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CsvFileReader extends BaseReader implements Reader {

    public StreamTextFileReader(File file) {
        super(file);
    }

    public StreamTextFileReader(String path) {
        super(path);
    }

    @Override
    public Set<String> read() throws UnableToReadException{
        try(FileInputStream fin = new FileInputStream(this.file)) { // try withou resources
            byte[] buffer = new byte[fin.available()];
            fin.read(buffer);
            String str = String(buffer);
            Set<String> set = str.split(',');
            return new set;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new UnableToReadException(String.format("Could not read %s!", this.path));
    }
}
