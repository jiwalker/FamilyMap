package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Event;

/**
 * The Event DAO handles the data transactions on the database for the Event table and Event objects
 */
public class EventDao {
    private Connection conn;

    public EventDao(Connection conn) {
        this.conn = conn;
    }



    /**
     * Inserts an event into the event table,
     * will throw Data Access exception if it can't commit
     * @param event is an event object with the data that will be inserted in the DB
     * @return
     * @throws DataAccessException if interrupted
     */
    public boolean insert(Event event) throws DataAccessException {
        boolean commit = true;
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Event (EventID, AssociatedUsername, PersonID, Latitude, " +
                "Longitude, Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getAssociatedUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setDouble(4, event.getLatitude());
            stmt.setDouble(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());

            stmt.executeUpdate();
        } catch (SQLException e) {
            commit = false;
            throw new DataAccessException("Error encountered while inserting into the database");
        }

        return commit;
    }

    /**
     * Inserts multiple events into the database
     * @param events is an array of Event objects
     * @return a boolean to indicate if the insert was successful
     * @throws DataAccessException
     */
    public boolean insertMany(Event[] events) throws DataAccessException {

        for (Event event : events) {
            boolean result = insert(event);
            if (!result) {
                throw new DataAccessException();
            }
        }
        return true;
    }

    /**
     * Searches the database for the event matching the id
     * @param eventID Primary key in Event table
     * @return an Event object
     * @throws DataAccessException
     */
    public Event find(String eventID) throws DataAccessException {
        Event event = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM Event WHERE EventID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if (rs.next() == true) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
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
     * Searches the database for all events related to a given user in an ArrayList
     * @param associatedUsername is the username associated with the desired events
     * @return an ArrayList of Events
     * @throws DataAccessException
     */
    public ArrayList<Event> findMany(String associatedUsername) throws DataAccessException{
        ArrayList<Event> events = new ArrayList<>();
        Event event = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM Event WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, associatedUsername);
            rs = stmt.executeQuery();
            while (rs.next() == true) {
                String aUsername = rs.getString("associatedUsername");
                String eventID = rs.getString("eventID");
                String personID = rs.getString("personID");
                float latitude = rs.getFloat("latitude");
                float longitude = rs.getFloat("longitude");
                String country = rs.getString("country");
                String city = rs.getString("city");
                String eventType = rs.getString("eventType");
                int year = rs.getInt("year");
                event = new Event(eventID, associatedUsername, personID, latitude, longitude,
                        country, city, eventType, year);
                events.add(event);
                event = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return events;
    }

    /**
     * Clears the Event table in the database
     * @return
     */
    public boolean clear(){
        String sql = "DELETE FROM Event;";
        String check = "SELECT * FROM Event;";
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
