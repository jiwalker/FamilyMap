package response;

/**
 * This object holds the response data for load requests
 */
public class LoadResponse extends Response {

    public LoadResponse(String response) {super.setMessage(response); }

    public String getMessage() {
        return super.getMessage();
    }
}