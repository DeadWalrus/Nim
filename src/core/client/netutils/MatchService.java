package core.client.netutils;


import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;

public class MatchService implements Runnable{

    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private TextArea taOutputLog;
    public MatchService(Socket server) throws IOException{
        this.toServer = new ObjectOutputStream(server.getOutputStream());
        this.fromServer = new ObjectInputStream(server.getInputStream());
    }

    public void run(){
        try {
            if(!waitForMatch()){
                System.out.println("Match cannot be made");
                this.taOutputLog.appendText("Match cannot be made");
                return;
            }
            startMatch();
        } catch (IOException ex){
            System.out.println("Match service run error");
            this.taOutputLog.appendText("Match service run error");
            ex.printStackTrace();
        }
    }

    public boolean waitForMatch() throws IOException {
        if(!this.fromServer.readBoolean()){
            return false;
        }
        return true;
    }

    public void setOutputLog(TextArea taOutputLog){
        this.taOutputLog = taOutputLog;
    }

    public void startMatch() throws IOException{
        System.out.println("match starting");
        this.toServer.writeBoolean(true);
        this.toServer.flush();

    }

    public void endMatch() throws IOException{
        this.toServer.close();
        this.fromServer.close();
    }
}
// Data parser. Determine what type of data is being sent.
// Server sends signals indicating its state.