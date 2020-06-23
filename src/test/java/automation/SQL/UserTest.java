package automation.SQL;

import automation.mybatis.model.User;
import automation.mybatis.service.UserService;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class UserTest {
    private static final Logger LOGGER = Logger.getLogger(UserTest.class);
    private static UserService us;

    static {
        BasicConfigurator.configure();
        us = new UserService();
    }

    @Test
    public void testUserGetById() {
        User a = us.getUserById(1);
        Assert.assertEquals(a.getPath_id(), 123);
    }

    @Test
    public void testUserGetByPath() {
        User a = us.getUserByPath(456);
        Assert.assertEquals(a.getUserName(),"god");
    }

    @Test
    public void testUserGet() {
        List<User> a = us.getAllUsers();
        a.forEach(user -> LOGGER.info(user.getUserName()));
        Assert.assertEquals(a.get(1).getUserName(),"god");
    }

    @Test
    public void testDelete() {
        us.deleteUserById(3);
        Assert.assertEquals(us.getAllUsers().size(),2);
    }

    @Test
    public void testCreate() {
        int userPath = 1238;
        String userName = "ceaki";
        us.createUser(new User(userPath,userName));
        Assert.assertEquals(us.getUserByPath(userPath).getUserName(),userName);
    }
}
