package service;
import dao.*;
import model.Event;
import model.Person;
import model.User;
import response.ErrorResponse;
import response.FillResponse;
import response.Response;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

/**
 * FillService will fill the database with generated data for ancestors of a given user
 */
public class FillService {

    public FillService() {
    }

    /**
     * fill will generate data for a user's family tree, and delete any pre-existing data
     * @param username will be used to map to and from relatives
     * @param generations will indicate how many generations of data should be generated
     * @return
     */
    public Response fill(String username, int generations) {

        if (generations < 0) {
            ErrorResponse errorResponse = new ErrorResponse("Invalid generation parameter. Please enter a positive number");
            return errorResponse;
        }
        FillResponse fillResponse = null;
        NameGenerator names = new NameGenerator();
        EventGenerator generator = new EventGenerator();
        Queue<Person> fathers = new LinkedList<>();
        Queue<Person> mothers = new LinkedList<>();
        ArrayList<Event> events = new ArrayList<>();
        ArrayList<Person> people = new ArrayList<>();

        Database db = new Database();
        boolean closed = true;

        try {
            db.openConnection();
            closed = false;
            db.createTables();
            db.clearUserInfo(username);
            String userDad = null;
            String userMom = null;

            for (int i = generations; i > 0; --i){
                int numNewCouples = (int) Math.pow(2, i); //Calculate how many new couples there will be

                for (int j = 0; j < (numNewCouples / 2); ++j){
                    boolean parents = true;
                    String manDad = null;
                    String manMom = null;
                    String womanDad = null;
                    String womanMom = null;

                    if (i == generations) {     //If this is the oldest generation, then they won't have parent data
                        parents = false;
                    }
                    if (parents) {
                        manDad = ((LinkedList<Person>) fathers).element().getPersonID();
                        ((LinkedList<Person>) fathers).pop();
                        manMom = ((LinkedList<Person>) mothers).element().getPersonID();
                        ((LinkedList<Person>) mothers).pop();
                        womanDad = ((LinkedList<Person>) fathers).element().getPersonID();
                        ((LinkedList<Person>) fathers).pop();
                        womanMom = ((LinkedList<Person>) mothers).element().getPersonID();
                        ((LinkedList<Person>) mothers).pop();

                    }
                    String manID = UUID.randomUUID().toString();
                    String manFName = names.getBoyName();
                    String manLName = names.getLastName();
                    String womanID = UUID.randomUUID().toString();
                    String womanFName = names.getGirlName();
                    String womanLName = names.getLastName();
                    if (i == 1){
                        userDad = manID;
                        userMom = womanID;
                    }
                    Person man = new Person(manID, username, manFName, manLName, "m", manDad, manMom, womanID);
                    fathers.add(man);
                    people.add(man);
                    Person woman = new Person(womanID, username, womanFName, womanLName, "f", womanDad, womanMom, manID);
                    mothers.add(woman);
                    people.add(woman);
                    EventGenerator.Location newLocation;
                    newLocation = generator.getLocation();
                    Event manBirth = new Event(UUID.randomUUID().toString(), username, manID,
                            newLocation.getLatitude(), newLocation.getLongitude(), newLocation.getCountry(),
                            newLocation.getCity(), "birth", generator.getBirthYear(i));

                    events.add(manBirth);

                    newLocation = generator.getLocation();

                    Event womanBirth = new Event(UUID.randomUUID().toString(), username, womanID,
                            newLocation.getLatitude(), newLocation.getLongitude(), newLocation.getCountry(),
                            newLocation.getCity(), "birth", generator.getBirthYear(i));

                    events.add(womanBirth);

                    newLocation = generator.getLocation();

                    Event manDeath = new Event(UUID.randomUUID().toString(), username, manID,
                            newLocation.getLatitude(), newLocation.getLongitude(), newLocation.getCountry(),
                            newLocation.getCity(), "death", generator.getDeathYear(manBirth.getYear()));

                    events.add(manDeath);

                    newLocation = generator.getLocation();

                    Event womanDeath = new Event(UUID.randomUUID().toString(), username, womanID,
                            newLocation.getLatitude(), newLocation.getLongitude(), newLocation.getCountry(),
                            newLocation.getCity(), "death", generator.getDeathYear(womanBirth.getYear()));

                    events.add(womanDeath);

                    newLocation = generator.getLocation();

                    Event marriage = new Event(UUID.randomUUID().toString(), username, manID,
                            newLocation.getLatitude(), newLocation.getLongitude(), newLocation.getCountry(),
                            newLocation.getCity(), "marriage", generator.getMarriageYear(manBirth.getYear()));

                    Event marriageMomCopy = new Event(UUID.randomUUID().toString(), username, womanID,
                            marriage.getLatitude(), marriage.getLongitude(), marriage.getCountry(),
                            marriage.getCity(), "marriage", marriage.getYear());

                    events.add(marriage);
                    events.add(marriageMomCopy);
                }
            }

            UserDao userDao = new UserDao(db.getConn());
            User user = userDao.find(username);

            //Create person data for the user
            Person person = new Person(user.getPersonID(), user.getUserName(), user.getFirstName(),
                    user.getLastName(), user.getGender(), userDad, userMom, "");

            people.add(person);

            EventGenerator.Location newLocation = new EventGenerator().getLocation();

            Event userBirth = new Event(UUID.randomUUID().toString(), username, person.getPersonID(),
                    newLocation.getLatitude(), newLocation.getLongitude(), newLocation.getCountry(),
                    newLocation.getCity(), "birth", generator.getBirthYear(0));

            events.add(userBirth);

            PersonDao personDao = new PersonDao(db.getConn());
            Person[] personArray = new Person[people.size()];

            for (int i = 0; i < people.size(); ++i) {
                personArray[i] = people.get(i);
            }

            personDao.insertMany(personArray);

            EventDao eventDao = new EventDao(db.getConn());
            Event[] eventArray = new Event[events.size()];

            for (int i = 0; i < events.size(); ++i) {
                eventArray[i] = events.get(i);
            }

            eventDao.insertMany(eventArray);

            db.closeConnection(true);
            closed = true;
            db = null;
        }
        catch (DataAccessException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());

            return error;
        }

        finally {
            if (!closed) {
                try {
                    db.closeConnection(false);
                    closed = true;
                }
                catch (DataAccessException d) {
                    ErrorResponse error = new ErrorResponse(d.getMessage());
                }
            }
        }


        StringBuilder response = new StringBuilder();
        int pSize = people.size();
        int eSize = events.size();
        response.append("Successfully added " + pSize + " persons and " + eSize + " events to the database.");
        fillResponse = new FillResponse(response.toString());

        return fillResponse;
    }
}
