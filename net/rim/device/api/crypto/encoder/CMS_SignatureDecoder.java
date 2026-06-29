package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import net.rim.device.api.crypto.InvalidSignatureEncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;

public class CMS_SignatureDecoder extends SignatureDecoder {
   @Override
   protected DecodedSignature decodeSignature(InputStream param1, String param2, String param3) throws InvalidSignatureEncodingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnonnull 0c
      // 04: new java/lang/IllegalArgumentException
      // 07: dup
      // 08: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0b: athrow
      // 0c: new net/rim/device/api/crypto/asn1/ASN1InputStream
      // 0f: dup
      // 10: aload 1
      // 11: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 14: astore 4
      // 16: new net/rim/device/api/crypto/asn1/ASN1InputByteArray
      // 19: dup
      // 1a: aload 4
      // 1c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 1f: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 22: astore 5
      // 24: aload 4
      // 26: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 29: astore 6
      // 2b: aload 5
      // 2d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 30: aload 5
      // 32: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 35: astore 7
      // 37: ldc2_w -5979163936319872658
      // 3a: aload 7
      // 3c: invokestatic net/rim/device/api/crypto/oid/OIDs.getAssociatedString (JLnet/rim/device/api/crypto/oid/OID;)Ljava/lang/String;
      // 3f: astore 8
      // 41: aload 8
      // 43: ifnonnull 49
      // 46: aload 2
      // 47: astore 8
      // 49: aload 8
      // 4b: ifnonnull 56
      // 4e: new net/rim/device/api/crypto/InvalidSignatureEncodingException
      // 51: dup
      // 52: invokespecial net/rim/device/api/crypto/InvalidSignatureEncodingException.<init> ()V
      // 55: athrow
      // 56: aload 8
      // 58: invokestatic net/rim/device/api/crypto/RIMFactoryUtilities.stripLeftMostSubAlgorithm (Ljava/lang/String;)Ljava/lang/String;
      // 5b: astore 9
      // 5d: aload 9
      // 5f: ifnonnull 65
      // 62: aload 3
      // 63: astore 9
      // 65: aload 8
      // 67: invokestatic net/rim/device/api/crypto/RIMFactoryUtilities.getLeftMostSubAlgorithm (Ljava/lang/String;)Ljava/lang/String;
      // 6a: astore 8
      // 6c: ldc_w "CMS"
      // 6f: aload 8
      // 71: invokestatic net/rim/device/api/crypto/encoder/SignatureDecoder.getDecoder (Ljava/lang/String;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/SignatureDecoder;
      // 74: checkcast net/rim/device/api/crypto/encoder/CMS_SignatureDecoder
      // 77: astore 10
      // 79: aload 10
      // 7b: aload 5
      // 7d: aload 6
      // 7f: aload 8
      // 81: aload 9
      // 83: invokevirtual net/rim/device/api/crypto/encoder/CMS_SignatureDecoder.decodeSignature (Lnet/rim/device/api/crypto/asn1/ASN1InputByteArray;[BLjava/lang/String;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/DecodedSignature;
      // 86: areturn
      // 87: astore 4
      // 89: new net/rim/device/api/crypto/InvalidSignatureEncodingException
      // 8c: dup
      // 8d: invokespecial net/rim/device/api/crypto/InvalidSignatureEncodingException.<init> ()V
      // 90: athrow
      // 91: astore 4
      // 93: new net/rim/device/api/crypto/InvalidSignatureEncodingException
      // 96: dup
      // 97: invokespecial net/rim/device/api/crypto/InvalidSignatureEncodingException.<init> ()V
      // 9a: athrow
      // try (6 -> 60): 61 null
      // try (6 -> 60): 66 null
   }

   protected DecodedSignature decodeSignature(ASN1InputByteArray parameters, byte[] encodedSignature, String algorithm, String digestString) {
      throw new RuntimeException();
   }

   @Override
   protected String getEncodingAlgorithm() {
      return "CMS";
   }

   @Override
   protected String[] getSignatureAlgorithms() {
      return null;
   }
}
