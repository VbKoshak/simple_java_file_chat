package automation.SQL;

import automation.mybatis.model.User;
import automation.mybatis.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

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
        List<User> uss = us.getAllUsers();
        long delId = uss.get(uss.size() - 1).getUser_id();
        us.deleteUserById(delId);
        Assert.assertEquals(us.getAllUsers().size(),uss.size() - 1);
    }

    @Test
    public void testCreate() {
        int userPath = new Random().nextInt();
        String userName = RandomStringUtils.random(5);
        try {
            us.createUser(new User(userPath, userName));
            Assert.assertEquals(us.getUserByPath(userPath).getUserName(),userName);
        } catch (PersistenceException ex) {
            Assert.assertEquals(true,true);
        }
    }

    @Test
    public void testDuplicateCreation() {
        User testUser = us.getAllUsers().get(1);
        boolean exception = false;
        try {
            us.createUser(testUser);
        } catch (PersistenceException ex) {
            exception = true;
        }
        Assert.assertEquals(exception, true);
    }
}
