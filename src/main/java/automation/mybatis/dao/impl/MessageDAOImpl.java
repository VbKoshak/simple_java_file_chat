package automation.mybatis.dao.impl;

import automation.mybatis.config.SessionFactory;
import automation.mybatis.dao.MessageDAO;
import automation.mybatis.model.Message;
import automation.mybatis.model.User;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class MessageDAOImpl implements MessageDAO {
    private final static String namespace = "message_mapper";


    @Override
    public void create(Message msg) {
        SqlSession sqlSession = SessionFactory.getSession();
        sqlSession.insert(namespace + ".create", msg);
        sqlSession.commit();
        sqlSession.close();
    }

    @Override
    public Message getById(long id) {
        SqlSession sqlSession = SessionFactory.getSession();
        Message a = sqlSession.selectOne(namespace + ".getById", id);
        sqlSession.close();
        return a;
    }

    @Override
    public List<Message> getByUserId(long user_id) {
        SqlSession sqlSession = SessionFactory.getSession();
        List<Message> as = sqlSession.selectList(namespace + ".getByUserId", user_id);
        sqlSession.close();
        return as;
    }

    @Override
    public List<Message> get() {
        SqlSession sqlSession = SessionFactory.getSession();
        List<Message> as = sqlSession.selectList(namespace + ".get");
        sqlSession.close();
        return as;
    }
}
