package net.rim.device.internal.crypto.pgp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Vector;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SignatureVerifier;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.device.api.crypto.pgp.PGPVerificationException;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class PGPSignaturePacket extends PGPPacket implements Persistable {
   private int _version;
   private byte[] _toBeHashed;
   private byte[] _checkValue;
   private int _signatureType;
   private long _creationTime;
   private byte[] _signerKeyID;
   private int _publicKeyAlgorithm;
   private int _hashAlgorithm;
   private byte[] _ephemeralKey;
   private byte[] _signature;
   private Vector _signatureSubPackets;
   private boolean _containsX509Cert;
   private PGPSignatureSubPacket _x509CertPacket;
   public static final int GENERIC_CERTIFICATION_OF_USERID_AND_PUB_KEY_PACKET = 16;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public PGPSignaturePacket(int tag, byte[] encoding) {
      super(tag, encoding);
      int offset = 0;
      this._signatureSubPackets = (Vector)(new Object());
      this._version = encoding[offset++] & 255;
      switch (this._version) {
         case 2:
            throw new Object(((StringBuffer)(new Object("Ver:"))).append(this._version).toString());
         case 3:
         default:
            int hashLength = encoding[offset++] & 255;
            if (hashLength != 5) {
               throw new PGPEncodingException("SgLM");
            }

            this._signatureType = encoding[offset++] & 255;
            this._creationTime = PGPUtilities.convertTime(encoding, offset);
            offset += 4;
            this._signerKeyID = new byte[8];
            System.arraycopy(encoding, offset, this._signerKeyID, 0, 8);
            offset += 8;
            this._publicKeyAlgorithm = encoding[offset++] & 255;
            this._hashAlgorithm = encoding[offset++] & 255;
            this._toBeHashed = new byte[5];
            System.arraycopy(encoding, 2, this._toBeHashed, 0, 5);
            this._checkValue = new byte[2];
            this._checkValue[0] = encoding[offset++];
            this._checkValue[1] = encoding[offset++];
            boolean var15 = false /* VF: Semaphore variable */;

            try {
               var15 = true;
               ByteArrayInputStream var38 = new Object(encoding, offset, encoding.length - offset);
               if (this._publicKeyAlgorithm == 17) {
                  this._ephemeralKey = PGPUtilities.readMPI((InputStream)var38);
                  this._signature = PGPUtilities.readMPI((InputStream)var38);
                  var15 = false;
               } else if (this._publicKeyAlgorithm != 1) {
                  var15 = false;
               } else {
                  this._signature = PGPUtilities.readMPI((InputStream)var38);
                  var15 = false;
               }
               break;
            } finally {
               if (var15) {
                  throw new PGPEncodingException("RMIO");
               }
            }
         case 4:
            this._signatureType = encoding[offset++] & 255;
            this._publicKeyAlgorithm = encoding[offset++] & 255;
            this._hashAlgorithm = encoding[offset++] & 255;
            int hashedSubPacketLength = (encoding[offset++] & 255) << 8;
            hashedSubPacketLength |= encoding[offset++] & 255;
            int hashLength = 6 + hashedSubPacketLength;
            this._toBeHashed = new byte[hashLength];
            System.arraycopy(encoding, 0, this._toBeHashed, 0, hashLength);
            int hashedSubPacketEnd = offset + hashedSubPacketLength;

            while (offset < hashedSubPacketEnd) {
               offset += this.readSignatureSubPacket(encoding, offset, hashedSubPacketEnd - offset);
            }

            int unhashedSubPacketLength = (encoding[offset++] & 255) << 8;
            unhashedSubPacketLength |= encoding[offset++] & 255;
            int unhashedSubPacketEnd = offset + unhashedSubPacketLength;

            while (offset < unhashedSubPacketEnd) {
               offset += this.readSignatureSubPacket(encoding, offset, unhashedSubPacketEnd - offset);
            }

            this._checkValue = new byte[2];
            this._checkValue[0] = encoding[offset++];
            this._checkValue[1] = encoding[offset++];
            boolean var12 = false /* VF: Semaphore variable */;

            try {
               var12 = true;
               ByteArrayInputStream e = new Object(encoding, offset, encoding.length - offset);
               if (this._publicKeyAlgorithm == 17) {
                  this._ephemeralKey = PGPUtilities.readMPI((InputStream)e);
                  this._signature = PGPUtilities.readMPI((InputStream)e);
                  var12 = false;
               } else if (this._publicKeyAlgorithm == 1) {
                  this._signature = PGPUtilities.readMPI((InputStream)e);
                  var12 = false;
               } else {
                  var12 = false;
               }
            } finally {
               if (var12) {
                  throw new PGPEncodingException("RMIO");
               }
            }
      }

      this.checkForX509Certificate();
   }

   private final int readSignatureSubPacket(byte[] encoding, int offset, int length) {
      int subPacketLength = 0;
      int startOffset = offset;
      int temp = encoding[offset++] & 255;
      if (temp >= 0 && temp < 192) {
         subPacketLength = temp;
      } else if (temp >= 192 && temp < 255) {
         subPacketLength = temp - 192 << 8;
         subPacketLength += encoding[offset++] & 255;
         subPacketLength += 192;
      } else {
         if (temp != 255) {
            throw new PGPEncodingException("InvL");
         }

         subPacketLength = (encoding[offset++] & 255) << 24;
         subPacketLength |= (encoding[offset++] & 255) << 16;
         subPacketLength |= (encoding[offset++] & 255) << 8;
         subPacketLength |= encoding[offset++] & 255;
      }

      if (offset - startOffset + subPacketLength > length) {
         throw new PGPEncodingException("SgLM");
      }

      int subPacketTag = encoding[offset] & 255;
      byte[] subPacketData = new byte[subPacketLength - 1];
      System.arraycopy(encoding, offset + 1, subPacketData, 0, subPacketData.length);
      offset += subPacketLength;
      if (subPacketTag == 16) {
         if (subPacketLength < 8) {
            throw new PGPEncodingException("SgLM");
         }

         this._signerKeyID = new byte[8];
         System.arraycopy(subPacketData, 0, this._signerKeyID, 0, 8);
      }

      this._signatureSubPackets.addElement(new PGPSignatureSubPacket(subPacketTag, subPacketData));
      return offset - startOffset;
   }

   public final byte[] getDataToBeHashed() {
      return this._toBeHashed;
   }

   public final byte[] getCheckValue() {
      return this._checkValue;
   }

   public final int getSignatureType() {
      return this._signatureType;
   }

   public final long getCreationTime() {
      return this._creationTime;
   }

   public final byte[] getSignerKeyID() {
      return this._signerKeyID;
   }

   public final int getPublicKeyAlgorithm() {
      return this._publicKeyAlgorithm;
   }

   public final int getHashAlgorithm() {
      return this._hashAlgorithm;
   }

   public final byte[] getEphemeralKey() {
      return this._ephemeralKey;
   }

   public final byte[] getSignature() {
      return this._signature;
   }

   public final Vector getSignatureSubPackets() {
      return this._signatureSubPackets;
   }

   public final int getVersion() {
      return this._version;
   }

   private final SignatureVerifier getSignatureVerifier(PublicKey param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnonnull 0c
      // 04: new java/lang/Object
      // 07: dup
      // 08: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0b: athrow
      // 0c: aload 0
      // 0d: getfield net/rim/device/internal/crypto/pgp/PGPSignaturePacket._hashAlgorithm I
      // 10: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.digestConstantToString (I)Ljava/lang/String;
      // 13: astore 2
      // 14: aload 2
      // 15: invokestatic net/rim/device/api/crypto/DigestFactory.getInstance (Ljava/lang/String;)Lnet/rim/device/api/crypto/Digest;
      // 18: astore 3
      // 19: aload 0
      // 1a: getfield net/rim/device/internal/crypto/pgp/PGPSignaturePacket._publicKeyAlgorithm I
      // 1d: lookupswitch 68 2 1 50 17 27
      // 38: new java/lang/Object
      // 3b: dup
      // 3c: aload 1
      // 3d: checkcast java/lang/Object
      // 40: aload 3
      // 41: aload 0
      // 42: getfield net/rim/device/internal/crypto/pgp/PGPSignaturePacket._ephemeralKey [B
      // 45: bipush 0
      // 46: aload 0
      // 47: getfield net/rim/device/internal/crypto/pgp/PGPSignaturePacket._signature [B
      // 4a: bipush 0
      // 4b: invokespecial net/rim/device/api/crypto/DSASignatureVerifier.<init> (Lnet/rim/device/api/crypto/DSAPublicKey;Lnet/rim/device/api/crypto/Digest;[BI[BI)V
      // 4e: areturn
      // 4f: new java/lang/Object
      // 52: dup
      // 53: aload 1
      // 54: checkcast java/lang/Object
      // 57: aload 3
      // 58: aload 0
      // 59: getfield net/rim/device/internal/crypto/pgp/PGPSignaturePacket._signature [B
      // 5c: bipush 0
      // 5d: invokespecial net/rim/device/api/crypto/PKCS1SignatureVerifier.<init> (Lnet/rim/device/api/crypto/RSAPublicKey;Lnet/rim/device/api/crypto/Digest;[BI)V
      // 60: areturn
      // 61: new java/lang/Object
      // 64: dup
      // 65: new java/lang/Object
      // 68: dup
      // 69: ldc_w "Pub:"
      // 6c: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 6f: aload 0
      // 70: getfield net/rim/device/internal/crypto/pgp/PGPSignaturePacket._publicKeyAlgorithm I
      // 73: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 76: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 79: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> (Ljava/lang/String;)V
      // 7c: athrow
      // 7d: astore 2
      // 7e: new net/rim/device/api/crypto/pgp/PGPVerificationException
      // 81: dup
      // 82: aload 2
      // 83: invokevirtual net/rim/device/api/crypto/CryptoException.toString ()Ljava/lang/String;
      // 86: invokespecial net/rim/device/api/crypto/pgp/PGPVerificationException.<init> (Ljava/lang/String;)V
      // 89: athrow
      // 8a: astore 2
      // 8b: new net/rim/device/api/crypto/pgp/PGPVerificationException
      // 8e: dup
      // 8f: ldc_w "SgAM"
      // 92: invokespecial net/rim/device/api/crypto/pgp/PGPVerificationException.<init> (Ljava/lang/String;)V
      // 95: athrow
      // try (6 -> 28): 51 null
      // try (29 -> 38): 51 null
      // try (39 -> 51): 51 null
      // try (6 -> 28): 58 null
      // try (29 -> 38): 58 null
      // try (39 -> 51): 58 null
   }

   public final void verify(PGPPublicKeyPacket parentKey, PublicKey verificationKey, PGPUserIDPacket userID) {
      this.verify(parentKey, null, verificationKey, userID);
   }

   public final void verify(PGPPublicKeyPacket parentKey, PGPUserIDPacket userID) {
      this.verify(parentKey, null, parentKey.getPublicKey(), userID);
   }

   public final void verify(PGPPublicKeyPacket parentKey, PGPPublicKeyPacket keyToHash) {
      this.verify(parentKey, keyToHash, parentKey.getPublicKey(), null);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void verify(PGPPublicKeyPacket parentKey, PGPPublicKeyPacket keyToHash, PublicKey verificationKey, PGPUserIDPacket userID) {
      if (parentKey == null) {
         throw new Object();
      }

      if (keyToHash == null) {
         keyToHash = parentKey;
      }

      try {
         SignatureVerifier verifier = this.getSignatureVerifier(verificationKey);
         switch (this._signatureType) {
            case 32:
               if (!Arrays.equals(this._signerKeyID, parentKey.getKeyID())) {
                  throw new PGPVerificationException("SgIK");
               }
               break;
            case 40:
               if (!Arrays.equals(this._signerKeyID, parentKey.getKeyID())) {
                  throw new PGPVerificationException("SgIK");
               }
            case 24:
               if (keyToHash == null) {
                  throw new PGPVerificationException("SgIS");
               }

               verifier.update(153);
               int parentKeyLength = parentKey.getPublicKeyLength();
               verifier.update(parentKeyLength >> 8 & 0xFF);
               verifier.update(parentKeyLength & 0xFF);
               verifier.update(parentKey.getEncoding(), 0, parentKeyLength);
         }

         verifier.update(153);
         int keyToHashLength = keyToHash.getPublicKeyLength();
         verifier.update(keyToHashLength >> 8 & 0xFF);
         verifier.update(keyToHashLength & 0xFF);
         verifier.update(keyToHash.getEncoding(), 0, keyToHashLength);
         switch (this._signatureType) {
            case 15:
               break;
            case 16:
            case 17:
            case 18:
            case 19:
            default:
               if (userID == null) {
                  throw new PGPVerificationException("SgIU");
               }

               byte[] userIDBytes = userID.getEncoding();
               if (this._version == 4) {
                  verifier.update(180);
                  int userIDLength = userIDBytes.length;
                  verifier.update(userIDLength >> 24 & 0xFF);
                  verifier.update(userIDLength >> 16 & 0xFF);
                  verifier.update(userIDLength >> 8 & 0xFF);
                  verifier.update(userIDLength & 0xFF);
               }

               verifier.update(userIDBytes);
         }

         verifier.update(this._toBeHashed);
         if (this._version == 4) {
            verifier.update(this._version);
            verifier.update(255);
            int toBeHashedLength = this._toBeHashed.length;
            verifier.update(toBeHashedLength >> 24 & 0xFF);
            verifier.update(toBeHashedLength >> 16 & 0xFF);
            verifier.update(toBeHashedLength >> 8 & 0xFF);
            verifier.update(toBeHashedLength & 0xFF);
         }

         if (verifier.verify()) {
            return;
         }
      } catch (Throwable var10) {
         throw new PGPVerificationException(e.toString());
      }

      throw new PGPVerificationException("SgVF");
   }

   private final void checkForX509Certificate() {
      this._containsX509Cert = false;
      if (this._version == 4 && this._signatureType == 16) {
         PGPSignatureSubPacket currSubPacket = null;

         for (int i = 0; i < this._signatureSubPackets.size(); i++) {
            currSubPacket = (PGPSignatureSubPacket)this._signatureSubPackets.elementAt(i);
            if (currSubPacket.getTag() == 100) {
               byte minorVersion = currSubPacket.getEncoding()[2];
               if (minorVersion >= 0 && minorVersion <= 3 && this._publicKeyAlgorithm == 0) {
                  this._containsX509Cert = true;
                  this._x509CertPacket = currSubPacket;
                  return;
               }

               if (minorVersion == 4 && this._publicKeyAlgorithm == 100) {
                  this._containsX509Cert = true;
                  this._x509CertPacket = currSubPacket;
                  return;
               }
            }
         }
      }
   }

   public final boolean containsX509Certificate() {
      return this._x509CertPacket == null ? false : this._containsX509Cert;
   }

   public final Certificate extractX509Certificate() {
      if (this._containsX509Cert && this._x509CertPacket != null) {
         byte[] certData = new byte[this._x509CertPacket.getEncoding().length - 3];
         System.arraycopy(this._x509CertPacket.getEncoding(), 3, certData, 0, certData.length);

         try {
            Certificate embeddedCert = (Certificate)(new Object(certData));
            return embeddedCert;
         } finally {
            ;
         }
      } else {
         return null;
      }
   }
}
