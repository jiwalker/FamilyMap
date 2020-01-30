package response;

/**
 * This object holds the response data for errors
 */
public class ErrorResponse extends Response {

    public ErrorResponse(String message) {super.setMessage(message); }

    public String getMessage() {
        return super.getMessage();
    }
}