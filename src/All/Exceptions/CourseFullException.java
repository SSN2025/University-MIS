package All.Exceptions;

public class CourseFullException extends Exception {
    @Override
    public String toString() {
        return "No Seat Left in this course";
    }
}
