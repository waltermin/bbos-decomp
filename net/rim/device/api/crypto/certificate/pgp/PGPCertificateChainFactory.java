package net.rim.device.api.crypto.certificate.pgp;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainFactory;
import net.rim.device.api.crypto.certificate.CertificateProperties;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.crypto.pgp.PGPUtilities;

public final class PGPCertificateChainFactory extends CertificateChainFactory {
   @Override
   protected final String getType() {
      return "PGP";
   }

   @Override
   protected final Certificate[][] createCertificateChainsInternal(
      Certificate certificate, Certificate[] certificatePool, KeyStore keyStore, String emailAddress
   ) {
      if (certificate == null) {
         throw new Object();
      }

      if (keyStore != null && !keyStore.existsIndex(-2737350786039236692L)) {
         keyStore.addIndex(new PGPKeyIDKeyStoreIndex());
      }

      int poolSize = certificatePool == null ? 0 : certificatePool.length;
      Vector completeThreads = (Vector)(new Object(10));
      Vector threadQueue = (Vector)(new Object(10));
      threadQueue.addElement(new PGPCertificate[]{(PGPCertificate)certificate});
      Hashtable threadPublicKeys = (Hashtable)(new Object(4));
      Vector signerCertificates = (Vector)(new Object(4));

      while (!threadQueue.isEmpty()) {
         PGPCertificate[] thread = (PGPCertificate[])threadQueue.firstElement();
         threadQueue.removeElementAt(0);
         int threadLength = thread.length;
         PGPCertificate currentCert = thread[threadLength - 1];
         if (!PGPUtilities.hasPrivateKey(currentCert, keyStore) && threadLength < 32) {
            signerCertificates.removeAllElements();

            label201:
            try {
               try {
                  byte[][] signerKeyIDs = currentCert.getSignerKeyIDs(emailAddress);
                  if (signerKeyIDs != null) {
                     int numSigners = signerKeyIDs.length;

                     for (int i = 0; i < numSigners; i++) {
                        byte[] currentSignerKeyID = signerKeyIDs[i];

                        for (int j = 0; j < poolSize; j++) {
                           PGPCertificate currentCertificate = (PGPCertificate)certificatePool[j];
                           if (Arrays.equals(currentSignerKeyID, currentCertificate.getKeyID()) && !signerCertificates.contains(currentCertificate)) {
                              signerCertificates.addElement(currentCertificate);
                           }
                        }

                        if (keyStore != null) {
                           Enumeration enumeration = keyStore.elements(-2737350786039236692L, signerKeyIDs[i]);

                           while (enumeration.hasMoreElements()) {
                              KeyStoreData currentData = (KeyStoreData)enumeration.nextElement();
                              PGPCertificate currentCertificate = (PGPCertificate)currentData.getCertificate();
                              if (!signerCertificates.contains(currentCertificate)) {
                                 signerCertificates.addElement(currentCertificate);
                              }
                           }
                        }
                     }
                  }
               } catch (PGPEncodingException var22) {
               }
            } finally {
               break label201;
            }

            threadPublicKeys.clear();

            for (int i = 0; i < threadLength; i++) {
               PublicKey publicKey = thread[i].getPublicKey();
               threadPublicKeys.put(publicKey, publicKey);
            }

            for (int i = signerCertificates.size() - 1; i >= 0; i--) {
               PGPCertificate currentSignerCertificate = (PGPCertificate)signerCertificates.elementAt(i);
               PublicKey currentSignerPublicKey = currentSignerCertificate.getPublicKey();
               if (threadPublicKeys.get(currentSignerPublicKey) != null) {
                  signerCertificates.removeElementAt(i);
               }
            }

            int numSignerCertificates = signerCertificates.size();
            if (numSignerCertificates == 0) {
               completeThreads.addElement(thread);
            } else {
               for (int i = 0; i < numSignerCertificates; i++) {
                  PGPCertificate currentSignerCertificate = (PGPCertificate)signerCertificates.elementAt(i);
                  threadQueue.addElement(this.extendPGPCertificateThread(thread, currentSignerCertificate));
               }
            }
         } else {
            completeThreads.addElement(thread);
         }
      }

      int numThreads = completeThreads.size();
      if (completeThreads.size() == 0) {
         return (Object[][])null;
      }

      PGPCertificate[][] result = new PGPCertificate[numThreads][];

      for (int i = 0; i < numThreads; i++) {
         result[i] = (PGPCertificate[])completeThreads.elementAt(i);
      }

      return result;
   }

   private final PGPCertificate[] extendPGPCertificateThread(PGPCertificate[] thread, PGPCertificate cert) {
      int threadLength = thread.length;
      PGPCertificate[] newThread = new PGPCertificate[threadLength + 1];

      for (int i = 0; i < threadLength; i++) {
         newThread[i] = thread[i];
      }

      newThread[threadLength] = cert;
      return newThread;
   }

   @Override
   protected final long getCertificateChainPropertiesInternal(
      Certificate[] certificateChain, KeyStore keyStore, KeyStore trustedKeyStore, long date, CryptoSystemProperties cryptoSystemProperties
   ) {
      long properties = getChainAndParentKeyProperties(certificateChain, keyStore, trustedKeyStore, date, cryptoSystemProperties);
      PGPCertificate certificate = (PGPCertificate)certificateChain[0];
      long[] subKeyProperties = PGPSubKeyProperties.getPGPSubKeyProperties(certificate, date, cryptoSystemProperties);
      if (subKeyProperties.length > 0) {
         properties |= CertificateProperties.selectBestProperties(subKeyProperties);
      }

      return properties;
   }

   public static final long getChainAndParentKeyProperties(Certificate[] param0, KeyStore param1, KeyStore param2, long param3, CryptoSystemProperties param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: ifnull 009
      // 004: aload 0
      // 005: arraylength
      // 006: ifne 011
      // 009: new java/lang/Object
      // 00c: dup
      // 00d: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 010: athrow
      // 011: aload 0
      // 012: checkcast [Lnet/rim/device/api/crypto/certificate/pgp/PGPCertificate;
      // 015: checkcast [Lnet/rim/device/api/crypto/certificate/pgp/PGPCertificate;
      // 018: astore 6
      // 01a: bipush 8
      // 01c: i2l
      // 01d: lstore 7
      // 01f: aload 6
      // 021: arraylength
      // 022: istore 9
      // 024: iload 9
      // 026: bipush 1
      // 027: isub
      // 028: istore 10
      // 02a: aload 6
      // 02c: iload 10
      // 02e: aaload
      // 02f: astore 11
      // 031: lload 7
      // 033: aload 11
      // 035: lload 3
      // 036: aload 5
      // 038: invokestatic net/rim/device/api/crypto/certificate/CertificateChainFactory.getCommonCertificateProperties (Lnet/rim/device/api/crypto/certificate/Certificate;JLnet/rim/device/api/crypto/CryptoSystemProperties;)J
      // 03b: lor
      // 03c: lstore 7
      // 03e: lload 7
      // 040: bipush 2
      // 042: i2l
      // 043: land
      // 044: bipush 0
      // 045: i2l
      // 046: lcmp
      // 047: ifne 05c
      // 04a: aload 11
      // 04c: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.verify ()V
      // 04f: goto 05c
      // 052: astore 12
      // 054: lload 7
      // 056: bipush 2
      // 058: i2l
      // 059: lor
      // 05a: lstore 7
      // 05c: lload 7
      // 05e: bipush 8
      // 060: i2l
      // 061: land
      // 062: bipush 0
      // 063: i2l
      // 064: lcmp
      // 065: ifeq 08c
      // 068: aload 2
      // 069: ifnull 077
      // 06c: aload 2
      // 06d: aload 11
      // 06f: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.isMember (Lnet/rim/device/api/crypto/certificate/Certificate;)Z 2
      // 074: ifne 084
      // 077: aload 1
      // 078: ifnull 08c
      // 07b: aload 11
      // 07d: aload 1
      // 07e: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.hasPrivateKey (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)Z
      // 081: ifeq 08c
      // 084: lload 7
      // 086: bipush -9
      // 088: i2l
      // 089: land
      // 08a: lstore 7
      // 08c: lload 7
      // 08e: bipush 64
      // 090: i2l
      // 091: land
      // 092: bipush 0
      // 093: i2l
      // 094: lcmp
      // 095: ifne 0f8
      // 098: aload 11
      // 09a: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 09d: astore 12
      // 09f: aload 11
      // 0a1: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getSelfSignatureDigestAlgorithms ()[Ljava/lang/String;
      // 0a4: astore 13
      // 0a6: bipush 0
      // 0a7: istore 14
      // 0a9: iload 14
      // 0ab: aload 13
      // 0ad: arraylength
      // 0ae: if_icmpge 0d5
      // 0b1: aload 13
      // 0b3: iload 14
      // 0b5: aaload
      // 0b6: invokestatic net/rim/device/api/crypto/DigestFactory.isDigestWeakByPolicy (Ljava/lang/String;)Z
      // 0b9: ifeq 0cf
      // 0bc: lload 7
      // 0be: bipush 32
      // 0c0: i2l
      // 0c1: lor
      // 0c2: lstore 7
      // 0c4: lload 7
      // 0c6: bipush 64
      // 0c8: i2l
      // 0c9: lor
      // 0ca: lstore 7
      // 0cc: goto 0d5
      // 0cf: iinc 14 1
      // 0d2: goto 0a9
      // 0d5: lload 7
      // 0d7: bipush 32
      // 0d9: i2l
      // 0da: land
      // 0db: bipush 0
      // 0dc: i2l
      // 0dd: lcmp
      // 0de: ifne 0f8
      // 0e1: aload 5
      // 0e3: aload 12
      // 0e5: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 0ea: invokevirtual net/rim/device/api/crypto/CryptoSystemProperties.isCryptoSystemStrong (Lnet/rim/device/api/crypto/CryptoSystem;)Z
      // 0ed: ifne 0f8
      // 0f0: lload 7
      // 0f2: bipush 32
      // 0f4: i2l
      // 0f5: lor
      // 0f6: lstore 7
      // 0f8: aload 11
      // 0fa: astore 12
      // 0fc: iinc 10 -1
      // 0ff: iload 10
      // 101: ifge 107
      // 104: goto 167
      // 107: aload 6
      // 109: iload 10
      // 10b: aaload
      // 10c: astore 11
      // 10e: lload 7
      // 110: bipush 2
      // 112: i2l
      // 113: land
      // 114: bipush 0
      // 115: i2l
      // 116: lcmp
      // 117: ifeq 11d
      // 11a: goto 02a
      // 11d: aload 11
      // 11f: aload 12
      // 121: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.verify (Lnet/rim/device/api/crypto/certificate/pgp/PGPCertificate;)V
      // 124: lload 7
      // 126: bipush 8
      // 128: i2l
      // 129: land
      // 12a: bipush 0
      // 12b: i2l
      // 12c: lcmp
      // 12d: ifeq 133
      // 130: goto 02a
      // 133: aload 11
      // 135: aload 12
      // 137: iload 10
      // 139: lload 3
      // 13a: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.verifyTrustedIntroducer (Lnet/rim/device/api/crypto/certificate/pgp/PGPCertificate;IJ)V
      // 13d: goto 02a
      // 140: astore 13
      // 142: lload 7
      // 144: bipush 8
      // 146: i2l
      // 147: lor
      // 148: lstore 7
      // 14a: goto 02a
      // 14d: astore 12
      // 14f: lload 7
      // 151: bipush 6
      // 153: i2l
      // 154: lor
      // 155: lstore 7
      // 157: goto 02a
      // 15a: astore 12
      // 15c: lload 7
      // 15e: bipush 2
      // 160: i2l
      // 161: lor
      // 162: lstore 7
      // 164: goto 02a
      // 167: lload 7
      // 169: lreturn
      // try (42 -> 44): 45 null
      // try (163 -> 168): 169 null
      // try (76 -> 137): 176 null
      // try (138 -> 175): 176 null
      // try (76 -> 137): 183 null
      // try (138 -> 175): 183 null
   }
}
