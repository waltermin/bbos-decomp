package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.certificate.CertificateProperties;

public final class PGPSubKeyProperties extends CertificateProperties {
   private static CryptoSystemProperties _defaultCryptoSystemProperties = (CryptoSystemProperties)(new Object());

   private PGPSubKeyProperties() {
   }

   public static final long[] getPGPSubKeyProperties(PGPCertificate certificate, long date, CryptoSystemProperties cryptoSystemProperties) {
      return getPGPSubKeyProperties(certificate, date, 0, cryptoSystemProperties);
   }

   public static final long[] getPGPSigningSubKeyProperties(PGPCertificate certificate, long date, CryptoSystemProperties cryptoSystemProperties) {
      long[] properties = getPGPSubKeyProperties(certificate, date, 1, cryptoSystemProperties);

      for (int i = 0; i < properties.length; i++) {
         if (properties[i] != -1) {
            return properties;
         }
      }

      return null;
   }

   public static final long[] getPGPEncryptionSubKeyProperties(PGPCertificate certificate, long date, CryptoSystemProperties cryptoSystemProperties) {
      long[] properties = getPGPSubKeyProperties(certificate, date, 4, cryptoSystemProperties);

      for (int i = 0; i < properties.length; i++) {
         if (properties[i] != -1) {
            return properties;
         }
      }

      return null;
   }

   private static final long[] getPGPSubKeyProperties(PGPCertificate certificate, long date, long keyUsage, CryptoSystemProperties cryptoSystemProperties) {
      if (cryptoSystemProperties == null) {
         cryptoSystemProperties = _defaultCryptoSystemProperties;
      }

      byte[][] subKeyIDs = certificate.getSubKeyIDs();
      if (subKeyIDs != null && subKeyIDs.length != 0) {
         int numSubKeys = subKeyIDs.length;
         long[] properties = new long[numSubKeys];

         for (int i = 0; i < numSubKeys; i++) {
            if (certificate.queryKeyUsage(subKeyIDs[i], keyUsage) != 0) {
               properties[i] = getPGPSubKeyProperties(certificate, subKeyIDs[i], date, cryptoSystemProperties);
            } else {
               properties[i] = -1;
            }
         }

         return properties;
      } else {
         return new long[0];
      }
   }

   public static final long getPGPSubKeyProperties(PGPCertificate param0, byte[] param1, long param2, CryptoSystemProperties param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 4
      // 02: ifnonnull 0a
      // 05: getstatic net/rim/device/api/crypto/certificate/pgp/PGPSubKeyProperties._defaultCryptoSystemProperties Lnet/rim/device/api/crypto/CryptoSystemProperties;
      // 08: astore 4
      // 0a: bipush 0
      // 0b: i2l
      // 0c: lstore 5
      // 0e: aload 0
      // 0f: aload 1
      // 10: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.verify ([B)V
      // 13: goto 20
      // 16: astore 7
      // 18: lload 5
      // 1a: bipush 2
      // 1c: i2l
      // 1d: lor
      // 1e: lstore 5
      // 20: aload 0
      // 21: lload 2
      // 22: aload 1
      // 23: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.isValid (J[B)Z
      // 26: ifne 32
      // 29: lload 5
      // 2b: sipush 256
      // 2e: i2l
      // 2f: lor
      // 30: lstore 5
      // 32: aload 0
      // 33: aload 1
      // 34: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getStatus ([B)Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 37: astore 7
      // 39: aload 7
      // 3b: ifnull 48
      // 3e: aload 7
      // 40: invokevirtual net/rim/device/api/crypto/certificate/CertificateStatus.getStatus ()I
      // 43: bipush -1
      // 45: if_icmpne 54
      // 48: lload 5
      // 4a: sipush 512
      // 4d: i2l
      // 4e: lor
      // 4f: lstore 5
      // 51: goto 75
      // 54: aload 7
      // 56: invokevirtual net/rim/device/api/crypto/certificate/CertificateStatus.getStatus ()I
      // 59: bipush 1
      // 5a: if_icmpne 75
      // 5d: lload 5
      // 5f: sipush 1024
      // 62: i2l
      // 63: lor
      // 64: lstore 5
      // 66: lload 5
      // 68: ldc_w 65536
      // 6b: i2l
      // 6c: aload 7
      // 6e: invokevirtual net/rim/device/api/crypto/certificate/CertificateStatus.getRevocationReason ()I
      // 71: lshl
      // 72: lor
      // 73: lstore 5
      // 75: aload 7
      // 77: ifnull 82
      // 7a: aload 7
      // 7c: invokevirtual net/rim/device/api/crypto/certificate/CertificateStatus.isStale ()Z
      // 7f: ifeq 8b
      // 82: lload 5
      // 84: sipush 2048
      // 87: i2l
      // 88: lor
      // 89: lstore 5
      // 8b: aload 0
      // 8c: aload 1
      // 8d: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getPublicKey ([B)Lnet/rim/device/api/crypto/PublicKey;
      // 90: astore 8
      // 92: aload 8
      // 94: ifnull b8
      // 97: aload 4
      // 99: aload 8
      // 9b: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // a0: invokevirtual net/rim/device/api/crypto/CryptoSystemProperties.isCryptoSystemStrong (Lnet/rim/device/api/crypto/CryptoSystem;)Z
      // a3: ifne b8
      // a6: lload 5
      // a8: bipush 32
      // aa: i2l
      // ab: lor
      // ac: lstore 5
      // ae: goto b8
      // b1: astore 8
      // b3: goto b8
      // b6: astore 8
      // b8: lload 5
      // ba: lreturn
      // try (7 -> 10): 11 null
      // try (70 -> 86): 87 null
      // try (70 -> 86): 89 null
   }

   public static final int selectBestPGPSubKey(long[] properties, PGPCertificate certificate) {
      if (properties != null && properties.length != 0 && certificate != null) {
         int numProperties = properties.length;
         long bestProperties = CertificateProperties.selectBestProperties(properties);
         int bestSubKeyIndex = -1;
         long latestNotBefore = Long.MIN_VALUE;

         for (int p = 0; p < numProperties; p++) {
            if (properties[p] == bestProperties) {
               byte[] subKeyID = certificate.getSubKeyID(p);
               long notBefore = certificate.getNotBefore(subKeyID);
               if (latestNotBefore < notBefore) {
                  latestNotBefore = notBefore;
                  bestSubKeyIndex = p;
               }
            }
         }

         return bestSubKeyIndex;
      } else {
         throw new Object();
      }
   }
}
