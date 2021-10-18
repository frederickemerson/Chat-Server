import java.io.*;
import java.net.*;
import java.util.*;
class chat_client
{
    void sendClientName(String name,Socket clientSocket)throws Exception
    {
        PrintWriter sd = new PrintWriter(clientSocket.getOutputStream(),true);
        sd.println(name);
    }

    String getData(Socket clientSocket)throws Exception
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputText = input.readLine();
        return inputText;
    }

    int numberOnline(String people)
    {
        return (people.indexOf("@")<0)?1:people.split("@").length;
    }

    void peopleOnline(String people)
    {
        System.out.println("People Online:");
        String online[] = people.split("@");
        for(int i=0;i<online.length;i++)
        {
            System.out.print(online[i]+"  ");
        }
        System.out.println();
    }

    void process(String data,Socket clientSocket,String userName)throws Exception
    {
        String arr[]=data.split("~");
        if(arr[0].equals("List"))
        {
            if(numberOnline(arr[1])>1)
            {
                peopleOnline(arr[1]);
                process(clientSocket,userName);
            }
            else
            {
                System.out.println("You are alone ???");
            }
        }
        if(arr[0].equals("Sent"))
        {
            System.out.println(arr[1]);
        }
    }

    void main()
    {
        try
        {

            System.out.println("Client Started");
            Socket clientSocket = new Socket("localhost",1717);

            System.out.println("Note: Type Message in particular format(Whom to Talk to:message) ");

            System.out.println("Enter name :");
            String userName = new Scanner(System.in).nextLine();
            sendClientName(userName,clientSocket);
            System.out.println("Connected to Server !!!");

            while(true)
            {
                String data=getData(clientSocket);
                process(data,clientSocket,userName);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    void process(Socket clientSocket,String byWhom)throws Exception
    {
        send send = new send(clientSocket,byWhom);
        new Thread(send).start();
    }

}
class send implements Runnable
{
    Socket clientSocket;
    String byWhom;
    send(Socket sock,String str)
    {
        this.clientSocket=sock;
        this.byWhom=str;
    }

    @Override
    public void run()
    {
        while(true)
        {
        try
        {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            String text = input.readLine();
            String toWhom = text.split(":")[0];

            String message = text.split(":")[1];
            PrintWriter send = new PrintWriter(this.clientSocket.getOutputStream(),true);
            send.println(message+"@"+toWhom+"@"+this.byWhom);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    }
}

class receive implements Runnable
{
    Socket clientSocket;
    receive(Socket sock)
    {
        this.clientSocket=sock;
    }

    public void run()
    {
        while(true)
        {
            try
            {

                BufferedReader receive = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                String receivedText = receive.readLine();
                System.out.println(receivedText);
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}