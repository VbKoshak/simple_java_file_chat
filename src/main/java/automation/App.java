package automation;

import automation.classes.c10.Client;
import automation.constant.C10Constant;
import automation.util.PropertyUtil;

public class App 
{
    final static String HOST = PropertyUtil.getValueByKey(C10Constant.HOSTNAME);
    final static int PORT = Integer.valueOf(PropertyUtil.getValueByKey(C10Constant.PORT));


    public static void main( String[] args ) {
        Client cl1 = new Client();
        cl1.start();
    }
}
