package automation.mybatis.dao;

import automation.mybatis.model.Message;

import java.util.List;

public interface MessageDAO {
    void create(Message msg);
    Message getById(long id);
    List<Message> getByUserId(long id);
    List<Message> get();
}
