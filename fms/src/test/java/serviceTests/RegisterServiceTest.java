package serviceTests;

import dao.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import request.RegisterRequest;
import response.ErrorResponse;
import response.RegisterResponse;
import response.Response;
import service.RegisterService;

import java.util.UUID;

import static org.junit.Assert.*;

public class RegisterServiceTest {
    RegisterRequest request;
    Database db;

    @Before
    public void setUp() throws Exception {
        request = new RegisterRequest();
        request.setUserName(UUID.randomUUID().toString());
        request.setPassword(UUID.randomUUID().toString());
        request.setEmail("email");
        request.setFirstName("John");
        request.setLastName("Denver");
        request.setGender("m");
        db = new Database();
    }

    @After
    public void tearDown() throws Exception {
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void register() {
        RegisterService regService = new RegisterService();
        Response response =  regService.register(request);
        RegisterResponse registerResponse = null;
        if (response instanceof ErrorResponse) {
            System.out.println(response.getMessage());
        }
        else {
            registerResponse = (RegisterResponse) response;
        }
        assertTrue(registerResponse.getUserName().equals(request.getUserName()));
    }

    @Test
    public void registerFail(){
        RegisterService registerService = new RegisterService();
        request.setUserName(null);
        Response response = registerService.register(request);

        assertTrue(response instanceof ErrorResponse);

    }
}