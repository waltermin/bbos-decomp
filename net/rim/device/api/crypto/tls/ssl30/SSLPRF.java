package net.rim.device.api.crypto.tls.ssl30;

import net.rim.device.api.crypto.AbstractPseudoRandomSource;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.crypto.PseudoRandomSource;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.util.Arrays;

final class SSLPRF extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private Digest _SHA1;
   private Digest _MD5 = new MD5Digest();
   private byte[] _buffer;
   private byte[] _SHAOutput;
   private byte[] _charPrefix;
   private int _bufferOffset;
   private int _available;
   private byte[] _serverRandom;
   private byte[] _clientRandom;
   private byte[] _secret;
   int _count;

   public SSLPRF(byte[] secret, byte[] serverRandom, byte[] clientRandom) {
      this._SHA1 = new SHA1Digest();
      this._buffer = new byte[16];
      this._SHAOutput = new byte[20];
      this._charPrefix = new byte[1];
      this._serverRandom = serverRandom;
      this._clientRandom = clientRandom;
      this._secret = Arrays.copy(secret);
   }

   @Override
   public final String getAlgorithm() {
      return "SSL_PRF";
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         while (length > 0) {
            if (this._available == 0) {
               this._SHA1.reset();
               this._charPrefix[0] = (byte)(65 + this._count++);

               for (int i = 0; i < this._count; i++) {
                  this._SHA1.update(this._charPrefix);
               }

               this._SHA1.update(this._secret);
               this._SHA1.update(this._serverRandom);
               this._SHA1.update(this._clientRandom);
               this._SHA1.getDigest(this._SHAOutput, 0);
               this._MD5.reset();
               this._MD5.update(this._secret);
               this._MD5.update(this._SHAOutput);
               this._available = this._MD5.getDigest(this._buffer, 0);
               this._bufferOffset = 0;
            }

            int copyLength = Math.min(length, this._available);
            length -= copyLength;
            this._available -= copyLength;

            while (--copyLength >= 0) {
               buffer[offset++] ^= this._buffer[this._bufferOffset++];
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int getAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final int getMaxAvailable() {
      return Integer.MAX_VALUE;
   }
}
