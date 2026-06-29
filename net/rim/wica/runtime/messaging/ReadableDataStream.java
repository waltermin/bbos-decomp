package net.rim.wica.runtime.messaging;

import net.rim.wica.transport.message.data.DataStreamV1;
import net.rim.wica.transport.util.DataException;

public class ReadableDataStream {
   private DataStreamV1 _dataStream;

   public ReadableDataStream(DataStreamV1 dataStream) {
      this._dataStream = dataStream;
   }

   public byte readByte() throws MessageException {
      try {
         return this._dataStream.readByte();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public boolean readBoolean() throws MessageException {
      try {
         return this._dataStream.readBoolean();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public int readInt() throws MessageException {
      try {
         return this._dataStream.readInt();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public long readLong() throws MessageException {
      try {
         return this._dataStream.readLong();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public double readDouble() throws MessageException {
      double var10000;
      try {
         var10000 = this._dataStream.readDouble();
      } catch (DataException e) {
         throw new MessageException(e);
      }

      return var10000;
   }

   public String readString() throws MessageException {
      try {
         return this._dataStream.readString();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public byte readByte(byte defaultValue) throws MessageException {
      try {
         return this._dataStream.readByte(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public boolean readBoolean(boolean defaultValue) throws MessageException {
      try {
         return this._dataStream.readBoolean(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public int readInt(int defaultValue) throws MessageException {
      try {
         return this._dataStream.readInt(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public long readLong(long defaultValue) throws MessageException {
      try {
         return this._dataStream.readLong(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public double readDouble(double defaultValue) throws MessageException {
      double var10000;
      try {
         var10000 = this._dataStream.readDouble(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }

      return var10000;
   }

   public String readString(String defaultValue) throws MessageException {
      try {
         return this._dataStream.readString(defaultValue);
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public byte[] readBlob() throws MessageException {
      try {
         return this._dataStream.readBlob();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public boolean[] readBooleanArray() throws MessageException {
      try {
         return this._dataStream.readBooleanArray();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public int[] readIntArray() throws MessageException {
      try {
         return this._dataStream.readIntArray();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public double[] readDoubleArray() throws MessageException {
      try {
         return this._dataStream.readDoubleArray();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public long[] readLongArray() throws MessageException {
      try {
         return this._dataStream.readLongArray();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public String[] readStringArray() throws MessageException {
      try {
         return this._dataStream.readStringArray();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public boolean startComponentRead() throws MessageException {
      try {
         return this._dataStream.startComponentRead();
      } catch (DataException e) {
         throw new MessageException(e);
      }
   }

   public int startComponentArrayRead() throws MessageException {
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
