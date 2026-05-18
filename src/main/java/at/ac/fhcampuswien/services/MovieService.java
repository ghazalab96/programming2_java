package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepositoryInterface;
import at.ac.fhcampuswien.strategies.GenreSearchStrategy;
import at.ac.fhcampuswien.strategies.MovieSearchStrategy;
import at.ac.fhcampuswien.strategies.ReleaseYearSearchStrategy;
import at.ac.fhcampuswien.strategies.TitleSearchStrategy;

import java.util.List;
import java.util.Map;

public class MovieService {
    private final MovieRepositoryInterface movieRepository;

    private final Map<String, MovieSearchStrategy> searchStrategies = Map.of(
            "title", new TitleSearchStrategy(),
            "genre", new GenreSearchStrategy(),
            "releaseYear", new ReleaseYearSearchStrategy()
    );

    public MovieService(MovieRepositoryInterface movieRepository) {
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
        return movieRepository.findAll().stream()
                .filter(movie -> queryParams.entrySet().stream()
                        .allMatch(entry -> {
                            String key = entry.getKey();
                            String value = entry.getValue();

                            if (value == null || value.isBlank()) {
                                return true;
                            }

                            MovieSearchStrategy strategy = searchStrategies.get(key);

                            if (strategy == null) {
                                return true;
                            }

                            return strategy.matches(movie, value);
                        }))
                .toList();
    }

    private boolean isValidMovie(Movie movie) {
        return movie.getTitle() != null && !movie.getTitle().trim().isEmpty()
                && movie.getGenre() != null && !movie.getGenre().trim().isEmpty()
                && movie.getReleaseYear() > 1800 && movie.getReleaseYear() < 2027;
    }
}