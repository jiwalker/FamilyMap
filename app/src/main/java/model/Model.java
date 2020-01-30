package model;

import android.provider.Settings;
import android.widget.Filter;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Model {

    private static Model instance;
    private ServerProxy serverProxy;

    static {
        initialize();
    }
    public static Model initialize() {
        if (instance == null){
            instance = new Model();
        }
        return instance;
    }
    public static void connect(String host, String port) {
        instance._connect(host, port);
    }
    public static boolean login(String username, String password) {
        return instance._login(username, password);
    }
    public static boolean clear() {return instance._clear(); }
    public static boolean register(RegisterRequest request) {
        User newUser = new User(request.getUserName(), request.getPassword(), request.getEmail(), request.getFirstName()
                , request.getLastName(), request.getGender(), null);
        return instance._register(newUser);
    }
    public boolean isLoggedIn() {return loggedIn;}

    private Map<String, Person> people;
    private Map<String, Event> events;
    private Map<String, List<Event>> personEvents;
    private Settings settings;
    private Filter filter;
    private List<String> eventTypes;
    private Map<String, Model.MapColor> eventTypeColors;
    private Person user;
    private Set<String> paternalAncestors;
    private Set<String> maternalAncestors;
    private Map<String, List<Person>> personChildren;
    private AuthToken currentToken;
    private boolean loggedIn;
    private Event currentEvent;

    private void _connect(String host, String port) {
        serverProxy = new ServerProxy(host, port);
    }

    private Model() {
        people = new HashMap<>();
        events = new HashMap<>();
        personEvents = new HashMap<>();
        settings = new Settings();
        //filter = new Filter();
        eventTypes = new ArrayList<>();
        eventTypeColors = new HashMap<>();
        user = null;
        paternalAncestors = new HashSet<>();
        maternalAncestors = new HashSet<>();
        personChildren = new HashMap<>();
    }

    private boolean _login(String username, String password){

        Response response = serverProxy.login(username,password);
        if (response == null) {
            loggedIn = false;
            return loggedIn;
        }
        if (response.getMessage() != null && response.getMessage().contains("Invalid")){
            loggedIn = false;
            return loggedIn;
        }
        if (response instanceof LoginResponse){
            System.out.println(response.toString());
            LoginResponse loginResponse = (LoginResponse) response;
            currentToken = new AuthToken(loginResponse.getAuthToken(), loginResponse.getUserName());
            this.user = serverProxy.getPerson(currentToken.getAuthToken(), loginResponse.getPersonID());
            _fillModel();
            loggedIn = true;
            return true;
        }

        loggedIn = false;
        return false;
    }

    private boolean _clear() {
        boolean result = serverProxy.clear();
        if (result){
            currentToken = null;
            user = null;
            people.clear();
            events.clear();
            personEvents.clear();
            eventTypes.clear();
            eventTypeColors.clear();
            paternalAncestors.clear();
            maternalAncestors.clear();
            personChildren.clear();
            loggedIn = false;
            return true;
        }
        loggedIn = false;
        return false;
    }

    private boolean _register(User user) {
        LoginResponse response = serverProxy.register(user.getUserName(), user.getPassword(), user.getEmail(),
                user.getFirstName(), user.getLastName(), user.getGender());
        if (response == null) {
            loggedIn = false;
            return false;
        }
        else {
            try {
                currentToken = new AuthToken(response.getAuthToken(), response.getUserName());
                this.user = serverProxy.getPerson(response.getAuthToken(), response.getPersonID());

                _fillModel();
                loggedIn = true;
            }
            catch (NullPointerException e){
                loggedIn = false;
            }
            return loggedIn;
        }
    }

    private void _fillModel(){
        if (user == null) {
            return;
        }

        loadPeopleData();
        createFamilyTree();
        loadEventData();
        createMapColors();
    }

    private void loadPeopleData(){
        Person[] peopleArray = serverProxy.getUserFamily(currentToken.getAuthToken());
        for (Person person : peopleArray) {
            List<Event> emptyEList = new LinkedList<>();
            people.put(person.getPersonID(), person);
            personEvents.put(person.getPersonID(), emptyEList);  //Creates the index for the personEvents map
            List<Person> emptyPList = new LinkedList<>();
            personChildren.put(person.getPersonID(), emptyPList);
        }
        findChildren(peopleArray);
    }

    private void loadEventData(){
        Event[] eventArray = serverProxy.getAllEvents(currentToken.getAuthToken());
        for (Event event: eventArray){
            events.put(event.getEventID(), event);
            if (!eventTypes.contains(event.getEventType().toLowerCase())){
                eventTypes.add(event.getEventType().toLowerCase());       //Puts event into events map
            }
            personEvents.get(event.getPersonID()).add(event); //Puts event into personEvents map
        }
    }

    private void createMapColors(){
        for (int i = 0; i < eventTypes.size(); i++) {
            Integer index = i;
            eventTypeColors.put(eventTypes.get(i), new MapColor(index));
        }
    }

    private void createFamilyTree(){
        String userDadID = user.getFatherID();
        String userMomID = user.getMotherID();
        paternalAncestors = getAncestors(userDadID);
        maternalAncestors = getAncestors(userMomID);
    }

    private void findChildren(Person[] peopleArray){
        for (Person parent: peopleArray){
            for (Person child: peopleArray){
                if ((parent.getPersonID().equals(child.getFatherID()))
                        || parent.getPersonID().equals(child.getMotherID())) {
                    personChildren.get(parent.getPersonID()).add(child);
                }
            }
        }
    }

    private Set<String> getAncestors(String personID){
        Set<String> ancestors = new HashSet<>();
        ancestors.add(personID);
        Person current = people.get(personID);
        String dadID = current.getFatherID();
        String momID = current.getMotherID();

        if (dadID != null) {
            ancestors.addAll(getAncestors(dadID));
        }
        if (momID != null) {
            ancestors.addAll(getAncestors(momID));
        }

        return ancestors;
    }


    public class MapColor {
        private float colorValue;
        public MapColor(Integer index){
            float value = (float) index * 40;
            colorValue = value % 330;
        }
        public float getColorValue() {return colorValue;}
    }

    public List<Person> getFamily(String personID){
        Person mainPerson = people.get(personID);
        List<Person> family = new LinkedList<>();
        if (mainPerson.getFatherID() != null) {
            family.add(people.get(mainPerson.getFatherID()));
        }
        if (mainPerson.getMotherID() != null) {
            family.add(people.get(mainPerson.getMotherID()));
        }
        if (mainPerson.getSpouseID() != null) {
            family.add(people.get(mainPerson.getSpouseID()));
        }
        family.addAll(personChildren.get(personID));
        return family;
    }

    public Person getPerson(String personID){
        return people.get(personID);
    }

    public Map<String, Person> getPeople() {
        return this.people;
    }

    public Map<String, Event> getEvents() {
        return this.events;
    }

    public Map<String, List<Event>> getAllPersonEvents() {
        return this.personEvents;
    }

    public List<Event> getPersonEvents(String personID){
        return personEvents.get(personID);
    }

    public Settings getSettings() {
        return this.settings;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public List<String> getEventTypes() {
        return this.eventTypes;
    }

    public Map<String, Model.MapColor> getEventTypeColors() {
        return this.eventTypeColors;
    }

    public Person getUser() {
        return this.user;
    }

    public Set<String> getPaternalAncestors() {
        return this.paternalAncestors;
    }

    public Set<String> getMaternalAncestors() {
        return this.maternalAncestors;
    }

    public Map<String, List<Person>> getPersonChildren() {
        return this.personChildren;
    }

    public AuthToken getCurrentToken() {
        return this.currentToken;
    }

    public Event getCurrentEvent(){ return this.currentEvent; }

    public void setCurrentEvent(Event event){ currentEvent = event; }
}
