package at.ac.fhcampuswien.strategies;

import at.ac.fhcampuswien.models.Movie;

public class ReleaseYearSearchStrategy implements MovieSearchStrategy {
    @Override
    public boolean matches(Movie movie, String value) {
        return String.valueOf(movie.getReleaseYear()).equals(value);
    }
}