package automation.classes.c10;

import automation.constant.MessageConstants;
import automation.constant.TimeConstant;
import automation.io.impl.xml.XMLController;
import automation.io.impl.xml.XMLMessage;
import automation.util.PropertyUtil;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


/**
 * class representing Server side of a chat
 *  1) server always  listen "serial_path" file in order to find  new regsters
 *  2) when register message passed creates new thread and passes further work to it
 */
public class Server {
    /**
     * logger - Logger for server output
     * AVAIBLE_CLIENTS - list of token, that allows clients to connect
     * HOST,PORT - server credentials
     * historyPath - path to a file, storing history of a chat
     * token - personal token for replies
     * registers - file to cjeck register messages ("serial_path")
     */

    private static final Logger logger = Logger.getLogger(Server.class.getSimpleName());

    private static final List<String> AVAILABLE_CLIENTS = Arrays.asList("user","admin");
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;

    private static String historyPath;
    private final static String TOKEN = "server";
    private final static File registers;

    /**
     * initialize server with necessary variables and files
     */
    static {
        BasicConfigurator.configure();
        String storyPath =  PropertyUtil.getValueByKey("chatHistory_path");
        historyPath =  System.getProperty("user.dir") + storyPath;
        Path p = Paths.get(historyPath);

        try {
            Files.createFile(p);
        } catch (FileAlreadyExistsException e) {
            logger.info(MessageConstants.HISTORY_FOUND_INFO);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        registers = new File(System.getProperty("user.dir") + PropertyUtil.getValueByKey("serial_path"));
        logger.info(MessageConstants.SERVER_READY_TO_WORK);
    }

    public static String getHistoryPath() {
        return historyPath;
    }

    /**
     * checks if client passed correct register credentials
     * @param msg - register 'XMLMessage' msg from client
     * @return boolean - if credentials are correct
     */
    public static boolean isRegisteredXML(XMLMessage msg) {
        return msg.getHost().equals(HOST) && msg.getPort() == PORT && AVAILABLE_CLIENTS.contains(msg.getToken());
    }

    /**
     * clears the file
     * @param localPath - local path to a file to be cleared
     */
    public static void clearFile(String localPath){
        try {
            FileWriter fwOb = new FileWriter(System.getProperty("user.dir") + localPath, false);
            PrintWriter pwOb = new PrintWriter(fwOb, false);
            pwOb.flush();
            pwOb.close();
            fwOb.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * writes XMLMessage to a responsePath file
     * @param msg - XMLMessage to be sent
     * @param responsePath - file-connector with client
     */
    private static void sendResponse(XMLMessage msg, String responsePath) {
        XMLController.sendMessage(msg,responsePath);
    }

    /**
     * generate XMLMessage and sends it to a client
     * @param responsePath - path to a file-connector with a client
     * @param resp - message to be sent
     * @param code - code of a message to be sent
     */
    public static void sendResponseMessage(String responsePath, String resp, int code){
        XMLMessage res = new XMLMessage(HOST, PORT, TOKEN, resp, code);
        sendResponse(res,responsePath);
        pingStatus(responsePath);
    }

    /**
     * ping status file, that server finished writing
     * @param responsePath - path to a file-connector with client
     */
    public static void pingStatus(String responsePath) {
        responsePath += PropertyUtil.getValueByKey("client_status_suffix");
        responsePath = System.getProperty("user.dir") + responsePath;
        try (FileWriter fw = new FileWriter(responsePath, false)) {
            fw.write(MessageConstants.SERVER_READY_CHAR);
        } catch (IOException ex) {
            logger.info(responsePath);
            logger.error(ex.getMessage());
        }
    }

    /**
     * check if message is not null and register type
     * @param msg - XMLMessage from clint to register user
     * @return true - if message is not null and have correct header, else - false
     */
    private static boolean checkMessage(XMLMessage msg) {
        return (msg != null && msg.getHead().equals(MessageConstants.REGISTER_MESSAGE_HEAD));
    }

    /**
     * check if register message send correct credentials
     * @param msg - XMLMessage from client to register user
     * @return true - if host, port, token corresponds to server credentials, else - false
     */
    private static boolean checkRegister(XMLMessage msg) {
        return (msg.getHost().equals(HOST) && msg.getPort() == PORT && AVAILABLE_CLIENTS.contains(msg.getToken()));
    }

    /**
     * function that checks if a new registration request appeared,
     *  if so creates new client thread
     *  if registration failed sends response of failed registration
     */
    private static void listenRegistersXML() {
        if (registers.length() > 0) {
            XMLMessage msg = XMLController.readMessage(PropertyUtil.getValueByKey("serial_path"));
            if (checkMessage(msg)) {
                clearFile(PropertyUtil.getValueByKey("serial_path"));
                if (checkRegister(msg)) {
                    sendResponseMessage(msg.getMessage(), MessageConstants.ON_REGISTER_SUCCESS, 200);
                    ClientThread th = new ClientThread(msg, logger, historyPath);
                } else if (!msg.getHost().equals(HOST)) {
                    sendResponseMessage(msg.getMessage(), MessageConstants.ON_REGISTER_FAILED, 401);
                } else if (msg.getPort() != PORT) {
                    sendResponseMessage(msg.getMessage(), MessageConstants.ON_REGISTER_FAILED, 402);
                } else if (!AVAILABLE_CLIENTS.contains(msg.getToken())) {
                    sendResponseMessage(msg.getMessage(), MessageConstants.ON_REGISTER_FAILED, 403);
                } else {
                    sendResponseMessage(msg.getMessage(), MessageConstants.ON_REGISTER_FAILED, 400);
                }
                pingStatus(msg.getMessage());
            }
        }
    }

    /**
     * main lifecycle function of a server
     */
    public static void run() {
        while(true) {
            listenRegistersXML();
            try {
                Thread.sleep(TimeConstant.TIME_TO_DELAY);
            } catch (InterruptedException ex) {
                logger.error(MessageConstants.ON_CRITICAL_ERROR);
            }
        }
    }

    public static void main(String[] args) {
        Server sv1 = new Server();
        sv1.run();
    }

}
