package mvc.persistence;

import mvc.domain.User;

import java.util.List;

public interface UserDao {
    public List<User> findAllUser();
    public User findUserByUsernameAndPassword(String username,String password);
    public User findUser(int id);
    public boolean insertUser(User user);

}
