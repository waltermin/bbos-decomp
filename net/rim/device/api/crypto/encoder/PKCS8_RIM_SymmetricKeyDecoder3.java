package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.asn1.ASN1InputStream;

final class PKCS8_RIM_SymmetricKeyDecoder3 extends PKCS8_SymmetricKeyDecoder {
   @Override
   public final SymmetricKey decodeKey(ASN1InputStream param1, ASN1InputStream param2, String param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 3
      // 01: ldc_w "RC2"
      // 04: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 07: ifeq 22
      // 0a: aload 1
      // 0b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 0e: istore 4
      // 10: aload 2
      // 11: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 14: astore 5
      // 16: new net/rim/device/api/crypto/RC2Key
      // 19: dup
      // 1a: aload 5
      // 1c: iload 4
      // 1e: invokespecial net/rim/device/api/crypto/RC2Key.<init> ([BI)V
      // 21: areturn
      // 22: aload 3
      // 23: ldc_w "Skipjack"
      // 26: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 29: ifeq 3d
      // 2c: aload 2
      // 2d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 30: astore 4
      // 32: new net/rim/device/api/crypto/SkipjackKey
      // 35: dup
      // 36: aload 4
      // 38: bipush 0
      // 39: invokespecial net/rim/device/api/crypto/SkipjackKey.<init> ([BI)V
      // 3c: areturn
      // 3d: aload 3
      // 3e: ldc_w "CAST128"
      // 41: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 44: ifeq 6c
      // 47: aload 2
      // 48: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 4b: astore 4
      // 4d: new net/rim/device/api/crypto/CAST128Key
      // 50: dup
      // 51: aload 4
      // 53: bipush 0
      // 54: invokespecial net/rim/device/api/crypto/CAST128Key.<init> ([BI)V
      // 57: areturn
      // 58: astore 4
      // 5a: new java/lang/Object
      // 5d: dup
      // 5e: invokespecial net/rim/device/api/crypto/InvalidKeyEncodingException.<init> ()V
      // 61: athrow
      // 62: astore 4
      // 64: new java/lang/Object
      // 67: dup
      // 68: invokespecial net/rim/device/api/crypto/InvalidKeyEncodingException.<init> ()V
      // 6b: athrow
      // 6c: new java/lang/Object
      // 6f: dup
      // 70: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 73: athrow
      // try (0 -> 15): 42 null
      // try (16 -> 28): 42 null
      // try (29 -> 41): 42 null
      // try (0 -> 15): 47 null
      // try (16 -> 28): 47 null
      // try (29 -> 41): 47 null
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "PKCS8";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"RC2", "Skipjack", "CAST128"};
   }
}
