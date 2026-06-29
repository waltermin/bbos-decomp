package javax.microedition.io;

public interface UDPDatagramConnection extends DatagramConnection {
   String getLocalAddress();

   int getLocalPort();
}
