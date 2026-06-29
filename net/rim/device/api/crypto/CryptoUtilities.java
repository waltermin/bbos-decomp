package net.rim.device.api.crypto;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;
import net.rim.vm.Memory;

public final class CryptoUtilities {
   private static final boolean DEBUG = false;

   private CryptoUtilities() {
   }

   public static final byte[] copyKey(byte[] key, int keyLength) {
      if (key == null) {
         throw new Object();
      }

      int dataLength = key.length;
      if (dataLength > keyLength) {
         throw new Object();
      }

      byte[] copy = new byte[keyLength];
      System.arraycopy(key, 0, copy, keyLength - dataLength, dataLength);
      return copy;
   }

   public static final int readIntegerLittleEndian(InputStream input) {
      int integer = input.read() & 0xFF;
      integer |= (input.read() & 0xFF) << 8;
      integer |= (input.read() & 0xFF) << 16;
      return integer | (input.read() & 0xFF) << 24;
   }

   public static final void writeIntegerLittleEndian(ByteArrayOutputStream output, int integer) {
      output.write(integer);
      output.write(integer >> 8);
      output.write(integer >> 16);
      output.write(integer >> 24);
   }

   public static final byte[] readInteger4BigEndianAsByteArray(InputStream input) {
      byte[] result = new byte[4];
      result[3] = (byte)(input.read() & 0xFF);
      result[2] = (byte)(input.read() & 0xFF);
      result[1] = (byte)(input.read() & 0xFF);
      result[0] = (byte)(input.read() & 0xFF);
      return result;
   }

   public static final void writeByteArray4BigEndian(ByteArrayOutputStream output, byte[] data) {
      if (data.length > 4) {
         throw new Object();
      }

      if (data.length < 4) {
         int oldLength = data.length;
         Array.resize(data, 4);

         for (int i = oldLength; i < 4; i++) {
            data[i] = 0;
         }
      }

      output.write(data[3]);
      output.write(data[2]);
      output.write(data[1]);
      output.write(data[0]);
   }

   public static final byte[] readByteArrayFromInputStream(InputStream input, int numBytes) throws InvalidKeyEncodingException {
      byte[] result = new byte[numBytes];
      if (numBytes != 0) {
         int len = input.read(result);
         if (len != numBytes) {
            throw new InvalidKeyEncodingException();
         }
      }

      return result;
   }

   public static final void verifyKeyBytes(InputStream input, int a, int b, int c, int d) throws InvalidKeyEncodingException {
      if (input.read() != a || input.read() != b || input.read() != c || input.read() != d) {
         throw new InvalidKeyEncodingException();
      }
   }

   public static final void writeBytes(ByteArrayOutputStream output, int a, int b) {
      output.write(a);
      output.write(b);
   }

   public static final void writeBytes(ByteArrayOutputStream output, int a, int b, int c, int d) {
      writeBytes(output, a, b);
      writeBytes(output, c, d);
   }

   public static final byte[] flipArray(byte[] array) {
      int length = array.length;
      int middle = length >> 1;
      int i = 0;

      for (int j = length - 1; i < middle; j--) {
         byte temp = array[i];
         array[i] = array[j];
         array[j] = temp;
         i++;
      }

      return array;
   }

   public static final byte[] getImmutableOrCopiedByteArray(byte[] arrayToCopy) {
      if (arrayToCopy == null) {
         return null;
      } else {
         return Memory.isObjectInGroup(arrayToCopy) ? arrayToCopy : Arrays.copy(arrayToCopy);
      }
   }
}
