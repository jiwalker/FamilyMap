package dao;

/**
 * Data Access exceptions are thrown when a SQL error occurs,
 * or when the transaction shouldn't be committed to the database
 */
public class DataAccessException extends Exception {
    DataAccessException(String message)
    {
        super(message);
    }

    DataAccessException()
    {
        super();
    }
}