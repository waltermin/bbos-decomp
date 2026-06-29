package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;

public class AbstractMAC {
   byte[] buffer;

   protected AbstractMAC() {
   }

   public String getAlgorithm() {
      throw null;
   }

   public void reset() {
      throw null;
   }

   public void update(int data) {
      if (this.buffer == null) {
         this.buffer = new byte[1];
      }

      this.buffer[0] = (byte)data;
      this.update(this.buffer, 0, 1);
   }

   public void update(byte[] data) {
      if (data == null) {
         throw new IllegalArgumentException();
      }

      this.update(data, 0, data.length);
   }

   public void update(byte[] _1, int _2, int _3) {
      throw null;
   }

   public int getLength() {
      throw null;
   }

   public byte[] getMAC() {
      return this.getMAC(true);
   }

   public byte[] getMAC(boolean reset) {
      byte[] buffer = new byte[this.getLength()];
      this.getMAC(buffer, 0, reset);
      return buffer;
   }

   public int getMAC(byte[] buffer, int offset) {
      return this.getMAC(buffer, offset, true);
   }

   public int getMAC(byte[] _1, int _2, boolean _3) {
      throw null;
   }

   public boolean checkMAC(byte[] mac) {
      return this.checkMAC(mac, 0, true);
   }

   public boolean checkMAC(byte[] mac, boolean reset) {
      return this.checkMAC(mac, 0, reset);
   }

   public boolean checkMAC(byte[] mac, int offset) {
      return this.checkMAC(mac, offset, true);
   }

   public boolean checkMAC(byte[] mac, int offset, boolean reset) {
      int length = this.getLength();
      if (mac == null || offset < 0 || mac.length - length < offset) {
         throw new IllegalArgumentException();
      } else {
         return length > 0 ? Arrays.equals(mac, offset, this.getMAC(reset), 0, length) : true;
      }
   }
}
