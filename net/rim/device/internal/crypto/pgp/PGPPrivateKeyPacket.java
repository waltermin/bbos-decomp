package net.rim.device.internal.crypto.pgp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import net.rim.device.api.crypto.CFBDecryptor;
import net.rim.device.api.crypto.DHPublicKey;
import net.rim.device.api.crypto.DSAPublicKey;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.EncryptorFactory;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.SymmetricKeyEncryptorEngine;
import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.device.api.crypto.pgp.PGPPasswordTicket;
import net.rim.device.api.crypto.pgp.PGPPseudoRandomSource;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class PGPPrivateKeyPacket extends PGPPublicKeyPacket implements Persistable {
   private byte _symmetricEncryptionAlgorithm;
   private byte _hashAlgorithm;
   private byte _s2kUsage;
   private byte _s2kType;
   private byte[] _iv;
   private byte[] _salt;
   private byte _count;
   private byte[] _encryptedSecretKeyData;

   public PGPPrivateKeyPacket(int tag, byte[] encoding) {
      super(tag, encoding);
      int offset = this.getPublicKeyLength();
      byte algorithmOrS2K = encoding[offset++];
      switch (algorithmOrS2K) {
         case -3:
            this._symmetricEncryptionAlgorithm = algorithmOrS2K;
            this._hashAlgorithm = 1;
            break;
         case -2:
         case -1:
         default:
            this._s2kUsage = algorithmOrS2K;
            this._symmetricEncryptionAlgorithm = encoding[offset++];
            this._s2kType = encoding[offset++];
            switch (this._s2kType) {
               case -1:
               case 2:
                  throw new Object(((StringBuffer)(new Object("S2K:"))).append(this._s2kType).toString());
               case 0:
               default:
                  this._hashAlgorithm = encoding[offset++];
                  break;
               case 1:
                  this._hashAlgorithm = encoding[offset++];
                  this._salt = new byte[8];
                  System.arraycopy(encoding, offset, this._salt, 0, 8);
                  offset += 8;
                  break;
               case 3:
                  this._hashAlgorithm = encoding[offset++];
                  this._salt = new byte[8];
                  System.arraycopy(encoding, offset, this._salt, 0, 8);
                  offset += 8;
                  this._count = encoding[offset++];
            }
      }

      if (this._symmetricEncryptionAlgorithm != 0) {
         int blockSize = this.getBlockSize(this._symmetricEncryptionAlgorithm);
         this._iv = new byte[blockSize];
         System.arraycopy(encoding, offset, this._iv, 0, blockSize);
         offset += blockSize;
      }

      int encryptedDataLength = encoding.length - offset;
      this._encryptedSecretKeyData = new byte[encryptedDataLength];
      System.arraycopy(encoding, offset, this._encryptedSecretKeyData, 0, encryptedDataLength);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final PrivateKey getPrivateKey(PGPPasswordTicket ticket) {
      if (ticket == null) {
         throw new Object();
      }

      int publicKeyAlgorithm = this.getPublicKeyAlgorithm();
      PrivateKey privateKey = null;
      PublicKey publicKey = null;
      byte[] x = null;
      byte[] d = null;
      byte[] p = null;
      byte[] q = null;
      if (this._symmetricEncryptionAlgorithm != 0) {
         String algorithmString = PGPUtilities.encryptionConstantToString(this._symmetricEncryptionAlgorithm);
         Digest digest = null;

         label420:
         try {
            String digestAlg = PGPUtilities.digestConstantToString(this._hashAlgorithm);
            digest = DigestFactory.getInstance(digestAlg);
         } finally {
            break label420;
         }

         boolean retry = false;

         while (true) {
            PGPPseudoRandomSource pseudoRandomSource = ticket.getPseudoRandomSource(this._s2kType, this._salt, digest, this._count, retry);

            try {
               SymmetricKey key = PGPUtilities.getSessionKey(this._symmetricEncryptionAlgorithm, pseudoRandomSource);
               SymmetricKeyEncryptorEngine engine = (SymmetricKeyEncryptorEngine)EncryptorFactory.getBlockEncryptorEngine(key, algorithmString);
               int version = this.getVersion();
               if (version != 3) {
                  if (version != 4) {
                     throw new Object(((StringBuffer)(new Object("Ver:"))).append(version).toString());
                  }

                  InitializationVector iv = (InitializationVector)(new Object(this._iv));
                  CFBDecryptor decryptor = (CFBDecryptor)(new Object(engine, iv, (InputStream)(new Object(this._encryptedSecretKeyData)), false));

                  label404:
                  try {
                     int secretKeyDataLength = this._encryptedSecretKeyData.length;
                     byte[] decryptedSecretKeyData = new byte[secretKeyDataLength];
                     int decryptedLength = decryptor.read(decryptedSecretKeyData);
                     if (decryptedLength != secretKeyDataLength) {
                        throw new PGPEncodingException("PrLM");
                     }

                     ByteArrayInputStream decryptedSecretKeyDataStream = (ByteArrayInputStream)(new Object(decryptedSecretKeyData));
                     switch (publicKeyAlgorithm) {
                        case 1:
                        case 2:
                        case 3:
                           d = PGPUtilities.readMPI(decryptedSecretKeyDataStream);
                           p = PGPUtilities.readMPI(decryptedSecretKeyDataStream);
                           q = PGPUtilities.readMPI(decryptedSecretKeyDataStream);
                           PGPUtilities.readMPI(decryptedSecretKeyDataStream);
                           break;
                        case 16:
                        case 17:
                        case 20:
                           x = PGPUtilities.readMPI(decryptedSecretKeyDataStream);
                           break;
                        default:
                           throw new Object(((StringBuffer)(new Object("Pub:"))).append(publicKeyAlgorithm).toString());
                     }

                     if (this.checkRedundancy(decryptedSecretKeyData)) {
                        break;
                     }
                  } finally {
                     break label404;
                  }

                  retry = true;
               } else {
                  ByteArrayInputStream inputStream = (ByteArrayInputStream)(new Object(this._encryptedSecretKeyData));
                  ByteArrayOutputStream checksumStream = (ByteArrayOutputStream)(new Object());

                  label410:
                  try {
                     switch (publicKeyAlgorithm) {
                        case 0:
                           throw new Object(((StringBuffer)(new Object("Pub:"))).append(publicKeyAlgorithm).toString());
                        case 1:
                        case 2:
                        case 3:
                     }

                     byte[] ivData = Arrays.copy(this._iv);
                     d = this.readV3EncryptedMPI(inputStream, engine, ivData, checksumStream);
                     p = this.readV3EncryptedMPI(inputStream, engine, ivData, checksumStream);
                     q = this.readV3EncryptedMPI(inputStream, engine, ivData, checksumStream);
                     this.readV3EncryptedMPI(inputStream, engine, ivData, checksumStream);
                     checksumStream.write(inputStream.read());
                     checksumStream.write(inputStream.read());
                     checksumStream.close();
                     if (this.checkRedundancy(checksumStream.toByteArray())) {
                        break;
                     }
                  } finally {
                     break label410;
                  }

                  retry = true;
               }
            } catch (Throwable var62) {
               throw new PGPEncodingException(e.toString());
            }
         }
      } else {
         label393:
         try {
            ByteArrayInputStream input = (ByteArrayInputStream)(new Object(this._encryptedSecretKeyData));
            switch (publicKeyAlgorithm) {
               case 1:
               case 2:
               case 3:
                  d = PGPUtilities.readMPI(input);
                  p = PGPUtilities.readMPI(input);
                  q = PGPUtilities.readMPI(input);
                  PGPUtilities.readMPI(input);
                  break;
               case 16:
               case 17:
               case 20:
                  x = PGPUtilities.readMPI(input);
                  break;
               default:
                  throw new Object(((StringBuffer)(new Object("Pub:"))).append(publicKeyAlgorithm).toString());
            }

            if (!this.checkRedundancy(this._encryptedSecretKeyData)) {
               throw new PGPEncodingException("PrRd");
            }
         } finally {
            break label393;
         }
      }

      try {
         publicKey = this.getPublicKey();
         switch (publicKeyAlgorithm) {
            case 1:
            case 2:
               RSAPublicKey rsaPublicKey = (RSAPublicKey)publicKey;
               privateKey = (PrivateKey)(new Object(rsaPublicKey.getRSACryptoSystem(), rsaPublicKey.getE(), d, p, q));
               break;
            case 16:
            case 20:
               DHPublicKey dhPublicKey = (DHPublicKey)publicKey;
               privateKey = (PrivateKey)(new Object(dhPublicKey.getDHCryptoSystem(), x));
               break;
            case 17:
               DSAPublicKey dsaPublicKey = (DSAPublicKey)publicKey;
               privateKey = (PrivateKey)(new Object(dsaPublicKey.getDSACryptoSystem(), x));
               break;
            default:
               throw new Object(((StringBuffer)(new Object("Pub:"))).append(publicKeyAlgorithm).toString());
         }

         return privateKey;
      } catch (Throwable var58) {
         throw new PGPEncodingException(e.toString());
      }
   }

   private final boolean checkRedundancy(byte[] data) {
      int version = this.getVersion();
      if (version != 3 && this._s2kUsage == -2) {
         int dataLength = data.length - 20;
         if (dataLength < 0) {
            throw new PGPEncodingException("PrLM");
         }

         SHA1Digest digest = (SHA1Digest)(new Object());
         digest.update(data, 0, dataLength);
         byte[] computedDigest = digest.getDigest();
         byte[] expectedDigest = new byte[20];
         System.arraycopy(data, dataLength, expectedDigest, 0, 20);
         return Arrays.equals(computedDigest, expectedDigest);
      } else {
         int dataLength = data.length - 2;
         if (dataLength < 0) {
            throw new PGPEncodingException("PrLM");
         }

         int computedChecksum = 0;

         for (int i = 0; i < dataLength; i++) {
            computedChecksum += data[i] & 255;
         }

         computedChecksum &= 65535;
         int expectedChecksum = (data[dataLength] & 255) << 8 | data[dataLength + 1] & 255;
         return computedChecksum == expectedChecksum;
      }
   }

   private final int getBlockSize(int algorithm) {
      switch (algorithm) {
         case 1:
         case 4:
         case 5:
         case 6:
            throw new Object(((StringBuffer)(new Object("Sym:"))).append(algorithm).toString());
         case 2:
         case 3:
         default:
            return 8;
         case 7:
         case 8:
         case 9:
            return 16;
      }
   }

   public final int getSymmetricKeyAlgorithm() {
      return this._symmetricEncryptionAlgorithm;
   }

   public final PGPPublicKeyPacket getPublicKeyPacket() {
      int privateKeyTag = this.getTag();
      int publicKeyTag;
      switch (privateKeyTag) {
         case 5:
            publicKeyTag = 6;
            break;
         case 7:
            publicKeyTag = 14;
            break;
         default:
            throw new Object(((StringBuffer)(new Object("Tag:"))).append(privateKeyTag).toString());
      }

      return new PGPPublicKeyPacket(publicKeyTag, this.getEncoding());
   }

   private final byte[] readV3EncryptedMPI(InputStream inputStream, SymmetricKeyEncryptorEngine engine, byte[] ivData, ByteArrayOutputStream checksumStream) {
      byte[] encodedLength = new byte[2];
      byte[] encryptedMPIData = PGPUtilities.readMPI(inputStream, encodedLength);
      InitializationVector iv = (InitializationVector)(new Object(ivData));
      CFBDecryptor mpiDecryptor = (CFBDecryptor)(new Object(engine, iv, (InputStream)(new Object(encryptedMPIData)), false));
      byte[] decryptedMPIData = new byte[encryptedMPIData.length];
      mpiDecryptor.read(decryptedMPIData);
      checksumStream.write(encodedLength);
      checksumStream.write(decryptedMPIData);
      System.arraycopy(encryptedMPIData, encryptedMPIData.length - ivData.length, ivData, 0, ivData.length);
      return decryptedMPIData;
   }
}
