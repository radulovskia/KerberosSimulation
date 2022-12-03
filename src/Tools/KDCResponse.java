package Tools;

public class KDCResponse{
    private byte[] cipSessionKey;
    private byte[] cipNonce;
    private byte[] cipTime;
    private byte[] cipOtherId;

    public KDCResponse(byte[] cipSessionKey, byte[] cipSelfNonce, byte[] cipTime, byte[] cipOtherId){
        this.cipSessionKey = cipSessionKey;
        this.cipNonce = cipSelfNonce;
        this.cipOtherId = cipOtherId;
        this.cipTime = cipTime;
    }

    public byte[] getCipSessionKey(){
        return cipSessionKey;
    }
    public byte[] getCipNonce(){
        return cipNonce;
    }
    public byte[] getCipOtherId(){
        return cipOtherId;
    }
    public byte[] getCipTime(){
        return cipTime;
    }
}
