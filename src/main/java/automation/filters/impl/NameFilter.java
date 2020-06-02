package automation.filters.impl;

import automation.filters.Filter;
import automation.filters.FilterWithPredefinedSet;
import automation.filters.MessageFilter;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

import static org.apache.log4j.config.PropertyPrinter.capitalize;

public class NameFilter extends FilterWithPredefinedSet implements MessageFilter {

    private Set<String> names;

    public NameFilter(String filterName, Logger logger) {
        super(filterName, logger);
        names = getSetFromCSV(filterName, logger);
    }

    @Override
    public String apply(String message) {
        String[] returnMsg = message.split(" ");
        String tmp;
        int length = returnMsg.length;
        for (int i = 0; i < length; i++) {
            tmp = capitalize(returnMsg[i].toLowerCase());
            if(names.contains(tmp)) {
                returnMsg[i] = tmp;
            }
        }
        return String.join(" ", returnMsg);
    }
}
