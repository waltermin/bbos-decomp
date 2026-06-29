package net.rim.device.api.crypto.tls.ssl30;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.NullMAC;
import net.rim.device.api.crypto.tls.KeyMaterial;

public class SSLCipherSuiteFactory implements SSLRecordProtocolConstants {
   protected SSLRecordProtocol _recordProtocol;

   public SSLCipherSuiteFactory(SSLRecordProtocol recordProtocol) {
      this._recordProtocol = recordProtocol;
   }

   public SSLConnectionState getConnectionState(int cipherSuite) {
      SSLConnectionState state = new SSLConnectionState();
      switch (cipherSuite) {
         case -1:
         case 6:
         case 7:
         case 11:
         case 14:
            SSLAlertProtocol alertProtocol = (SSLAlertProtocol)this._recordProtocol.getAlertProtocol();
            alertProtocol.sendAlertMessage((byte)3, (byte)47);
            throw new Object((byte)3, (byte)47);
         case 0:
         default:
            state.setKeyExchangeAlgorithm((byte)0);
            break;
         case 1:
         case 2:
         case 4:
         case 5:
         case 9:
         case 10:
            state.setKeyExchangeAlgorithm((byte)1);
            break;
         case 3:
         case 8:
            state.setKeyExchangeAlgorithm((byte)2);
            break;
         case 12:
         case 13:
            state.setKeyExchangeAlgorithm((byte)3);
            break;
         case 15:
         case 16:
            state.setKeyExchangeAlgorithm((byte)4);
            break;
         case 17:
            state.setKeyExchangeAlgorithm((byte)7);
            break;
         case 18:
         case 19:
            state.setKeyExchangeAlgorithm((byte)6);
            break;
         case 20:
            state.setKeyExchangeAlgorithm((byte)9);
            break;
         case 21:
         case 22:
            state.setKeyExchangeAlgorithm((byte)8);
            break;
         case 23:
         case 25:
            state.setKeyExchangeAlgorithm((byte)11);
            break;
         case 24:
         case 26:
         case 27:
            state.setKeyExchangeAlgorithm((byte)5);
      }

      switch (cipherSuite) {
         case -1:
         case 6:
         case 7:
         case 11:
         case 14:
            SSLAlertProtocol alertProtocol = (SSLAlertProtocol)this._recordProtocol.getAlertProtocol();
            alertProtocol.sendAlertMessage((byte)3, (byte)47);
            throw new Object((byte)3, (byte)47);
         case 0:
         case 1:
         case 2:
         default:
            state.setBulkCipherAlgorithm(null);
            state.setIVSize(0);
            state.setCipherType((byte)0);
            state.setIsExportable(true);
            state.setKeySize(0);
            state.setKeyMaterialLength(0);
            break;
         case 3:
         case 23:
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
            state.setBulkCipherAlgorithm("TripleDES");
            state.setIVSize(8);
            state.setCipherType((byte)2);
            state.setIsExportable(false);
            state.setKeySize(24);
            state.setKeyMaterialLength(24);
      }

      switch (cipherSuite) {
         case -1:
         case 6:
         case 7:
         case 11:
         case 14:
            SSLAlertProtocol alertProtocol = (SSLAlertProtocol)this._recordProtocol.getAlertProtocol();
            alertProtocol.sendAlertMessage((byte)3, (byte)47);
            throw new Object((byte)3, (byte)47);
         case 0:
         default:
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
            state.setHashSize(20);
            state.setMacAlgorithm("SHA1");
            return state;
      }
   }

   public void updateReadConnectionState(SSLConnectionState param1, KeyMaterial param2, InputStream param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 1
      // 002: aload 2
      // 003: bipush 1
      // 004: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory.updateHashInConnection (Lnet/rim/device/api/crypto/tls/ssl30/SSLConnectionState;Lnet/rim/device/api/crypto/tls/KeyMaterial;Z)V
      // 007: aload 1
      // 008: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getBulkCipherAlgorithm ()Ljava/lang/String;
      // 00b: astore 4
      // 00d: aload 4
      // 00f: ifnonnull 021
      // 012: aload 1
      // 013: new java/lang/Object
      // 016: dup
      // 017: aload 3
      // 018: invokespecial net/rim/device/api/crypto/NullDecryptor.<init> (Ljava/io/InputStream;)V
      // 01b: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setDecryptor (Lnet/rim/device/api/crypto/DecryptorInputStream;)V
      // 01e: goto 109
      // 021: aload 1
      // 022: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getCipherType ()B
      // 025: bipush 2
      // 027: if_icmpne 09a
      // 02a: aload 0
      // 02b: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 02e: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 031: ifeq 059
      // 034: aload 2
      // 035: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 038: new java/lang/Object
      // 03b: dup
      // 03c: invokespecial java/lang/StringBuffer.<init> ()V
      // 03f: aload 4
      // 041: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 044: ldc_w "/CBC"
      // 047: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 04a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 04d: aload 2
      // 04e: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 051: invokestatic net/rim/device/api/crypto/DecryptorFactory.getBlockDecryptorEngine (Lnet/rim/device/api/crypto/Key;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/BlockDecryptorEngine;
      // 054: astore 5
      // 056: goto 07b
      // 059: aload 2
      // 05a: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 05d: new java/lang/Object
      // 060: dup
      // 061: invokespecial java/lang/StringBuffer.<init> ()V
      // 064: aload 4
      // 066: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 069: ldc_w "/CBC"
      // 06c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 06f: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 072: aload 2
      // 073: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 076: invokestatic net/rim/device/api/crypto/DecryptorFactory.getBlockDecryptorEngine (Lnet/rim/device/api/crypto/Key;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/BlockDecryptorEngine;
      // 079: astore 5
      // 07b: aload 1
      // 07c: aload 5
      // 07e: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setDecryptorEngine (Lnet/rim/device/api/crypto/BlockDecryptorEngine;)V
      // 081: aload 1
      // 082: new java/lang/Object
      // 085: dup
      // 086: new java/lang/Object
      // 089: dup
      // 08a: aload 5
      // 08c: bipush 0
      // 08d: invokespecial net/rim/device/api/crypto/tls/TLSBlockUnformatterEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;Z)V
      // 090: aload 3
      // 091: invokespecial net/rim/device/api/crypto/BlockDecryptor.<init> (Lnet/rim/device/api/crypto/BlockUnformatterEngine;Ljava/io/InputStream;)V
      // 094: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setDecryptor (Lnet/rim/device/api/crypto/DecryptorInputStream;)V
      // 097: goto 109
      // 09a: aload 1
      // 09b: aconst_null
      // 09c: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setDecryptorEngine (Lnet/rim/device/api/crypto/BlockDecryptorEngine;)V
      // 09f: aload 0
      // 0a0: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0a3: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 0a6: ifeq 0be
      // 0a9: aload 1
      // 0aa: aload 2
      // 0ab: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 0ae: aload 3
      // 0af: aload 4
      // 0b1: aload 2
      // 0b2: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 0b5: invokestatic net/rim/device/api/crypto/DecryptorFactory.getDecryptorInputStream (Lnet/rim/device/api/crypto/Key;Ljava/io/InputStream;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/DecryptorInputStream;
      // 0b8: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setDecryptor (Lnet/rim/device/api/crypto/DecryptorInputStream;)V
      // 0bb: goto 109
      // 0be: aload 1
      // 0bf: aload 2
      // 0c0: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 0c3: aload 3
      // 0c4: aload 4
      // 0c6: aload 2
      // 0c7: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 0ca: invokestatic net/rim/device/api/crypto/DecryptorFactory.getDecryptorInputStream (Lnet/rim/device/api/crypto/Key;Ljava/io/InputStream;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/DecryptorInputStream;
      // 0cd: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setDecryptor (Lnet/rim/device/api/crypto/DecryptorInputStream;)V
      // 0d0: return
      // 0d1: astore 4
      // 0d3: aload 0
      // 0d4: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0d7: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 0da: bipush 3
      // 0dc: bipush 51
      // 0de: invokeinterface net/rim/device/api/crypto/tls/AlertProtocolMethods.sendAlertMessage (BB)V 3
      // 0e3: new java/lang/Object
      // 0e6: dup
      // 0e7: aload 4
      // 0e9: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 0ec: athrow
      // 0ed: astore 4
      // 0ef: aload 0
      // 0f0: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0f3: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 0f6: bipush 3
      // 0f8: bipush 51
      // 0fa: invokeinterface net/rim/device/api/crypto/tls/AlertProtocolMethods.sendAlertMessage (BB)V 3
      // 0ff: new java/lang/Object
      // 102: dup
      // 103: aload 4
      // 105: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 108: athrow
      // 109: return
      // try (0 -> 95): 96 null
      // try (0 -> 95): 108 null
   }

   private void updateHashInConnection(SSLConnectionState state, KeyMaterial keyMaterial, boolean read) {
      boolean clientMode = this._recordProtocol.getClientMode();
      if ((!read || !clientMode) && (read || clientMode)) {
         state.setCipherKey(keyMaterial.getClientCipher());
         state.setIV(keyMaterial.getClientIV());
      } else {
         state.setCipherKey(keyMaterial.getServerCipher());
         state.setIV(keyMaterial.getServerIV());
      }

      String macAlgorithm = state.getMacAlgorithm();
      if (macAlgorithm == null) {
         NullMAC mac = (NullMAC)(new Object());
         state.setMAC(mac);
      } else if ((!read || !clientMode) && (read || clientMode)) {
         state.setMAC(new SSL_HMAC((HMACKey)keyMaterial.getClientMAC(), macAlgorithm));
      } else {
         state.setMAC(new SSL_HMAC((HMACKey)keyMaterial.getServerMAC(), macAlgorithm));
      }
   }

   public void updateWriteConnectionState(SSLConnectionState param1, KeyMaterial param2, OutputStream param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 1
      // 002: aload 2
      // 003: bipush 0
      // 004: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory.updateHashInConnection (Lnet/rim/device/api/crypto/tls/ssl30/SSLConnectionState;Lnet/rim/device/api/crypto/tls/KeyMaterial;Z)V
      // 007: aconst_null
      // 008: astore 4
      // 00a: aload 1
      // 00b: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getBulkCipherAlgorithm ()Ljava/lang/String;
      // 00e: astore 5
      // 010: aload 5
      // 012: ifnonnull 024
      // 015: aload 1
      // 016: new java/lang/Object
      // 019: dup
      // 01a: aload 3
      // 01b: invokespecial net/rim/device/api/crypto/NullEncryptor.<init> (Ljava/io/OutputStream;)V
      // 01e: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setEncryptor (Lnet/rim/device/api/crypto/EncryptorOutputStream;)V
      // 021: goto 10b
      // 024: aload 1
      // 025: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getCipherType ()B
      // 028: bipush 2
      // 02a: if_icmpne 09c
      // 02d: aload 0
      // 02e: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 031: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 034: ifeq 05c
      // 037: aload 2
      // 038: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 03b: new java/lang/Object
      // 03e: dup
      // 03f: invokespecial java/lang/StringBuffer.<init> ()V
      // 042: aload 5
      // 044: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 047: ldc_w "/CBC"
      // 04a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 04d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 050: aload 2
      // 051: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 054: invokestatic net/rim/device/api/crypto/EncryptorFactory.getBlockEncryptorEngine (Lnet/rim/device/api/crypto/Key;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/BlockEncryptorEngine;
      // 057: astore 6
      // 059: goto 07e
      // 05c: aload 2
      // 05d: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 060: new java/lang/Object
      // 063: dup
      // 064: invokespecial java/lang/StringBuffer.<init> ()V
      // 067: aload 5
      // 069: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 06c: ldc_w "/CBC"
      // 06f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 072: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 075: aload 2
      // 076: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 079: invokestatic net/rim/device/api/crypto/EncryptorFactory.getBlockEncryptorEngine (Lnet/rim/device/api/crypto/Key;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/BlockEncryptorEngine;
      // 07c: astore 6
      // 07e: aload 1
      // 07f: aload 6
      // 081: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setEncryptorEngine (Lnet/rim/device/api/crypto/BlockEncryptorEngine;)V
      // 084: aload 1
      // 085: new java/lang/Object
      // 088: dup
      // 089: new java/lang/Object
      // 08c: dup
      // 08d: aload 6
      // 08f: invokespecial net/rim/device/api/crypto/tls/TLSBlockFormatterEngine.<init> (Lnet/rim/device/api/crypto/BlockEncryptorEngine;)V
      // 092: aload 3
      // 093: invokespecial net/rim/device/api/crypto/BlockEncryptor.<init> (Lnet/rim/device/api/crypto/BlockFormatterEngine;Ljava/io/OutputStream;)V
      // 096: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setEncryptor (Lnet/rim/device/api/crypto/EncryptorOutputStream;)V
      // 099: goto 10b
      // 09c: aload 1
      // 09d: aconst_null
      // 09e: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLConnectionState.setEncryptorEngine (Lnet/rim/device/api/crypto/BlockEncryptorEngine;)V
      // 0a1: aload 0
      // 0a2: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0a5: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 0a8: ifeq 0c0
      // 0ab: aload 1
      // 0ac: aload 2
      // 0ad: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 0b0: aload 3
      // 0b1: aload 5
      // 0b3: aload 2
      // 0b4: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getClientIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 0b7: invokestatic net/rim/device/api/crypto/EncryptorFactory.getEncryptorOutputStream (Lnet/rim/device/api/crypto/Key;Ljava/io/OutputStream;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/EncryptorOutputStream;
      // 0ba: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setEncryptor (Lnet/rim/device/api/crypto/EncryptorOutputStream;)V
      // 0bd: goto 10b
      // 0c0: aload 1
      // 0c1: aload 2
      // 0c2: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerCipher ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 0c5: aload 3
      // 0c6: aload 5
      // 0c8: aload 2
      // 0c9: invokevirtual net/rim/device/api/crypto/tls/KeyMaterial.getServerIV ()Lnet/rim/device/api/crypto/InitializationVector;
      // 0cc: invokestatic net/rim/device/api/crypto/EncryptorFactory.getEncryptorOutputStream (Lnet/rim/device/api/crypto/Key;Ljava/io/OutputStream;Ljava/lang/String;Lnet/rim/device/api/crypto/InitializationVector;)Lnet/rim/device/api/crypto/EncryptorOutputStream;
      // 0cf: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setEncryptor (Lnet/rim/device/api/crypto/EncryptorOutputStream;)V
      // 0d2: return
      // 0d3: astore 4
      // 0d5: aload 0
      // 0d6: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0d9: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 0dc: bipush 3
      // 0de: bipush 51
      // 0e0: invokeinterface net/rim/device/api/crypto/tls/AlertProtocolMethods.sendAlertMessage (BB)V 3
      // 0e5: new java/lang/Object
      // 0e8: dup
      // 0e9: aload 4
      // 0eb: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 0ee: athrow
      // 0ef: astore 4
      // 0f1: aload 0
      // 0f2: getfield net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0f5: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 0f8: bipush 3
      // 0fa: bipush 51
      // 0fc: invokeinterface net/rim/device/api/crypto/tls/AlertProtocolMethods.sendAlertMessage (BB)V 3
      // 101: new java/lang/Object
      // 104: dup
      // 105: aload 4
      // 107: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 10a: athrow
      // 10b: return
      // try (0 -> 96): 97 null
      // try (0 -> 96): 109 null
   }

   public String getCompressionAlgorithm(byte algorithm) {
      return null;
   }
}
