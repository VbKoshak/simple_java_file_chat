package automation.classes.c10;

import automation.classes.c10.bo.ConnectMessage;
import automation.classes.c10.bo.HistoryMessage;
import automation.classes.c10.bo.RegisterMessage;
import automation.classes.c10.bo.ResponseMessage;
import automation.constant.TimeConstant;
import automation.io.interfaces.Packable;
import automation.util.PropertyUtil;
import automation.util.SerializationUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;


/**
 * 1. object streams
 * 2. task
 * 3. swap strings
 * 33. loggers (stdin, stdout, stderr)
 * 4. refactoring
 * 5. fixes
 */
public class Client {
    private static int clientCount = 0;
    private static final Logger logger = Logger.getLogger(Server.class.getSimpleName());
    private String responsePath;
    private String fullResponsePath;
    private String host;
    private int port;
    private String token;
    private boolean canConnect = false;
    private int id;

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
        Path p = Paths.get(fullResponsePath);
        BasicConfigurator.configure();

        try {
            Files.createFile(p);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Welcome Back!");
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            canConnect = true;
        }
    }

    public String sendMessage(){
        String responseMsg = "CONNECTION FAILED";
        Scanner in = new Scanner(System.in);
        System.out.println("Enter message: ");
        String msg = in.nextLine();

        if (canConnect == true) {
            connect(host, port, token, msg);
            try {
                boolean doit = true;
                while (doit){
                    Packable obj = SerializationUtil.readObject(this.responsePath);
                    if (obj != null && (obj.getClass().getSimpleName().equals("HistoryMessage") || obj.getClass().getSimpleName().equals("ResponseMessage"))) {
                        doit = false;
                    }
                }
                if (msg.equals(":history")) {
                    ((HistoryMessage) getResponse()).printHistory();
                    responseMsg = ((HistoryMessage) getResponse()).getResp();
                } else {
                    responseMsg = (((ResponseMessage) getResponse()).getResp());
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
    }

    private Packable getResponse() {
        try {
            Thread.sleep(TimeConstant.TIME_TO_DELAY);
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage());
        }
        Packable ans = SerializationUtil.readObject(this.responsePath);
        return ans;
    }

    private boolean register() {
        Packable request = new RegisterMessage(host, port, token, responsePath);
        SerializationUtil.writeObject(request, PropertyUtil.getValueByKey("serial_path"));
        boolean ans = false;
        try {
            Packable resp = getResponse();
            if (resp != null) {
                int responseCode = (((ResponseMessage) resp).getCode());
                if (responseCode == 200) {
                    ans = true;
                }
                Server.clearFile(responsePath);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR #1");
            logger.error(ex.getMessage());
        }

        return ans;
    }

    public void start() {
        boolean result = this.register();

        if (result == true) {
            logger.info("Registration complete");
            String resp;
            do {
                resp = this.sendMessage();
            } while(!resp.equals("CONNECTION FAILED"));
        } else {
            //TODO make more descriptive answer according to codes
            logger.error("Registration failed");
        }

        sendCloseMessage();
    }

    private void sendCloseMessage() {

    }

    public int getId() {
        return this.id;
    }

    public Logger getLogger(){
        return this.logger;
    }
}
