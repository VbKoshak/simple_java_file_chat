package automation.classes.c10.bo;

public class ConnectMessage extends Package {
    private String message;
    private String responsePath;
    private String type;

    public ConnectMessage(String host, int port, String token, String message,String responsePath) {
        super(host, port, token);
        this.message = message;
        this.responsePath = responsePath;
        this.type = "message";
    }

    public ConnectMessage(String host, int port, String token, String message,String responsePath, String type) {
        super(host, port, token);
        this.message = message;
        this.responsePath = responsePath;
        this.type = "history";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponsePath() {return this.responsePath;}

    public String toString() {
        return String.format("%s:%s:%s", super.toString(), this.message, this.responsePath);
    }

    public String getType() {
        return type;
    }
}
