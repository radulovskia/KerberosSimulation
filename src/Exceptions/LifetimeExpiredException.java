package Exceptions;

public class LifetimeExpiredException extends Exception{
    public LifetimeExpiredException(){
        super("EXPIRED-LIFETIME-ERROR");
    }
}
