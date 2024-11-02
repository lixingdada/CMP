package mvc.persistence;

import java.sql.*;
import java.util.ArrayList;

public class DBUtil {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/web?serverTimezone=Asia/Shanghai";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    private static Connection con = null; // 声明Connection对象
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;


    public DBUtil(){}
    // 建立返回值为Connection的方法
    public static Connection getConnection() {
        try { // 通过访问数据库的URL获取数据库连接对象
            Class.forName(DRIVER);
            con = DriverManager.getConnection(
                    URL,USERNAME,PASSWORD);
            System.out.println("数据库连接成功");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("数据库连接失败");
        }
        return con;
    }

    //获取命令对象
    public static PreparedStatement getpreparedStatement(String sql) {
        try {
            con = getConnection();
            preparedStatement = con.prepareStatement(sql);
        } catch (SQLException e) {
            System.out.println("sql语法错误");
        }
        return preparedStatement;
    }

    // 返回受影响的行数
    public static int executeUpdate(String sql, ArrayList<Object> params) {
        try {
            preparedStatement = getpreparedStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet executeQuery(String sql, ArrayList<Object> params) {
        preparedStatement = getpreparedStatement(sql);
        try {
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeStatement(Statement statement){
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void  closeConnection(Connection connection) {
        if(connection!=null){
            try{
                connection.close();
            }catch(SQLException e){
                throw new RuntimeException(e);
            }
        }
    }

    public static void closePreparedStatement(PreparedStatement preparedStatement){
        if(preparedStatement != null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet ){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //测试数据库连接
    //public static void main(String[] args) {
        //System.out.println("数据库："+getConnection());
   // }
}
