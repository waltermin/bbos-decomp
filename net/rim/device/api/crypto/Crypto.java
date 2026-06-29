package net.rim.device.api.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import net.rim.device.api.crypto.encoder.DecodedSignature;
import net.rim.device.api.crypto.encoder.EncodedSignature;
import net.rim.device.api.crypto.encoder.SignatureDecoder;
import net.rim.device.api.crypto.encoder.SignatureEncoder;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class Crypto {
   private Crypto() {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final byte[] encrypt(byte[] plaintext, int offset, int length, Key key, String algorithm, InitializationVector iv) {
      if (plaintext != null && offset >= 0 && offset <= length && length > 0 && plaintext.length - length >= offset) {
         CryptoIOException e;
         try {
            try {
               ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
               EncryptorOutputStream eos = EncryptorFactory.getEncryptorOutputStream(key, output, algorithm, iv);
               eos.write(plaintext, offset, length);
               eos.close();
               return output.toByteArray();
            } catch (CryptoIOException var10) {
               e = var10;
            }
         } catch (Throwable var11) {
            throw new Object(e.toString());
         }

         throw e.getCryptoException();
      } else {
         throw new Object();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final byte[] decrypt(byte[] ciphertext, int offset, int length, Key key, String algorithm, InitializationVector iv) {
      if (ciphertext != null && ciphertext.length > 0 && offset >= 0 && offset <= length && length > 0 && ciphertext.length - length >= offset) {
         CryptoIOException e;
         try {
            try {
               ByteArrayInputStream input = (ByteArrayInputStream)(new Object(ciphertext, offset, length));
               DecryptorInputStream dis = DecryptorFactory.getDecryptorInputStream(key, input, algorithm, iv);
               byte[] buffer = new byte[length];
               int plaintextLength = dis.read(buffer);
               Array.resize(buffer, plaintextLength);
               return buffer;
            } catch (CryptoIOException var12) {
               e = var12;
            }
         } catch (Throwable var13) {
            throw new Object(e.toString());
         }

         throw e.getCryptoException();
      } else {
         throw new Object();
      }
   }

   public static final byte[] getDigest(byte[] data, int offset, int length, String algorithm) {
      Digest digest = DigestFactory.getInstance(algorithm);
      digest.update(data, offset, length);
      return digest.getDigest();
   }

   public static final byte[] getMAC(byte[] data, int offset, int length, SymmetricKey key, String algorithm) {
      MAC mac = MACFactory.getInstance(algorithm, key);
      mac.update(data, offset, length);
      return mac.getMAC();
   }

   public static final boolean checkMAC(
      byte[] data, int dataOffset, int dataLength, SymmetricKey key, String algorithm, byte[] mac, int macOffset, int macLength
   ) {
      if (mac != null && macOffset >= 0 && macLength >= 0 && mac.length - macLength >= macOffset) {
         return Arrays.equals(getMAC(data, dataOffset, dataLength, key, algorithm), 0, mac, macOffset, macLength);
      } else {
         throw new Object();
      }
   }

   public static final byte[] sign(byte[] data, int offset, int length, PrivateKey key, String signingAlgorithm, String encodingAlgorithm) {
      SignatureSigner signer = SignatureSignerFactory.getInstance(key, signingAlgorithm);
      signer.update(data, offset, length);
      EncodedSignature signature = SignatureEncoder.encode(signer, encodingAlgorithm);
      return signature.getEncodedSignature();
   }

   public static final boolean verify(
      byte[] data, int dataOffset, int dataLength, PublicKey key, String encodingAlgorithm, byte[] encodedSignature, int encodedSignatureOffset
   ) {
      DecodedSignature signature = SignatureDecoder.decode(encodedSignature, encodedSignatureOffset, encodingAlgorithm);
      SignatureVerifier verifier = signature.getVerifier(key);
      verifier.update(data, dataOffset, dataLength);
      return verifier.verify();
   }

   public static final byte[] getPRNG(byte[] seed, int seedOffset, int seedLength, int outputLength) {
      try {
         PseudoRandomSource prng = (PseudoRandomSource)(new Object(seed, seedOffset, seedLength));
         return prng.getBytes(outputLength);
      } finally {
         throw new Object();
      }
   }
}
