package net.rim.wica.transport.internal.message.data;

import net.rim.wica.transport.message.data.DataStreamV1;

public class DataStreamV1_0 extends DataBuffer implements DataStreamV1 {
   private int _fieldMarkerCursor;
   private int _fieldMarkerOffset;

   public DataStreamV1_0() {
      this(256);
   }

   public DataStreamV1_0(int bufferLength) {
      super(bufferLength);
      this.initDefaultFieldMarkers();
   }

   public DataStreamV1_0(byte[] buffer) {
      this(buffer, 0, buffer.length);
   }

   public DataStreamV1_0(byte[] buffer, int offset, int length) {
      super(buffer, offset, length);
      this.initDefaultFieldMarkers();
   }

   @Override
   public byte readByte(byte defaultValue) {
      return this.readFieldMarker() ? defaultValue : this.readByte();
   }

   @Override
   public boolean readBoolean(boolean defaultValue) {
      return this.readFieldMarker() ? defaultValue : this.readBoolean();
   }

   @Override
   public int readInt(int defaultValue) {
      return this.readFieldMarker() ? defaultValue : this.readCompressedInt();
   }

   @Override
   public long readLong(long defaultValue) {
      return this.readFieldMarker() ? defaultValue : this.readCompressedLong();
   }

   @Override
   public float readFloat(float defaultValue) {
      return this.readFieldMarker() ? defaultValue : Float.intBitsToFloat(this.readUncompressedInt());
   }

   @Override
   public double readDouble(double defaultValue) {
      return this.readFieldMarker() ? defaultValue : Double.longBitsToDouble(this.readUncompressedLong());
   }

   @Override
   public String readString(String defaultValue) {
      return this.readFieldMarker() ? defaultValue : this.readUTF();
   }

   @Override
   public byte[] readBlob() {
      int blobLength = this.readCompressedInt() - 1;
      if (blobLength < 0) {
         return null;
      }

      byte[] blob = new byte[blobLength];
      if (blobLength != 0) {
         this.ensureAvailable(super._cursor + blobLength);
         System.arraycopy(super._buffer, super._cursor, blob, 0, blobLength);
      }

      super._cursor += blobLength;
      return blob;
   }

   @Override
   public boolean[] readBooleanArray() {
      int arrayLength = this.readCompressedInt() - 1;
      if (arrayLength < 0) {
         return null;
      }

      boolean[] value = new boolean[arrayLength];

      for (int index = 0; index < arrayLength; index++) {
         value[index] = this.readBoolean();
      }

      return value;
   }

   @Override
   public int[] readIntArray() {
      int arrayLength = this.readCompressedInt() - 1;
      if (arrayLength < 0) {
         return null;
      }

      int[] value = new int[arrayLength];

      for (int index = 0; index < arrayLength; index++) {
         value[index] = this.readCompressedInt();
      }

      return value;
   }

   @Override
   public long[] readLongArray() {
      int arrayLength = this.readCompressedInt() - 1;
      if (arrayLength < 0) {
         return null;
      }

      long[] value = new long[arrayLength];

      for (int index = 0; index < arrayLength; index++) {
         value[index] = this.readCompressedLong();
      }

      return value;
   }

   @Override
   public float[] readFloatArray() {
      int arrayLength = this.readCompressedInt() - 1;
      if (arrayLength < 0) {
         return null;
      }

      float[] value = new float[arrayLength];

      for (int index = 0; index < arrayLength; index++) {
         value[index] = Float.intBitsToFloat(this.readUncompressedInt());
      }

      return value;
   }

   @Override
   public double[] readDoubleArray() {
      int arrayLength = this.readCompressedInt() - 1;
      if (arrayLength < 0) {
         return null;
      }

      double[] value = new double[arrayLength];

      for (int index = 0; index < arrayLength; index++) {
         value[index] = Double.longBitsToDouble(this.readUncompressedLong());
      }

      return value;
   }

   @Override
   public String[] readStringArray() {
      int arrayLength = this.readCompressedInt() - 1;
      if (arrayLength < 0) {
         return null;
      }

      String[] value = new Object[arrayLength];

      for (int index = 0; index < arrayLength; index++) {
         value[index] = this.readUTF();
      }

      return value;
   }

   @Override
   public boolean startComponentRead() {
      return !this.readFieldMarker();
   }

   @Override
   public int startComponentArrayRead() {
      return this.readCompressedInt() - 1;
   }

   @Override
   public void writeBlob(byte[] blob) {
      if (blob == null) {
         this.writeCompressedInt(0);
      } else {
         int blobLength = blob.length;
         this.writeCompressedInt(blobLength + 1);
         this.ensureBuffer(super._cursor + blobLength);
         System.arraycopy(blob, 0, super._buffer, super._cursor, blobLength);
         super._cursor += blobLength;
      }
   }

   @Override
   public void writeBooleanArray(boolean[] value) {
      if (value == null) {
         this.writeCompressedInt(0);
      } else {
         int arrayLength = value.length;
         this.writeCompressedInt(arrayLength + 1);

         for (int index = 0; index < arrayLength; index++) {
            this.writeBoolean(value[index]);
         }
      }
   }

   @Override
   public void writeIntArray(int[] value) {
      if (value == null) {
         this.writeCompressedInt(0);
      } else {
         int arrayLength = value.length;
         this.writeCompressedInt(arrayLength + 1);

         for (int index = 0; index < arrayLength; index++) {
            this.writeCompressedInt(value[index]);
         }
      }
   }

   @Override
   public void writeLongArray(long[] value) {
      if (value == null) {
         this.writeCompressedInt(0);
      } else {
         int arrayLength = value.length;
         this.writeCompressedInt(arrayLength + 1);

         for (int index = 0; index < arrayLength; index++) {
            this.writeCompressedLong(value[index]);
         }
      }
   }

   @Override
   public void writeFloatArray(float[] value) {
      if (value == null) {
         this.writeCompressedInt(0);
      } else {
         int arrayLength = value.length;
         this.writeCompressedInt(arrayLength + 1);

         for (int index = 0; index < arrayLength; index++) {
            this.writeUncompressedInt(Float.floatToIntBits(value[index]));
         }
      }
   }

   @Override
   public void writeDoubleArray(double[] value) {
      if (value == null) {
         this.writeCompressedInt(0);
      } else {
         int arrayLength = value.length;
         this.writeCompressedInt(arrayLength + 1);

         for (int index = 0; index < arrayLength; index++) {
            this.writeUncompressedLong(Double.doubleToLongBits(value[index]));
         }
      }
   }

   @Override
   public void writeStringArray(String[] value) {
      if (value == null) {
         this.writeCompressedInt(0);
      } else {
         int arrayLength = value.length;
         this.writeCompressedInt(arrayLength + 1);

         for (int index = 0; index < arrayLength; index++) {
            this.writeUTF(value[index]);
         }
      }
   }

   @Override
   public void noteDefaultField(boolean useDefaultValue) {
      this.writeFieldMarker(useDefaultValue);
   }

   @Override
   public void startComponentWrite(boolean isNull) {
      this.writeFieldMarker(isNull);
   }

   @Override
   public void startComponentArrayWrite(int length) {
      this.writeCompressedInt(length < 0 ? 0 : length + 1);
   }

   private boolean readFieldMarker() {
      if (this._fieldMarkerOffset == 128) {
         this.ensureAvailable(super._cursor + 1);
         this._fieldMarkerCursor = super._cursor++;
      }

      int isOn = super._buffer[this._fieldMarkerCursor] & this._fieldMarkerOffset;
      this._fieldMarkerOffset = this._fieldMarkerOffset >>> 1 & 0xFF;
      if (this._fieldMarkerOffset == 0) {
         this._fieldMarkerOffset |= 128;
      }

      return isOn != 0;
   }

   private void writeFieldMarker(boolean on) {
      if (this._fieldMarkerOffset == 128) {
         this.ensureBuffer(super._cursor + 1);
         this._fieldMarkerCursor = super._cursor;
         super._buffer[this._fieldMarkerCursor] = 0;
         super._cursor++;
      }

      if (on) {
         super._buffer[this._fieldMarkerCursor] = (byte)(super._buffer[this._fieldMarkerCursor] | this._fieldMarkerOffset);
      }

      this._fieldMarkerOffset = this._fieldMarkerOffset >>> 1 & 0xFF;
      if (this._fieldMarkerOffset == 0) {
         this._fieldMarkerOffset |= 128;
      }
   }

   @Override
   public void rewind() {
      super.rewind();
      this.initDefaultFieldMarkers();
   }

   private void initDefaultFieldMarkers() {
      this._fieldMarkerOffset = 128;
      this._fieldMarkerCursor = -1;
   }
}
