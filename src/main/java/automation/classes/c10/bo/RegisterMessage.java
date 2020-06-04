package automation.classes.c10.bo;

public class RegisterMessage extends Package {
    private String responsePath;

    public RegisterMessage(String host, int port, String token,String responsePath) {
        super(host, port, token);
        this.responsePath = responsePath;
    }
    public String getResponsePath() {return this.responsePath;}

    public String toString() {
        return String.format("%s:%s", super.toString(), this.responsePath);
    }
}
