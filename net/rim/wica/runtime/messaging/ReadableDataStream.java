package net.rim.wica.runtime.messaging;

import net.rim.wica.transport.message.data.DataStreamV1;
import net.rim.wica.transport.util.DataException;

public class ReadableDataStream {
   private DataStreamV1 _dataStream;

   public ReadableDataStream(DataStreamV1 dataStream) {
      this._dataStream = dataStream;
   }

   public byte readByte() {
      try {
         return this._dataStream.readByte();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public boolean readBoolean() {
      try {
         return this._dataStream.readBoolean();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public int readInt() {
      try {
         return this._dataStream.readInt();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public long readLong() {
      try {
         return this._dataStream.readLong();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public double readDouble() {
      double var10000;
      try {
         var10000 = this._dataStream.readDouble();
      } catch (DataException e) {
         throw new MessageException(e);
      }

      return var10000;
   }

   public String readString() {
      try {
         return this._dataStream.readString();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public byte readByte(byte defaultValue) {
      try {
         return this._dataStream.readByte(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public boolean readBoolean(boolean defaultValue) {
      try {
         return this._dataStream.readBoolean(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public int readInt(int defaultValue) {
      try {
         return this._dataStream.readInt(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public long readLong(long defaultValue) {
      try {
         return this._dataStream.readLong(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public double readDouble(double defaultValue) {
      double var10000;
      try {
         var10000 = this._dataStream.readDouble(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }

      return var10000;
   }

   public String readString(String defaultValue) {
      try {
         return this._dataStream.readString(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public byte[] readBlob() {
      try {
         return this._dataStream.readBlob();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public boolean[] readBooleanArray() {
      try {
         return this._dataStream.readBooleanArray();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public int[] readIntArray() {
      try {
         return this._dataStream.readIntArray();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public double[] readDoubleArray() {
      try {
         return this._dataStream.readDoubleArray();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public long[] readLongArray() {
      try {
         return this._dataStream.readLongArray();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public String[] readStringArray() {
      try {
         return this._dataStream.readStringArray();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public boolean startComponentRead() {
      try {
         return this._dataStream.startComponentRead();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public int startComponentArrayRead() {
      try {
         return this._dataStream.startComponentArrayRead();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public void rewind() {
      this._dataStream.rewind();
   }
}
