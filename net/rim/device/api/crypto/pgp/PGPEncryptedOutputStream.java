package net.rim.device.api.crypto.pgp;

import java.io.OutputStream;
import net.rim.device.api.crypto.AESEncryptorEngine;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.BlockEncryptor;
import net.rim.device.api.crypto.CAST128EncryptorEngine;
import net.rim.device.api.crypto.CAST128Key;
import net.rim.device.api.crypto.CFBEncryptor;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.DHPublicKey;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.ElGamalEncryptorEngine;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.PKCS1FormatterEngine;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.PublicKeyEncryptorEngine;
import net.rim.device.api.crypto.RSAEncryptorEngine;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.SymmetricKeyEncryptorEngine;
import net.rim.device.api.crypto.TripleDESEncryptorEngine;
import net.rim.device.api.crypto.TripleDESKey;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.io.SharedOutputStream;
import net.rim.device.internal.crypto.pgp.PGPUtilities;

public final class PGPEncryptedOutputStream extends PGPOutputStream {
   private SymmetricKey _sessionKey;
   private OutputStream _keyPackets;
   private OutputStream _header;
   private PGPCFBEncryptor _blockEncryptor;
   private NoCopyByteArrayOutputStream _dataOut;
   private SharedOutputStream _sharedInternal;
   private int _sessionKeyAlgorithm;
   private static final byte PUBLIC_KEY_PACKET_VERSION = 3;
   private static final byte SYMMETRIC_KEY_PACKET_VERSION = 4;
   private static final byte SALTED_S2K = 1;
   private static final byte SALTED_ITERATED_S2K = 3;
   private static final byte IV_SIZE = 8;

   public PGPEncryptedOutputStream(OutputStream out) {
      this(out, 2, null, 4);
   }

   public PGPEncryptedOutputStream(OutputStream out, int symmetricAlgorithm) {
      this(out, symmetricAlgorithm, null, 4);
   }

   public PGPEncryptedOutputStream(OutputStream out, int symmetricAlgorithm, int tagFormat) {
      this(out, symmetricAlgorithm, null, tagFormat);
   }

   public PGPEncryptedOutputStream(OutputStream param1, int param2, PGPPseudoRandomSource param3, int param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 1
      // 002: iload 4
      // 004: invokespecial net/rim/device/api/crypto/pgp/PGPOutputStream.<init> (Ljava/io/OutputStream;I)V
      // 007: aload 0
      // 008: aload 0
      // 009: getfield net/rim/device/api/crypto/pgp/PGPOutputStream._out Lnet/rim/device/api/io/SharedOutputStream;
      // 00c: invokevirtual net/rim/device/api/io/SharedOutputStream.getOutputStream ()Ljava/io/OutputStream;
      // 00f: putfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._keyPackets Ljava/io/OutputStream;
      // 012: aload 0
      // 013: aload 0
      // 014: getfield net/rim/device/api/crypto/pgp/PGPOutputStream._out Lnet/rim/device/api/io/SharedOutputStream;
      // 017: invokevirtual net/rim/device/api/io/SharedOutputStream.getOutputStream ()Ljava/io/OutputStream;
      // 01a: putfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._header Ljava/io/OutputStream;
      // 01d: aload 0
      // 01e: new net/rim/device/api/io/NoCopyByteArrayOutputStream
      // 021: dup
      // 022: invokespecial net/rim/device/api/io/NoCopyByteArrayOutputStream.<init> ()V
      // 025: putfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._dataOut Lnet/rim/device/api/io/NoCopyByteArrayOutputStream;
      // 028: aload 0
      // 029: iload 2
      // 02a: putfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._sessionKeyAlgorithm I
      // 02d: iload 2
      // 02e: tableswitch 50 1 9 239 50 86 239 239 239 122 161 200
      // 060: aload 0
      // 061: aload 3
      // 062: ifnonnull 06f
      // 065: new net/rim/device/api/crypto/TripleDESKey
      // 068: dup
      // 069: invokespecial net/rim/device/api/crypto/TripleDESKey.<init> ()V
      // 06c: goto 07e
      // 06f: new net/rim/device/api/crypto/TripleDESKey
      // 072: dup
      // 073: aload 3
      // 074: bipush 24
      // 076: invokeinterface net/rim/device/api/crypto/PseudoRandomSource.getBytes (I)[B 2
      // 07b: invokespecial net/rim/device/api/crypto/TripleDESKey.<init> ([B)V
      // 07e: putfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 081: goto 125
      // 084: aload 0
      // 085: aload 3
      // 086: ifnonnull 093
      // 089: new net/rim/device/api/crypto/CAST128Key
      // 08c: dup
      // 08d: invokespecial net/rim/device/api/crypto/CAST128Key.<init> ()V
      // 090: goto 0a2
      // 093: new net/rim/device/api/crypto/CAST128Key
      // 096: dup
      // 097: aload 3
      // 098: bipush 16
      // 09a: invokeinterface net/rim/device/api/crypto/PseudoRandomSource.getBytes (I)[B 2
      // 09f: invokespecial net/rim/device/api/crypto/CAST128Key.<init> ([B)V
      // 0a2: putfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 0a5: goto 125
      // 0a8: aload 0
      // 0a9: aload 3
      // 0aa: ifnonnull 0ba
      // 0ad: new net/rim/device/api/crypto/AESKey
      // 0b0: dup
      // 0b1: sipush 128
      // 0b4: invokespecial net/rim/device/api/crypto/AESKey.<init> (I)V
      // 0b7: goto 0c9
      // 0ba: new net/rim/device/api/crypto/AESKey
      // 0bd: dup
      // 0be: aload 3
      // 0bf: bipush 16
      // 0c1: invokeinterface net/rim/device/api/crypto/PseudoRandomSource.getBytes (I)[B 2
      // 0c6: invokespecial net/rim/device/api/crypto/AESKey.<init> ([B)V
      // 0c9: putfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 0cc: goto 125
      // 0cf: aload 0
      // 0d0: aload 3
      // 0d1: ifnonnull 0e1
      // 0d4: new net/rim/device/api/crypto/AESKey
      // 0d7: dup
      // 0d8: sipush 192
      // 0db: invokespecial net/rim/device/api/crypto/AESKey.<init> (I)V
      // 0de: goto 0f0
      // 0e1: new net/rim/device/api/crypto/AESKey
      // 0e4: dup
      // 0e5: aload 3
      // 0e6: bipush 24
      // 0e8: invokeinterface net/rim/device/api/crypto/PseudoRandomSource.getBytes (I)[B 2
      // 0ed: invokespecial net/rim/device/api/crypto/AESKey.<init> ([B)V
      // 0f0: putfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 0f3: goto 125
      // 0f6: aload 0
      // 0f7: aload 3
      // 0f8: ifnonnull 108
      // 0fb: new net/rim/device/api/crypto/AESKey
      // 0fe: dup
      // 0ff: sipush 256
      // 102: invokespecial net/rim/device/api/crypto/AESKey.<init> (I)V
      // 105: goto 117
      // 108: new net/rim/device/api/crypto/AESKey
      // 10b: dup
      // 10c: aload 3
      // 10d: bipush 32
      // 10f: invokeinterface net/rim/device/api/crypto/PseudoRandomSource.getBytes (I)[B 2
      // 114: invokespecial net/rim/device/api/crypto/AESKey.<init> ([B)V
      // 117: putfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 11a: goto 125
      // 11d: new java/lang/IllegalArgumentException
      // 120: dup
      // 121: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 124: athrow
      // 125: aload 0
      // 126: getfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 129: invokestatic net/rim/device/api/crypto/EncryptorFactory.getBlockEncryptorEngine (Lnet/rim/device/api/crypto/Key;)Lnet/rim/device/api/crypto/BlockEncryptorEngine;
      // 12c: astore 5
      // 12e: aload 5
      // 130: instanceof net/rim/device/api/crypto/SymmetricKeyEncryptorEngine
      // 133: ifne 13e
      // 136: new java/lang/RuntimeException
      // 139: dup
      // 13a: invokespecial java/lang/RuntimeException.<init> ()V
      // 13d: athrow
      // 13e: aload 0
      // 13f: new net/rim/device/api/crypto/pgp/PGPCFBEncryptor
      // 142: dup
      // 143: aload 5
      // 145: checkcast net/rim/device/api/crypto/SymmetricKeyEncryptorEngine
      // 148: aload 0
      // 149: getfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._dataOut Lnet/rim/device/api/io/NoCopyByteArrayOutputStream;
      // 14c: invokespecial net/rim/device/api/crypto/pgp/PGPCFBEncryptor.<init> (Lnet/rim/device/api/crypto/SymmetricKeyEncryptorEngine;Ljava/io/OutputStream;)V
      // 14f: putfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._blockEncryptor Lnet/rim/device/api/crypto/pgp/PGPCFBEncryptor;
      // 152: goto 169
      // 155: astore 5
      // 157: new java/lang/RuntimeException
      // 15a: dup
      // 15b: invokespecial java/lang/RuntimeException.<init> ()V
      // 15e: athrow
      // 15f: astore 5
      // 161: new java/lang/RuntimeException
      // 164: dup
      // 165: invokespecial java/lang/RuntimeException.<init> ()V
      // 168: athrow
      // 169: aload 3
      // 16a: ifnull 18b
      // 16d: aload 0
      // 16e: iload 2
      // 16f: aload 3
      // 170: bipush 0
      // 171: invokespecial net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream.addRecipient (ILnet/rim/device/api/crypto/pgp/PGPPseudoRandomSource;Z)V
      // 174: goto 18b
      // 177: astore 5
      // 179: new java/lang/RuntimeException
      // 17c: dup
      // 17d: invokespecial java/lang/RuntimeException.<init> ()V
      // 180: athrow
      // 181: astore 5
      // 183: new java/lang/RuntimeException
      // 186: dup
      // 187: invokespecial java/lang/RuntimeException.<init> ()V
      // 18a: athrow
      // 18b: aload 0
      // 18c: new net/rim/device/api/io/SharedOutputStream
      // 18f: dup
      // 190: aload 0
      // 191: getfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._blockEncryptor Lnet/rim/device/api/crypto/pgp/PGPCFBEncryptor;
      // 194: invokespecial net/rim/device/api/io/SharedOutputStream.<init> (Ljava/io/OutputStream;)V
      // 197: putfield net/rim/device/api/crypto/pgp/PGPEncryptedOutputStream._sharedInternal Lnet/rim/device/api/io/SharedOutputStream;
      // 19a: return
      // try (106 -> 126): 127 null
      // try (106 -> 126): 132 null
      // try (22 -> 144): 145 null
      // try (22 -> 144): 150 null
   }

   public final void addRecipient(byte[] keyID, PublicKey publicKey) throws CryptoUnsupportedOperationException {
      if (keyID != null && publicKey != null) {
         NoCopyByteArrayOutputStream temp1 = new NoCopyByteArrayOutputStream();
         NoCopyByteArrayOutputStream temp2 = new NoCopyByteArrayOutputStream();
         temp1.write(3);
         temp1.write(keyID);
         byte[] sessionKey = new byte[this._sessionKey.getLength() + 3];
         sessionKey[0] = (byte)this._sessionKeyAlgorithm;
         System.arraycopy(this._sessionKey.getData(), 0, sessionKey, 1, this._sessionKey.getLength());
         int keyCheckSum = 0;

         for (int x = 1; x < sessionKey.length - 2; x++) {
            keyCheckSum += sessionKey[x] & 255;
         }

         sessionKey[sessionKey.length - 2] = (byte)(keyCheckSum >> 8);
         sessionKey[sessionKey.length - 1] = (byte)keyCheckSum;
         PublicKeyEncryptorEngine encryptorEngine;
         if (publicKey instanceof DHPublicKey) {
            temp1.write(16);
            encryptorEngine = new ElGamalEncryptorEngine((DHPublicKey)publicKey);
         } else {
            if (!(publicKey instanceof RSAPublicKey)) {
               throw new CryptoUnsupportedOperationException();
            }

            temp1.write(1);
            encryptorEngine = new RSAEncryptorEngine((RSAPublicKey)publicKey);
         }

         BlockEncryptor engine = new BlockEncryptor(new PKCS1FormatterEngine(encryptorEngine), temp2);
         engine.write(sessionKey, 0, sessionKey.length);
         engine.close();
         temp2.close();
         if (publicKey instanceof DHPublicKey) {
            PGPUtilities.writeMPI(temp1, ((ElGamalEncryptorEngine)encryptorEngine).getEphemeralKey().getPublicKeyData());
         }

         PGPUtilities.writeMPI(temp1, temp2.toByteArray(), 0, temp2.size());
         temp1.close();
         byte[] temp1bytes = temp1.toByteArray();
         PGPUtilities.writeTagAndLength(this._keyPackets, 1, temp1.size(), 4);
         this._keyPackets.write(temp1bytes, 0, temp1.size());
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void addRecipient(int symmetricAlgorithm, PGPPseudoRandomSource source) {
      if ((symmetricAlgorithm == 2 || symmetricAlgorithm == 3 || symmetricAlgorithm == 7 || symmetricAlgorithm == 8 || symmetricAlgorithm == 9)
         && source != null) {
         this.addRecipient(symmetricAlgorithm, source, true);
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final void addRecipient(int symmetricAlgorithm, PGPPseudoRandomSource source, boolean passSessionKey) throws CryptoUnsupportedOperationException {
      NoCopyByteArrayOutputStream temp = new NoCopyByteArrayOutputStream();
      byte[] tempAsBytes = new byte[]{4, (byte)symmetricAlgorithm, 0, (byte)PGPUtilities.digestStringToConstant(source.getDigest().getAlgorithm())};
      if (source instanceof PGPSaltedKDFPseudoRandomSource) {
         tempAsBytes[2] = 1;
         temp.write(tempAsBytes);
         temp.write(((PGPSaltedKDFPseudoRandomSource)source).getSalt());
      } else {
         if (!(source instanceof PGPSaltedIteratedKDFPseudoRandomSource)) {
            throw new IllegalArgumentException();
         }

         tempAsBytes[2] = 3;
         temp.write(tempAsBytes);
         temp.write(((PGPSaltedIteratedKDFPseudoRandomSource)source).getSalt());
         temp.write(((PGPSaltedIteratedKDFPseudoRandomSource)source).getCodedCount());
      }

      if (passSessionKey) {
         byte[] sessionKey = new byte[this._sessionKey.getLength() + 1];
         System.arraycopy(this._sessionKey.getData(), 0, sessionKey, 1, sessionKey.length - 1);
         SymmetricKeyEncryptorEngine symmetricEngine;
         switch (symmetricAlgorithm) {
            case 1:
            case 4:
            case 5:
            case 6:
               throw new CryptoUnsupportedOperationException();
            case 2:
            default: {
               byte[] bytesFromPassphrase = source.getBytes(24);
               symmetricEngine = new TripleDESEncryptorEngine(new TripleDESKey(bytesFromPassphrase));
               break;
            }
            case 3:
               byte[] var13 = source.getBytes(16);
               symmetricEngine = new CAST128EncryptorEngine(new CAST128Key(var13));
               break;
            case 7:
               byte[] var12 = source.getBytes(16);
               symmetricEngine = new AESEncryptorEngine(new AESKey(var12));
               break;
            case 8:
               byte[] var11 = source.getBytes(24);
               symmetricEngine = new AESEncryptorEngine(new AESKey(var11));
               break;
            case 9: {
               byte[] bytesFromPassphrase = source.getBytes(32);
               symmetricEngine = new AESEncryptorEngine(new AESKey(bytesFromPassphrase));
            }
         }

         sessionKey[0] = (byte)symmetricAlgorithm;
         CFBEncryptor engine = new CFBEncryptor(symmetricEngine, new InitializationVector(new byte[symmetricEngine.getBlockLength()]), temp, true);
         engine.write(sessionKey, 0, sessionKey.length);
      }

      temp.close();
      byte[] tempbytes = temp.toByteArray();
      PGPUtilities.writeTagAndLength(this._keyPackets, 3, tempbytes.length, 4);
      this._keyPackets.write(tempbytes, 0, temp.size());
   }

   @Override
   public final void write(byte[] b, int off, int len) {
      if (b != null && off >= 0 && len >= 0 && b.length - len >= off) {
         this._blockEncryptor.write(b, off, len);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void close() {
      this._keyPackets.close();
      this._sharedInternal.close();
      this._dataOut.close();
      byte[] encryptedData = this._dataOut.getByteArray();
      PGPUtilities.writeTagAndLength(this._header, 9, this._dataOut.size(), super._tagFormat);
      this._header.write(encryptedData, 0, this._dataOut.size());
      this._header.close();
      super._out.close();
   }

   public final PGPCompressedOutputStream getPGPCompressedOutputStream(int algorithm, int tagFormat) {
      return new PGPCompressedOutputStream(this._sharedInternal.getOutputStream(), algorithm, tagFormat);
   }

   public final PGPEncryptedOutputStream getPGPEncryptedOutputStream(int symmetricAlgorithm, int tagFormat) {
      return new PGPEncryptedOutputStream(this._sharedInternal.getOutputStream(), symmetricAlgorithm, tagFormat);
   }

   public final PGPEncryptedOutputStream getPGPEncryptedOutputStream(int symmetricAlgorithm, PGPPseudoRandomSource source, int tagFormat) {
      return new PGPEncryptedOutputStream(this._sharedInternal.getOutputStream(), symmetricAlgorithm, source, tagFormat);
   }

   public final PGPLiteralOutputStream getPGPLiteralOutputStream(int type, long time, String filename, int tagFormat) {
      return new PGPLiteralOutputStream(this._sharedInternal.getOutputStream(), type, time, filename, tagFormat);
   }

   public final PGPSignedOutputStream getPGPSignedOutputStream(int signatureType, PrivateKey privateKey, byte[] keyID, Digest digest, int tagFormat) {
      return new PGPSignedOutputStream(this._sharedInternal.getOutputStream(), signatureType, privateKey, keyID, digest, tagFormat);
   }

   @Override
   final void update(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         if (super._pgpOut != null) {
            super._pgpOut.update(data, offset, length);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
