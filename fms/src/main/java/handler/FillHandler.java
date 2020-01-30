package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

import service.ClearService;
import service.FillService;

public class FillHandler extends Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        boolean defaultHandle;
        String username = null;
        int generations = 4;
        StringBuilder url = new StringBuilder();
        url.append(exchange.getRequestURI());

        String[] pieces = url.toString().split("/");
        int length = pieces.length;
        if (length == 4){
            generations = Integer.parseInt(pieces[length -1]);
            username = pieces[length - 2];
            defaultHandle = false;
        }
        else if (length == 3){
            username = pieces[length - 1];
        }


        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                Gson gson = new Gson();

                Headers reqHeaders = exchange.getRequestHeaders();

                InputStream reqBody = exchange.getRequestBody();

                String reqData = readString(reqBody);

                System.out.println(reqData);

                FillService fillService = new FillService();

                String jsonStr = gson.toJson(fillService.fill(username, generations));

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                OutputStream respBody = exchange.getResponseBody();

                writeString(jsonStr, respBody);

                exchange.getResponseBody().close();

                success = true;
            }

            if (!success) {
                // The HTTP request was invalid somehow, so we return a "bad request"
                // status code to the client.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                // We are not sending a response body, so close the response body
                // output stream, indicating that the response is complete.
                exchange.getResponseBody().close();
            }
        }
        catch (IOException e) {
            // Some kind of internal error has occurred inside the server (not the
            // client's fault), so we return an "internal server error" status code
            // to the client.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            // We are not sending a response body, so close the response body
            // output stream, indicating that the response is complete.
            exchange.getResponseBody().close();

            // Display/log the stack trace
            e.printStackTrace();
        }
    }

}
