package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Person;

/**
 * The Person DAO handles the data transactions on the database for the Person table and Person objects
 */
public class PersonDao {
    private Connection conn;

    public PersonDao(Connection conn){this.conn = conn; }

    /**
     *
     * @param person contains the data needed to be inserted into the person table of the DB
     * @return
     * @throws DataAccessException if unable to commit
     */
    public boolean insert(Person person) throws DataAccessException {
        boolean commit = true;
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Person (PersonID, AssociatedUsername, FirstName, " +
                "LastName, Gender, FatherID, MotherID, SpouseID) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            commit = false;
            throw new DataAccessException("Error encountered while inserting into the database");
        }

        return commit;
    }

    /**
     * Inserts multiple persons data into person table
     * @param persons is an array of person objects
     * @return a boolean relating the success of the transaction
     * @throws DataAccessException
     */
    public boolean insertMany(Person[] persons) throws DataAccessException {

        for (Person person : persons) {
            boolean result = insert(person);
            if (result == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Will search database for person data
     * @param person_id is the key that will be queried in the database
     * @return a person  object
     * @throws DataAccessException
     */
    public Person findOne(String person_id) throws DataAccessException {
        Person person = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM Person WHERE PersonID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, person_id);
            rs = stmt.executeQuery();
            if (rs.next() == true) {
                person = new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Gender"),
                        rs.getString("FatherID"), rs.getString("MotherID"), rs.getString("SpouseID"));
                return person;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * Finds all Person data given a username
     * @param associatedUsername is the username associated with the Persons wanted
     * @return an ArrayList of Person objects
     * @throws DataAccessException
     */
    public ArrayList<Person> findMany(String associatedUsername) throws DataAccessException {
        ArrayList<Person> persons = new ArrayList<>();
        Person person = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM Person WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, associatedUsername);
            rs = stmt.executeQuery();
            while (rs.next() == true) {
                person = new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Gender"),
                        rs.getString("FatherID"), rs.getString("MotherID"), rs.getString("SpouseID"));
                persons.add(person);
                person = null;
            }
            return persons;         //?
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Clears the Person table in the database
     * @return
     */
    public boolean clear(){
        String sql = "DELETE FROM Person;";
        String check = "SELECT * FROM Person;";
        ResultSet rs = null;
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            try(PreparedStatement checker = conn.prepareStatement(check)){
                rs = checker.executeQuery();
                if (rs.next()){
                    return false;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
