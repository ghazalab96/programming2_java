 ````markdown
# Movie REST API

A Java-based Movie REST API built step by step as part of the Programming 2 exercises.

The project started as a simple REST API for managing movies and gradually evolved into a more structured backend application with a controller layer, service layer, repository layer, H2 database persistence, exception handling, unit testing, mocking, and design pattern improvements.

## Project Overview

This application manages movies through a simple REST API. It supports creating, reading, updating, deleting, and searching movies.

The current architecture follows a layered structure:

```text
API Request
   ↓
Controller Layer
   ↓
Service Layer
   ↓
Repository Layer
   ↓
H2 Database
````

The goal of the project was not only to make the API work, but also to improve the design step by step by applying clean architecture ideas, separation of concerns, exception propagation, testing strategies, SOLID principles, and design patterns.

## Features

* REST API for movie management
* MVC-inspired structure
* Controller layer for HTTP request handling
* Service layer for business logic
* Repository layer for database access
* H2 database integration
* Automatic database table initialization
* JSON parsing with Gson
* CRUD operations for movies
* Search endpoint with query parameters
* Prepared SQL statements
* Custom exception handling
* HTTP response mapping for errors
* Unit testing with JUnit
* Mocking with Mockito
* SOLID-based refactoring
* Design pattern improvements

## Tech Stack

* Java
* Maven
* H2 Database
* Gson
* JUnit
* Mockito
* Java HTTP Server
* IntelliJ IDEA

## API Endpoints

### Get All Movies

```http
GET /api/movies/getAll
```

Returns all movies stored in the database.

Example request:

```bash
curl -X GET http://localhost:8080/api/movies/getAll
```

Example response:

```json
[
  {
    "id": "200ce2c9-765d-4486-bdb1-bf68963d8142",
    "title": "Inception",
    "genre": "Sci-Fi",
    "releaseYear": 2010
  },
  {
    "id": "de122389-b7f5-4c61-8588-5b651fb31382",
    "title": "The Silent Orbit",
    "genre": "Sci-Fi",
    "releaseYear": 2021
  }
]
```

---

### Add Movie

```http
POST /api/movies/add
```

Adds a new movie to the database.

Example request:

```bash
curl -X POST http://localhost:8080/api/movies/add \
  -H "Content-Type: application/json" \
  -d '{"title":"Inception","genre":"Sci-Fi","releaseYear":2010}'
```

Example response:

```json
{
  "message": "Movie added successfully"
}
```

---

### Delete Movie

```http
DELETE /api/movies/delete
```

Deletes a movie based on title, genre, and release year.

Example request:

```bash
curl -X DELETE http://localhost:8080/api/movies/delete \
  -H "Content-Type: application/json" \
  -d '{"title":"Inception","genre":"Sci-Fi","releaseYear":2010}'
```

Example response:

```json
{
  "message": "Movie deleted successfully"
}
```

If the movie does not exist:

```json
{
  "error": "Movie not found for deletion"
}
```

---

### Update Movie

```http
PUT /api/movies/update
```

Updates a movie using its ID.

Example request:

```bash
curl -X PUT http://localhost:8080/api/movies/update \
  -H "Content-Type: application/json" \
  -d '{
    "id":"200ce2c9-765d-4486-bdb1-bf68963d8142",
    "title":"Inception Updated",
    "genre":"Sci-Fi",
    "releaseYear":2010
  }'
```

Example response:

```json
{
  "message": "Movie updated successfully"
}
```

---

### Search Movies

```http
GET /api/movies/search
```

Searches movies using query parameters.

Supported query parameters:

* `title`
* `genre`
* `releaseYear`

The search supports partial string matches and ignores upper/lower case.

Example requests:

```bash
curl "http://localhost:8080/api/movies/search?title=incep"
```

```bash
curl "http://localhost:8080/api/movies/search?genre=Sci-Fi"
```

```bash
curl "http://localhost:8080/api/movies/search?title=Inception&releaseYear=2010&genre=Sci-Fi"
```

Example response:

```json
[
  {
    "id": "200ce2c9-765d-4486-bdb1-bf68963d8142",
    "title": "Inception",
    "genre": "Sci-Fi",
    "releaseYear": 2010
  }
]
```

## Database

The application uses an H2 database.

The database table is initialized automatically when the application starts.

```sql
CREATE TABLE IF NOT EXISTS movies (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    release_year INT NOT NULL
);
```

The repository layer uses prepared statements for all database operations.

Example:

```java
String sql = "INSERT INTO movies (id, title, genre, release_year) VALUES (?, ?, ?, ?)";
PreparedStatement statement = connection.prepareStatement(sql);

statement.setObject(1, movie.getId());
statement.setString(2, movie.getTitle());
statement.setString(3, movie.getGenre());
statement.setInt(4, movie.getReleaseYear());
```

Prepared statements are used because they:

* Separate SQL logic from user input
* Help prevent SQL injection
* Handle special characters correctly
* Make database code cleaner and safer

## Error Handling

The application uses custom exceptions to keep error handling clear and meaningful.

Custom exceptions:

* `MovieNotFoundException`
* `DatabaseException`

The controller catches exceptions and converts them into proper HTTP responses.

| Exception                | HTTP Response               |
| ------------------------ | --------------------------- |
| `MovieNotFoundException` | `404 Not Found`             |
| `DatabaseException`      | `500 Internal Server Error` |
| `JsonSyntaxException`    | `400 Bad Request`           |
| Unknown Exception        | `500 Internal Server Error` |

Example error response:

```json
{
  "error": "Movie not found for deletion"
}
```

## Testing

The project includes unit tests for the service layer.

Testing tools:

* JUnit
* Mockito

After adding the repository layer, the service tests use a mocked `MovieRepository` instead of connecting to the real database.

This keeps the tests focused on service behavior and avoids depending on the database during unit tests.

Example:

```java
when(movieRepository.delete(movieToDelete))
        .thenThrow(new DatabaseException("Database connection error"));

assertThrows(DatabaseException.class, () -> {
    movieService.deleteMovie(movieToDelete);
});
```

## SOLID Principles

The project was also analyzed and improved using the SOLID principles.

### Single Responsibility Principle

Each class has one main responsibility.

* `MovieController` handles HTTP requests and responses.
* `MovieService` contains business logic.
* `MovieRepository` handles database operations.
* `DatabaseUtil` manages database connection and initialization.
* Custom exception classes represent specific error cases.

This makes the code easier to understand, test, and maintain.

### Open/Closed Principle

The application can be extended without heavily modifying existing code.

For example, the service and repository structure makes it easier to add new operations later, such as filtering by director, rating, or runtime.

New behavior can be added in the service or repository without putting all logic inside the controller.

### Liskov Substitution Principle

The application can be improved by depending on abstractions instead of concrete classes.

For example, a `MovieRepositoryInterface` could be introduced:

```java
public interface MovieRepositoryInterface {
    void add(Movie movie) throws DatabaseException;
    List<Movie> findAll() throws DatabaseException;
    boolean delete(Movie movie) throws DatabaseException, MovieNotFoundException;
    boolean update(Movie movie) throws DatabaseException, MovieNotFoundException;
}
```

Then `MovieRepository` can implement this interface.

This would make it possible to replace the database repository with another implementation, such as an in-memory repository for testing.

### Interface Segregation Principle

The repository and service interfaces should stay focused.

Instead of creating one large interface with many unrelated methods, the application should keep interfaces small and meaningful.

For this project, the movie-related methods belong together because they all work with the `Movie` model.

### Dependency Inversion Principle

The service layer should not be tightly coupled to a concrete repository implementation.

The improved design is to inject the repository dependency into the service constructor.

Example:

```java
public MovieService(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
}
```

This makes the service easier to test because a mock repository can be injected during unit tests.

## Design Patterns

The project can be explained using several design patterns.

### Creational Pattern: Factory Pattern

A factory can be used to create service or repository objects in one central place.

For example, instead of creating dependencies directly in the controller, an application factory could create and wire them together.

Example idea:

```java
public class AppFactory {
    public static MovieService createMovieService() {
        MovieRepository repository = new MovieRepository();
        return new MovieService(repository);
    }
}
```

This helps centralize object creation and keeps the controller cleaner.

### Structural Pattern: Repository Pattern

The repository pattern is used to separate database access from business logic.

`MovieRepository` hides the SQL details from the rest of the application.

The service does not need to know how SQL works. It only calls methods such as:

```java
movieRepository.findAll();
movieRepository.add(movie);
movieRepository.delete(movie);
movieRepository.update(movie);
```

This improves maintainability because database logic is isolated in one layer.

### Behavioral Pattern: Strategy Pattern

The search behavior can be improved with the Strategy Pattern.

For example, different search strategies could be created for:

* Search by title
* Search by genre
* Search by release year
* Combined search

Instead of putting all search conditions in one method, each strategy could handle one type of filtering.

Example idea:

```java
public interface MovieSearchStrategy {
    boolean matches(Movie movie, String value);
}
```

This would make search logic easier to extend and test.

## Running the Project

Clone the repository:

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPOSITORY_NAME.git
cd YOUR_REPOSITORY_NAME
```

Build the project:

```bash
mvn clean install
```

Run the application from the `Main` class in IntelliJ.

The server runs on:

```text
http://localhost:8080
```

Test the API:

```bash
curl -X GET http://localhost:8080/api/movies/getAll
```

## Running Tests

Run all tests with Maven:

```bash
mvn test
```

Or run the test class directly in IntelliJ.

## Notes About H2 and IntelliJ

If you use H2 as a file-based database, avoid opening the database in IntelliJ's Database window while the Java server is running.

The database file can become locked.

Recommended workflow:

```text
Testing API:
1. Disconnect IntelliJ Database
2. Run Java server
3. Use browser, Postman, or curl

Inspecting database:
1. Stop Java server
2. Open IntelliJ Database window
3. Refresh the H2 database
4. Check the MOVIES table
```

## Project Structure

Example structure:

```text
src
└── main
    └── java
        └── at.ac.fhcampuswien
            ├── Main.java
            ├── ApiUtils.java
            ├── controllers
            │   └── MovieController.java
            ├── database
            │   └── DatabaseUtil.java
            ├── exceptions
            │   ├── DatabaseException.java
            │   └── MovieNotFoundException.java
            ├── models
            │   └── Movie.java
            ├── repositories
            │   └── MovieRepository.java
            └── services
                └── MovieService.java
```

## What I Learned

Through this project, I practiced:

* Building REST endpoints in Java
* Structuring a backend application
* Working with controller, service, and repository layers
* Using Gson for JSON parsing
* Connecting Java to an H2 database
* Writing SQL queries with prepared statements
* Handling checked exceptions
* Mapping application errors to HTTP responses
* Writing unit tests with JUnit
* Mocking dependencies with Mockito
* Applying SOLID principles
* Thinking about design patterns and software maintainability

## Author

Ghazal Arbabzadeh

```
```
