package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MovieServiceTest {

    private MovieService movieService;
    private List<Movie> movies;
    private Movie inception;
    private Movie titanic;

    @BeforeEach
    void setUp() {
        movies = new ArrayList<>();

        inception = new Movie("Inception", "Sci-Fi", 2010);
        titanic = new Movie("Titanic", "Drama", 1997);

        movies.add(inception);
        movies.add(titanic);

        movieService = new MovieService(movies);
    }

    @Test
    void givenValidMovie_whenAddMovie_thenMovieIsAdded() {
        Movie newMovie = new Movie("Interstellar", "Sci-Fi", 2014);

        boolean result = movieService.addMovie(newMovie);

        assertTrue(result);
        assertEquals(3, movies.size());
    }

    @Test
    void givenDuplicateMovie_whenAddMovie_thenReturnFalse() {
        Movie duplicateMovie = new Movie("Inception", "Sci-Fi", 2010);

        boolean result = movieService.addMovie(duplicateMovie);

        assertFalse(result);
        assertEquals(2, movies.size());
    }

    @Test
    void givenExistingMovie_whenDeleteMovie_thenMovieIsRemoved() {
        Movie movieToDelete = new Movie("Inception", "Sci-Fi", 2010);

        boolean result = movieService.deleteMovie(movieToDelete);

        assertTrue(result);
        assertEquals(1, movies.size());
    }

    @Test
    void givenNonExistingMovie_whenDeleteMovie_thenReturnFalse() {
        Movie movieToDelete = new Movie("Avatar", "Sci-Fi", 2009);

        boolean result = movieService.deleteMovie(movieToDelete);

        assertFalse(result);
        assertEquals(2, movies.size());
    }

    @Test
    void givenExistingMovieId_whenUpdateMovie_thenMovieIsUpdated() {
        Movie updatedMovie = new Movie("Inception Updated", "Thriller", 2011);
        updatedMovie.setId(inception.getId());

        boolean result = movieService.updateMovie(updatedMovie);

        assertTrue(result);
        assertEquals("Inception Updated", inception.getTitle());
        assertEquals("Thriller", inception.getGenre());
        assertEquals(2011, inception.getReleaseYear());
    }

    @Test
    void givenUnknownMovieId_whenUpdateMovie_thenReturnFalse() {
        Movie updatedMovie = new Movie("Unknown", "Drama", 2020);
        updatedMovie.setId(UUID.randomUUID());

        boolean result = movieService.updateMovie(updatedMovie);

        assertFalse(result);
    }

    @Test
    void givenPartialTitle_whenSearchMovies_thenMatchingMoviesAreReturned() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("title", "cep");

        List<Movie> result = movieService.searchMovies(queryParams);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenLowerCaseGenre_whenSearchMovies_thenSearchIsCaseInsensitive() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("genre", "sci-fi");

        List<Movie> result = movieService.searchMovies(queryParams);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenReleaseYear_whenSearchMovies_thenMatchingMovieIsReturned() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("releaseYear", "1997");

        List<Movie> result = movieService.searchMovies(queryParams);

        assertEquals(1, result.size());
        assertEquals("Titanic", result.get(0).getTitle());
    }

    @Test
    void givenMultipleSearchParams_whenSearchMovies_thenCorrectMovieIsReturned() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("title", "incep");
        queryParams.put("genre", "sci");
        queryParams.put("releaseYear", "2010");

        List<Movie> result = movieService.searchMovies(queryParams);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }
}