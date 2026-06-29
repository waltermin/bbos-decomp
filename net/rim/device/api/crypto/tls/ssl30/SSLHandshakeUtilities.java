package net.rim.device.api.crypto.tls.ssl30;

import net.rim.device.api.crypto.DHKeyPair;
import net.rim.device.api.crypto.DHPublicKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.tls.TLSDeviceOptionStore;
import net.rim.device.api.crypto.tls.TLSUtilities;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.ssl.SSLConnectionOptions;
import net.rim.device.internal.ui.component.BackgroundDialog;

public final class SSLHandshakeUtilities implements SSLRecordProtocolConstants {
   private static final long KEY = 4941551061071482255L;
   private static final boolean DEBUG = false;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(5710659227867441061L, "net.rim.device.internal.resource.crypto.SSL");

   private SSLHandshakeUtilities() {
   }

   public static final void verifyCertificateCapabilities(X509Certificate param0, SSLConnectionOptions param1, SSLRecordProtocol param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 2
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 2
      // 001: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getPendingRead ()Ljava/lang/Object;
      // 004: astore 3
      // 005: aload 3
      // 006: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getKeyExchangeAlgorithm ()B
      // 009: istore 4
      // 00b: aconst_null
      // 00c: astore 5
      // 00e: iload 4
      // 010: tableswitch 88 0 17 402 126 88 252 357 366 213 175 126 88 393 402 329 291 348 402 375 384
      // 068: aload 0
      // 069: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 06c: astore 5
      // 06e: aload 5
      // 070: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 075: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 07a: sipush 1024
      // 07d: if_icmple 08e
      // 080: aload 2
      // 081: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 084: bipush 60
      // 086: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 089: goto 08e
      // 08c: astore 6
      // 08e: aload 5
      // 090: ifnonnull 09e
      // 093: aload 0
      // 094: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 097: astore 5
      // 099: goto 09e
      // 09c: astore 6
      // 09e: aload 5
      // 0a0: instanceof java/lang/Object
      // 0a3: ifeq 0b3
      // 0a6: aload 0
      // 0a7: bipush 4
      // 0a9: i2l
      // 0aa: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.queryKeyUsage (J)I
      // 0ad: ifeq 0b3
      // 0b0: goto 1b6
      // 0b3: aload 2
      // 0b4: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 0b7: bipush 46
      // 0b9: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 0bc: goto 1b6
      // 0bf: aload 0
      // 0c0: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 0c3: astore 5
      // 0c5: aload 5
      // 0c7: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 0cc: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 0d1: sipush 1024
      // 0d4: if_icmple 0e5
      // 0d7: aload 2
      // 0d8: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 0db: bipush 60
      // 0dd: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 0e0: goto 0e5
      // 0e3: astore 6
      // 0e5: aload 5
      // 0e7: ifnonnull 0f5
      // 0ea: aload 0
      // 0eb: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 0ee: astore 5
      // 0f0: goto 0f5
      // 0f3: astore 6
      // 0f5: aload 5
      // 0f7: instanceof java/lang/Object
      // 0fa: ifeq 100
      // 0fd: goto 1b6
      // 100: aload 2
      // 101: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 104: bipush 46
      // 106: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 109: goto 1b6
      // 10c: aload 5
      // 10e: ifnonnull 11c
      // 111: aload 0
      // 112: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 115: astore 5
      // 117: goto 11c
      // 11a: astore 6
      // 11c: aload 5
      // 11e: instanceof java/lang/Object
      // 121: ifeq 127
      // 124: goto 1b6
      // 127: aload 2
      // 128: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 12b: bipush 46
      // 12d: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 130: goto 1b6
      // 133: aload 0
      // 134: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 137: astore 5
      // 139: aload 5
      // 13b: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 140: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 145: sipush 163
      // 148: if_icmple 159
      // 14b: aload 2
      // 14c: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 14f: bipush 60
      // 151: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 154: goto 159
      // 157: astore 6
      // 159: aload 5
      // 15b: ifnonnull 1b6
      // 15e: aload 0
      // 15f: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 162: astore 5
      // 164: goto 1b6
      // 167: astore 6
      // 169: goto 1b6
      // 16c: aload 2
      // 16d: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 170: bipush 40
      // 172: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 175: aload 2
      // 176: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 179: bipush 40
      // 17b: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 17e: aload 2
      // 17f: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 182: bipush 40
      // 184: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 187: aload 2
      // 188: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 18b: bipush 40
      // 18d: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 190: aload 2
      // 191: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 194: bipush 40
      // 196: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 199: aload 2
      // 19a: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 19d: bipush 40
      // 19f: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 1a2: aload 2
      // 1a3: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 1a6: bipush 40
      // 1a8: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 1ab: return
      // 1ac: astore 3
      // 1ad: aload 2
      // 1ae: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 1b1: bipush 40
      // 1b3: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 1b6: return
      // try (10 -> 22): 23 null
      // try (26 -> 29): 30 null
      // try (45 -> 57): 58 null
      // try (61 -> 64): 65 null
      // try (77 -> 80): 81 null
      // try (91 -> 103): 104 null
      // try (107 -> 110): 111 null
      // try (0 -> 141): 142 null
   }

   public static final boolean checkCertificateType(Certificate certificate, byte[] types) {
      PublicKey publicKey = null;

      label42:
      try {
         publicKey = certificate.getPublicKey();
      } finally {
         break label42;
      }

      int typeLength = types.length;
      int i = 0;

      while (i < typeLength) {
         switch (types[i]) {
            case 1:
            default:
               if (publicKey instanceof Object) {
                  return true;
               }
            case 2:
               if (publicKey instanceof Object) {
                  return true;
               }
            case 0:
               i++;
         }
      }

      return false;
   }

   public static final boolean serverAcceptsCertChain(Certificate[] certChain, DistinguishedName[] dn) {
      int dnLength = dn.length;
      int rootCert = certChain.length;

      for (int i = 0; i < dnLength; i++) {
         if (certChain[rootCert - 1].getIssuer().equals(dn[i])) {
            return true;
         }
      }

      return false;
   }

   public static final boolean checkForDH(Certificate certificate, PublicKey publicKey, SSLConnectionState write) {
      byte keyExchange = write.getKeyExchangeAlgorithm();
      if (keyExchange == 3 && keyExchange == 4 && keyExchange == 5) {
         try {
            return checkForDH(certificate.getPublicKey(), publicKey);
         } finally {
            ;
         }
      } else {
         return true;
      }
   }

   public static final boolean checkForDH(PublicKey param0, PublicKey param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: instanceof java/lang/Object
      // 04: ifne 09
      // 07: bipush 0
      // 08: ireturn
      // 09: aload 0
      // 0a: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 0f: checkcast java/lang/Object
      // 12: astore 2
      // 13: aload 1
      // 14: instanceof java/lang/Object
      // 17: ifne 1c
      // 1a: bipush 0
      // 1b: ireturn
      // 1c: aload 1
      // 1d: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 22: checkcast java/lang/Object
      // 25: astore 3
      // 26: aload 2
      // 27: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getP ()[B
      // 2a: aload 3
      // 2b: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getP ()[B
      // 2e: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 31: ifeq 56
      // 34: aload 2
      // 35: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getQ ()[B
      // 38: aload 3
      // 39: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getQ ()[B
      // 3c: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 3f: ifeq 56
      // 42: aload 2
      // 43: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getG ()[B
      // 46: aload 3
      // 47: invokevirtual net/rim/device/api/crypto/DHCryptoSystem.getG ()[B
      // 4a: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 4d: ifeq 56
      // 50: bipush 1
      // 51: ireturn
      // 52: astore 2
      // 53: bipush 0
      // 54: ireturn
      // 55: astore 2
      // 56: bipush 0
      // 57: ireturn
      // try (0 -> 4): 38 null
      // try (5 -> 13): 38 null
      // try (14 -> 37): 38 null
      // try (0 -> 4): 41 null
      // try (5 -> 13): 41 null
      // try (14 -> 37): 41 null
   }

   public static final boolean checkCipherSuite(int[] cipherSuites, int cipherSuite) {
      int cipherSuiteLength = cipherSuites.length;

      for (int i = 0; i < cipherSuiteLength; i++) {
         if (cipherSuites[i] == cipherSuite) {
            return true;
         }
      }

      return false;
   }

   public static final DHKeyPair findDHKeyPair(DHPublicKey param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/crypto/keystore/DeviceKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 03: astore 1
      // 04: aload 1
      // 05: new java/lang/Object
      // 08: dup
      // 09: invokespecial net/rim/device/api/crypto/keystore/PrivateKeysKeyStoreIndex.<init> ()V
      // 0c: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.addIndex (Lnet/rim/device/api/crypto/keystore/KeyStoreIndex;)Z 2
      // 11: pop
      // 12: bipush 0
      // 13: anewarray 527
      // 16: astore 2
      // 17: bipush 0
      // 18: anewarray 529
      // 1b: astore 3
      // 1c: bipush 0
      // 1d: istore 4
      // 1f: aload 1
      // 20: ldc2_w -8376547269562148933
      // 23: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (J)Ljava/util/Enumeration; 3
      // 28: astore 5
      // 2a: aload 5
      // 2c: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 31: ifeq ab
      // 34: aload 5
      // 36: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 3b: checkcast java/lang/Object
      // 3e: astore 6
      // 40: aload 6
      // 42: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 47: ifeq 2a
      // 4a: aload 6
      // 4c: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 51: astore 7
      // 53: aload 7
      // 55: instanceof java/lang/Object
      // 58: ifeq 2a
      // 5b: aload 7
      // 5d: aload 0
      // 5e: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.checkForDH (Lnet/rim/device/api/crypto/PublicKey;Lnet/rim/device/api/crypto/PublicKey;)Z
      // 61: ifeq 2a
      // 64: aload 2
      // 65: arraylength
      // 66: istore 4
      // 68: aload 2
      // 69: iload 4
      // 6b: bipush 1
      // 6c: iadd
      // 6d: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 70: aload 3
      // 71: iload 4
      // 73: bipush 1
      // 74: iadd
      // 75: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 78: aload 2
      // 79: iload 4
      // 7b: new java/lang/Object
      // 7e: dup
      // 7f: aload 7
      // 81: checkcast java/lang/Object
      // 84: aload 6
      // 86: aconst_null
      // 87: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 8c: checkcast java/lang/Object
      // 8f: invokespecial net/rim/device/api/crypto/DHKeyPair.<init> (Lnet/rim/device/api/crypto/DHPublicKey;Lnet/rim/device/api/crypto/DHPrivateKey;)V
      // 92: aastore
      // 93: aload 3
      // 94: iload 4
      // 96: aload 6
      // 98: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 9d: aastore
      // 9e: goto 2a
      // a1: astore 8
      // a3: goto 2a
      // a6: astore 8
      // a8: goto 2a
      // ab: aload 2
      // ac: arraylength
      // ad: ifne b2
      // b0: aconst_null
      // b1: areturn
      // b2: aload 2
      // b3: arraylength
      // b4: bipush 1
      // b5: if_icmpne bc
      // b8: aload 2
      // b9: bipush 0
      // ba: aaload
      // bb: areturn
      // bc: getstatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // bf: bipush 13
      // c1: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // c4: aload 3
      // c5: bipush 1
      // c6: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.getChoice (Ljava/lang/String;[Ljava/lang/Object;I)I
      // c9: istore 6
      // cb: aload 2
      // cc: iload 6
      // ce: aaload
      // cf: areturn
      // try (40 -> 70): 71 null
      // try (40 -> 70): 73 null
   }

   public static final void writeClientCertificate(DataBuffer buffer, Certificate[] certChain) {
      TLSUtilities.writeIntegerThreeBytes(buffer, 0);
      int certChainBytes = 0;
      int certChainLength = certChain.length > 1 ? certChain.length - 1 : 1;

      for (int i = 0; i < certChainLength; i++) {
         byte[] encoding = certChain[i].getEncoding();
         int encodingLength = encoding.length;
         certChainBytes += encodingLength + 3;
         TLSUtilities.writeIntegerThreeBytes(buffer, encodingLength);
         buffer.write(encoding);
      }

      buffer.rewind();
      buffer.setPosition(4);
      TLSUtilities.writeIntegerThreeBytes(buffer, certChainBytes);
   }

   public static final void promptForDefaultCertificate(KeyStoreData data, Certificate certificate) {
      String message = MessageFormat.format(_rb.getString(19), new Object[]{certificate.getSubjectFriendlyName()});
      promptFor(message, data, certificate, null);
   }

   public static final void promptForMapping(KeyStoreData data, Certificate certificate, String hostname) {
      String message = MessageFormat.format(_rb.getString(21), new Object[]{certificate.getSubjectFriendlyName(), hostname});
      promptFor(message, data, certificate, hostname);
   }

   private static final void promptFor(String message, KeyStoreData data, Certificate certificate, String hostname) {
      Object[] choices = _rb.getStringArray(20);

      while (true) {
         int result = BackgroundDialog.getChoice(message, choices, 0);
         if (result != 2) {
            if (result == 0) {
               TLSDeviceOptionStore options = TLSDeviceOptionStore.getOptions();
               if (hostname != null) {
                  options.addDefaultCert(hostname, data);
                  return;
               } else {
                  options.setDefaultCert(data);
                  return;
               }
            } else {
               return;
            }
         }

         CertificateUtilities.displayCertificateDetails(certificate);
      }
   }
}
