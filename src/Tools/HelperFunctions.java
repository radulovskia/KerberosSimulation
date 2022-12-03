package Tools;

import java.security.SecureRandom;
import java.sql.Time;

public class HelperFunctions{
    public static byte[] generateSessionKeyOrNonce(){
        SecureRandom sr = new SecureRandom();
        byte[] sessionKey = new byte[16];
        sr.nextBytes(sessionKey);
        return sessionKey;
    }

    public static String generateLifetime(){
        return new Time(System.currentTimeMillis()+1000*60*60).toString();//1 hour
    }

    public static Time generateTimestamp(){
        return new Time(System.currentTimeMillis());
    }
}
