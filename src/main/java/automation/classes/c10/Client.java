package automation.classes.c10;

import automation.classes.c10.bo.ConnectMessage;
import automation.classes.c10.bo.HistoryMessage;
import automation.classes.c10.bo.RegisterMessage;
import automation.classes.c10.bo.ResponseMessage;
import automation.constant.TimeConstant;
import automation.io.impl.file.TextFileReader;
import automation.io.interfaces.Packable;
import automation.util.PropertyUtil;
import automation.util.SerializationUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public class Client {
    private static int clientCount = 0;
    private static final Logger logger = Logger.getLogger(Server.class.getSimpleName());
    private String responsePath;
    private String fullResponsePath;
    private String statusPath;
    private String fullStatusPath;
    private String host;
    private int port;
    private String token;
    private boolean canConnect = false;
    private int id;
    private File statusF;
    private TextFileReader statusTFR;
    private boolean going = true;

    public Client() {
        Scanner in = new Scanner(System.in);
        System.out.println("enter params: ");
        System.out.println("HOST: ");
        String host = in.nextLine();
        System.out.println("Port: ");
        int port = in.nextInt();
        in.nextLine();
        System.out.println("token: ");
        String token = in.nextLine();


        this.host = host;
        this.port = port;
        this.token = token;
        id = ++clientCount;
        this.responsePath =  PropertyUtil.getValueByKey("client_path") + id;
        this.fullResponsePath =  System.getProperty("user.dir") + this.responsePath;
        BasicConfigurator.configure();
        statusPath = this.responsePath + PropertyUtil.getValueByKey("client_status_suffix");
        this.fullStatusPath =  System.getProperty("user.dir") + this.statusPath;
        this.statusF = new File(fullStatusPath);
        this.statusTFR = new TextFileReader(statusF);

        boolean s1 = false;
        boolean s2 = false;
        try {
            Files.createFile(Paths.get(fullResponsePath));
        } catch (FileAlreadyExistsException e) {

        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            s1 = true;
            if (s2 = true) {
                canConnect = true;
            }
        }

        try {
            Files.createFile(Paths.get(System.getProperty("user.dir") + this.statusPath));
        } catch (FileAlreadyExistsException e) {

        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            s2 = true;
            if (s1 == true) {
                canConnect = true;
            }
        }

        canConnect = true;
    }

    public String sendMessage(){
        String responseMsg = "CONNECTION FAILED";
        Scanner in = new Scanner(System.in);
        System.out.println("Enter message: ");
        String msg = in.nextLine();

        if (canConnect == true) {
            connect(host, port, token, msg);
            try {
                Thread.sleep(TimeConstant.TIME_TO_DELAY);
                boolean doit = true;
                Packable obj = null;
                while (doit){
                    if (statusF.length() > 0 && statusTFR.read().equals("s")) {
                        Server.clearFile(statusPath);
                        obj = getResponse();
                        Server.clearFile(responsePath);
                        if (obj != null && (obj.getClass().getSimpleName().equals("HistoryMessage") || obj.getClass().getSimpleName().equals("ResponseMessage"))) {
                            doit = false;
                        }
                    }
                }
                if (msg.equals(":history")) {
                    ((HistoryMessage) obj).printHistory();
                    responseMsg = ((HistoryMessage) obj).getResp();
                } else if (msg.equals(":close")) {
                    going = false;
                } else {
                    responseMsg = ((ResponseMessage) obj).getResp();
                }
            } catch (Exception ex) {
               logger.error(ex.getMessage());
            }
        }
        return responseMsg;
    }

    private void connect(final String host, final int port, final String token, String msg) {
        Packable pkg;
        if (msg.equals(":history")){
            pkg = new ConnectMessage(host, port, token, msg, this.responsePath, "history");
        } else if (msg.equals(":close")) {
            pkg = new ConnectMessage(host, port, token, msg, this.responsePath, "close");
        } else {
            pkg = new ConnectMessage(host, port, token, msg, this.responsePath);
        }
        SerializationUtil.writeObject(pkg, this.responsePath);
        pingStatus();
    }

    private void pingStatus() {
        try (FileWriter fw = new FileWriter(fullStatusPath, false)) {
            fw.write("c");
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    private Packable getResponse() {
        Packable ans = SerializationUtil.readObject(this.responsePath);
        return ans;
    }

    private boolean register() {
        Packable request = new RegisterMessage(host, port, token, responsePath);
        SerializationUtil.writeObject(request, PropertyUtil.getValueByKey("serial_path"));
        boolean ans = true;
        while (ans) {
            if (statusF.length() > 0){
                ans = false;
            }
        }
        try {
            Packable resp = getResponse();
            Server.clearFile(responsePath);
            if (resp != null) {
                int responseCode = (((ResponseMessage) resp).getCode());
                if (responseCode == 200) {
                    ans = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR #1");
            logger.error(ex.getMessage());
        }

        return ans;
    }

    public void start() {
        while (canConnect == false) {
            //Waiting to create files
        }
        boolean result = this.register();

        if (result == true) {
            logger.info("Registration complete");
            String resp;
            do {
                resp = this.sendMessage();
            } while(!resp.equals("CONNECTION FAILED") && going);
        } else {
            //TODO make more descriptive answer according to codes
            logger.error("Registration failed");
        }

        if(going == true) {
            sendCloseMessage();
        }
    }

    private void sendCloseMessage() {
        Packable pkg;
        pkg = new ConnectMessage(host, port, token, "disconnect", this.responsePath, "close");
        SerializationUtil.writeObject(pkg, this.responsePath);
        pingStatus();
    }

    public int getId() {
        return this.id;
    }

    public Logger getLogger(){
        return this.logger;
    }
}
