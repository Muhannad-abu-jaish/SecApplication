import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.System.in;

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
    //BufferedReader inputStream ;
    //PrintWriter outputStream ;
    String connectionNumber ;
    String name ;
    final ArrayList<String> receivedMessages = new ArrayList<>() ;


    ClientHandler(Socket clientSocket)
    {
        client = clientSocket ;
        isOn=true ;
        clientNumber = "";
        clientPassword = "" ;

        try {
             inputStream = new DataInputStream(client.getInputStream()) ;
             outputStream = new DataOutputStream(client.getOutputStream()) ;
            //inputStream = new BufferedReader(new InputStreamReader(client.getInputStream())) ;
            //outputStream = new PrintWriter((client.getOutputStream())) ;

            try {

                //read the information of the clients
           /* this.clientNumber = inputStream.readUTF() ;
            this.clientPassword = inputStream.readUTF() ;
            this.connectionNumber = inputStream.readUTF() ;*/

                String register ;
                int i =0 ;
                while (inputStream!=null)
                {
                    register = inputStream.readUTF() ;
                    if (i==0)
                    {
                        System.out.println("this is first message");
                        this.clientNumber = register ;
                        System.out.println("The client number : " + this.clientNumber);
                        i++ ;
                    }

                    else if (i==1)
                    {
                        System.out.println("this is second message");
                        this.clientPassword = register ;
                        System.out.println("The client password : " + this.clientPassword);
                        i++ ;
                    }

                    if (i==2)
                        break;
                }
                System.out.println("Hi i am out of the loop");


                //read from the client
                /// makeConnectionWithAnotherClient() ;

            }catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {


        /*if (outputStream!=null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        }*/
    }


    public void sendMessage(String message)
    {

        try {
            if (isOn)
                outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
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

     public void makeConnectionWithAnotherClient(ArrayList<ClientHandler> clientHandlers , String connectNumber) throws IOException
     {

         //getting the client to connect
         int clientIndex = 0;
         for (int i = 0 ; i < clientHandlers.size() ; i++)
         {
             if (clientHandlers.get(i).getConnectionNumber().equals(connectNumber))
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

    //لاستقبال الرقم المراد التواصل معه
    public String receiveConnectionNumber()
    {
        try {

            System.out.print("this is third message : ");
            this.connectionNumber = inputStream.readUTF() ;
            System.out.println(this.connectionNumber);
            return this.connectionNumber ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void chooseTheClient(String number)
    {

    }
    public void sendOtherClientsNumbers(ArrayList<String> clientNumbers)
    {

        try {
            System.out.println("my number is : " + getClientNumber());
            System.out.println("my password is : " + clientPassword);

            for(int i = 1 ; i <= clientNumbers.size() ; i++)
            {
              //  System.out.print("the other numbers : "+clientNumbers.get(i-1)+"       ");
                if (!getClientNumber().equals(clientNumbers.get(i-1)))
                    sendMessage(i+"- "+clientNumbers.get(i-1));
            }

        }catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }

    }
}
