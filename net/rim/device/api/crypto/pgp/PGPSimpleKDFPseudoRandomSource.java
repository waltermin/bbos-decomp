package net.rim.device.api.crypto.pgp;

import net.rim.device.api.crypto.AbstractPseudoRandomSource;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.SHA1Digest;

public final class PGPSimpleKDFPseudoRandomSource extends AbstractPseudoRandomSource implements PGPPseudoRandomSource {
   byte[] _passPhrase;
   Digest _digest;
   int _digestLength;

   public PGPSimpleKDFPseudoRandomSource(byte[] passPhrase) {
      this(passPhrase, null);
   }

   public PGPSimpleKDFPseudoRandomSource(byte[] passPhrase, Digest digest) {
      this(passPhrase, 0, passPhrase == null ? 0 : passPhrase.length, digest);
   }

   public PGPSimpleKDFPseudoRandomSource(byte[] passPhrase, int offset, int length, Digest digest) {
      if (passPhrase != null && offset >= 0 && length >= 0 && passPhrase.length - length >= offset) {
         this._passPhrase = new byte[length];
         System.arraycopy(passPhrase, offset, this._passPhrase, 0, length);
         this._digest = digest == null ? new SHA1Digest() : digest;
         this._digestLength = this._digest.getDigestLength();
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "PGPSimpleKDF";
   }

   @Override
   public final Digest getDigest() {
      return this._digest;
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
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         byte[] passPhraseHash;
         if (length > this._digestLength) {
            int numberOfRepetitions = length % this._digestLength == 0 ? length / this._digestLength : length / this._digestLength + 1;
            passPhraseHash = new byte[numberOfRepetitions * this._digestLength];

            for (int zeroCount = 0; zeroCount < numberOfRepetitions; zeroCount++) {
               if (zeroCount > 0) {
                  this._digest.update(new byte[zeroCount]);
               }

               this._digest.update(this._passPhrase);
               System.arraycopy(this._digest.getDigest(), 0, passPhraseHash, this._digestLength * zeroCount, this._digestLength);
               this._digest.reset();
            }
         } else {
            this._digest.update(this._passPhrase);
            passPhraseHash = this._digest.getDigest();
            this._digest.reset();
         }

         for (int i = 0; i < length; i++) {
            buffer[offset + i] = (byte)(buffer[offset + i] ^ passPhraseHash[i]);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
