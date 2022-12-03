import Actors.KDCServer;
import Actors.User;
import Tools.KDCResponse;
import Tools.RQST;
import Tools.YABMessage;
import java.util.ArrayList;

public class SimpleKerberosTest{
    public static void main(String[] args){
        String keyA = "ThisIsAliceKey123";
        String keyB = "ThisIsBobbyKey321";//16B each key
        User Alice = new User("Alice","AliceID",keyA);
        User Bobby = new User("Bobby","BobbyID",keyB);
        Alice.setOtherId(Bobby.getId());
        Bobby.setOtherId(Alice.getId());
        KDCServer kdcServer = new KDCServer(keyA,keyB);

        RQST request = new RQST(Alice.getId(), Bobby.getId(), Alice.getNonce());
        ArrayList<KDCResponse> responses = kdcServer.ResponseGenerator(request);
        KDCResponse yA = responses.get(0);
        KDCResponse yB = responses.get(1);

        try{
            YABMessage yAB = Alice.aliceMessageVerify(yA);
            Bobby.bobbyMessageVerify(yAB, yB);
            System.out.println("START OF SIMULATION");
            String messageAlice = "ALICE: hello bobby";
            byte[] messageAliceBytes = Alice.encMessage(messageAlice);
            String s = Bobby.decMessage(messageAliceBytes);
            System.out.println(s);

            String messageBobby = "BOBBY: hello alice";
            byte[] messageBobbyBytes = Bobby.encMessage(messageBobby);
            System.out.println(Alice.decMessage(messageBobbyBytes));
            System.out.println("END OF SIMULATION");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
