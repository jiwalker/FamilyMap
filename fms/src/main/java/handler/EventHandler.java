package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

import dao.AuthTokenDao;
import dao.DataAccessException;
import model.Event;
import response.ErrorResponse;
import response.EventResponse;
import response.Response;
import service.EventService;

public class EventHandler extends Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        boolean curUser;
        String eventID = null;
        StringBuilder urlArgs = new StringBuilder();
        urlArgs.append(exchange.getRequestURI());
        if (urlArgs.toString().endsWith("/event")){
            curUser = true;
        }
        else {
            String[] pieces = urlArgs.toString().split("/");
            eventID = pieces[pieces.length - 1];
            //System.out.println(eventID);
            curUser = false;
        }


        try {

            if (exchange.getRequestMethod().toLowerCase().equals("get")) {

                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {

                    String authToken = reqHeaders.getFirst("Authorization");

                    EventService eventService = new EventService();
                    Gson gson = new Gson();
                    String jsonStr = null;
                    if (curUser){
                        Response[] responses = eventService.getAllEvents(authToken);

                        if (responses[0].getMessage() != null && responses.length == 1) {
                            jsonStr = gson.toJson(responses[0]);
                        }
                        else {
                            EventResponse[] eventResponses = (EventResponse[]) responses;
                            jsonStr = gson.toJson(eventResponses);
                        }
                    }
                    else {
                        try{
                            Response response = eventService.getEvent(authToken, eventID);
                            if (response instanceof ErrorResponse) {
                                jsonStr = gson.toJson(response);
                            }
                            else {
                                EventResponse eventResponse = (EventResponse) response;
                                jsonStr = gson.toJson(eventResponse);
                            }
                        }
                        catch (DataAccessException e) {
                            ErrorResponse error = new ErrorResponse("Issue accessing data");
                            jsonStr = gson.toJson(error);
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
