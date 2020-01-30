package serviceTests;

import com.google.gson.Gson;
import dao.*;
import model.AuthToken;
import model.Event;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import response.ErrorResponse;
import response.EventResponse;
import response.PersonResponse;
import response.Response;
import service.ClearService;
import service.EventService;
import service.FillService;
import sun.nio.cs.US_ASCII;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class EventServiceTest {
    Database db;
    Event baseEvent;
    AuthToken authToken;
    @Before
    public void setUp() throws Exception {
        db = new Database();
        db.openConnection();
        //db.createTables();

        baseEvent = new Event("eventID", "username", "personID",
                100, 200, "country", "city", "eventType", 2000);
        EventDao eventDao = new EventDao(db.getConn());
        eventDao.insert(baseEvent);

        UserDao userDao = new UserDao(db.getConn());
        User user = new User("username", "password", "email", "firstName",
                "lastName", "m", "personID");
        userDao.insertOne(user);

        authToken = new AuthToken(UUID.randomUUID().toString(), "username");
        AuthTokenDao authTokenDao = new AuthTokenDao(db.getConn());
        authTokenDao.insert(authToken);

        db.closeConnection(true);
        db = null;

        FillService fillService = new FillService();
        fillService.fill("username", 3);
    }

    @After
    public void tearDown() throws Exception {
        ClearService clearService = new ClearService();
        clearService.clear();
    }

    @Test
    public void getEvent() {
        Gson gson = new Gson();
        EventService eventService = new EventService();
        try {
            db = new Database();
            db.openConnection();

            baseEvent = new Event("eventID", "username", "personID",
                    100, 200, "country", "city", "eventType", 2000);
            EventDao eventDao = new EventDao(db.getConn());
            eventDao.insert(baseEvent);

            authToken = new AuthToken(UUID.randomUUID().toString(), "username");
            AuthTokenDao authTokenDao = new AuthTokenDao(db.getConn());
            authTokenDao.insert(authToken);
            db.closeConnection(true);

            Response response = eventService.getEvent(authToken.getAuthToken(),"eventID");
            if (response instanceof ErrorResponse) {

            }
            String jsonStr = gson.toJson(response);
            //System.out.println(jsonStr);
            Event responseEvent = gson.fromJson(jsonStr, Event.class);
            assertTrue(responseEvent.equals(baseEvent));
        }
        catch (DataAccessException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Test
    public void getEventFail(){
        EventService eventService = new EventService();
        try {
            //This passes in an authToken that has been cleared.
            Response response = eventService.getEvent(authToken.getAuthToken(),"eventID");
            assertTrue(response instanceof ErrorResponse);
        }
        catch (DataAccessException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Test
    public void getAllEvents() {
        int targetSize = (43); // 2 ^ 3 * 3 + 1
        FillService fillService = new FillService();
        fillService.fill(authToken.getUserName(), 3);

        EventService eventService = new EventService();
        Response[] responses = eventService.getAllEvents(authToken.getAuthToken());
        EventResponse[] events = new EventResponse[responses.length];

        assertTrue(events.length == targetSize);
    }

    @Test
    public void getAllEventsNeg(){
        FillService fillService = new FillService();

        //Pass in a negative number for generations
        Response response = fillService.fill(authToken.getUserName(), -1);
        assertTrue(response instanceof ErrorResponse);

    }
}