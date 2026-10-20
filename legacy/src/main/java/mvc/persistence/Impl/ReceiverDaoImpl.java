package mvc.persistence.Impl;

import mvc.domain.Address;
import mvc.domain.User;
import mvc.persistence.DBUtil;
import mvc.persistence.ReceiverDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReceiverDaoImpl implements ReceiverDao {
    private  static final String GET_RECEIVER = "SELECT * FROM receiver WHERE userid = ?";

    @Override
    public List<Address> getReceiversByUserId(String userId) {
        List<Address> receivers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;


        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(GET_RECEIVER);
            preparedStatement.setString(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Address receiver = new Address();
                receiver.setReceiverName(resultSet.getString("receiver_name"));
                receiver.setReceiverPhone(resultSet.getString("receiver_phone"));
                receiver.setReceiverAddress(resultSet.getString("receiver_addr"));

                receivers.add(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return receivers;
    }
}
