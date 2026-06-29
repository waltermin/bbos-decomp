package net.rim.device.api.crypto.pgp;

import java.io.InputStream;
import java.util.Enumeration;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.io.SharedInputStream;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class PGPArmorDecoder extends InputStream {
   private PGPCertificate[] _certificates = new PGPCertificate[0];
   private PGPPrivateKey[] _privateKeys = new PGPPrivateKey[0];
   private PGPInternalArmorDecoder[] _decoders = new PGPInternalArmorDecoder[0];
   private int _currentDecoder;

   public PGPArmorDecoder(InputStream input) {
      this(input, null, true);
   }

   public PGPArmorDecoder(InputStream input, KeyStore keystore) {
      this(input, keystore, true);
   }

   public PGPArmorDecoder(InputStream input, KeyStore keyStore, boolean displayUI) {
      SharedInputStream sharedInput = SharedInputStream.getSharedInputStream(input);

      while (true) {
         try {
            PGPInternalArmorDecoder internalDecoder = new PGPInternalArmorDecoder(sharedInput, keyStore, displayUI);
            if (internalDecoder.wereProcessingErrorsFound()) {
               Arrays.add(this._decoders, internalDecoder);
               break;
            }

            int numNewKeys = internalDecoder.numCertificates();
            if (numNewKeys > 0) {
               int numExistingKeys = this._certificates.length;
               Array.resize(this._certificates, numExistingKeys + numNewKeys);
               Array.resize(this._privateKeys, numExistingKeys + numNewKeys);

               for (int i = 0; i < numNewKeys; i++) {
                  this._certificates[numExistingKeys] = internalDecoder.getCertificate(i);
                  this._privateKeys[numExistingKeys] = internalDecoder.getPrivateKey(i);
                  numExistingKeys++;
               }
            }

            Arrays.add(this._decoders, internalDecoder);
         } catch (PGPIncompleteKeyException var11) {
            break;
         } finally {
            break;
         }
      }

      this.performRelatedKeyCheck();
   }

   @Override
   public final int read() {
      if (this._currentDecoder >= this._decoders.length) {
         return -1;
      } else {
         int readValue = this._decoders[this._currentDecoder].read();
         if (readValue < 0) {
            this._currentDecoder++;
            return this.read();
         } else {
            return readValue;
         }
      }
   }

   @Override
   public final int read(byte[] buffer) {
      return this.read(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   @Override
   public final int read(byte[] buffer, int offset, int length) {
      if (this._currentDecoder >= this._decoders.length) {
         return -1;
      } else {
         int numBytesRead = this._decoders[this._currentDecoder].read(buffer, offset, length);
         if (numBytesRead < 0) {
            this._currentDecoder++;
            return this.read(buffer, offset, length);
         } else {
            return numBytesRead;
         }
      }
   }

   @Override
   public final int available() {
      int numAvailable = 0;
      int numDecoders = this._decoders.length;

      for (int i = this._currentDecoder; i < numDecoders; i++) {
         numAvailable += this._decoders[i].available();
      }

      return numAvailable;
   }

   @Override
   public final long skip(long n) {
      if (this._currentDecoder >= this._decoders.length) {
         return 0;
      } else {
         long numSkipped = this._decoders[this._currentDecoder].skip(n);
         if (numSkipped == 0) {
            this._currentDecoder++;
            return this.skip(n);
         } else {
            return numSkipped;
         }
      }
   }

   @Override
   public final void close() {
      int numDecoders = this._decoders.length;

      for (int i = 0; i < numDecoders; i++) {
         this._decoders[i].close();
      }
   }

   public final int numCertificates() {
      return this._certificates.length;
   }

   public final PGPCertificate getCertificate(int index) {
      this.validateIndex(this._certificates, index);
      return this._certificates[index];
   }

   public final String getCertificateLabel(int index) {
      return this.getCertificate(index).getSubjectFriendlyName();
   }

   public final boolean isPrivateKey(int index) {
      return this.getPrivateKey(index) != null;
   }

   public final PrivateKey getPrivateKey(int index) {
      this.validateIndex(this._privateKeys, index);
      return this._privateKeys[index];
   }

   public final InputStream getNextStream() {
      while (this._currentDecoder < this._decoders.length) {
         InputStream inputStream = this._decoders[this._currentDecoder++].getInnerStream();
         if (inputStream != null) {
            return inputStream;
         }
      }

      return null;
   }

   public final boolean isCheckSumPresent() {
      int numDecoders = this._decoders.length;

      for (int i = 0; i < numDecoders; i++) {
         if (!this._decoders[i].isCheckSumPresent()) {
            return false;
         }
      }

      return true;
   }

   public final boolean isCheckSumPresent(int index) {
      this.validateIndex(this._decoders, index);
      return this._decoders[index].isCheckSumPresent();
   }

   public final boolean isCheckSumValid() {
      int length = this._decoders.length;

      for (int i = 0; i < length; i++) {
         if (!this._decoders[i].isCheckSumValid()) {
            return false;
         }
      }

      return true;
   }

   public final boolean isCheckSumValid(int index) {
      this.validateIndex(this._decoders, index);
      return this._decoders[index].isCheckSumValid();
   }

   public final boolean hasBase64Data() {
      int numDecoders = this._decoders.length;

      for (int i = 0; i < numDecoders; i++) {
         if (!this._decoders[i].hasBase64Data()) {
            return false;
         }
      }

      return true;
   }

   public final boolean hasBase64Data(int index) {
      this.validateIndex(this._decoders, index);
      return this._decoders[index].hasBase64Data();
   }

   public final Enumeration getHeaderValues(int index, String key) {
      this.validateIndex(this._decoders, index);
      return this._decoders[index].getValues(key);
   }

   public final Enumeration getHeaderValues(int index) {
      this.validateIndex(this._decoders, index);
      return this._decoders[index].getValues();
   }

   private final void validateIndex(Object[] array, int index) {
      if (index < 0 || index >= array.length) {
         throw new Object();
      }
   }

   private final void performRelatedKeyCheck() {
      int numPrivateKeys = this._privateKeys.length;
      int numCertificates = this._certificates.length;
      if (numPrivateKeys != numCertificates) {
         throw new Object();
      }

      int numDuplicateCertificates = 0;

      for (int i = 0; i < numCertificates; i++) {
         for (int j = i + 1; j < numCertificates; j++) {
            if (this._certificates[i] != null
               && this._certificates[j] != null
               && Arrays.equals(this._certificates[i].getFingerprint(), this._certificates[j].getFingerprint())
               && Arrays.equals(this._certificates[i].getUserIDs(), this._certificates[j].getUserIDs())) {
               if (this._privateKeys[i] == null) {
                  this._privateKeys[i] = this._privateKeys[j];
               } else if (this._privateKeys[j] == null) {
                  this._certificates[i] = this._certificates[j];
               }

               this._certificates[j] = null;
               this._privateKeys[j] = null;
               numDuplicateCertificates++;
            }
         }
      }

      int numNewCertificates = numCertificates - numDuplicateCertificates;
      PGPCertificate[] newCertificates = new PGPCertificate[numNewCertificates];
      PGPPrivateKey[] newPrivateKeys = new PGPPrivateKey[numNewCertificates];
      int newIndex = 0;

      for (int oldIndex = 0; oldIndex < numCertificates; oldIndex++) {
         if (this._certificates[oldIndex] != null) {
            newCertificates[newIndex] = this._certificates[oldIndex];
            newPrivateKeys[newIndex] = this._privateKeys[oldIndex];
            newIndex++;
         }
      }

      this._certificates = newCertificates;
      this._privateKeys = newPrivateKeys;
   }
}
