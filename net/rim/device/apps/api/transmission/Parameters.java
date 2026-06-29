package net.rim.device.apps.api.transmission;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntIntHashtable;

public class Parameters {
   protected Parameters$Integers _offsets;
   protected IntIntHashtable _counts;
   protected DataBuffer _buffer;
   protected int _start;

   public Parameters(int capacityInt, int incrementInt) {
      this._offsets = new Parameters$Integers(capacityInt, incrementInt, null);
      this._counts = new IntIntHashtable(capacityInt);
   }

   public Parameters(DataBuffer aDataBuffer, int capacityInt, int incrementInt) {
      this(capacityInt, incrementInt);
      this._buffer = aDataBuffer;
      this._start = aDataBuffer.getPosition();
   }

   public DataBuffer getDataBuffer() {
      return this._buffer;
   }

   public void setDataBuffer(DataBuffer aDataBuffer) {
      this._offsets.removeAll();
      this._buffer = aDataBuffer;
      this._start = aDataBuffer.getPosition();
   }

   public int read(DataBuffer aDataBuffer, byte boundaryNameByte) {
      this.setDataBuffer(aDataBuffer);
      return this.read(boundaryNameByte);
   }

   public int read(DataBuffer aDataBuffer) {
      this.setDataBuffer(aDataBuffer);
      return this.read();
   }

   public int read(byte boundaryNameByte) {
      byte name = 0;

      while ((name = this._buffer.readByte()) != boundaryNameByte) {
         this.addToIndex(name, this._buffer.getPosition() - 1);
         this._buffer.skipBytes(this._buffer.readCompressedInt());
      }

      return this._buffer.getPosition();
   }

   public int read() {
      byte name = 0;

      while (!this._buffer.eof()) {
         name = this._buffer.readByte();
         this.addToIndex(name, this._buffer.getPosition() - 1);
         this._buffer.skipBytes(this._buffer.readCompressedInt());
      }

      return this._buffer.getPosition();
   }

   public boolean isEmpty() {
      return this._offsets.size() == 0;
   }

   public byte[][] get(byte nameByte) {
      int count = this.count(nameByte);
      byte[][] result = (byte[][])null;
      if (count > 0) {
         result = new byte[count][];
         int size = this._offsets.size();
         int position = this._buffer.getPosition();
         int counter = 0;

         label45:
         try {
            for (int index = 0; index < size; index++) {
               this._buffer.setPosition(this._offsets.get(index));
               if (this._buffer.readByte() == nameByte) {
                  result[counter++] = this._buffer.readByteArray();
                  if (counter >= count) {
                     break;
                  }
               }
            }
         } finally {
            break label45;
         }

         this._buffer.setPosition(position);
      }

      return result;
   }

   public byte[] getFirst(byte nameByte) {
      byte[] result = null;
      if (this.count(nameByte) > 0) {
         int firstIndex = this.resolveInIndex(nameByte, 0);
         int position = this._buffer.getPosition();

         label24:
         try {
            this._buffer.setPosition(firstIndex);
            this._buffer.readByte();
            result = this._buffer.readByteArray();
         } finally {
            break label24;
         }

         this._buffer.setPosition(position);
      }

      return result;
   }

   public int count(byte nameByte) {
      int count = this._counts.get(nameByte & 255);
      return count == -1 ? 0 : count;
   }

   public boolean has(byte nameByte) {
      return this._counts.get(nameByte & 255) > 0;
   }

   public void add(byte nameByte, byte[] valueBytes) {
      this.add(nameByte, valueBytes, 0, valueBytes.length);
   }

   public void add(byte nameByte, byte valueByte) {
      this.addToIndex(nameByte, this._buffer.getPosition());
      this._buffer.writeByte(nameByte);
      this._buffer.writeByte(1);
      this._buffer.writeByte(valueByte & 255);
   }

   public void add(byte nameByte, byte[] valueBytes, int offsetInt, int lengthInt) {
      this.addToIndex(nameByte, this._buffer.getPosition());
      this._buffer.writeByte(nameByte);
      this._buffer.writeByteArray(valueBytes, offsetInt, lengthInt);
   }

   public void add(byte nameByte, byte encodingByte, byte[] valueBytes) {
      this.addToIndex(nameByte, this._buffer.getPosition());
      this._buffer.writeByte(nameByte);
      this._buffer.writeCompressedInt(valueBytes.length + 1);
      this._buffer.writeByte(encodingByte);
      this._buffer.write(valueBytes, 0, valueBytes.length);
   }

   public int resolveInIndex(byte nameByte, int indexInt) {
      if (this.count(nameByte) <= indexInt) {
         return -1;
      }

      int count = -1;
      int result = -1;
      int offset = -1;
      int size = this._offsets.size();
      int position = this._buffer.getPosition();

      label45:
      try {
         for (int index = 0; index < size; index++) {
            offset = this._offsets.get(index);
            this._buffer.setPosition(offset);
            if (this._buffer.readByte() == nameByte) {
               if (++count == indexInt) {
                  result = offset;
                  break;
               }
            }
         }
      } finally {
         break label45;
      }

      this._buffer.setPosition(position);
      return result;
   }

   protected void addToIndex(byte nameByte, int positionInt) {
      int nameInt = nameByte & 255;
      int count = this._counts.get(nameInt);
      this._counts.put(nameInt, count == -1 ? 1 : count + 1);
      this._offsets.add(positionInt);
   }
}
