package net.rim.device.api.crypto;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.system.NvStore;

public class MIDletSecurityCryptoImpl extends MIDletSecurityCrypto {
   private static int CMIDSIG_CHECK_DIGEST = 1;
   private static int CMIDSIG_NULL = 0;

   @Override
   public int checkMIDletSignatureImpl(Digest digest, String rsaSHA1Sig, String[] signingCerts, byte[] refTrailerBytes, byte[] signerCertEncoding) {
      return this.doCheckMIDletSignatureImpl(digest, rsaSHA1Sig, signingCerts, refTrailerBytes, signerCertEncoding, CMIDSIG_CHECK_DIGEST);
   }

   @Override
   public int checkJADCertChainImpl(String[] signingCerts) {
      if (signingCerts == null) {
         throw new Object();
      } else {
         return signingCerts.length <= 0 ? 0 : this.doCheckMIDletSignatureImpl(null, null, signingCerts, null, null, CMIDSIG_NULL);
      }
   }

   private int doCheckMIDletSignatureImpl(Digest param1, String param2, String[] param3, byte[] param4, byte[] param5, int param6) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 7
      // 003: aload 3
      // 004: ifnonnull 00a
      // 007: bipush 1
      // 008: istore 7
      // 00a: aload 1
      // 00b: ifnull 01c
      // 00e: aload 4
      // 010: ifnull 01c
      // 013: aload 5
      // 015: ifnull 01c
      // 018: aload 2
      // 019: ifnonnull 028
      // 01c: iload 6
      // 01e: getstatic net/rim/device/api/crypto/MIDletSecurityCryptoImpl.CMIDSIG_CHECK_DIGEST I
      // 021: iand
      // 022: ifeq 028
      // 025: bipush 1
      // 026: istore 7
      // 028: iload 7
      // 02a: ifeq 035
      // 02d: new java/lang/Object
      // 030: dup
      // 031: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 034: athrow
      // 035: invokestatic net/rim/device/api/crypto/keystore/DeviceKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 038: astore 8
      // 03a: aload 8
      // 03c: ldc2_w 4966172969402917741
      // 03f: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.existsIndex (J)Z 3
      // 044: ifne 056
      // 047: aload 8
      // 049: new java/lang/Object
      // 04c: dup
      // 04d: invokespecial net/rim/device/api/crypto/certificate/CertificateHashKeyStoreIndex.<init> ()V
      // 050: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.addIndex (Lnet/rim/device/api/crypto/keystore/KeyStoreIndex;)Z 2
      // 055: pop
      // 056: aconst_null
      // 057: astore 9
      // 059: aconst_null
      // 05a: astore 10
      // 05c: aload 2
      // 05d: ifnull 094
      // 060: new java/lang/Object
      // 063: dup
      // 064: new java/lang/Object
      // 067: dup
      // 068: aload 2
      // 069: invokevirtual java/lang/String.getBytes ()[B
      // 06c: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 06f: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 072: astore 11
      // 074: aload 2
      // 075: invokevirtual java/lang/String.length ()I
      // 078: newarray 8
      // 07a: astore 9
      // 07c: aload 11
      // 07e: aload 9
      // 080: invokevirtual net/rim/device/api/io/Base64InputStream.read ([B)I
      // 083: istore 12
      // 085: iload 12
      // 087: ifle 094
      // 08a: aload 9
      // 08c: bipush 0
      // 08d: iload 12
      // 08f: invokestatic net/rim/device/api/util/Arrays.copy ([BII)[B
      // 092: astore 10
      // 094: bipush 1
      // 095: aload 3
      // 096: arraylength
      // 097: iadd
      // 098: anewarray 164
      // 09b: astore 11
      // 09d: bipush 0
      // 09e: istore 12
      // 0a0: iload 12
      // 0a2: aload 3
      // 0a3: arraylength
      // 0a4: if_icmpge 0d2
      // 0a7: new net/rim/device/api/crypto/certificate/x509/X509Certificate
      // 0aa: dup
      // 0ab: new java/lang/Object
      // 0ae: dup
      // 0af: new java/lang/Object
      // 0b2: dup
      // 0b3: aload 3
      // 0b4: iload 12
      // 0b6: aaload
      // 0b7: invokevirtual java/lang/String.getBytes ()[B
      // 0ba: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 0bd: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 0c0: invokespecial net/rim/device/api/crypto/certificate/x509/X509Certificate.<init> (Ljava/io/InputStream;)V
      // 0c3: astore 13
      // 0c5: aload 11
      // 0c7: iload 12
      // 0c9: aload 13
      // 0cb: aastore
      // 0cc: iinc 12 1
      // 0cf: goto 0a0
      // 0d2: bipush 26
      // 0d4: istore 12
      // 0d6: iload 12
      // 0d8: bipush 30
      // 0da: if_icmple 0e0
      // 0dd: goto 336
      // 0e0: iload 12
      // 0e2: invokestatic net/rim/device/internal/system/NvStore.readData (I)[B
      // 0e5: astore 13
      // 0e7: aload 13
      // 0e9: ifnonnull 0ef
      // 0ec: goto 316
      // 0ef: aload 13
      // 0f1: invokestatic net/rim/device/internal/system/MIDletSecurity.checkDomainPolicy ([B)Z
      // 0f4: ifne 0fa
      // 0f7: bipush 4
      // 0f9: ireturn
      // 0fa: bipush 20
      // 0fc: newarray 8
      // 0fe: astore 14
      // 100: aload 13
      // 102: bipush 0
      // 103: aload 14
      // 105: bipush 0
      // 106: bipush 20
      // 108: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 10b: aload 8
      // 10d: ldc2_w 4966172969402917741
      // 110: aload 14
      // 112: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 117: astore 15
      // 119: aload 15
      // 11b: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 120: ifne 126
      // 123: goto 316
      // 126: aload 15
      // 128: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 12d: checkcast java/lang/Object
      // 130: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 135: astore 16
      // 137: aload 16
      // 139: instanceof net/rim/device/api/crypto/certificate/x509/X509Certificate
      // 13c: ifeq 119
      // 13f: aload 11
      // 141: aload 11
      // 143: arraylength
      // 144: bipush 1
      // 145: isub
      // 146: aload 16
      // 148: checkcast net/rim/device/api/crypto/certificate/x509/X509Certificate
      // 14b: aastore
      // 14c: aload 11
      // 14e: arraylength
      // 14f: istore 17
      // 151: aload 16
      // 153: invokeinterface net/rim/device/api/crypto/certificate/Certificate.isRoot ()Z 1
      // 158: ifne 19d
      // 15b: aload 16
      // 15d: aload 8
      // 15f: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.buildCertificateChain (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 162: astore 18
      // 164: aload 18
      // 166: arraylength
      // 167: bipush 1
      // 168: if_icmple 18b
      // 16b: aload 11
      // 16d: iload 17
      // 16f: aload 18
      // 171: arraylength
      // 172: bipush 1
      // 173: isub
      // 174: iadd
      // 175: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 178: aload 18
      // 17a: bipush 1
      // 17b: aload 11
      // 17d: iload 17
      // 17f: aload 11
      // 181: arraylength
      // 182: iload 17
      // 184: isub
      // 185: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 188: goto 19d
      // 18b: iload 17
      // 18d: aload 11
      // 18f: arraylength
      // 190: if_icmpeq 119
      // 193: aload 11
      // 195: iload 17
      // 197: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 19a: goto 119
      // 19d: aload 11
      // 19f: bipush 0
      // 1a0: aaload
      // 1a1: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.isCA ()Z
      // 1a4: ifne 1ca
      // 1a7: aload 11
      // 1a9: bipush 0
      // 1aa: aaload
      // 1ab: sipush 2048
      // 1ae: i2l
      // 1af: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.queryKeyUsage (J)I
      // 1b2: ifne 1ca
      // 1b5: iload 17
      // 1b7: aload 11
      // 1b9: arraylength
      // 1ba: if_icmpne 1c0
      // 1bd: goto 119
      // 1c0: aload 11
      // 1c2: iload 17
      // 1c4: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 1c7: goto 119
      // 1ca: aload 11
      // 1cc: bipush 0
      // 1cd: aaload
      // 1ce: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.isCA ()Z
      // 1d1: ifne 1f5
      // 1d4: aload 11
      // 1d6: bipush 0
      // 1d7: aaload
      // 1d8: bipush 1
      // 1d9: i2l
      // 1da: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.queryKeyUsage (J)I
      // 1dd: ifne 1f5
      // 1e0: iload 17
      // 1e2: aload 11
      // 1e4: arraylength
      // 1e5: if_icmpne 1eb
      // 1e8: goto 119
      // 1eb: aload 11
      // 1ed: iload 17
      // 1ef: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 1f2: goto 119
      // 1f5: bipush 1
      // 1f6: istore 18
      // 1f8: bipush 1
      // 1f9: istore 19
      // 1fb: iload 19
      // 1fd: aload 11
      // 1ff: arraylength
      // 200: if_icmpge 21d
      // 203: aload 11
      // 205: iload 19
      // 207: aaload
      // 208: bipush 32
      // 20a: i2l
      // 20b: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.queryKeyUsage (J)I
      // 20e: ifne 217
      // 211: bipush 0
      // 212: istore 18
      // 214: goto 21d
      // 217: iinc 19 1
      // 21a: goto 1fb
      // 21d: iload 18
      // 21f: ifne 237
      // 222: iload 17
      // 224: aload 11
      // 226: arraylength
      // 227: if_icmpne 22d
      // 22a: goto 119
      // 22d: aload 11
      // 22f: iload 17
      // 231: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 234: goto 119
      // 237: aload 11
      // 239: bipush 0
      // 23a: aaload
      // 23b: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 23e: invokeinterface net/rim/device/api/crypto/Key.getAlgorithm ()Ljava/lang/String; 1
      // 243: ldc_w "RSA"
      // 246: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 249: ifne 24f
      // 24c: goto 2ed
      // 24f: bipush 0
      // 250: istore 19
      // 252: iload 6
      // 254: getstatic net/rim/device/api/crypto/MIDletSecurityCryptoImpl.CMIDSIG_CHECK_DIGEST I
      // 257: iand
      // 258: ifeq 280
      // 25b: new java/lang/Object
      // 25e: dup
      // 25f: aload 11
      // 261: bipush 0
      // 262: aaload
      // 263: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 266: checkcast java/lang/Object
      // 269: aload 1
      // 26a: aload 10
      // 26c: bipush 0
      // 26d: invokespecial net/rim/device/api/crypto/PKCS1SignatureVerifier.<init> (Lnet/rim/device/api/crypto/RSAPublicKey;Lnet/rim/device/api/crypto/Digest;[BI)V
      // 270: astore 20
      // 272: aload 20
      // 274: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.verify ()Z
      // 277: ifeq 283
      // 27a: bipush 1
      // 27b: istore 19
      // 27d: goto 283
      // 280: bipush 1
      // 281: istore 19
      // 283: iload 19
      // 285: ifeq 2ed
      // 288: aload 11
      // 28a: aconst_null
      // 28b: invokestatic java/lang/System.currentTimeMillis ()J
      // 28e: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.verifyCertificateChain ([Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;J)Z
      // 291: pop
      // 292: iload 6
      // 294: getstatic net/rim/device/api/crypto/MIDletSecurityCryptoImpl.CMIDSIG_CHECK_DIGEST I
      // 297: iand
      // 298: ifne 2b0
      // 29b: bipush 0
      // 29c: istore 20
      // 29e: iload 17
      // 2a0: aload 11
      // 2a2: arraylength
      // 2a3: if_icmpeq 2ad
      // 2a6: aload 11
      // 2a8: iload 17
      // 2aa: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 2ad: iload 20
      // 2af: ireturn
      // 2b0: iload 12
      // 2b2: aload 13
      // 2b4: aload 4
      // 2b6: invokestatic net/rim/device/internal/system/MIDletSecurity.createTrailerFromNvStoreDomain (I[B[B)V
      // 2b9: aload 11
      // 2bb: bipush 0
      // 2bc: aaload
      // 2bd: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getEncoding ()[B
      // 2c0: astore 20
      // 2c2: aload 5
      // 2c4: aload 20
      // 2c6: arraylength
      // 2c7: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 2ca: aload 20
      // 2cc: bipush 0
      // 2cd: aload 5
      // 2cf: bipush 0
      // 2d0: aload 20
      // 2d2: arraylength
      // 2d3: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 2d6: bipush 0
      // 2d7: istore 21
      // 2d9: iload 17
      // 2db: aload 11
      // 2dd: arraylength
      // 2de: if_icmpeq 2e8
      // 2e1: aload 11
      // 2e3: iload 17
      // 2e5: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 2e8: iload 21
      // 2ea: ireturn
      // 2eb: astore 20
      // 2ed: iload 17
      // 2ef: aload 11
      // 2f1: arraylength
      // 2f2: if_icmpne 2f8
      // 2f5: goto 119
      // 2f8: aload 11
      // 2fa: iload 17
      // 2fc: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 2ff: goto 119
      // 302: astore 22
      // 304: iload 17
      // 306: aload 11
      // 308: arraylength
      // 309: if_icmpeq 313
      // 30c: aload 11
      // 30e: iload 17
      // 310: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 313: aload 22
      // 315: athrow
      // 316: iinc 12 1
      // 319: goto 0d6
      // 31c: astore 7
      // 31e: bipush 1
      // 31f: ireturn
      // 320: astore 7
      // 322: bipush 2
      // 324: ireturn
      // 325: astore 7
      // 327: bipush 2
      // 329: ireturn
      // 32a: astore 7
      // 32c: bipush 1
      // 32d: ireturn
      // 32e: astore 7
      // 330: bipush 1
      // 331: ireturn
      // 332: astore 7
      // 334: bipush 1
      // 335: ireturn
      // 336: bipush 3
      // 338: ireturn
      // try (301 -> 312): 352 null
      // try (321 -> 343): 352 null
      // try (154 -> 183): 362 null
      // try (191 -> 203): 362 null
      // try (212 -> 224): 362 null
      // try (233 -> 255): 362 null
      // try (264 -> 312): 362 null
      // try (321 -> 343): 362 null
      // try (352 -> 353): 362 null
      // try (362 -> 363): 362 null
      // try (0 -> 116): 374 null
      // try (117 -> 319): 374 null
      // try (321 -> 350): 374 null
      // try (352 -> 374): 374 null
      // try (0 -> 116): 377 null
      // try (117 -> 319): 377 null
      // try (321 -> 350): 377 null
      // try (352 -> 374): 377 null
      // try (0 -> 116): 380 null
      // try (117 -> 319): 380 null
      // try (321 -> 350): 380 null
      // try (352 -> 374): 380 null
      // try (0 -> 116): 383 null
      // try (117 -> 319): 383 null
      // try (321 -> 350): 383 null
      // try (352 -> 374): 383 null
      // try (0 -> 116): 386 null
      // try (117 -> 319): 386 null
      // try (321 -> 350): 386 null
      // try (352 -> 374): 386 null
      // try (0 -> 116): 389 null
      // try (117 -> 319): 389 null
      // try (321 -> 350): 389 null
      // try (352 -> 374): 389 null
   }

   @Override
   public byte[] signMIDletTrailerImpl(byte[] codfile, byte[] trailerBytes) {
      if (codfile != null && trailerBytes != null) {
         byte[] signature = this.sign(codfile, trailerBytes);
         int trailerLength = 4 + signature.length + 3 & -4;
         DataBuffer buffer = (DataBuffer)(new Object());
         buffer.writeByte(1);
         buffer.writeByte(0);
         buffer.writeByte(trailerLength & 0xFF);
         buffer.writeByte(trailerLength >> 8 & 0xFF);
         buffer.writeByte(1346652493);
         buffer.writeByte(5260361);
         buffer.writeByte(20548);
         buffer.writeByte(80);
         buffer.write(signature);
         trailerLength += 4;

         while (buffer.getLength() < trailerLength) {
            buffer.writeByte(0);
         }

         return buffer.toArray();
      } else {
         throw new Object();
      }
   }

   private static int readLittleEndianUnsignedShort(byte[] data, int offset) {
      int lsb = data[offset] & 255;
      int msb = data[offset + 1] & 255;
      return msb << 8 | lsb;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private byte[] sign(byte[] codFile, byte[] codFileTrailer) {
      Digest digest = (Digest)(new Object());
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         int privateKey = readLittleEndianUnsignedShort(codFile, 36);
         if (privateKey < 74) {
            throw new Object();
         }

         int codeSize = readLittleEndianUnsignedShort(codFile, 38);
         int dataSize = readLittleEndianUnsignedShort(codFile, 40);
         digest.update(codFile, 0, 44 + codeSize + dataSize);
         byte[] codFileHash = digest.getDigest(true);
         digest.update(codFileHash);
         digest.update(codFileTrailer);
         var9 = false;
      } finally {
         if (var9) {
            throw new Object();
         }
      }

      byte[] privateKey = NvStore.readData(24);
      if (privateKey == null) {
         privateKey = new byte[21];
         byte[] publicKey = new byte[22];
         NativeEC.generateKeyPair(MIDletSecurityCrypto.CURVE_NAME, privateKey, publicKey);
         NvStore.writeData(24, privateKey);
         NvStore.writeData(23, publicKey);
      }

      byte[] r = new byte[21];
      byte[] s = new byte[21];
      NativeEC.signDSA(MIDletSecurityCrypto.CURVE_NAME, privateKey, digest.getDigest(), r, 0, s, 0);
      DataBuffer buffer = (DataBuffer)(new Object(true));
      TLEUtilities.writeDataField(buffer, 1, r);
      TLEUtilities.writeDataField(buffer, 2, s);
      return buffer.toArray();
   }
}
