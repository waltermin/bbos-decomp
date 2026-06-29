package net.rim.device.apps.internal.keystore.browser.certificate;

import net.rim.device.apps.internal.keystore.browser.FileSystemContentTypeListener;
import net.rim.device.apps.internal.keystore.browser.FileSystemContentTypeRegistry;

public class PKCS12FileSystemContentTypeListener implements FileSystemContentTypeListener {
   public PKCS12FileSystemContentTypeListener() {
      FileSystemContentTypeRegistry.getInstance().addFileSystemContentTypeListener(this);
   }

   @Override
   public boolean isTypeSupported(String fileName) {
      boolean result = false;
      if (fileName.endsWith(".pfx") || fileName.endsWith(".p12")) {
         result = true;
      }

      return result;
   }

   @Override
   public boolean parseCertificatesKeys(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 2
      // 002: new java/lang/StringBuffer
      // 005: dup
      // 006: ldc_w "file:///SDCard/blackberry/certificates/"
      // 009: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 00c: aload 1
      // 00d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 010: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 013: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 016: checkcast javax/microedition/io/file/FileConnection
      // 019: astore 2
      // 01a: aload 2
      // 01b: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 020: astore 3
      // 021: aload 3
      // 022: invokevirtual java/io/InputStream.read ()I
      // 025: i2b
      // 026: istore 4
      // 028: aload 3
      // 029: invokevirtual java/io/InputStream.available ()I
      // 02c: istore 5
      // 02e: iload 5
      // 030: bipush 1
      // 031: iadd
      // 032: newarray 8
      // 034: astore 6
      // 036: aload 6
      // 038: bipush 0
      // 039: iload 4
      // 03b: bastore
      // 03c: aload 3
      // 03d: aload 6
      // 03f: bipush 1
      // 040: iload 5
      // 042: invokevirtual java/io/InputStream.read ([BII)I
      // 045: pop
      // 046: new net/rim/device/api/crypto/PKCS12
      // 049: dup
      // 04a: aload 6
      // 04c: invokespecial net/rim/device/api/crypto/PKCS12.<init> ([B)V
      // 04f: astore 7
      // 051: aload 7
      // 053: invokestatic net/rim/device/api/crypto/PKCS12Utilities.getAllCertificateKeyPairs (Lnet/rim/device/api/crypto/PKCS12;)[Lnet/rim/device/api/crypto/CertificatePrivateKeyPair;
      // 056: astore 8
      // 058: aload 8
      // 05a: ifnull 063
      // 05d: aload 8
      // 05f: arraylength
      // 060: ifne 078
      // 063: bipush 1
      // 064: istore 9
      // 066: aload 2
      // 067: ifnull 075
      // 06a: aload 2
      // 06b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 070: goto 075
      // 073: astore 10
      // 075: iload 9
      // 077: ireturn
      // 078: aload 8
      // 07a: arraylength
      // 07b: istore 5
      // 07d: iload 5
      // 07f: anewarray 136
      // 082: astore 9
      // 084: iload 5
      // 086: anewarray 138
      // 089: astore 10
      // 08b: bipush 0
      // 08c: newarray 10
      // 08e: astore 11
      // 090: invokestatic net/rim/device/api/crypto/keystore/DeviceKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 093: astore 12
      // 095: bipush 0
      // 096: istore 13
      // 098: iload 13
      // 09a: iload 5
      // 09c: if_icmplt 0a2
      // 09f: goto 11e
      // 0a2: aload 9
      // 0a4: iload 13
      // 0a6: aload 8
      // 0a8: iload 13
      // 0aa: aaload
      // 0ab: invokevirtual net/rim/device/api/crypto/CertificatePrivateKeyPair.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate;
      // 0ae: aastore
      // 0af: aload 10
      // 0b1: iload 13
      // 0b3: aload 9
      // 0b5: iload 13
      // 0b7: aaload
      // 0b8: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.getSubjectFriendlyName (Lnet/rim/device/api/crypto/certificate/Certificate;)Ljava/lang/String;
      // 0bb: aastore
      // 0bc: aload 12
      // 0be: aload 9
      // 0c0: iload 13
      // 0c2: aaload
      // 0c3: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.isMember (Lnet/rim/device/api/crypto/certificate/Certificate;)Z 2
      // 0c8: ifeq 118
      // 0cb: aload 8
      // 0cd: iload 13
      // 0cf: aaload
      // 0d0: invokevirtual net/rim/device/api/crypto/CertificatePrivateKeyPair.getPrivateKey ()Lnet/rim/device/api/crypto/PrivateKey;
      // 0d3: ifnull 111
      // 0d6: aload 12
      // 0d8: ldc2_w -2038609988711824737
      // 0db: aload 9
      // 0dd: iload 13
      // 0df: aaload
      // 0e0: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 0e5: astore 14
      // 0e7: aload 14
      // 0e9: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 0ee: ifeq 118
      // 0f1: aload 14
      // 0f3: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0f8: checkcast net/rim/device/api/crypto/keystore/KeyStoreData
      // 0fb: astore 15
      // 0fd: aload 15
      // 0ff: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 104: ifeq 118
      // 107: aload 11
      // 109: iload 13
      // 10b: invokestatic net/rim/device/api/util/Arrays.add ([II)V
      // 10e: goto 118
      // 111: aload 11
      // 113: iload 13
      // 115: invokestatic net/rim/device/api/util/Arrays.add ([II)V
      // 118: iinc 13 1
      // 11b: goto 098
      // 11e: new net/rim/device/api/ui/component/RichTextField
      // 121: dup
      // 122: new java/lang/StringBuffer
      // 125: dup
      // 126: invokespecial java/lang/StringBuffer.<init> ()V
      // 129: sipush 6098
      // 12c: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // 12f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 132: aload 1
      // 133: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 136: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 139: invokespecial net/rim/device/api/ui/component/RichTextField.<init> (Ljava/lang/String;)V
      // 13c: aload 10
      // 13e: aload 9
      // 140: aload 11
      // 142: aload 12
      // 144: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.selectCertificates (Lnet/rim/device/api/ui/component/RichTextField;[Ljava/lang/String;[Lnet/rim/device/api/crypto/certificate/Certificate;[ILnet/rim/device/api/crypto/keystore/KeyStore;)[I
      // 147: astore 13
      // 149: aload 13
      // 14b: ifnull 1ab
      // 14e: bipush 0
      // 14f: anewarray 256
      // 152: astore 14
      // 154: bipush 0
      // 155: anewarray 258
      // 158: astore 15
      // 15a: bipush 0
      // 15b: istore 16
      // 15d: iload 16
      // 15f: aload 13
      // 161: arraylength
      // 162: if_icmpge 199
      // 165: aload 11
      // 167: aload 13
      // 169: iload 16
      // 16b: iaload
      // 16c: invokestatic net/rim/device/api/util/Arrays.binarySearch ([II)I
      // 16f: ifge 193
      // 172: aload 9
      // 174: aload 13
      // 176: iload 16
      // 178: iaload
      // 179: aaload
      // 17a: astore 17
      // 17c: aload 14
      // 17e: aload 17
      // 180: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 183: aload 15
      // 185: aload 8
      // 187: aload 13
      // 189: iload 16
      // 18b: iaload
      // 18c: aaload
      // 18d: invokevirtual net/rim/device/api/crypto/CertificatePrivateKeyPair.getPrivateKey ()Lnet/rim/device/api/crypto/PrivateKey;
      // 190: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 193: iinc 16 1
      // 196: goto 15d
      // 199: aload 14
      // 19b: arraylength
      // 19c: ifle 1ab
      // 19f: aload 14
      // 1a1: aload 15
      // 1a3: aconst_null
      // 1a4: aload 12
      // 1a6: aconst_null
      // 1a7: invokestatic net/rim/device/api/crypto/certificate/CertificateImporterFactory.importCertificates ([Lnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/PrivateKey;[Ljava/lang/String;Lnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Z
      // 1aa: pop
      // 1ab: aload 2
      // 1ac: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1b1: bipush 1
      // 1b2: istore 14
      // 1b4: aload 2
      // 1b5: ifnull 1c3
      // 1b8: aload 2
      // 1b9: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1be: goto 1c3
      // 1c1: astore 15
      // 1c3: iload 14
      // 1c5: ireturn
      // 1c6: astore 3
      // 1c7: aload 2
      // 1c8: ifnull 1fa
      // 1cb: aload 2
      // 1cc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1d1: bipush 0
      // 1d2: ireturn
      // 1d3: astore 3
      // 1d4: bipush 0
      // 1d5: ireturn
      // 1d6: astore 3
      // 1d7: aload 2
      // 1d8: ifnull 1fa
      // 1db: aload 2
      // 1dc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1e1: bipush 0
      // 1e2: ireturn
      // 1e3: astore 3
      // 1e4: bipush 0
      // 1e5: ireturn
      // 1e6: astore 18
      // 1e8: aload 2
      // 1e9: ifnull 1f7
      // 1ec: aload 2
      // 1ed: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1f2: goto 1f7
      // 1f5: astore 19
      // 1f7: aload 18
      // 1f9: athrow
      // 1fa: bipush 0
      // 1fb: ireturn
      // try (52 -> 56): 57 null
      // try (202 -> 206): 207 null
      // try (2 -> 52): 210 null
      // try (60 -> 202): 210 null
      // try (211 -> 215): 217 null
      // try (2 -> 52): 220 null
      // try (60 -> 202): 220 null
      // try (221 -> 225): 227 null
      // try (2 -> 52): 230 null
      // try (60 -> 202): 230 null
      // try (210 -> 211): 230 null
      // try (220 -> 221): 230 null
      // try (231 -> 235): 236 null
      // try (230 -> 231): 230 null
   }
}
