package net.rim.device.api.crypto;

final class NativeARC4PRNG {
   private int _i;
   private int _j;
   private byte[] _s;

   public NativeARC4PRNG(byte[] seed, int offset, int length) {
      if (seed != null && offset >= 0 && length > 0 && seed.length - length >= offset) {
         this._s = new byte[256];

         for (int i = 0; i < 256; i++) {
            this._s[i] = (byte)i;
         }

         int j = 0;

         for (int var7 = 0; var7 < 256; var7++) {
            j = seed[offset + var7 % length] + this._s[var7] + j & 0xFF;
            byte t = this._s[var7];
            this._s[var7] = this._s[j];
            this._s[j] = t;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final native void xorBytes(byte[] var1, int var2, int var3);
}
