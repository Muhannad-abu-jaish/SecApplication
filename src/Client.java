import com.mysql.cj.util.Base64Decoder;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

public class Client {

    boolean isOn;
    String clientCount ;
    Socket other;
    static Cipher cipher;
    static Mac mac;
    static SecretKey KEY;
    static String IV;
    DataInputStream otherReadSource ;
    DataOutputStream otherWriteSource ;

    /*BufferedReader otherReadSource ;
    PrintWriter otherWriteSource ;
     */
    final ArrayList<String> otherClientNumbers = new ArrayList<>() ;
    Client(String clientNumber , String clientPassword) throws Exception {
        mac = Mac.getInstance("HmacSHA256");
        isOn = true ;
        Scanner myInput = new Scanner(System.in) ;
        clientCount = "";
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        try {
            InetAddress ip = InetAddress.getLocalHost() ;//if the client is the same laptop

             //if the ip for other client
             // InetAddress ip = InetAddress.getByName('the ip');
             // System.out.println("The ip " +ip);
             other = new Socket(ip , 22000); //the first parameter is my ip , the second is the server (other person is port) IP
             otherReadSource = new DataInputStream(other.getInputStream())  ;//قراءة ما وصل من المتصل الآخر


            //كتابة أي معلومات للطرف الآخر المتصل من خلال السوكيت
            otherWriteSource = new DataOutputStream(other.getOutputStream()) ;

            otherWriteSource.writeUTF(clientNumber);
            otherWriteSource.writeUTF(clientPassword);
            clientCount = otherReadSource.readUTF();

            //get the client numbers from the server
            //and printed it in the client console
            showClientsNumbers() ;

            //KEY = javaDB.getClientKey(clientNumber);
            KEY = Crypto.createAESKey(clientNumber);
            IV =getIVSecureRandom();
            //Choose the client to connect  with him
            System.out.print("\nChoose a client number :");
            otherWriteSource.writeUTF(myInput.next());
            otherWriteSource.writeUTF(IV);


            Thread getFromOther = new Thread()
            {
                @Override
                public void run() {
                    try {
                        String serverResponse = "";
                        int i = 0 ;
                        while (isOn) {
                            if (i%2!=0){
                                String macOld = otherReadSource.readUTF();
                                Mac mac = Mac.getInstance("HmacSHA256");
                                mac.init(KEY);
                                byte[] macResult = mac.doFinal(serverResponse.getBytes());
                                String macNew = new String(macResult);
                                if(macOld.equals(macNew)) {
                                    System.out.println("Your friend said : "+ Crypto.decrypt(serverResponse,KEY,IV));
                                }
                                else{
                                    throw new Exception("ERROR IN MAC IN CLIENT");
                                }
                                }
                            else{
                                serverResponse = otherReadSource.readUTF();
                            }
                            i++;
                        }
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                }
            };
            getFromOther.start();

            String serverResponse = "";
            while (true)
            {
                serverResponse = myInput.next() ;

                if (serverResponse.equalsIgnoreCase("exit"))
                {
                    break;
                }
                mac.init(KEY);
                byte[] macResult = mac.doFinal(Crypto.encrypt(serverResponse , KEY , IV).getBytes());
                String MACFINAL = new String(macResult);
                otherWriteSource.writeUTF(Crypto.encrypt(serverResponse , KEY , IV));
                otherWriteSource.writeUTF(MACFINAL);
            }
            isOn = false ;
            otherWriteSource.close();
            otherReadSource.close();
            other.close();
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public static String getIVSecureRandom() throws NoSuchAlgorithmException, NoSuchPaddingException {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {

            int index = (int) (AlphaNumericString.length() * Math.random());

            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
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

    public void showClientsNumbers()
    {
        try {

            //While there is a numbers are sets
            String numbers ;
            int k = 0 ;
            int p = Integer.parseInt(clientCount) ;
            System.out.println("numbers of clients : " + p);
            while (k <3)
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
        }catch (IOException ex)
        {
            ex.printStackTrace();
        }

    }


}
