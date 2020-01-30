package daoTests;

import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
import model.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonDaoTest {
    private Database db;
    Person basePerson;

    @Before
    public void setUp() throws Exception{
        basePerson = new Person("basicID", "user123", "Elvis",
                "Presley", "m", "WhoKnows",
                "WhoCares", "Single");
        db = new Database();
        db.openConnection();
        db.createTables();
    }

    @After
    public void tearDown() throws Exception {
        db.closeConnection(false);
        db = null;
        basePerson = null;
    }

    @Test
    public void insertPass() {
        try {
            PersonDao personDao = new PersonDao(db.getConn());
            personDao.insert(basePerson);
            Person response = personDao.findOne("basicID");
            assertTrue(basePerson.equals(response));
        }
        catch (DataAccessException e ){
            e.printStackTrace();
            System.exit(13);
        }
    }

    @Test
    public void insertFail() {
        Person hacker = new Person("basicID", "qwerty","Bad","Man",
                "f", null, null, null);
        boolean pass = false;
        try {
            PersonDao personDao = new PersonDao(db.getConn());
            personDao.insert(basePerson);
            boolean result = personDao.insert(hacker);
            assertFalse(result);
        }
        catch (DataAccessException e){
            pass = true;
        }
        assertTrue(pass);
    }

    @Test
    public void findOnePass() {

        try {
            PersonDao personDao = new PersonDao(db.getConn());
            personDao.insert(basePerson);
            Person result = personDao.findOne("basicID");
            assertTrue(basePerson.equals(result));
            //System.out.println("Test successful, data queried successfully.");
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findOneFail() {
        try {
            PersonDao personDao = new PersonDao(db.getConn());
            personDao.insert(basePerson);
            Person result = personDao.findOne("BASICid"); //Should return nothing
            assertFalse(basePerson.equals(result));
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void clearPass() {
        try {
            PersonDao personDao = new PersonDao(db.getConn());
            personDao.insert(basePerson);
            assertTrue(personDao.clear());
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
            PersonDao personDao = new PersonDao(null);
            assertFalse(personDao.clear());
        }
        catch (NullPointerException e) {
            exception = true;
        }
        assertTrue(exception);
    }


}