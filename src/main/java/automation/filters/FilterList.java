package automation.filters;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FilterList {
    List<MessageFilter> fls;

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
}
