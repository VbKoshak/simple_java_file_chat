package automation.SQL;

import automation.mybatis.model.User;
import automation.mybatis.service.UserService;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserTest {
    private static final Logger LOGGER = Logger.getLogger(UserTest.class);

    static {
        BasicConfigurator.configure();
    }

    @Test
    public void testAuthorGetById() {
        User a = new UserService().getUserById(1);
        LOGGER.info(a.toString());
        Assert.assertEquals(a.getPath_id(), 123);
    }
}
