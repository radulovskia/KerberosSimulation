package Exceptions;

public class TimestampExpiredException extends Exception{
    public TimestampExpiredException(){
        super("EXPIRED-TIMESTAMP-ERROR");
    }
}
