package automation.filters.impl;

import automation.filters.Filter;
import automation.filters.MessageFilter;
import org.apache.log4j.Logger;

public class SentenceFilter extends Filter implements MessageFilter {

    public SentenceFilter(String filterName, Logger logger) {
        super(filterName, logger);
    }


    @Override
    public String apply(String message) {
        return message.replaceAll("([,?!.])(\\S)", "$1 $2");
    }
}
