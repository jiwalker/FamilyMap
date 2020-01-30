package handler;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import com.google.gson.*;

import service.RegisterService;
import request.RegisterRequest;

public class RegisterHandler extends Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {

            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                Headers reqHeaders = exchange.getRequestHeaders();

                InputStream reqBody = exchange.getRequestBody();
                String reqData = readString(reqBody);

                //System.out.println(reqData);

                Gson gson = new Gson();
                RegisterRequest registerRequest = gson.fromJson(reqData, RegisterRequest.class);
                RegisterService registerService = new RegisterService();
                String jsonStr = gson.toJson(registerService.register(registerRequest));

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
