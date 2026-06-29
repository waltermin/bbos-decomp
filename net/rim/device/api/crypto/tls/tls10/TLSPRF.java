package net.rim.device.api.crypto.tls.tls10;

import net.rim.device.api.crypto.AbstractPseudoRandomSource;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.crypto.PseudoRandomSource;
import net.rim.device.api.crypto.SHA1Digest;

public final class TLSPRF extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private TLSP_hash P_MD5;
   private TLSP_hash P_SHA1;

   public TLSPRF(byte[] secret, byte[] label, byte[] seed) {
      int secretLength = secret.length >> 1;
      if ((secret.length & 1) == 1) {
         secretLength++;
      }

      byte[] labelSeed = new byte[label.length + seed.length];
      System.arraycopy(label, 0, labelSeed, 0, label.length);
      System.arraycopy(seed, 0, labelSeed, label.length, seed.length);
      this.P_MD5 = new TLSP_hash(new MD5Digest(), secret, 0, secretLength, labelSeed);
      this.P_SHA1 = new TLSP_hash(new SHA1Digest(), secret, secret.length - secretLength, secretLength, labelSeed);
   }

   @Override
   public final String getAlgorithm() {
      return "TLS_PRF";
   }

   @Override
   public final byte[] getBytes(int length) {
      byte[] output = new byte[length];
      this.xorBytes(output);
      return output;
   }

   @Override
   public final void getBytes(byte[] buffer) {
      this.getBytes(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   @Override
   public final void getBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && length > 0 && offset >= 0 && buffer.length - length >= offset) {
         byte[] output = new byte[length];
         this.xorBytes(output);
         System.arraycopy(output, 0, buffer, offset, length);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void xorBytes(byte[] buffer) {
      this.xorBytes(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         this.P_MD5.xorBytes(buffer, offset, length);
         this.P_SHA1.xorBytes(buffer, offset, length);
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
