package net.rim.device.api.crypto.pgp;

import java.io.OutputStream;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.io.SharedOutputStream;
import net.rim.device.internal.crypto.pgp.PGPUtilities;

public final class PGPCompressedOutputStream extends PGPOutputStream {
   private OutputStream _headerStream = this._out.getOutputStream();
   private OutputStream _internalStream;
   private PGPLengthCounterOutputStream _pgpLengthCounter;
   private SharedOutputStream _sharedInternal;
   private int _compressionAlgorithm;
   private int _compressedLengthWritten;
   private static final int LENGTH_OF_ALGORITHM_SPECIFIER = 1;

   public PGPCompressedOutputStream(OutputStream output) {
      this(output, 1, 4);
   }

   public PGPCompressedOutputStream(OutputStream output, int algorithm, int tagFormat) {
      super(output, tagFormat);
      switch (algorithm) {
         case -1:
            throw new Object();
         case 0:
         default:
            this._pgpLengthCounter = new PGPLengthCounterOutputStream(super._out.getOutputStream());
            this._internalStream = this._pgpLengthCounter;
            break;
         case 1:
            this._pgpLengthCounter = new PGPLengthCounterOutputStream(super._out.getOutputStream());
            this._internalStream = (OutputStream)(new Object(this._pgpLengthCounter, true, 13));
            break;
         case 2:
            this._pgpLengthCounter = new PGPLengthCounterOutputStream(super._out.getOutputStream());
            this._internalStream = (OutputStream)(new Object(this._pgpLengthCounter, false, 13));
      }

      this._sharedInternal = (SharedOutputStream)(new Object(this._internalStream));
      this._compressionAlgorithm = algorithm;
      this._compressedLengthWritten = 0;
   }

   @Override
   public final void write(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         this._internalStream.write(data, offset, length);
         this._compressedLengthWritten += length;
      } else {
         throw new Object();
      }
   }

   @Override
   final void update(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         if (super._pgpOut != null) {
            super._pgpOut.update(data, offset, length);
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final void close() {
      this._sharedInternal.close();
      if (this._compressionAlgorithm == 0 || this._compressionAlgorithm == 2 || this._compressionAlgorithm == 1) {
         this._compressedLengthWritten = this._pgpLengthCounter.getAmountWritten();
      }

      PGPUtilities.writeTagAndLength(this._headerStream, 8, this._compressedLengthWritten + 1, super._tagFormat);
      this._headerStream.write(this._compressionAlgorithm);
      this._headerStream.close();
      super._out.close();
   }

   public final PGPCompressedOutputStream getPGPCompressedOutputStream(int algorithm, int tagFormat) {
      return new PGPCompressedOutputStream(this._sharedInternal.getOutputStream(), algorithm, tagFormat);
   }

   public final PGPEncryptedOutputStream getPGPEncryptedOutputStream(int symmetricAlgorithm, int tagFormat) {
      return new PGPEncryptedOutputStream(this._sharedInternal.getOutputStream(), symmetricAlgorithm, tagFormat);
   }

   public final PGPEncryptedOutputStream getPGPEncryptedOutputStream(int symmetricAlgorithm, PGPPseudoRandomSource source, int tagFormat) {
      return new PGPEncryptedOutputStream(this._sharedInternal.getOutputStream(), symmetricAlgorithm, source, tagFormat);
   }

   public final PGPLiteralOutputStream getPGPLiteralOutputStream(int type, long time, String filename, int tagFormat) {
      return new PGPLiteralOutputStream(this._sharedInternal.getOutputStream(), type, time, filename, tagFormat);
   }

   public final PGPSignedOutputStream getPGPSignedOutputStream(int signatureType, PrivateKey privateKey, byte[] keyID, Digest digest, int tagFormat) {
      return new PGPSignedOutputStream(this._sharedInternal.getOutputStream(), signatureType, privateKey, keyID, digest, tagFormat);
   }
}
