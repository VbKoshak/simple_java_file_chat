package automation.io.impl.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "message")
@XmlType(propOrder = { "head", "host", "port", "token", "type", "message", "code", "date" })
public class XMLMessage {
    private String head;
    private String host;
    private int port;
    private String token;
    private String type;
    private String message;
    private int code;
    private Date date;

    public XMLMessage() {

    }

    // ConnectMessage
    public XMLMessage(String host, int port, String token, String message, String type) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.token = token;
        this.message = message;
        this.date = new Date();
        this.head = "ConnectMessage";
    }

    // ResponseMessage
    public XMLMessage(String host, int port, String token, String resp, int code) {
        this.host = host;
        this.port = port;
        this.token = token;
        this.message = resp;
        this.code = code;
        this.date=new Date();
        this.head = "ResponseMessage";
    }

    //RegisterMessage
    public XMLMessage (String host, int port, String token, String  responsePath) {
        this.host = host;
        this.port = port;
        this.token = token;
        this.message = responsePath;
        this.date = new Date();
        this.head = "RegisterMessage";
    }

    public String getHead() {
        return head;
    }

    @XmlElement
    public void setHead(String head) {
        this.head = head;
    }

    public String getHost() {
        return host;
    }

    @XmlElement
    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    @XmlElement
    public void setPort(int port) {
        this.port = port;
    }

    public String getToken() {
        return token;
    }

    @XmlElement
    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    @XmlElement
    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    @XmlElement
    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @XmlElement
    public void setCode(int code) {
        this.code = code;
    }

    public void printHistory() {
        //TODO implement this
    }

    public Date getDate() {
        return date;
    }

    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message [" + head + " " + message + " " + date.toString() + "]";
    }
}
