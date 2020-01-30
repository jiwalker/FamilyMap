package response;

/**
 * This object holds the response data for the fill requests
 */
public class FillResponse extends Response{
    public FillResponse(String message) {super.setMessage(message); }

    public String getMessage() {
        return super.getMessage();
    }
}