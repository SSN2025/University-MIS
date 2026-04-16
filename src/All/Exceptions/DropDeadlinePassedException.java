package All.Exceptions;

public class DropDeadlinePassedException extends Exception{
    @Override
    public String toString() {
        return ("The deadline for dropping the course has passed");
    }
}
