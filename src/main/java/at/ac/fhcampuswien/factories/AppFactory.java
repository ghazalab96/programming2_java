package at.ac.fhcampuswien.factories;

import at.ac.fhcampuswien.repositories.MovieRepository;
import at.ac.fhcampuswien.repositories.MovieRepositoryInterface;
import at.ac.fhcampuswien.services.MovieService;

public class AppFactory {

    public static MovieService createMovieService() {
        MovieRepositoryInterface movieRepository = new MovieRepository();
        return new MovieService(movieRepository);
    }
}