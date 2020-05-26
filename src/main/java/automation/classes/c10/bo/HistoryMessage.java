package automation.classes.c10.bo;

import java.util.List;

public class HistoryMessage extends Package {
    private List<String> resp;
    private int code;

    public HistoryMessage(String host, int port, String token, List<String> resp, int code) {
        super(host, port, token);
        this.resp = resp;
        this.code = code;
    }

    public String getResp() {
        if (resp.size() != 0){
            return "DONE";
        } else {
            return "ERROR";
        }
    }

    public void printHistory() {
        for (String l: resp){
            System.out.println(l);
        }
    }

    @Override
    public String toString() {
        return String.format("%s:%d:%d", super.toString(), this.resp.size(), this.code);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
