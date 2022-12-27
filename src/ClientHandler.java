import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {

    //a thread for each client

    //the class handle the received message
    //and send the messages to the same client
    Socket client ;
    public boolean isOn ;
    DataInputStream inputStream ;
    DataOutputStream outputStream ;
    String clientNumber ;
    String clientPassword ;
    String connectionNumber ;
    String name ;
    final ArrayList<String> receivedMessages = new ArrayList<>() ;


    ClientHandler(Socket clientSocket)
    {
        client = clientSocket ;
        isOn=true ;

        try {
            inputStream = new DataInputStream(client.getInputStream()) ;
            outputStream = new DataOutputStream(client.getOutputStream()) ;
        }catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {

            //read the information of the clients
            this.clientNumber = inputStream.readUTF() ;
            this.clientPassword = inputStream.readUTF() ;

            connectionNumber = inputStream.readUTF() ;

            //read from the client
           /// makeConnectionWithAnotherClient() ;

        }catch (IOException ex)
        {
            ex.printStackTrace();
        }

        try {
            if (outputStream!=null)
         outputStream.close();

        }catch (IOException ex)
        {
            ex.printStackTrace();
        }

        try {
            if (inputStream!=null)
            inputStream.close();

        }catch (IOException ex)
        {
            ex.printStackTrace();
        }

        try {
            if (client!=null)
            client.close();

        }catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }


    public void sendMessage(String message)
    {
        try {

            if (isOn)
                outputStream.writeUTF(message);


        }catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    public ArrayList<String> getReceivedMessages()
    {
        return receivedMessages;
    }

  /*  public void setClientName(String name)
    {
        this.name = name ;
    }
    public String getClientName()
    {
        return name ;
    }*/

    public void closeAll()
    {
        isOn = false ;
        receivedMessages.clear();
        try {inputStream.close();}catch (Exception e){}
        try {outputStream.close();}catch (Exception e){}
        try {client.close();}catch (Exception e){}
    }

     public String getClientNumber()
    {
        return clientNumber ;
    }
     public String getConnectionNumber()
     {
         return  connectionNumber ;
     }

     public void makeConnectionWithAnotherClient(ArrayList<ClientHandler> clientHandlers) throws IOException {

         //getting the client to connect
         int clientIndex = 0;
         for (int i = 0 ; i < clientHandlers.size() ; i++)
         {
             if (clientHandlers.get(i).getConnectionNumber().equals(getConnectionNumber()))
                 clientIndex = i ;

         }

         ArrayList<String> messages;

         while (true) {
             //Received the messages from the current client(in this class)
             messages = getReceivedMessages();

             if (!messages.isEmpty()) {
                 synchronized (messages) {
                     for (int i = 0; i < messages.size(); i++) {
                         //send the messages to the other client
                         clientHandlers.get(clientIndex).sendMessage(messages.get(i));
                     }
                     messages.clear();
                 }
             }


             //Received the messages from the first client(clientHandler2)
             messages = clientHandlers.get(clientIndex).getReceivedMessages();
             if (!messages.isEmpty()) {
                 synchronized (messages) {
                     for (int i = 0; i < messages.size(); i++) {
                         //send the messages to the other client
                         sendMessage(messages.get(i));
                     }
                     messages.clear();
                 }
             }


             /*
             while (isOn)
        {
            String message ;
            message = inputStream.readUTF() ;
            synchronized (receivedMessages)
            {
                //تعني الانتظار حتى الانتهاء من التعامل من المصفوفة في حال كان أحد يستخدمها لأننا نتعامل مع threads
                receivedMessages.add(message) ;
            }
        }//

              */
    }
}
    }
