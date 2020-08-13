package automation.classes.c10;

import automation.constant.CommandConstants;
import automation.constant.MessageConstants;
import automation.constant.TimeConstant;
import automation.io.impl.file.TextFileReader;
import automation.io.impl.xml.XMLController;
import automation.io.impl.xml.XMLMessage;
import automation.util.PropertyUtil;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 * class representing client side of a chat
 *  1) app sends a register request to "serial_path" file, which includes
 *      - host, port, token for auth (typed by user or passed in constructor)
 *      - response path, which consists of predefined 'client_path' and random number
 *  2) if auth credentials are correct server sends a respond and futher talk between server and client uses 2 files
 *      - response path, described in register message
 *      - status path, the same with response but with 'client_status_suffix' in the end, showing that file is ready 4 work
 *  3) lifecycle is simple: client sends msg and gets responses from server, there is 2 reserved msgs
 *      - :history - will ask server to show chat history and print it
 *      - :close - ends connection between server and client
 */
public class Client {
    /**
     * logger - Logger for client output
     * responsePath - Path of a file 4 communication with server
     * fullResponsePath - Full path of a file 4 communication with server
     * statusPath - Path of a file-marker of response status
     * fullStatusPath - Full path of a file-marker of response status
     *
     * host, port, token - authorisation credentials
     * canConnect - flag, true if all files are created and ready 4 work
     * id - randomly generated number for a client
     *
     * statusF - link to a file of "fullResponsePath"
     * statusTFR - File reader for "statusF"
     *
     * going - flag, showing if current client is working
     */
    private Logger logger;
    private String responsePath;
    private String fullResponsePath;
    private String statusPath;
    private String fullStatusPath;
    private String host;
    private String login;
    private int port;
    private String token;
    private boolean canConnect = false;
    private int id;
    private File statusF;
    private TextFileReader statusTFR;
    private boolean going = true;

    /**
     * basic constructor with passed input
     *
     * @param host - host of a server to connect
     * @param port - port of a server to connect
     * @param token - token to connect to server
     */
    public Client(String host, int port, String token, String login) {

        this.host = host;
        this.port = port;
        this.token = token;

        init();
    }

    /**
     * basic constructor with terminal-based input, where
     * host - host of a server to connect
     * port - port of a server to connect
     * token - token to connect to server
     */
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
        System.out.println("login: ");
        String login = in.nextLine();


        this.host = host;
        this.port = port;
        this.token = token;
        this.login = login;

        init();
    }

    /**
     * Method to create id (almost unique) for user
     * @return randomly generated id for a user
     */
    private int getRandomID() {
        return new Random().nextInt() + 1;
    }

    /**
     * function, that creates files for communication with server, or clears if such files exist
     * if everything is ok, sets 'canConnect' flag to true
     */
    private void initFiles(){
        boolean s1 = false;
        boolean s2 = false;
        try {
            Files.createFile(Paths.get(fullResponsePath));
        } catch (FileAlreadyExistsException e) {
            automation.classes.c10.Server.clearFile(statusPath);
            automation.classes.c10.Server.clearFile(responsePath);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            s1 = true;
            if (s2 = true) {
                canConnect = true;
            }
        }

        try {
            Files.createFile(Paths.get(fullResponsePath));
        } catch (FileAlreadyExistsException e) {
            Server.clearFile(statusPath);
            Server.clearFile(responsePath);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            s2 = true;
            if (s1 == true) {
                canConnect = true;
            }
        }
    }

    /**
     * initialize user with all necessary variables + creating files for communication with 'initFiles'
     */
    private void init() {
        id = getRandomID();
        this.logger = Logger.getLogger(Server.class.getSimpleName() + id);
        this.responsePath =  PropertyUtil.getValueByKey("client_path") + id;
        this.fullResponsePath =  System.getProperty("user.dir") + this.responsePath;
        BasicConfigurator.configure();
        statusPath = this.responsePath + PropertyUtil.getValueByKey("client_status_suffix");
        this.fullStatusPath =  System.getProperty("user.dir") + this.statusPath;
        this.statusF = new File(fullStatusPath);
        this.statusTFR = new TextFileReader(statusF);

        initFiles();
    }

    /**
     * Prints to terminal chat history
     * @param historyPath - path  to a history File, gained by server after request
     */
    private  void printHistory(String history) {
        System.out.println(history);
    }

    /**
     * fills a status  file with a 'CLIENT_READY_CHAR' to show that client finished to work with response file
     */
    private void pingStatus() {
        try (FileWriter fw = new FileWriter(fullStatusPath, false)) {
            fw.write(MessageConstants.CLIENT_READY_CHAR);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * sends message, after writing file is done, marks is in status file with 'pingStatus'
     * @param msg - String msg to be sent
     */
    private void sendMessage(String msg) {
        XMLMessage xmm;
        if (msg.equals(CommandConstants.HISTORY_REQUEST_MESSAGE)){
            xmm = new XMLMessage(host, login, port, token, msg, MessageConstants.HISTORY_MESSAGE_TYPE);
        } else if (msg.equals(CommandConstants.CLOSE_REQUEST_MESSAGE)) {
            xmm = new XMLMessage(host, login, port, token, msg, MessageConstants.CLOSE_MESSAGE_TYPE);
        } else {
            xmm = new XMLMessage(host, login, port, token, msg, MessageConstants.SIMPLE_MESSAGE_TYPE);
        }
        XMLController.sendMessage(xmm,responsePath);
        pingStatus();
    }

    /**
     * reads response from response file and returnes XMLMessage from it
     * @return XMLMESSAGE obj - response from server
     */
    private XMLMessage getResponseXML() {
        XMLMessage ans = XMLController.readMessage(this.responsePath);
        return ans;
    }

    /**
     * sends message to server with 'sendMessage' and waits until server sends response, returnes the message of response
     * @param msg - String - msg to be sent
     * @return String - text reponse from server
     */
    public String connect(String msg){
        String responseMsg = MessageConstants.CONNECTION_FAILED_INFO;
        if (canConnect == true) {
            sendMessage(msg);
            try {
                boolean doit = true;
                XMLMessage obj = null;
                while (doit){
                    if (statusF.length() > 0 && statusTFR.read().equals(MessageConstants.SERVER_READY_CHAR)) {
                        obj = getResponseXML();
                        Server.clearFile(responsePath);
                        Server.clearFile(statusPath);
                        if (obj != null && (obj.getHead().equals(MessageConstants.HISTORY_MESSAGE_HEAD) || obj.getHead().equals(MessageConstants.RESPONSE_MESSAGE_HEAD))) {
                            doit = false;
                        }
                    }
                }
                if (msg.equals(CommandConstants.HISTORY_REQUEST_MESSAGE)) {
                    String history = obj.getMessage();
                    printHistory(history);
                    responseMsg = MessageConstants.CONNECTION_SUCCESS_INFO;
                } else if (msg.equals(CommandConstants.CLOSE_REQUEST_MESSAGE)) {
                    going = false;
                } else {
                    responseMsg = obj.getMessage();
                }
            } catch (Exception ex) {
               logger.error(ex.getMessage());
            }
        }
        return responseMsg;
    }

    /**
     * sends a registering message to a server
     * @return msg from server
     */
    public boolean registerXML(){
        boolean ans;
        boolean time;
        do {
            XMLMessage request = new XMLMessage(host, login, port, token, responsePath);
            XMLController.sendMessage(request, PropertyUtil.getValueByKey("serial_path"));
            long reqT = System.currentTimeMillis();

            ans = true;
            time = true;
            while (ans && time) {
                time = reqT + TimeConstant.REPLY_DELAY > System.currentTimeMillis();
                if (statusF.length() > 0) {
                    ans = false;
                    time = true;
                }
            }
        } while (!time);
        try {
            XMLMessage resp = XMLController.readMessage(responsePath);
            if (resp.getLogin().equals(login)) {
                Server.clearFile(responsePath);
                Server.clearFile(statusPath);
                if (resp != null) {
                    int responseCode = resp.getCode();
                    if (responseCode == 200) {
                        ans = true;
                    }
                }
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            System.out.println(MessageConstants.FAILED_REGISTRATION_MESSAGE);
        }

        return ans;
    }

    /**
     * sends a request for server to close connection
     */
    private void sendCloseMessageXML() {
        XMLMessage msg = new XMLMessage(host, login, port, token, MessageConstants.ON_CONNECT_CLOSE, MessageConstants.CLOSE_MESSAGE_TYPE);
        XMLController.sendMessage(msg,responsePath);
        pingStatus();
    }

    /**
     * starts the life of a client from register to close message
     */
    public void start() {
        while (canConnect == false) {
            //Waiting to create files
        }
        boolean result = this.registerXML();

        if (result == true) {
            Scanner in = new Scanner(System.in);
            logger.info(MessageConstants.SUCCESSFUL_REGISTRATION_MESSAGE);
            String resp;
            do {
                System.out.println(MessageConstants.READY_MESSAGE_INPUT_INFO);
                String msg = in.nextLine();
                resp = this.connect(msg);
            } while(!resp.equals(MessageConstants.FAILED_REGISTRATION_MESSAGE) && going);
        } else {
            logger.error(MessageConstants.FAILED_REGISTRATION_MESSAGE);
        }

        if(going == true) {
            sendCloseMessageXML();
        }
    }


    public Logger getLogger(){
        return this.logger;
    }

    public static void main(String[] args) {
        Client cl1 = new Client();
        cl1.start();
    }
}
