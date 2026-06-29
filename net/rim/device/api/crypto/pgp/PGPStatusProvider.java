package net.rim.device.api.crypto.pgp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.crypto.certificate.CertificateServers;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.crypto.certificate.status.ProviderRequestData;
import net.rim.device.api.crypto.certificate.status.ProviderResponseData;
import net.rim.device.api.crypto.certificate.status.ProviderUiContext;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.keystore.PGPKeyStore;

public class PGPStatusProvider extends CertificateStatusProvider {
   private static final long ID = -5040597249237244723L;
   private static final int TAG_RESPONDER_URL = 1;
   private static final int TAG_KEY_ID = 2;
   private static final int TAG_STATUS = 3;
   private static final int TAG_HASH = 4;
   private static final int TAG_CERTIFICATE = 5;

   public PGPStatusProvider() {
      super(-5040597249237244723L);
   }

   @Override
   protected boolean checkCompatibility(Certificate[] certChain, boolean extendedChecking) {
      int length = certChain.length;

      for (int i = 0; i < length; i++) {
         if (!(certChain[i] instanceof PGPCertificate)) {
            return false;
         }
      }

      if (extendedChecking) {
         KeyStore keyStore = PGPKeyStore.getInstance();
         keyStore.addIndex((KeyStoreIndex)(new Object()));
         Enumeration keyStoreEnumeration = keyStore.elements(-2038609988711824737L, certChain[0]);

         while (keyStoreEnumeration.hasMoreElements()) {
            KeyStoreData data = (KeyStoreData)keyStoreEnumeration.nextElement();
            if (data.isPrivateKeySet() || data.isSymmetricKeySet()) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   protected void encodeRequest(Certificate[] certChain, boolean extendedChecking, ProviderRequestData request, KeyStore keyStore, ProviderUiContext uiContext) {
      request.addGlobalField(1, this.getResponderURLs(certChain));
      int length = certChain.length;

      for (int i = 0; i < length; i++) {
         if (!(certChain[i] instanceof PGPCertificate)) {
            throw new Object();
         }

         PGPCertificate certificate = (PGPCertificate)certChain[i];
         request.addCertField(certChain[i], 2, certificate.getKeyID());
         if (extendedChecking) {
            request.addCertField(certChain[i], 4, this.computeHash(certificate));
         }
      }
   }

   @Override
   protected void decodeResponse(Certificate[] param1, boolean param2, ProviderResponseData param3, KeyStore param4, ProviderUiContext param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 3
      // 001: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderResponseData.getCertificates ()Ljava/util/Enumeration; 1
      // 006: astore 6
      // 008: aload 6
      // 00a: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 00f: ifne 015
      // 012: goto 1e9
      // 015: aload 6
      // 017: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 01c: checkcast net/rim/device/api/crypto/certificate/pgp/PGPCertificate
      // 01f: astore 7
      // 021: aload 0
      // 022: aload 3
      // 023: aload 7
      // 025: bipush 3
      // 027: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderResponseData.getCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I)[B 3
      // 02c: invokespecial net/rim/device/api/crypto/pgp/PGPStatusProvider.getStatus ([B)Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 02f: astore 8
      // 031: aload 3
      // 032: aload 7
      // 034: aload 8
      // 036: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderResponseData.setCertificateStatus (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;)V 3
      // 03b: aload 3
      // 03c: aload 7
      // 03e: bipush 5
      // 040: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderResponseData.getCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I)[B 3
      // 045: astore 9
      // 047: aload 9
      // 049: ifnull 008
      // 04c: ldc_w "PGP"
      // 04f: aload 9
      // 051: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 054: checkcast net/rim/device/api/crypto/certificate/pgp/PGPCertificate
      // 057: astore 10
      // 059: aload 4
      // 05b: ifnonnull 063
      // 05e: invokestatic net/rim/device/api/crypto/keystore/PGPKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/PGPKeyStore;
      // 061: astore 4
      // 063: aload 4
      // 065: new net/rim/device/api/crypto/certificate/pgp/PGPKeyIDKeyStoreIndex
      // 068: dup
      // 069: invokespecial net/rim/device/api/crypto/certificate/pgp/PGPKeyIDKeyStoreIndex.<init> ()V
      // 06c: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.addIndex (Lnet/rim/device/api/crypto/keystore/KeyStoreIndex;)Z 2
      // 071: pop
      // 072: invokestatic net/rim/device/api/crypto/keystore/TrustedKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 075: astore 11
      // 077: aload 11
      // 079: new java/lang/Object
      // 07c: dup
      // 07d: invokespecial net/rim/device/api/crypto/certificate/CertificateKeyStoreIndex.<init> ()V
      // 080: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.addIndex (Lnet/rim/device/api/crypto/keystore/KeyStoreIndex;)Z 2
      // 085: pop
      // 086: aconst_null
      // 087: astore 12
      // 089: aconst_null
      // 08a: astore 13
      // 08c: invokestatic net/rim/device/internal/crypto/pgp/PGPUtilities.getResourceBundle ()Lnet/rim/device/api/i18n/ResourceBundle;
      // 08f: astore 14
      // 091: aload 4
      // 093: ldc2_w -2737350786039236692
      // 096: aload 10
      // 098: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getKeyID ()[B
      // 09b: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 0a0: astore 15
      // 0a2: aload 15
      // 0a4: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 0a9: ifne 0af
      // 0ac: goto 008
      // 0af: aload 15
      // 0b1: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0b6: checkcast java/lang/Object
      // 0b9: astore 16
      // 0bb: aload 16
      // 0bd: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 0c2: ifne 0a2
      // 0c5: aload 16
      // 0c7: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isSymmetricKeySet ()Z 1
      // 0cc: ifeq 0d2
      // 0cf: goto 0a2
      // 0d2: aload 16
      // 0d4: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 0d9: checkcast net/rim/device/api/crypto/certificate/pgp/PGPCertificate
      // 0dc: astore 17
      // 0de: aload 17
      // 0e0: ifnonnull 0e6
      // 0e3: goto 0a2
      // 0e6: bipush 0
      // 0e7: istore 18
      // 0e9: aload 11
      // 0eb: ldc2_w -2038609988711824737
      // 0ee: aload 17
      // 0f0: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 0f5: astore 19
      // 0f7: aload 19
      // 0f9: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 0fe: ifeq 15e
      // 101: aload 19
      // 103: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 108: checkcast java/lang/Object
      // 10b: astore 20
      // 10d: aload 13
      // 10f: ifnonnull 138
      // 112: aload 14
      // 114: sipush 8030
      // 117: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 11a: bipush 1
      // 11b: anewarray 372
      // 11e: dup
      // 11f: bipush 0
      // 120: aload 16
      // 122: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 127: aastore
      // 128: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 12b: astore 21
      // 12d: aload 11
      // 12f: aload 21
      // 131: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreTicket; 2
      // 136: astore 13
      // 138: aload 11
      // 13a: aload 20
      // 13c: aload 13
      // 13e: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.removeKey (Lnet/rim/device/api/crypto/keystore/KeyStoreData;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)V 3
      // 143: iload 18
      // 145: ifne 0f7
      // 148: aload 17
      // 14a: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getFingerprint ()[B
      // 14d: aload 10
      // 14f: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getFingerprint ()[B
      // 152: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 155: ifeq 0f7
      // 158: bipush 1
      // 159: istore 18
      // 15b: goto 0f7
      // 15e: aload 12
      // 160: ifnonnull 189
      // 163: aload 14
      // 165: sipush 8030
      // 168: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 16b: bipush 1
      // 16c: anewarray 422
      // 16f: dup
      // 170: bipush 0
      // 171: aload 16
      // 173: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 178: aastore
      // 179: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 17c: astore 20
      // 17e: aload 4
      // 180: aload 20
      // 182: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreTicket; 2
      // 187: astore 12
      // 189: aload 4
      // 18b: aload 16
      // 18d: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getAssociatedData ()[Lnet/rim/device/api/crypto/keystore/AssociatedData; 1
      // 192: aload 16
      // 194: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 199: aload 10
      // 19b: aconst_null
      // 19c: aload 12
      // 19e: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.set ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData; 6
      // 1a3: pop
      // 1a4: aload 4
      // 1a6: aload 16
      // 1a8: aload 12
      // 1aa: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.removeKey (Lnet/rim/device/api/crypto/keystore/KeyStoreData;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)V 3
      // 1af: iload 18
      // 1b1: ifne 1b7
      // 1b4: goto 0a2
      // 1b7: aload 11
      // 1b9: aload 16
      // 1bb: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getAssociatedData ()[Lnet/rim/device/api/crypto/keystore/AssociatedData; 1
      // 1c0: aload 16
      // 1c2: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 1c7: aload 10
      // 1c9: aconst_null
      // 1ca: aload 13
      // 1cc: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.set ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData; 6
      // 1d1: pop
      // 1d2: goto 0a2
      // 1d5: astore 20
      // 1d7: goto 0a2
      // 1da: astore 10
      // 1dc: goto 008
      // 1df: astore 10
      // 1e1: goto 008
      // 1e4: astore 10
      // 1e6: goto 008
      // 1e9: return
      // try (151 -> 178): 179 null
      // try (29 -> 181): 181 null
      // try (29 -> 181): 183 null
      // try (29 -> 181): 185 null
   }

   private CertificateStatus getStatus(byte[] statusEncoding) {
      if (statusEncoding != null && statusEncoding.length > 0) {
         try {
            ByteArrayInputStream input = (ByteArrayInputStream)(new Object(statusEncoding));
            DataInputStream dataIn = (DataInputStream)(new Object(input));
            int status = dataIn.readInt();
            long creationTime = dataIn.readLong();
            return (CertificateStatus)(status == 1
               ? new Object(status, System.currentTimeMillis(), creationTime, -1, creationTime, 0)
               : new Object(status, System.currentTimeMillis(), creationTime, -1, -1));
         } finally {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   private byte[] getResponderURLs(Certificate[] certChain) {
      try {
         ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
         DataOutputStream dataOut = (DataOutputStream)(new Object(output));
         CertificateServers servers = CertificateServers.getInstance();
         dataOut.writeInt(servers.getServerSize(1));
         Enumeration ldapServers = servers.getServers(1);

         while (ldapServers.hasMoreElements()) {
            dataOut.writeUTF(this.getURL((CertificateServerInfo)ldapServers.nextElement(), false));
         }

         dataOut.close();
         output.close();
         return output.toByteArray();
      } finally {
         throw new Object();
      }
   }

   private String getURL(CertificateServerInfo serverInfo, boolean stopAfterPort) {
      StringBuffer buffer = (StringBuffer)(new Object());
      buffer.append("ldap://");
      buffer.append(serverInfo.getServer()).append(':');
      buffer.append(serverInfo.getPort()).append('/');
      if (!stopAfterPort) {
         buffer.append(serverInfo.getBaseQuery()).append('?');
      }

      return buffer.toString();
   }

   private byte[] computeHash(Certificate certificate) {
      SHA1Digest digest = (SHA1Digest)(new Object());
      digest.update(certificate.getEncoding());
      return digest.getDigest();
   }
}
