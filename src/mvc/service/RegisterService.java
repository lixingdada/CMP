package mvc.service;

import mvc.domain.User;
import mvc.persistence.Impl.UserDaoImpl;

public class RegisterService {
    private UserDaoImpl userDao;

    public RegisterService() {
        userDao = new UserDaoImpl();
    }

    public boolean register(User user) {
        // 检查用户名是否存在
        UserDaoImpl userDAO = new UserDaoImpl();
        if (userDAO.userExists(user.getUsername())) {
            // 用户名已存在，注册失败
            return false;
        }
        // 用户名不存在，插入新用户
        return userDAO.insertUser(user);
    }

    public int createAccount(String username){
        return userDao.insertAccount(username);
    }


}
