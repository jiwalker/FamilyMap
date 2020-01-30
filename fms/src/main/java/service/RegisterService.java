package service;

import dao.*;
import model.AuthToken;
import model.Event;
import model.Person;
import request.LoginRequest;
import request.RegisterRequest;
import response.*;
import model.User;

import java.util.Random;
import java.util.UUID;

/**
 * RegisterService is used to make calls on other classes to register a new user
 */
public class RegisterService {
    private RegisterRequest request;
    private RegisterResponse response;
    private Database db;
    boolean commit;

    public RegisterService() {
    }
    /**
     * register creates a new user account, generates 4 generations of ancestor data for the new
     * user, logs the user in, and returns an auth token.
     * @param registerRequest
     * @return
     */
    public Response register(RegisterRequest registerRequest) {
        request = registerRequest;
        response = new RegisterResponse();
        db = new Database();
        commit = true;
        int defaultGens = 4;

        try {

            createNewUser(); //Create user

            FillService fillService = new FillService();
            if (fillService.fill(request.getUserName(), defaultGens) instanceof ErrorResponse){

            }

            LoginService loginService = new LoginService();
            LoginRequest lReq = new LoginRequest();
            lReq.setUserName(request.getUserName());
            lReq.setPassword(request.getPassword());

            LoginResponse lRes = (LoginResponse) loginService.login(lReq);

            response.setUserName(lRes.getUserName());
            response.setPersonID(lRes.getPersonID());
            response.setAuthToken(lRes.getAuthToken());


        }
        catch (DataAccessException e) {
            ErrorResponse error = new ErrorResponse("Username already registered");
            return error;
        }
        if (commit == false) {
            ErrorResponse error = new ErrorResponse("There was an issue while filling the database.");
            return error;
        }
        return response;
    }

    private void createNewUser() throws DataAccessException{
        db.openConnection();
        db.createTables();

        User user = new User(request.getUserName(), request.getPassword(), request.getEmail(),
                             request.getFirstName(), request.getLastName(), request.getGender(),
                             UUID.randomUUID().toString());


        //Create a user model object and insert it into the database
        UserDao userDao = new UserDao(db.getConn());

        try{
            userDao.insertOne(user);
        }
        catch (DataAccessException e) {
            db.closeConnection(false);
            db = null;
            throw e;
        }

        db.closeConnection(true);
        db = null;
    }
}
