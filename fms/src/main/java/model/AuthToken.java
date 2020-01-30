package model;

/**
 * The AuthToken class is a data structure to hold the data for the Authorization Token tables and objects
 *
 */
public class AuthToken {
    private String authToken;
    private String userName;

    public AuthToken(String authToken, String userName) {
        this.authToken = authToken;
        this.userName = userName;
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
}
