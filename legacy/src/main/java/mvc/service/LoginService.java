package mvc.service;

import mvc.domain.User;
import mvc.persistence.Impl.UserDaoImpl;

public class LoginService {
    private UserDaoImpl userDao;

    public LoginService() {
        userDao = new UserDaoImpl();
    }

    public boolean login(String username, String password) {
        User user = userDao.findUserByUsernameAndPassword(username, password);
        return user != null; // 如果用户不为null，则登录成功
    }



    public User getUser(String username, String password) {
        return userDao.findUserByUsernameAndPassword(username, password);
    }

    // 新增：检查用户是否存在
    public boolean userExists(String username) {
        return userDao.userExists(username);
    }
}
