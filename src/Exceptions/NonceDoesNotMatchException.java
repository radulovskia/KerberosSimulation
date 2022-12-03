package Exceptions;

public class NonceDoesNotMatchException extends Exception{
    public NonceDoesNotMatchException(){
        super("NONCE-DOES-NOT-MATCH-ERROR");
    }
}
