package response;

/**
 * This objects holds data for login responses
 */
public class LoginResponse extends Response{
    private String authToken;
    private String userName;
    private String personID;

    public LoginResponse() {

    }

    public String getAuthToken() {
        return authToken;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String username) {
        this.userName = username;
    }
    public String getPersonID() {
        return personID;
    }
    public void setPersonID(String personID) {
        this.personID = personID;
    }
}