package model;

/**
 * Event is a container for all of the data needed for rows in the event table of the database
 */
public class Event {

    private String eventID;
    private String associatedUsername;
    private String personID;
    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String eventType;
    private int year;

    public Event(String eventId, String associatedUsername, String personId, float latitude,
                 float longitude, String country, String city, String eventType, int year) {
        this.eventID = eventId;
        this.associatedUsername = associatedUsername;
        this.personID = personId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }
    public Event(){}

    public boolean equals(Object o) {
        if (o == null) {return false;}
        if (!(o instanceof Event)){
            return false;
        }
        Event event = (Event) o;
        if ((this.eventID.equals(event.eventID)) &&
                (this.associatedUsername.equals((event.associatedUsername))) &&
                (this.personID.equals(event.personID)) &&
                (this.latitude == event.latitude) &&
                (this.longitude == event.longitude) &&
                (this.country.equals(event.country)) &&
                (this.city.equals(event.city)) &&
                (this.eventType.equals(event.eventType)) &&
                (this.year == event.year)){
            return true;
        }
        else {
            return false;
        }
    }

    public String getEventID() {
        return eventID;
    }
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
    public String getAssociatedUsername() {
        return associatedUsername;
    }
    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }
    public String getPersonID() {
        return personID;
    }
    public void setPersonID(String personID) {
        this.personID = personID;
    }
    public float getLatitude() {
        return latitude;
    }
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    public float getLongitude() {
        return longitude;
    }
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getEventType() {
        return eventType;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

}
