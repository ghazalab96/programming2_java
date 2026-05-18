package at.ac.fhcampuswien.strategies;

import at.ac.fhcampuswien.models.Movie;

public interface MovieSearchStrategy {
    boolean matches(Movie movie, String value);
}