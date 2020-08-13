package automation.filters.impl;

import automation.filters.Filter;
import automation.filters.MessageFilter;
import org.apache.log4j.Logger;

public class SpaceFilter extends Filter implements MessageFilter {

    public SpaceFilter(String filterName, Logger logger) {
        super(filterName, logger);
    }


    @Override
    public String apply(String message) {
        return message.replaceAll("\\s+"," ");
    }
}
