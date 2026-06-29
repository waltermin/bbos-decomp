package net.rim.device.api.crypto.certificate.status.crl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.crypto.certificate.CertificateServers;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.crypto.certificate.status.ProviderRequestData;
import net.rim.device.api.crypto.certificate.status.ProviderResponseData;
import net.rim.device.api.crypto.certificate.status.ProviderUiContext;
import net.rim.device.api.crypto.certificate.status.StatusProviderException;
import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;
import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.vm.Array;

public class CRLProvider extends CertificateStatusProvider {
   private static final long ID;
   private static final int TAG_RESPONDER_URL;
   private static final int TAG_SERIAL_NUMBER;
   private static final int TAG_STATUS;
   private static final int TAG_ISSUER;
   private static final int TAG_ISSUER_KEY;
   private static final int TAG_UNVERIFIED_OK;
   private static final int GOOD;
   private static final int REVOKED;

   public CRLProvider() {
      super(-2255283056435802812L);
   }

   @Override
   protected boolean checkCompatibility(Certificate[] certChain, boolean checkEntireChain) {
      int length = checkEntireChain ? certChain.length : Math.min(certChain.length, 2);

      for (int i = 0; i < length; i++) {
         if (!(certChain[i] instanceof Object)) {
            return false;
         }
      }

      return true;
   }

   @Override
   protected void encodeRequest(Certificate[] param1, boolean param2, ProviderRequestData param3, KeyStore param4, ProviderUiContext param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 3
      // 001: bipush 1
      // 002: aload 0
      // 003: aload 1
      // 004: iload 2
      // 005: invokespecial net/rim/device/api/crypto/certificate/status/crl/CRLProvider.getResponderURLs ([Lnet/rim/device/api/crypto/certificate/Certificate;Z)[B
      // 008: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderRequestData.addGlobalField (I[B)V 3
      // 00d: aload 3
      // 00e: bipush 6
      // 010: bipush 1
      // 011: newarray 8
      // 013: dup
      // 014: bipush 0
      // 015: invokestatic net/rim/device/api/crypto/keystore/KeyStoreOptions.getAllowUnverifiedCRLs ()Z
      // 018: ifeq 01f
      // 01b: bipush 1
      // 01c: goto 020
      // 01f: bipush 0
      // 020: i2b
      // 021: bastore
      // 022: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderRequestData.addGlobalField (I[B)V 3
      // 027: iload 2
      // 028: ifeq 030
      // 02b: aload 1
      // 02c: arraylength
      // 02d: goto 031
      // 030: bipush 1
      // 031: istore 6
      // 033: bipush 0
      // 034: istore 7
      // 036: iload 7
      // 038: iload 6
      // 03a: if_icmplt 040
      // 03d: goto 11b
      // 040: aload 1
      // 041: iload 7
      // 043: aaload
      // 044: instanceof java/lang/Object
      // 047: ifne 052
      // 04a: new net/rim/device/api/crypto/certificate/status/StatusProviderException
      // 04d: dup
      // 04e: invokespecial net/rim/device/api/crypto/certificate/status/StatusProviderException.<init> ()V
      // 051: athrow
      // 052: aload 3
      // 053: aload 1
      // 054: iload 7
      // 056: aaload
      // 057: bipush 2
      // 059: aload 1
      // 05a: iload 7
      // 05c: aaload
      // 05d: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getSerialNumber ()[B 1
      // 062: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderRequestData.addCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I[B)V 4
      // 067: aload 1
      // 068: iload 7
      // 06a: aaload
      // 06b: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getIssuer ()Lnet/rim/device/api/crypto/certificate/DistinguishedName; 1
      // 070: checkcast java/lang/Object
      // 073: astore 8
      // 075: aload 3
      // 076: aload 1
      // 077: iload 7
      // 079: aaload
      // 07a: bipush 4
      // 07c: aload 8
      // 07e: invokevirtual net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.toRFC2253CompatibleString ()Ljava/lang/String;
      // 081: invokevirtual java/lang/String.getBytes ()[B
      // 084: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderRequestData.addCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I[B)V 4
      // 089: aload 0
      // 08a: aload 1
      // 08b: iload 7
      // 08d: aaload
      // 08e: aload 4
      // 090: invokespecial net/rim/device/api/crypto/certificate/status/crl/CRLProvider.getIssuerPublicKeys (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)[Lnet/rim/device/api/crypto/PublicKey;
      // 093: astore 9
      // 095: aload 9
      // 097: arraylength
      // 098: istore 10
      // 09a: iload 10
      // 09c: ifgt 0a2
      // 09f: goto 115
      // 0a2: new java/lang/Object
      // 0a5: dup
      // 0a6: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 0a9: astore 11
      // 0ab: new java/lang/Object
      // 0ae: dup
      // 0af: aload 11
      // 0b1: invokespecial java/io/DataOutputStream.<init> (Ljava/io/OutputStream;)V
      // 0b4: astore 12
      // 0b6: aload 12
      // 0b8: iload 10
      // 0ba: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 0bd: bipush 0
      // 0be: istore 13
      // 0c0: iload 13
      // 0c2: iload 10
      // 0c4: if_icmpge 0f0
      // 0c7: aload 9
      // 0c9: iload 13
      // 0cb: aaload
      // 0cc: ldc_w "X509"
      // 0cf: invokestatic net/rim/device/api/crypto/encoder/PublicKeyEncoder.encode (Lnet/rim/device/api/crypto/PublicKey;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/EncodedKey;
      // 0d2: astore 14
      // 0d4: aload 14
      // 0d6: invokevirtual net/rim/device/api/crypto/encoder/EncodedKey.getEncodedKey ()[B
      // 0d9: astore 15
      // 0db: aload 12
      // 0dd: aload 15
      // 0df: arraylength
      // 0e0: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 0e3: aload 12
      // 0e5: aload 15
      // 0e7: invokevirtual java/io/OutputStream.write ([B)V
      // 0ea: iinc 13 1
      // 0ed: goto 0c0
      // 0f0: aload 12
      // 0f2: invokevirtual java/io/DataOutputStream.close ()V
      // 0f5: aload 11
      // 0f7: invokevirtual java/io/ByteArrayOutputStream.close ()V
      // 0fa: aload 3
      // 0fb: aload 1
      // 0fc: iload 7
      // 0fe: aaload
      // 0ff: bipush 5
      // 101: aload 11
      // 103: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 106: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderRequestData.addCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I[B)V 4
      // 10b: goto 115
      // 10e: astore 9
      // 110: goto 115
      // 113: astore 9
      // 115: iinc 7 1
      // 118: goto 036
      // 11b: return
      // try (68 -> 80): 129 null
      // try (81 -> 128): 129 null
      // try (68 -> 80): 131 null
      // try (81 -> 128): 131 null
   }

   @Override
   protected void decodeResponse(
      Certificate[] cerChain, boolean checkEntireChain, ProviderResponseData response, KeyStore keyStore, ProviderUiContext uiContext
   ) {
      Enumeration certEnum = response.getCertificates();

      while (certEnum.hasMoreElements()) {
         Certificate certificate = (Certificate)certEnum.nextElement();
         CertificateStatus status = this.getStatus(response.getCertField(certificate, 3));
         response.setCertificateStatus(certificate, status);
      }
   }

   private CertificateStatus getStatus(byte[] statusEncoding) {
      if (statusEncoding != null && statusEncoding.length > 0) {
         try {
            ByteArrayInputStream input = (ByteArrayInputStream)(new Object(statusEncoding));
            DataInputStream dataIn = (DataInputStream)(new Object(input));
            int status = dataIn.readInt();
            long thisUpdate = dataIn.readLong();
            long nextUpdate = dataIn.readLong();
            if (status == 1) {
               long revocationDate = dataIn.readLong();
               int revocationReason = dataIn.readInt();
               return (CertificateStatus)(new Object(status, System.currentTimeMillis(), thisUpdate, nextUpdate, revocationDate, revocationReason));
            } else {
               return (CertificateStatus)(new Object(status, System.currentTimeMillis(), thisUpdate, nextUpdate, -1));
            }
         } finally {
            throw new StatusProviderException();
         }
      } else {
         throw new StatusProviderException();
      }
   }

   private byte[] getResponderURLs(Certificate[] certChain, boolean checkEntireChain) {
      try {
         Hashtable hashtable = (Hashtable)(new Object());
         this.addDistributionPoints(certChain, checkEntireChain, hashtable);
         this.addServers(hashtable);
         ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
         DataOutputStream dataOut = (DataOutputStream)(new Object(output));
         dataOut.writeInt(hashtable.size());
         Enumeration enumeration = hashtable.elements();

         while (enumeration.hasMoreElements()) {
            dataOut.writeUTF((String)enumeration.nextElement());
         }

         dataOut.close();
         output.close();
         return output.toByteArray();
      } finally {
         throw new Object();
      }
   }

   private void addDistributionPoints(Certificate[] param1, boolean param2, Hashtable param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: iload 2
      // 01: ifeq 09
      // 04: aload 1
      // 05: arraylength
      // 06: goto 0a
      // 09: bipush 1
      // 0a: istore 4
      // 0c: bipush 0
      // 0d: istore 5
      // 0f: iload 5
      // 11: iload 4
      // 13: if_icmpge 5e
      // 16: aload 1
      // 17: iload 5
      // 19: aaload
      // 1a: ldc_w -1251215445
      // 1d: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 20: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getExtension (Lnet/rim/device/api/crypto/oid/OID;)Lnet/rim/device/api/crypto/certificate/CertificateExtension; 2
      // 25: astore 6
      // 27: aload 6
      // 29: ifnonnull 2f
      // 2c: goto 58
      // 2f: new net/rim/device/api/crypto/certificate/status/crl/CRLDistributionPoint
      // 32: dup
      // 33: aload 6
      // 35: invokespecial net/rim/device/api/crypto/certificate/status/crl/CRLDistributionPoint.<init> (Lnet/rim/device/api/crypto/certificate/CertificateExtension;)V
      // 38: astore 7
      // 3a: aload 0
      // 3b: aload 7
      // 3d: invokevirtual net/rim/device/api/crypto/certificate/status/crl/CRLDistributionPoint.getRelativeDirectories ()[Lnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;
      // 40: aload 3
      // 41: invokespecial net/rim/device/api/crypto/certificate/status/crl/CRLProvider.addRelativeDirectories ([Lnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;Ljava/util/Hashtable;)V
      // 44: aload 0
      // 45: aload 7
      // 47: invokevirtual net/rim/device/api/crypto/certificate/status/crl/CRLDistributionPoint.getURIs ()[Ljava/lang/String;
      // 4a: aload 3
      // 4b: invokespecial net/rim/device/api/crypto/certificate/status/crl/CRLProvider.addURIs ([Ljava/lang/String;Ljava/util/Hashtable;)V
      // 4e: goto 58
      // 51: astore 6
      // 53: goto 58
      // 56: astore 6
      // 58: iinc 5 1
      // 5b: goto 0f
      // 5e: return
      // try (12 -> 21): 38 null
      // try (22 -> 37): 38 null
      // try (12 -> 21): 40 null
      // try (22 -> 37): 40 null
   }

   private void addRelativeDirectories(X509DistinguishedName[] relativeDirectories, Hashtable hashtable) {
      int numDirectories = relativeDirectories.length;
      if (numDirectories != 0) {
         CertificateServers servers = CertificateServers.getInstance();
         Enumeration ldapServers = servers.getServers(1);

         while (ldapServers.hasMoreElements()) {
            CertificateServerInfo serverInfo = (CertificateServerInfo)ldapServers.nextElement();
            StringBuffer buffer = (StringBuffer)(new Object());
            buffer.append("ldap://");
            buffer.append(serverInfo.getServer()).append(':');
            buffer.append(serverInfo.getPort()).append('/');
            String serverAndPort = buffer.toString();

            for (int i = 0; i < numDirectories; i++) {
               String fullServer = ((StringBuffer)(new Object())).append(serverAndPort).append(relativeDirectories[i]).append('?').toString();
               hashtable.put(fullServer, fullServer);
            }
         }
      }
   }

   private void addURIs(String[] uris, Hashtable hashtable) {
      if (uris != null) {
         int uriLength = uris.length;

         for (int i = 0; i < uriLength; i++) {
            hashtable.put(uris[i], uris[i]);
         }
      }
   }

   private void addServers(Hashtable hashtable) {
      CertificateServers servers = CertificateServers.getInstance();
      Enumeration ldapServers = servers.getServers(1);

      while (ldapServers.hasMoreElements()) {
         CertificateServerInfo serverInfo = (CertificateServerInfo)ldapServers.nextElement();
         StringBuffer buffer = (StringBuffer)(new Object());
         buffer.append("ldap://");
         buffer.append(serverInfo.getServer()).append(':');
         buffer.append(serverInfo.getPort()).append('/');
         buffer.append(serverInfo.getBaseQuery()).append('?');
         String url = buffer.toString();
         hashtable.put(url, url);
      }

      Enumeration crlServers = servers.getServers(3);

      while (crlServers.hasMoreElements()) {
         CertificateServerInfo serverInfo = (CertificateServerInfo)crlServers.nextElement();
         String url = serverInfo.getServer();
         hashtable.put(url, url);
      }
   }

   private PublicKey[] getIssuerPublicKeys(Certificate certificate, KeyStore keyStore) {
      PublicKey[] publicKeys = new Object[0];
      if (certificate.isRoot()) {
         return publicKeys;
      }

      if (keyStore == null) {
         keyStore = DeviceKeyStore.getInstance();
      }

      CertificateStatusManager manager = CertificateStatusManager.getInstance();
      keyStore.addIndex((KeyStoreIndex)(new Object()));
      Enumeration enumeration = keyStore.elements(-1581141357654337861L, certificate.getIssuer(), true);

      while (enumeration.hasMoreElements()) {
         KeyStoreData data = (KeyStoreData)enumeration.nextElement();
         Certificate issuerCertificate = data.getCertificate();
         if (issuerCertificate != null && issuerCertificate.isValid()) {
            CertificateStatus status = manager.getStatus(issuerCertificate);
            if (status != null && status.getStatus() != 1) {
               int length = publicKeys.length;
               Array.resize(publicKeys, length + 1);
               publicKeys[length] = data.getPublicKey();
            }
         }
      }

      return publicKeys;
   }
}
