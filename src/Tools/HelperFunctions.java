package Tools;

import java.security.SecureRandom;
import java.sql.Time;
import java.time.LocalDateTime;

public class HelperFunctions{
    public static byte[] generateSessionKeyOrNonce(){
        SecureRandom sr = new SecureRandom();
        byte[] sessionKey = new byte[16];
        sr.nextBytes(sessionKey);
        return sessionKey;
    }

    public static String generateLifetime(){
        return LocalDateTime.now().plusHours(1).toString();
    }

    public static LocalDateTime generateTimestamp(){
        return LocalDateTime.now();
    }
}
