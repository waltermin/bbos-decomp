package net.rim.device.api.crypto.tls;

import net.rim.device.api.util.Arrays;

public final class RandomStructure {
   private int _time;
   private byte[] _random;

   public RandomStructure(int gmt_unix_time, byte[] random_bytes) {
      this._time = gmt_unix_time;
      this._random = Arrays.copy(random_bytes);
   }

   public final int getTime() {
      return this._time;
   }

   public final byte[] getRandomBytes() {
      byte[] random = new byte[4 + this._random.length];
      random[0] = (byte)(this._time >> 24 & 0xFF);
      random[1] = (byte)(this._time >> 16 & 0xFF);
      random[2] = (byte)(this._time >> 8 & 0xFF);
      random[3] = (byte)(this._time & 0xFF);
      System.arraycopy(this._random, 0, random, 4, this._random.length);
      return random;
   }
}
