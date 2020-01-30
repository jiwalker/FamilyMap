package service;
import dao.DataAccessException;
import dao.Database;
import java.sql.*;
import response.Response;
import response.ErrorResponse;
import response.ClearResponse;

/**
 * This class will implement the clear operation to empty the database of its data
 */
public class ClearService {
    private Connection conn;
    public ClearService(){}
    ClearResponse clearResponse;

    /**
     * clear() will erase all data from the database
     * @return
     */
    public Response clear(){
        ClearResponse clearResponse = new ClearResponse();
        try {
            Database db = new Database();
            db.openConnection();

            db.clearTables();

            db.closeConnection(true);
            db = null;
            clearResponse.setMessage("Clear Succeeded.");
        }
        catch (DataAccessException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return error;
        }

        return clearResponse;
    }
}
