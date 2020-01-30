package daoTests;

import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.Event;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import javax.xml.crypto.Data;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class EventDaoTest {
    private Database db;

    @Before
    public void setUp() throws Exception {
        db = new Database();
        db.openConnection();
        db.createTables();

    }

    @After
    public void tearDown() throws Exception {
        db.closeConnection(false);
        db = null;
    }

    @Test
    public void insert() {

        Event inputEvent = new Event("unique123", "JacobTest", "personID123",
                33.777f, 79.931f, "USA", "Charleston", "Birth", 1996);

        try {
            EventDao eventDao = new EventDao(db.getConn());
            eventDao.insert(inputEvent);
            Event retrieval = eventDao.find("unique123");

            assertTrue (inputEvent.equals(retrieval));

            //System.out.println("Test passed, data inserted correctly into database.");

        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * This test checks to see if the database will only allow unique eventIDs
     */
    @Test
    public void insertFail() {
        Event event1 = new Event("thisisatest", "testy", "ID123",
                22.2f, 33.3f, "Canada", "Alberta", "Something", 1900);
        Event event2 = new Event("thisisatest", "test", "ID321",
                62.2f, 63.3f, "Nowhere", "Somewhere", "Nothing", 2000);
        boolean pass = false;
        try {
            EventDao eventDao = new EventDao(db.getConn());
            eventDao.insert(event1);
            boolean result = eventDao.insert(event2);
            assertTrue(!result);
        }
        catch (DataAccessException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    @Test
    public void insertMany(){
        Event[] events = new Event[10];
        String username = "username";
        ArrayList<Event> results = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            String eventID = "event " + i;
            String personID = "person " + i;
            Event event = new Event(eventID, username, personID,
                    22.2f, 33.3f, "Canada", "Alberta", "Something", 1900);
            events[i] = event;
        }

        EventDao eventDao = new EventDao(db.getConn());
        try {
            assertTrue(eventDao.insertMany(events));
            results = eventDao.findMany(username);

        }
        catch (DataAccessException e ) {
            e.printStackTrace();
        }

        assertTrue(results.size() == events.length);
    }

    @Test
    public void insertManyFail(){
        Event[] events = new Event[10];
        String username = "username";
        ArrayList<Event> results = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            String personID = "person " + i;
            Event event = new Event("bad string", username, personID,
                    22.2f, 33.3f, "Canada", "Alberta", "Something", 1900);
            events[i] = event;
        }

        EventDao eventDao = new EventDao(db.getConn());
        boolean pass = false;
        try {
            eventDao.insertMany(events);
        }
        catch (DataAccessException e ) {
            //e.printStackTrace();
            //The expected exception is from inserting events with the same primary key into the database, which is illegal
            pass = true;
        }

        assertTrue(pass);
    }


    @Test
    public void clear(){
        Event inputEvent = new Event("unique123", "JacobTest", "personID123",
                33.777f, 79.931f, "USA", "Charleston", "Birth", 1996);
        try {
            EventDao eventDao = new EventDao(db.getConn());
            eventDao.insert(inputEvent);
            assertTrue(eventDao.clear());
            //assertTrue(userDao.find("JAWA") == null);
        }
        catch (DataAccessException e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void clearFail(){
        boolean exception = false;
        try {
            EventDao eventDao = new EventDao(null);
            assertFalse(eventDao.clear());
        }
        catch (NullPointerException e) {
            exception = true;
        }
        assertTrue(exception);
    }

    @Test
    public void find() {
        EventDao ed = new EventDao(db.getConn());
        try {
            Event event = new Event("unique123", "JacobTest", "personID123",
                    33.777f, 79.931f, "USA", "Charleston", "Birth", 1996);
            ed.insert(event);
            Event newEvent = ed.find("unique123");
            assertTrue(event.equals(newEvent));
        }
        catch (DataAccessException e ){
            e.printStackTrace();
        }

    }

    @Test
    public void findNeg(){
        EventDao ed = new EventDao(db.getConn());
        try {
            Event event = new Event("unique123", "JacobTest", "personID123",
                    33.777f, 79.931f, "USA", "Charleston", "Birth", 1996);
            ed.insert(event);
            Event newEvent = ed.find("UNIQUE123"); //Check for case sensitivity
            assertTrue(newEvent == null);
        }
        catch (DataAccessException e ){
            e.printStackTrace();
        }
    }
}