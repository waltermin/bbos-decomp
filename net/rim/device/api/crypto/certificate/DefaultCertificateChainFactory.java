package net.rim.device.api.crypto.certificate;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.InvalidCryptoSystemException;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RIMFactoryUtilities;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.util.ObjectUtilities;

class DefaultCertificateChainFactory extends CertificateChainFactory {
   @Override
   protected String getType() {
      return null;
   }

   @Override
   protected Certificate[][] createCertificateChainsInternal(Certificate certificate, Certificate[] pool, KeyStore keyStore, String emailAddress) {
      if (certificate == null) {
         throw new IllegalArgumentException();
      }

      if (keyStore != null && !keyStore.existsIndex(-1581141357654337861L)) {
         keyStore.addIndex(new SubjectKeyStoreIndex());
      }

      int poolSize = 0;
      DistinguishedName[] poolSubjectDNs = null;
      if (pool != null) {
         poolSize = pool.length;
         poolSubjectDNs = new DistinguishedName[poolSize];

         for (int i = 0; i < poolSize; i++) {
            poolSubjectDNs[i] = pool[i].getSubject();
         }
      }

      Vector chains = new Vector(10);
      Vector certQueue = new Vector(10);
      certQueue.addElement(new Certificate[]{certificate});
      Hashtable chainPublicKeys = new Hashtable(4);
      Vector issuerCerts = new Vector(4);

      while (!certQueue.isEmpty()) {
         Certificate[] chain = (Certificate[])certQueue.firstElement();
         certQueue.removeElementAt(0);
         int chainLength = chain.length;
         Certificate subjectCert = chain[chainLength - 1];
         if (!subjectCert.isRoot() && chainLength < 32) {
            DistinguishedName issuerDN = subjectCert.getIssuer();
            issuerCerts.removeAllElements();

            for (int i = 0; i < poolSize; i++) {
               if (issuerDN.equals(poolSubjectDNs[i]) && !issuerCerts.contains(pool[i])) {
                  issuerCerts.addElement(pool[i]);
               }
            }

            if (keyStore != null) {
               Enumeration enumeration = keyStore.elements(-1581141357654337861L, issuerDN, true);

               while (enumeration.hasMoreElements()) {
                  KeyStoreData data = (KeyStoreData)enumeration.nextElement();
                  Certificate cert = data.getCertificate();
                  if (!issuerCerts.contains(cert)) {
                     issuerCerts.addElement(cert);
                  }
               }
            }

            chainPublicKeys.clear();

            for (int i = 0; i < chainLength; i++) {
               try {
                  PublicKey publicKey = chain[i].getPublicKey();
                  chainPublicKeys.put(publicKey, publicKey);
               } catch (InvalidCryptoSystemException var28) {
               }
            }

            for (int i = issuerCerts.size() - 1; i >= 0; i--) {
               Certificate currentIssuer = (Certificate)issuerCerts.elementAt(i);

               try {
                  PublicKey currentIssuerPublicKey = currentIssuer.getPublicKey();
                  if (chainPublicKeys.get(currentIssuerPublicKey) != null) {
                     issuerCerts.removeElementAt(i);
                  }
               } catch (InvalidCryptoSystemException var27) {
               }
            }

            int numIssuerCerts = issuerCerts.size();
            if (numIssuerCerts > 1) {
               boolean multiplePublicKeys = false;

               try {
                  Certificate firstCertificate = (Certificate)issuerCerts.elementAt(0);
                  PublicKey firstPublicKey = firstCertificate.getPublicKey();

                  for (int i = 1; i < numIssuerCerts; i++) {
                     Certificate currentCertificate = (Certificate)issuerCerts.elementAt(i);
                     if (!ObjectUtilities.objEqual(currentCertificate.getPublicKey(), firstPublicKey)) {
                        multiplePublicKeys = true;
                        break;
                     }
                  }
               } catch (InvalidCryptoSystemException var30) {
               }

               if (multiplePublicKeys) {
                  for (int i = numIssuerCerts - 1; i >= 0; i--) {
                     Certificate currentIssuerCert = (Certificate)issuerCerts.elementAt(i);

                     try {
                        PublicKey currentPublicKey = currentIssuerCert.getPublicKey();

                        try {
                           subjectCert.verify(currentPublicKey);
                        } catch (CertificateVerificationException e) {
                           issuerCerts.removeElementAt(i);
                        }
                     } finally {
                        continue;
                     }
                  }

                  numIssuerCerts = issuerCerts.size();
               }
            }

            if (numIssuerCerts == 0) {
               chains.addElement(chain);
            } else {
               for (int i = 0; i < numIssuerCerts; i++) {
                  Certificate issuerCert = (Certificate)issuerCerts.elementAt(i);
                  certQueue.addElement(this.extendCertificateChain(chain, issuerCert));
               }
            }
         } else {
            chains.addElement(chain);
         }
      }

      int numChains = chains.size();
      if (chains.size() == 0) {
         return (Certificate[][])null;
      }

      Certificate[][] result = new Certificate[numChains][];

      for (int i = 0; i < numChains; i++) {
         result[i] = (Certificate[])chains.elementAt(i);
      }

      return result;
   }

   private Certificate[] extendCertificateChain(Certificate[] chain, Certificate cert) {
      int chainLength = chain.length;
      Certificate[] newChain = new Certificate[chainLength + 1];

      for (int i = 0; i < chainLength; i++) {
         newChain[i] = chain[i];
      }

      newChain[chainLength] = cert;
      return newChain;
   }

   @Override
   protected long getCertificateChainPropertiesInternal(Certificate[] param1, KeyStore param2, KeyStore param3, long param4, CryptoSystemProperties param6) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 8
      // 002: i2l
      // 003: lstore 7
      // 005: aload 1
      // 006: arraylength
      // 007: istore 9
      // 009: aload 1
      // 00a: iload 9
      // 00c: bipush 1
      // 00d: isub
      // 00e: aaload
      // 00f: astore 10
      // 011: aload 10
      // 013: invokeinterface net/rim/device/api/crypto/certificate/Certificate.isRoot ()Z 1
      // 018: ifeq 032
      // 01b: aload 10
      // 01d: invokeinterface net/rim/device/api/crypto/certificate/Certificate.verify ()V 1
      // 022: goto 039
      // 025: astore 11
      // 027: lload 7
      // 029: bipush 2
      // 02b: i2l
      // 02c: lor
      // 02d: lstore 7
      // 02f: goto 039
      // 032: lload 7
      // 034: bipush 1
      // 035: i2l
      // 036: lor
      // 037: lstore 7
      // 039: iload 9
      // 03b: bipush 1
      // 03c: isub
      // 03d: istore 11
      // 03f: iload 11
      // 041: ifge 047
      // 044: goto 11d
      // 047: aload 1
      // 048: iload 11
      // 04a: aaload
      // 04b: astore 12
      // 04d: lload 7
      // 04f: aload 12
      // 051: lload 4
      // 053: aload 6
      // 055: invokestatic net/rim/device/api/crypto/certificate/CertificateChainFactory.getCommonCertificateProperties (Lnet/rim/device/api/crypto/certificate/Certificate;JLnet/rim/device/api/crypto/CryptoSystemProperties;)J
      // 058: lor
      // 059: lstore 7
      // 05b: aload 3
      // 05c: ifnull 07e
      // 05f: lload 7
      // 061: bipush 8
      // 063: i2l
      // 064: land
      // 065: bipush 0
      // 066: i2l
      // 067: lcmp
      // 068: ifeq 07e
      // 06b: aload 3
      // 06c: aload 12
      // 06e: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.isMember (Lnet/rim/device/api/crypto/certificate/Certificate;)Z 2
      // 073: ifeq 07e
      // 076: lload 7
      // 078: bipush -9
      // 07a: i2l
      // 07b: land
      // 07c: lstore 7
      // 07e: aload 12
      // 080: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 085: astore 13
      // 087: aload 12
      // 089: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getSignatureAlgorithm ()Ljava/lang/String; 1
      // 08e: invokestatic net/rim/device/api/crypto/certificate/DefaultCertificateChainFactory.getDigestAlgorithm (Ljava/lang/String;)Ljava/lang/String;
      // 091: astore 14
      // 093: aload 14
      // 095: invokestatic net/rim/device/api/crypto/DigestFactory.isDigestWeakByPolicy (Ljava/lang/String;)Z
      // 098: ifeq 0ae
      // 09b: lload 7
      // 09d: bipush 32
      // 09f: i2l
      // 0a0: lor
      // 0a1: lstore 7
      // 0a3: lload 7
      // 0a5: bipush 64
      // 0a7: i2l
      // 0a8: lor
      // 0a9: lstore 7
      // 0ab: goto 0c5
      // 0ae: aload 6
      // 0b0: aload 13
      // 0b2: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 0b7: invokevirtual net/rim/device/api/crypto/CryptoSystemProperties.isCryptoSystemStrong (Lnet/rim/device/api/crypto/CryptoSystem;)Z
      // 0ba: ifne 0c5
      // 0bd: lload 7
      // 0bf: bipush 32
      // 0c1: i2l
      // 0c2: lor
      // 0c3: lstore 7
      // 0c5: iload 11
      // 0c7: ifle 117
      // 0ca: lload 7
      // 0cc: bipush 2
      // 0ce: i2l
      // 0cf: land
      // 0d0: bipush 0
      // 0d1: i2l
      // 0d2: lcmp
      // 0d3: ifne 117
      // 0d6: aload 1
      // 0d7: iload 11
      // 0d9: bipush 1
      // 0da: isub
      // 0db: aaload
      // 0dc: aload 13
      // 0de: invokeinterface net/rim/device/api/crypto/certificate/Certificate.verify (Lnet/rim/device/api/crypto/PublicKey;)V 2
      // 0e3: goto 117
      // 0e6: astore 13
      // 0e8: lload 7
      // 0ea: bipush 6
      // 0ec: i2l
      // 0ed: lor
      // 0ee: lstore 7
      // 0f0: goto 117
      // 0f3: astore 13
      // 0f5: lload 7
      // 0f7: bipush 6
      // 0f9: i2l
      // 0fa: lor
      // 0fb: lstore 7
      // 0fd: goto 117
      // 100: astore 13
      // 102: lload 7
      // 104: bipush 6
      // 106: i2l
      // 107: lor
      // 108: lstore 7
      // 10a: goto 117
      // 10d: astore 13
      // 10f: lload 7
      // 111: bipush 2
      // 113: i2l
      // 114: lor
      // 115: lstore 7
      // 117: iinc 11 -1
      // 11a: goto 03f
      // 11d: iload 9
      // 11f: bipush 1
      // 120: isub
      // 121: istore 11
      // 123: iload 11
      // 125: iflt 154
      // 128: aload 1
      // 129: iload 11
      // 12b: aaload
      // 12c: iload 11
      // 12e: aload 1
      // 12f: invokeinterface net/rim/device/api/crypto/certificate/Certificate.checkCertificateChain (I[Lnet/rim/device/api/crypto/certificate/Certificate;)V 3
      // 134: goto 14e
      // 137: astore 12
      // 139: lload 7
      // 13b: bipush 16
      // 13d: i2l
      // 13e: lor
      // 13f: lstore 7
      // 141: goto 14e
      // 144: astore 12
      // 146: lload 7
      // 148: bipush 2
      // 14a: i2l
      // 14b: lor
      // 14c: lstore 7
      // 14e: iinc 11 -1
      // 151: goto 123
      // 154: lload 7
      // 156: lreturn
      // try (15 -> 17): 18 null
      // try (67 -> 115): 116 null
      // try (67 -> 115): 123 net/rim/device/api/crypto/InvalidCryptoSystemException
      // try (67 -> 115): 130 null
      // try (67 -> 115): 137 null
      // try (151 -> 157): 158 net/rim/device/api/crypto/certificate/CertificateChainTooLongException
      // try (151 -> 157): 165 net/rim/device/api/crypto/certificate/CertificateVerificationException
   }

   protected static String getDigestAlgorithm(String signatureAlgorithm) {
      String digestAlgorithm = null;
      if (signatureAlgorithm != null) {
         digestAlgorithm = RIMFactoryUtilities.stripLeftMostSubAlgorithm(signatureAlgorithm);
      }

      if (digestAlgorithm == null) {
         digestAlgorithm = "SHA1";
      }

      return digestAlgorithm;
   }
}
