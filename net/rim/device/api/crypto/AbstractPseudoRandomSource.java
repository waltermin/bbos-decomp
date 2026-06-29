package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;

public class AbstractPseudoRandomSource {
   protected AbstractPseudoRandomSource() {
   }

   public void xorBytes(byte[] _1, int _2, int _3) {
      throw null;
   }

   public void xorBytes(byte[] buffer) {
      if (buffer == null) {
         throw new IllegalArgumentException();
      }

      this.xorBytes(buffer, 0, buffer.length);
   }

   public void xorBytes(byte[] data, int dataOffset, int dataLength, byte[] buffer, int bufferOffset) {
      if (data != null
         && dataOffset >= 0
         && dataLength >= 0
         && data.length - dataLength >= dataOffset
         && buffer != null
         && bufferOffset >= 0
         && buffer.length - dataLength >= bufferOffset) {
         System.arraycopy(data, dataOffset, buffer, bufferOffset, dataLength);
         this.xorBytes(buffer, bufferOffset, dataLength);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public byte[] xorCopy(byte[] data) {
      if (data == null) {
         throw new IllegalArgumentException();
      } else {
         return this.xorCopy(data, 0, data.length);
      }
   }

   public byte[] xorCopy(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         byte[] buffer = new byte[length];
         this.xorBytes(data, offset, length, buffer, 0);
         return buffer;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void getBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         Arrays.fill(buffer, (byte)0, offset, length);
         this.xorBytes(buffer, offset, length);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public byte[] getBytes(int length) {
      if (length < 0) {
         throw new IllegalArgumentException();
      }

      byte[] buffer = new byte[length];
      this.xorBytes(buffer, 0, length);
      return buffer;
   }

   public void getBytes(byte[] buffer) {
      if (buffer == null) {
         throw new IllegalArgumentException();
      }

      this.getBytes(buffer, 0, buffer.length);
   }
}
