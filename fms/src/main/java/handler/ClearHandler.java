package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.*;

import response.ClearResponse;
import response.Response;
import service.ClearService;

public class ClearHandler extends Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {


            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                Gson gson = new Gson();

                Headers reqHeaders = exchange.getRequestHeaders();

                ClearService clearService = new ClearService();

                Response response = clearService.clear();
                String jsonStr = gson.toJson(response);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                OutputStream respBody = exchange.getResponseBody();

                writeString(jsonStr, respBody);

                exchange.getResponseBody().close();

                success = true;
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
