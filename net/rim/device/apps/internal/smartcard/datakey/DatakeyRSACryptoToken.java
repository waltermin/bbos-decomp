package net.rim.device.apps.internal.smartcard.datakey;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoTokenPrivateKeyData;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSACryptoToken;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.smartcard.SmartCardSession;
import net.rim.device.api.util.Persistable;

final class DatakeyRSACryptoToken extends RSACryptoToken implements Persistable {
   private static ResourceBundle _rb = ResourceBundle.getBundle(-2744454300651253428L, "net.rim.device.apps.internal.resource.crypto.SmartCard");

   @Override
   public final boolean providesUserAuthentication() {
      return true;
   }

   @Override
   public final boolean isSupported(CryptoSystem cryptoSystem, int operation) {
      return operation == 4;
   }

   @Override
   public final boolean isSupportedDecryptRSA(RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData) {
      return privateKeyData instanceof DatakeyCryptoTokenData;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void decryptRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) {
      try {
         this.signDecryptHelper(cryptoSystem, privateKeyData, input, inputOffset, output, outputOffset, _rb.getString(17), 2, null);
      } catch (Throwable var9) {
         throw new Object(e.toString());
      }
   }

   @Override
   public final void signRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) {
      this.signDecryptHelper(cryptoSystem, privateKeyData, input, inputOffset, output, outputOffset, _rb.getString(16), 1, null);
   }

   @Override
   public final void signRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset, Object context
   ) {
      this.signDecryptHelper(cryptoSystem, privateKeyData, input, inputOffset, output, outputOffset, _rb.getString(16), 1, (SmartCardSession)context);
   }

   private final void signDecryptHelper(
      RSACryptoSystem param1,
      CryptoTokenPrivateKeyData param2,
      byte[] param3,
      int param4,
      byte[] param5,
      int param6,
      String param7,
      int param8,
      SmartCardSession param9
   ) throws CryptoTokenException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 10
      // 003: aconst_null
      // 004: astore 11
      // 006: aload 9
      // 008: ifnonnull 00f
      // 00b: bipush 1
      // 00c: goto 010
      // 00f: bipush 0
      // 010: istore 12
      // 012: aload 2
      // 013: dup
      // 014: instanceof net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData
      // 017: ifne 01e
      // 01a: pop
      // 01b: goto 0a0
      // 01e: checkcast net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData
      // 021: invokevirtual net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData.getSmartCardID ()Lnet/rim/device/api/smartcard/SmartCardID;
      // 024: astore 13
      // 026: aload 9
      // 028: ifnonnull 035
      // 02b: aload 13
      // 02d: invokestatic net/rim/device/api/smartcard/SmartCardFactory.getSmartCardSession (Lnet/rim/device/api/smartcard/SmartCardID;)Lnet/rim/device/api/smartcard/SmartCardSession;
      // 030: astore 9
      // 032: goto 04d
      // 035: aload 13
      // 037: aload 9
      // 039: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getSmartCardID ()Lnet/rim/device/api/smartcard/SmartCardID;
      // 03c: invokevirtual net/rim/device/api/smartcard/SmartCardID.equals (Ljava/lang/Object;)Z
      // 03f: ifne 04d
      // 042: new java/lang/Object
      // 045: dup
      // 046: ldc_w "Wrong card inserted"
      // 049: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 04c: athrow
      // 04d: aload 9
      // 04f: dup
      // 050: instanceof net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoSmartCardSession
      // 053: ifne 05a
      // 056: pop
      // 057: goto 0a0
      // 05a: checkcast net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoSmartCardSession
      // 05d: astore 14
      // 05f: aload 14
      // 061: invokevirtual net/rim/device/api/smartcard/SmartCardSession.isLoggedIn ()Z
      // 064: ifne 070
      // 067: aload 14
      // 069: aload 7
      // 06b: iload 8
      // 06d: invokevirtual net/rim/device/api/smartcard/SmartCardSession.loginPrompt (Ljava/lang/String;I)V
      // 070: aload 14
      // 072: aload 1
      // 073: aload 2
      // 074: checkcast net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData
      // 077: aload 3
      // 078: iload 4
      // 07a: aload 5
      // 07c: iload 6
      // 07e: iload 8
      // 080: invokevirtual net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoSmartCardSession.signDecryptRSA (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData;[BI[BII)V
      // 083: aload 9
      // 085: ifnull 092
      // 088: iload 12
      // 08a: ifeq 092
      // 08d: aload 9
      // 08f: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 092: aload 10
      // 094: ifnull 09f
      // 097: aload 10
      // 099: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 09c: aload 11
      // 09e: athrow
      // 09f: return
      // 0a0: new java/lang/Object
      // 0a3: dup
      // 0a4: invokespecial java/lang/RuntimeException.<init> ()V
      // 0a7: athrow
      // 0a8: astore 13
      // 0aa: ldc2_w 7215549882295292649
      // 0ad: ldc_w "net.rim.device.internal.resource.SmartCard"
      // 0b0: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 0b3: astore 14
      // 0b5: aload 14
      // 0b7: bipush 14
      // 0b9: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0bc: astore 10
      // 0be: new java/lang/Object
      // 0c1: dup
      // 0c2: aload 13
      // 0c4: invokevirtual net/rim/device/api/smartcard/SmartCardLockedException.toString ()Ljava/lang/String;
      // 0c7: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 0ca: astore 11
      // 0cc: aload 9
      // 0ce: ifnull 0db
      // 0d1: iload 12
      // 0d3: ifeq 0db
      // 0d6: aload 9
      // 0d8: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 0db: aload 10
      // 0dd: ifnonnull 0e3
      // 0e0: goto 179
      // 0e3: aload 10
      // 0e5: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 0e8: aload 11
      // 0ea: athrow
      // 0eb: astore 13
      // 0ed: new java/lang/Object
      // 0f0: dup
      // 0f1: aload 13
      // 0f3: invokevirtual net/rim/device/api/smartcard/SmartCardSessionClosedException.toString ()Ljava/lang/String;
      // 0f6: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 0f9: athrow
      // 0fa: astore 13
      // 0fc: new java/lang/Object
      // 0ff: dup
      // 100: aload 13
      // 102: invokevirtual net/rim/device/api/smartcard/SmartCardCancelException.toString ()Ljava/lang/String;
      // 105: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 108: athrow
      // 109: astore 13
      // 10b: new java/lang/Object
      // 10e: dup
      // 10f: aload 13
      // 111: invokevirtual net/rim/device/api/smartcard/SmartCardRemovedException.toString ()Ljava/lang/String;
      // 114: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 117: athrow
      // 118: astore 13
      // 11a: ldc2_w 7215549882295292649
      // 11d: ldc_w "net.rim.device.internal.resource.SmartCard"
      // 120: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 123: astore 14
      // 125: aload 14
      // 127: bipush 21
      // 129: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 12c: astore 10
      // 12e: new java/lang/Object
      // 131: dup
      // 132: aload 13
      // 134: invokevirtual net/rim/device/api/smartcard/SmartCardException.toString ()Ljava/lang/String;
      // 137: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 13a: astore 11
      // 13c: aload 9
      // 13e: ifnull 14b
      // 141: iload 12
      // 143: ifeq 14b
      // 146: aload 9
      // 148: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 14b: aload 10
      // 14d: ifnull 179
      // 150: aload 10
      // 152: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 155: aload 11
      // 157: athrow
      // 158: astore 15
      // 15a: aload 9
      // 15c: ifnull 169
      // 15f: iload 12
      // 161: ifeq 169
      // 164: aload 9
      // 166: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 169: aload 10
      // 16b: ifnull 176
      // 16e: aload 10
      // 170: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 173: aload 11
      // 175: athrow
      // 176: aload 15
      // 178: athrow
      // 179: return
      // try (10 -> 60): 77 null
      // try (73 -> 77): 77 null
      // try (10 -> 60): 105 null
      // try (73 -> 77): 105 null
      // try (10 -> 60): 112 null
      // try (73 -> 77): 112 null
      // try (10 -> 60): 119 null
      // try (73 -> 77): 119 null
      // try (10 -> 60): 126 null
      // try (73 -> 77): 126 null
      // try (10 -> 60): 153 null
      // try (73 -> 92): 153 null
      // try (105 -> 141): 153 null
      // try (153 -> 154): 153 null
   }
}
