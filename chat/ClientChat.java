import java.io.*;
import java.net.*;

public class ClientChat
{
    private final static int serverPort = 6789;
    private Socket s;
    private BufferedReader input;
    private BufferedReader inFromServer;
    private DataOutputStream outToServer;
    private SendThread sendThread;
    private ReadThread readThread;
    
    public static void main(String args[]) throws UnknownHostException, IOException 
    {
        ClientChat client = new ClientChat();
        client.connect();
    } 
    
    public void connect() 
    {
        input = new BufferedReader(new InputStreamReader(System.in));
        
        try 
        {
            InetAddress ip = InetAddress.getByName("localhost"); 
          
            s = new Socket(ip, serverPort); 

            inFromServer = new BufferedReader (new InputStreamReader (s.getInputStream()));
            outToServer = new DataOutputStream(s.getOutputStream()); 

            sendThread = new SendThread();
            readThread = new ReadThread();
        }
        catch(Exception ex) 
        {
           ex.toString();
        }    
    }

    public class SendThread extends Thread
    {
        public SendThread() 
        {
            start();
        }

        @Override
        public void run() 
        {
            try 
            {
                for(;;) 
                {
                    String mex = input.readLine();
                    if(mex.equalsIgnoreCase("FINE")) 
                    {
                        outToServer.writeBytes(mex + '\n');
                        outToServer.writeBytes("Connessione in chiusura..." + '\n');                      
                        close();
                        break;
                    }
                    else 
                    {
                       System.out.println("IO: " + mex);
                        outToServer.writeBytes(mex + '\n');
                    }
                }
            }
            catch (Exception ex) 
            {
                ex.toString();
                System.exit(1);
            }
        }

        public void close() 
        {
            try 
            {
                inFromServer.close();
                outToServer.close();
                s.close();
                readThread.close();
                this.stop();
            }
            catch(Exception ex) 
            {
                ex.toString();
            }
            System.exit(0);
        }
    }

    public class ReadThread extends Thread
    {
        public ReadThread() 
        {
            start();
        }

        @Override
        public void run() 
        {
            try 
            {
                for(;;) 
                {
                    String mex = inFromServer.readLine();
                    if(mex != null) System.out.println(mex);
                }
            }
            catch (Exception ex) 
            {
                ex.toString();
                System.exit(1);
            }
        }

        public void close() 
        {
            try 
            {
                this.stop();
            }
            catch(Exception ex) 
            {
                ex.toString();
            }
            System.exit(0);
        }
    }
}