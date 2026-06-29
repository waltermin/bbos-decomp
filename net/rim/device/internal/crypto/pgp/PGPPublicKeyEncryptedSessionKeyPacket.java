package net.rim.device.internal.crypto.pgp;

import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.util.Persistable;

public final class PGPPublicKeyEncryptedSessionKeyPacket extends PGPPacket implements Persistable {
   private byte[] _keyID;
   private int _publicKeyAlgorithm;
   private int _sessionKeyAlgorithm;
   private byte[] _encodedData;

   public PGPPublicKeyEncryptedSessionKeyPacket(int tag, byte[] encoding) {
      super(tag, encoding);
      int offset = 0;
      int version = encoding[offset++];
      switch (version) {
         case 1:
         default:
            throw new Object(((StringBuffer)(new Object("Ver:"))).append(version).toString());
         case 2:
         case 3:
            this._keyID = new byte[8];
            System.arraycopy(encoding, offset, this._keyID, 0, 8);
            offset += 8;
            this._publicKeyAlgorithm = encoding[offset++];
            int encodedDataLength = encoding.length - offset;
            this._encodedData = new byte[encodedDataLength];
            System.arraycopy(encoding, offset, this._encodedData, 0, encodedDataLength);
      }
   }

   public final int getPublicKeyAlgorithm() {
      return this._publicKeyAlgorithm;
   }

   public final int getSessionKeyAlgorithm() {
      return this._sessionKeyAlgorithm;
   }

   public final byte[] getEncodedData() {
      return this._encodedData;
   }

   public final byte[] getKeyID() {
      return this._keyID;
   }

   public final SymmetricKey getSessionKey(KeyStore param1, boolean param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnonnull 006
      // 004: aconst_null
      // 005: areturn
      // 006: aload 1
      // 007: new net/rim/device/api/crypto/certificate/pgp/PGPKeyIDKeyStoreIndex
      // 00a: dup
      // 00b: invokespecial net/rim/device/api/crypto/certificate/pgp/PGPKeyIDKeyStoreIndex.<init> ()V
      // 00e: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.addIndex (Lnet/rim/device/api/crypto/keystore/KeyStoreIndex;)Z 2
      // 013: pop
      // 014: aload 1
      // 015: ldc2_w -2737350786039236692
      // 018: aload 0
      // 019: getfield net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket._keyID [B
      // 01c: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 021: astore 3
      // 022: aload 3
      // 023: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 028: ifne 02e
      // 02b: goto 2f9
      // 02e: aload 3
      // 02f: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 034: checkcast java/lang/Object
      // 037: astore 4
      // 039: aload 4
      // 03b: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 040: ifne 046
      // 043: goto 022
      // 046: aconst_null
      // 047: astore 5
      // 049: aload 4
      // 04b: aconst_null
      // 04c: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.checkTicket (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Z 2
      // 051: ifeq 064
      // 054: aload 4
      // 056: aconst_null
      // 057: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 05c: checkcast net/rim/device/api/crypto/pgp/PGPPrivateKey
      // 05f: astore 5
      // 061: goto 085
      // 064: iload 2
      // 065: ifeq 085
      // 068: aload 4
      // 06a: sipush 6068
      // 06d: invokestatic net/rim/device/api/crypto/keystore/KeyStoreResources.getString (I)Ljava/lang/String;
      // 070: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket; 2
      // 075: astore 6
      // 077: aload 4
      // 079: aload 6
      // 07b: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 080: checkcast net/rim/device/api/crypto/pgp/PGPPrivateKey
      // 083: astore 5
      // 085: aload 5
      // 087: ifnonnull 097
      // 08a: aload 5
      // 08c: ifnull 022
      // 08f: aload 5
      // 091: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 094: goto 022
      // 097: aload 5
      // 099: aload 0
      // 09a: getfield net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket._keyID [B
      // 09d: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.getPrivateKey ([B)Lnet/rim/device/api/crypto/PrivateKey;
      // 0a0: astore 6
      // 0a2: aload 6
      // 0a4: ifnonnull 0b4
      // 0a7: aload 5
      // 0a9: ifnull 022
      // 0ac: aload 5
      // 0ae: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 0b1: goto 022
      // 0b4: aconst_null
      // 0b5: astore 7
      // 0b7: aconst_null
      // 0b8: astore 8
      // 0ba: aload 0
      // 0bb: getfield net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket._publicKeyAlgorithm I
      // 0be: lookupswitch 171 4 1 119 2 119 16 42 20 42
      // 0e8: aload 6
      // 0ea: dup
      // 0eb: instanceof java/lang/Object
      // 0ee: ifne 0f5
      // 0f1: pop
      // 0f2: goto 185
      // 0f5: checkcast java/lang/Object
      // 0f8: astore 9
      // 0fa: new java/lang/Object
      // 0fd: dup
      // 0fe: aload 0
      // 0ff: getfield net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket._encodedData [B
      // 102: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 105: astore 10
      // 107: aload 10
      // 109: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.readMPI (Ljava/io/InputStream;)[B
      // 10c: astore 11
      // 10e: aload 10
      // 110: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.readMPI (Ljava/io/InputStream;)[B
      // 113: astore 8
      // 115: new java/lang/Object
      // 118: dup
      // 119: aload 9
      // 11b: invokevirtual net/rim/device/api/crypto/DHPrivateKey.getDHCryptoSystem ()Lnet/rim/device/api/crypto/DHCryptoSystem;
      // 11e: aload 11
      // 120: invokespecial net/rim/device/api/crypto/DHPublicKey.<init> (Lnet/rim/device/api/crypto/DHCryptoSystem;[B)V
      // 123: astore 12
      // 125: new java/lang/Object
      // 128: dup
      // 129: aload 9
      // 12b: aload 12
      // 12d: invokespecial net/rim/device/api/crypto/ElGamalDecryptorEngine.<init> (Lnet/rim/device/api/crypto/DHPrivateKey;Lnet/rim/device/api/crypto/DHPublicKey;)V
      // 130: astore 7
      // 132: goto 185
      // 135: aload 6
      // 137: dup
      // 138: instanceof java/lang/Object
      // 13b: ifne 142
      // 13e: pop
      // 13f: goto 185
      // 142: checkcast java/lang/Object
      // 145: astore 9
      // 147: new java/lang/Object
      // 14a: dup
      // 14b: aload 0
      // 14c: getfield net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket._encodedData [B
      // 14f: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 152: astore 10
      // 154: aload 10
      // 156: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.readMPI (Ljava/io/InputStream;)[B
      // 159: astore 8
      // 15b: new java/lang/Object
      // 15e: dup
      // 15f: aload 9
      // 161: invokespecial net/rim/device/api/crypto/RSADecryptorEngine.<init> (Lnet/rim/device/api/crypto/RSAPrivateKey;)V
      // 164: astore 7
      // 166: goto 185
      // 169: new java/lang/Object
      // 16c: dup
      // 16d: new java/lang/Object
      // 170: dup
      // 171: ldc_w "Pub:"
      // 174: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 177: aload 0
      // 178: getfield net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket._publicKeyAlgorithm I
      // 17b: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 17e: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 181: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> (Ljava/lang/String;)V
      // 184: athrow
      // 185: aload 7
      // 187: ifnonnull 19a
      // 18a: aload 5
      // 18c: ifnonnull 192
      // 18f: goto 022
      // 192: aload 5
      // 194: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 197: goto 022
      // 19a: aload 8
      // 19c: aload 7
      // 19e: invokeinterface net/rim/device/api/crypto/BlockDecryptorEngine.getBlockLength ()I 1
      // 1a3: invokestatic net/rim/device/api/crypto/CryptoByteArrayArithmetic.ensureLength ([BI)[B
      // 1a6: astore 8
      // 1a8: new java/lang/Object
      // 1ab: dup
      // 1ac: new java/lang/Object
      // 1af: dup
      // 1b0: aload 7
      // 1b2: invokespecial net/rim/device/api/crypto/PKCS1UnformatterEngine.<init> (Lnet/rim/device/api/crypto/PrivateKeyDecryptorEngine;)V
      // 1b5: new java/lang/Object
      // 1b8: dup
      // 1b9: aload 8
      // 1bb: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 1be: invokespecial net/rim/device/api/crypto/BlockDecryptor.<init> (Lnet/rim/device/api/crypto/BlockUnformatterEngine;Ljava/io/InputStream;)V
      // 1c1: astore 9
      // 1c3: aload 9
      // 1c5: invokevirtual net/rim/device/api/crypto/BlockDecryptor.getOutputBlockLength ()I
      // 1c8: newarray 8
      // 1ca: astore 10
      // 1cc: aload 9
      // 1ce: aload 10
      // 1d0: bipush 0
      // 1d1: aload 10
      // 1d3: arraylength
      // 1d4: invokevirtual net/rim/device/api/crypto/BlockDecryptor.read ([BII)I
      // 1d7: istore 11
      // 1d9: iload 11
      // 1db: bipush 3
      // 1dd: if_icmpge 1f0
      // 1e0: aload 5
      // 1e2: ifnonnull 1e8
      // 1e5: goto 022
      // 1e8: aload 5
      // 1ea: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 1ed: goto 022
      // 1f0: aload 0
      // 1f1: aload 10
      // 1f3: bipush 0
      // 1f4: baload
      // 1f5: putfield net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket._sessionKeyAlgorithm I
      // 1f8: aload 0
      // 1f9: getfield net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket._sessionKeyAlgorithm I
      // 1fc: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.encryptionConstantToString (I)Ljava/lang/String;
      // 1ff: astore 12
      // 201: bipush 1
      // 202: istore 13
      // 204: iload 11
      // 206: bipush 2
      // 208: isub
      // 209: istore 14
      // 20b: bipush 0
      // 20c: istore 15
      // 20e: iload 13
      // 210: istore 16
      // 212: iload 16
      // 214: iload 14
      // 216: if_icmpge 22d
      // 219: iload 15
      // 21b: aload 10
      // 21d: iload 16
      // 21f: baload
      // 220: sipush 255
      // 223: iand
      // 224: iadd
      // 225: istore 15
      // 227: iinc 16 1
      // 22a: goto 212
      // 22d: iload 15
      // 22f: ldc_w 65535
      // 232: iand
      // 233: istore 15
      // 235: aload 10
      // 237: iload 14
      // 239: baload
      // 23a: sipush 255
      // 23d: iand
      // 23e: bipush 8
      // 240: ishl
      // 241: aload 10
      // 243: iload 14
      // 245: bipush 1
      // 246: iadd
      // 247: baload
      // 248: sipush 255
      // 24b: iand
      // 24c: ior
      // 24d: istore 16
      // 24f: iload 15
      // 251: iload 16
      // 253: if_icmpeq 266
      // 256: aload 5
      // 258: ifnonnull 25e
      // 25b: goto 022
      // 25e: aload 5
      // 260: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 263: goto 022
      // 266: aload 12
      // 268: aload 10
      // 26a: iload 13
      // 26c: iload 14
      // 26e: iload 13
      // 270: isub
      // 271: invokestatic net/rim/device/api/crypto/SymmetricKeyFactory.getInstance (Ljava/lang/String;[BII)Lnet/rim/device/api/crypto/SymmetricKey;
      // 274: astore 17
      // 276: aload 5
      // 278: ifnull 280
      // 27b: aload 5
      // 27d: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 280: aload 17
      // 282: areturn
      // 283: astore 6
      // 285: aload 5
      // 287: ifnonnull 28d
      // 28a: goto 022
      // 28d: aload 5
      // 28f: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 292: goto 022
      // 295: astore 6
      // 297: aload 5
      // 299: ifnonnull 29f
      // 29c: goto 022
      // 29f: aload 5
      // 2a1: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 2a4: goto 022
      // 2a7: astore 6
      // 2a9: aload 5
      // 2ab: ifnonnull 2b1
      // 2ae: goto 022
      // 2b1: aload 5
      // 2b3: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 2b6: goto 022
      // 2b9: astore 6
      // 2bb: aload 5
      // 2bd: ifnonnull 2c3
      // 2c0: goto 022
      // 2c3: aload 5
      // 2c5: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 2c8: goto 022
      // 2cb: astore 6
      // 2cd: aload 5
      // 2cf: ifnonnull 2d5
      // 2d2: goto 022
      // 2d5: aload 5
      // 2d7: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 2da: goto 022
      // 2dd: astore 6
      // 2df: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // 2e2: dup
      // 2e3: ldc_w "RMIO"
      // 2e6: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // 2e9: athrow
      // 2ea: astore 18
      // 2ec: aload 5
      // 2ee: ifnull 2f6
      // 2f1: aload 5
      // 2f3: invokevirtual net/rim/device/api/crypto/pgp/PGPPrivateKey.clearPasswordTicket ()V
      // 2f6: aload 18
      // 2f8: athrow
      // 2f9: aconst_null
      // 2fa: areturn
      // try (30 -> 54): 267 null
      // try (59 -> 66): 267 null
      // try (71 -> 149): 267 null
      // try (155 -> 186): 267 null
      // try (192 -> 247): 267 null
      // try (253 -> 261): 267 null
      // try (30 -> 54): 274 null
      // try (59 -> 66): 274 null
      // try (71 -> 149): 274 null
      // try (155 -> 186): 274 null
      // try (192 -> 247): 274 null
      // try (253 -> 261): 274 null
      // try (30 -> 54): 281 null
      // try (59 -> 66): 281 null
      // try (71 -> 149): 281 null
      // try (155 -> 186): 281 null
      // try (192 -> 247): 281 null
      // try (253 -> 261): 281 null
      // try (30 -> 54): 288 null
      // try (59 -> 66): 288 null
      // try (71 -> 149): 288 null
      // try (155 -> 186): 288 null
      // try (192 -> 247): 288 null
      // try (253 -> 261): 288 null
      // try (30 -> 54): 295 null
      // try (59 -> 66): 295 null
      // try (71 -> 149): 295 null
      // try (155 -> 186): 295 null
      // try (192 -> 247): 295 null
      // try (253 -> 261): 295 null
      // try (30 -> 54): 302 null
      // try (59 -> 66): 302 null
      // try (71 -> 149): 302 null
      // try (155 -> 186): 302 null
      // try (192 -> 247): 302 null
      // try (253 -> 261): 302 null
      // try (30 -> 54): 308 null
      // try (59 -> 66): 308 null
      // try (71 -> 149): 308 null
      // try (155 -> 186): 308 null
      // try (192 -> 247): 308 null
      // try (253 -> 261): 308 null
      // try (267 -> 268): 308 null
      // try (274 -> 275): 308 null
      // try (281 -> 282): 308 null
      // try (288 -> 289): 308 null
      // try (295 -> 296): 308 null
      // try (302 -> 309): 308 null
   }
}
