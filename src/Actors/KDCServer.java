package Actors;

import Tools.AESImpl;
import Tools.HelperFunctions;
import Tools.KDCResponse;
import Tools.RQST;
import java.util.ArrayList;

public class KDCServer{
    private String keyA;
    private String keyB;

    public KDCServer(String keyA, String keyB){
        this.keyA = keyA;
        this.keyB = keyB;
    }


    public ArrayList<KDCResponse> ResponseGenerator(RQST request){
        byte[] sessionKey = HelperFunctions.generateSessionKeyOrNonce();
        String lifetime = HelperFunctions.generateLifetime();
        AESImpl aes = new AESImpl();

        //This is for Allice, yA
        aes.setTheKey(keyA.getBytes());
        byte[] cipSessionKey = aes.encrypt(sessionKey);
        byte[] cipNonce = aes.encrypt(request.getNonce());
        byte[] cipTime = aes.encrypt(lifetime.getBytes());
        byte[] cipOtherId = aes.encrypt(request.getIdB().getBytes());
        KDCResponse kdcResponseAlice = new KDCResponse(cipSessionKey,cipNonce,cipTime,cipOtherId);

        //This is for Bobby, yB
        aes.setTheKey(keyB.getBytes());
        cipSessionKey = aes.encrypt(sessionKey);
        cipOtherId = aes.encrypt(request.getIdA().getBytes());
        cipTime = aes.encrypt(lifetime.getBytes());
        KDCResponse kdcResponseBobby = new KDCResponse(cipSessionKey, null, cipTime, cipOtherId);

        ArrayList<KDCResponse> responses = new ArrayList<>();
        responses.add(kdcResponseAlice);
        responses.add(kdcResponseBobby);
        return responses;
    }

}
