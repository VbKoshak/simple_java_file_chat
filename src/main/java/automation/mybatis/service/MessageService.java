package automation.mybatis.service;

import automation.mybatis.dao.MessageDAO;
import automation.mybatis.dao.impl.MessageDAOImpl;
import automation.mybatis.model.Message;

import java.util.List;

public class MessageService {
    MessageDAO messageDAO = new MessageDAOImpl();

    public Message getMessageById(long id) {
        return messageDAO.getById(id);
    }

    public List<Message> getAllMessages() {
        return messageDAO.get();
    }

    public List<Message> getByUserId(long id) { return messageDAO.getByUserId(id); }

    public void createMessage(Message message) {
        messageDAO.create(message);
    }

}
