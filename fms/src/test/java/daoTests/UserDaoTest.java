package daoTests;

import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserDaoTest {
    private Database db;

    @Before
    public void setUp() throws Exception{
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
    public void insertPass() {
        User jedi = new User("jawaman", "starwars", "darthJawa@endor.net",
                "Obi", "Kenobi", "m", "helloThere");
        try {
            UserDao userDao = new UserDao(db.getConn());
            userDao.insertOne(jedi);
            User data = userDao.find("jawaman");
            assertTrue(jedi.equals(data));
        }
        catch (DataAccessException e) {
            e.printStackTrace();
            System.exit(13);
        }
    }

    /**
     * This should throw an exception and not insert
     */
    @Test
    public void insertFail() {
        User jedi1 = new User("jawaman", "starwars", "darthJawa@endor.net",
                "Obi", "Kenobi", "m", "helloThere");
        User jedi2 = new User("jawaman", "fake", "faker@darkside.net",
                "Greedo", "Alien", "m", "iBetYouHave");

        try {
            UserDao userDao = new UserDao(db.getConn());
            userDao.insertOne(jedi1);
            boolean result = userDao.insertOne(jedi2);
            assertTrue(!result);
        }
        catch (DataAccessException e) {
            //System.out.println("Test passed, exception was thrown");
        }

    }


    @Test
    public void findPass() {
        User jedi1 = new User("jawaman", "starwars", "darthJawa@endor.net",
                "Obi", "Kenobi", "m", "helloThere");
        try {
            UserDao userDao = new UserDao(db.getConn());
            userDao.insertOne(jedi1);
            User jedi2 = userDao.find("jawaman");
            assertTrue(jedi1.equals(jedi2));
            //System.out.println("Test successful, data queried successfully.");
            }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findFail() {
        User baseUser = new User("JAWA", "EWOK", "a@mail.com",
                "John", "Smith", "m", "perID");
        try {
            UserDao userDao = new UserDao(db.getConn());
            userDao.insertOne(baseUser);
            User result = userDao.find("jawa"); //Should return nothing
            assertFalse(baseUser.equals(result));
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void clearPass(){
        User baseUser = new User("JAWA", "EWOK", "a@mail.com",
                "John", "Smith", "m", "perID");
        try {
            UserDao userDao = new UserDao(db.getConn());
            userDao.insertOne(baseUser);
            assertTrue(userDao.clear());
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
            UserDao ud = new UserDao(null);
            assertFalse(ud.clear());
        }
        catch (NullPointerException e) {
            exception = true;
        }
        assertTrue(exception);
    }


}