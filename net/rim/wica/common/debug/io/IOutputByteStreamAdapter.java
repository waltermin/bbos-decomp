package net.rim.wica.common.debug.io;

public interface IOutputByteStreamAdapter {
   void writeBoolean(boolean var1);

   void writeBuffer(int var1, byte[] var2);

   void writeInt(int var1);

   void writeLong(long var1);

   void writeString(String var1);

   void flush();
}
