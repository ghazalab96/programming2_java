package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.services.MovieService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MovieController implements HttpHandler {
    private final String BASE = "/api/movies/";
    private final MovieService movieService;
    private final Gson gson = new Gson();

    public MovieController() {
        MovieRepository movieRepository = new MovieRepository();
        this.movieService = new MovieService(movieRepository);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (path) {
                case BASE + "getAll" -> handleGetAllRequest(method, exchange);
                case BASE + "add" -> handleAddRequest(method, exchange);
                case BASE + "delete" -> handleDeleteRequest(method, exchange);
                case BASE + "update" -> handleUpdateRequest(method, exchange);
                case BASE + "search" -> handleSearchRequest(method, exchange);
                default -> {
                    String response = "{ \"error\": \"Path not found\" }";
                    ApiUtils.sendResponse(exchange, 404, response);
                }
            }
        } catch (JsonSyntaxException e) {
            sendError(exchange, 400, "Malformed JSON request");
        } catch (MovieNotFoundException e) {
            sendError(exchange, 404, e.getMessage());
        } catch (DatabaseException e) {
            sendError(exchange, 500, e.getMessage());
        } catch (Exception e) {
            sendError(exchange, 500, "Unexpected server error");
        }
    }

    private void handleGetAllRequest(String method, HttpExchange exchange)
            throws IOException, DatabaseException {
        switch (method) {
            case "GET" -> {
                String response = gson.toJson(movieService.getAllMovies());
                ApiUtils.sendResponse(exchange, 200, response);
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private void handleAddRequest(String method, HttpExchange exchange)
            throws IOException, DatabaseException {
        switch (method) {
            case "POST" -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Movie movie = gson.fromJson(requestBody, Movie.class);

                boolean added = movieService.addMovie(movie);

                if (!added) {
                    sendError(exchange, 400, "Invalid movie data");
                    return;
                }

                String response = "{ \"message\": \"Movie added successfully\" }";
                ApiUtils.sendResponse(exchange, 201, response);
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private void handleDeleteRequest(String method, HttpExchange exchange)
            throws IOException, DatabaseException, MovieNotFoundException {
        switch (method) {
            case "DELETE" -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Movie movieToDelete = gson.fromJson(requestBody, Movie.class);

                boolean deleted = movieService.deleteMovie(movieToDelete);

                if (!deleted) {
                    sendError(exchange, 400, "Invalid movie data");
                    return;
                }

                String response = "{ \"message\": \"Movie deleted successfully\" }";
                ApiUtils.sendResponse(exchange, 200, response);
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private void handleUpdateRequest(String method, HttpExchange exchange)
            throws IOException, DatabaseException, MovieNotFoundException {
        switch (method) {
            case "PUT" -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Movie updatedMovie = gson.fromJson(requestBody, Movie.class);

                boolean updated = movieService.updateMovie(updatedMovie);

                if (!updated) {
                    sendError(exchange, 400, "Invalid movie data");
                    return;
                }

                String response = "{ \"message\": \"Movie updated successfully\" }";
                ApiUtils.sendResponse(exchange, 200, response);
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private void handleSearchRequest(String method, HttpExchange exchange)
            throws IOException, DatabaseException {
        switch (method) {
            case "GET" -> {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> queryParams = ApiUtils.parseQueryParams(query);

                String response = gson.toJson(movieService.searchMovies(queryParams));
                ApiUtils.sendResponse(exchange, 200, response);
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = "{ \"error\": \"" + message + "\" }";
        ApiUtils.sendResponse(exchange, statusCode, response);
    }
}