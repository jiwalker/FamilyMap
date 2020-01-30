package model;

/**
 * User is a container for the data needed to fill in the rows of the user table in the database
 */
public class User {
    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String personID;

    public User(String userName, String password, String email, String firstName, String lastName, String gender, String personID) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
    }

    public boolean equals(Object o) {
        if (o == null) {return false;}
        if (!(o instanceof User)){
            return false;
        }
        User user = (User) o;
        if ((this.userName.equals(user.userName)) &&
                (this.password.equals((user.password))) &&
                (this.email.equals(user.email)) &&
                (this.firstName.equals(user.firstName)) &&
                (this.lastName.equals(user.lastName)) &&
                (this.gender.equals(user.gender)) &&
                (this.personID.equals(user.personID))){
            return true;
        }
        else {
            return false;
        }
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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
    public String getPersonID() {
        return personID;
    }
    public void setPersonID(String personID) {
        this.personID = personID;
    }


}
