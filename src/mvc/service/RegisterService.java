package mvc.service;

import mvc.domain.User;
import mvc.persistence.Impl.UserDaoImpl;

public class RegisterService {
    private UserDaoImpl userDao;

    public RegisterService() {
        userDao = new UserDaoImpl();
    }

    public boolean register(User user) {
        return userDao.insertUser(user);
    }
}
