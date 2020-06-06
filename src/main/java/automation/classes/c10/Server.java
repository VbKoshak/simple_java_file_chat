package automation.classes.c10;

import automation.classes.c10.bo.ConnectMessage;
import automation.classes.c10.bo.HistoryMessage;
import automation.classes.c10.bo.RegisterMessage;
import automation.classes.c10.bo.ResponseMessage;
import automation.constant.TimeConstant;
import automation.io.interfaces.Packable;
import automation.util.PropertyUtil;
import automation.util.SerializationUtil;

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

    private static String historyPath;

    static {
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

    }

    public static List<String> getHistory() {
        List<String> resp = new ArrayList<>();
        try {
            File file = new File(historyPath);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                resp.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return resp;
    }

    public static boolean isRegistered(ConnectMessage msg) {
        return msg.getHost().equals(HOST) && msg.getPort() == PORT && AVAILABLE_CLIENTS.contains(msg.getToken());
    }

    private static void listenRegisters() {
        Packable obj = SerializationUtil.readObject(PropertyUtil.getValueByKey("serial_path"));
        if (obj != null) {
            clearFile(PropertyUtil.getValueByKey("serial_path"));
            RegisterMessage msg = ((RegisterMessage) obj);
            Packable resp;
            if (msg.getHost().equals(HOST) && msg.getPort() == PORT && AVAILABLE_CLIENTS.contains(msg.getToken())) {
                resp = new ResponseMessage(HOST, PORT, "", "REGISTER COMPLETE", 200);
                //TODO создание нового thread'a
                ClientThread th = new ClientThread(msg, logger, historyPath);

            } else if (!msg.getHost().equals(HOST)){
                resp = new ResponseMessage(HOST, PORT, "", "REGISTER FAILED", 401);
            } else if (msg.getPort() != PORT){
                resp = new ResponseMessage(HOST, PORT, "", "REGISTER FAILED", 402);
            } else if (!AVAILABLE_CLIENTS.contains(msg.getToken())){
                resp = new ResponseMessage(HOST, PORT, "", "REGISTER FAILED", 403);
            } else {
                resp = new ResponseMessage(HOST, PORT, "", "REGISTER FAILED", 400);
            }
            sendResponse(resp, msg.getResponsePath());
            pingStatus(msg.getResponsePath());
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

    private static void sendResponse(Packable pkg, String responsePath) {
        SerializationUtil.writeObject(pkg,responsePath);
    }

    public static void sendResponseMessage(String responsePath, String resp, int code){
        Packable res = new ResponseMessage(HOST, PORT, "", resp, code);
        sendResponse(res,responsePath);
        pingStatus(responsePath);
    }

    public static void sendResponseMessage(String responsePath, List<String> resp, int code ) {
        Packable res = new HistoryMessage(HOST, PORT, "", resp, code);
        sendResponse(res, responsePath);
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

    public static void main(String[] args) {
        while(true) {
            listenRegisters();
            try {
                Thread.sleep(TimeConstant.TIME_TO_DELAY);
            } catch (InterruptedException ex) {
                logger.error("Critical error");
            }
        }
    }
}
