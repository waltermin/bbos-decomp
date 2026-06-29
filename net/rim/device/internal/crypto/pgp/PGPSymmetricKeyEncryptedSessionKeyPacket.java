package net.rim.device.internal.crypto.pgp;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.pgp.PGPEncodingException;

public final class PGPSymmetricKeyEncryptedSessionKeyPacket extends PGPPacket {
   private byte _encryptionAlgorithm;
   private Digest _digest;
   private byte _s2kType;
   private byte[] _salt;
   private byte _codedValue;
   private int _sessionKeyAlgorithm;
   private byte[] _encryptedSessionKeyData;

   public PGPSymmetricKeyEncryptedSessionKeyPacket(int tag, byte[] encoding) {
      super(tag, encoding);
      int offset = 0;
      int version = encoding[offset++];
      switch (version) {
         case 4:
            int digestAlgorithm = 0;
            this._encryptionAlgorithm = encoding[offset++];
            this._s2kType = encoding[offset++];
            byte var16;
            switch (this._s2kType) {
               case 1:
                  var16 = encoding[offset++];
                  this._salt = new byte[8];
                  System.arraycopy(encoding, offset, this._salt, 0, 8);
                  offset += 8;
                  break;
               case 3:
                  var16 = encoding[offset++];
                  this._salt = new byte[8];
                  System.arraycopy(encoding, offset, this._salt, 0, 8);
                  offset += 8;
                  this._codedValue = encoding[offset++];
                  break;
               default:
                  throw new Object(((StringBuffer)(new Object("S2K:"))).append(this._s2kType).toString());
            }

            label28:
            try {
               String digestAlgorithmString = PGPUtilities.digestConstantToString(var16);
               this._digest = DigestFactory.getInstance(digestAlgorithmString);
            } finally {
               break label28;
            }

            int encryptedDataLength = encoding.length - offset;
            this._encryptedSessionKeyData = new byte[encryptedDataLength];
            System.arraycopy(encoding, offset, this._encryptedSessionKeyData, 0, encryptedDataLength);
            return;
         default:
            throw new Object(((StringBuffer)(new Object("Ver:"))).append(version).toString());
      }
   }

   public final SymmetricKey getSessionKey(boolean param1, boolean param2) throws PGPEncodingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: iload 2
      // 01: ifne 06
      // 04: aconst_null
      // 05: areturn
      // 06: new net/rim/device/api/crypto/pgp/PGPPasswordTicket
      // 09: dup
      // 0a: bipush 1
      // 0b: invokespecial net/rim/device/api/crypto/pgp/PGPPasswordTicket.<init> (I)V
      // 0e: astore 3
      // 0f: aload 3
      // 10: aload 0
      // 11: getfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._s2kType B
      // 14: aload 0
      // 15: getfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._salt [B
      // 18: aload 0
      // 19: getfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._digest Lnet/rim/device/api/crypto/Digest;
      // 1c: aload 0
      // 1d: getfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._codedValue B
      // 20: iload 1
      // 21: invokevirtual net/rim/device/api/crypto/pgp/PGPPasswordTicket.getPseudoRandomSource (I[BLnet/rim/device/api/crypto/Digest;BZ)Lnet/rim/device/api/crypto/pgp/PGPPseudoRandomSource;
      // 24: astore 4
      // 26: aload 0
      // 27: getfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._encryptedSessionKeyData [B
      // 2a: arraylength
      // 2b: ifgt 31
      // 2e: goto dc
      // 31: aload 0
      // 32: getfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._encryptionAlgorithm B
      // 35: aload 4
      // 37: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.getSessionKey (ILnet/rim/device/api/crypto/pgp/PGPPseudoRandomSource;)Lnet/rim/device/api/crypto/SymmetricKey;
      // 3a: astore 5
      // 3c: new java/lang/Object
      // 3f: dup
      // 40: aload 0
      // 41: invokespecial net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket.getBlockLength ()I
      // 44: newarray 8
      // 46: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 49: astore 6
      // 4b: new java/lang/Object
      // 4e: dup
      // 4f: invokespecial java/lang/StringBuffer.<init> ()V
      // 52: aload 5
      // 54: invokeinterface net/rim/device/api/crypto/Key.getAlgorithm ()Ljava/lang/String; 1
      // 59: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 5c: ldc_w "/CFB8"
      // 5f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 62: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 65: astore 7
      // 67: aload 5
      // 69: new java/lang/Object
      // 6c: dup
      // 6d: aload 0
      // 6e: getfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._encryptedSessionKeyData [B
      // 71: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 74: aload 7
      // 76: aload 6
      // 78: invokestatic net/rim/device/api/crypto/DecryptorFactory.getDecryptorInputStream (Lnet/rim/device/api/crypto/Key;Ljava/io/InputStream;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/DecryptorInputStream;
      // 7b: astore 8
      // 7d: aload 0
      // 7e: getfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._encryptedSessionKeyData [B
      // 81: arraylength
      // 82: istore 9
      // 84: iload 9
      // 86: newarray 8
      // 88: astore 10
      // 8a: aload 8
      // 8c: aload 10
      // 8e: invokevirtual net/rim/device/api/crypto/CryptoInputStream.read ([B)I
      // 91: istore 11
      // 93: iload 11
      // 95: iload 9
      // 97: if_icmpeq a5
      // 9a: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // 9d: dup
      // 9e: ldc_w "ESLM"
      // a1: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // a4: athrow
      // a5: aload 0
      // a6: aload 10
      // a8: bipush 0
      // a9: baload
      // aa: putfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._sessionKeyAlgorithm I
      // ad: aload 0
      // ae: getfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._sessionKeyAlgorithm I
      // b1: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.encryptionConstantToString (I)Ljava/lang/String;
      // b4: astore 12
      // b6: aload 12
      // b8: aload 10
      // ba: bipush 1
      // bb: iload 11
      // bd: bipush 1
      // be: isub
      // bf: invokestatic net/rim/device/api/crypto/SymmetricKeyFactory.getInstance (Ljava/lang/String;[BII)Lnet/rim/device/api/crypto/SymmetricKey;
      // c2: areturn
      // c3: astore 5
      // c5: goto dc
      // c8: astore 5
      // ca: goto dc
      // cd: astore 5
      // cf: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // d2: dup
      // d3: aload 5
      // d5: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // d8: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // db: athrow
      // dc: aload 0
      // dd: aload 0
      // de: getfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._encryptionAlgorithm B
      // e1: putfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._sessionKeyAlgorithm I
      // e4: aload 0
      // e5: getfield net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket._sessionKeyAlgorithm I
      // e8: aload 4
      // ea: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.getSessionKey (ILnet/rim/device/api/crypto/pgp/PGPPseudoRandomSource;)Lnet/rim/device/api/crypto/SymmetricKey;
      // ed: areturn
      // ee: astore 5
      // f0: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // f3: dup
      // f4: ldc_w "ESKU"
      // f7: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // fa: athrow
      // try (26 -> 93): 94 null
      // try (26 -> 93): 96 null
      // try (26 -> 93): 98 null
      // try (105 -> 113): 114 null
   }

   private final int getBlockLength() {
      switch (this._encryptionAlgorithm) {
         case 1:
         case 4:
         case 5:
         case 6:
            throw new Object(((StringBuffer)(new Object("Sym:"))).append(this._encryptionAlgorithm).toString());
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

   public final int getSessionKeyAlgorithm() {
      return this._sessionKeyAlgorithm;
   }
}
