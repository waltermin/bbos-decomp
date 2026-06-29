package javax.microedition.apdu;

import javax.microedition.io.Connection;

public interface APDUConnection extends Connection {
   byte[] exchangeAPDU(byte[] var1);

   byte[] getATR();

   byte[] changePin(int var1);

   byte[] disablePin(int var1);

   byte[] enablePin(int var1);

   byte[] enterPin(int var1);

   byte[] unblockPin(int var1, int var2);
}
