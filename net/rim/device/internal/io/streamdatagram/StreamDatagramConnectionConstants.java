package net.rim.device.internal.io.streamdatagram;

public interface StreamDatagramConnectionConstants {
   String MAX_CONNECTIONS_REACHED_STR = "Max connections opened.";
   String CONN_TIMED_OUT_STR = "Connection timed out";
   int MAX_NUM_SIMULTANEOUS_TCP_CONNECTIONS = 30;
   int TIMER_CONN_DEFAULT_TIMEOUT = 75000;
   int TIMER_CONN_MIN_TIMEOUT = 4000;
   int TUNNEL_OPEN_TIMEOUT = 30000;
   String CONN_CLOSED_STR = "Connection closed";
   long DEFAULT_DATAGRAM_WAIT_TIMEOUT = 30000L;
   String STR_STREAM_IS_ALREADY_CLOSED = "Stream was already closed";
   int STREAM_DEBUG_LEVEL = 1000000;
}
