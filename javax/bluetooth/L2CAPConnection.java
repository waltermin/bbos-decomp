package javax.bluetooth;

import javax.microedition.io.Connection;

public interface L2CAPConnection extends Connection {
   int DEFAULT_MTU = 672;
   int MINIMUM_MTU = 48;

   int getTransmitMTU();

   int getReceiveMTU();

   void send(byte[] var1);

   int receive(byte[] var1);

   boolean ready();
}
