package automation.mybatis.model;

import java.util.Date;

public class Message extends AbstractEntity{
    private int id;
    private int user_id;
    private String message;
    private Date tm;



    public Message() {

    }

    public Message(int user_id, String message, Date tm) {
        this.user_id = user_id;
        this.message = message;
        this.tm = tm;
    }

    public Message(int user_id, String message) {
        this.message = message;
        this.user_id = user_id;
    }


    @Override
    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
