package automation.filters.impl;

import automation.filters.Filter;
import automation.filters.FilterWithPredefinedSet;
import automation.filters.MessageFilter;
import automation.io.exception.UnableToReadException;
import automation.io.impl.file.StreamTextFileReader;
import automation.util.PropertyUtil;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class BadWordFilter extends FilterWithPredefinedSet implements MessageFilter {
    Set badWords;

    public BadWordFilter(String filterName, Logger logger){
        super(filterName, logger);
        badWords = getSetFromCSV(filterName, logger);
    }

    @Override
    public String apply(String msg) {
        String[] returnMsg = msg.split(" ");

        int length = returnMsg.length;
        for (int i = 0; i < length; i++) {
            if(badWords.contains(returnMsg[i].toUpperCase())) {
                returnMsg[i] = "***";
            }
        }
        return String.join(" ", returnMsg);
    }
}
