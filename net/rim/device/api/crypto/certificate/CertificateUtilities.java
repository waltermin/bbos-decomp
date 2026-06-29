package net.rim.device.api.crypto.certificate;

import java.util.Enumeration;
import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.keystore.AssociatedData;
import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.crypto.keystore.CertificateStatusManagerTicket;
import net.rim.device.api.crypto.keystore.CombinedKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.vm.Array;

public final class CertificateUtilities {
   private static final byte[] BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----".getBytes();
   private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";
   private static final byte[] BEGIN_WTLS_CERTIFICATE = "-----BEGIN WTLS CERTIFICATE-----".getBytes();
   private static final String END_WTLS_CERTIFICATE = "-----END WTLS CERTIFICATE-----";
   private static final byte[] BEGIN_PGP_CERTIFICATE = "-----BEGIN PGP PUBLIC KEY BLOCK-----".getBytes();
   private static final String END_PGP_CERTIFICATE = "-----END PGP PUBLIC KEY BLOCK-----";

   private CertificateUtilities() {
   }

   public static final Certificate[] buildCertificateChain(Certificate certificate, KeyStore keyStore) {
      return buildCertificateChain(certificate, null, keyStore);
   }

   public static final Certificate[] buildCertificateChain(Certificate certificate, Certificate[] pool, KeyStore keyStore) {
      Certificate[][] certificateChains = buildCertificateChains(certificate, pool, keyStore);
      long[] properties = CertificateChainProperties.getCertificateChainProperties(certificateChains, keyStore, null, System.currentTimeMillis());
      int bestChainIndex = CertificateChainProperties.selectBestCertificateChain(properties);
      return certificateChains[bestChainIndex];
   }

   public static final Certificate[][] buildCertificateChains(Certificate certificate, KeyStore keyStore) {
      return buildCertificateChains(certificate, null, keyStore, null);
   }

   public static final Certificate[][] buildCertificateChains(Certificate certificate, KeyStore keyStore, String emailAddress) {
      return buildCertificateChains(certificate, null, keyStore, emailAddress);
   }

   public static final Certificate[][] buildCertificateChains(Certificate certificate, Certificate[] pool, KeyStore keyStore) {
      return buildCertificateChains(certificate, pool, keyStore, null);
   }

   public static final Certificate[][] buildCertificateChains(Certificate certificate, Certificate[] pool, KeyStore keyStore, String emailAddress) {
      return CertificateChainFactory.createCertificateChains(certificate, pool, keyStore, emailAddress);
   }

   public static final Certificate[] buildTrustedCertificateChain(Certificate certificate, Certificate[] pool, KeyStore keyStore, KeyStore trustedKeyStore) {
      if (keyStore == null) {
         keyStore = trustedKeyStore;
      } else if (trustedKeyStore != null) {
         keyStore = new CombinedKeyStore(new KeyStore[]{keyStore, trustedKeyStore});
      }

      Certificate[][] certificateChains = buildCertificateChains(certificate, pool, keyStore);
      long[] certificateProperties = CertificateChainProperties.getCertificateChainProperties(
         certificateChains, keyStore, trustedKeyStore, System.currentTimeMillis()
      );
      int numProperties = certificateProperties.length;

      for (int i = 0; i < numProperties; i++) {
         if ((certificateProperties[i] & 8) == 0) {
            return certificateChains[i];
         }
      }

      return null;
   }

   public static final Certificate[][] buildTrustedCertificateChains(Certificate certificate, Certificate[] pool, KeyStore keyStore, KeyStore trustedKeyStore) {
      if (keyStore == null) {
         keyStore = trustedKeyStore;
      } else if (trustedKeyStore != null) {
         keyStore = new CombinedKeyStore(new KeyStore[]{keyStore, trustedKeyStore});
      }

      Certificate[][] certificateChains = buildCertificateChains(certificate, pool, keyStore);
      long[] certificateProperties = CertificateChainProperties.getCertificateChainProperties(
         certificateChains, keyStore, trustedKeyStore, System.currentTimeMillis()
      );
      int numProperties = certificateProperties.length;
      int numTrustedChains = 0;

      for (int i = 0; i < numProperties; i++) {
         if ((certificateProperties[i] & 8) == 0) {
            certificateChains[numTrustedChains++] = certificateChains[i];
         }
      }

      if (numTrustedChains == 0) {
         return (Certificate[][])null;
      }

      Array.resize(certificateChains, numTrustedChains);
      return certificateChains;
   }

   public static final boolean verifyCertificateChain(Certificate[] chain, KeyStore trustedKeyStore, long date) {
      if (chain != null && chain.length != 0) {
         CertificateStatusManager csm = CertificateStatusManager.getInstance();
         int chainLength = chain.length;
         boolean chainTrusted = false;
         int currentPos = chainLength - 1;
         Certificate currentCert = chain[currentPos];
         if (!currentCert.isRoot()) {
            throw new CertificateVerificationException();
         }

         currentCert.verify();

         while (currentCert.isValid(date)) {
            CertificateStatus currentStatus = csm.getStatus(currentCert);
            if (currentStatus != null && currentStatus.getStatus() == 1) {
               throw new CertificateRevokedException();
            }

            if (trustedKeyStore != null && !chainTrusted) {
               chainTrusted = trustedKeyStore.isMember(currentCert);
            }

            if (--currentPos < 0) {
               for (int i = chainLength - 1; i >= 0; i--) {
                  chain[i].checkCertificateChain(i, chain);
               }

               return chainTrusted;
            }

            PublicKey publicKey = currentCert.getPublicKey();
            currentCert = chain[currentPos];
            currentCert.verify(publicKey);
         }

         throw new CertificateInvalidException();
      } else {
         throw new Object();
      }
   }

   public static final boolean isCertificateChainTrusted(Certificate[] chain, KeyStore trustedKeyStore) {
      long properties = CertificateChainProperties.getCertificateChainProperties(chain, trustedKeyStore, System.currentTimeMillis());
      return (properties & 8) == 0;
   }

   public static final boolean isCertificateTrusted(Certificate certificate, KeyStore trustedKeyStore) {
      return isCertificateTrusted(certificate, null, null, trustedKeyStore);
   }

   public static final boolean isCertificateTrusted(Certificate certificate, Certificate[] pool, KeyStore keyStore, KeyStore trustedKeyStore) {
      Certificate[][] certificateChains = buildCertificateChains(certificate, pool, keyStore);
      long[] properties = CertificateChainProperties.getCertificateChainProperties(certificateChains, keyStore, trustedKeyStore, System.currentTimeMillis());
      int numProperties = properties.length;

      for (int i = 0; i < numProperties; i++) {
         if ((properties[i] & 8) == 0) {
            return true;
         }
      }

      return false;
   }

   public static final String getSubjectFriendlyName(Certificate certificate) {
      return certificate != null ? getFriendlyName(certificate.getSubject()) : null;
   }

   public static final String getFriendlyName(DistinguishedName distinguishedName) {
      if (distinguishedName != null) {
         String commonName = distinguishedName.getString(OIDs.getOID(-1253056853));
         if (commonName != null) {
            return commonName;
         }

         String name = distinguishedName.getString(OIDs.getOID(-1250566485));
         if (name != null) {
            return name;
         }

         StringBuffer buffer = (StringBuffer)(new Object());
         String givenName = distinguishedName.getString(OIDs.getOID(-1250500949));
         if (givenName != null) {
            buffer.append(givenName);
            buffer.append(' ');
         }

         String initial = distinguishedName.getString(OIDs.getOID(-1250435413));
         if (initial != null) {
            buffer.append(initial);
            buffer.append(' ');
         }

         String surname = distinguishedName.getString(OIDs.getOID(-1252991317));
         if (surname != null) {
            buffer.append(surname);
         }

         if (buffer.length() > 0) {
            return buffer.toString();
         }

         String organizationalUnit = distinguishedName.getString(OIDs.getOID(-1252532565));
         if (organizationalUnit != null) {
            buffer.append(organizationalUnit);
         }

         if (buffer.length() > 0) {
            return buffer.toString();
         }

         String organizationName = distinguishedName.getString(OIDs.getOID(-1252598101));
         if (organizationName != null) {
            buffer.append(organizationName);
         }

         if (buffer.length() > 0) {
            return buffer.toString();
         }
      }

      return null;
   }

   public static final void displayCertificateDetails(Certificate certificate) {
      displayCertificateDetails(certificate, null);
   }

   public static final void displayCertificateDetails(Certificate certificate, KeyStore keyStore) {
      displayCertificateDetails(certificate, keyStore, true, null);
   }

   public static final void displayCertificateDetails(
      Certificate certificate, KeyStore keyStore, boolean allowFetchStatus, CertificateStatusManagerTicket ticket
   ) {
      displayCertificateDetails(certificate, null, keyStore, allowFetchStatus, ticket);
   }

   public static final void displayCertificateDetails(
      Certificate certificate, Certificate[] certificatePool, KeyStore keyStore, boolean allowFetchStatus, CertificateStatusManagerTicket ticket
   ) {
      displayCertificateDetails(certificate, certificatePool, keyStore, null, allowFetchStatus, ticket);
   }

   public static final void displayCertificateDetails(
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CryptoSystemProperties cryptoSystemProperties,
      boolean allowFetchStatus,
      CertificateStatusManagerTicket ticket
   ) {
      displayCertificateDetails(certificate, certificatePool, keyStore, cryptoSystemProperties, allowFetchStatus, ticket, false);
   }

   public static final void displayCertificateDetails(
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CryptoSystemProperties cryptoSystemProperties,
      boolean allowFetchStatus,
      CertificateStatusManagerTicket ticket,
      boolean allowFromLockScreen
   ) {
      PopupDialog certificateInfoDialog = CertificateInfoDialogFactory.createCertificateInfoDialog(
         certificate, certificatePool, keyStore, cryptoSystemProperties, allowFetchStatus, ticket, 134217728
      );
      if (allowFromLockScreen) {
         ControlledAccess.assertRRISignatures(true);
         certificateInfoDialog.setStatusPriority(-2147483644);
      }

      BackgroundDialog.show(certificateInfoDialog);
   }

   public static final void displayCertificateChainDetails(String title, Certificate certificate, KeyStore keyStore) {
      Certificate[][] certificateChains = buildCertificateChains(certificate, keyStore);
      displayCertificateChainDetails(title, certificateChains, keyStore);
   }

   public static final void displayCertificateChainDetails(String title, Certificate[] certificateChain) {
      displayCertificateChainDetails(title, new Certificate[][]{certificateChain}, null);
   }

   public static final void displayCertificateChainDetails(String title, Certificate[][] certificateChains, KeyStore keyStore) {
      BackgroundDialog.show(new CertificateChainInfoDialog(title, certificateChains, keyStore, 134217728));
   }

   public static final String calculateThumbprint(Certificate certificate, Digest digest) {
      if (digest != null && certificate != null) {
         digest.reset();
         digest.update(certificate.getEncoding());
         return getHexAsciiString(digest.getDigest());
      } else {
         throw new Object();
      }
   }

   public static final String getHexAsciiString(byte[] data) {
      return getHexAsciiString(data, 0, data == null ? 0 : data.length);
   }

   public static final String getHexAsciiString(byte[] data, int offset, int length) {
      if (data == null || offset < 0 || length < 0 || data.length - length < offset) {
         throw new Object();
      }

      if (data.length != 0 && length != 0) {
         byte[] hexBuffer = new byte[(length << 1) + (length - 1 >>> 1)];
         int hexIndex = 0;

         while (true) {
            byte currentByte = data[offset++];
            hexBuffer[hexIndex++] = (byte)NumberUtilities.intToUpperHexDigit(currentByte >>> 4);
            hexBuffer[hexIndex++] = (byte)NumberUtilities.intToUpperHexDigit(currentByte);
            if (--length <= 0) {
               break;
            }

            currentByte = data[offset++];
            hexBuffer[hexIndex++] = (byte)NumberUtilities.intToUpperHexDigit(currentByte >>> 4);
            hexBuffer[hexIndex++] = (byte)NumberUtilities.intToUpperHexDigit(currentByte);
            if (--length <= 0) {
               break;
            }

            hexBuffer[hexIndex++] = 32;
         }

         return (String)(new Object(hexBuffer));
      } else {
         return "";
      }
   }

   public static final String getKeyUsageString(long keyUsage) {
      int id;
      if (keyUsage == 1) {
         id = 201;
      } else if (keyUsage == 2) {
         id = 202;
      } else if (keyUsage == 4) {
         id = 203;
      } else if (keyUsage == 8) {
         id = 204;
      } else if (keyUsage == 16) {
         id = 205;
      } else if (keyUsage == 32) {
         id = 206;
      } else if (keyUsage == 64) {
         id = 207;
      } else if (keyUsage == 128) {
         id = 208;
      } else if (keyUsage == 256) {
         id = 209;
      } else if (keyUsage == 512) {
         id = 210;
      } else if (keyUsage == 1024) {
         id = 211;
      } else if (keyUsage == 2048) {
         id = 212;
      } else if (keyUsage == 4096) {
         id = 213;
      } else if (keyUsage == 8192) {
         id = 214;
      } else {
         if (keyUsage != 16384) {
            return null;
         }

         id = 215;
      }

      return CertificateResources.getString(id);
   }

   public static final int selectCertificate(RichTextField descriptionField, String[] names, Certificate[] certificates, KeyStore keyStore) {
      return selectCertificate(descriptionField, names, certificates, keyStore, null);
   }

   public static final int selectCertificate(
      RichTextField descriptionField, String[] names, Certificate[] certificates, KeyStore keyStore, KeyStore trustedKeyStore
   ) {
      return selectCertificate(descriptionField, names, certificates, keyStore, trustedKeyStore, null);
   }

   public static final int selectCertificate(
      RichTextField descriptionField,
      String[] names,
      Certificate[] certificates,
      KeyStore keyStore,
      KeyStore trustedKeyStore,
      CryptoSystemProperties cryptoSystemProperties
   ) {
      SelectCertificateDialog dialog = new SelectCertificateDialog(
         descriptionField, certificates, keyStore, trustedKeyStore, cryptoSystemProperties, names, null, false, 134217728
      );
      BackgroundDialog.show(dialog);
      return dialog.getCloseReason() == -1 ? -1 : dialog.getSelectedIndex();
   }

   public static final int[] selectCertificates(RichTextField descriptionField, String[] names, Certificate[] certificates, KeyStore keyStore) {
      return selectCertificates(descriptionField, names, certificates, null, keyStore);
   }

   public static final int[] selectCertificates(
      RichTextField descriptionField, String[] names, Certificate[] certificates, int[] selectedCertificates, KeyStore keyStore
   ) {
      return selectCertificates(descriptionField, names, certificates, selectedCertificates, keyStore, null);
   }

   public static final int[] selectCertificates(
      RichTextField descriptionField, String[] names, Certificate[] certificates, int[] selectedCertificates, KeyStore keyStore, KeyStore trustedKeyStore
   ) {
      return selectCertificates(descriptionField, names, certificates, selectedCertificates, keyStore, trustedKeyStore, null);
   }

   public static final int[] selectCertificates(
      RichTextField descriptionField,
      String[] names,
      Certificate[] certificates,
      int[] selectedCertificates,
      KeyStore keyStore,
      KeyStore trustedKeyStore,
      CryptoSystemProperties cryptoSystemProperties
   ) {
      SelectCertificateDialog dialog = new SelectCertificateDialog(
         descriptionField, certificates, keyStore, trustedKeyStore, cryptoSystemProperties, names, selectedCertificates, true, 134217728
      );
      BackgroundDialog.show(dialog);
      return dialog.getCloseReason() == -1 ? null : dialog.getSelectedIndexes();
   }

   public static final boolean compareDistinguishedNames(DistinguishedName dn1, DistinguishedName dn2) {
      if (dn1 != null && dn2 != null) {
         Enumeration enum1 = dn1.getOIDs();
         Enumeration enum2 = dn2.getOIDs();

         while (enum1.hasMoreElements() && enum2.hasMoreElements()) {
            OID oid = (OID)enum1.nextElement();
            enum2.nextElement();
            if (!StringUtilities.strEqual(dn1.getString(oid), dn2.getString(oid))) {
               return false;
            }
         }

         return enum1.hasMoreElements() != !enum2.hasMoreElements();
      } else {
         throw new Object();
      }
   }

   public static final AssociatedData[] getEmailAssociatedDataArray(Certificate certificate) {
      AssociatedData[] associatedData = null;
      if (certificate != null) {
         String[] addresses = (Object[])certificate.getInformation(-7850001002262082664L, null, null);
         if (addresses != null) {
            int numAddresses = addresses.length;
            associatedData = new AssociatedData[numAddresses];

            for (int i = 0; i < numAddresses; i++) {
               associatedData[i] = new AssociatedData(-1124699153917633064L, StringUtilities.toLowerCase(addresses[i], 1701707776).getBytes());
            }
         }
      }

      return associatedData;
   }

   public static final String getX509WTLSSummaryText(Certificate certificate) {
      DistinguishedName subject = certificate.getSubject();
      DistinguishedName issuer = certificate.getIssuer();
      StringBuffer buffer = (StringBuffer)(new Object());
      buffer.append(CertificateResources.getString(3));
      buffer.append(' ');
      buffer.append(getFriendlyName(subject));
      buffer.append(" ( ");
      buffer.append(subject.toString());
      buffer.append(" )\n\n");
      buffer.append(CertificateResources.getString(9));
      buffer.append(' ');
      buffer.append(getFriendlyName(issuer));
      buffer.append(" ( ");
      buffer.append(issuer.toString());
      buffer.append(" )");
      return buffer.toString();
   }

   public static final javax.microedition.pki.Certificate convertCertificate(Certificate certificate) {
      if (certificate == null) {
         throw new Object();
      }

      Certificate cert = certificate;
      return new CertificateUtilities$1(cert);
   }

   public static final Certificate readCertificateFile(String param0, byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnonnull 00c
      // 004: new java/lang/Object
      // 007: dup
      // 008: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 00b: athrow
      // 00c: aload 0
      // 00d: ifnull 01d
      // 010: aload 0
      // 011: ldc_w "X509"
      // 014: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 017: ifne 01d
      // 01a: goto 0a2
      // 01d: aload 1
      // 01e: bipush 0
      // 01f: getstatic net/rim/device/api/crypto/certificate/CertificateUtilities.BEGIN_CERTIFICATE [B
      // 022: bipush 0
      // 023: getstatic net/rim/device/api/crypto/certificate/CertificateUtilities.BEGIN_CERTIFICATE [B
      // 026: arraylength
      // 027: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 02a: ifeq 07a
      // 02d: new java/lang/Object
      // 030: dup
      // 031: aload 1
      // 032: invokespecial java/lang/String.<init> ([B)V
      // 035: astore 2
      // 036: getstatic net/rim/device/api/crypto/certificate/CertificateUtilities.BEGIN_CERTIFICATE [B
      // 039: arraylength
      // 03a: istore 3
      // 03b: aload 2
      // 03c: getstatic net/rim/device/api/crypto/certificate/CertificateUtilities.BEGIN_CERTIFICATE [B
      // 03f: arraylength
      // 040: invokevirtual java/lang/String.charAt (I)C
      // 043: bipush 10
      // 045: if_icmpne 04e
      // 048: iinc 3 1
      // 04b: goto 051
      // 04e: iinc 3 2
      // 051: aload 2
      // 052: ldc_w "-----END CERTIFICATE-----"
      // 055: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 058: iload 3
      // 059: isub
      // 05a: istore 4
      // 05c: ldc_w "X509"
      // 05f: new java/lang/Object
      // 062: dup
      // 063: new java/lang/Object
      // 066: dup
      // 067: aload 1
      // 068: iload 3
      // 069: iload 4
      // 06b: invokespecial java/io/ByteArrayInputStream.<init> ([BII)V
      // 06e: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 071: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;Ljava/io/InputStream;)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 074: areturn
      // 075: astore 2
      // 076: goto 07a
      // 079: astore 2
      // 07a: ldc_w "X509"
      // 07d: aload 1
      // 07e: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 081: areturn
      // 082: astore 2
      // 083: goto 087
      // 086: astore 2
      // 087: ldc_w "X509"
      // 08a: new java/lang/Object
      // 08d: dup
      // 08e: new java/lang/Object
      // 091: dup
      // 092: aload 1
      // 093: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 096: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 099: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;Ljava/io/InputStream;)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 09c: areturn
      // 09d: astore 2
      // 09e: goto 0a2
      // 0a1: astore 2
      // 0a2: aload 0
      // 0a3: ifnull 0b3
      // 0a6: aload 0
      // 0a7: ldc_w "PGP"
      // 0aa: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0ad: ifne 0b3
      // 0b0: goto 12e
      // 0b3: aload 1
      // 0b4: bipush 0
      // 0b5: getstatic net/rim/device/api/crypto/certificate/CertificateUtilities.BEGIN_PGP_CERTIFICATE [B
      // 0b8: bipush 0
      // 0b9: getstatic net/rim/device/api/crypto/certificate/CertificateUtilities.BEGIN_PGP_CERTIFICATE [B
      // 0bc: arraylength
      // 0bd: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 0c0: ifeq 106
      // 0c3: new java/lang/Object
      // 0c6: dup
      // 0c7: aload 1
      // 0c8: invokespecial java/lang/String.<init> ([B)V
      // 0cb: astore 2
      // 0cc: getstatic net/rim/device/api/crypto/certificate/CertificateUtilities.BEGIN_PGP_CERTIFICATE [B
      // 0cf: arraylength
      // 0d0: istore 3
      // 0d1: aload 2
      // 0d2: getstatic net/rim/device/api/crypto/certificate/CertificateUtilities.BEGIN_PGP_CERTIFICATE [B
      // 0d5: arraylength
      // 0d6: invokevirtual java/lang/String.charAt (I)C
      // 0d9: bipush 10
      // 0db: if_icmpne 0e4
      // 0de: iinc 3 1
      // 0e1: goto 0e7
      // 0e4: iinc 3 2
      // 0e7: aload 2
      // 0e8: ldc_w "-----END PGP PUBLIC KEY BLOCK-----"
      // 0eb: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 0ee: iload 3
      // 0ef: isub
      // 0f0: istore 4
      // 0f2: ldc_w "PGP"
      // 0f5: new java/lang/Object
      // 0f8: dup
      // 0f9: aload 1
      // 0fa: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 0fd: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;Ljava/io/InputStream;)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 100: areturn
      // 101: astore 2
      // 102: goto 106
      // 105: astore 2
      // 106: ldc_w "PGP"
      // 109: aload 1
      // 10a: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 10d: areturn
      // 10e: astore 2
      // 10f: goto 113
      // 112: astore 2
      // 113: ldc_w "PGP"
      // 116: new java/lang/Object
      // 119: dup
      // 11a: new java/lang/Object
      // 11d: dup
      // 11e: aload 1
      // 11f: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 122: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 125: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;Ljava/io/InputStream;)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 128: areturn
      // 129: astore 2
      // 12a: goto 12e
      // 12d: astore 2
      // 12e: aload 0
      // 12f: ifnull 13f
      // 132: aload 0
      // 133: ldc_w "WTLS"
      // 136: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 139: ifne 13f
      // 13c: goto 1b0
      // 13f: aload 1
      // 140: bipush 0
      // 141: getstatic net/rim/device/api/crypto/certificate/CertificateUtilities.BEGIN_WTLS_CERTIFICATE [B
      // 144: bipush 0
      // 145: getstatic net/rim/device/api/crypto/certificate/CertificateUtilities.BEGIN_WTLS_CERTIFICATE [B
      // 148: arraylength
      // 149: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 14c: ifeq 189
      // 14f: new java/lang/Object
      // 152: dup
      // 153: aload 1
      // 154: invokespecial java/lang/String.<init> ([B)V
      // 157: astore 2
      // 158: getstatic net/rim/device/api/crypto/certificate/CertificateUtilities.BEGIN_WTLS_CERTIFICATE [B
      // 15b: arraylength
      // 15c: bipush 2
      // 15e: iadd
      // 15f: istore 3
      // 160: aload 2
      // 161: ldc_w "-----END WTLS CERTIFICATE-----"
      // 164: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 167: iload 3
      // 168: isub
      // 169: istore 4
      // 16b: ldc_w "WTLS"
      // 16e: new java/lang/Object
      // 171: dup
      // 172: new java/lang/Object
      // 175: dup
      // 176: aload 1
      // 177: iload 3
      // 178: iload 4
      // 17a: invokespecial java/io/ByteArrayInputStream.<init> ([BII)V
      // 17d: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 180: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;Ljava/io/InputStream;)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 183: areturn
      // 184: astore 2
      // 185: goto 189
      // 188: astore 2
      // 189: ldc_w "WTLS"
      // 18c: aload 1
      // 18d: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 190: areturn
      // 191: astore 2
      // 192: goto 196
      // 195: astore 2
      // 196: ldc_w "WTLS"
      // 199: new java/lang/Object
      // 19c: dup
      // 19d: new java/lang/Object
      // 1a0: dup
      // 1a1: aload 1
      // 1a2: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 1a5: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 1a8: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;Ljava/io/InputStream;)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 1ab: areturn
      // 1ac: astore 2
      // 1ad: aconst_null
      // 1ae: areturn
      // 1af: astore 2
      // 1b0: aconst_null
      // 1b1: areturn
      // try (13 -> 55): 56 null
      // try (13 -> 55): 58 null
      // try (59 -> 62): 63 null
      // try (59 -> 62): 65 null
      // try (66 -> 75): 76 null
      // try (66 -> 75): 78 null
      // try (86 -> 123): 124 null
      // try (86 -> 123): 126 null
      // try (127 -> 130): 131 null
      // try (127 -> 130): 133 null
      // try (134 -> 143): 144 null
      // try (134 -> 143): 146 null
      // try (154 -> 189): 190 null
      // try (154 -> 189): 192 null
      // try (193 -> 196): 197 null
      // try (193 -> 196): 199 null
      // try (200 -> 209): 210 null
      // try (200 -> 209): 213 null
   }

   public static final String formatDistinguishedName(DistinguishedName dn, char separator) {
      if (dn == null) {
         throw new Object();
      }

      StringBuffer dnStringBuf = (StringBuffer)(new Object(dn.toString()));
      int length = dnStringBuf.length();
      int replaceIndex = -1;

      for (int i = 1; i < length; i++) {
         switch (dnStringBuf.charAt(i)) {
            case ';':
               replaceIndex = i;
               break;
            case '=':
               if (replaceIndex != -1) {
                  dnStringBuf.setCharAt(replaceIndex, separator);
               }
         }
      }

      return dnStringBuf.toString();
   }

   public static final void canonicalizeEmailAddresses(String[] emailAddresses) {
      String canonicalDomainName = ITPolicy.getString(45, 2);
      if (canonicalDomainName != null) {
         for (String currentEmailAddress : emailAddresses) {
            int atIndex = currentEmailAddress.indexOf(64);
            if (atIndex >= 0) {
               String canonicalizedEmailAddress = ((StringBuffer)(new Object()))
                  .append(currentEmailAddress.substring(0, atIndex + 1))
                  .append(canonicalDomainName)
                  .toString();
               if (!containsStringIgnoreCase(emailAddresses, canonicalizedEmailAddress)) {
                  Arrays.add(emailAddresses, canonicalizedEmailAddress);
               }
            }
         }
      }
   }

   private static final boolean containsStringIgnoreCase(String[] stringArray, String string) {
      int numStrings = stringArray.length;

      for (int i = 0; i < numStrings; i++) {
         if (StringUtilities.strEqualIgnoreCase(stringArray[i], string)) {
            return true;
         }
      }

      return false;
   }
}
