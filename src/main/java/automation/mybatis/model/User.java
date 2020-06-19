package automation.mybatis.model;

public class User extends AbstractEntity{
    private int id;
    private int path_id;

    public User(int id, int path_id) {
        this.path_id = path_id;
        this.id = id;
    }

    public User() {

    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPath_id() {
        return path_id;
    }

    public void setPath_id(int path_id) {
        this.path_id = path_id;
    }
}
