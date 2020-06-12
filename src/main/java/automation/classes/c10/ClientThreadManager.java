package automation.classes.c10;

import automation.classes.c10.ClientThread;
import automation.classes.c10.bo.RegisterMessage;
import automation.io.impl.xml.XMLMessage;
import org.apache.log4j.Logger;

public class ClientThreadManager {
    private static int clientThreadCount = 0;

    public void createThread(XMLMessage msg, Logger logger, String historyPath) {
        new ClientThread(msg,logger,historyPath);
        clientThreadCount++;
    }


    public static int getThreadCount() {
        return clientThreadCount + 1;
    }
}
