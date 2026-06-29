package net.rim.wica.transport.message.data;

public interface DataStreamV1 {
   int available();

   boolean eod();

   void rewind();

   byte readByte();

   boolean readBoolean();

   int readInt();

   long readLong();

   float readFloat();

   double readDouble();

   String readString();

   byte readByte(byte var1);

   boolean readBoolean(boolean var1);

   int readInt(int var1);

   long readLong(long var1);

   float readFloat(float var1);

   double readDouble(double var1);

   String readString(String var1);

   byte[] readBlob();

   boolean[] readBooleanArray();

   int[] readIntArray();

   long[] readLongArray();

   float[] readFloatArray();

   double[] readDoubleArray();

   String[] readStringArray();

   boolean startComponentRead();

   int startComponentArrayRead();

   void noteDefaultField(boolean var1);

   void writeByte(byte var1);

   void writeBoolean(boolean var1);

   void writeInt(int var1);

   void writeLong(long var1);

   void writeFloat(float var1);

   void writeDouble(double var1);

   void writeString(String var1);

   void writeBlob(byte[] var1);

   void writeBooleanArray(boolean[] var1);

   void writeIntArray(int[] var1);

   void writeLongArray(long[] var1);

   void writeFloatArray(float[] var1);

   void writeDoubleArray(double[] var1);

   void writeStringArray(String[] var1);

   void startComponentWrite(boolean var1);

   void startComponentArrayWrite(int var1);
}
