package service;
import dao.*;
import model.AuthToken;
import model.Person;
import model.User;
import response.ErrorResponse;
import response.PersonResponse;
import response.Response;

import java.util.ArrayList;

/**
 * PersonService houses the methods needed to pass person data to the person DAO
 */
public class PersonService {
    Database db;

    public PersonService(){

    }

    /**
     * getPerson will ask for and return the person data from a row that matches the person_id
     * @param personID is used to identify the relevant row of data
     * @param authTokenStr is the auth token to verify
     * @return one person's data
     */
    public Response getPerson(String authTokenStr, String personID){
        PersonResponse response = new PersonResponse();
        db = new Database();
        try {
            db.openConnection();
            db.createTables();

            AuthTokenDao atd = new AuthTokenDao(db.getConn());
            AuthToken token = atd.find(authTokenStr);

            PersonDao personDao = new PersonDao(db.getConn());
            Person person = personDao.findOne(personID);



            db.closeConnection(true);

            if (token == null){
                ErrorResponse error = new ErrorResponse("Invalid auth token");
                return error;
            }
            if (!token.getUserName().equals(person.getAssociatedUsername())){
                ErrorResponse error = new ErrorResponse("Person does not belong to this user");
                return error;
            }

            db.openConnection();

            personDao = new PersonDao(db.getConn());
            Person result = personDao.findOne(personID);

            if (result == null) {
                ErrorResponse error = new ErrorResponse("No data matched the query");
                db.closeConnection(false);
                db = null;
                return error;
            }

            response.setAssociatedUsername(result.getAssociatedUsername());
            response.setPersonID(result.getPersonID());
            response.setFirstName(result.getFirstName());
            response.setLastName(result.getLastName());
            response.setGender(result.getGender());
            response.setFatherID(result.getFatherID());
            response.setMotherID(result.getMotherID());
            response.setSpouseID(result.getSpouseID());


            db.closeConnection(true);
            db = null;
        }
        catch (DataAccessException e ) {
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
     * getFamily will return all the family data related to a single user
     * @param authTokenStr is the token associated with the user of the needed ancestors
     * @return
     */
    public Response[] getFamily(String authTokenStr ){
        ArrayList<PersonResponse> familyAL = new ArrayList<>();
        ArrayList<Person> familyMembers = new ArrayList<>();
        db = new Database();
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
            PersonDao personDao = new PersonDao(db.getConn());
            familyMembers = personDao.findMany(username);
            for (Person person : familyMembers) {
                PersonResponse response = new PersonResponse();

                response.setAssociatedUsername(person.getAssociatedUsername());
                response.setPersonID(person.getPersonID());
                response.setFirstName(person.getFirstName());
                response.setLastName(person.getLastName());
                response.setGender(person.getGender());
                response.setFatherID(person.getFatherID());
                response.setMotherID(person.getMotherID());
                response.setSpouseID(person.getSpouseID());

                familyAL.add(response);
            }
            db.closeConnection(true);
        }
        catch (DataAccessException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            Response[] errorResp= {error};
            return errorResp;
        }
        PersonResponse[] family =  new PersonResponse[familyAL.size()];
        for (int i = 0; i < family.length; ++i) {
            family[i] = familyAL.get(i);
        }
        return family;
    }
}
