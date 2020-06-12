package automation.classes.c10;

import automation.constant.MessageConstants;
import automation.constant.TimeConstant;
import automation.filters.FilterList;
import automation.io.exception.UnableToReadException;
import automation.io.impl.file.TextFileReader;
import automation.io.impl.xml.XMLController;
import automation.io.impl.xml.XMLMessage;
import automation.util.PropertyUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * thread created by server to communicate with a certain client
 */
public class ClientThread extends Thread {

    /**
     * responsePath - Path of a file 4 communication with server
     * fullResponsePath - Full path of a file 4 communication with server
     * statusPath - Path of a file-marker of response status
     * fullStatusPath - Full path of a file-marker of response status
     * logger - Logger for Server output
     *
     * running - flag, showing 'life' of a connection
     *
     * fls - List of Filters to refactor messages
     * historyPath - path to store history of a chat
     * statusF - file for file-marker
     * statusTFR -FileReader for file-marker
     */
    private String responsePath;
    private String statusPath;
    private String fullStatusPath;
    private Logger logger;

    private boolean running = true;

    private FilterList fls;
    private String historyPath;
    private File statusF;
    private TextFileReader statusTFR;

    /**
     * basic constructor for client thread
     * initialize all necessary variables and starts communication process
     * @param msg - XMLMessage register message from client
     * @param logger - Logger for server output
     * @param historyPath - path to store history of a chat
     */
    public ClientThread(XMLMessage msg, Logger logger, String historyPath) {
        super(msg.getMessage().replaceAll("[^0-9]+", ""));
        this.logger = logger;
        this.historyPath = historyPath;
        this.responsePath = msg.getMessage();
        this.statusPath = responsePath + PropertyUtil.getValueByKey("client_status_suffix");
        this.fullStatusPath =  System.getProperty("user.dir") + this.statusPath;

        this.statusF = new File(fullStatusPath);
        this.statusTFR = new TextFileReader(statusF);

        fls = new FilterList();
        fls.initBasic(logger);

        logger.info(MessageConstants.ON_THREAD_CREATION);
        start();
    }

    /**
     * work with message from aa client, sends corresponding response
     * @param msg - XMLMessage from a client
     */
    private void processMessage(XMLMessage msg) {
            if (Server.isRegisteredXML(msg)) {
                if (msg.getType().equals(MessageConstants.SIMPLE_MESSAGE_TYPE)) {
                    String mess = fls.formatString(msg.getMessage());
                    try (FileWriter fw = new FileWriter(historyPath, true)) {
                        fw.write("\n" + mess);
                    } catch (IOException ex) {
                        logger.error(ex.getMessage());
                    }
                    logger.info(mess);
                    Server.sendResponseMessage(responsePath, MessageConstants.CONNECTION_SUCCESS_INFO, 200);
                } else if (msg.getType().equals(MessageConstants.CLOSE_MESSAGE_TYPE)) {
                    running = false;
                    Server.sendResponseMessage(responsePath, MessageConstants.CONNECTION_SUCCESS_INFO, 200);
                } else if (msg.getType().equals(MessageConstants.HISTORY_MESSAGE_TYPE)) {
                    String hstry = Server.getHistoryPath();
                    Server.sendResponseMessage(responsePath, hstry, 201);
                } else {
                    Server.sendResponseMessage(responsePath, MessageConstants.CONNECTION_FAILED_INFO, 410);
                }
            } else {
                Server.sendResponseMessage(responsePath, MessageConstants.CONNECTION_FAILED_INFO, 410);
            }
    }
    /**
     * constantly checks if message from client ready to read, after that scans it
     * and passes to 'processMessage'
     */
    private void listen() {
        if (statusF.length() > 0) {
            try {
                if (statusTFR.read().equals(MessageConstants.CLIENT_READY_CHAR)) {
                    XMLMessage msg = XMLController.readMessage(this.responsePath);
                    Server.clearFile(statusPath);
                    Server.clearFile(responsePath);

                    if (msg.getHead().equals(MessageConstants.SIMPLE_MESSAGE_HEAD)) {
                        processMessage(msg);
                    }
                }
                Thread.sleep(TimeConstant.TIME_TO_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnableToReadException e) {
                Server.clearFile(statusPath);
                Server.clearFile(responsePath);
                logger.error(MessageConstants.ON_READ_ERROR);
            }
        }
    }

    @Override
    public void run() {

        while (running) {
            listen();
        }

    }
}
