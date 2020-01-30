package serviceTests;

import dao.Database;
import dao.UserDao;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import request.LoginRequest;
import response.ErrorResponse;
import response.LoginResponse;
import service.ClearService;
import service.LoginService;

import static org.junit.Assert.*;

public class LoginServiceTest {
    LoginRequest lReq;

    @Before
    public void setUp() throws Exception {
        ClearService cs = new ClearService();
        cs.clear();

        Database db = new Database();
        db.openConnection();
        db.createTables();

        UserDao userDao = new UserDao(db.getConn());
        User user = new User("username", "password", "email", "firstName",
                "lastName", "m", "personID");
        userDao.insertOne(user);

        db.closeConnection(true);
        db = null;


    }

    @After
    public void tearDown() throws Exception {
        ClearService cs = new ClearService();
        cs.clear();
    }

    @Test
    public void login() {
        lReq = new LoginRequest();
        lReq.setUserName("username");
        lReq.setPassword("password");

        LoginService ls = new LoginService();
        LoginResponse lRes = (LoginResponse) ls.login(lReq);
        assertTrue(lRes.getMessage() == null);

    }

    @Test
    public void loginFail() {
        lReq = new LoginRequest();
        lReq.setUserName("username"); //This is the correct username
        lReq.setPassword("Password"); //This is NOT the password, should be lowercase 'p'

        LoginService ls = new LoginService();
        ErrorResponse error = (ErrorResponse) ls.login(lReq);

        String targetMessage = "Invalid username or password";
        assertTrue(targetMessage.equals(error.getMessage()));
    }
}