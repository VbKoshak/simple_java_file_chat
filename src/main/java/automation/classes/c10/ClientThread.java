package automation.classes.c10;

import automation.constant.MessageConstants;
import automation.constant.TimeConstant;
import automation.filters.FilterList;
import automation.io.exception.UnableToReadException;
import automation.io.impl.file.TextFileReader;
import automation.io.impl.xml.XMLController;
import automation.io.impl.xml.XMLMessage;
import automation.mybatis.model.Message;
import automation.mybatis.model.User;
import automation.mybatis.service.MessageService;
import automation.mybatis.service.UserService;
import automation.util.PropertyUtil;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
     * login - user's login to display (auth for second time log-in)
     * id - user's id in db
     *
     * us - tool to communicate with user db
     * ms - tool to communicate with msg db
     *
     * running - flag, showing 'life' of a connection
     *
     * fls - List of Filters to refactor messages
     * historyPath - path to store history of a chat
     * statusF - file for file-marker
     * statusTFR - FileReader for file-marker
     * dateFormat - formatter for Date
     */
    private String responsePath;
    private String statusPath;
    private String fullStatusPath;
    private Logger logger;
    private String login;
    private long id;

    private UserService us = new UserService();
    private MessageService ms = new MessageService();

    private boolean running = true;

    private FilterList fls;
    private String historyPath;
    private File statusF;
    private TextFileReader statusTFR;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

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
        this.login = msg.getLogin();
        this.statusPath = responsePath + PropertyUtil.getValueByKey("client_status_suffix");
        this.fullStatusPath =  System.getProperty("user.dir") + this.statusPath;

        this.statusF = new File(fullStatusPath);
        this.statusTFR = new TextFileReader(statusF);

        fls = new FilterList();
        fls.initBasic(logger);

        this.id = us.getUserByPath(Integer.parseInt(msg.getMessage().replaceAll("[^0-9]+", ""))).getUser_id();


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
                    ms.createMessage(new Message(id, mess));
                    logger.info(mess);
                    Server.sendResponseMessage(responsePath, login, MessageConstants.CONNECTION_SUCCESS_INFO, 200);
                } else if (msg.getType().equals(MessageConstants.CLOSE_MESSAGE_TYPE)) {
                    running = false;
                    Server.sendResponseMessage(responsePath, login, MessageConstants.CONNECTION_SUCCESS_INFO, 200);
                } else if (msg.getType().equals(MessageConstants.HISTORY_MESSAGE_TYPE)) {
                    String hstry = "";
                    List<Message> usersList = ms.getByUserId(id);
                    for (Message mesg: usersList) {
                        hstry += dateFormat.format(mesg.getTm()) + ": " + mesg.getMessage() + '\n';
                    }
                    Server.sendResponseMessage(responsePath, login, hstry, 201);
                } else {
                    Server.sendResponseMessage(responsePath, login, MessageConstants.CONNECTION_FAILED_INFO, 410);
                }
            } else {
                Server.sendResponseMessage(responsePath, login, MessageConstants.CONNECTION_FAILED_INFO, 410);
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
