package mvc.service;


import mvc.domain.User;


public class UserService {
    private String msg;
    public boolean login(User user) {
        if (user.getUsername().length() > 8) {
            this.msg = "用户名不合法";
            return false;
        }
        if (user.getPassword().length() > 8) {
            this.msg = "密码不合法";
            return false;
        }

        return true;
    }
}
