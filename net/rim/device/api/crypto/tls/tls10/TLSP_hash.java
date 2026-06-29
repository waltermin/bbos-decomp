package net.rim.device.api.crypto.tls.tls10;

import net.rim.device.api.crypto.AbstractPseudoRandomSource;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.PseudoRandomSource;
import net.rim.device.api.util.Arrays;

public class TLSP_hash extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private HMAC _hmac;
   private byte[] _seed;
   private byte[] _updatedSeed;
   private byte[] _buffer;
   private int _bufferOffset;
   private int _available;

   public TLSP_hash(Digest digest, byte[] secret, int secretOffset, int secretLength, byte[] seed) {
      if (digest != null && secret != null && seed != null && secretOffset >= 0 && secretLength >= 0 && secret.length - secretLength >= secretOffset) {
         this._hmac = (HMAC)(new Object((HMACKey)(new Object(secret, secretOffset, secretLength)), digest));
         this._seed = Arrays.copy(seed);
         this._updatedSeed = new byte[this._hmac.getLength()];
         this._buffer = new byte[this._hmac.getLength() + seed.length];
         this._hmac.reset();
         this._hmac.update(this._seed);
         this._hmac.getMAC(this._updatedSeed, 0);
      } else {
         throw new Object();
      }
   }

   @Override
   public String getAlgorithm() {
      return "P_hash";
   }

   @Override
   public byte[] getBytes(int length) {
      byte[] output = new byte[length];
      this.getBytes(output);
      return output;
   }

   @Override
   public void getBytes(byte[] buffer) {
      this.getBytes(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   @Override
   public void getBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && length > 0 && offset >= 0 && buffer.length - length >= offset) {
         byte[] output = new byte[length];
         this.xorBytes(output);
         System.arraycopy(output, 0, buffer, offset, length);
      } else {
         throw new Object();
      }
   }

   @Override
   public void xorBytes(byte[] buffer) {
      this.xorBytes(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   @Override
   public void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && length > 0 && offset >= 0 && buffer.length - length >= offset) {
         while (length > 0) {
            if (this._available == 0) {
               System.arraycopy(this._updatedSeed, 0, this._buffer, 0, this._hmac.getLength());
               System.arraycopy(this._seed, 0, this._buffer, this._hmac.getLength(), this._seed.length);
               this._hmac.reset();
               this._hmac.update(this._buffer);
               this._available = this._hmac.getMAC(this._buffer, 0);
               this._bufferOffset = 0;
               this._hmac.reset();
               this._hmac.update(this._updatedSeed);
               this._hmac.getMAC(this._updatedSeed, 0);
            }

            int copyLength = Math.min(length, this._available);
            length -= copyLength;
            this._available -= copyLength;

            while (--copyLength >= 0) {
               buffer[offset++] ^= this._buffer[this._bufferOffset++];
            }
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public int getAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public int getMaxAvailable() {
      return Integer.MAX_VALUE;
   }
}
