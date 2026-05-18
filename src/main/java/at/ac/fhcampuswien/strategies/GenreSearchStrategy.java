package at.ac.fhcampuswien.strategies;

import at.ac.fhcampuswien.models.Movie;

public class GenreSearchStrategy implements MovieSearchStrategy {
    @Override
    public boolean matches(Movie movie, String value) {
        return movie.getGenre() != null &&
                movie.getGenre().toLowerCase().contains(value.toLowerCase());
    }
}