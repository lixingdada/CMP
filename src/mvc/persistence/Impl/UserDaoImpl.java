package mvc.persistence.Impl;

import mvc.domain.Address;
import mvc.domain.User;
import mvc.persistence.DBUtil;
import mvc.persistence.UserDao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    // SQL 查询语句
    private static final String INSERT_USER =
            "INSERT INTO signon (username, password) VALUES (?, ?)";
    private static final String FIND_USER_BY_USERNAME_AND_PASSWORD =
            "SELECT * FROM signon WHERE username = ? AND password = ?";
    private static final String FIND_USER_BY_USERNAME =
            "SELECT * FROM signon WHERE username = ?";
    private static final String FIND_ALL_USERS =
            "SELECT * FROM signon";

    private static final String INSERT_ACCOUNT = "INSERT INTO account (userid) VALUES (?)";

    private static final String UPDATE_USER_INFO = "UPDATE account set virtualname=?,birthday=?,email=?,phone=? WHERE userid = ?";

    private static final String LOAD_USER = "SELECT virtualname,birthday,email,phone,avatar FROM account WHERE userid = ?";
    private static final String FIND_ADDRESS = "SELECT receiver_name,receiver_phone,receiver_addr FROM receiver WHERE userid = ?";
    private static final String INSERT_ADDRESS = "INSERT INTO receiver  VALUES (?,?,?,?)";
    private static final String EDIT_ADDRESS = " UPDATE receiver set receiver_name =? , receiver_phone = ? , receiver_addr = ? WHERE userid = ? AND receiver_name = ? AND receiver_phone = ? AND receiver_addr = ?";

    private static final String DELETE_ADDRESS = "DELETE FROM receiver WHERE userid = ?AND receiver_name = ? AND receiver_phone = ? AND receiver_addr = ?";

    private static final String IS_REPEAT_ADDRESS = "SELECT * FROM receiver WHERE userid = ?AND receiver_name = ? AND receiver_phone = ? AND receiver_addr = ? ";
    // 获取所有用户
    @Override
    public List<User> findAllUser() {
        List<User> userList = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_USERS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    // 根据用户名和密码查询用户
    @Override
    public User findUserByUsernameAndPassword(String username, String password) {
        User user = null;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_USER_BY_USERNAME_AND_PASSWORD)) {

            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();

                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void loadUser(User user,String username) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        ResultSet resultSet = DBUtil.executeQuery(LOAD_USER,arrayList);
        while (true) {
            try {
                if (!resultSet.next()) break;
                user.setVirtualName(resultSet.getString("virtualname"));
                Date birthday = resultSet.getDate("birthday");
                if(birthday!=null){
                    user.setBirthday(birthday.toLocalDate());
                }
                user.setEmail(resultSet.getString("email"));
                user.setPhone(resultSet.getString("phone"));
                user.setAvatar(resultSet.getString("avatar"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 新增：仅通过用户名查询用户
    public boolean userExists(String username) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_USER_BY_USERNAME)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 插入新用户
    @Override
    public boolean insertUser(User user) {
        boolean success = false;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            success = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public int insertAccount(String username) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        return DBUtil.executeUpdate(INSERT_ACCOUNT,arrayList);
    }

    @Override
    public int updateUserInfo(String virtualName, LocalDate birthday, String email, String phone,String username) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(virtualName);
        arrayList.add(birthday);
        arrayList.add(email);
        arrayList.add(phone);
        arrayList.add(username);
        return DBUtil.executeUpdate(UPDATE_USER_INFO,arrayList);
    }

    @Override
    public ArrayList<Address> findAddressByUser(String username) {
        ArrayList<Object> arrayList = new ArrayList<>();
        ArrayList<Address> addresses = new ArrayList<>();
        arrayList.add(username);
        ResultSet resultSet = DBUtil.executeQuery(FIND_ADDRESS,arrayList);
        while (true) {
            try {
                if (!resultSet.next()) break;
                Address address = new Address();
                address.setReceiverName(resultSet.getString("receiver_name"));
                address.setReceiverPhone(resultSet.getString("receiver_phone"));
                address.setReceiverAddress(resultSet.getString("receiver_addr"));
                addresses.add(address);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return addresses;
    }

    @Override
    public  Address insertAddress(String username,String name, String phone, String address) {
        ArrayList<Object> arrayList = new ArrayList<>();
        Address address1 = new Address();
        arrayList.add(username);
        arrayList.add(name);
        arrayList.add(phone);
        arrayList.add(address);
        DBUtil.executeUpdate(INSERT_ADDRESS,arrayList);

        address1.setReceiverName(name);
        address1.setReceiverPhone(phone);
        address1.setReceiverAddress(address);
        return address1;
    }

    @Override
    public void editAddress(String name, String phone, String address, String username, String oldName, String oldPhone, String oldAddress) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(name);
        arrayList.add(phone);
        arrayList.add(address);
        arrayList.add(username);
        arrayList.add(oldName);
        arrayList.add(oldPhone);
        arrayList.add(oldAddress);
        DBUtil.executeUpdate(EDIT_ADDRESS,arrayList);
    }

    @Override
    public void deleteAddress(String username, String name, String phone, String address) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        arrayList.add(name);
        arrayList.add(phone);
        arrayList.add(address);
        DBUtil.executeUpdate(DELETE_ADDRESS,arrayList);
    }

    @Override
    public boolean isRepeatAddress(String username, String name, String phone, String address) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(username);
        arrayList.add(name);
        arrayList.add(phone);
        arrayList.add(address);
        ResultSet resultSet =DBUtil.executeQuery(IS_REPEAT_ADDRESS,arrayList);
        try {
            if(resultSet.next())
                return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
