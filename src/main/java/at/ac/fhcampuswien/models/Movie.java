package at.ac.fhcampuswien.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Movie {
    private UUID id;
    private String title;
    private String genre;
    private int releaseYear;

    public Movie() {
        this.id = UUID.randomUUID();
    }

    public Movie(String title, String genre, int releaseYear) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    @Override
    public String toString() {
        return "Movie{id=" + id + ", title='" + title + "', genre='" + genre + "', releaseYear=" + releaseYear + "}";
    }

    public static List<Movie> generateDummyMovies() {
        List<Movie> movies = new ArrayList<>();
        Random random = new Random();

        String[] titles = {
                "Inception", "Interstellar", "The Matrix", "Titanic", "Avatar",
                "Joker", "Gladiator", "Whiplash", "Dune", "Memento",
                "Alien", "The Prestige", "Fight Club", "Oppenheimer", "La La Land",
                "Pulp Fiction", "The Godfather", "Forrest Gump", "Shutter Island", "The Dark Knight"
        };

        String[] genres = {
                "Action", "Drama", "Comedy", "Sci-Fi", "Thriller", "Fantasy", "Adventure"
        };

        for (int i = 0; i < 20; i++) {
            String title = titles[random.nextInt(titles.length)] + " " + (i + 1);
            String genre = genres[random.nextInt(genres.length)];
            int releaseYear = 1980 + random.nextInt(46);
            movies.add(new Movie(title, genre, releaseYear));
        }

        return movies;
    }
}