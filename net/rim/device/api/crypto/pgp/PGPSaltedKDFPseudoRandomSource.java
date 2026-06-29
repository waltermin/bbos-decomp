package net.rim.device.api.crypto.pgp;

import net.rim.device.api.crypto.AbstractPseudoRandomSource;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.util.Arrays;

public final class PGPSaltedKDFPseudoRandomSource extends AbstractPseudoRandomSource implements PGPPseudoRandomSource {
   private PGPSimpleKDFPseudoRandomSource _kdf;
   private byte[] _salt;
   private Digest _digest;
   private static final int DEFAULT_SALT_LENGTH;

   public final byte[] getSalt() {
      return Arrays.copy(this._salt);
   }

   @Override
   public final int getAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final int getMaxAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final Digest getDigest() {
      return this._digest;
   }

   @Override
   public final String getAlgorithm() {
      return "PGPSaltedKDF";
   }

   public PGPSaltedKDFPseudoRandomSource(byte[] passPhrase, byte[] salt) {
      this(passPhrase, salt == null ? RandomSource.getBytes(8) : salt, (Digest)(new Object()));
   }

   public PGPSaltedKDFPseudoRandomSource(byte[] passPhrase, byte[] salt, Digest digest) {
      this(passPhrase, 0, passPhrase == null ? 0 : passPhrase.length, salt, 0, salt == null ? 8 : salt.length, digest);
   }

   public PGPSaltedKDFPseudoRandomSource(byte[] passPhrase, int offset, int length, byte[] salt, int saltOffset, int saltLength, Digest digest) {
      if ((passPhrase != null || offset == 0 || length == 0)
         && offset >= 0
         && length >= 0
         && (passPhrase == null || passPhrase.length - length >= offset)
         && saltOffset >= 0
         && saltLength >= 0
         && (salt == null || salt.length - saltLength >= saltOffset && saltLength == 8)) {
         if (salt == null) {
            salt = RandomSource.getBytes(8);
            saltOffset = 0;
            saltLength = 8;
         }

         this._digest = digest;
         byte[] hashPhrase = new byte[length + saltLength];
         System.arraycopy(salt, saltOffset, hashPhrase, 0, saltLength);
         System.arraycopy(passPhrase, offset, hashPhrase, saltLength, length);
         this._kdf = new PGPSimpleKDFPseudoRandomSource(hashPhrase, digest);
         this._salt = salt;
      } else {
         throw new Object();
      }
   }

   public PGPSaltedKDFPseudoRandomSource(byte[] passPhrase) {
      this(passPhrase, RandomSource.getBytes(8));
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         this._kdf.xorBytes(buffer, offset, length);
      } else {
         throw new Object();
      }
   }
}
