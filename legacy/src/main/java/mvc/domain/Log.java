package mvc.domain;

import java.sql.Timestamp;

public class Log {
    private int logId;
    private String user_id;
    private String action;
    private Timestamp action_time;
    private String item_id;
    private String details;

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Timestamp getAction_time() {
        return action_time;
    }

    public void setAction_time(Timestamp action_time) {
        this.action_time = action_time;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
