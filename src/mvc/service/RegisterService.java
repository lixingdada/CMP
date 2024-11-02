package service;

import domain.User;
import persistence.UserDaoImpl;

public class RegisterService {
    private UserDaoImpl userDao;

    public RegisterService() {
        userDao = new UserDaoImpl();
    }

    public boolean register(User user) {
        return userDao.insertUser(user);
    }
}
