package response;

/**
 * This object holds response data for a Person object
 */
public class PersonResponse extends Response {
    private String associatedUsername;
    private String personID;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;

    public PersonResponse() {
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

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public String getFatherID() {
        return this.fatherID;
    }

    public void setFatherID(final String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return this.motherID;
    }

    public void setMotherID(final String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return this.spouseID;
    }

    public void setSpouseID(final String spouseID) {
        this.spouseID = spouseID;
    }
}
