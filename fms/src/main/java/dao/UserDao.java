package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;

/**
 * The User DAO handles the data transactions on the database for the User table and User objects
 */
public class UserDao {
    private Connection conn;

    public UserDao(Connection conn) { this.conn = conn; }

    /**
     * Inserts data into the user table of the database
     * @param user is the object that stores the data
     * @return a boolean indicating if the transaction was committed
     * @throws DataAccessException
     */
    public boolean insertOne(User user) throws DataAccessException {
        boolean commit = true;
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO User (Username, Password, Email, FirstName, " +
                "LastName, Gender, PersonID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            commit = false;
            throw new DataAccessException("Error encountered while inserting into the database");
        }

        return commit;
    }

    /**
     * Inserts data from many users into the User table
     * @param users array of user ids
     * @return a boolean indicating if the transaction was committed
     * @throws DataAccessException
     */
    public boolean insertMany(User[] users) throws DataAccessException {
        for (User user : users) {
            boolean result = insertOne(user);
            if (result == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Searches the database for a row with the matching username
     * @param username is the key for the search
     * @return
     * @throws DataAccessException
     */
    public User find(String username) throws DataAccessException {
        User user = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM User WHERE Username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next() == true) {
                user = new User(rs.getString("Username"), rs.getString("Password"),
                        rs.getString("Email"), rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("PersonID"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding user");
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
     * Clears the User table in the database
     * @return
     */
    public boolean clear(){
        String sql = "DELETE FROM User;";
        String check = "SELECT * FROM User;";
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
