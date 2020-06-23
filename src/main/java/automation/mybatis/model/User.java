package automation.mybatis.model;

public class User extends AbstractEntity{
    private long user_id;
    private long path_id;
    private String userName;

    public User(long id, long path_id, String userName) {
        this.path_id = path_id;
        this.user_id = id;
        this.userName = userName;
    }

    public User(long path_id, String userName) {
        this.path_id = path_id;
        this.userName = userName;
    }

    public User() {

    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(int id) {
        this.user_id = id;
    }

    public long getPath_id() {
        return path_id;
    }

    public void setPath_id(int path_id) {
        this.path_id = path_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
