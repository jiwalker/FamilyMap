package daoTests;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import model.AuthToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.security.krb5.internal.AuthorizationData;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class AuthTokenDaoTest {
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
        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), "username");
        AuthTokenDao atd = new AuthTokenDao(db.getConn());

        boolean pass = false;
        try {
            atd.insert(authToken);

            AuthToken result = atd.find(authToken.getAuthToken());
            if (result.getAuthToken().equals(authToken.getAuthToken()) &&
                result.getUserName().equals(authToken.getUserName())){

                pass = true;

            }
        }
        catch (DataAccessException e ) {
            e.printStackTrace();
        }
        assertTrue(pass);
    }

    @Test
    public void insertFail() {
        AuthToken authToken1 = new AuthToken("tokenID", "user1");
        AuthToken authToken2 = new AuthToken("tokenID", "user2");

        boolean pass = false;
        try {
            AuthTokenDao atd = new AuthTokenDao(db.getConn());
            atd.insert(authToken1);
            atd.insert(authToken2);
        }
        catch (DataAccessException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    @Test
    public void insertMany() {
        ArrayList<AuthToken> tokens = new ArrayList<>();
        ArrayList<String> tokenIDs = new ArrayList<>();
        ArrayList<AuthToken> results = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            StringBuilder username = new StringBuilder();
            username.append("user");
            username.append(i);
            AuthToken token = new AuthToken(UUID.randomUUID().toString(), username.toString());
            tokens.add(token);
            tokenIDs.add(token.getAuthToken());
        }

        AuthTokenDao authTokenDao = new AuthTokenDao(db.getConn());
        try {
            assertTrue(authTokenDao.insertMany(tokens));
            results = authTokenDao.findMany(tokenIDs);

        }
        catch (DataAccessException e ) {
            e.printStackTrace();
        }

        assertTrue(results.size() == tokens.size());
    }

    @Test
    public void insertManyFail() {

        AuthTokenDao authTokenDao = new AuthTokenDao(db.getConn());

        boolean pass = false;
        try {
            authTokenDao.insertMany(null);
        }
        catch (DataAccessException e){
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    @Test
    public void find() {
        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), "username");
        AuthTokenDao atd = new AuthTokenDao(db.getConn());
        boolean pass = false;
        try {
            atd.insert(authToken);
            AuthToken result = atd.find(authToken.getAuthToken());
            if (result.getAuthToken().equals(authToken.getAuthToken()) &&
                    result.getUserName().equals(authToken.getUserName())){

                pass = true;

            }

        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
        assertTrue(pass);
    }

    @Test
    public void findNegative() {
        AuthTokenDao atd = new AuthTokenDao(db.getConn());
        boolean pass = false;
        try {
            AuthToken token = atd.find("fooey");
            if (token == null){ pass = true; }
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }

        assertTrue(pass);
    }


}