package automation.classes.c10;

import automation.classes.c10.bo.ConnectMessage;
import automation.classes.c10.bo.HistoryMessage;
import automation.classes.c10.bo.ResponseMessage;
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
    private String host;
    private int port;
    private String token;
    private boolean canConnect = false;

    private void clearResponse(){
        try {
            FileWriter fwOb = new FileWriter(System.getProperty("user.dir") + this.responsePath, false);
            PrintWriter pwOb = new PrintWriter(fwOb, false);
            pwOb.flush();
            pwOb.close();
            fwOb.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Client(String host, int port, String token) {
        this.host = host;
        this.port = port;
        this.token = token;
        clientCount++;
        this.responsePath =  PropertyUtil.getValueByKey("client_path") + clientCount;
        String path =  System.getProperty("user.dir") + this.responsePath;
        Path p = Paths.get(path);
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
                Thread.sleep(500);
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
        } else {
            pkg = new ConnectMessage(host, port, token, msg, this.responsePath);
        }
        SerializationUtil.writeObject(pkg, PropertyUtil.getValueByKey("serial_path"));
    }

    private Packable getResponse() {
        Packable ans = SerializationUtil.readObject(this.responsePath);
        return ans;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("enter params: ");
        System.out.println("HOST: ");
        String host = in.nextLine();
        System.out.println("Port: ");
        int port = in.nextInt();
        in.nextLine();
        System.out.println("token: ");
        String token = in.nextLine();

        Client cl = new Client(host, port, token);
        String resp;
        do {
            resp = cl.sendMessage();
        } while(!resp.equals("CONNECTION FAILED"));
        logger.info("Connection closed");
    }
}
