package automation.mybatis.dao;


import automation.mybatis.model.User;

import java.util.List;

public interface UserDAO {
    void create(User user);
    User getById(long id);
    User getByPath(long  path_id);
    User getByLogin(String login);
    List<User> get();
    void delete(long id);
}
