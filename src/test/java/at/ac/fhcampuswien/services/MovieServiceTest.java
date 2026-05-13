package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    private MovieService movieService;
    private MovieRepository movieRepository;
    private List<Movie> movies;
    private Movie inception;
    private Movie titanic;

    @BeforeEach
    void setUp() throws DatabaseException {
        movieRepository = mock(MovieRepository.class);

        movies = new ArrayList<>();

        inception = new Movie("Inception", "Sci-Fi", 2010);
        titanic = new Movie("Titanic", "Drama", 1997);

        movies.add(inception);
        movies.add(titanic);

        when(movieRepository.findAll()).thenReturn(movies);

        movieService = new MovieService(movieRepository);
    }

    @Test
    void givenValidMovie_whenAddMovie_thenMovieIsAdded() throws DatabaseException {
        Movie newMovie = new Movie("Interstellar", "Sci-Fi", 2014);

        boolean result = movieService.addMovie(newMovie);

        assertTrue(result);
        verify(movieRepository).add(newMovie);
    }

    @Test
    void givenInvalidMovie_whenAddMovie_thenReturnFalse() throws DatabaseException {
        Movie invalidMovie = new Movie("", "Sci-Fi", 2014);

        boolean result = movieService.addMovie(invalidMovie);

        assertFalse(result);
        verify(movieRepository, never()).add(invalidMovie);
    }

    @Test
    void givenExistingMovie_whenDeleteMovie_thenRepositoryDeleteIsCalled()
            throws DatabaseException, MovieNotFoundException {
        when(movieRepository.delete(inception)).thenReturn(true);

        boolean result = movieService.deleteMovie(inception);

        assertTrue(result);
        verify(movieRepository).delete(inception);
    }

    @Test
    void givenInvalidMovie_whenDeleteMovie_thenReturnFalse()
            throws DatabaseException, MovieNotFoundException {
        Movie invalidMovie = new Movie("", "Sci-Fi", 2010);

        boolean result = movieService.deleteMovie(invalidMovie);

        assertFalse(result);
        verify(movieRepository, never()).delete(invalidMovie);
    }

    @Test
    void givenExistingMovieId_whenUpdateMovie_thenRepositoryUpdateIsCalled()
            throws DatabaseException, MovieNotFoundException {
        Movie updatedMovie = new Movie("Inception Updated", "Thriller", 2011);
        updatedMovie.setId(inception.getId());

        when(movieRepository.update(updatedMovie)).thenReturn(true);

        boolean result = movieService.updateMovie(updatedMovie);

        assertTrue(result);
        verify(movieRepository).update(updatedMovie);
    }

    @Test
    void givenMovieWithoutId_whenUpdateMovie_thenReturnFalse()
            throws DatabaseException, MovieNotFoundException {
        Movie updatedMovie = new Movie("Unknown", "Drama", 2020);
        updatedMovie.setId(null);

        boolean result = movieService.updateMovie(updatedMovie);

        assertFalse(result);
        verify(movieRepository, never()).update(updatedMovie);
    }

    @Test
    void givenPartialTitle_whenSearchMovies_thenMatchingMoviesAreReturned() throws DatabaseException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("title", "cep");

        List<Movie> result = movieService.searchMovies(queryParams);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenLowerCaseGenre_whenSearchMovies_thenSearchIsCaseInsensitive() throws DatabaseException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("genre", "sci-fi");

        List<Movie> result = movieService.searchMovies(queryParams);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void givenReleaseYear_whenSearchMovies_thenMatchingMovieIsReturned() throws DatabaseException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("releaseYear", "1997");

        List<Movie> result = movieService.searchMovies(queryParams);

        assertEquals(1, result.size());
        assertEquals("Titanic", result.get(0).getTitle());
    }

    @Test
    void givenMultipleSearchParams_whenSearchMovies_thenCorrectMovieIsReturned() throws DatabaseException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("title", "incep");
        queryParams.put("genre", "sci");
        queryParams.put("releaseYear", "2010");

        List<Movie> result = movieService.searchMovies(queryParams);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void shouldThrowDatabaseException_whenDeletingMovieWithDatabaseError()
            throws DatabaseException, MovieNotFoundException {
        Movie movieToDelete = new Movie("Inception", "Sci-Fi", 2010);

        when(movieRepository.delete(movieToDelete))
                .thenThrow(new DatabaseException("Database connection error"));

        assertThrows(DatabaseException.class, () -> movieService.deleteMovie(movieToDelete));
    }

    @Test
    void shouldThrowMovieNotFoundException_whenDeletingNonExistingMovie()
            throws DatabaseException, MovieNotFoundException {
        Movie movieToDelete = new Movie("Avatar", "Sci-Fi", 2009);

        when(movieRepository.delete(movieToDelete))
                .thenThrow(new MovieNotFoundException("Movie not found for deletion"));

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(movieToDelete));
    }
}