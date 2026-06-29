package net.rim.device.api.crypto.certificate.pgp;

import java.io.InputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateFactory;

final class PGPCertificateFactory extends CertificateFactory {
   @Override
   public final Certificate createCertificate(InputStream param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: new net/rim/device/api/crypto/pgp/PGPArmorDecoder
      // 03: dup
      // 04: aload 1
      // 05: invokespecial net/rim/device/api/crypto/pgp/PGPArmorDecoder.<init> (Ljava/io/InputStream;)V
      // 08: astore 2
      // 09: aload 2
      // 0a: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.numCertificates ()I
      // 0d: ifle 17
      // 10: aload 2
      // 11: bipush 0
      // 12: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.getCertificate (I)Lnet/rim/device/api/crypto/certificate/pgp/PGPCertificate;
      // 15: areturn
      // 16: astore 2
      // 17: new net/rim/device/api/crypto/certificate/pgp/PGPCertificate
      // 1a: dup
      // 1b: aload 1
      // 1c: invokespecial net/rim/device/api/crypto/certificate/pgp/PGPCertificate.<init> (Ljava/io/InputStream;)V
      // 1f: areturn
      // 20: astore 2
      // 21: new java/lang/Object
      // 24: dup
      // 25: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 28: athrow
      // 29: astore 2
      // 2a: new java/lang/Object
      // 2d: dup
      // 2e: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 31: athrow
      // 32: astore 2
      // 33: new java/lang/Object
      // 36: dup
      // 37: invokespecial net/rim/device/api/crypto/NoSuchAlgorithmException.<init> ()V
      // 3a: athrow
      // try (0 -> 11): 12 null
      // try (13 -> 17): 18 net/rim/device/api/crypto/pgp/PGPEncodingException
      // try (13 -> 17): 23 null
      // try (13 -> 17): 28 null
   }

   @Override
   public final Certificate createCertificate(byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: new net/rim/device/api/crypto/pgp/PGPArmorDecoder
      // 03: dup
      // 04: new java/lang/Object
      // 07: dup
      // 08: aload 1
      // 09: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 0c: invokespecial net/rim/device/api/crypto/pgp/PGPArmorDecoder.<init> (Ljava/io/InputStream;)V
      // 0f: astore 2
      // 10: aload 2
      // 11: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.numCertificates ()I
      // 14: ifle 1e
      // 17: aload 2
      // 18: bipush 0
      // 19: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.getCertificate (I)Lnet/rim/device/api/crypto/certificate/pgp/PGPCertificate;
      // 1c: areturn
      // 1d: astore 2
      // 1e: new net/rim/device/api/crypto/certificate/pgp/PGPCertificate
      // 21: dup
      // 22: aload 1
      // 23: invokespecial net/rim/device/api/crypto/certificate/pgp/PGPCertificate.<init> ([B)V
      // 26: areturn
      // 27: astore 2
      // 28: new java/lang/Object
      // 2b: dup
      // 2c: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 2f: athrow
      // 30: astore 2
      // 31: new java/lang/Object
      // 34: dup
      // 35: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 38: athrow
      // 39: astore 2
      // 3a: new java/lang/Object
      // 3d: dup
      // 3e: invokespecial net/rim/device/api/crypto/NoSuchAlgorithmException.<init> ()V
      // 41: athrow
      // try (0 -> 14): 15 null
      // try (16 -> 20): 21 net/rim/device/api/crypto/pgp/PGPEncodingException
      // try (16 -> 20): 26 null
      // try (16 -> 20): 31 null
   }

   @Override
   public final String getType() {
      return "PGP";
   }
}
