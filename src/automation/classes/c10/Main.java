package automation.classes.c10;

import automation.constant.C10Constant;
import automation.util.PropertyUtil;

public class Main {
    final static String HOST = PropertyUtil.getValueByKey(C10Constant.HOSTNAME);
    final static int PORT = Integer.valueOf(PropertyUtil.getValueByKey(C10Constant.PORT));

    public static void main(String[] args) {
        Client cl1 = new Client(HOST,PORT,PropertyUtil.getValueByKey("niceToken1"));
        Client cl2 = new Client(HOST,PORT,PropertyUtil.getValueByKey("niceToken2"));
        Client cl3 = new Client(HOST,PORT,PropertyUtil.getValueByKey("badToken1"));

        cl1.sendMessage();
        cl2.sendMessage();
        cl1.sendMessage();
        cl3.sendMessage();
    }
}
