package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

import dao.DataAccessException;
import response.ErrorResponse;
import response.PersonResponse;
import response.Response;
import service.PersonService;

public class PersonHandler extends Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        boolean curUser;
        String personID = null;
        StringBuilder urlArgs = new StringBuilder();
        urlArgs.append(exchange.getRequestURI());
        if (urlArgs.toString().endsWith("/person")){
            curUser = true;
        }
        else {
            curUser = false;
            String[] pieces = urlArgs.toString().split("/");
            if (pieces.length == 3){
                personID = pieces[pieces.length - 1];
                //System.out.println(personID);
            }
            else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }
        try {

            if (exchange.getRequestMethod().toLowerCase().equals("get")) {

                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {

                    String authToken = reqHeaders.getFirst("Authorization");

                    PersonService personService = new PersonService();
                    Gson gson = new Gson();
                    String jsonStr = null;
                    if (curUser){
                        Response[] responses = personService.getFamily(authToken);

                        if (responses[0].getMessage() != null && responses.length == 1) {
                            jsonStr = gson.toJson(responses[0]);
                        }
                        else {
                            PersonResponse[] personResponses = (PersonResponse[]) responses;
                            jsonStr = gson.toJson(personResponses);
                        }
                    }
                    else {
                        Response response = personService.getPerson(authToken, personID);
                        if (response instanceof ErrorResponse){
                            jsonStr = gson.toJson(response);
                        }
                        else{
                            PersonResponse personResponse = (PersonResponse) response;
                            jsonStr = gson.toJson(personResponse);
                        }

                    }

                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream respBody = exchange.getResponseBody();
                    writeString(jsonStr, respBody);

                    exchange.getResponseBody().close();
                    success = true;
                }

            }

            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }
        catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }

}
