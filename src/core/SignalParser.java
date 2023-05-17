package core;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface SignalParser extends NimNetworkSignals {

    /**
     * Send a signal to player
     * @param signal The integer signal to send
     * @param toPlayer The player to send signal to
     */
    void sendSignal(int signal, ObjectOutputStream toPlayer);

    /**
     * Receives a signal from the player
     * @param fromPlayer Player to receive signal from
     * @return the signal received
     */
    int getSignal(ObjectInputStream fromPlayer);
}
