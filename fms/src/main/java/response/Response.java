package response;

/**
 * Response is the parent class for the different response types that are used by the service classes
 */
public abstract class Response{
    private String message;
    public Response(){

    }
    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}