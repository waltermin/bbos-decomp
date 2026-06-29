package net.rim.wica.runtime.messaging;

import net.rim.wica.transport.message.data.DataStreamV1;

public class WritableDataStream {
   private DataStreamV1 _dataStream;

   public WritableDataStream(DataStreamV1 dataStream) {
      this._dataStream = dataStream;
   }

   public void writeByte(byte value) {
      this._dataStream.writeByte(value);
   }

   public void writeBoolean(boolean value) {
      this._dataStream.writeBoolean(value);
   }

   public void writeInt(int value) {
      this._dataStream.writeInt(value);
   }

   public void writeLong(long value) {
      this._dataStream.writeLong(value);
   }

   public void writeDouble(double value) {
      this._dataStream.writeDouble(value);
   }

   public void writeString(String str) {
      this._dataStream.writeString(str);
   }

   public void writeBlob(byte[] b) {
      this._dataStream.writeBlob(b);
   }

   public void writeBooleanArray(boolean[] value) {
      this._dataStream.writeBooleanArray(value);
   }

   public void writeIntArray(int[] value) {
      this._dataStream.writeIntArray(value);
   }

   public void writeLongArray(long[] value) {
      this._dataStream.writeLongArray(value);
   }

   public void writeDoubleArray(double[] value) {
      this._dataStream.writeDoubleArray(value);
   }

   public void writeStringArray(String[] str) {
      this._dataStream.writeStringArray(str);
   }

   public void startComponentWrite(boolean isNull) {
      this._dataStream.startComponentWrite(isNull);
   }

   public void startComponentArrayWrite(int arrayLength) {
      this._dataStream.startComponentArrayWrite(arrayLength);
   }

   public void noteDefaultField(boolean useDefaultValue) {
      this._dataStream.noteDefaultField(useDefaultValue);
   }

   public void close() {
      this._dataStream.rewind();
   }
}
