package automation.classes.c10;

import automation.classes.c10.bo.ConnectMessage;
import automation.classes.c10.bo.ResponseMessage;
import automation.constant.C10Constant;
import automation.constant.TimeConstant;
import automation.io.interfaces.Packable;
import automation.util.PropertyUtil;
import automation.util.SerializationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

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
    private String responsePath;
    private String host;
    private int port;
    private String token;
    private boolean canConnect = false;


    public Client(String host, int port, String token) {
        this.host = host;
        this.port = port;
        this.token = token;
        clientCount++;
        this.responsePath =  PropertyUtil.getValueByKey("client_path") + clientCount;
        String path =  System.getProperty("user.dir") + this.responsePath;
        Path p = Paths.get(path);
        try {
            Files.createFile(p);
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            canConnect = true;
        }
    }

    public void sendMessage(){
        if (canConnect == true) {
            connect(host, port, token);
            System.out.println(((ResponseMessage) getResponse()).getResp());
        }
    }

    private void connect(final String host, final int port, final String token) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter message: ");
        String msg = in.nextLine();
        Packable pkg = new ConnectMessage(host, port, token, msg, this.responsePath);
        SerializationUtil.writeObject(pkg,PropertyUtil.getValueByKey("serial_path"));
    }

    private Packable getResponse() {
        return SerializationUtil.readObject(this.responsePath);
    }
}
