import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    boolean isOn;
    String name ;
    Socket other;
    DataInputStream otherReadSource ;
    DataOutputStream otherWriteSource ;

    /*BufferedReader otherReadSource ;
    PrintWriter otherWriteSource ;
     */
    final ArrayList<String> otherClientNumbers = new ArrayList<>() ;
    Client(String clientNumber , String clientPassword)
    {
        isOn = true ;
        Scanner myInput = new Scanner(System.in) ;

        try {
            InetAddress ip = InetAddress.getLocalHost() ;//if the client is the same laptop

             //if the ip for other client
             // InetAddress ip = InetAddress.getByName('the ip');
             // System.out.println("The ip " +ip);
             other = new Socket(ip , 22000); //the first parameter is my ip , the second is the server (other person is port) IP
             otherReadSource = new DataInputStream(other.getInputStream())  ;//قراءة ما وصل من المتصل الآخر

            //otherReadSource = new BufferedReader(new InputStreamReader(other.getInputStream()))  ;//قراءة ما وصل من المتصل الآخر


            //كتابة أي معلومات للطرف الآخر المتصل من خلال السوكيت
            otherWriteSource = new DataOutputStream(other.getOutputStream()) ;
            //otherWriteSource = new PrintWriter(other.getOutputStream());

           /*  otherWriteSource.writeUTF(clientNumber) ;
             otherWriteSource.writeUTF(clientPassword) ;*/

            otherWriteSource.writeUTF(clientNumber);
            otherWriteSource.writeUTF(clientPassword);


             //While there is a numbers are sets
             String numbers ;
             int k = 0 ;
             while (k<5)
             {
                 numbers = otherReadSource.readUTF() ;
                                  synchronized (otherClientNumbers)
                 {
                    //تعني الانتظار حتى الانتهاء من التعامل من المصفوفة في حال كان أحد يستخدمها لأننا نتعامل مع threads
                    otherClientNumbers.add(numbers);
                 }
                 k++ ;
            }
            printClientNumbers();

            //Choose the client to connect  with him
            System.out.print("\nChoose a client number :");
            otherWriteSource.writeUTF(myInput.next());


            String serverResponse = "";
            while (true) {

                serverResponse = myInput.next() ;

                if (serverResponse.equalsIgnoreCase("exit"))
                {
                    break;
                }
                otherWriteSource.writeUTF(serverResponse);

                serverResponse = otherReadSource.readUTF();
                System.out.println("Your friend said : "+serverResponse);

            }
            otherWriteSource.close();
            otherReadSource.close();
            other.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void printClientNumbers()
    {
        for (int i = 1 ; i <= otherClientNumbers.size() ; i++ )
        {

            System.out.print(otherClientNumbers.get(i-1) + "      ");
            if (i%3==0)
                System.out.print("\n");
        }

    }
    public void handleMessages()
    {
        Thread clientThread = new Thread()
        {
            @Override
            public void run()
            {
                String serverResponse = "";
                try {

                    while (isOn)
                    {
                        serverResponse = otherReadSource.readUTF();
                        System.out.println("Other client said : " + serverResponse);
                    }

                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        };
        clientThread.start();

    }
    public void getTheHandleMessages()
    {
        handleMessages();
    }

}
