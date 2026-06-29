package net.rim.wica.runtime.security.internal;

import java.io.ByteArrayInputStream;
import net.rim.device.api.crypto.AESDecryptorEngine;
import net.rim.device.api.crypto.AESEncryptorEngine;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.BlockEncryptor;
import net.rim.device.api.crypto.CBCDecryptorEngine;
import net.rim.device.api.crypto.CBCEncryptorEngine;
import net.rim.device.api.crypto.DecryptorInputStream;
import net.rim.device.api.crypto.ECCryptoSystem;
import net.rim.device.api.crypto.ECDHKeyAgreement;
import net.rim.device.api.crypto.ECDSASignatureSigner;
import net.rim.device.api.crypto.ECDSASignatureVerifier;
import net.rim.device.api.crypto.ECIESDecryptor;
import net.rim.device.api.crypto.ECIESEncryptor;
import net.rim.device.api.crypto.ECKeyPair;
import net.rim.device.api.crypto.ECPrivateKey;
import net.rim.device.api.crypto.ECPublicKey;
import net.rim.device.api.crypto.EncryptorOutputStream;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.PKCS1FormatterEngine;
import net.rim.device.api.crypto.PKCS1SignatureSigner;
import net.rim.device.api.crypto.PKCS1SignatureVerifier;
import net.rim.device.api.crypto.PKCS1UnformatterEngine;
import net.rim.device.api.crypto.PKCS5FormatterEngine;
import net.rim.device.api.crypto.PKCS5UnformatterEngine;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSADecryptorEngine;
import net.rim.device.api.crypto.RSAEncryptorEngine;
import net.rim.device.api.crypto.RSAPrivateKey;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.SignatureVerifierInputStream;
import net.rim.device.api.crypto.encoder.EncodedKey;
import net.rim.device.api.crypto.encoder.PrivateKeyDecoder;
import net.rim.device.api.crypto.encoder.PublicKeyDecoder;
import net.rim.device.api.crypto.encoder.PublicKeyEncoder;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;
import net.rim.wica.transport.security.Key;
import net.rim.wica.transport.security.KeyPair;
import net.rim.wica.transport.security.KeyType;
import net.rim.wica.transport.security.SecurityAlgorithm;
import net.rim.wica.transport.security.SecurityProvider;
import net.rim.wica.transport.security.SecurityProviderException;

final class SecurityProviderImpl implements SecurityProvider {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final Key decodeKey(KeyType keyType, byte[] encodedKey, boolean asymIsPublic) throws SecurityProviderException {
      try {
         if (keyType.equals(KeyType.AES)) {
            return new Key(new AESKey(encodedKey), keyType);
         }

         if (keyType.equals(KeyType.RSA)) {
            return new Key(asymIsPublic ? PublicKeyDecoder.decode(encodedKey, "X509") : PrivateKeyDecoder.decode(encodedKey, "X509"), keyType);
         }

         if (keyType.equals(KeyType.ECDSA)) {
            return new Key(asymIsPublic ? new ECPublicKey(new ECCryptoSystem(), encodedKey) : new ECPrivateKey(new ECCryptoSystem(), encodedKey), keyType);
         }
      } catch (Throwable var6) {
         throw new SecurityProviderException(e);
      }

      throw new SecurityProviderException("Not supported operation: decodeKey for type " + keyType.getName());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final int sign(byte[] text, int textOffset, int textLength, byte[] output, int outputOffset, SecurityAlgorithm algorithm, Key key) throws SecurityProviderException {
      try {
         if (SecurityAlgorithm.HMAC_SHA1 == algorithm) {
            HMAC hMAC = new HMAC(new HMACKey(((AESKey)key.getNativeKey()).getData()), new SHA1Digest());
            hMAC.update(text, textOffset, textLength);
            hMAC.getMAC(output, outputOffset, true);
            return hMAC.getLength();
         }

         if (SecurityAlgorithm.ECDSA_SHA1 == algorithm) {
            ECDSASignatureSigner signer = new ECDSASignatureSigner((ECPrivateKey)key.getNativeKey(), new SHA1Digest());
            signer.update(text, textOffset, textLength);
            signer.sign(output, outputOffset, output, outputOffset + signer.getRLength());
            signer.reset();
            return signer.getSLength() + signer.getRLength();
         }

         if (SecurityAlgorithm.RSA_SHA1 == algorithm) {
            PKCS1SignatureSigner signer = new PKCS1SignatureSigner((RSAPrivateKey)key.getNativeKey(), new SHA1Digest());
            signer.update(text, textOffset, textLength);
            signer.sign(output, outputOffset);
            signer.reset();
            return signer.getLength();
         }
      } catch (Throwable var10) {
         throw new SecurityProviderException(e);
      }

      throw new SecurityProviderException("Sign: unsupported algorithm " + algorithm.getName());
   }

   @Override
   public final boolean verifySignature(byte[] text, byte[] signature, SecurityAlgorithm algorithm, Key key) {
      return this.verifySignature(text, 0, text.length, signature, 0, signature.length, algorithm, key);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean verifySignature(
      byte[] text, int textOffset, int textLength, byte[] signature, int signatureOffset, int signatureLength, SecurityAlgorithm algorithm, Key key
   ) throws SecurityProviderException {
      try {
         if (SecurityAlgorithm.HMAC_SHA1 == algorithm) {
            HMAC hMAC = new HMAC(new HMACKey(((AESKey)key.getNativeKey()).getData()), new SHA1Digest());
            hMAC.update(text, textOffset, textLength);
            byte[] signed = hMAC.getMAC(true);
            return Arrays.equals(signed, 0, signature, signatureOffset, signed.length);
         }

         if (SecurityAlgorithm.ECDSA_SHA1 == algorithm) {
            ECPublicKey pubKey = (ECPublicKey)key.getNativeKey();
            ECDSASignatureVerifier verifierECC = new ECDSASignatureVerifier(
               pubKey, signature, signatureOffset, signature, signatureOffset + pubKey.getECCryptoSystem().getFieldLength()
            );
            ByteArrayInputStream inputStream = new ByteArrayInputStream(text, textOffset, textLength);
            SignatureVerifierInputStream verifierStream = new SignatureVerifierInputStream(verifierECC, inputStream);
            byte[] data = new byte[verifierStream.available()];
            verifierStream.read(data);
            return verifierECC.verify();
         }

         if (SecurityAlgorithm.RSA_SHA1 == algorithm) {
            PKCS1SignatureVerifier verifier = new PKCS1SignatureVerifier((RSAPublicKey)key.getNativeKey(), signature, signatureOffset);
            verifier.update(text, textOffset, textLength);
            return verifier.verify();
         }
      } catch (Throwable var15) {
         throw new SecurityProviderException(e);
      }

      throw new SecurityProviderException("Verify signature: unsupported algorithm " + algorithm.getName());
   }

   @Override
   public final byte[] encrypt(byte[] plaintext, SecurityAlgorithm algorithm, Key key, byte[] IV) {
      return this.encrypt(plaintext, 0, plaintext.length, IV, 0, IV != null ? IV.length : 0, algorithm, key);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final byte[] encrypt(
      byte[] plaintext, int plaintextOffset, int plaintextLength, byte[] IV, int ivOffset, int ivLength, SecurityAlgorithm algorithm, Key key
   ) throws SecurityProviderException {
      byte[] cipherTxt = null;
      NoCopyByteArrayOutputStream out = new NoCopyByteArrayOutputStream();
      EncryptorOutputStream cryptoStream = null;

      SecurityProviderException e;
      try {
         try {
            if (SecurityAlgorithm.AES_CBC_PKCS5 == algorithm) {
               cryptoStream = new BlockEncryptor(
                  new PKCS5FormatterEngine(
                     new CBCEncryptorEngine(
                        new AESEncryptorEngine((AESKey)key.getNativeKey()), IV == null ? null : new InitializationVector(IV, ivOffset, ivLength)
                     )
                  ),
                  out
               );
            } else {
               if (SecurityAlgorithm.RSA_ECB_PKCS1 != algorithm) {
                  throw new SecurityProviderException("Encrypt: unsupported algorithm " + algorithm.getName());
               }

               if (key.getKeyType() == KeyType.RSA) {
                  cryptoStream = new BlockEncryptor(new PKCS1FormatterEngine(new RSAEncryptorEngine((RSAPublicKey)key.getNativeKey())), out);
               } else {
                  cryptoStream = new ECIESEncryptor(out, (ECPublicKey)key.getNativeKey());
               }
            }

            cryptoStream.write(plaintext, plaintextOffset, plaintextLength);
            cryptoStream.flush();
            cryptoStream.close();
            int finalLength = out.size();
            cipherTxt = new byte[finalLength];
            System.arraycopy(out.getByteArray(), 0, cipherTxt, 0, finalLength);
            out.close();
            return cipherTxt;
         } catch (SecurityProviderException var15) {
            e = var15;
         }
      } catch (Throwable var16) {
         throw new SecurityProviderException("Encrypt: ", e);
      }

      throw e;
   }

   @Override
   public final byte[] decrypt(byte[] ciphertext, SecurityAlgorithm algorithm, Key key, byte[] IV) {
      return this.decrypt(ciphertext, 0, ciphertext.length, IV, 0, IV != null ? IV.length : 0, algorithm, key);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final byte[] decrypt(
      byte[] ciphertext, int ciphertextOffset, int ciphertextLength, byte[] IV, int ivOffset, int ivLength, SecurityAlgorithm algorithm, Key key
   ) throws SecurityProviderException {
      byte[] plainTxt = null;
      ByteArrayInputStream in = new ByteArrayInputStream(ciphertext, ciphertextOffset, ciphertextLength);
      DecryptorInputStream decryptoStream = null;

      SecurityProviderException e;
      try {
         try {
            if (SecurityAlgorithm.AES_CBC_PKCS5 == algorithm) {
               decryptoStream = new BlockDecryptor(
                  new PKCS5UnformatterEngine(
                     new CBCDecryptorEngine(new AESDecryptorEngine((AESKey)key.getNativeKey()), new InitializationVector(IV, ivOffset, ivLength))
                  ),
                  in
               );
            } else {
               if (SecurityAlgorithm.RSA_ECB_PKCS1 != algorithm) {
                  throw new SecurityProviderException("Decrypt: unsupported algorithm " + algorithm.getName());
               }

               if (key.getKeyType() == KeyType.ECDSA) {
                  decryptoStream = new ECIESDecryptor(in, (ECPrivateKey)key.getNativeKey());
               } else {
                  decryptoStream = new BlockDecryptor(new PKCS1UnformatterEngine(new RSADecryptorEngine((RSAPrivateKey)key.getNativeKey())), in);
               }
            }

            plainTxt = new byte[ciphertextLength];
            int actual = decryptoStream.read(plainTxt, 0, ciphertextLength);
            decryptoStream.close();
            Array.resize(plainTxt, actual);
            return plainTxt;
         } catch (SecurityProviderException var15) {
            e = var15;
         }
      } catch (Throwable var16) {
         throw new SecurityProviderException("Decrypt: ", e);
      }

      throw e;
   }

   @Override
   public final boolean isServer() {
      return false;
   }

   @Override
   public final Key generateKey(KeyType keyType) {
      if (keyType.equals(KeyType.AES)) {
         return new Key(new AESKey(), KeyType.AES);
      } else {
         throw new IllegalArgumentException("Invalid key type: " + keyType.getName());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final KeyPair generateKeyPair(KeyType keyType) throws SecurityProviderException {
      try {
         if (keyType.equals(KeyType.ECDSA)) {
            ECKeyPair keyPair = new ECKeyPair(new ECCryptoSystem());
            return new KeyPair(new Key(keyPair.getPrivateKey(), KeyType.ECDSA), new Key(keyPair.getPublicKey(), KeyType.ECDSA));
         }
      } catch (Throwable var4) {
         throw new SecurityProviderException(e);
      }

      throw new IllegalArgumentException("Invalid key type" + keyType.getName());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final byte[] encodeKey(Key key) throws SecurityProviderException {
      try {
         KeyType keyType = key.getKeyType();
         if (keyType.equals(KeyType.AES)) {
            AESKey aesKey = (AESKey)key.getNativeKey();
            return aesKey.getData();
         }

         if (keyType.equals(KeyType.ECDSA)) {
            if (key.getNativeKey() instanceof ECPublicKey) {
               ECPublicKey pKey = (ECPublicKey)key.getNativeKey();
               return pKey.getPublicKeyData();
            }

            ECPrivateKey pKey = (ECPrivateKey)key.getNativeKey();
            return pKey.getPrivateKeyData();
         }

         if (keyType.equals(KeyType.RSA)) {
            EncodedKey encodedKey = PublicKeyEncoder.encode((PublicKey)key.getNativeKey());
            return encodedKey.getEncodedKey();
         }
      } catch (Throwable var5) {
         throw new SecurityProviderException(e);
      }

      throw new SecurityProviderException("Not supported operation: encodeKey for type " + key.getKeyType().getName());
   }

   @Override
   public final Key decodeKey(KeyType keyType, byte[] encodedKey) {
      return this.decodeKey(keyType, encodedKey, true);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final byte[] sign(byte[] text, SecurityAlgorithm algorithm, Key key) throws SecurityProviderException {
      try {
         if (SecurityAlgorithm.HMAC_SHA1 == algorithm) {
            HMAC hMAC = new HMAC(new HMACKey(((AESKey)key.getNativeKey()).getData()), new SHA1Digest());
            hMAC.update(text, 0, text.length);
            return hMAC.getMAC(true);
         }

         if (SecurityAlgorithm.ECDSA_SHA1 == algorithm) {
            ECDSASignatureSigner signer = new ECDSASignatureSigner((ECPrivateKey)key.getNativeKey(), new SHA1Digest());
            signer.update(text, 0, text.length);
            byte[] signed = new byte[signer.getRLength() + signer.getSLength()];
            signer.sign(signed, 0, signed, signer.getRLength());
            signer.reset();
            return signed;
         }

         if (SecurityAlgorithm.RSA_SHA1 == algorithm) {
            PKCS1SignatureSigner signer = new PKCS1SignatureSigner((RSAPrivateKey)key.getNativeKey(), new SHA1Digest());
            signer.update(text, 0, text.length);
            byte[] signed = new byte[signer.getLength()];
            signer.sign(signed, 0);
            signer.reset();
            return signed;
         }
      } catch (Throwable var7) {
         throw new SecurityProviderException(e);
      }

      throw new SecurityProviderException("Sign: unsupported algorithm " + algorithm.getName());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final byte[] deriveSecret(SecurityAlgorithm algorithm, Key privateKey, Key remotePublicKey) throws SecurityProviderException {
      byte[] encodedRemotePublicKey = this.encodeKey(remotePublicKey);
      ECPrivateKey deviceKey = (ECPrivateKey)privateKey.getNativeKey();

      try {
         return new AESKey(ECDHKeyAgreement.generateSharedSecret(deviceKey, new ECPublicKey(deviceKey.getECCryptoSystem(), encodedRemotePublicKey), false))
            .getData();
      } catch (Throwable var8) {
         throw new SecurityProviderException("Derive secret: ", e);
      }
   }

   @Override
   public final byte[] generateIV(int length) {
      return RandomSource.getBytes(length);
   }
}
