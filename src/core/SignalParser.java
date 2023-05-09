package core;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface SignalParser extends NimNetworkSignals {

    void sendSignal(int signal, ObjectOutputStream toPlayer);
    int getSignal(ObjectInputStream fromPlayer);
}
