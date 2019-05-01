import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient extends Thread {
    private static PrintWriter out;
    Socket socket;
    private static String myName;

    public GameClient(Socket s, String n) {
        socket = s;
        myName = n;
    }

    public static String getMyName () {return myName;}

    public void run() {
        try {
            System.out.println("socket = " + socket);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(myName);

            while (true) {
                String inputLine = in.readLine();
                System.out.println(inputLine);
                if (inputLine != null) {
                    if (inputLine.equals("DISCONNECT")) break;
                    Game.receiveCmd(inputLine);
                }
                //try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace();}
            }
            socket.close();
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        }
    }

    public static void Sendmessage (String msg) {
        out.println(msg);
        out.flush();
    }
}