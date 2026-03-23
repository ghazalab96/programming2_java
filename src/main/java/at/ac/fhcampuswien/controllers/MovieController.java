package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.models.Movie;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class MovieController implements HttpHandler {
    private final String BASE = "/api/movies/";
    private final List<Movie> movies = Movie.generateDummyMovies();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case BASE + "getAll" -> handleGetAllRequest(method, exchange);
            case BASE + "add" -> handleAddRequest(method, exchange);
            case BASE + "delete" -> handleDeleteRequest(method, exchange);
            case BASE + "update" -> handleUpdateRequest(method, exchange);
            default -> {
                String response = "{ \"error\": \"Path not found\" }";
                ApiUtils.sendResponse(exchange, 404, response);
            }
        }
    }

    private void handleGetAllRequest(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "GET" -> {
                StringBuilder response = new StringBuilder("[");
                for (int i = 0; i < movies.size(); i++) {
                    response.append(movieToJson(movies.get(i)));
                    if (i < movies.size() - 1) {
                        response.append(",");
                    }
                }
                response.append("]");
                ApiUtils.sendResponse(exchange, 200, response.toString());
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private String movieToJson(Movie movie) {
        return "{"
                + "\"id\":\"" + movie.getId() + "\","
                + "\"title\":\"" + escapeJson(movie.getTitle()) + "\","
                + "\"genre\":\"" + escapeJson(movie.getGenre()) + "\","
                + "\"releaseYear\":" + movie.getReleaseYear()
                + "}";
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"");
    }

    // methods from Person 2 and Person 3 are added here later
}