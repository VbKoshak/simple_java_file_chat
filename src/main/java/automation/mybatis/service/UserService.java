package automation.mybatis.service;

import automation.mybatis.dao.UserDAO;
import automation.mybatis.dao.impl.UserDAOImpl;
import automation.mybatis.model.User;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.List;

public class UserService {
    UserDAO userDAO = new UserDAOImpl();

    public User getUserById(long id) {
        return userDAO.getById(id);
    }

    public List<User> getAllUsers() {
        return userDAO.get();
    }

    public User getUserByPath(long path_id) {return userDAO.getByPath(path_id);}

    public User getUserByLogin(String login) {return userDAO.getByLogin(login);}

    public void createUser(User User) throws PersistenceException {
        userDAO.create(User);
    }

    public void deleteUserById(long id) {
        userDAO.delete(id);
    }

}
