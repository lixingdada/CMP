package mvc.persistence;

import mvc.domain.Address;
import mvc.domain.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface UserDao {
    public List<User> findAllUser();
    public User findUserByUsernameAndPassword(String username,String password);

    public void loadUser(User user,String username);
   /* public User findUser(int id);*/
    public boolean insertUser(User user);

    public int insertAccount(String username);

    public int updateUserInfo(String virtualName, LocalDate birthday,String email,String phone,String username);

    public ArrayList<Address> findAddressByUser(String username);

    public Address insertAddress(String username,String name,String phone,String address);

    public void editAddress(String name,String phone,String address,String username,String oldName,String oldPhone,String oldAddress);

    public void deleteAddress(String username,String name,String phone,String address);

    public boolean isRepeatAddress(String username,String name,String phone,String address);

    public int updateAvatar(String avatar,String userid);
}
