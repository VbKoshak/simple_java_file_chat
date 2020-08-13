package automation.SQL;

import automation.mybatis.model.Message;
import automation.mybatis.service.MessageService;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class MessageTest {
    private static final Logger LOGGER = Logger.getLogger(MessageTest.class);
    private static MessageService ms;

    static {
        BasicConfigurator.configure();
        ms = new MessageService();
    }

    @Test
    public void testMessageCreate() {
        Message msg = new Message(20,"Hello, world!");
        ms.createMessage(msg);
    }

    @Test
    public void testMessageGetById() {
        long id = ms.getMessageById(1).getUser_id();
        Assert.assertEquals(id, 20);
    }

    @Test void testMessageGetByUserId() {
        List<Message> list = ms.getByUserId(20);
        LOGGER.info(list.get(0).getTm());
        Assert.assertEquals(list.size(), 1);
    }

    @Test void testMessageGetAll() {
        List<Message> list = ms.getAllMessages();
        Assert.assertEquals(list.size(), 2);
    }
}
