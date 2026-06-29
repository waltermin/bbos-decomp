package javax.microedition.io;

public interface DatagramConnection extends Connection {
   int getMaximumLength();

   int getNominalLength();

   void send(Datagram var1);

   void receive(Datagram var1);

   Datagram newDatagram(int var1);

   Datagram newDatagram(int var1, String var2);

   Datagram newDatagram(byte[] var1, int var2);

   Datagram newDatagram(byte[] var1, int var2, String var3);
}
