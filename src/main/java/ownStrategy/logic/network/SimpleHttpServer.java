package ownStrategy.logic.network;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleHttpServer {

    // To jest nasz "Magazyn" na gotowe danie (JSON), żeby Handler miał do niego dostęp
    private static String globalJsonData = "";

    public static void startServer(String json) {
        globalJsonData = json; // Kucharz odkłada gotowe pudełko tutaj
        try {
            // 1. Otwieramy okienko nr 8000
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

            // 2. Ustalamy, że jak ktoś zapuka do "/api/data", to obsługuje go MyHandler
            server.createContext("/api/data", new MyHandler());

            server.setExecutor(null);
            server.start();

            System.out.println("\n>>> SERVER LAUNCHED! <<<");
            System.out.println("Here you can collect your data: http://localhost:8000/api/data");
            System.out.println("Don't close the console window or the server will shut down!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // To jest pracownik w okienku, który podaje dane
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Nagłówek: "Pozwalam każdemu (All) pobrać te dane" (Ważne dla przeglądarki!)
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            // Nagłówek: "To co wysyłam to JSON"
            t.getResponseHeaders().add("Content-Type", "application/json");

            // Wysyłanie
            byte[] response = globalJsonData.getBytes();
            t.sendResponseHeaders(200, response.length);
//zamieniamy na bajty- wysyłam komunikat erfolgreich created i zawsze na drugim parametrze podajemy liczbę bajtów
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
