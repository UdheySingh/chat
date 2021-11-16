import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server
{
    private ArrayList<ServerThread> sockets = new ArrayList<>();
    private ArrayList <String> utenti = new ArrayList<>();
    private ServerSocket s;

    public void start()
    {
        try
        {
            System.out.println("server in attesa");
            s = new ServerSocket(6789);

            for(int i = 0; i < 10; i++) {
                sockets.add(new ServerThread(s.accept()));
                sockets.get(i).start();
            }
            s.close();
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Errore durante l'istanza del messaggio!");
            System.exit(1);
        }
    }

    public static void main(String[] args) 
    {
        Server tcpServer = new Server();
        tcpServer.start();
    }

    public class ServerThread extends Thread
    {
        private Socket clientS;
        private BufferedReader inDalClient;
        private DataOutputStream outVersoClient;
        private String usernameClient;


        public ServerThread (Socket socket)
        {
            this.clientS = socket;
        }

        @Override
        public void run()
        {
            try{
                comunica();
            } catch (Exception e)
            {
                e.toString();
            }
        }

        public void comunica () throws Exception 
        {
            inDalClient = new BufferedReader(new InputStreamReader(clientS.getInputStream()));
            outVersoClient = new DataOutputStream(clientS.getOutputStream());
            outVersoClient.writeBytes("Inserisci username" + '\n');
            usernameClient = inDalClient.readLine();

            if(utenti.isEmpty()) utenti.add(usernameClient);
            else 
            {
                for(int i = 0; i < utenti.size(); i++)
                {
                    while(usernameClient.equals(utenti.get(i)))
                    {
                        outVersoClient.writeBytes("Nome utente non disponibile, inserirne uno nuovo" + '\n');
                        usernameClient = inDalClient.readLine();
                    }
                }
            utenti.add(usernameClient);
            }               

            System.out.println(usernameClient + " connesso");
            outVersoClient.writeBytes(usernameClient + " connesso" + '\n' + '\n');
            for(int i = 0; i < sockets.size(); i++)
            {
                sockets.get(i).outVersoClient.writeBytes("Utenti connessi:" + '\n');
                for(int j = 0; j < utenti.size(); j++) 
                {
                    sockets.get(i).outVersoClient.writeBytes("Utente:" + utenti.get(j) + '\n');
                }
                sockets.get(i).outVersoClient.writeBytes("\n");
            }
            
            for(;;) 
            {
                String mex = inDalClient.readLine();
                if(mex.equalsIgnoreCase("FINE")) 
                {
                    System.out.println(usernameClient + ": utente disconnesso" + '\n');
                    for (ServerThread s : sockets) 
                    {
                        if(s != this) s.outVersoClient.writeBytes("Utente " + usernameClient + " disconnesso" + '\n');
                    }
                    utenti.remove(usernameClient);
                    sockets.remove(this);
                    for(int i = 0; i < sockets.size(); i++) 
                    {
                        sockets.get(i).outVersoClient.writeBytes("Utenti connessi:" + '\n');
                        for(int j = 0; j < utenti.size(); j++) 
                        {
                            sockets.get(i).outVersoClient.writeBytes("Utente:" + utenti.get(j) + '\n');
                        }
                        sockets.get(i).outVersoClient.writeBytes("\n");
                    }
                    break;
                }
                else 
                {
                    String[] mexSplit = mex.split("@");
                    if(sockets.size() > 1) 
                    {
                        if(mexSplit[1].equalsIgnoreCase("Everyone")) 
                        {         
                            for (ServerThread s : sockets) 
                            {
                                if(s != this) s.outVersoClient.writeBytes(usernameClient + ": " + mexSplit[0] + ("(Everyone)") + '\n');
                            }
                        }
                        else 
                        {
                            for (ServerThread s : sockets) 
                            {
                                if(s != this) {
                                    if(s.usernameClient.equals(mexSplit[1])) 
                                    {
                                        s.outVersoClient.writeBytes(usernameClient + ": " + mexSplit[0] + '\n');
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    else outVersoClient.writeBytes("Nessuno è connesso" + '\n');
                    break;
                }
            }
        outVersoClient.close();
        inDalClient.close();
        clientS.close();
        }
    }
}