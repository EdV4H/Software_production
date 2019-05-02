import java.util.Random;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

class ClientProcThread extends Thread {
    private int number;
    private Socket incoming;
    private InputStreamReader myIsr;
    private BufferedReader myIn;
    private PrintWriter myOut;
    private String myName;

    private boolean isAlive, isAction;
    private int x, y;

    public ClientProcThread (int n, Socket i, InputStreamReader isr, BufferedReader in, PrintWriter out) {
        number = n;
		incoming = i;
		myIsr = isr;
		myIn = in;
        myOut = out;
        
        isAlive = true;
        isAction = false;
    }

    public String getMyName () {return myName;}
    public boolean getIsAction () {return isAction;}

    public String getStatus () {
        String msg = myName + " " + isAlive + " " + x + " " + y;
        return msg;
    }

    public void setTranslateX (int px) {x = px;}
    public void setTranslateY (int py) {y = py;}
    public void setIsAction (boolean b) {isAction = b;}

    public void run () {
        try {
            myName = myIn.readLine();
            myOut.println("Client No." + number + " ( " + myName +  " ) logged in.");

            while (true) {
                String inputLine = myIn.readLine();
                System.out.println("Received from client No."+number+"("+myName+"), Messages: "+ inputLine);
                if (inputLine != null) {
                    if (inputLine.equals("DISCONNECT")) {
                        isAlive = false;
                        GameServer.disconnect(number);
                        myOut.println("DISCONNECT");
                        break;
                    }
                    if (inputLine.startsWith("CMD")) isAction = true;
                    GameServer.SendAll(inputLine + " " + number, myName, false);
                }
            }
        } catch (Exception e) {
            System.out.println("Disconnect from client No. " + number + "(" + myName + ")");
            GameServer.SetFlag(number, false);
        }
    }
}

class GameServer extends Thread{
    private static Random rnd = new Random();
    private static int maxConnection = 2;
    private static Socket[] incoming;
    private static boolean[] flag;
    private static InputStreamReader[] isr;
    private static BufferedReader[] in;
    private static PrintWriter[] out;
    private static ClientProcThread[] myClientProcThread;
    private static int member;
    private static boolean isGameStarted = false;

    public static void SendAll(String str, String myName, boolean withoutOwn) {
        for (int i=0; i<member; i++) {
            if (flag[i] == true) {
                if (withoutOwn==true && myClientProcThread[i].getMyName().equals(myName)) continue;
                out[i].println(str);
                out[i].flush();
                System.out.println("Send messages to client No."+i);
            }
        }
    }

    public static void GameStart () {
        StringBuilder buf = new StringBuilder();
        buf.append("START ");
        buf.append(maxConnection);
        for (int i = 0; i < maxConnection; i++) {
            myClientProcThread[i].setTranslateX(20  + rnd.nextInt(390));
            myClientProcThread[i].setTranslateY(20  + rnd.nextInt(390));
            buf.append(" " + myClientProcThread[i].getStatus());
        }
        SendAll(buf.toString(), "Server", false);
        isGameStarted = true;
    }

    public static void disconnect (int n) {
        member--;
        incoming[n] = null;
        flag[n] = false;
        isr[n] = null;
        in[n] = null;
        out[n] = null;
        myClientProcThread[n] = null;
    }

    public static void SetFlag (int n, boolean value) {
        flag[n] = value;
    }

    public void run () {
		incoming = new Socket[maxConnection];
		flag = new boolean[maxConnection];
		isr = new InputStreamReader[maxConnection];
		in = new BufferedReader[maxConnection];
		out = new PrintWriter[maxConnection];
		myClientProcThread = new ClientProcThread[maxConnection];
		
		int n = 0;
		member = 0;

		try {
			System.out.println("The server has launched!");
			ServerSocket server = new ServerSocket(10000);
			while (!isGameStarted) {
                System.out.println("No."+n+" is waiting...");
				incoming[n] = server.accept();
				flag[n] = true;
                System.out.println("Accept client No." + n);
                
				isr[n] = new InputStreamReader(incoming[n].getInputStream());
				in[n] = new BufferedReader(isr[n]);
				out[n] = new PrintWriter(incoming[n].getOutputStream(), true);
				
				myClientProcThread[n] = new ClientProcThread(n, incoming[n], isr[n], in[n], out[n]);//必要なパラメータを渡しスレッドを作成
				myClientProcThread[n] .start();
                n++;
                member = n;
                
                Thread.sleep(1000); //追加したスレッドが動き出すのを待つ
                
                if (member == maxConnection && !isGameStarted) GameStart();
            }
            while (true) {
                boolean flag = true;
                for (int i = 0; i < maxConnection; i++) if (!(myClientProcThread[i].getIsAction())) flag = false;
                if (flag) {
                    SendAll("ACTION", "Server", false);
                    for (int i = 0; i < maxConnection; i++) myClientProcThread[i].setIsAction(false);
                }
            }
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
        }
	}
}