package service;
import dao.*;
import model.Event;
import model.Person;
import model.User;
import request.LoadRequest;
import response.ErrorResponse;
import response.LoadResponse;
import response.Response;

/**
 * LoadService as a class will retrieve user data and return it
 */
public class LoadService {
    private LoadResponse response;
    private Database db;
    public LoadService(){}

    /**
     * Clears all data from the database (just like the /clear API), and then loads the
     * posted user, person, and event data into the database.
     * @param loadRequest
     * @return
     */
    public Response load(LoadRequest loadRequest) {

        db = new Database();
        try {
            db.openConnection();
            db.clearTables();
            UserDao userDao = new UserDao(db.getConn());
            PersonDao personDao = new PersonDao(db.getConn());
            EventDao eventDao = new EventDao(db.getConn());

            User[] users = loadRequest.getUsers();
            Person[] people = loadRequest.getPersons();
            Event[] events = loadRequest.getEvents();

            userDao.insertMany(users);
            personDao.insertMany(people);
            eventDao.insertMany(events);

            String message = String.format("Successfully added %d users, %d persons, and %d events to the database",
                    users.length, people.length, events.length);

            db.closeConnection(true);
            response = new LoadResponse(message);
        }catch (DataAccessException e) {
            try {
                db.closeConnection(false);
            }
            catch (DataAccessException d){
                ErrorResponse error = new ErrorResponse(d.getMessage());
                return error;
            }
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error;
        }
        return response;
    }

}
