package Actors;

import Exceptions.InvalidUserIdException;
import Exceptions.LifetimeExpiredException;
import Exceptions.NonceDoesNotMatchException;
import Exceptions.TimestampExpiredException;
import Tools.AESImpl;
import Tools.HelperFunctions;
import Tools.KDCResponse;
import Tools.YABMessage;
import java.sql.Time;
import java.time.LocalTime;
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

    private Time lifetimeVerifier(AESImpl aes, KDCResponse kdcResponse) throws LifetimeExpiredException{
        byte[] decLifetime = aes.decrypt(kdcResponse.getCipTime());
        String lifetime = new String(decLifetime);
        Time lifetimeTime = Time.valueOf(lifetime);
        Time currentTime = Time.valueOf(LocalTime.now());
        if(currentTime.after(lifetimeTime))
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

        Time timestamp = HelperFunctions.generateTimestamp();

        aes.setTheKey(sessionKey);
        byte[] cipSelfId = aes.encrypt(id.getBytes());
        byte[] cipTimestamp = aes.encrypt(timestamp.toString().getBytes());
        return new YABMessage(cipSelfId,cipTimestamp);
    }

    public void bobbyMessageVerify (YABMessage yabMessage, KDCResponse kdcResponse) throws LifetimeExpiredException, InvalidUserIdException, TimestampExpiredException{
        AESImpl aes = aesAndSessionKeyInit(kdcResponse);

        byte[] decOtherId = aes.decrypt(kdcResponse.getCipOtherId());
        String otherId1 = new String(decOtherId);
        Time lifetime = lifetimeVerifier(aes,kdcResponse);

        aes.setTheKey(this.sessionKey);

        decOtherId = aes.decrypt(yabMessage.getId());
        String otherId2 = new String(decOtherId);
        if(!otherId1.equals(otherId2))
            throw new InvalidUserIdException();

        byte[] decTimestamp = aes.decrypt(yabMessage.getTimestamp());
        String ts = new String(decTimestamp);
        Time timestamp = Time.valueOf(ts);

        if(timestamp.after(lifetime))
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

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getId(){
        return id;
    }

    public void setSelfId(String selfId){
        this.id = selfId;
    }

    public String getKey(){
        return key;
    }

    public void setKey(String key){
        this.key = key;
    }

    public byte[] getSessionKey(){
        return sessionKey;
    }

    public void setSessionKey(byte[] sessionKey){
        this.sessionKey = sessionKey;
    }

    public byte[] getNonce(){
        return nonce;
    }

    public void setNonce(byte[] nonce){
        this.nonce = nonce;
    }

    public void setOtherId(String otherId){
        this.otherId = otherId;
    }
}
