package net.rim.device.apps.internal.smartcard.gsacac;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoTokenPrivateKeyData;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSACryptoToken;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.smartcard.SmartCardSession;
import net.rim.device.api.util.Persistable;

final class CACRSACryptoToken extends RSACryptoToken implements Persistable {
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
      return privateKeyData instanceof CACCryptoTokenData;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void decryptRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) throws CryptoTokenException {
      try {
         this.signDecryptHelper(cryptoSystem, privateKeyData, input, inputOffset, output, outputOffset, _rb.getString(17), 2, null);
      } catch (Throwable var9) {
         throw new CryptoTokenException(e.toString());
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

   public final void signDecryptHelper(
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
      // 014: instanceof net/rim/device/apps/internal/smartcard/gsacac/CACCryptoTokenData
      // 017: ifne 01e
      // 01a: pop
      // 01b: goto 0a1
      // 01e: checkcast net/rim/device/apps/internal/smartcard/gsacac/CACCryptoTokenData
      // 021: invokevirtual net/rim/device/apps/internal/smartcard/gsacac/CACCryptoTokenData.getSmartCardID ()Lnet/rim/device/api/smartcard/SmartCardID;
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
      // 042: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 045: dup
      // 046: ldc_w "Wrong card inserted"
      // 049: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 04c: athrow
      // 04d: aload 9
      // 04f: dup
      // 050: instanceof net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession
      // 053: ifne 05a
      // 056: pop
      // 057: goto 0a1
      // 05a: checkcast net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession
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
      // 074: checkcast net/rim/device/apps/internal/smartcard/gsacac/CACCryptoTokenData
      // 077: aload 3
      // 078: iload 4
      // 07a: aload 5
      // 07c: iload 6
      // 07e: invokevirtual net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession.signDecrypt (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/apps/internal/smartcard/gsacac/CACCryptoTokenData;[BI[BI)V
      // 081: aload 9
      // 083: ifnull 090
      // 086: iload 12
      // 088: ifeq 090
      // 08b: aload 9
      // 08d: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 090: aload 10
      // 092: ifnull 0a0
      // 095: aload 10
      // 097: ldc_w -2147483644
      // 09a: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 09d: aload 11
      // 09f: athrow
      // 0a0: return
      // 0a1: new java/lang/RuntimeException
      // 0a4: dup
      // 0a5: invokespecial java/lang/RuntimeException.<init> ()V
      // 0a8: athrow
      // 0a9: astore 13
      // 0ab: ldc2_w 7215549882295292649
      // 0ae: ldc_w "net.rim.device.internal.resource.SmartCard"
      // 0b1: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 0b4: astore 14
      // 0b6: aload 14
      // 0b8: bipush 14
      // 0ba: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0bd: astore 10
      // 0bf: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 0c2: dup
      // 0c3: aload 13
      // 0c5: invokevirtual net/rim/device/api/smartcard/SmartCardLockedException.toString ()Ljava/lang/String;
      // 0c8: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 0cb: astore 11
      // 0cd: aload 9
      // 0cf: ifnull 0dc
      // 0d2: iload 12
      // 0d4: ifeq 0dc
      // 0d7: aload 9
      // 0d9: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 0dc: aload 10
      // 0de: ifnonnull 0e4
      // 0e1: goto 183
      // 0e4: aload 10
      // 0e6: ldc_w -2147483644
      // 0e9: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 0ec: aload 11
      // 0ee: athrow
      // 0ef: astore 13
      // 0f1: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 0f4: dup
      // 0f5: aload 13
      // 0f7: invokevirtual net/rim/device/api/smartcard/SmartCardSessionClosedException.toString ()Ljava/lang/String;
      // 0fa: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 0fd: athrow
      // 0fe: astore 13
      // 100: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 103: dup
      // 104: aload 13
      // 106: invokevirtual net/rim/device/api/smartcard/SmartCardCancelException.toString ()Ljava/lang/String;
      // 109: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 10c: athrow
      // 10d: astore 13
      // 10f: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 112: dup
      // 113: aload 13
      // 115: invokevirtual net/rim/device/api/smartcard/SmartCardRemovedException.toString ()Ljava/lang/String;
      // 118: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 11b: athrow
      // 11c: astore 13
      // 11e: ldc2_w 7215549882295292649
      // 121: ldc_w "net.rim.device.internal.resource.SmartCard"
      // 124: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 127: astore 14
      // 129: aload 14
      // 12b: bipush 21
      // 12d: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 130: astore 10
      // 132: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 135: dup
      // 136: aload 13
      // 138: invokevirtual net/rim/device/api/smartcard/SmartCardException.toString ()Ljava/lang/String;
      // 13b: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 13e: astore 11
      // 140: aload 9
      // 142: ifnull 14f
      // 145: iload 12
      // 147: ifeq 14f
      // 14a: aload 9
      // 14c: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 14f: aload 10
      // 151: ifnull 183
      // 154: aload 10
      // 156: ldc_w -2147483644
      // 159: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 15c: aload 11
      // 15e: athrow
      // 15f: astore 15
      // 161: aload 9
      // 163: ifnull 170
      // 166: iload 12
      // 168: ifeq 170
      // 16b: aload 9
      // 16d: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 170: aload 10
      // 172: ifnull 180
      // 175: aload 10
      // 177: ldc_w -2147483644
      // 17a: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 17d: aload 11
      // 17f: athrow
      // 180: aload 15
      // 182: athrow
      // 183: return
      // try (10 -> 59): 77 null
      // try (73 -> 77): 77 null
      // try (10 -> 59): 106 null
      // try (73 -> 77): 106 null
      // try (10 -> 59): 113 null
      // try (73 -> 77): 113 null
      // try (10 -> 59): 120 null
      // try (73 -> 77): 120 null
      // try (10 -> 59): 127 null
      // try (73 -> 77): 127 null
      // try (10 -> 59): 155 null
      // try (73 -> 92): 155 null
      // try (106 -> 142): 155 null
      // try (155 -> 156): 155 null
   }
}
