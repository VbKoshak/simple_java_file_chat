package automation.classes.c10;

import automation.classes.c10.bo.ConnectMessage;
import automation.classes.c10.bo.ResponseMessage;
import automation.constant.TimeConstant;
import automation.io.exception.UnableToReadException;
import automation.io.impl.file.StreamTextFileReader;
import automation.io.interfaces.Packable;
import automation.util.PropertyUtil;
import automation.util.SerializationUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getSimpleName());

    private static final List<String> AVAILABLE_CLIENTS = Arrays.asList("user","admin");
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;

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

    // TODO: filter msgs
    private static void listen() {
        Packable obj = SerializationUtil.readObject(PropertyUtil.getValueByKey("serial_path"));
        if (obj != null) {
            ConnectMessage msg = ((ConnectMessage) obj);
            Packable resp;
            if (msg.getHost().equals(HOST) && msg.getPort() == PORT && AVAILABLE_CLIENTS.contains(msg.getToken())) {
                String mess = formatMsg(msg.getMessage());
                LOGGER.info(mess);
                resp = new ResponseMessage(HOST, PORT, "", "SUCCESS", 200);
                clearSerial();
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
