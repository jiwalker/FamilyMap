package handler;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

import response.LoadResponse;
import service.LoadService;
import request.LoadRequest;
import com.google.gson.*;

public class LoadHandler extends Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {

            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                Headers reqHeaders = exchange.getRequestHeaders();

                InputStream reqBody = exchange.getRequestBody();
                String reqData = readString(reqBody);

                System.out.println(reqData);

                Gson gson = new Gson();
                LoadRequest loadRequest = gson.fromJson(reqData, LoadRequest.class);
                LoadService loadService = new LoadService();
                LoadResponse loadResponse = (LoadResponse) loadService.load(loadRequest);
                String jsonStr = gson.toJson(loadResponse);

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
