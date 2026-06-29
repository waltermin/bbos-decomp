package net.rim.device.apps.internal.smartcard.datakey;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.CryptoTokenCryptoSystemData;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoTokenPrivateKeyData;
import net.rim.device.api.crypto.DSACryptoToken;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.Persistable;

final class DatakeyDSACryptoToken extends DSACryptoToken implements Persistable {
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
   public final int getDSAPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      return 20;
   }

   @Override
   public final void signDSA(
      CryptoTokenCryptoSystemData cryptoSystemData,
      CryptoTokenPrivateKeyData privateKeyData,
      byte[] digest,
      int digestOffset,
      int digestLength,
      byte[] r,
      int rOffset,
      byte[] s,
      int sOffset
   ) {
      this.signDSA(cryptoSystemData, privateKeyData, digest, digestOffset, digestLength, r, rOffset, s, sOffset, null);
   }

   @Override
   public final void signDSA(
      CryptoTokenCryptoSystemData param1,
      CryptoTokenPrivateKeyData param2,
      byte[] param3,
      int param4,
      int param5,
      byte[] param6,
      int param7,
      byte[] param8,
      int param9,
      Object param10
   ) throws CryptoTokenException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 10
      // 002: checkcast net/rim/device/api/smartcard/SmartCardSession
      // 005: astore 11
      // 007: aconst_null
      // 008: astore 12
      // 00a: aconst_null
      // 00b: astore 13
      // 00d: aload 11
      // 00f: ifnonnull 016
      // 012: bipush 1
      // 013: goto 017
      // 016: bipush 0
      // 017: istore 14
      // 019: aload 2
      // 01a: dup
      // 01b: instanceof net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData
      // 01e: ifne 025
      // 021: pop
      // 022: goto 0b3
      // 025: checkcast net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData
      // 028: invokevirtual net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData.getSmartCardID ()Lnet/rim/device/api/smartcard/SmartCardID;
      // 02b: astore 15
      // 02d: aload 11
      // 02f: ifnonnull 03c
      // 032: aload 15
      // 034: invokestatic net/rim/device/api/smartcard/SmartCardFactory.getSmartCardSession (Lnet/rim/device/api/smartcard/SmartCardID;)Lnet/rim/device/api/smartcard/SmartCardSession;
      // 037: astore 11
      // 039: goto 054
      // 03c: aload 15
      // 03e: aload 11
      // 040: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getSmartCardID ()Lnet/rim/device/api/smartcard/SmartCardID;
      // 043: invokevirtual net/rim/device/api/smartcard/SmartCardID.equals (Ljava/lang/Object;)Z
      // 046: ifne 054
      // 049: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 04c: dup
      // 04d: ldc_w "Wrong card inserted"
      // 050: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 053: athrow
      // 054: aload 11
      // 056: dup
      // 057: instanceof net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoSmartCardSession
      // 05a: ifne 061
      // 05d: pop
      // 05e: goto 0b3
      // 061: checkcast net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoSmartCardSession
      // 064: astore 16
      // 066: aload 16
      // 068: invokevirtual net/rim/device/api/smartcard/SmartCardSession.isLoggedIn ()Z
      // 06b: ifne 07c
      // 06e: aload 16
      // 070: getstatic net/rim/device/apps/internal/smartcard/datakey/DatakeyDSACryptoToken._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 073: bipush 16
      // 075: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 078: bipush 1
      // 079: invokevirtual net/rim/device/api/smartcard/SmartCardSession.loginPrompt (Ljava/lang/String;I)V
      // 07c: aload 16
      // 07e: aload 1
      // 07f: checkcast net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenCryptoSystemData
      // 082: aload 2
      // 083: checkcast net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData
      // 086: aload 3
      // 087: iload 4
      // 089: iload 5
      // 08b: aload 6
      // 08d: iload 7
      // 08f: aload 8
      // 091: iload 9
      // 093: invokevirtual net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoSmartCardSession.signDSA (Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;Lnet/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData;[BII[BI[BI)V
      // 096: aload 11
      // 098: ifnull 0a5
      // 09b: iload 14
      // 09d: ifeq 0a5
      // 0a0: aload 11
      // 0a2: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 0a5: aload 12
      // 0a7: ifnull 0b2
      // 0aa: aload 12
      // 0ac: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 0af: aload 13
      // 0b1: athrow
      // 0b2: return
      // 0b3: new java/lang/RuntimeException
      // 0b6: dup
      // 0b7: invokespecial java/lang/RuntimeException.<init> ()V
      // 0ba: athrow
      // 0bb: astore 15
      // 0bd: ldc2_w 7215549882295292649
      // 0c0: ldc_w "net.rim.device.internal.resource.SmartCard"
      // 0c3: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 0c6: astore 16
      // 0c8: aload 16
      // 0ca: bipush 14
      // 0cc: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0cf: astore 12
      // 0d1: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 0d4: dup
      // 0d5: aload 15
      // 0d7: invokevirtual net/rim/device/api/smartcard/SmartCardLockedException.toString ()Ljava/lang/String;
      // 0da: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 0dd: astore 13
      // 0df: aload 11
      // 0e1: ifnull 0ee
      // 0e4: iload 14
      // 0e6: ifeq 0ee
      // 0e9: aload 11
      // 0eb: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 0ee: aload 12
      // 0f0: ifnonnull 0f6
      // 0f3: goto 18c
      // 0f6: aload 12
      // 0f8: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 0fb: aload 13
      // 0fd: athrow
      // 0fe: astore 15
      // 100: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 103: dup
      // 104: aload 15
      // 106: invokevirtual net/rim/device/api/smartcard/SmartCardSessionClosedException.toString ()Ljava/lang/String;
      // 109: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 10c: athrow
      // 10d: astore 15
      // 10f: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 112: dup
      // 113: aload 15
      // 115: invokevirtual net/rim/device/api/smartcard/SmartCardCancelException.toString ()Ljava/lang/String;
      // 118: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 11b: athrow
      // 11c: astore 15
      // 11e: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 121: dup
      // 122: aload 15
      // 124: invokevirtual net/rim/device/api/smartcard/SmartCardRemovedException.toString ()Ljava/lang/String;
      // 127: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 12a: athrow
      // 12b: astore 15
      // 12d: ldc2_w 7215549882295292649
      // 130: ldc_w "net.rim.device.internal.resource.SmartCard"
      // 133: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 136: astore 16
      // 138: aload 16
      // 13a: bipush 21
      // 13c: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 13f: astore 12
      // 141: new net/rim/device/api/crypto/CryptoTokenCancelException
      // 144: dup
      // 145: aload 15
      // 147: invokevirtual net/rim/device/api/smartcard/SmartCardException.toString ()Ljava/lang/String;
      // 14a: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 14d: astore 13
      // 14f: aload 11
      // 151: ifnull 15e
      // 154: iload 14
      // 156: ifeq 15e
      // 159: aload 11
      // 15b: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 15e: aload 12
      // 160: ifnull 18c
      // 163: aload 12
      // 165: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 168: aload 13
      // 16a: athrow
      // 16b: astore 17
      // 16d: aload 11
      // 16f: ifnull 17c
      // 172: iload 14
      // 174: ifeq 17c
      // 177: aload 11
      // 179: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 17c: aload 12
      // 17e: ifnull 189
      // 181: aload 12
      // 183: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 186: aload 13
      // 188: athrow
      // 189: aload 17
      // 18b: athrow
      // 18c: return
      // try (13 -> 68): 85 null
      // try (81 -> 85): 85 null
      // try (13 -> 68): 113 null
      // try (81 -> 85): 113 null
      // try (13 -> 68): 120 null
      // try (81 -> 85): 120 null
      // try (13 -> 68): 127 null
      // try (81 -> 85): 127 null
      // try (13 -> 68): 134 null
      // try (81 -> 85): 134 null
      // try (13 -> 68): 161 null
      // try (81 -> 100): 161 null
      // try (113 -> 149): 161 null
      // try (161 -> 162): 161 null
   }
}
