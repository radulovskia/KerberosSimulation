package Actors;

import Exceptions.InvalidUserIdException;
import Exceptions.LifetimeExpiredException;
import Exceptions.NonceDoesNotMatchException;
import Exceptions.TimestampExpiredException;
import Tools.AESImpl;
import Tools.HelperFunctions;
import Tools.KDCResponse;
import Tools.YABMessage;
import java.time.LocalDateTime;
import java.util.Arrays;

public class User{
    private String username;
    private String id;
    private String otherId;
    private String key;
    private byte[] sessionKey;
    private byte[] nonce;

    public User(String username, String Id, String key){
        this.id = Id;
        this.username = username;
        this.key = key;
        this.nonce = HelperFunctions.generateSessionKeyOrNonce();
    }

    private AESImpl aesAndSessionKeyInit(KDCResponse kdcResponse){
        AESImpl aes = new AESImpl();
        aes.setTheKey(key.getBytes());
        this.sessionKey = aes.decrypt(kdcResponse.getCipSessionKey());
        return aes;
    }

    private LocalDateTime lifetimeVerifier(AESImpl aes, KDCResponse kdcResponse) throws LifetimeExpiredException{
        byte[] decLifetime = aes.decrypt(kdcResponse.getCipTime());
        String lifetime = new String(decLifetime);
        LocalDateTime lifetimeTime = LocalDateTime.parse(lifetime);
        LocalDateTime currentTime = LocalDateTime.now();
        if(currentTime.isAfter(lifetimeTime))
            throw new LifetimeExpiredException();
        return lifetimeTime;
    }

    public YABMessage aliceMessageVerify(KDCResponse kdcResponse) throws LifetimeExpiredException, NonceDoesNotMatchException, InvalidUserIdException{
        AESImpl aes = aesAndSessionKeyInit(kdcResponse);

        byte[] decNonce = aes.decrypt(kdcResponse.getCipNonce());
        if(decNonce == null || !Arrays.equals(decNonce, nonce))
            throw new NonceDoesNotMatchException();

        byte[] decOtherId = aes.decrypt(kdcResponse.getCipOtherId());
        String otherId = new String(decOtherId);
        if(!otherId.equals(this.otherId))
            throw new InvalidUserIdException();

        lifetimeVerifier(aes,kdcResponse);

        LocalDateTime timestamp = HelperFunctions.generateTimestamp();

        aes.setTheKey(sessionKey);
        byte[] cipSelfId = aes.encrypt(id.getBytes());
        byte[] cipTimestamp = aes.encrypt(timestamp.toString().getBytes());
        return new YABMessage(cipSelfId,cipTimestamp);
    }

    public void bobbyMessageVerify (YABMessage yabMessage, KDCResponse kdcResponse) throws LifetimeExpiredException, InvalidUserIdException, TimestampExpiredException{
        AESImpl aes = aesAndSessionKeyInit(kdcResponse);

        byte[] decOtherId = aes.decrypt(kdcResponse.getCipOtherId());
        String otherId1 = new String(decOtherId);
        LocalDateTime lifetime = lifetimeVerifier(aes,kdcResponse);

        aes.setTheKey(this.sessionKey);

        decOtherId = aes.decrypt(yabMessage.getId());
        String otherId2 = new String(decOtherId);
        if(!otherId1.equals(otherId2))
            throw new InvalidUserIdException();

        byte[] decTimestamp = aes.decrypt(yabMessage.getTimestamp());
        String ts = new String(decTimestamp);
        LocalDateTime timestamp = LocalDateTime.parse(ts);

        if(timestamp.isAfter(lifetime))
            throw new TimestampExpiredException();
    }

    public byte[] encMessage(String message){
        AESImpl aes = new AESImpl();
        aes.setTheKey(sessionKey);
        return aes.encrypt(message.getBytes());
    }

    public String decMessage(byte[] message){
        AESImpl aes = new AESImpl();
        aes.setTheKey(sessionKey);
        return new String(aes.decrypt(message));
    }

    public String getId(){
        return id;
    }

    public byte[] getNonce(){
        return nonce;
    }

    public void setOtherId(String otherId){
        this.otherId = otherId;
    }
}
