import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    //room all clients enter
    //everytime a client enters, broadcast client name
    //client send name first (verify unused name)
    //each client has his name and it is added during broadcasting
    //creating client commands
    //creating server commands

    ArrayList<String> clientNames = new ArrayList<>();
    static ArrayList<String> clientNumbers = new ArrayList<>();

    final ArrayList<ClientHandler> clientHandlerArrayList = new ArrayList<>();
    boolean serverIsOn;

    Server() {
        serverIsOn = true;
        try {
            //Data type تقوم بعمل Socket من نوع استماع
            //حيث تستسمع للاتصالات وتجهز بيانات المتصل وتعيد socket فيهاالبيانات

            ServerSocket serverSocket = new ServerSocket(22000);

            /*
            كود سحب الأرقام من قاعدة البيانات
            وتخزينها في clientNumber
             */

            //for accepting the connection
            //اي اتصالات قادمة يتم قبولها هنا
            Thread handlingIncomingConnections = new Thread() {
                @Override
                public void run() {
                    try {

                        while (serverIsOn) {
                            System.out.println("server is on");
                            Socket clientSocket = serverSocket.accept();
                            System.out.println("Client Accepted");
                            ClientHandler clientHandler0 = new ClientHandler(clientSocket);

                            /*
                            The code for check the information in the database

                             */
                            clientHandler0.start();
                            clientNumbers.add(clientHandler0.getClientNumber()) ;
                            sendOtherClientsNumbers(clientHandler0);
                            synchronized (clientHandlerArrayList) {
                                clientHandlerArrayList.add(clientHandler0);
                            }
                            clientHandler0.makeConnectionWithAnotherClient(clientHandlerArrayList);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            };
            handlingIncomingConnections.start();



            //Handling messages
            /*
            Thread messageHandler = new Thread() {
                @Override
                public void run() {
                    while (serverIsOn) {
                        checkOnClients();
                    }//end of while
                }
            };

             */
          //  messageHandler.start();

            //this code for make connection between 2 clients
            /*
            ServerSocket serverSocket = new ServerSocket(22000);
            Socket clientSocket = serverSocket.accept(); //مازال ينتظر تواصل أحد الكلاينت مع هذا البورت


            System.out.println("the first client is ready ");
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clientHandler.start();


            clientSocket = serverSocket.accept();
            System.out.println("the second client is ready ");
            ClientHandler clientHandler2 = new ClientHandler(clientSocket);
            clientHandler2.start();

            ArrayList<String> messages;

            while (true) {
                //Received the messages from the first client(clientHandler)
                messages = clientHandler.getReceivedMessages();

                if (!messages.isEmpty()) {
                    synchronized (messages) {
                        for (int i = 0; i < messages.size(); i++) {
                            //send the messages to the other client
                            clientHandler2.sendMessage(messages.get(i));
                        }
                        messages.clear();
                    }
                }


                //Received the messages from the first client(clientHandler2)
                messages = clientHandler2.getReceivedMessages();
                if (!messages.isEmpty()) {
                    synchronized (messages) {
                        for (int i = 0; i < messages.size(); i++) {
                            //send the messages to the other client
                            clientHandler.sendMessage(messages.get(i));
                        }
                        messages.clear();
                    }
                }


             */

                //This code for make a connection with the server
            /*System.out.println("waiting for the response ");


                Socket client = serverSocket.accept();//قبول اتصال من شخص واحد
                //يقوم هذا الكلاس باستقبال البيانات من المتصل وقراءتها بالطريقة التي أريد


                //لقراءة البيانات ممن المصدر القادمة منه
                DataInputStream clientReadSource = new DataInputStream(client.getInputStream());//قراءة ما وصل من المتصل الآخر

                //كتابة أي معلومات للطرف الآخر المتصل من خلال السوكيت
                DataOutputStream clientWriteSource = new DataOutputStream(client.getOutputStream());

                while (true)
                {

                    clientWriteSource.writeUTF("hi i am the server"); //sending message for the source
                    clientWriteSource.writeUTF("you can only ask me once");

                    String sourceResponse = clientReadSource.readUTF();// reading the message is sourcing
                    System.out.println("Client said: " + sourceResponse);
                    if (sourceResponse.equalsIgnoreCase("exit"))
                    {
                        break;
                    }
                    clientWriteSource.writeUTF("we have no service right now, good");

                }
                clientWriteSource.close();
                clientReadSource.close();
                client.close();*//*

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }//End of while loop*/


            }catch(IOException e){
            e.printStackTrace();

            }
        }

        //handleClientMessages
        /*
    private void handleClientMessages(ClientHandler cTemp , ArrayList<String> messages)
    {
        if (!messages.isEmpty()) {
            //Command:message
            synchronized (messages) {
                for (int j = 0; j < messages.size(); j++) {
                    if (cTemp == null)
                        break;

                    String cmd = messages.get(j).trim();
                    if (cmd.equals("exit"))
                    {
                        clientNames.remove(cTemp.getClientName()) ;
                        cTemp.closeAll();
                        clientHandlerArrayList.remove(cTemp) ;
                        cTemp=null ;
                        break ;
                    }
                    if (cmd.contains("@") && cmd.length() > 2) {

                        String part1 = cmd.substring(0, cmd.indexOf("@"));
                        if (part1.length() == cmd.length() - 1)
                            continue;

                        String part2 = cmd.substring(part1.length() + 1);
                        System.out.println("part1 : " + part1 + " part2 : " + part2);

                        switch (part1) {
                            case "broadcast":
                                for (int k = 0; k < clientHandlerArrayList.size(); k++) {
                                    if (clientHandlerArrayList.get(k) != cTemp)
                                        clientHandlerArrayList.get(k).sendMessage(cTemp.getClientName() + "(All)" + part2);
                                }
                                break;

                            case "whisper":
                                part1 = cmd.substring(0, cmd.indexOf("@"));
                                if (part1.length() == cmd.length() - 1)
                                    break;

                                part2 = part2.substring(part1.length() + 1 );

                                if (clientNames.contains(part1)) {

                                    for (int k = 0 ; k < clientHandlerArrayList.size() ; k++)
                                    {
                                        if (clientHandlerArrayList.get(k).getClientName().equals(part1))
                                        {
                                            clientHandlerArrayList.get(k).sendMessage(cTemp.getClientName()+ "(You):" + part2);
                                        }
                                    }

                                } else {
                                    cTemp.sendMessage("No one using this name");
                                }
                                System.out.println("privat part1 " + part1 + ", part2 " + part2);
                                break;

                            case "setName":
                                if (clientNames.contains(part2)) {

                                    cTemp.sendMessage("Name already taken");
                                } else {
                                    clientNames.remove(cTemp.getClientName());
                                    clientNames.add(part2);
                                    cTemp.setClientName(part2);
                                }

                                break;


                        }
                    }


                }
                messages.clear();
            }
        }
    }

         */

    //checkOnClients
    /*
    private void checkOnClients()
    {
        synchronized (clientHandlerArrayList) {
            for (int i = 0; i < clientHandlerArrayList.size(); i++) {
                try {
                    ClientHandler cTemp = clientHandlerArrayList.get(i);
                    if (!cTemp.isOn)
                    {
                        clientNames.remove(clientHandlerArrayList.get(i).getClientName()) ;
                        clientHandlerArrayList.get(i).closeAll() ;
                        clientHandlerArrayList.remove(i);
                        i--;
                        continue;
                    }

                    handleClientMessages(cTemp , cTemp.getReceivedMessages());
                } catch (Exception ex) {
                    //لو لم استطع التواصل مع الكلاينت
                    System.out.println("Error occurred : " + ex);
                    clientNames.remove(clientHandlerArrayList.get(i).getClientName());
                    clientHandlerArrayList.get(i).closeAll();
                    clientHandlerArrayList.remove(i);
                    i--;
                }
            }//End of for
        }//End of synchronized
    }

     */
    public void printAllUserNames()
    {
        System.out.println(clientNames);
    }

    public void printUserCount()
    {
        System.out.println(clientNames.size());
    }


    //makeConnectionWithClient
    /*
    public void makeConnectionWithClient(String clientNumber)
    {
        synchronized (clientHandlerArrayList) {
            for (int i = 0; i < clientHandlerArrayList.size(); i++) {
                try {
                    ClientHandler cTemp = clientHandlerArrayList.get(i);
                    if (!cTemp.isOn)
                    {
                        clientNames.remove(clientHandlerArrayList.get(i).getClientName()) ;
                        clientHandlerArrayList.get(i).closeAll() ;
                        clientHandlerArrayList.remove(i);
                        i--;
                        continue;
                    }

                    handleClientMessages(cTemp , cTemp.getReceivedMessages());
                } catch (Exception ex) {
                    //لو لم استطع التواصل مع الكلاينت
                    System.out.println("Error occurred : " + ex);
                    clientNames.remove(clientHandlerArrayList.get(i).getClientName());
                    clientHandlerArrayList.get(i).closeAll();
                    clientHandlerArrayList.remove(i);
                    i--;
                }
            }//End of for
        }//End of synchronized
    }

     */

    public void sendOtherClientsNumbers(ClientHandler clientHandler)
    {

        for(int i = 0 ; i < clientNumbers.size() ; i++)
        {
                if (!clientHandler.getClientNumber().equals(clientNumbers.get(i)))
                clientHandler.sendMessage(i+"- "+clientNumbers.get(i));
        }

    }

    }

