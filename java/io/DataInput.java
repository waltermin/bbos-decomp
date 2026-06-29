package java.io;

public interface DataInput {
   void readFully(byte[] var1);

   void readFully(byte[] var1, int var2, int var3);

   int skipBytes(int var1);

   boolean readBoolean();

   byte readByte();

   int readUnsignedByte();

   short readShort();

   int readUnsignedShort();

   char readChar();

   int readInt();

   long readLong();

   float readFloat();

   double readDouble();

   String readUTF();
}
