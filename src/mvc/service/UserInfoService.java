package mvc.service;

import mvc.domain.Address;
import mvc.domain.User;
import mvc.persistence.Impl.UserDaoImpl;
import mvc.persistence.UserDao;

import java.time.LocalDate;
import java.util.ArrayList;

public class UserInfoService {
    private UserDao userDao;

    public UserInfoService(){
        userDao = new UserDaoImpl();
    }

    public int updateUserInfo(String virtualName, LocalDate birthday, String email, String phone,String username){
        return userDao.updateUserInfo(virtualName, birthday, email, phone,username);
    }

    public void loadUser(User user, String username){
        userDao.loadUser(user,username);
    }

    public void findAddress(User user,String username){
        user.setAddresses(userDao.findAddressByUser(username));
    }

    public  String insertAddress(User user,String username,String name, String phone, String address){
        String message = "";
        if(userDao.isRepeatAddress(username, name, phone, address)){
            System.out.println("userInfoService : "+"重复了哟");
            message = "新增的地址信息已存在 ！";
        }
        else {
            user.getAddresses().add(userDao.insertAddress(username, name, phone, address));
        }
        return message;
    }

    public String editAddress(User user,String name,String phone,String address,String username,String oldName,String oldPhone,String oldAddress){
        String message = "";
        if(userDao.isRepeatAddress(username, name, phone, address)){
            System.out.println("userInfoService : "+"重复了哟");
            message = "修改的地址信息已存在 ！";
        }
        else {
            userDao.editAddress(name, phone, address, username, oldName, oldPhone, oldAddress);
            findAddress(user,username);
        }
        return message;
    }

    public void deleteAddress(User user,String username,String name,String phone,String address){
        userDao.deleteAddress(username, name, phone, address);
        findAddress(user,username);
    }
}
