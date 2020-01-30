package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException{

        StringBuilder filePathBldr = new StringBuilder();
        filePathBldr.append("fms/web");
        filePathBldr.append(exchange.getRequestURI());
        if (filePathBldr.toString().endsWith("web/")){
            filePathBldr.append("index.html");
        }
        String filePathStr = filePathBldr.toString();

        Path filePath;
        File file = new File(filePathStr);
        if (!file.exists()){
            filePath = FileSystems.getDefault().getPath("fms/web/HTML/404.html");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
        }
        else {
            filePath = FileSystems.getDefault().getPath(filePathStr);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        }

        Files.copy(filePath, exchange.getResponseBody());

        exchange.getResponseBody().close();
    }
}
