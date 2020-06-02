package automation.filters;

import org.apache.log4j.Logger;

public abstract class Filter {
    public String name;
    public Logger logger;

    public Filter(String name, Logger logger){
        this.name = name;
        this.logger = logger;
    }
}
