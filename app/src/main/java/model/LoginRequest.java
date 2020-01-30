package model;

/**
 * This object will store the data needed for Login Requests from the handlers to the services
 * It will have a username and a password
 */
public class LoginRequest {
    private String userName;
    private String password;

    public LoginRequest() {

    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}