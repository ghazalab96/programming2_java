package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.models.Movie;

import java.util.List;
import java.util.Map;

public class MovieService {
    private final List<Movie> movies;

    public MovieService(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public boolean addMovie(Movie movie) {
        if (movie == null || !isValidMovie(movie) || movieExists(movie)) {
            return false;
        }

        if (movie.getId() == null) {
            movie.setId(java.util.UUID.randomUUID());
        }

        movies.add(movie);
        return true;
    }

    public boolean deleteMovie(Movie movieToDelete) {
        if (movieToDelete == null || !isValidMovie(movieToDelete)) {
            return false;
        }

        return movies.removeIf(movie -> sameMovieData(movie, movieToDelete));
    }

    public boolean updateMovie(Movie updatedMovie) {
        if (updatedMovie == null || updatedMovie.getId() == null || !isValidMovie(updatedMovie)) {
            return false;
        }

        return movies.stream()
                .filter(movie -> movie.getId().equals(updatedMovie.getId()))
                .findFirst()
                .map(movie -> {
                    movie.setTitle(updatedMovie.getTitle());
                    movie.setGenre(updatedMovie.getGenre());
                    movie.setReleaseYear(updatedMovie.getReleaseYear());
                    return true;
                })
                .orElse(false);
    }

    public List<Movie> searchMovies(Map<String, String> queryParams) {
        String title = queryParams.get("title");
        String genre = queryParams.get("genre");
        String releaseYear = queryParams.get("releaseYear");

        return movies.stream()
                .filter(movie -> title == null || title.isBlank() ||
                        movie.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(movie -> genre == null || genre.isBlank() ||
                        movie.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .filter(movie -> releaseYear == null || releaseYear.isBlank() ||
                        String.valueOf(movie.getReleaseYear()).contains(releaseYear))
                .toList();
    }

    private boolean isValidMovie(Movie movie) {
        return movie.getTitle() != null && !movie.getTitle().trim().isEmpty()
                && movie.getGenre() != null && !movie.getGenre().trim().isEmpty()
                && movie.getReleaseYear() > 1800 && movie.getReleaseYear() < 2027;
    }

    private boolean movieExists(Movie movie) {
        return movies.stream().anyMatch(existingMovie -> sameMovieData(existingMovie, movie));
    }

    private boolean sameMovieData(Movie movie1, Movie movie2) {
        return movie1.getTitle().equalsIgnoreCase(movie2.getTitle())
                && movie1.getGenre().equalsIgnoreCase(movie2.getGenre())
                && movie1.getReleaseYear() == movie2.getReleaseYear();
    }
}