package net.rim.device.api.crypto.pgp;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.certificate.pgp.PGPKeyIDKeyStoreIndex;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.crypto.pgp.PGPSignaturePacket;
import net.rim.device.internal.crypto.pgp.PGPUtilities;

public final class PGPSignedInputStream extends PGPInputStream {
   private byte[][] _signerKeyIDs;
   private boolean[] _signatureVerified;
   private Exception[] _signatureException;
   private byte[] _dataBuffer;
   private boolean _isClearSigned;
   private Vector _signaturePackets;
   private boolean _displayUI;

   PGPSignedInputStream(InputStream input, KeyStore keyStore, Vector signaturePackets, byte[] encoding) {
      this(input, keyStore, signaturePackets, encoding, true, true);
   }

   PGPSignedInputStream(InputStream input, KeyStore keyStore, Vector signaturePackets, byte[] encoding, boolean isClearSigned, boolean displayUI) {
      super(input, keyStore);
      this._signaturePackets = signaturePackets;
      this._isClearSigned = isClearSigned;
      this._displayUI = displayUI;

      label42:
      try {
         this.setData(super._input, encoding);
      } finally {
         break label42;
      }

      int numSignaturePackets = signaturePackets != null ? signaturePackets.size() : 0;
      this._signerKeyIDs = new byte[numSignaturePackets][];

      for (int i = 0; i < numSignaturePackets; i++) {
         PGPSignaturePacket packet = (PGPSignaturePacket)signaturePackets.elementAt(i);
         this._signerKeyIDs[i] = packet.getSignerKeyID();
      }

      this._signatureVerified = new boolean[numSignaturePackets];
      this._signatureException = new Object[numSignaturePackets];
   }

   private final int getSignerIndex(byte[] signerKeyID) throws PGPVerificationException {
      int numSigners = this._signerKeyIDs.length;

      for (int i = 0; i < numSigners; i++) {
         if (Arrays.equals(this._signerKeyIDs[i], signerKeyID)) {
            return i;
         }
      }

      throw new PGPVerificationException("NSID");
   }

   public final Certificate getSignerCertificate(byte[] signerKeyID) {
      this.getSignerIndex(signerKeyID);
      super._keyStore.addIndex(new PGPKeyIDKeyStoreIndex());
      Enumeration keys = super._keyStore.elements(-2737350786039236692L, signerKeyID);

      while (keys.hasMoreElements()) {
         KeyStoreData keyStoreData = (KeyStoreData)keys.nextElement();
         Certificate certificate = keyStoreData.getCertificate();
         if (certificate instanceof PGPCertificate) {
            return certificate;
         }
      }

      return null;
   }

   public final void verify(byte[] signerKeyID) throws PGPNoKeyFoundException {
      int signerIndex = this.getSignerIndex(signerKeyID);
      if (!this._signatureVerified[signerIndex]) {
         Exception lastException = this._signatureException[signerIndex];
         if (!(lastException instanceof Object)) {
            if (!(lastException instanceof Object)) {
               Certificate certificate = this.getSignerCertificate(signerKeyID);
               if (certificate == null) {
                  throw new PGPNoKeyFoundException();
               }

               this.verify(signerKeyID, certificate);
            } else {
               throw (Object)lastException;
            }
         } else {
            throw (Object)lastException;
         }
      }
   }

   public final void verify(byte[] param1, Certificate param2) throws CryptoTokenException, CryptoUnsupportedOperationException, NoSuchAlgorithmException, PGPEncodingException, PGPVerificationException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 1
      // 002: invokespecial net/rim/device/api/crypto/pgp/PGPSignedInputStream.getSignerIndex ([B)I
      // 005: istore 3
      // 006: aload 0
      // 007: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._signaturePackets Ljava/util/Vector;
      // 00a: iload 3
      // 00b: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 00e: checkcast net/rim/device/internal/crypto/pgp/PGPSignaturePacket
      // 011: astore 4
      // 013: aload 2
      // 014: checkcast net/rim/device/api/crypto/certificate/pgp/PGPCertificate
      // 017: astore 5
      // 019: aload 5
      // 01b: aload 1
      // 01c: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getPublicKey ([B)Lnet/rim/device/api/crypto/PublicKey;
      // 01f: astore 6
      // 021: aload 4
      // 023: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getHashAlgorithm ()I
      // 026: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.digestConstantToString (I)Ljava/lang/String;
      // 029: astore 7
      // 02b: aload 7
      // 02d: invokestatic net/rim/device/api/crypto/DigestFactory.getInstance (Ljava/lang/String;)Lnet/rim/device/api/crypto/Digest;
      // 030: astore 8
      // 032: aload 7
      // 034: invokestatic net/rim/device/api/crypto/DigestFactory.getInstance (Ljava/lang/String;)Lnet/rim/device/api/crypto/Digest;
      // 037: astore 9
      // 039: aconst_null
      // 03a: astore 10
      // 03c: aconst_null
      // 03d: astore 11
      // 03f: aload 6
      // 041: instanceof java/lang/Object
      // 044: ifeq 082
      // 047: new java/lang/Object
      // 04a: dup
      // 04b: aload 6
      // 04d: checkcast java/lang/Object
      // 050: aload 8
      // 052: aload 4
      // 054: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getEphemeralKey ()[B
      // 057: bipush 0
      // 058: aload 4
      // 05a: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getSignature ()[B
      // 05d: bipush 0
      // 05e: invokespecial net/rim/device/api/crypto/DSASignatureVerifier.<init> (Lnet/rim/device/api/crypto/DSAPublicKey;Lnet/rim/device/api/crypto/Digest;[BI[BI)V
      // 061: astore 10
      // 063: new java/lang/Object
      // 066: dup
      // 067: aload 6
      // 069: checkcast java/lang/Object
      // 06c: aload 9
      // 06e: aload 4
      // 070: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getEphemeralKey ()[B
      // 073: bipush 0
      // 074: aload 4
      // 076: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getSignature ()[B
      // 079: bipush 0
      // 07a: invokespecial net/rim/device/api/crypto/DSASignatureVerifier.<init> (Lnet/rim/device/api/crypto/DSAPublicKey;Lnet/rim/device/api/crypto/Digest;[BI[BI)V
      // 07d: astore 11
      // 07f: goto 0e7
      // 082: aload 6
      // 084: instanceof java/lang/Object
      // 087: ifeq 0b9
      // 08a: new java/lang/Object
      // 08d: dup
      // 08e: aload 6
      // 090: checkcast java/lang/Object
      // 093: aload 8
      // 095: aload 4
      // 097: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getSignature ()[B
      // 09a: bipush 0
      // 09b: invokespecial net/rim/device/api/crypto/PKCS1SignatureVerifier.<init> (Lnet/rim/device/api/crypto/RSAPublicKey;Lnet/rim/device/api/crypto/Digest;[BI)V
      // 09e: astore 10
      // 0a0: new java/lang/Object
      // 0a3: dup
      // 0a4: aload 6
      // 0a6: checkcast java/lang/Object
      // 0a9: aload 9
      // 0ab: aload 4
      // 0ad: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getSignature ()[B
      // 0b0: bipush 0
      // 0b1: invokespecial net/rim/device/api/crypto/PKCS1SignatureVerifier.<init> (Lnet/rim/device/api/crypto/RSAPublicKey;Lnet/rim/device/api/crypto/Digest;[BI)V
      // 0b4: astore 11
      // 0b6: goto 0e7
      // 0b9: aload 6
      // 0bb: ifnull 0c8
      // 0be: aload 6
      // 0c0: invokeinterface net/rim/device/api/crypto/Key.getAlgorithm ()Ljava/lang/String; 1
      // 0c5: goto 0cb
      // 0c8: ldc_w "Key ID not found"
      // 0cb: astore 12
      // 0cd: new java/lang/Object
      // 0d0: dup
      // 0d1: new java/lang/Object
      // 0d4: dup
      // 0d5: ldc_w "Pub:"
      // 0d8: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0db: aload 12
      // 0dd: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0e0: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0e3: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> (Ljava/lang/String;)V
      // 0e6: athrow
      // 0e7: bipush 0
      // 0e8: istore 12
      // 0ea: aload 4
      // 0ec: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getSignatureType ()I
      // 0ef: bipush 1
      // 0f0: if_icmpne 129
      // 0f3: aload 0
      // 0f4: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._isClearSigned Z
      // 0f7: ifeq 129
      // 0fa: aload 0
      // 0fb: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 0fe: bipush 0
      // 0ff: aload 0
      // 100: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 103: arraylength
      // 104: invokestatic net/rim/device/api/crypto/pgp/PGPSignedInputStream.convertLineEndings ([BII)[B
      // 107: astore 13
      // 109: aload 10
      // 10b: aload 13
      // 10d: bipush 0
      // 10e: aload 13
      // 110: arraylength
      // 111: invokeinterface net/rim/device/api/crypto/SignatureVerifier.update ([BII)V 4
      // 116: aload 11
      // 118: aload 13
      // 11a: bipush 0
      // 11b: aload 13
      // 11d: arraylength
      // 11e: invokestatic net/rim/device/api/crypto/pgp/PGPSignedInputStream.stripSpaces ([BII)[B
      // 121: invokeinterface net/rim/device/api/crypto/SignatureVerifier.update ([B)V 2
      // 126: goto 13f
      // 129: aload 10
      // 12b: aload 0
      // 12c: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 12f: invokeinterface net/rim/device/api/crypto/SignatureVerifier.update ([B)V 2
      // 134: aload 11
      // 136: aload 0
      // 137: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 13a: invokeinterface net/rim/device/api/crypto/SignatureVerifier.update ([B)V 2
      // 13f: aload 4
      // 141: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getDataToBeHashed ()[B
      // 144: astore 13
      // 146: aload 10
      // 148: aload 13
      // 14a: invokeinterface net/rim/device/api/crypto/SignatureVerifier.update ([B)V 2
      // 14f: aload 11
      // 151: aload 13
      // 153: invokeinterface net/rim/device/api/crypto/SignatureVerifier.update ([B)V 2
      // 158: aload 13
      // 15a: arraylength
      // 15b: istore 12
      // 15d: aload 4
      // 15f: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getVersion ()I
      // 162: bipush 4
      // 164: if_icmpne 1b4
      // 167: bipush 6
      // 169: newarray 8
      // 16b: astore 14
      // 16d: aload 14
      // 16f: bipush 0
      // 170: bipush 4
      // 172: bastore
      // 173: aload 14
      // 175: bipush 1
      // 176: bipush -1
      // 178: bastore
      // 179: aload 14
      // 17b: bipush 2
      // 17d: iload 12
      // 17f: bipush 24
      // 181: ishr
      // 182: i2b
      // 183: bastore
      // 184: aload 14
      // 186: bipush 3
      // 188: iload 12
      // 18a: bipush 16
      // 18c: ishr
      // 18d: i2b
      // 18e: bastore
      // 18f: aload 14
      // 191: bipush 4
      // 193: iload 12
      // 195: bipush 8
      // 197: ishr
      // 198: i2b
      // 199: bastore
      // 19a: aload 14
      // 19c: bipush 5
      // 19e: iload 12
      // 1a0: i2b
      // 1a1: bastore
      // 1a2: aload 10
      // 1a4: aload 14
      // 1a6: invokeinterface net/rim/device/api/crypto/SignatureVerifier.update ([B)V 2
      // 1ab: aload 11
      // 1ad: aload 14
      // 1af: invokeinterface net/rim/device/api/crypto/SignatureVerifier.update ([B)V 2
      // 1b4: aload 8
      // 1b6: bipush 0
      // 1b7: invokeinterface net/rim/device/api/crypto/Digest.getDigest (Z)[B 2
      // 1bc: astore 14
      // 1be: aload 9
      // 1c0: bipush 0
      // 1c1: invokeinterface net/rim/device/api/crypto/Digest.getDigest (Z)[B 2
      // 1c6: astore 15
      // 1c8: aload 14
      // 1ca: bipush 0
      // 1cb: aload 4
      // 1cd: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getCheckValue ()[B
      // 1d0: bipush 0
      // 1d1: bipush 2
      // 1d3: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 1d6: ifeq 1eb
      // 1d9: aload 10
      // 1db: invokeinterface net/rim/device/api/crypto/SignatureVerifier.verify ()Z 1
      // 1e0: ifeq 250
      // 1e3: aload 0
      // 1e4: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._signatureVerified [Z
      // 1e7: iload 3
      // 1e8: bipush 1
      // 1e9: bastore
      // 1ea: return
      // 1eb: aload 15
      // 1ed: bipush 0
      // 1ee: aload 4
      // 1f0: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getCheckValue ()[B
      // 1f3: bipush 0
      // 1f4: bipush 2
      // 1f6: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 1f9: ifeq 250
      // 1fc: aload 11
      // 1fe: invokeinterface net/rim/device/api/crypto/SignatureVerifier.verify ()Z 1
      // 203: ifeq 250
      // 206: aload 0
      // 207: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._signatureVerified [Z
      // 20a: iload 3
      // 20b: bipush 1
      // 20c: bastore
      // 20d: return
      // 20e: astore 6
      // 210: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // 213: dup
      // 214: aload 6
      // 216: invokevirtual net/rim/device/api/crypto/InvalidSignatureEncodingException.toString ()Ljava/lang/String;
      // 219: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // 21c: astore 7
      // 21e: aload 0
      // 21f: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._signatureException [Ljava/lang/Exception;
      // 222: iload 3
      // 223: aload 7
      // 225: aastore
      // 226: aload 7
      // 228: athrow
      // 229: astore 6
      // 22b: aload 0
      // 22c: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._signatureException [Ljava/lang/Exception;
      // 22f: iload 3
      // 230: aload 6
      // 232: aastore
      // 233: aload 6
      // 235: athrow
      // 236: astore 6
      // 238: aload 0
      // 239: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._signatureException [Ljava/lang/Exception;
      // 23c: iload 3
      // 23d: aload 6
      // 23f: aastore
      // 240: aload 6
      // 242: athrow
      // 243: astore 6
      // 245: aload 0
      // 246: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._signatureException [Ljava/lang/Exception;
      // 249: iload 3
      // 24a: aload 6
      // 24c: aastore
      // 24d: aload 6
      // 24f: athrow
      // 250: new net/rim/device/api/crypto/pgp/PGPVerificationException
      // 253: dup
      // 254: invokespecial net/rim/device/api/crypto/pgp/PGPVerificationException.<init> ()V
      // 257: athrow
      // try (13 -> 225): 243 null
      // try (226 -> 242): 243 null
      // try (13 -> 225): 257 null
      // try (226 -> 242): 257 null
      // try (13 -> 225): 265 null
      // try (226 -> 242): 265 null
      // try (13 -> 225): 273 null
      // try (226 -> 242): 273 null
   }

   public final String getSignatureDigestName(byte[] signerKeyID) {
      try {
         int signerIndex = this.getSignerIndex(signerKeyID);
         PGPSignaturePacket packet = (PGPSignaturePacket)this._signaturePackets.elementAt(signerIndex);
         return PGPUtilities.digestConstantToString(packet.getHashAlgorithm());
      } catch (PGPVerificationException e) {
         return null;
      } finally {
         ;
      }
   }

   public final byte[][] getSignerKeyIDs() {
      return this._signerKeyIDs;
   }

   public final long getSignatureCreationTime(byte[] signerKeyID) {
      int signerIndex = this.getSignerIndex(signerKeyID);
      PGPSignaturePacket signerPacket = (PGPSignaturePacket)this._signaturePackets.elementAt(signerIndex);
      return signerPacket.getCreationTime();
   }

   public final void setData(InputStream data) {
      this.setData(data, null);
   }

   private final void setData(InputStream param1, byte[] param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnonnull 00c
      // 004: new java/lang/Object
      // 007: dup
      // 008: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 00b: athrow
      // 00c: bipush 0
      // 00d: istore 3
      // 00e: aload 0
      // 00f: bipush 100
      // 011: newarray 8
      // 013: putfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 016: aload 1
      // 017: aload 0
      // 018: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 01b: iload 3
      // 01c: bipush 100
      // 01e: invokevirtual java/io/InputStream.read ([BII)I
      // 021: istore 4
      // 023: iload 4
      // 025: bipush -1
      // 027: if_icmpne 02d
      // 02a: bipush 0
      // 02b: istore 4
      // 02d: iload 4
      // 02f: bipush 100
      // 031: if_icmpge 068
      // 034: iload 3
      // 035: iload 4
      // 037: iadd
      // 038: newarray 8
      // 03a: astore 5
      // 03c: aload 0
      // 03d: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 040: bipush 0
      // 041: aload 5
      // 043: bipush 0
      // 044: iload 3
      // 045: iload 4
      // 047: iadd
      // 048: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 04b: aload 0
      // 04c: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 04f: iload 3
      // 050: iload 4
      // 052: iadd
      // 053: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 056: aload 5
      // 058: bipush 0
      // 059: aload 0
      // 05a: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 05d: bipush 0
      // 05e: iload 3
      // 05f: iload 4
      // 061: iadd
      // 062: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 065: goto 07d
      // 068: aload 0
      // 069: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 06c: aload 0
      // 06d: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 070: arraylength
      // 071: bipush 100
      // 073: iadd
      // 074: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 077: iinc 3 100
      // 07a: goto 016
      // 07d: aload 2
      // 07e: ifnull 09b
      // 081: aload 0
      // 082: new java/lang/Object
      // 085: dup
      // 086: aload 2
      // 087: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 08a: aload 0
      // 08b: getfield net/rim/device/api/crypto/pgp/PGPInputStream._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 08e: aload 0
      // 08f: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._displayUI Z
      // 092: invokestatic net/rim/device/api/crypto/pgp/PGPInputStream.getPGPInputStream (Ljava/io/InputStream;Lnet/rim/device/api/crypto/keystore/KeyStore;Z)Lnet/rim/device/api/crypto/pgp/PGPInputStream;
      // 095: putfield net/rim/device/api/crypto/pgp/PGPInputStream._input Ljava/io/InputStream;
      // 098: goto 0aa
      // 09b: aload 0
      // 09c: new java/lang/Object
      // 09f: dup
      // 0a0: aload 0
      // 0a1: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 0a4: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 0a7: putfield net/rim/device/api/crypto/pgp/PGPInputStream._input Ljava/io/InputStream;
      // 0aa: aload 0
      // 0ab: getfield net/rim/device/api/crypto/pgp/PGPInputStream._input Ljava/io/InputStream;
      // 0ae: ifnonnull 108
      // 0b1: aload 0
      // 0b2: new java/lang/Object
      // 0b5: dup
      // 0b6: aload 0
      // 0b7: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 0ba: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 0bd: putfield net/rim/device/api/crypto/pgp/PGPInputStream._input Ljava/io/InputStream;
      // 0c0: return
      // 0c1: astore 4
      // 0c3: aload 0
      // 0c4: new java/lang/Object
      // 0c7: dup
      // 0c8: aload 0
      // 0c9: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 0cc: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 0cf: putfield net/rim/device/api/crypto/pgp/PGPInputStream._input Ljava/io/InputStream;
      // 0d2: return
      // 0d3: astore 4
      // 0d5: aload 0
      // 0d6: new java/lang/Object
      // 0d9: dup
      // 0da: aload 0
      // 0db: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 0de: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 0e1: putfield net/rim/device/api/crypto/pgp/PGPInputStream._input Ljava/io/InputStream;
      // 0e4: return
      // 0e5: astore 4
      // 0e7: aload 0
      // 0e8: new java/lang/Object
      // 0eb: dup
      // 0ec: aload 0
      // 0ed: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 0f0: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 0f3: putfield net/rim/device/api/crypto/pgp/PGPInputStream._input Ljava/io/InputStream;
      // 0f6: return
      // 0f7: astore 4
      // 0f9: aload 0
      // 0fa: new java/lang/Object
      // 0fd: dup
      // 0fe: aload 0
      // 0ff: getfield net/rim/device/api/crypto/pgp/PGPSignedInputStream._dataBuffer [B
      // 102: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 105: putfield net/rim/device/api/crypto/pgp/PGPInputStream._input Ljava/io/InputStream;
      // 108: return
      // try (67 -> 98): 99 null
      // try (67 -> 98): 108 null
      // try (67 -> 98): 117 net/rim/device/api/crypto/pgp/PGPException
      // try (67 -> 98): 126 null
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      if (b != null && off >= 0 && len >= 0 && b.length - len >= off) {
         return super._input.read(b, off, len);
      } else {
         throw new Object();
      }
   }

   @Override
   public final int available() {
      return super._input.available();
   }

   @Override
   public final long skip(long n) {
      return super._input.skip(n);
   }

   @Override
   public final void close() {
      super._input.close();
   }

   @Override
   public final String getType() {
      return "Signed";
   }

   public static final byte[] stripSpaces(byte[] input, int offset, int length) {
      byte CR = 13;
      byte LF = 10;
      if (input == null) {
         return null;
      }

      if (offset >= 0 && length >= 0 && input.length - length >= offset) {
         int realLength = 0;
         int start = 0;
         boolean foundCR = false;
         byte[] result = new byte[length];

         for (int i = offset; i < length + offset; i++) {
            if (input[i] == CR) {
               result[realLength++] = input[i];
               foundCR = true;
            } else if (input[i] == LF && foundCR) {
               int numSpaces = 0;

               for (int j = i - 2; j >= start && (input[j] == 32 || input[j] == 9); j--) {
                  numSpaces++;
               }

               realLength -= 1 + numSpaces;
               result[realLength++] = CR;
               result[realLength++] = LF;
               start = i + 1;
            } else {
               result[realLength++] = input[i];
            }
         }

         if (realLength < length) {
            byte[] returnValue = new byte[realLength];
            System.arraycopy(result, 0, returnValue, 0, realLength);
            result = returnValue;
         }

         return arrayAllSpaces(result) ? new byte[0] : result;
      } else {
         throw new Object();
      }
   }

   public static final byte[] convertLineEndings(byte[] input, int offset, int length) {
      byte CR = 13;
      byte LF = 10;
      if (input == null) {
         return null;
      }

      if (offset >= 0 && length >= 0 && input.length - length >= offset) {
         boolean foundCR = false;
         byte[] result = new byte[input.length * 2];
         int realLength = 0;

         for (int i = offset; i < offset + length; i++) {
            if (input[i] == CR) {
               if (foundCR) {
                  result[realLength++] = LF;
               }

               foundCR = true;
            } else if (input[i] == LF) {
               if (!foundCR) {
                  result[realLength++] = CR;
               }

               foundCR = false;
            } else if (foundCR) {
               result[realLength++] = LF;
               foundCR = false;
            }

            result[realLength++] = input[i];
         }

         if (foundCR) {
            result[realLength++] = LF;
         }

         if (realLength < result.length) {
            byte[] returnValue = new byte[realLength];
            System.arraycopy(result, 0, returnValue, 0, realLength);
            result = returnValue;
         }

         return result;
      } else {
         throw new Object();
      }
   }

   private static final boolean arrayAllSpaces(byte[] checkArray) {
      for (int i = 0; i < checkArray.length; i++) {
         if (checkArray[i] != 32) {
            return false;
         }
      }

      return true;
   }
}
