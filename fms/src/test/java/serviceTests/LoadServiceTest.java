package serviceTests;

import com.google.gson.Gson;
import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import model.AuthToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import request.LoadRequest;
import response.ErrorResponse;
import response.LoadResponse;
import response.Response;
import service.ClearService;
import service.LoadService;
import service.PersonService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.UUID;

import static org.junit.Assert.*;


public class LoadServiceTest {
    LoadRequest req;
    AuthToken token;
    @Before
    public void setUp() throws Exception {
        Database db = new Database();

        File location = new File("C:/Users/jacob/IdeaProjects/FamilyMap/fms/json/example.json");

        try (Reader reader = new FileReader(location)){
            Gson gson = new Gson();

            req = gson.fromJson(reader, LoadRequest.class);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        ClearService clearService = new ClearService();
        clearService.clear();
    }

    @Test
    public void load() {
        LoadService ls = new LoadService();
        LoadResponse res = (LoadResponse) ls.load(req);
        Database db = new Database();
        try{
            db.openConnection();
            AuthTokenDao atDao = new AuthTokenDao(db.getConn());
            token = new AuthToken(UUID.randomUUID().toString(), "sheila");
            atDao.insert(token);
            db.closeConnection(true);
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
        PersonService personService = new PersonService();
        Response[] response =  personService.getFamily(token.getAuthToken());

        assertTrue(response.length == 3);
    }

    @Test
    public void loadFail(){
        req.getUsers()[0].setUserName(null); //Change the username to null, should not be accepted into db
        LoadService ls = new LoadService();
        boolean fail = false;
        ErrorResponse er = (ErrorResponse) ls.load(req);
        assertTrue(er.getMessage() != null);
        assertTrue(er.getMessage() ==
                "Error encountered while inserting into the database");
    }
}