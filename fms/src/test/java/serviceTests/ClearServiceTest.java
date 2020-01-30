package serviceTests;

import com.google.gson.Gson;
import dao.*;
import model.AuthToken;
import model.Event;
import model.Person;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import response.Response;
import service.ClearService;
import service.FillService;
import service.PersonService;

import static org.junit.Assert.*;

public class ClearServiceTest {
    PersonService personService;

    Database db;

    @Before
    public void setUp() throws Exception {
        personService = new PersonService();
        db = new Database();
        db.openConnection();
        db.createTables();

        User user = new User("username", "password", "email", "firstName",
                "lastName", "m", "personID");
        UserDao userDao = new UserDao(db.getConn());
        userDao.insertOne(user);
        Person person = new Person("personID", "username", "firstName",
                "lastName", "m", "fatherID", "motherID", "spouseID");
        PersonDao personDao = new PersonDao(db.getConn());
        personDao.insert(person);

        Event event = new Event("eventID", "username", "personID",
                100, 200, "country", "city", "eventType", 2000);
        EventDao eventDao = new EventDao(db.getConn());
        eventDao.insert(event);

        AuthToken authToken = new AuthToken("authToken", "username");
        AuthTokenDao authTokenDao = new AuthTokenDao(db.getConn());
        authTokenDao.insert(authToken);

        db.closeConnection(true);
        db = null;
    }

    @After
    public void tearDown() throws Exception {
        ClearService clearService = new ClearService();
        clearService.clear();
    }

    @Test
    public void clear() {
        Gson gson = new Gson();
        //System.out.println(gson.toJson(personService.getPerson("personID")));

        ClearService clearService = new ClearService();

        clearService.clear(); //Call the clear function on the database

        Response response = personService.getPerson("authToken", "personID");    //Query to see if the person is still in the database
        assertTrue(response.getMessage() == "Invalid auth token");   //Check if the clear works

    }


}