package response;

/**
 * This object holds the response data for event requests
 */
public class EventResponse extends Response{
    private String associatedUsername;
    private String eventID;
    private String personID;
    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String eventType;
    private int year;

    public EventResponse() {

    }

    public String getEventID() {
        return this.eventID;
    }

    public void setEventID(final String eventID) {
        this.eventID = eventID;
    }

    public String getAssociatedUsername() {
        return this.associatedUsername;
    }

    public void setAssociatedUsername(final String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getPersonID() {
        return this.personID;
    }

    public void setPersonID(final String personID) {
        this.personID = personID;
    }

    public float getLatitude() {
        return this.latitude;
    }

    public void setLatitude(final float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return this.longitude;
    }

    public void setLongitude(final float longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(final int year) {
        this.year = year;
    }
}
