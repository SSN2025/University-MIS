package All.Exceptions;

public class InvalidLoginException extends Exception{
    @Override
    public String toString() {
        return "This is an Invalid Login Id or Password";
    }
}
