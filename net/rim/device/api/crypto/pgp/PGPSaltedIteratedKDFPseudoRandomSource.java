package net.rim.device.api.crypto.pgp;

import net.rim.device.api.crypto.AbstractPseudoRandomSource;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.util.Arrays;

public final class PGPSaltedIteratedKDFPseudoRandomSource extends AbstractPseudoRandomSource implements PGPPseudoRandomSource {
   byte _codedCount;
   int _decodedCount;
   byte[] _passphrase;
   byte[] _salt;
   Digest _digest;
   int _digestLength;
   private static final int DEFAULT_SALT_LENGTH;
   private static final int EXPBIAS;
   private static final int CONCATENATED_HASH_LENGTH;

   public final byte getCodedCount() {
      return this._codedCount;
   }

   public final byte[] getSalt() {
      return Arrays.copy(this._salt);
   }

   @Override
   public final int getMaxAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final int getAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final String getAlgorithm() {
      return "PGPSaltedIteratedKDF";
   }

   @Override
   public final Digest getDigest() {
      return this._digest;
   }

   public PGPSaltedIteratedKDFPseudoRandomSource(byte codedCount, byte[] passphrase, byte[] salt, Digest digest) {
      this(codedCount, passphrase, 0, passphrase == null ? 0 : passphrase.length, salt, 0, salt == null ? 0 : salt.length, digest);
   }

   public PGPSaltedIteratedKDFPseudoRandomSource(byte codedCount, byte[] passphrase, byte[] salt) {
      this(codedCount, passphrase, salt, (Digest)(new Object()));
   }

   public PGPSaltedIteratedKDFPseudoRandomSource(byte codedCount, byte[] passphrase) {
      this(codedCount, passphrase, null);
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         int numInstances = (length + this._digestLength - 1) / this._digestLength;
         byte[] preloadZeros = new byte[numInstances - 1];
         byte[] finalOutput = new byte[numInstances * this._digestLength];
         int hashPacketLength = this._salt.length + this._passphrase.length;
         int numHashPacketsToConcatenate = (1024 + hashPacketLength - 1) / hashPacketLength;
         int concatenatedHashPacketLength = hashPacketLength * numHashPacketsToConcatenate;
         byte[] concatenatedHashPacket = new byte[concatenatedHashPacketLength];
         System.arraycopy(this._salt, 0, concatenatedHashPacket, 0, this._salt.length);
         System.arraycopy(this._passphrase, 0, concatenatedHashPacket, this._salt.length, this._passphrase.length);

         for (int i = 1; i < numHashPacketsToConcatenate; i++) {
            System.arraycopy(concatenatedHashPacket, 0, concatenatedHashPacket, i * hashPacketLength, hashPacketLength);
         }

         int numFullHashUpdates = this._decodedCount / concatenatedHashPacketLength;
         int numBytesLeftOver = this._decodedCount - numFullHashUpdates * concatenatedHashPacketLength;

         for (int i = 0; i < numInstances; i++) {
            this._digest.update(preloadZeros, 0, i);

            for (int j = 0; j < numFullHashUpdates; j++) {
               this._digest.update(concatenatedHashPacket, 0, concatenatedHashPacketLength);
            }

            this._digest.update(concatenatedHashPacket, 0, numBytesLeftOver);
            this._digest.getDigest(finalOutput, i * this._digestLength);
            this._digest.reset();
         }

         int endOffset = offset + length;

         for (int i = offset; i < endOffset; i++) {
            buffer[i] ^= finalOutput[i];
         }
      } else {
         throw new Object();
      }
   }

   public PGPSaltedIteratedKDFPseudoRandomSource(
      byte codedCount, byte[] passphrase, int passphraseOffset, int passphraseLength, byte[] salt, int saltOffset, int saltLength, Digest digest
   ) {
      if (passphrase != null
         && passphraseOffset >= 0
         && passphraseLength >= 0
         && passphrase.length - passphraseLength >= passphraseOffset
         && saltOffset >= 0
         && saltLength >= 0
         && (salt == null || salt.length - saltLength >= saltOffset && saltLength == 8)) {
         int decodedCountBase = 16 + (codedCount & 15);
         int decidedCountShift = (codedCount >> 4 & 15) + 6;
         this._decodedCount = decodedCountBase << decidedCountShift;
         if (this._decodedCount < 0) {
            throw new Object();
         }

         this._codedCount = codedCount;
         this._passphrase = new byte[passphraseLength];
         System.arraycopy(passphrase, passphraseOffset, this._passphrase, 0, passphraseLength);
         if (salt == null) {
            salt = RandomSource.getBytes(8);
            saltOffset = 0;
            saltLength = 8;
         }

         this._salt = new byte[saltLength];
         System.arraycopy(salt, saltOffset, this._salt, 0, saltLength);
         this._digest = (Digest)(digest == null ? new Object() : digest);
         this._digestLength = this._digest.getDigestLength();
      } else {
         throw new Object();
      }
   }
}
