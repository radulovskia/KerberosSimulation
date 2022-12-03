package Tools;

public class RQST{
    String idA;
    String idB;
    byte[] nonce;

    public RQST(String idA, String idB, byte[] nonce){
        this.idA = idA;
        this.idB = idB;
        this.nonce = nonce;
    }

    public String getIdA(){
        return idA;
    }

    public String getIdB(){
        return idB;
    }

    public byte[] getNonce(){
        return nonce;
    }
}
