package automation.classes.c10;

import automation.classes.c10.bo.ConnectMessage;
import automation.classes.c10.bo.HistoryMessage;
import automation.classes.c10.bo.ResponseMessage;
import automation.constant.TimeConstant;
import automation.io.exception.UnableToReadException;
import automation.io.impl.file.StreamTextFileReader;
import automation.io.interfaces.Packable;
import automation.util.PropertyUtil;
import automation.util.SerializationUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getSimpleName());

    private static final List<String> AVAILABLE_CLIENTS = Arrays.asList("user","admin");
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;

    private static String historyPath;

    private static Set<String> badWords;

    static {
        badWords = new HashSet<String>();
        try {
            String[] strArr = (new StreamTextFileReader(System.getProperty("user.dir") + PropertyUtil.getValueByKey("badWord_path"))).read().split(",");
            for(String el : strArr) {
                badWords.add(el);
            }
            LOGGER.info("BadWord list loaded");
        } catch (UnableToReadException ex) {
            System.out.println(ex.getMessage());
        }
        String storyPath =  PropertyUtil.getValueByKey("chatHistory_path");
        historyPath =  System.getProperty("user.dir") + storyPath;
        Path p = Paths.get(historyPath);
        try {
            Files.createFile(p);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        LOGGER.info(String.format("Listening on %s:%d", HOST, PORT));

        while (true) {
            try {
                listen();
                Thread.sleep(TimeConstant.TIME_TO_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static String formatMsg(String msg) {
        String[] returnMsg = msg.split(" ");

        int length = returnMsg.length;
        for (int i = 0; i < length; i++) {
            if(badWords.contains(returnMsg[i].toUpperCase())) {
                returnMsg[i] = "***";
            }
        }
        return String.join(" ", returnMsg);
    }

    private static List<String> getHistory() {
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
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return resp;
    }

    // TODO: filter msgs
    private static void listen() {
        Packable obj = SerializationUtil.readObject(PropertyUtil.getValueByKey("serial_path"));
        if (obj != null) {
            ConnectMessage msg = ((ConnectMessage) obj);
            Packable resp;
            if (msg.getHost().equals(HOST) && msg.getPort() == PORT && AVAILABLE_CLIENTS.contains(msg.getToken())) {
                if (msg.getType().equals("message")) {
                    String mess = formatMsg(msg.getMessage());
                    try (FileWriter fw = new FileWriter(historyPath, true)) {
                        fw.write("\n" + mess);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                    LOGGER.info(mess);
                    resp = new ResponseMessage(HOST, PORT, "", "SUCCESS", 200);
                } else if (msg.getType().equals("history")) {
                    List<String> hstry = getHistory();
                    resp = new HistoryMessage(HOST,PORT,"",hstry,201);
                } else {
                    resp = new ResponseMessage(HOST, PORT, "", "CONNECTION FAILED", 410);
                }
                clearSerial();
            } else if (!msg.getHost().equals(HOST)){
                resp = new ResponseMessage(HOST, PORT, "", "CONNECTION FAILED", 401);
            } else if (msg.getPort() != PORT){
                resp = new ResponseMessage(HOST, PORT, "", "CONNECTION FAILED", 402);
            } else if (!AVAILABLE_CLIENTS.contains(msg.getToken())){
                resp = new ResponseMessage(HOST, PORT, "", "CONNECTION FAILED", 403);
            } else {
                resp = new ResponseMessage(HOST, PORT, "", "CONNECTION FAILED", 400);
            }
            sendResponse(resp,msg.getResponsePath());
        }
    }

    private static void clearSerial(){
        try {
            FileWriter fwOb = new FileWriter(System.getProperty("user.dir") + PropertyUtil.getValueByKey("serial_path"), false);
            PrintWriter pwOb = new PrintWriter(fwOb, false);
            pwOb.flush();
            pwOb.close();
            fwOb.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void sendResponse(Packable pkg, String responsePath) {
        SerializationUtil.writeObject(pkg,responsePath);
    }
}
