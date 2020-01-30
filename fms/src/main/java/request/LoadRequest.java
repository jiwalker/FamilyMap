package request;
import model.*;

/**
 * This is the object that holds the needed data for a Load Request that will be passed from the handler to the services
 */
public class LoadRequest {
    private User[] users;
    private Person[] persons;
    private Event[] events;

    /**
     * This is the constructor and will initialize the data members needed for the request
     * @param users array of user objects
     * @param persons array of person objects
     * @param events array of event objects
     */
    public LoadRequest(User[] users, Person[] persons, Event[] events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public User[] getUsers() {
        return users;
    }
    public Person[] getPersons() {
        return persons;
    }
    public Event[] getEvents() {
        return events;
    }
}