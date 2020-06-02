package automation.filters;

import automation.io.exception.UnableToReadException;
import automation.io.impl.file.StreamTextFileReader;
import automation.util.PropertyUtil;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class FilterWithPredefinedSet extends Filter{

    public static Set<String> getSetFromCSV(String filterName, Logger logger){
        Set<String> res = new HashSet<>();
        try {
            String[] strArr = (new StreamTextFileReader(System.getProperty("user.dir") + PropertyUtil.getValueByKey("filter_path") + filterName)).read().split(",");
            for(String el : strArr) {
                res.add(el);
            }
            logger.info("BadWord list loaded");
        } catch (UnableToReadException ex) {
            logger.error(ex.getMessage());
        }
        return res;
    }

    public FilterWithPredefinedSet(String name, Logger logger) {
        super(name, logger);
    }
}
