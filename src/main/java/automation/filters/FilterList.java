package automation.filters;

import automation.filters.impl.*;
import automation.util.PropertyUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FilterList {
    private List<MessageFilter> fls;

    public FilterList() {
        fls = new ArrayList<>();
    }

    public void add(MessageFilter filter) {
        this.fls.add(filter);
    }

    public String formatString(String str) {
        String res = str;
        for (MessageFilter f: fls){
            res = f.apply(res);
        }
        return res;
    }

    public void initBasic(Logger logger) {
        fls.add(new SpaceFilter("Spaces",logger));
        fls.add(new SentenceFilter("Sentences",logger));
        fls.add(new BadWordFilter(PropertyUtil.getValueByKey("badWord"), logger));
        fls.add(new NameFilter(PropertyUtil.getValueByKey("names"), logger));
        fls.add(new NameFilter(PropertyUtil.getValueByKey("geoNames"), logger));
        fls.add(new EmojiFiler());
    }
}
