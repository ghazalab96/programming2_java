package at.ac.fhcampuswien.controllers;
import java.nio.charset.StandardCharsets;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.models.Movie;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.nio.charset.StandardCharsets;


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




    private void handleAddRequest(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "POST" -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Movie movie = parseMovieWithoutId(requestBody);

                if (movie == null || !isValidMovie(movie)) {
                    String response = "{ \"error\": \"Invalid movie data\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
                    return;
                }

                if (movieExists(movie)) {
                    String response = "{ \"error\": \"Movie already exists\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
                    return;
                }

                movies.add(movie);
                String response = "{ \"message\": \"Movie added successfully\" }";
                ApiUtils.sendResponse(exchange, 201, response);
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private boolean isValidMovie(Movie movie) {
        return movie.getTitle() != null && !movie.getTitle().trim().isEmpty()
                && movie.getGenre() != null && !movie.getGenre().trim().isEmpty()
                && movie.getReleaseYear() > 1800;
    }

    private boolean movieExists(Movie movie) {
        for (Movie existingMovie : movies) {
            if (sameMovieData(existingMovie, movie)) {
                return true;
            }
        }
        return false;
    }

    private boolean sameMovieData(Movie movie1, Movie movie2) {
        return movie1.getTitle().equalsIgnoreCase(movie2.getTitle())
                && movie1.getGenre().equalsIgnoreCase(movie2.getGenre())
                && movie1.getReleaseYear() == movie2.getReleaseYear();
    }

    private Movie parseMovieWithoutId(String json) {
        try {
            String title = extractJsonValue(json, "title");
            String genre = extractJsonValue(json, "genre");
            String releaseYearString = extractJsonValue(json, "releaseYear");

            if (title == null || genre == null || releaseYearString == null) {
                return null;
            }

            int releaseYear = Integer.parseInt(releaseYearString);
            return new Movie(title, genre, releaseYear);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) {
            return null;
        }

        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) {
            return null;
        }

        int valueStart = colonIndex + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (valueStart >= json.length()) {
            return null;
        }

        if (json.charAt(valueStart) == '"') {
            int valueEnd = json.indexOf("\"", valueStart + 1);
            if (valueEnd == -1) {
                return null;
            }
            return json.substring(valueStart + 1, valueEnd);
        } else {
            int valueEnd = valueStart;
            while (valueEnd < json.length()
                    && json.charAt(valueEnd) != ','
                    && json.charAt(valueEnd) != '}'
                    && !Character.isWhitespace(json.charAt(valueEnd))) {
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd);
        }
    }
    private void handleDeleteRequest(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "DELETE" -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Movie movieToDelete = parseMovieWithoutId(requestBody);

                if (movieToDelete == null || !isValidMovie(movieToDelete)) {
                    String response = "{ \"error\": \"Invalid movie data\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
                    return;
                }

                Iterator<Movie> iterator = movies.iterator();
                while (iterator.hasNext()) {
                    Movie currentMovie = iterator.next();
                    if (sameMovieData(currentMovie, movieToDelete)) {
                        iterator.remove();
                        String response = "{ \"message\": \"Movie deleted successfully\" }";
                        ApiUtils.sendResponse(exchange, 200, response);
                        return;
                    }
                }

                String response = "{ \"error\": \"Movie not found\" }";
                ApiUtils.sendResponse(exchange, 404, response);
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private void handleUpdateRequest(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "PUT" -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Movie updatedMovie = parseMovieWithId(requestBody);

                if (updatedMovie == null || updatedMovie.getId() == null || !isValidMovie(updatedMovie)) {
                    String response = "{ \"error\": \"Invalid movie data\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
                    return;
                }

                for (Movie currentMovie : movies) {
                    if (currentMovie.getId().equals(updatedMovie.getId())) {
                        currentMovie.setTitle(updatedMovie.getTitle());
                        currentMovie.setGenre(updatedMovie.getGenre());
                        currentMovie.setReleaseYear(updatedMovie.getReleaseYear());

                        String response = "{ \"message\": \"Movie updated successfully\" }";
                        ApiUtils.sendResponse(exchange, 200, response);
                        return;
                    }
                }

                String response = "{ \"error\": \"Movie not found\" }";
                ApiUtils.sendResponse(exchange, 404, response);
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private Movie parseMovieWithId(String json) {
        try {
            String idString = extractJsonValue(json, "id");
            String title = extractJsonValue(json, "title");
            String genre = extractJsonValue(json, "genre");
            String releaseYearString = extractJsonValue(json, "releaseYear");

            if (idString == null || title == null || genre == null || releaseYearString == null) {
                return null;
            }

            UUID id = UUID.fromString(idString);
            int releaseYear = Integer.parseInt(releaseYearString);

            Movie movie = new Movie(title, genre, releaseYear);
            movie.setId(id);
            return movie;
        } catch (Exception e) {
            return null;
        }
    }
}