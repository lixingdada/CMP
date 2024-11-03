package mvc.persistence.Impl;

import mvc.domain.User;
import mvc.persistence.UserDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    //通过username去查password
    //看用户名在不在
    private static final String INSERT_USER =
            "INSERT INTO signon (username, password) VALUES (?, ?)";
    /*private static final String FIND_USER =
            "SELECT * FROM signon WHERE id = ?";*/
    private static final String FIND_USER_BY_USERNAME_AND_PASSWORD =
            "SELECT * FROM signon WHERE username = ? AND password = ?";
    private static final String FIND_ALL_USERS =
            "SELECT * FROM signon";

    @Override
    public List<User> findAllUser() {
        List<User> userList = new ArrayList<>();
        DatabaseMetaData DBUtil = null;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_USERS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id")); // 假设 id 是一个字段
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public User findUserByUsernameAndPassword(String username, String password) {
        User user = null;
        DatabaseMetaData DBUtil = null;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_USER_BY_USERNAME_AND_PASSWORD)) {

            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id")); // 假设 id 是一个字段
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

  /*  @Override
    public User findUser(int id) {
        User user = null;
        DatabaseMetaData DBUtil = null;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_USER)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id")); // 假设 id 是一个字段
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }*/

    @Override
    public boolean insertUser(User user) {
        boolean success = false;
        DatabaseMetaData DBUtil = null;
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
