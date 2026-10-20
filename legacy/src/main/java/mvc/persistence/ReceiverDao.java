package mvc.persistence;

import mvc.domain.Address;
import mvc.domain.User;

import java.util.List;

public interface ReceiverDao {
    List<Address> getReceiversByUserId(String userId);
}
