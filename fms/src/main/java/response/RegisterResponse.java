package response;

/**
 * This object holds response data from register requests
 */
public class RegisterResponse extends Response{
    private String authToken;
    private String userName;
    private String personID;

    public RegisterResponse() {
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
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPersonID() {
        return personID;
    }
    public void setPersonID(String personID) {
        this.personID = personID;
    }
}