package mvc.persistence.Impl;

import mvc.domain.User;
import mvc.persistence.DBUtil;
import mvc.persistence.UserDao;

import java.sql.*;
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
}
