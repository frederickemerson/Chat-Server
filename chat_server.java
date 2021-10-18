import java.net.*;
import java.io.*;
import java.util.*;
class chat_server
{
    public static HashMap<String,Socket> online = new HashMap<String,Socket>();
    String getClientName(Socket clientSocket)throws Exception
    {
        BufferedReader r = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String clientName = r.readLine();
        return clientName;
    }

    void sendPeople(String text,Socket clientSocket)throws Exception
    {
        PrintWriter sd = new PrintWriter(clientSocket.getOutputStream(),true);
        sd.println("List~"+text);   
    }

    String stringify()
    {
        String result="";
        Set<String> keys = online.keySet();
        for (String k : keys) {
            result+=k+"@";
        }
        return result.substring(0,result.length()-1);
    }

    void spill()
    {
        online.forEach((k,v)-> System.out.println(k+":"+v));
    }

    void send()
    {
        try
        {
            for (Map.Entry<String, Socket> entry : online.entrySet()) {
                String k = entry.getKey();
                Socket v = entry.getValue();
                sendPeople(stringify(),v);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    void main()
    {
        try
        {
            System.out.println("Waiting for clients........");

            ServerSocket serverSocket = new ServerSocket(1717);
            int c =0;
            while(true)
            {
                Socket clientSocket = serverSocket.accept();
                String clientName=getClientName(clientSocket);
                online.put(clientName,clientSocket);
                System.out.println();
                System.out.println("Connection established!!!");
                System.out.println("Connected to : "+clientSocket);
                System.out.println();
                clientHandler multiClient = new clientHandler(clientSocket);
                new Thread(multiClient).start();
                send();
                c++;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
class clientHandler extends chat_server implements Runnable 
{
    final Socket clientSocket;
    
    clientHandler(Socket socket)
    {
        this.clientSocket=socket;
         }

    void converse(String keyData)throws Exception
    {
        System.out.println(keyData);
        String message = keyData.split("@")[0];
        String toWhom = keyData.split("@")[1];
        String byWhom = keyData.split("@")[2];
        spill();
        Socket toSend=super.online.get(toWhom);
        String send = byWhom+" says : "+message; 

        System.out.println(toSend);

        PrintWriter pwSend = new PrintWriter(toSend.getOutputStream(),true);
        pwSend.println("Sent~"+send);
    }
    
    Socket returnSocket()
    {
        return this.clientSocket;
    }
    
    @Override
    public void run()
    {
        try
        {
            while(true)
            {
                BufferedReader receive = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                String receivedText = receive.readLine();
                converse(receivedText);
                //send.getList();
                //new converse(receivedText).talkTo();
                /*String clientName = receivedText.split("#")[1];
                String data = receivedText.split("#")[0];

                if(data.equals("bye"))
                {
                System.out.println(clientName+" has left!!!!");
                break;
                }

                System.out.println(clientName+" Says : "+data);
                System.out.println();

                System.out.println("Enter message : ");
                BufferedReader send = new BufferedReader(new InputStreamReader(System.in));
                String inputText= send.readLine();

                System.out.println("Sending result");
                System.out.println();

                PrintWriter printWriter = new PrintWriter(this.clientSocket.getOutputStream(),true);
                printWriter.println(data);*/
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    void send(String msg,PrintWriter pw)
    {
        pw.println(msg+"is online now");
    }
}



