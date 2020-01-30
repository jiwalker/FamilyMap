package serviceTests;

import com.google.gson.Gson;
import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
import model.AuthToken;
import model.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import request.LoadRequest;
import response.ErrorResponse;
import response.PersonResponse;
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

public class PersonServiceTest {
    Person person;
    AuthToken authToken;
    AuthToken sheilaToken;

    @Before
    public void setUp() throws Exception {
        ClearService cs = new ClearService();
        cs.clear();
        Database db = new Database();
        db.openConnection();
        db.createTables();

        person = new Person("personID", "username", "firstName",
                "lastName", "m", "fatherID", "motherID", "spouseID");
        PersonDao personDao = new PersonDao(db.getConn());
        personDao.insert(person);
        authToken = new AuthToken(UUID.randomUUID().toString(), "username");
        AuthTokenDao authTokenDao = new AuthTokenDao(db.getConn());
        authTokenDao.insert(authToken);
        db.closeConnection(true);
        db = null;
    }

    @After
    public void tearDown() throws Exception {
        ClearService cs = new ClearService();
        cs.clear();
    }

    @Test
    public void getPerson() {
        PersonService ps = new PersonService();
        PersonResponse pr = (PersonResponse) ps.getPerson(authToken.getAuthToken(), "personID");

        assertTrue(pr.getMessage() == null);
        assertTrue(pr.getAssociatedUsername().equals(person.getAssociatedUsername()));
        assertTrue(pr.getPersonID().equals(person.getPersonID()));
        assertTrue(pr.getFirstName().equals(person.getFirstName()));
        assertTrue(pr.getLastName().equals(person.getLastName()));
        assertTrue(pr.getGender().equals(person.getGender()));

    }

    @Test
    public void getPersonFail(){
        PersonService ps = new PersonService();
        ErrorResponse er = (ErrorResponse) ps.getPerson(authToken.getAuthToken(), "personid"); //Check for case sensitive searching
        assertTrue(er.getMessage() != null);

        String targetMessage = "Invalid data";

        assertTrue(targetMessage.equals(er.getMessage()));
    }

    @Test
    public void getFamily() {
        File location = new File("C:/Users/jacob/IdeaProjects/FamilyMap/fms/json/example.json");
        LoadRequest req;
        try (Reader reader = new FileReader(location)){
            Gson gson = new Gson();

            req = gson.fromJson(reader, LoadRequest.class);
            LoadService ls = new LoadService();
            ls.load(req);
            Database db = new Database();
            db.openConnection();
            AuthTokenDao atd = new AuthTokenDao(db.getConn());
            sheilaToken = new AuthToken(UUID.randomUUID().toString(), "sheila");
            atd.insert(sheilaToken);
            db.closeConnection(true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (DataAccessException e){
            e.printStackTrace();
        }

        PersonService ps = new PersonService();
        Response[] pr = ps.getFamily(sheilaToken.getAuthToken());

        assertTrue(pr.length == 3);
    }

    @Test
    public void getFamilyFail() {
        PersonService ps = new PersonService();
        Response[] response =  ps.getFamily(null);

        assertTrue(response[0] instanceof ErrorResponse);
    }
}