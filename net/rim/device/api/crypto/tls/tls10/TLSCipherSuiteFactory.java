package net.rim.device.api.crypto.tls.tls10;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.tls.KeyMaterial;
import net.rim.device.api.crypto.tls.TLSUtilities;
import net.rim.device.api.crypto.tls.ssl30.SSLCipherSuiteFactory;
import net.rim.device.api.crypto.tls.ssl30.SSLConnectionState;

final class TLSCipherSuiteFactory extends SSLCipherSuiteFactory {
   public TLSCipherSuiteFactory(TLSRecordProtocol recordProtocol) {
      super(recordProtocol);
   }

   @Override
   public final SSLConnectionState getConnectionState(int cipherSuite) {
      SSLConnectionState state = new SSLConnectionState();
      switch (cipherSuite) {
         case 0:
            state.setKeyExchangeAlgorithm((byte)0);
            break;
         case 1:
         case 2:
         case 4:
         case 5:
         case 9:
         case 10:
         case 47:
         case 53:
            state.setKeyExchangeAlgorithm((byte)1);
            break;
         case 3:
         case 8:
            state.setKeyExchangeAlgorithm((byte)2);
            break;
         case 12:
         case 13:
         case 48:
         case 54:
            state.setKeyExchangeAlgorithm((byte)3);
            break;
         case 15:
         case 16:
         case 49:
         case 55:
            state.setKeyExchangeAlgorithm((byte)4);
            break;
         case 17:
            state.setKeyExchangeAlgorithm((byte)7);
            break;
         case 18:
         case 19:
         case 50:
         case 56:
            state.setKeyExchangeAlgorithm((byte)6);
            break;
         case 20:
            state.setKeyExchangeAlgorithm((byte)9);
            break;
         case 21:
         case 22:
         case 51:
         case 57:
            state.setKeyExchangeAlgorithm((byte)8);
            break;
         case 23:
         case 25:
            state.setKeyExchangeAlgorithm((byte)11);
            break;
         case 24:
         case 26:
         case 27:
         case 52:
         case 58:
            state.setKeyExchangeAlgorithm((byte)5);
            break;
         case 72:
         case 73:
         case 74:
            state.setKeyExchangeAlgorithm((byte)12);
            break;
         case 86:
         case 87:
         case 88:
            state.setKeyExchangeAlgorithm((byte)16);
            break;
         case 89:
         case 90:
            state.setKeyExchangeAlgorithm((byte)17);
            break;
         default:
            TLSUtilities.sendAlertAndThrowException(super._recordProtocol.getAlertProtocol(), (byte)47);
      }

      switch (cipherSuite) {
         case 0:
         case 1:
         case 2:
            state.setBulkCipherAlgorithm(null);
            state.setIVSize(0);
            state.setCipherType((byte)0);
            state.setIsExportable(true);
            state.setKeySize(0);
            state.setKeyMaterialLength(0);
            break;
         case 3:
         case 23:
         case 90:
            state.setBulkCipherAlgorithm("ARC4");
            state.setIVSize(0);
            state.setCipherType((byte)1);
            state.setIsExportable(true);
            state.setKeySize(5);
            state.setKeyMaterialLength(16);
            break;
         case 4:
         case 5:
         case 24:
         case 72:
         case 86:
            state.setBulkCipherAlgorithm("ARC4");
            state.setIVSize(0);
            state.setCipherType((byte)1);
            state.setIsExportable(false);
            state.setKeySize(16);
            state.setKeyMaterialLength(16);
            break;
         case 8:
         case 17:
         case 20:
         case 25:
         case 89:
            state.setBulkCipherAlgorithm("DES");
            state.setIVSize(8);
            state.setCipherType((byte)2);
            state.setIsExportable(true);
            state.setKeySize(5);
            state.setKeyMaterialLength(8);
            break;
         case 9:
         case 12:
         case 15:
         case 18:
         case 21:
         case 26:
         case 73:
         case 87:
            state.setBulkCipherAlgorithm("DES");
            state.setIVSize(8);
            state.setCipherType((byte)2);
            state.setIsExportable(false);
            state.setKeySize(8);
            state.setKeyMaterialLength(8);
            break;
         case 10:
         case 13:
         case 16:
         case 19:
         case 22:
         case 27:
         case 74:
         case 88:
            state.setBulkCipherAlgorithm("TripleDES");
            state.setIVSize(8);
            state.setCipherType((byte)2);
            state.setIsExportable(false);
            state.setKeySize(24);
            state.setKeyMaterialLength(24);
            break;
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
            state.setBulkCipherAlgorithm("AES");
            state.setIVSize(16);
            state.setCipherType((byte)2);
            state.setIsExportable(false);
            state.setKeySize(16);
            state.setKeyMaterialLength(16);
            break;
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
            state.setBulkCipherAlgorithm("AES");
            state.setIVSize(16);
            state.setCipherType((byte)2);
            state.setIsExportable(false);
            state.setKeySize(32);
            state.setKeyMaterialLength(32);
            break;
         default:
            TLSUtilities.sendAlertAndThrowException(super._recordProtocol.getAlertProtocol(), (byte)47);
      }

      switch (cipherSuite) {
         case 0:
            state.setHashSize(0);
            state.setMacAlgorithm(null);
            return state;
         case 1:
         case 3:
         case 4:
         case 23:
         case 24:
            state.setHashSize(16);
            state.setMacAlgorithm("MD5");
            return state;
         case 2:
         case 5:
         case 8:
         case 9:
         case 10:
         case 12:
         case 13:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 25:
         case 26:
         case 27:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
         case 72:
         case 73:
         case 74:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
            state.setHashSize(20);
            state.setMacAlgorithm("SHA1");
            return state;
         default:
            TLSUtilities.sendAlertAndThrowException(super._recordProtocol.getAlertProtocol(), (byte)47);
            return state;
      }
   }

   @Override
   public final void updateReadConnectionState(SSLConnectionState param1, KeyMaterial param2, InputStream param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 004: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getRemoteVersion ()I
      // 007: aload 0
      // 008: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 00b: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getLocalVersion ()I
      // 00e: if_icmpeq 019
      // 011: aload 0
      // 012: aload 1
      // 013: aload 2
      // 014: aload 3
      // 015: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory.updateReadConnectionState (Lnet/rim/device/api/crypto/tls/ssl30/SSLConnectionState;Lnet/rim/device/api/crypto/tls/KeyMaterial;Ljava/io/InputStream;)V
      // 018: return
      // 019: aload 0
      // 01a: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 01d: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 020: ifeq 036
      // 023: aload 1
      // 024: aload 2
      // 025: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 028: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setCipherKey (Lnet/rim/device/api/crypto/SymmetricKey;)V
      // 02b: aload 1
      // 02c: aload 2
      // 02d: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 030: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setIV (Lnet/rim/device/api/crypto/InitializationVector;)V
      // 033: goto 046
      // 036: aload 1
      // 037: aload 2
      // 038: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 03b: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setCipherKey (Lnet/rim/device/api/crypto/SymmetricKey;)V
      // 03e: aload 1
      // 03f: aload 2
      // 040: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 043: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setIV (Lnet/rim/device/api/crypto/InitializationVector;)V
      // 046: aload 1
      // 047: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getBulkCipherAlgorithm ()Ljava/lang/String;
      // 04a: astore 4
      // 04c: aload 4
      // 04e: ifnonnull 060
      // 051: aload 1
      // 052: new java/lang/Object
      // 055: dup
      // 056: aload 3
      // 057: invokespecial net/rim/device/api/crypto/NullDecryptor.<init> (Ljava/io/InputStream;)V
      // 05a: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setDecryptor (Lnet/rim/device/api/crypto/DecryptorInputStream;)V
      // 05d: goto 112
      // 060: aload 1
      // 061: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getCipherType ()B
      // 064: bipush 2
      // 066: if_icmpne 0dc
      // 069: aconst_null
      // 06a: astore 5
      // 06c: aload 0
      // 06d: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 070: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 073: ifeq 09b
      // 076: aload 2
      // 077: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 07a: new java/lang/Object
      // 07d: dup
      // 07e: invokespecial java/lang/StringBuffer.<init> ()V
      // 081: aload 4
      // 083: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 086: ldc_w "/CBC"
      // 089: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 08c: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 08f: aload 2
      // 090: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 093: invokestatic net/rim/device/api/crypto/DecryptorFactory.getBlockDecryptorEngine (Lnet/rim/device/api/crypto/Key;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/BlockDecryptorEngine;
      // 096: astore 5
      // 098: goto 0bd
      // 09b: aload 2
      // 09c: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 09f: new java/lang/Object
      // 0a2: dup
      // 0a3: invokespecial java/lang/StringBuffer.<init> ()V
      // 0a6: aload 4
      // 0a8: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0ab: ldc_w "/CBC"
      // 0ae: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0b1: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0b4: aload 2
      // 0b5: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 0b8: invokestatic net/rim/device/api/crypto/DecryptorFactory.getBlockDecryptorEngine (Lnet/rim/device/api/crypto/Key;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/BlockDecryptorEngine;
      // 0bb: astore 5
      // 0bd: aload 1
      // 0be: aload 5
      // 0c0: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setDecryptorEngine (Lnet/rim/device/api/crypto/BlockDecryptorEngine;)V
      // 0c3: aload 1
      // 0c4: new java/lang/Object
      // 0c7: dup
      // 0c8: new java/lang/Object
      // 0cb: dup
      // 0cc: aload 5
      // 0ce: bipush 1
      // 0cf: invokespecial net/rim/device/api/crypto/tls/TLSBlockUnformatterEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;Z)V
      // 0d2: aload 3
      // 0d3: invokespecial net/rim/device/api/crypto/BlockDecryptor.<init> (Lnet/rim/device/api/crypto/BlockUnformatterEngine;Ljava/io/InputStream;)V
      // 0d6: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setDecryptor (Lnet/rim/device/api/crypto/DecryptorInputStream;)V
      // 0d9: goto 112
      // 0dc: aload 1
      // 0dd: aconst_null
      // 0de: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setDecryptorEngine (Lnet/rim/device/api/crypto/BlockDecryptorEngine;)V
      // 0e1: aload 0
      // 0e2: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0e5: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 0e8: ifeq 100
      // 0eb: aload 1
      // 0ec: aload 2
      // 0ed: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 0f0: aload 3
      // 0f1: aload 4
      // 0f3: aload 2
      // 0f4: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 0f7: invokestatic net/rim/device/api/crypto/DecryptorFactory.getDecryptorInputStream (Lnet/rim/device/api/crypto/Key;Ljava/io/InputStream;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/DecryptorInputStream;
      // 0fa: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setDecryptor (Lnet/rim/device/api/crypto/DecryptorInputStream;)V
      // 0fd: goto 112
      // 100: aload 1
      // 101: aload 2
      // 102: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 105: aload 3
      // 106: aload 4
      // 108: aload 2
      // 109: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 10c: invokestatic net/rim/device/api/crypto/DecryptorFactory.getDecryptorInputStream (Lnet/rim/device/api/crypto/Key;Ljava/io/InputStream;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/DecryptorInputStream;
      // 10f: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setDecryptor (Lnet/rim/device/api/crypto/DecryptorInputStream;)V
      // 112: aload 1
      // 113: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getMacAlgorithm ()Ljava/lang/String;
      // 116: astore 5
      // 118: aload 5
      // 11a: ifnonnull 12b
      // 11d: aload 1
      // 11e: new java/lang/Object
      // 121: dup
      // 122: invokespecial net/rim/device/api/crypto/NullMAC.<init> ()V
      // 125: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setMAC (Lnet/rim/device/api/crypto/MAC;)V
      // 128: goto 186
      // 12b: aload 0
      // 12c: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 12f: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 132: ifeq 14d
      // 135: new java/lang/Object
      // 138: dup
      // 139: aload 2
      // 13a: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerMAC ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 13d: checkcast java/lang/Object
      // 140: aload 5
      // 142: invokestatic net/rim/device/api/crypto/DigestFactory.getInstance (Ljava/lang/String;)Lnet/rim/device/api/crypto/Digest;
      // 145: invokespecial net/rim/device/api/crypto/HMAC.<init> (Lnet/rim/device/api/crypto/HMACKey;Lnet/rim/device/api/crypto/Digest;)V
      // 148: astore 6
      // 14a: goto 162
      // 14d: new java/lang/Object
      // 150: dup
      // 151: aload 2
      // 152: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientMAC ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 155: checkcast java/lang/Object
      // 158: aload 5
      // 15a: invokestatic net/rim/device/api/crypto/DigestFactory.getInstance (Ljava/lang/String;)Lnet/rim/device/api/crypto/Digest;
      // 15d: invokespecial net/rim/device/api/crypto/HMAC.<init> (Lnet/rim/device/api/crypto/HMACKey;Lnet/rim/device/api/crypto/Digest;)V
      // 160: astore 6
      // 162: aload 1
      // 163: aload 6
      // 165: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setMAC (Lnet/rim/device/api/crypto/MAC;)V
      // 168: return
      // 169: astore 4
      // 16b: aload 0
      // 16c: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 16f: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 172: bipush 51
      // 174: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 177: return
      // 178: astore 4
      // 17a: aload 0
      // 17b: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 17e: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 181: bipush 51
      // 183: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 186: return
      // try (13 -> 163): 164 null
      // try (13 -> 163): 171 null
   }

   @Override
   public final void updateWriteConnectionState(SSLConnectionState param1, KeyMaterial param2, OutputStream param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 004: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getRemoteVersion ()I
      // 007: aload 0
      // 008: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 00b: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getLocalVersion ()I
      // 00e: if_icmpeq 019
      // 011: aload 0
      // 012: aload 1
      // 013: aload 2
      // 014: aload 3
      // 015: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory.updateWriteConnectionState (Lnet/rim/device/api/crypto/tls/ssl30/SSLConnectionState;Lnet/rim/device/api/crypto/tls/KeyMaterial;Ljava/io/OutputStream;)V
      // 018: return
      // 019: aload 0
      // 01a: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 01d: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 020: ifeq 036
      // 023: aload 1
      // 024: aload 2
      // 025: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 028: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setCipherKey (Lnet/rim/device/api/crypto/SymmetricKey;)V
      // 02b: aload 1
      // 02c: aload 2
      // 02d: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 030: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setIV (Lnet/rim/device/api/crypto/InitializationVector;)V
      // 033: goto 046
      // 036: aload 1
      // 037: aload 2
      // 038: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 03b: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setCipherKey (Lnet/rim/device/api/crypto/SymmetricKey;)V
      // 03e: aload 1
      // 03f: aload 2
      // 040: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 043: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setIV (Lnet/rim/device/api/crypto/InitializationVector;)V
      // 046: aconst_null
      // 047: astore 4
      // 049: aload 1
      // 04a: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getBulkCipherAlgorithm ()Ljava/lang/String;
      // 04d: astore 5
      // 04f: aload 5
      // 051: ifnonnull 063
      // 054: aload 1
      // 055: new java/lang/Object
      // 058: dup
      // 059: aload 3
      // 05a: invokespecial net/rim/device/api/crypto/NullEncryptor.<init> (Ljava/io/OutputStream;)V
      // 05d: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setEncryptor (Lnet/rim/device/api/crypto/EncryptorOutputStream;)V
      // 060: goto 114
      // 063: aload 1
      // 064: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getCipherType ()B
      // 067: bipush 2
      // 069: if_icmpne 0de
      // 06c: aconst_null
      // 06d: astore 6
      // 06f: aload 0
      // 070: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 073: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 076: ifeq 09e
      // 079: aload 2
      // 07a: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 07d: new java/lang/Object
      // 080: dup
      // 081: invokespecial java/lang/StringBuffer.<init> ()V
      // 084: aload 5
      // 086: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 089: ldc_w "/CBC"
      // 08c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 08f: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 092: aload 2
      // 093: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 096: invokestatic net/rim/device/api/crypto/EncryptorFactory.getBlockEncryptorEngine (Lnet/rim/device/api/crypto/Key;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/BlockEncryptorEngine;
      // 099: astore 6
      // 09b: goto 0c0
      // 09e: aload 2
      // 09f: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 0a2: new java/lang/Object
      // 0a5: dup
      // 0a6: invokespecial java/lang/StringBuffer.<init> ()V
      // 0a9: aload 5
      // 0ab: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0ae: ldc_w "/CBC"
      // 0b1: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0b4: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0b7: aload 2
      // 0b8: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 0bb: invokestatic net/rim/device/api/crypto/EncryptorFactory.getBlockEncryptorEngine (Lnet/rim/device/api/crypto/Key;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/BlockEncryptorEngine;
      // 0be: astore 6
      // 0c0: aload 1
      // 0c1: aload 6
      // 0c3: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setEncryptorEngine (Lnet/rim/device/api/crypto/BlockEncryptorEngine;)V
      // 0c6: aload 1
      // 0c7: new java/lang/Object
      // 0ca: dup
      // 0cb: new java/lang/Object
      // 0ce: dup
      // 0cf: aload 6
      // 0d1: invokespecial net/rim/device/api/crypto/tls/TLSBlockFormatterEngine.<init> (Lnet/rim/device/api/crypto/BlockEncryptorEngine;)V
      // 0d4: aload 3
      // 0d5: invokespecial net/rim/device/api/crypto/BlockEncryptor.<init> (Lnet/rim/device/api/crypto/BlockFormatterEngine;Ljava/io/OutputStream;)V
      // 0d8: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setEncryptor (Lnet/rim/device/api/crypto/EncryptorOutputStream;)V
      // 0db: goto 114
      // 0de: aload 1
      // 0df: aconst_null
      // 0e0: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setEncryptorEngine (Lnet/rim/device/api/crypto/BlockEncryptorEngine;)V
      // 0e3: aload 0
      // 0e4: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0e7: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 0ea: ifeq 102
      // 0ed: aload 1
      // 0ee: aload 2
      // 0ef: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 0f2: aload 3
      // 0f3: aload 5
      // 0f5: aload 2
      // 0f6: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 0f9: invokestatic net/rim/device/api/crypto/EncryptorFactory.getEncryptorOutputStream (Lnet/rim/device/api/crypto/Key;Ljava/io/OutputStream;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/EncryptorOutputStream;
      // 0fc: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setEncryptor (Lnet/rim/device/api/crypto/EncryptorOutputStream;)V
      // 0ff: goto 114
      // 102: aload 1
      // 103: aload 2
      // 104: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 107: aload 3
      // 108: aload 5
      // 10a: aload 2
      // 10b: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 10e: invokestatic net/rim/device/api/crypto/EncryptorFactory.getEncryptorOutputStream (Lnet/rim/device/api/crypto/Key;Ljava/io/OutputStream;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/EncryptorOutputStream;
      // 111: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setEncryptor (Lnet/rim/device/api/crypto/EncryptorOutputStream;)V
      // 114: aload 1
      // 115: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getMacAlgorithm ()Ljava/lang/String;
      // 118: astore 6
      // 11a: aload 6
      // 11c: ifnonnull 12d
      // 11f: aload 1
      // 120: new java/lang/Object
      // 123: dup
      // 124: invokespecial net/rim/device/api/crypto/NullMAC.<init> ()V
      // 127: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setMAC (Lnet/rim/device/api/crypto/MAC;)V
      // 12a: goto 188
      // 12d: aload 0
      // 12e: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 131: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 134: ifeq 14f
      // 137: new java/lang/Object
      // 13a: dup
      // 13b: aload 2
      // 13c: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientMAC ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 13f: checkcast java/lang/Object
      // 142: aload 6
      // 144: invokestatic net/rim/device/api/crypto/DigestFactory.getInstance (Ljava/lang/String;)Lnet/rim/device/api/crypto/Digest;
      // 147: invokespecial net/rim/device/api/crypto/HMAC.<init> (Lnet/rim/device/api/crypto/HMACKey;Lnet/rim/device/api/crypto/Digest;)V
      // 14a: astore 7
      // 14c: goto 164
      // 14f: new java/lang/Object
      // 152: dup
      // 153: aload 2
      // 154: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerMAC ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 157: checkcast java/lang/Object
      // 15a: aload 6
      // 15c: invokestatic net/rim/device/api/crypto/DigestFactory.getInstance (Ljava/lang/String;)Lnet/rim/device/api/crypto/Digest;
      // 15f: invokespecial net/rim/device/api/crypto/HMAC.<init> (Lnet/rim/device/api/crypto/HMACKey;Lnet/rim/device/api/crypto/Digest;)V
      // 162: astore 7
      // 164: aload 1
      // 165: aload 7
      // 167: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setMAC (Lnet/rim/device/api/crypto/MAC;)V
      // 16a: return
      // 16b: astore 4
      // 16d: aload 0
      // 16e: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 171: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 174: bipush 51
      // 176: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 179: return
      // 17a: astore 4
      // 17c: aload 0
      // 17d: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 180: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 183: bipush 51
      // 185: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 188: return
      // try (13 -> 164): 165 null
      // try (13 -> 164): 172 null
   }
}
