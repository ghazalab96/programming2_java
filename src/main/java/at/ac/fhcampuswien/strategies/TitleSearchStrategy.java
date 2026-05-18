package at.ac.fhcampuswien.strategies;

import at.ac.fhcampuswien.models.Movie;

public class TitleSearchStrategy implements MovieSearchStrategy {
    @Override
    public boolean matches(Movie movie, String value) {
        return movie.getTitle() != null &&
                movie.getTitle().toLowerCase().contains(value.toLowerCase());
    }
}