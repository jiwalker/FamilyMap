package serviceTests;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.AuthToken;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import response.PersonResponse;
import response.Response;
import service.ClearService;
import service.FillService;
import service.PersonService;

import javax.xml.crypto.Data;

import java.util.UUID;

import static org.junit.Assert.*;

public class FillServiceTest {
    Database db;
    FillService fillService;


    @Before
    public void setUp() throws Exception {
        ClearService clearService = new ClearService();
        clearService.clear();
        fillService = new FillService();
        db = new Database();
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
        ClearService clearService = new ClearService();
        clearService.clear();
    }

    @Test
    public void fill() {
        int generations = 4;
        int targetLength = 31; //Total number of family members plus the user person

        fillService.fill("username", generations);
        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), "username");
        db = new Database();
        try{
            db.openConnection();
            AuthTokenDao atDao = new AuthTokenDao(db.getConn());
            atDao.insert(authToken);
            db.closeConnection(true);
            db = null;
        }
        catch (DataAccessException e){
            e.printStackTrace();
        }

        PersonService personService = new PersonService();
        Response[] response =  personService.getFamily(authToken.getAuthToken());
        int resLength = response.length;

        assertTrue(resLength == targetLength);
    }

    @Test
    public void fillFail() {
        int generations = 4;
        boolean pass = false;
        try {
            fillService.fill("fakeUsername", generations); //This should cause the fill to fail, because the user doesn't exit
        }
        catch (NullPointerException e) {
            //e.printStackTrace();
            pass = true;
        }
        assertTrue(pass);
    }
}