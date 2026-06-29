package net.rim.device.api.crypto.cms;

import java.io.InputStream;
import net.rim.device.api.crypto.oid.OID;

public final class CMSSignedReceiptInputStream extends CMSInputStream {
   private OID _contentType;
   private byte[] _signedContentIdentifier;
   private byte[] _signatureValue;

   CMSSignedReceiptInputStream(InputStream param1) throws CMSParsingException {
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
      // 05: new java/lang/Object
      // 08: dup
      // 09: aload 0
      // 0a: getfield net/rim/device/api/crypto/cms/CMSInputStream._input Ljava/io/InputStream;
      // 0d: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 10: astore 2
      // 11: aload 2
      // 12: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 15: astore 3
      // 16: aload 3
      // 17: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 1a: istore 4
      // 1c: iload 4
      // 1e: bipush 1
      // 1f: if_icmpeq 2a
      // 22: new net/rim/device/api/crypto/cms/CMSParsingException
      // 25: dup
      // 26: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 29: athrow
      // 2a: aload 0
      // 2b: aload 3
      // 2c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 2f: putfield net/rim/device/api/crypto/cms/CMSSignedReceiptInputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 32: aload 0
      // 33: aload 3
      // 34: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 37: putfield net/rim/device/api/crypto/cms/CMSSignedReceiptInputStream._signedContentIdentifier [B
      // 3a: aload 0
      // 3b: aload 3
      // 3c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 3f: putfield net/rim/device/api/crypto/cms/CMSSignedReceiptInputStream._signatureValue [B
      // 42: return
      // 43: astore 2
      // 44: new net/rim/device/api/crypto/cms/CMSParsingException
      // 47: dup
      // 48: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 4b: athrow
      // 4c: astore 2
      // 4d: return
      // try (3 -> 34): 35 null
      // try (3 -> 34): 40 null
   }

   final CMSReceiptData getCMSReceiptData() {
      return new CMSReceiptData(this._signedContentIdentifier, this._signatureValue, this._contentType, new byte[0], new byte[0]);
   }

   public final OID getReceiptContentType() {
      return this._contentType;
   }

   public final byte[] getSignedContentIdentifier() {
      return this._signedContentIdentifier;
   }

   public final byte[] getSignature() {
      return this._signatureValue;
   }

   @Override
   public final boolean isEncrypted() {
      return false;
   }

   @Override
   public final boolean isSigned() {
      return false;
   }

   @Override
   public final int read(byte[] data, int offset, int length) {
      return -1;
   }

   @Override
   public final void setData(InputStream data) {
   }

   @Override
   public final boolean isContentComplete() {
      return true;
   }
}
