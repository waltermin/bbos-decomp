package net.rim.wica.common.debug.io;

public interface IInputByteStreamAdapter {
   int readBuffer(int var1, byte[] var2);

   boolean readBoolean();

   int readInt();

   long readLong();

   String readString();
}
