package model;

/**
 * Person is a container for the data needed to fill in a row in the person table of the database
 */
public class Person {

    private String personID;
    private String associatedUsername;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;

    public Person(String personID, String associatedUsername, String firstName, String lastName,
                  String gender, String fatherID, String motherID, String spouseID) {
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }
    public boolean equals(Object o) {
        if (o == null) {return false;}
        if (!(o instanceof Person)){
            return false;
        }
        Person person = (Person) o;
        if ((this.personID.equals(person.personID)) &&
                (this.associatedUsername.equals((person.associatedUsername))) &&
                (this.firstName.equals(person.firstName)) &&
                (this.lastName.equals(person.lastName)) &&
                (this.fatherID.equals(person.fatherID)) &&
                (this.motherID.equals(person.motherID)) &&
                (this.spouseID.equals(person.spouseID)) &&
                (this.gender.equals(person.gender))){
            return true;
        }
        else {
            return false;
        }
    }
    public String getPersonID() {
        return personID;
    }
    public void setPersonID(String personID) {
        this.personID = personID;
    }
    public String getAssociatedUsername() {
        return associatedUsername;
    }
    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getFatherID() {
        return fatherID;
    }
    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }
    public String getMotherID() {
        return motherID;
    }
    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }
    public String getSpouseID() {
        return spouseID;
    }
    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }
}
