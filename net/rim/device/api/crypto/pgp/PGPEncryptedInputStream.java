package net.rim.device.api.crypto.pgp;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.certificate.pgp.PGPKeyIDKeyStoreIndex;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.io.SharedInputStream;

public final class PGPEncryptedInputStream extends PGPInputStream {
   private SharedInputStream _decryptedData;
   private SymmetricKey _symmetricKey;
   private int _symmetricKeyAlgorithm;
   private byte[] _keyID;
   private boolean _mdcPacket;
   private boolean _displayUI;

   PGPEncryptedInputStream(InputStream param1, KeyStore param2, Vector param3, boolean param4, boolean param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 1
      // 002: aload 2
      // 003: invokespecial net/rim/device/api/crypto/pgp/PGPInputStream.<init> (Ljava/io/InputStream;Lnet/rim/device/api/crypto/keystore/KeyStore;)V
      // 006: aload 0
      // 007: iload 4
      // 009: putfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._mdcPacket Z
      // 00c: aload 0
      // 00d: iload 5
      // 00f: putfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._displayUI Z
      // 012: aload 0
      // 013: getfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._mdcPacket Z
      // 016: ifeq 03f
      // 019: aload 1
      // 01a: invokevirtual java/io/InputStream.read ()I
      // 01d: istore 6
      // 01f: iload 6
      // 021: bipush 1
      // 022: if_icmpeq 03f
      // 025: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // 028: dup
      // 029: new java/lang/Object
      // 02c: dup
      // 02d: ldc_w "Ver:"
      // 030: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 033: iload 6
      // 035: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 038: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 03b: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // 03e: athrow
      // 03f: aload 1
      // 040: invokestatic net/rim/device/api/io/SharedInputStream.getSharedInputStream (Ljava/io/InputStream;)Lnet/rim/device/api/io/SharedInputStream;
      // 043: astore 6
      // 045: aload 6
      // 047: invokevirtual net/rim/device/api/io/SharedInputStream.getCurrentPosition ()I
      // 04a: istore 7
      // 04c: bipush 0
      // 04d: multianewarray 92 1
      // 051: astore 8
      // 053: bipush 0
      // 054: istore 9
      // 056: bipush 0
      // 057: istore 10
      // 059: aconst_null
      // 05a: astore 11
      // 05c: bipush 0
      // 05d: istore 12
      // 05f: aload 3
      // 060: invokevirtual java/util/Vector.size ()I
      // 063: istore 13
      // 065: iload 12
      // 067: iload 13
      // 069: if_icmplt 06f
      // 06c: goto 170
      // 06f: aload 3
      // 070: iload 12
      // 072: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 075: checkcast net/rim/device/internal/crypto/pgp/PGPPacket
      // 078: astore 14
      // 07a: aload 14
      // 07c: dup
      // 07d: instanceof net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket
      // 080: ifne 087
      // 083: pop
      // 084: goto 0bb
      // 087: checkcast net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket
      // 08a: astore 15
      // 08c: bipush 0
      // 08d: istore 9
      // 08f: aload 0
      // 090: aload 15
      // 092: invokevirtual net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket.getKeyID ()[B
      // 095: putfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._keyID [B
      // 098: aload 8
      // 09a: aload 0
      // 09b: getfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._keyID [B
      // 09e: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 0a1: aload 0
      // 0a2: aload 15
      // 0a4: aload 2
      // 0a5: aload 0
      // 0a6: getfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._displayUI Z
      // 0a9: invokevirtual net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket.getSessionKey (Lnet/rim/device/api/crypto/keystore/KeyStore;Z)Lnet/rim/device/api/crypto/SymmetricKey;
      // 0ac: putfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._symmetricKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 0af: aload 0
      // 0b0: aload 15
      // 0b2: invokevirtual net/rim/device/internal/crypto/pgp/PGPPublicKeyEncryptedSessionKeyPacket.getSessionKeyAlgorithm ()I
      // 0b5: putfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._symmetricKeyAlgorithm I
      // 0b8: goto 108
      // 0bb: aload 14
      // 0bd: dup
      // 0be: instanceof net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket
      // 0c1: ifne 0c8
      // 0c4: pop
      // 0c5: goto 0eb
      // 0c8: checkcast net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket
      // 0cb: astore 15
      // 0cd: bipush 1
      // 0ce: istore 9
      // 0d0: aload 0
      // 0d1: aload 15
      // 0d3: iload 10
      // 0d5: aload 0
      // 0d6: getfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._displayUI Z
      // 0d9: invokevirtual net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket.getSessionKey (ZZ)Lnet/rim/device/api/crypto/SymmetricKey;
      // 0dc: putfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._symmetricKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 0df: aload 0
      // 0e0: aload 15
      // 0e2: invokevirtual net/rim/device/internal/crypto/pgp/PGPSymmetricKeyEncryptedSessionKeyPacket.getSessionKeyAlgorithm ()I
      // 0e5: putfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._symmetricKeyAlgorithm I
      // 0e8: goto 108
      // 0eb: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // 0ee: dup
      // 0ef: new java/lang/Object
      // 0f2: dup
      // 0f3: ldc_w "Tag:"
      // 0f6: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0f9: aload 14
      // 0fb: invokevirtual net/rim/device/internal/crypto/pgp/PGPPacket.getTag ()I
      // 0fe: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 101: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 104: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // 107: athrow
      // 108: aload 0
      // 109: getfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._symmetricKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 10c: ifnull 16a
      // 10f: aload 0
      // 110: getfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._symmetricKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 113: invokestatic net/rim/device/api/crypto/EncryptorFactory.getBlockEncryptorEngine (Lnet/rim/device/api/crypto/Key;)Lnet/rim/device/api/crypto/BlockEncryptorEngine;
      // 116: checkcast java/lang/Object
      // 119: astore 15
      // 11b: new net/rim/device/api/crypto/pgp/PGPCFBDecryptor
      // 11e: dup
      // 11f: aload 15
      // 121: aload 6
      // 123: aload 0
      // 124: getfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._mdcPacket Z
      // 127: invokespecial net/rim/device/api/crypto/pgp/PGPCFBDecryptor.<init> (Lnet/rim/device/api/crypto/SymmetricKeyEncryptorEngine;Ljava/io/InputStream;Z)V
      // 12a: astore 16
      // 12c: aload 0
      // 12d: aload 16
      // 12f: invokestatic net/rim/device/api/io/SharedInputStream.getSharedInputStream (Ljava/io/InputStream;)Lnet/rim/device/api/io/SharedInputStream;
      // 132: putfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._decryptedData Lnet/rim/device/api/io/SharedInputStream;
      // 135: aload 0
      // 136: aload 0
      // 137: getfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._decryptedData Lnet/rim/device/api/io/SharedInputStream;
      // 13a: aload 0
      // 13b: getfield net/rim/device/api/crypto/pgp/PGPInputStream._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 13e: aload 0
      // 13f: getfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._displayUI Z
      // 142: invokestatic net/rim/device/api/crypto/pgp/PGPInputStream.getPGPInputStream (Ljava/io/InputStream;Lnet/rim/device/api/crypto/keystore/KeyStore;Z)Lnet/rim/device/api/crypto/pgp/PGPInputStream;
      // 145: putfield net/rim/device/api/crypto/pgp/PGPInputStream._input Ljava/io/InputStream;
      // 148: return
      // 149: astore 15
      // 14b: iload 9
      // 14d: ifeq 15d
      // 150: bipush 1
      // 151: istore 10
      // 153: aload 6
      // 155: iload 7
      // 157: invokevirtual net/rim/device/api/io/SharedInputStream.setCurrentPosition (I)V
      // 15a: goto 065
      // 15d: aload 15
      // 15f: astore 11
      // 161: goto 16a
      // 164: astore 15
      // 166: aload 15
      // 168: astore 11
      // 16a: iinc 12 1
      // 16d: goto 065
      // 170: aload 0
      // 171: getfield net/rim/device/api/crypto/pgp/PGPEncryptedInputStream._symmetricKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 174: ifnonnull 1f0
      // 177: aload 11
      // 179: instanceof java/lang/Object
      // 17c: ifeq 18c
      // 17f: new java/lang/Object
      // 182: dup
      // 183: aload 11
      // 185: invokevirtual net/rim/device/api/crypto/CryptoException.toString ()Ljava/lang/String;
      // 188: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> (Ljava/lang/String;)V
      // 18b: athrow
      // 18c: aload 11
      // 18e: ifnull 19e
      // 191: new net/rim/device/api/crypto/pgp/PGPException
      // 194: dup
      // 195: aload 11
      // 197: invokevirtual net/rim/device/api/crypto/CryptoException.toString ()Ljava/lang/String;
      // 19a: invokespecial net/rim/device/api/crypto/pgp/PGPException.<init> (Ljava/lang/String;)V
      // 19d: athrow
      // 19e: aload 8
      // 1a0: arraylength
      // 1a1: ifle 1e8
      // 1a4: new java/lang/Object
      // 1a7: dup
      // 1a8: aload 8
      // 1aa: bipush 0
      // 1ab: aaload
      // 1ac: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.getHexAsciiString ([B)Ljava/lang/String;
      // 1af: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 1b2: astore 14
      // 1b4: bipush 1
      // 1b5: istore 15
      // 1b7: iload 15
      // 1b9: aload 8
      // 1bb: arraylength
      // 1bc: if_icmpge 1db
      // 1bf: aload 14
      // 1c1: bipush 44
      // 1c3: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 1c6: pop
      // 1c7: aload 14
      // 1c9: aload 8
      // 1cb: iload 15
      // 1cd: aaload
      // 1ce: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.getHexAsciiString ([B)Ljava/lang/String;
      // 1d1: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1d4: pop
      // 1d5: iinc 15 1
      // 1d8: goto 1b7
      // 1db: new net/rim/device/api/crypto/pgp/PGPNoKeyFoundException
      // 1de: dup
      // 1df: aload 14
      // 1e1: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1e4: invokespecial net/rim/device/api/crypto/pgp/PGPNoKeyFoundException.<init> (Ljava/lang/String;)V
      // 1e7: athrow
      // 1e8: new net/rim/device/api/crypto/pgp/PGPNoKeyFoundException
      // 1eb: dup
      // 1ec: invokespecial net/rim/device/api/crypto/pgp/PGPNoKeyFoundException.<init> ()V
      // 1ef: athrow
      // 1f0: return
      // try (126 -> 152): 153 null
      // try (126 -> 152): 165 null
   }

   public final SymmetricKey getSessionKey() {
      return this._symmetricKey;
   }

   @Override
   public final PGPInputStream getNextStream() {
      try {
         super._input = PGPInputStream.getPGPInputStream(this._decryptedData, super._keyStore, this._displayUI);
         if (super._input != null) {
            return (PGPInputStream)super._input;
         }
      } catch (PGPException var4) {
         return null;
      } finally {
         return null;
      }

      return null;
   }

   public final byte[] getKeyID() {
      return this._keyID;
   }

   public final PGPCertificate getPGPCertificate(KeyStore keyStore) {
      if (this._keyID != null && keyStore != null) {
         keyStore.addIndex(new PGPKeyIDKeyStoreIndex());
         Enumeration enumeration = keyStore.elements(-2737350786039236692L, this._keyID);
         if (enumeration.hasMoreElements()) {
            KeyStoreData data = (KeyStoreData)enumeration.nextElement();
            return (PGPCertificate)data.getCertificate();
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      return super._input == null ? -1 : super._input.read(b, off, len);
   }

   @Override
   public final int available() {
      return super._input == null ? 0 : super._input.available();
   }

   @Override
   public final long skip(long n) {
      return super._input == null ? 0 : super._input.skip(n);
   }

   @Override
   public final void close() {
      if (super._input != null) {
         super._input.close();
      }

      super.close();
   }

   @Override
   public final String getType() {
      return "Encrypted";
   }

   public final int getContentCipherConstant() {
      return this._symmetricKeyAlgorithm;
   }
}
