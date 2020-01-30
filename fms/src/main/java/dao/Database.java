package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The Database class will establish the connection with the database needed in order
 * to execute all other methods in the dao package that relate to the database
 */
public class Database {

    private Connection conn;

    static {
        try {
            final String driver = "org.sqlite.JDBC";
            Class.forName(driver);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() {
        return this.conn;
    }

    /**
     * Establishes a connection with the database
     * @return
     * @throws DataAccessException if connection unsuccessful
     */
    public Connection openConnection() throws DataAccessException {
        try {
            //The Structure for this Connection is driver:language:path
            //The pathing assumes you start in the root of your project unless given a non-relative path
            final String CONNECTION_URL = "jdbc:sqlite:familymap.sqlite";

            // Open a database connection to the file given in the path
            conn = DriverManager.getConnection(CONNECTION_URL);

            // Start a transaction
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Unable to open connection to database");
        }

        return conn;
    }

    /**
     * Closes the connection with the database, will throw a Data Access exception if the connection encounters errors
     * @param commit indicates if the db should commit the transaction
     * @throws DataAccessException if the transaction is interrupted
     */
    public void closeConnection(boolean commit) throws DataAccessException {
        try {
            if (commit) {
                //This will commit the changes to the database
                conn.commit();
            } else {
                //If we find out something went wrong, pass a false into closeConnection and this
                //will rollback any changes we made during this connection
                conn.rollback();
            }

            conn.close();
            conn = null;
        } catch (SQLException e) {
            //e.printStackTrace();
            throw new DataAccessException("Unable to close database connection");
        }
    }

    /**
     * Will instantiate tables in the database
     * @throws DataAccessException if there is a SQL error
     */
    public void createTables() throws DataAccessException {

        try (Statement stmt = conn.createStatement()){
            //First lets open our connection to our database.

            //We pull out a statement from the connection we just established
            //Statements are the basis for our transactions in SQL
            //Format this string to be exaclty like a sql create table command
            String sql = "CREATE TABLE IF NOT EXISTS\"Auth Token\" (\n" +
                    "\"TokenID\" TEXT NOT NULL UNIQUE,\n" +
                    "\"UserID\" TEXT NOT NULL,\n" +
                    "PRIMARY KEY(\"TokenID\"),\n" +
                    "FOREIGN KEY(\"UserID\") REFERENCES \"User\"\n" +
                    ");\n" +
                    "CREATE TABLE IF NOT EXISTS \"Event\" (\n" +
                    "\"EventID\" TEXT NOT NULL UNIQUE,\n" +
                    "\"AssociatedUsername\" TEXT NOT NULL,\n" +
                    "\"PersonID\" TEXT NOT NULL,\n" +
                    "\"Latitude\" REAL NOT NULL,\n" +
                    "\"Longitude\" REAL NOT NULL,\n" +
                    "\"Country\" TEXT NOT NULL,\n" +
                    "\"City\" TEXT NOT NULL,\n" +
                    "\"EventType\" TEXT NOT NULL,\n" +
                    "\"Year\" INTEGER NOT NULL,\n" +
                    "PRIMARY KEY(\"EventID\")\n" +
                    ");\n" +
                    "CREATE TABLE IF NOT EXISTS\"Person\" (\n" +
                    "\"PersonID\" TEXT NOT NULL UNIQUE,\n" +
                    "\"AssociatedUsername\" TEXT NOT NULL,\n" +
                    "\"FirstName\" TEXT NOT NULL,\n" +
                    "\"LastName\" TEXT NOT NULL,\n" +
                    "\"Gender\" TEXT NOT NULL,\n" +
                    "\"FatherID\" TEXT,\n" +
                    "\"MotherID\" TEXT,\n" +
                    "\"SpouseID\" TEXT,\n" +
                    "PRIMARY KEY(\"PersonID\"),\n" +
                    "FOREIGN KEY(\"SpouseID\") REFERENCES \"Person\"(\"PersonID\"),\n" +
                    "FOREIGN KEY(\"FatherID\") REFERENCES \"Person\"(\"PersonID\"),\n" +
                    "FOREIGN KEY(\"MotherID\") REFERENCES \"Person\"(\"PersonID\"),\n" +
                    "FOREIGN KEY(\"AssociatedUsername\") REFERENCES \"User\"(\"Username\")\n" +
                    ");\n" +
                    "CREATE TABLE IF NOT EXISTS \"User\" (\n" +
                    "\"Username\" TEXT NOT NULL UNIQUE,\n" +
                    "\"Password\" TEXT NOT NULL,\n" +
                    "\"Email\" TEXT NOT NULL,\n" +
                    "\"FirstName\" TEXT NOT NULL,\n" +
                    "\"LastName\" TEXT NOT NULL,\n" +
                    "\"Gender\" TEXT NOT NULL,\n" +
                    "\"PersonID\" TEXT NOT NULL UNIQUE,\n" +
                    "PRIMARY KEY(\"Username\")\n" +
                    ");";


            stmt.executeUpdate(sql);
            //if we got here without any problems we succesfully created the table and can commit
        } catch (SQLException e) {
            e.printStackTrace();
            //if our table creation caused an error, we can just not commit the changes that did happen
            throw new DataAccessException("SQL Error encountered while creating tables");
        }


    }

    /**
     * Clears the tables
     * @throws DataAccessException
     */
    public void clearTables() throws DataAccessException
    {

        try (Statement stmt = conn.createStatement()){
            String sql = "DELETE FROM Event";
            stmt.executeUpdate(sql);

            sql = "DELETE FROM User";
            stmt.executeUpdate(sql);

            sql = "DELETE FROM Person";
            stmt.executeUpdate(sql);

            sql = "DELETE FROM \"Auth Token\"";
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing tables");
        }
    }
    public void clearUserInfo(String username) throws DataAccessException{
        try (Statement stmt = conn.createStatement()){
            String sql = "DELETE FROM Event WHERE AssociatedUsername = \"" + username + "\";";
            stmt.executeUpdate(sql);

            sql = "DELETE FROM Person WHERE AssociatedUsername = \"" + username + "\";";
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while deleting user info");
        }
    }
}