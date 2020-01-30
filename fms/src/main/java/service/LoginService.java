package service;
import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.AuthToken;
import model.User;
import request.LoginRequest;
import request.RegisterRequest;
import response.ErrorResponse;
import response.LoginResponse;
import response.RegisterResponse;
import response.Response;
import java.util.UUID;

/**
 * LoginService will call the necessary functions to log in a user
 */
public class LoginService {
    private LoginRequest request;
    private LoginResponse response;
    private Database db;
    private ErrorResponse error;

    public LoginService() {
    }

    /**
     * login checks for the existence of a user and verifies the password
     * @param loginRequest contains the username and password strings
     * @return an authentication token
     */
    public Response login(LoginRequest loginRequest){
        response = new LoginResponse();
        request = loginRequest;


        String userName = request.getUserName();
        String password = request.getPassword();

        try {
            db = new Database();
            db.openConnection();
            db.createTables();

            UserDao userDao = new UserDao(db.getConn());
            User user = userDao.find(userName);
            if (user == null) {
                ErrorResponse error = new ErrorResponse("Invalid Username or Password");
                db.closeConnection(false);
                db = null;
                return error;
            }

            String key = user.getPassword();
            if (password.equals(key)) {
                AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), userName);
                AuthTokenDao authTokenDao = new AuthTokenDao(db.getConn());
                authTokenDao.insert(authToken);
                response.setAuthToken(authToken.getAuthToken());
                response.setUserName(userName);
                response.setPersonID(user.getPersonID());
            }
            else {
                error = new ErrorResponse("Invalid username or password");
            }

            db.closeConnection(true);
            db = null;
        }
        catch (DataAccessException e) {
            error = new ErrorResponse(e.getMessage());
            return error;
        }


        if (error != null) {
            return error;
        }

        return response;
    }
}
