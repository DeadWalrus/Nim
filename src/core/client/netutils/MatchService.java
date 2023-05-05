package core.client.netutils;


import core.client.NimMatchDisplay;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;

public class MatchService implements Runnable{

    private final ObjectInputStream fromServer;
    private final ObjectOutputStream toServer;
    private TextArea taOutputLog;
    private boolean isStarted;
    public MatchService(Socket server) throws IOException{
        this.toServer = new ObjectOutputStream(server.getOutputStream());
        this.fromServer = new ObjectInputStream(server.getInputStream());
    }

    public void run(){
        this.isStarted = true;
        try {
            if(!waitForMatch()){
                System.out.println("Match cannot be made");
                this.taOutputLog.appendText("Match cannot be made");
                return;
            }
            startMatch();
        } catch (IOException | ClassNotFoundException ex){
            System.out.println("Match service run error");
            this.taOutputLog.appendText("Match service run error");
            try{
                this.toServer.writeObject(-1);
            } catch(IOException e){
                System.out.println("Could not end session");
            }

            ex.printStackTrace();
        }
    }

    public boolean waitForMatch() throws IOException {
        return this.fromServer.readBoolean();
    }

    public void setOutputLog(TextArea taOutputLog){
        this.taOutputLog = taOutputLog;
    }

    public void startMatch() throws IOException, ClassNotFoundException {
        // Start game.
        System.out.println("Match started");
        Integer i = 100;
        toServer.writeObject(i);
        toServer.flush();
        System.out.println(fromServer.readObject());
    }

    public void endMatch() throws IOException{
        this.toServer.writeObject(-1);
        this.toServer.close();
        this.fromServer.close();
        this.isStarted = false;
    }

    public boolean isRunning(){
        return this.isStarted;
    }
}
// Data parser. Determine what type of data is being sent.
// Server sends signals indicating its state.