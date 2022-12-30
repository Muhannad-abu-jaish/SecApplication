import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientMessagesHandler extends Thread{

    Socket client ;
    DataInputStream inputStream ;
    DataOutputStream outputStream ;
    String clientNumber ;
    String clientPassword ;

    String connectionNumber ;

    @Override
    public void run() {

    }
}
