package core;

/**
 * Constants for the signals recognized by the server and clients
 */
public interface NimNetworkSignals {
    int CLOSE_CONNECTION = -1;
    int BOARD_DATA = 100;
    int CONNECTION_ESTABLISHED = 500;
    int CONNECTION_PROBE = 1;
    int TURN_INDICATOR = 200;
    int WON_SIGNAL = 999;
    int LOSE_SIGNAL = 111;
}
