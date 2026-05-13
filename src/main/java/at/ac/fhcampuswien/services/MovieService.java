package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;

import java.util.List;
import java.util.Map;

public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies() throws DatabaseException {
        return movieRepository.findAll();
    }

    public boolean addMovie(Movie movie) throws DatabaseException {
        if (movie == null || !isValidMovie(movie)) {
            return false;
        }

        movieRepository.add(movie);
        return true;
    }

    public boolean deleteMovie(Movie movieToDelete) throws DatabaseException, MovieNotFoundException {
        if (movieToDelete == null || !isValidMovie(movieToDelete)) {
            return false;
        }

        return movieRepository.delete(movieToDelete);
    }

    public boolean updateMovie(Movie updatedMovie) throws DatabaseException, MovieNotFoundException {
        if (updatedMovie == null || updatedMovie.getId() == null || !isValidMovie(updatedMovie)) {
            return false;
        }

        return movieRepository.update(updatedMovie);
    }

    public List<Movie> searchMovies(Map<String, String> queryParams) throws DatabaseException {
        String title = queryParams.get("title");
        String genre = queryParams.get("genre");
        String releaseYear = queryParams.get("releaseYear");

        return movieRepository.findAll().stream()
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
}