package net.rim.wica.transport.internal.security;

import net.rim.wica.transport.VersionProvider;
import net.rim.wica.transport.VersionProviderException;
import net.rim.wica.transport.internal.message.TransportMessageV1_0;
import net.rim.wica.transport.internal.message.commonheader.CommonHeaderV1_0;
import net.rim.wica.transport.internal.message.data.DataBuffer;
import net.rim.wica.transport.message.TransportMessageException;
import net.rim.wica.transport.message.TransportMessageV1;
import net.rim.wica.transport.security.Key;
import net.rim.wica.transport.security.KeyPair;
import net.rim.wica.transport.security.KeyProvider;
import net.rim.wica.transport.security.KeyType;
import net.rim.wica.transport.security.SecureMessageException;
import net.rim.wica.transport.security.SecureMessageV1;
import net.rim.wica.transport.security.SecurityAlgorithm;
import net.rim.wica.transport.security.SecurityProvider;

public class SecureMessageV1_0 implements SecureMessageV1 {
   private KeyProvider _keyProvider;
   private SecurityProvider _securityProvider;
   private VersionProvider _versionProvider;
   private long _receiverId;
   private byte[] _unsecureMsg;
   private TransportMessageV1_0 _tm;
   private CommonHeaderV1_0 _header;
   private byte[] _secureMsg;
   private int _headerLength;
   private static final int _RSA_SHA1_SIG_LENGTH = 128;
   private static final int _ECDSA_SHA1_SIG_LENGTH = 42;
   private static final int _ENC_AES_KEY_LENGTH = 128;
   private static final int _EC_KEY_LENGTH = 22;
   private static final byte[] _IV = new byte[16];

   public SecureMessageV1_0(byte[] secureMsg, VersionProvider versionProvider, KeyProvider keyProvider, SecurityProvider securityProvider) {
      this._secureMsg = secureMsg;
      this._keyProvider = keyProvider;
      this._securityProvider = securityProvider;
      this._versionProvider = versionProvider;
   }

   public SecureMessageV1_0(long receiverId, byte[] unsecureMsg, KeyProvider keyProvider, SecurityProvider securityProvider) {
      try {
         this._tm = new TransportMessageV1_0(unsecureMsg);
      } catch (TransportMessageException e) {
         throw new SecureMessageException(0, e);
      }

      this._receiverId = receiverId;
      this._keyProvider = keyProvider;
      this._securityProvider = securityProvider;
   }

   public SecureMessageV1_0(TransportMessageV1 tm, long receiverId, KeyProvider keyProvider, SecurityProvider securityProvider) {
      this._tm = (TransportMessageV1_0)tm;
      this._receiverId = receiverId;
      this._keyProvider = keyProvider;
      this._securityProvider = securityProvider;
   }

   private int getVersion() {
      return 1;
   }

   @Override
   public void secure(int mode) {
      if (this._tm != null) {
         if (mode == 2) {
            this.secureNothing();
         } else {
            this._header = new CommonHeaderV1_0((CommonHeaderV1_0)this._tm.getCommonHeader());
            this._header.setSecurityMode(mode);
            this._header.setSecurityVersion(this.getVersion());
            if (mode == 0) {
               this.sign();
            } else {
               this.signAndEncrypt();
            }
         }

         this._tm = null;
      }
   }

   private void secureNothing() {
      this._secureMsg = this._tm.serialize();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void sign() {
      int headerLength = this._header.getHeaderLength();
      int sigLength = this.getOutgoingSignatureLength();
      DataBuffer buffer = new DataBuffer(headerLength + this._tm.getPayloadLength() + sigLength);
      this._header.serialize(buffer);
      int payloadLength = this._tm.serializePayload(buffer);
      int signatureStart = buffer.cursor();
      buffer.ensureCapacity(sigLength);
      buffer.trimToSize();
      this._secureMsg = buffer.getBuffer();

      try {
         this.sign(this._secureMsg, headerLength, payloadLength, this._secureMsg, signatureStart);
      } catch (Throwable var8) {
         throw new SecureMessageException(200, t);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void signAndEncrypt() {
      int sigLength = this.getOutgoingSignatureLength();
      DataBuffer buffer = new DataBuffer(this._tm.getPayloadLength() + sigLength);
      int payloadLength = this._tm.serializePayload(buffer);
      buffer.ensureCapacity(sigLength);
      buffer.trimToSize();
      byte[] plaintext = buffer.getBuffer();

      try {
         this.sign(plaintext, 0, payloadLength, plaintext, payloadLength);
      } catch (Throwable var12) {
         throw new SecureMessageException(200, t);
      }

      byte[] ciphertext;
      try {
         ciphertext = this.encrypt(plaintext, 0, plaintext.length);
      } catch (Throwable var11) {
         throw new SecureMessageException(201, t);
      }

      buffer = new DataBuffer(this._header.getHeaderLength() + ciphertext.length);
      this._header.serialize(buffer);
      buffer.copy(ciphertext, 0, ciphertext.length, false);
      this._secureMsg = buffer.getBuffer();
   }

   private void sign(byte[] text, int textOffset, int textLength, byte[] output, int outputOffset) {
      Key key = this._keyProvider.getPrivateKey();
      SecurityAlgorithm algorithm = this._securityProvider.isServer() ? SecurityAlgorithm.RSA_SHA1 : SecurityAlgorithm.ECDSA_SHA1;
      this._securityProvider.sign(text, textOffset, textLength, output, outputOffset, algorithm, key);
   }

   private byte[] encrypt(byte[] plaintext, int offset, int length) {
      Key remotePublicKey = this._keyProvider.getRemotePublicKey(this._receiverId);
      Key ephemeralAESKey;
      byte[] transmittedKey;
      if (this._securityProvider.isServer()) {
         KeyPair keyPair = this._securityProvider.generateKeyPair(KeyType.ECDSA);
         transmittedKey = this._securityProvider.encodeKey(keyPair.getPublicKey());
         byte[] secret = this._securityProvider.deriveSecret(SecurityAlgorithm.ECDH, keyPair.getPrivateKey(), remotePublicKey);
         byte[] truncatedSecret = new byte[16];
         System.arraycopy(secret, 0, truncatedSecret, 0, truncatedSecret.length);
         ephemeralAESKey = this._securityProvider.decodeKey(KeyType.AES, truncatedSecret);
      } else {
         ephemeralAESKey = this._securityProvider.generateKey(KeyType.AES);
         transmittedKey = this._securityProvider
            .encrypt(this._securityProvider.encodeKey(ephemeralAESKey), SecurityAlgorithm.RSA_ECB_PKCS1, remotePublicKey, null);
      }

      byte[] ciphertext = this._securityProvider.encrypt(plaintext, offset, length, _IV, 0, _IV.length, SecurityAlgorithm.AES_CBC_PKCS5, ephemeralAESKey);
      byte[] combination = new byte[transmittedKey.length + ciphertext.length];
      System.arraycopy(transmittedKey, 0, combination, 0, transmittedKey.length);
      System.arraycopy(ciphertext, 0, combination, transmittedKey.length, ciphertext.length);
      return combination;
   }

   @Override
   public void verifySecurity() {
      if (this._secureMsg != null) {
         DataBuffer buffer = new DataBuffer(this._secureMsg);
         this._header = new CommonHeaderV1_0();

         try {
            this._headerLength = this._header.deserialize(buffer);
         } catch (TransportMessageException e) {
            throw new SecureMessageException(0, e);
         }

         this.verifyVersion();
         if (this._header.isUnsecure()) {
            this.unsecureNothing();
         } else {
            boolean signedOnly = this._header.isSignedOnly();
            this._header.setSecurityVersion(0);
            this._header.setSecurityMode(2);
            if (signedOnly) {
               this.verifySignature();
            } else {
               this.decryptAndVerifySignature();
            }
         }

         this._secureMsg = null;
         this._header = null;
      }
   }

   private void verifyVersion() {
      int expectedVersion = -1;

      try {
         expectedVersion = this._versionProvider.getSecurityVersion(this._header.getSenderId());
      } catch (VersionProviderException e) {
         throw new SecureMessageException(104, e);
      }

      if (expectedVersion != this.getVersion()) {
         throw new SecureMessageException(
            104, ((StringBuffer)(new Object("Expected version "))).append(expectedVersion).append(", received version ").append(this.getVersion()).toString()
         );
      }
   }

   private void unsecureNothing() {
      this._unsecureMsg = this._secureMsg;
   }

   private void verifySignature() {
      int signatureLength = this.getIncomingSignatureLength();
      int signatureOffset = this._secureMsg.length - signatureLength;
      this.verifySignature(this._secureMsg, this._headerLength, signatureOffset - this._headerLength, this._secureMsg, signatureOffset, signatureLength);
      DataBuffer buffer = new DataBuffer(signatureOffset);
      this._header.serialize(buffer);
      buffer.copy(this._secureMsg, this._headerLength, signatureOffset - this._headerLength, false);
      buffer.trimToSize();
      this._unsecureMsg = buffer.getBuffer();
   }

   private void decryptAndVerifySignature() {
      byte[] plaintext = this.decrypt(this._secureMsg, this._headerLength, this._secureMsg.length - this._headerLength);
      int signatureLength = this.getIncomingSignatureLength();
      int signatureOffset = plaintext.length - signatureLength;
      this.verifySignature(plaintext, 0, signatureOffset, plaintext, signatureOffset, signatureLength);
      DataBuffer buffer = new DataBuffer(this._headerLength + signatureOffset);
      this._header.serialize(buffer);
      buffer.copy(plaintext, 0, signatureOffset, false);
      buffer.trimToSize();
      this._unsecureMsg = buffer.getBuffer();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void verifySignature(byte[] text, int textOffset, int textLength, byte[] signature, int signatureOffset, int signatureLength) {
      if (signature.length < signatureOffset + signatureLength) {
         throw new SecureMessageException(0);
      }

      try {
         Key publicKey = this._keyProvider.getRemotePublicKey(this._header.getSenderId());
         SecurityAlgorithm algorithm = this._securityProvider.isServer() ? SecurityAlgorithm.ECDSA_SHA1 : SecurityAlgorithm.RSA_SHA1;
         if (!this._securityProvider.verifySignature(text, textOffset, textLength, signature, signatureOffset, signatureLength, algorithm, publicKey)) {
            throw new SecureMessageException(101);
         }
      } catch (Throwable var10) {
         throw new SecureMessageException(101, t);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private byte[] decrypt(byte[] ciphertext, int offset, int length) {
      int encodedKeyLength = this._securityProvider.isServer() ? 128 : 22;
      if (length < encodedKeyLength) {
         throw new SecureMessageException(0);
      }

      try {
         byte[] encodedKey = null;
         Key privateKey = this._keyProvider.getPrivateKey();
         if (this._securityProvider.isServer()) {
            encodedKey = this._securityProvider.decrypt(ciphertext, offset, encodedKeyLength, null, -1, -1, SecurityAlgorithm.RSA_ECB_PKCS1, privateKey);
         } else {
            encodedKey = new byte[encodedKeyLength];
            System.arraycopy(ciphertext, offset, encodedKey, 0, encodedKeyLength);
            Key publicKey = this._securityProvider.decodeKey(KeyType.ECDSA, encodedKey);
            encodedKey = this._securityProvider.deriveSecret(SecurityAlgorithm.ECDH, privateKey, publicKey);
         }

         Key symmetricKey = this._securityProvider.decodeKey(KeyType.AES, encodedKey);
         return this._securityProvider
            .decrypt(ciphertext, offset + encodedKeyLength, length - encodedKeyLength, _IV, 0, _IV.length, SecurityAlgorithm.AES_CBC_PKCS5, symmetricKey);
      } catch (Throwable var9) {
         throw new SecureMessageException(102, t);
      }
   }

   private int getIncomingSignatureLength() {
      return this._securityProvider.isServer() ? 42 : 128;
   }

   private int getOutgoingSignatureLength() {
      return this._securityProvider.isServer() ? 128 : 42;
   }

   @Override
   public byte[] getPayload() {
      if (this._secureMsg != null) {
         return this._secureMsg;
      } else {
         return this._tm != null ? this._tm.serialize() : this._unsecureMsg;
      }
   }
}
