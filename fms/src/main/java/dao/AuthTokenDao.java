package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.AuthToken;

/**
 * The class will take a connection to the database and execute insert and find functions of Authorization tokens
 */
public class AuthTokenDao {
    private Connection conn;

    public AuthTokenDao(Connection conn) {this.conn = conn; }

    /**
     * This method will insert data into the auth_token table
     * @param auth_token The authorization token that is requesting to be inserted
     * @return
     * @throws DataAccessException
     */
    public boolean insert(AuthToken auth_token) throws DataAccessException{
        boolean commit = true;


        String sql = "INSERT INTO \"Auth Token\" (TokenID, UserID) VALUES(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, auth_token.getAuthToken());
            stmt.setString(2, auth_token.getUserName());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            commit = false;
            throw new DataAccessException("Error encountered while inserting into the database");
        }

        return commit;
    }

    /**
     * Inserts multiple tokens into the Auth Token table of the database
     * @param tokens tokens to be inserted
     * @return a boolean value to indicate if the insert was successful
     * @throws DataAccessException
     */
    public boolean insertMany(ArrayList<AuthToken> tokens) throws DataAccessException {
        for (AuthToken token : tokens) {
            boolean result = insert(token);
            if (!result) {
                throw new DataAccessException();
            }
        }
        return true;
    }

    /**
     * This method will check and see if there exists a matching token, and will return it from the database
     * @param token_id is the id of the token being searched
     * @return
     * @throws DataAccessException
     */
    public AuthToken find(String token_id) throws DataAccessException{
        AuthToken authToken = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM \"Auth Token\" WHERE TokenID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token_id);
            rs = stmt.executeQuery();
            if (rs.next() == true) {
                authToken = new AuthToken(rs.getString("TokenID"), rs.getString("UserID"));
                return authToken;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding Auth Token");
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
     * Searches the database for auth tokens
     * @param tokenIDs
     * @return
     * @throws DataAccessException
     */
    public ArrayList<AuthToken> findMany(ArrayList<String> tokenIDs) throws DataAccessException {
        ArrayList<AuthToken> tokens = new ArrayList<AuthToken>();
        for (String id : tokenIDs) {
            tokens.add(find(id));
        }
        return tokens;
    }

    /**
     * Clears the Auth Token table in the Database
     * @return
     */
    public boolean clear(){
        String sql = "DELETE FROM \"Auth Token\";";
        String check = "SELECT * FROM \"Auth Token\";";
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

