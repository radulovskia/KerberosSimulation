import Actors.KDCServer;
import Actors.User;
import Tools.KDCResponse;
import Tools.KDCRequest;
import Tools.YABMessage;
import java.util.ArrayList;
import java.util.HexFormat;

public class SimpleKerberosTest{
    public static void main(String[] args){
        String keyA = "Alice123";
        String keyB = "Bobby321";
        User Alice = new User("Alice","AliceID",keyA);
        User Bobby = new User("Bobby","BobbyID",keyB);
        Alice.setOtherId(Bobby.getId());
        KDCServer kdcServer = new KDCServer(keyA,keyB);

        KDCRequest request = new KDCRequest(Alice.getId(), Bobby.getId(), Alice.getNonce());
        ArrayList<KDCResponse> responses = kdcServer.ResponseGenerator(request);
        KDCResponse yA = responses.get(0);
        KDCResponse yB = responses.get(1);

        try{
            YABMessage yAB = Alice.aliceMessageVerify(yA);
            Bobby.bobbyMessageVerify(yAB, yB);
            System.out.println("START OF SIMULATION");
            String messageAlice = "Hello Bobby, it's Alice";
            byte[] messageAliceBytes = Alice.encMessage(messageAlice);
            System.out.println(HexFormat.of().formatHex(messageAliceBytes));
            String s = Bobby.decMessage(messageAliceBytes);
            System.out.println(s);

            String messageBobby = "Hi Alice, how are you";
            byte[] messageBobbyBytes = Bobby.encMessage(messageBobby);
            System.out.println(HexFormat.of().formatHex(messageBobbyBytes));
            System.out.println(Alice.decMessage(messageBobbyBytes));
            System.out.println("END OF SIMULATION");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
