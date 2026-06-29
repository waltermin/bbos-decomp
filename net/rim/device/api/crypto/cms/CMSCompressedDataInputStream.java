package net.rim.device.api.crypto.cms;

import java.io.InputStream;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.vm.Array;

public final class CMSCompressedDataInputStream extends CMSInputStream {
   private KeyStore _keyStore;
   private OID _compressionType;
   private byte[] _dataBuffer;
   private boolean _displayUI;
   private boolean _contentComplete;

   CMSCompressedDataInputStream(InputStream inputStream, KeyStore keyStore) {
      this(inputStream, keyStore, false, true);
   }

   CMSCompressedDataInputStream(InputStream param1, KeyStore param2, boolean param3, boolean param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: invokespecial net/rim/device/api/crypto/cms/CMSInputStream.<init> (Ljava/io/InputStream;)V
      // 05: aload 0
      // 06: aload 2
      // 07: putfield net/rim/device/api/crypto/cms/CMSCompressedDataInputStream._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 0a: aload 0
      // 0b: iload 4
      // 0d: putfield net/rim/device/api/crypto/cms/CMSCompressedDataInputStream._displayUI Z
      // 10: new java/lang/Object
      // 13: dup
      // 14: aload 0
      // 15: getfield net/rim/device/api/crypto/cms/CMSInputStream._input Ljava/io/InputStream;
      // 18: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 1b: astore 5
      // 1d: aload 5
      // 1f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 22: astore 6
      // 24: aload 6
      // 26: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 29: ifeq 34
      // 2c: new net/rim/device/api/crypto/cms/CMSParsingException
      // 2f: dup
      // 30: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 33: athrow
      // 34: aload 6
      // 36: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 39: astore 7
      // 3b: aload 0
      // 3c: aload 7
      // 3e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 41: putfield net/rim/device/api/crypto/cms/CMSCompressedDataInputStream._compressionType Lnet/rim/device/api/crypto/oid/OID;
      // 44: aload 6
      // 46: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 49: astore 8
      // 4b: aload 0
      // 4c: aload 8
      // 4e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 51: putfield net/rim/device/api/crypto/cms/CMSInputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 54: aload 8
      // 56: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 59: ifeq 69
      // 5c: aload 0
      // 5d: aconst_null
      // 5e: putfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 61: aload 0
      // 62: aconst_null
      // 63: putfield net/rim/device/api/crypto/cms/CMSCompressedDataInputStream._dataBuffer [B
      // 66: goto 8a
      // 69: aload 8
      // 6b: bipush 1
      // 6c: bipush 0
      // 6d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetString (II)Ljava/io/InputStream;
      // 70: astore 9
      // 72: aload 0
      // 73: aload 9
      // 75: iload 3
      // 76: invokespecial net/rim/device/api/crypto/cms/CMSCompressedDataInputStream.setData (Ljava/io/InputStream;Z)V
      // 79: return
      // 7a: astore 5
      // 7c: new net/rim/device/api/crypto/cms/CMSParsingException
      // 7f: dup
      // 80: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 83: athrow
      // 84: astore 5
      // 86: return
      // 87: astore 5
      // 89: return
      // 8a: return
      // try (9 -> 58): 59 null
      // try (9 -> 58): 64 null
      // try (9 -> 58): 66 null
   }

   final void continueInitialization(KeyStore keyStore, boolean displayUI) {
      if (!(super._data instanceof CMSSignedDataInputStream)) {
         if (!(super._data instanceof CMSEnvelopedDataInputStream)) {
            if (!(super._data instanceof CMSCompressedDataInputStream)) {
               if (super._data != null && this._dataBuffer != null && this._compressionType.equals(OIDs.getOID(-1721348808))) {
                  super._data = (InputStream)(new Object((InputStream)(new Object(this._dataBuffer))));
               }
            } else {
               ((CMSCompressedDataInputStream)super._data).continueInitialization(this._keyStore, displayUI);
            }
         } else {
            ((CMSEnvelopedDataInputStream)super._data).continueInitialization(this._keyStore, null, displayUI);
         }
      } else {
         ((CMSSignedDataInputStream)super._data).continueInitialization(this._keyStore, displayUI);
      }
   }

   @Override
   public final boolean isSigned() {
      return !(super._data instanceof CMSInputStream) ? false : ((CMSInputStream)super._data).isSigned();
   }

   @Override
   public final boolean isEncrypted() {
      return !(super._data instanceof CMSInputStream) ? false : ((CMSInputStream)super._data).isEncrypted();
   }

   @Override
   public final int read(byte[] buffer, int offset, int length) {
      if (buffer == null || offset < 0 || length < 0 || buffer.length - length < offset) {
         throw new Object();
      } else {
         return super._data == null ? -1 : super._data.read(buffer, offset, length);
      }
   }

   @Override
   public final int available() {
      return super._data == null ? 0 : super._data.available();
   }

   @Override
   public final long skip(long n) {
      return super._data == null ? 0 : super._data.skip(n);
   }

   @Override
   public final void setData(InputStream data) {
      this.setData(data, false);
   }

   private final void setData(InputStream data, boolean twoStage) {
      this._dataBuffer = null;
      if (!this._compressionType.equals(OIDs.getOID(-1721348808))) {
         throw new CMSNoSuchAlgorithmException();
      }

      InputStream decoded = new Object(data);
      if (super._contentType.equals(OIDs.getOID(542121532))) {
         super._data = new CMSSignedDataInputStream((InputStream)decoded, this._keyStore, twoStage, this._displayUI);
      } else if (super._contentType.equals(OIDs.getOID(542383676))) {
         super._data = new CMSEnvelopedDataInputStream((InputStream)decoded, this._keyStore, null, twoStage, this._displayUI);
      } else if (super._contentType.equals(OIDs.getOID(-1721352904))) {
         super._data = new CMSCompressedDataInputStream((InputStream)decoded, this._keyStore, twoStage, this._displayUI);
      } else {
         int offset = 0;
         this._dataBuffer = new byte[100];

         while (true) {
            int len = data.read(this._dataBuffer, offset, 100);
            if (len == -1) {
               break;
            }

            if (len < 100) {
               byte[] tem = new byte[offset + len];
               System.arraycopy(this._dataBuffer, 0, tem, 0, offset + len);
               Array.resize(this._dataBuffer, offset + len);
               System.arraycopy(tem, 0, this._dataBuffer, 0, offset + len);
               break;
            }

            Array.resize(this._dataBuffer, this._dataBuffer.length + 100);
            offset += 100;
         }

         if (this._compressionType.equals(OIDs.getOID(-1721348808))) {
            super._data = (InputStream)(new Object((InputStream)(new Object(this._dataBuffer))));
         }
      }

      this._contentComplete = true;
   }

   @Override
   public final boolean isContentComplete() {
      return this._contentComplete;
   }
}
