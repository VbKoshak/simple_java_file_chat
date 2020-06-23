package automation.mybatis.dao.impl;

import automation.mybatis.config.SessionFactory;
import automation.mybatis.dao.UserDAO;
import automation.mybatis.model.User;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class UserDAOImpl implements UserDAO {

    private final static String namespace = "user_mapper";

    @Override
    public void create(User user) {
        SqlSession sqlSession = SessionFactory.getSession();
        sqlSession.insert(namespace + ".create", user);
        sqlSession.commit();
        sqlSession.close();
    }

    @Override
    public User getById(long id) {
        SqlSession sqlSession = SessionFactory.getSession();
        User a = sqlSession.selectOne(namespace + ".getById", id);
        sqlSession.close();
        return a;
    }

    @Override
    public User getByPath(long path_id) {
        SqlSession sqlSession = SessionFactory.getSession();
        User a = sqlSession.selectOne(namespace + ".getByPath", path_id);
        sqlSession.close();
        return a;
    }

    @Override
    public User getByLogin(String login) {
        SqlSession sqlSession = SessionFactory.getSession();
        User a = sqlSession.selectOne(namespace + ".getByLogin", login);
        sqlSession.close();
        return a;
    }

    @Override
    public List<User> get() {
        SqlSession sqlSession = SessionFactory.getSession();
        List<User> as = sqlSession.selectList(namespace + ".get");
        sqlSession.close();
        return as;
    }

    @Override
    public void delete(long id) {
        SqlSession sqlSession = SessionFactory.getSession();
        sqlSession.delete(namespace + ".deleteById", id);
        sqlSession.commit();
        sqlSession.close();
    }
}
