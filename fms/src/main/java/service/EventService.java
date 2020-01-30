package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.AuthToken;
import model.Event;
import response.ErrorResponse;
import response.EventResponse;
import response.Response;

import java.util.ArrayList;

/**
 * Event Service will call the DAOs to get event data from the database and pass it to the handler
 */
public class EventService {


    public EventService() {

    }

    /**
     * getEvent returns the data for a single event
     * @param event_id is the primary key used to retrieve the data
     * @return
     */
    public Response getEvent(String authTokenStr, String event_id) throws DataAccessException{
        EventResponse response = new EventResponse();
        Database db = new Database();
        try {
            db.openConnection();
            db.createTables();

            AuthTokenDao atd = new AuthTokenDao(db.getConn());
            AuthToken token = atd.find(authTokenStr);

            EventDao eventDao = new EventDao(db.getConn());
            Event event = eventDao.find(event_id);

            db.closeConnection(true);

            if (token == null){
                ErrorResponse error = new ErrorResponse("Invalid auth token");
                return error;
            }
            if (!event.getAssociatedUsername().equals(token.getUserName())){
                ErrorResponse error = new ErrorResponse("Event does not belong to user");
                return error;
            }

            db.openConnection();

            eventDao = new EventDao(db.getConn());
            Event result = eventDao.find(event_id);
            if (result == null) {
                ErrorResponse error = new ErrorResponse("No data matched the query");
                db.closeConnection(false);
                db = null;
                return error;
            }
            else {
                response.setAssociatedUsername(result.getAssociatedUsername());
                response.setEventID(result.getEventID());
                response.setPersonID(result.getPersonID());
                response.setLatitude(result.getLatitude());
                response.setLongitude(result.getLongitude());
                response.setCountry(result.getCountry());
                response.setCity(result.getCity());
                response.setEventType(result.getEventType());
                response.setYear(result.getYear());

                db.closeConnection(true);
                db = null;
            }
        }
        catch (DataAccessException e) {
            db.closeConnection(false);
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error;
        }
        catch (NullPointerException e){
            ErrorResponse error = new ErrorResponse("Invalid data");
            return error;
        }
        return response;
    }

    /**
     * getAllEvents will return all the events associated with a username, which will be found using the authtoken
     * @param authTokenStr is the identifier that will be used to retrieve the data
     * @return
     */
    public Response[] getAllEvents(String authTokenStr) {

        ArrayList<EventResponse> familyAL = new ArrayList<>();
        ArrayList<Event> famEvents = new ArrayList<>();
        Database db = new Database();
        try {
            db.openConnection();
            db.createTables();

            AuthTokenDao atd = new AuthTokenDao(db.getConn());
            AuthToken token = atd.find(authTokenStr);

            db.closeConnection(true);

            if (token == null){
                ErrorResponse error = new ErrorResponse("Invalid auth token");
                Response[] errorResp = {error};
                return errorResp;
            }
            String username = token.getUserName();

            db.openConnection();

            EventDao eventDao = new EventDao(db.getConn());
            famEvents = eventDao.findMany(username);
            db.closeConnection(true);

            for (Event event : famEvents) {
                EventResponse response = new EventResponse();

                response.setAssociatedUsername(event.getAssociatedUsername());
                response.setEventID(event.getEventID());
                response.setPersonID(event.getPersonID());
                response.setLatitude(event.getLatitude());
                response.setLongitude(event.getLongitude());
                response.setCountry(event.getCountry());
                response.setCity(event.getCity());
                response.setEventType(event.getEventType());
                response.setYear(event.getYear());

                familyAL.add(response);
            }
        }
        catch (DataAccessException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            Response[] errorResp= {error};
            return errorResp;
        }
        EventResponse[] family = new EventResponse[familyAL.size()];
        for (int i = 0; i < family.length; ++i) {
            family[i] = familyAL.get(i);
        }
        return family;
    }
}
