package automation.classes.c10;

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



public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getSimpleName());

    private static final List<String> AVAILABLE_CLIENTS = Arrays.asList("user","admin");
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;


    private static ClientThreadManager clm;
    private static String historyPath;
    private final static String TOKEN = "server";
    private final static File registers;

    static {
        clm = new ClientThreadManager();
        BasicConfigurator.configure();
        String storyPath =  PropertyUtil.getValueByKey("chatHistory_path");
        historyPath =  System.getProperty("user.dir") + storyPath;
        Path p = Paths.get(historyPath);
        try {
            Files.createFile(p);
        } catch (FileAlreadyExistsException e) {
            logger.info("Loading history");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        registers = new File(System.getProperty("user.dir")+PropertyUtil.getValueByKey("serial_path"));
    }

    public static String getHistoryPath() {
        return historyPath;
    }

    public static boolean isRegisteredXML(XMLMessage msg) {
        return msg.getHost().equals(HOST) && msg.getPort() == PORT && AVAILABLE_CLIENTS.contains(msg.getToken());
    }

    private static void listenRegistersXML() {
        if (registers.length() > 0) {
            XMLMessage msg = XMLController.readMessage(PropertyUtil.getValueByKey("serial_path"));
            if (msg != null && msg.getHead().equals("RegisterMessage")) {
                clearFile(PropertyUtil.getValueByKey("serial_path"));
                if (msg.getHost().equals(HOST) && msg.getPort() == PORT && AVAILABLE_CLIENTS.contains(msg.getToken())) {
                    sendResponseMessage(msg.getMessage(), "REGISTER COMPLETE", 200);
                    //TODO создание нового thread'a
                    ClientThread th = new ClientThread(msg, logger, historyPath);

                } else if (!msg.getHost().equals(HOST)) {
                    sendResponseMessage(msg.getMessage(), "REGISTER FAILED", 401);
                } else if (msg.getPort() != PORT) {
                    sendResponseMessage(msg.getMessage(), "REGISTER FAILED", 402);
                } else if (!AVAILABLE_CLIENTS.contains(msg.getToken())) {
                    sendResponseMessage(msg.getMessage(), "REGISTER FAILED", 403);
                } else {
                    sendResponseMessage(msg.getMessage(), "REGISTER FAILED", 400);
                }
                pingStatus(msg.getMessage());
            }
        }
    }

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

    private static void sendResponse(XMLMessage msg, String responsePath) {
        XMLController.sendMessage(msg,responsePath);
    }

    public static void sendResponseMessage(String responsePath, String resp, int code){
        XMLMessage res = new XMLMessage(HOST, PORT, TOKEN, resp, code);
        sendResponse(res,responsePath);
        pingStatus(responsePath);
    }

    public static void pingStatus(String responsePath) {
        responsePath += PropertyUtil.getValueByKey("client_status_suffix");
        responsePath = System.getProperty("user.dir") + responsePath;
        try (FileWriter fw = new FileWriter(responsePath, false)) {
            fw.write("s");
        } catch (IOException ex) {
            logger.info(responsePath);
            logger.error(ex.getMessage());
        }
    }

    public static void run() {
        while(true) {
            listenRegistersXML();
            try {
                Thread.sleep(TimeConstant.TIME_TO_DELAY);
            } catch (InterruptedException ex) {
                logger.error("Critical error");
            }
        }
    }

    public static void main(String[] args) {
        Server sv1 = new Server();
        sv1.run();
    }

}
