package at.ac.fhcampuswien.responses;

public class ApiResponseFactory {

    public static String message(String message) {
        return "{ \"message\": \"" + message + "\" }";
    }

    public static String error(String message) {
        return "{ \"error\": \"" + message + "\" }";
    }
}