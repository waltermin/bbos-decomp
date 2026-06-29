package net.rim.wica.runtime.util;

import net.rim.device.api.collection.util.BigIntVector;
import net.rim.device.api.collection.util.BigLongVector;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

public final class SerializerUtil {
   private static final short BYTES_IN_BYTE;
   private static final short BYTES_IN_BOOLEAN;
   private static final short BYTES_IN_LONG;
   private static final short BYTES_IN_DOUBLE;

   public static final void writeString(DataBuffer buffer, byte type, String s) {
      if (s != null) {
         ConverterUtilities.writeString(buffer, type, s);
      }
   }

   public static final void writeBoolean(DataBuffer buffer, byte type, boolean b) {
      buffer.writeShort(1);
      buffer.writeByte(type);
      buffer.writeBoolean(b);
   }

   public static final void writeByte(DataBuffer buffer, byte type, byte b) {
      buffer.writeShort(1);
      buffer.writeByte(type);
      buffer.writeByte(b);
   }

   public static final void writeByteArray(DataBuffer buffer, byte type, byte[] array) {
      if (array != null) {
         ConverterUtilities.writeByteArray(buffer, type, array);
      }
   }

   public static final void writeIntArray(DataBuffer buffer, byte type, int[] array) {
      if (array != null) {
         ConverterUtilities.writeIntArray(buffer, type, array);
      }
   }

   public static final void writeLongArray(DataBuffer buffer, byte type, long[] array) {
      if (array != null) {
         buffer.writeShort(8 * array.length);
         buffer.writeByte(type);

         for (int i = 0; i < array.length; i++) {
            buffer.writeLong(array[i]);
         }
      }
   }

   public static final void writeDoubleArray(DataBuffer buffer, byte type, double[] array) {
      if (array != null) {
         buffer.writeShort(8 * array.length);
         buffer.writeByte(type);

         for (int i = 0; i < array.length; i++) {
            buffer.writeDouble(array[i]);
         }
      }
   }

   public static final void writeBooleanArray(DataBuffer buffer, byte type, boolean[] array) {
      if (array != null) {
         buffer.writeShort(1 * array.length);
         buffer.writeByte(type);

         for (int i = 0; i < array.length; i++) {
            buffer.writeBoolean(array[i]);
         }
      }
   }

   public static final void writeBigIntVector(DataBuffer buffer, byte type, BigIntVector vector) {
      if (vector != null) {
         int[] array = new int[vector.size()];
         vector.copyInto(0, array.length, array, 0);
         ConverterUtilities.writeIntArray(buffer, type, array);
      }
   }

   public static final void writeBigLongVector(DataBuffer buffer, byte type, BigLongVector vector) {
      if (vector != null) {
         long[] array = new long[vector.size()];
         vector.copyInto(0, array.length, array, 0);
         writeLongArray(buffer, type, array);
      }
   }

   public static final boolean readBoolean(DataBuffer buffer) {
      consumeLengthType(buffer);
      return buffer.readBoolean();
   }

   public static final byte readByte(DataBuffer buffer) {
      consumeLengthType(buffer);
      return buffer.readByte();
   }

   public static final long[] readLongArray(DataBuffer buffer) {
      int size = buffer.readShort() / 8;
      buffer.readByte();
      long[] array = new long[size];

      for (int i = 0; i < array.length; i++) {
         array[i] = buffer.readLong();
      }

      return array;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final double[] readDoubleArray(DataBuffer buffer) {
      int size = buffer.readShort() / 8;
      buffer.readByte();
      double[] array = new double[size];

      for (int i = 0; i < array.length; i++) {
         try {
            array[i] = buffer.readDouble();
         } catch (Throwable var6) {
            e.printStackTrace();
            continue;
         }
      }

      return array;
   }

   public static final boolean[] readBooleanArray(DataBuffer buffer) {
      int size = buffer.readShort() / 1;
      buffer.readByte();
      boolean[] array = new boolean[size];

      for (int i = 0; i < array.length; i++) {
         array[i] = buffer.readBoolean();
      }

      return array;
   }

   public static final BigIntVector readBigIntVector(DataBuffer buffer) {
      int[] array = ConverterUtilities.readIntArray(buffer);
      BigIntVector vector = null;
      if (array != null) {
         vector = (BigIntVector)(new Object(array.length));
         vector.addElements(array);
      }

      return vector;
   }

   public static final BigLongVector readBigLongVector(DataBuffer buffer) {
      long[] array = readLongArray(buffer);
      BigLongVector vector = null;
      if (array != null) {
         vector = (BigLongVector)(new Object(array.length));
         vector.addElements(array);
      }

      return vector;
   }

   private static final void consumeLengthType(DataBuffer buffer) {
      buffer.readShort();
      buffer.readByte();
   }
}
