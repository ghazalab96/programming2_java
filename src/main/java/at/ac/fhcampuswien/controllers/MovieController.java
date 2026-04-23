package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.services.MovieService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MovieController implements HttpHandler {
    private final String BASE = "/api/movies/";
    private final List<Movie> movies = Movie.generateDummyMovies();
    private final MovieService movieService = new MovieService(movies);
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

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
    }

    private void handleGetAllRequest(String method, HttpExchange exchange) throws IOException {
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

    private void handleAddRequest(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "POST" -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Movie movie = gson.fromJson(requestBody, Movie.class);

                boolean added = movieService.addMovie(movie);

                if (!added) {
                    String response = "{ \"error\": \"Invalid movie data or movie already exists\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
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

    private void handleDeleteRequest(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "DELETE" -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Movie movieToDelete = gson.fromJson(requestBody, Movie.class);

                boolean deleted = movieService.deleteMovie(movieToDelete);

                if (!deleted) {
                    String response = "{ \"error\": \"Movie not found or invalid movie data\" }";
                    ApiUtils.sendResponse(exchange, 404, response);
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

    private void handleUpdateRequest(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "PUT" -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Movie updatedMovie = gson.fromJson(requestBody, Movie.class);

                boolean updated = movieService.updateMovie(updatedMovie);

                if (!updated) {
                    String response = "{ \"error\": \"Movie not found or invalid movie data\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
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

    private void handleSearchRequest(String method, HttpExchange exchange) throws IOException {
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
}