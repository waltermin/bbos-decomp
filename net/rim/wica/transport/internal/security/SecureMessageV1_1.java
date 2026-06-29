package net.rim.wica.transport.internal.security;

import net.rim.wica.transport.VersionProvider;
import net.rim.wica.transport.VersionProviderException;
import net.rim.wica.transport.security.Key;
import net.rim.wica.transport.security.KeyProvider;
import net.rim.wica.transport.security.KeyProviderException;
import net.rim.wica.transport.security.SecureMessageException;
import net.rim.wica.transport.security.SecureMessageV1;
import net.rim.wica.transport.security.SecurityAlgorithm;
import net.rim.wica.transport.security.SecurityProvider;
import net.rim.wica.transport.security.SecurityProviderException;
import net.rim.wica.transport.security.SequenceProvider;
import net.rim.wica.transport.security.SequenceProviderException;
import net.rim.wica.transport.util.CompressedBuffer;
import net.rim.wica.transport.util.DataException;

public class SecureMessageV1_1 implements SecureMessageV1 {
   private KeyProvider _keyProvider;
   private SecurityProvider _securityProvider;
   private SequenceProvider _sequenceProvider;
   private VersionProvider _versionProvider;
   private byte[] _payload;
   private long _senderId;
   private long _receiverId;
   private byte[] _secureMsg;
   private static final int _HMAC_SHA1_LENGTH;
   private static final int _IV_LENGTH;

   public SecureMessageV1_1(
      byte[] secureMsg, KeyProvider keyProvider, SecurityProvider securityProvider, SequenceProvider sequenceProvider, VersionProvider versionProvider
   ) {
      this._secureMsg = secureMsg;
      this._keyProvider = keyProvider;
      this._securityProvider = securityProvider;
      this._sequenceProvider = sequenceProvider;
      this._versionProvider = versionProvider;
   }

   public SecureMessageV1_1(
      byte[] payload, long senderId, long receiverId, KeyProvider keyProvider, SecurityProvider securityProvider, SequenceProvider sequenceProvider
   ) {
      this._payload = payload;
      this._senderId = senderId;
      this._receiverId = receiverId;
      this._keyProvider = keyProvider;
      this._securityProvider = securityProvider;
      this._sequenceProvider = sequenceProvider;
   }

   private int getVersion() {
      return 2;
   }

   @Override
   public void secure(int mode) {
      if (this._payload != null) {
         int sequenceId;
         try {
            sequenceId = this._sequenceProvider.next(this._receiverId);
         } catch (SequenceProviderException e) {
            throw new SecureMessageException(202, e);
         }

         CompressedBuffer buffer = new CompressedBuffer(
            this.getRequiredHeaderCapacity(this._senderId, sequenceId) + this.getRequiredCapacity(this._payload) + this.getRequiredSignatureCapacity()
         );
         if (mode != 1) {
            mode = 0;
         }

         buffer.writeByte((byte)this.getVersion());
         buffer.writeLong(this._senderId);
         buffer.writeInt(sequenceId);
         buffer.writeInt(mode);
         if (mode == 0) {
            this.sign(buffer);
         } else {
            this.signAndEncrypt(buffer);
         }

         this._payload = null;
      }
   }

   private void sign(CompressedBuffer buffer) {
      buffer.writeInt(this._payload.length);
      buffer.copy(this._payload, 0, this._payload.length, true);
      int payloadEnd = buffer.cursor();
      buffer.writeInt(20);
      int signatureOffset = buffer.cursor();
      buffer.ensureCapacity(20);
      buffer.trimToSize();
      this._secureMsg = buffer.getBuffer();

      try {
         Key key = this._keyProvider.getPrimaryRegistrationKey(this._receiverId);
         this._securityProvider.sign(this._secureMsg, 0, payloadEnd, this._secureMsg, signatureOffset, SecurityAlgorithm.HMAC_SHA1, key);
      } catch (KeyProviderException e) {
         throw new SecureMessageException(200, e);
      } catch (SecurityProviderException e) {
         throw new SecureMessageException(200, e);
      }
   }

   private void signAndEncrypt(CompressedBuffer buffer) {
      int headerLength = buffer.cursor();
      this.sign(buffer);

      byte[] iv;
      byte[] ciphertext;
      try {
         Key key = this._keyProvider.getPrimaryRegistrationKey(this._receiverId);
         SecurityAlgorithm algorithm = SecurityAlgorithm.AES_CBC_PKCS5;
         iv = this._securityProvider.generateIV(16);
         ciphertext = this._securityProvider.encrypt(buffer.getBuffer(), headerLength, buffer.end() - headerLength, iv, 0, iv.length, algorithm, key);
      } catch (KeyProviderException e) {
         throw new SecureMessageException(201, e);
      } catch (SecurityProviderException e) {
         throw new SecureMessageException(201, e);
      }

      CompressedBuffer encryptionBuffer = new CompressedBuffer(headerLength + this.getRequiredCapacity(iv) + this.getRequiredCapacity(ciphertext));
      encryptionBuffer.copy(buffer, 0, headerLength, true);
      encryptionBuffer.writeInt(iv.length);
      encryptionBuffer.copy(iv, 0, iv.length, true);
      encryptionBuffer.writeInt(ciphertext.length);
      encryptionBuffer.copy(ciphertext, 0, ciphertext.length, true);
      this._secureMsg = encryptionBuffer.getBuffer();
   }

   @Override
   public void verifySecurity() {
      if (this._secureMsg != null) {
         CompressedBuffer buffer;
         int sequenceId;
         int mode;
         try {
            buffer = new CompressedBuffer(this._secureMsg);
            buffer.readByte();
            this._senderId = buffer.readLong();
            sequenceId = buffer.readInt();
            mode = buffer.readByte();
         } catch (DataException e) {
            throw new SecureMessageException(0, e);
         }

         this.verifyVersion();
         this.verifySecurityMode(mode);
         this.verifySecurity(mode, buffer);
         this.verifySequence(sequenceId);
         this._secureMsg = null;
      }
   }

   private void verifyVersion() {
      int expectedVersion = -1;

      try {
         expectedVersion = this._versionProvider.getSecurityVersion(this._senderId);
      } catch (VersionProviderException e) {
         throw new SecureMessageException(104, e);
      }

      if (expectedVersion != this.getVersion()) {
         throw new SecureMessageException(
            104, ((StringBuffer)(new Object("Expected version "))).append(expectedVersion).append(", received version ").append(this.getVersion()).toString()
         );
      }
   }

   private void verifySecurityMode(int mode) {
      if (mode != 0 && mode != 1) {
         throw new SecureMessageException(103);
      }
   }

   private void verifySequence(int sequenceId) {
      boolean verified = false;
      Exception error = null;

      try {
         verified = this._sequenceProvider.verify(sequenceId, this._senderId);
      } catch (SequenceProviderException e) {
         error = e;
      }

      if (!verified) {
         throw new SecureMessageException(100, error);
      }
   }

   private void verifySecurity(int mode, CompressedBuffer buffer) {
      boolean signedOnly = mode == 0;

      Key key;
      try {
         key = this._keyProvider.getPrimaryRegistrationKey(this._senderId);
      } catch (KeyProviderException e) {
         throw new SecureMessageException(signedOnly ? 101 : 102, e);
      }

      int cursor = buffer.cursor();
      boolean verified = false;
      Exception primaryError = null;

      try {
         verified = signedOnly ? this.verifySignature(buffer, key) : this.decryptAndVerifySignature(buffer, key);
      } catch (SecureMessageException e) {
         throw e;
      } catch (SecurityProviderException e) {
         primaryError = e;
      }

      Exception secondaryError = null;
      if (!verified) {
         try {
            key = this._keyProvider.getSecondaryRegistrationKey(this._senderId);
            buffer.rewind(cursor);
            verified = key != null && (signedOnly ? this.verifySignature(buffer, key) : this.decryptAndVerifySignature(buffer, key));
         } catch (KeyProviderException e) {
            secondaryError = e;
         } catch (SecurityProviderException e) {
            secondaryError = e;
         } catch (SecureMessageException e) {
            secondaryError = e;
         }
      }

      if (!verified) {
         throw new SecureMessageException(signedOnly ? 101 : 102, primaryError, secondaryError);
      }
   }

   private boolean verifySignature(CompressedBuffer buffer, Key key) {
      int payloadLength;
      int payloadOffset;
      int payloadEnd;
      int signatureOffset;
      try {
         payloadLength = buffer.readInt();
         payloadOffset = buffer.cursor();
         buffer.forward(payloadLength);
         payloadEnd = buffer.cursor();
         buffer.readInt();
         signatureOffset = buffer.cursor();
      } catch (DataException e) {
         throw new SecureMessageException(0, e);
      }

      byte[] src = buffer.getBuffer();
      boolean verified = this._securityProvider.verifySignature(src, 0, payloadEnd, src, signatureOffset, 20, SecurityAlgorithm.HMAC_SHA1, key);
      if (verified) {
         this._payload = new byte[payloadLength];
         System.arraycopy(src, payloadOffset, this._payload, 0, payloadLength);
      }

      return verified;
   }

   private boolean decryptAndVerifySignature(CompressedBuffer buffer, Key key) {
      int headerLength = buffer.cursor();

      int ivLength;
      int ivOffset;
      int ciphertextLength;
      int ciphertextOffset;
      try {
         ivLength = buffer.readInt();
         ivOffset = buffer.cursor();
         buffer.forward(ivLength);
         ciphertextLength = buffer.readInt();
         ciphertextOffset = buffer.cursor();
      } catch (DataException e) {
         throw new SecureMessageException(0, e);
      }

      byte[] src = buffer.getBuffer();
      byte[] plaintext = this._securityProvider.decrypt(src, ciphertextOffset, ciphertextLength, src, ivOffset, ivLength, SecurityAlgorithm.AES_CBC_PKCS5, key);
      CompressedBuffer verificationBuffer = new CompressedBuffer(headerLength + plaintext.length);
      verificationBuffer.copy(buffer, 0, headerLength, true);
      verificationBuffer.copy(plaintext, 0, plaintext.length, false);
      return this.verifySignature(verificationBuffer, key);
   }

   private int getRequiredHeaderCapacity(long senderId, int sequenceId) {
      return 2 + CompressedBuffer.getCompressedLongSize(senderId) + CompressedBuffer.getCompressedIntSize(sequenceId);
   }

   private int getRequiredSignatureCapacity() {
      return CompressedBuffer.getCompressedIntSize(20) + 20;
   }

   private int getRequiredCapacity(byte[] b) {
      return CompressedBuffer.getCompressedIntSize(b.length) + b.length;
   }

   @Override
   public byte[] getPayload() {
      return this._secureMsg != null ? this._secureMsg : this._payload;
   }
}
