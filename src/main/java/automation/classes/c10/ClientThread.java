package automation.classes.c10;

import automation.classes.c10.bo.ConnectMessage;
import automation.classes.c10.bo.RegisterMessage;
import automation.constant.TimeConstant;
import automation.filters.FilterList;
import automation.filters.impl.BadWordFilter;
import automation.filters.impl.NameFilter;
import automation.filters.impl.SentenceFilter;
import automation.filters.impl.SpaceFilter;
import automation.io.exception.UnableToReadException;
import automation.io.impl.file.TextFileReader;
import automation.io.interfaces.Packable;
import automation.util.PropertyUtil;
import automation.util.SerializationUtil;
import com.vdurmont.emoji.EmojiParser;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ClientThread extends Thread {

    private Client client;
    private String responsePath;
    private String statusPath;
    private String fullStatusPath;
    private Logger logger;

    private boolean running = true;

    private FilterList fls;
    private String historyPath;
    private File statusF;
    private TextFileReader statusTFR;

    public ClientThread(RegisterMessage msg, Logger logger, String historyPath) {
        super(msg.getToken());
        this.logger = logger;
        this.historyPath = historyPath;
        logger.info("new thread created");
        this.responsePath = msg.getResponsePath();
        this.statusPath = msg.getResponsePath() + PropertyUtil.getValueByKey("client_status_suffix");
        this.fullStatusPath =  System.getProperty("user.dir") + this.statusPath;

        this.statusF = new File(fullStatusPath);
        this.statusTFR = new TextFileReader(statusF);

        fls = new FilterList();
        fls.add(new SpaceFilter("Spaces",logger));
        fls.add(new SentenceFilter("Sentences",logger));
        fls.add(new BadWordFilter(PropertyUtil.getValueByKey("badWord"), logger));
        fls.add(new NameFilter(PropertyUtil.getValueByKey("names"), logger));
        fls.add(new NameFilter(PropertyUtil.getValueByKey("geoNames"), logger));

        start();
    }

    private void listen() {
        if (statusF.length() > 0) {
            try {
            if (statusTFR.read().equals("c")) {
                Packable obj = SerializationUtil.readObject(this.responsePath);
                Server.clearFile(statusPath);
                Server.clearFile(responsePath);
                if (obj != null && obj.getClass().getSimpleName().equals("ConnectMessage")) {
                    ConnectMessage msg = ((ConnectMessage) obj);
                    Packable resp;
                    if (Server.isRegistered(msg)) {
                        if (msg.getType().equals("message")) {
                            String mess = fls.formatString(msg.getMessage());
                            mess = EmojiParser.parseToUnicode(mess);
                            try (FileWriter fw = new FileWriter(historyPath, true)) {
                                fw.write("\n" + mess);
                            } catch (IOException ex) {
                                logger.error(ex.getMessage());
                            }
                            logger.info(mess);
                            Server.sendResponseMessage(responsePath, "SUCCESS", 200);
                        } else if (msg.getType().equals("close")) {
                            //TODO close connection
                            running = false;
                            Server.sendResponseMessage(responsePath, "SUCCESS", 200);
                        } else if (msg.getType().equals("history")) {
                            List<String> hstry = Server.getHistory();
                            Server.sendResponseMessage(responsePath, hstry, 201);
                        } else {
                            Server.sendResponseMessage(responsePath, "CONNECTION FAILED", 410);
                        }
                    } else {
                        Server.sendResponseMessage(responsePath, "CONNECTION FAILED", 410);
                    }
                }
            }
            Thread.sleep(TimeConstant.TIME_TO_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnableToReadException e) {
                logger.error("Can not read file");
            }
        }
    }

    @Override
    public void run() {

        while (running) {
            listen();
        }

    }
}
