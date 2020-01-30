package modelTests;

import model.ServerProxy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerProxyTest {
    ServerProxy serverProxy;

    @Before
    public void setUp() throws Exception {
        serverProxy = new ServerProxy("192.168.0.4", "8080");
    }

    @After
    public void tearDown() throws Exception {
        serverProxy = null;
    }

    @Test
    public void clear() {
        serverProxy.clear();
    }

    @Test
    public void getEvent() {

    }

    @Test
    public void getAllEvents() {
        serverProxy.getAllEvents("b1a95ae9-214e-4cf8-9275-a95097a9e918");
    }

    @Test
    public void fill() {

    }

    @Test
    public void login() {
    }

    @Test
    public void getPerson() {
        serverProxy.getPerson("b1a95ae9-214e-4cf8-9275-a95097a9e918", "e7b02b44-6c37-4f00-bc72-cbad9a515345");
    }

    @Test
    public void getUserFamily() {
    }

    @Test
    public void register() {
        serverProxy.register("user", "password", "email", "John",
                "Doe", "m");
    }
}