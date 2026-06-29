package net.rim.device.apps.internal.smartcard.piv;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoTokenPrivateKeyData;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSACryptoToken;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.smartcard.SmartCardSession;
import net.rim.device.api.util.Persistable;

final class PIVRSACryptoToken extends RSACryptoToken implements Persistable {
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
      return privateKeyData instanceof PIVCryptoTokenData;
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
      // 014: instanceof net/rim/device/apps/internal/smartcard/piv/PIVCryptoTokenData
      // 017: ifne 01e
      // 01a: pop
      // 01b: goto 0ad
      // 01e: checkcast net/rim/device/apps/internal/smartcard/piv/PIVCryptoTokenData
      // 021: astore 13
      // 023: aload 13
      // 025: invokevirtual net/rim/device/apps/internal/smartcard/piv/PIVCryptoTokenData.getSmartCardID ()Lnet/rim/device/api/smartcard/SmartCardID;
      // 028: astore 14
      // 02a: aload 9
      // 02c: ifnonnull 039
      // 02f: aload 14
      // 031: invokestatic net/rim/device/api/smartcard/SmartCardFactory.getSmartCardSession (Lnet/rim/device/api/smartcard/SmartCardID;)Lnet/rim/device/api/smartcard/SmartCardSession;
      // 034: astore 9
      // 036: goto 051
      // 039: aload 14
      // 03b: aload 9
      // 03d: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getSmartCardID ()Lnet/rim/device/api/smartcard/SmartCardID;
      // 040: invokevirtual net/rim/device/api/smartcard/SmartCardID.equals (Ljava/lang/Object;)Z
      // 043: ifne 051
      // 046: new java/lang/Object
      // 049: dup
      // 04a: ldc_w "Wrong card inserted"
      // 04d: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 050: athrow
      // 051: aload 9
      // 053: dup
      // 054: instanceof net/rim/device/apps/internal/smartcard/piv/PIVCryptoSmartCardSession
      // 057: ifne 05e
      // 05a: pop
      // 05b: goto 0ad
      // 05e: checkcast net/rim/device/apps/internal/smartcard/piv/PIVCryptoSmartCardSession
      // 061: astore 15
      // 063: aload 15
      // 065: invokevirtual net/rim/device/api/smartcard/SmartCardSession.isLoggedIn ()Z
      // 068: ifne 07e
      // 06b: aload 13
      // 06d: invokevirtual net/rim/device/apps/internal/smartcard/piv/PIVCryptoTokenData.getKeyReference ()B
      // 070: bipush -98
      // 072: if_icmpeq 07e
      // 075: aload 15
      // 077: aload 7
      // 079: iload 8
      // 07b: invokevirtual net/rim/device/api/smartcard/SmartCardSession.loginPrompt (Ljava/lang/String;I)V
      // 07e: aload 15
      // 080: aload 1
      // 081: aload 13
      // 083: aload 3
      // 084: iload 4
      // 086: aload 5
      // 088: iload 6
      // 08a: invokevirtual net/rim/device/apps/internal/smartcard/piv/PIVCryptoSmartCardSession.signDecrypt (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/apps/internal/smartcard/piv/PIVCryptoTokenData;[BI[BI)V
      // 08d: aload 9
      // 08f: ifnull 09c
      // 092: iload 12
      // 094: ifeq 09c
      // 097: aload 9
      // 099: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 09c: aload 10
      // 09e: ifnull 0ac
      // 0a1: aload 10
      // 0a3: ldc_w -2147483644
      // 0a6: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 0a9: aload 11
      // 0ab: athrow
      // 0ac: return
      // 0ad: new java/lang/Object
      // 0b0: dup
      // 0b1: invokespecial java/lang/RuntimeException.<init> ()V
      // 0b4: athrow
      // 0b5: astore 13
      // 0b7: ldc2_w 7215549882295292649
      // 0ba: ldc_w "net.rim.device.internal.resource.SmartCard"
      // 0bd: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 0c0: astore 14
      // 0c2: aload 14
      // 0c4: bipush 14
      // 0c6: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0c9: astore 10
      // 0cb: new java/lang/Object
      // 0ce: dup
      // 0cf: aload 13
      // 0d1: invokevirtual net/rim/device/api/smartcard/SmartCardLockedException.toString ()Ljava/lang/String;
      // 0d4: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 0d7: astore 11
      // 0d9: aload 9
      // 0db: ifnull 0e8
      // 0de: iload 12
      // 0e0: ifeq 0e8
      // 0e3: aload 9
      // 0e5: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 0e8: aload 10
      // 0ea: ifnonnull 0f0
      // 0ed: goto 18f
      // 0f0: aload 10
      // 0f2: ldc_w -2147483644
      // 0f5: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 0f8: aload 11
      // 0fa: athrow
      // 0fb: astore 13
      // 0fd: new java/lang/Object
      // 100: dup
      // 101: aload 13
      // 103: invokevirtual net/rim/device/api/smartcard/SmartCardSessionClosedException.toString ()Ljava/lang/String;
      // 106: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 109: athrow
      // 10a: astore 13
      // 10c: new java/lang/Object
      // 10f: dup
      // 110: aload 13
      // 112: invokevirtual net/rim/device/api/smartcard/SmartCardCancelException.toString ()Ljava/lang/String;
      // 115: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 118: athrow
      // 119: astore 13
      // 11b: new java/lang/Object
      // 11e: dup
      // 11f: aload 13
      // 121: invokevirtual net/rim/device/api/smartcard/SmartCardRemovedException.toString ()Ljava/lang/String;
      // 124: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 127: athrow
      // 128: astore 13
      // 12a: ldc2_w 7215549882295292649
      // 12d: ldc_w "net.rim.device.internal.resource.SmartCard"
      // 130: invokestatic net/rim/device/api/i18n/ResourceBundle.getBundle (JLjava/lang/String;)Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 133: astore 14
      // 135: aload 14
      // 137: bipush 21
      // 139: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 13c: astore 10
      // 13e: new java/lang/Object
      // 141: dup
      // 142: aload 13
      // 144: invokevirtual net/rim/device/api/smartcard/SmartCardException.toString ()Ljava/lang/String;
      // 147: invokespecial net/rim/device/api/crypto/CryptoTokenCancelException.<init> (Ljava/lang/String;)V
      // 14a: astore 11
      // 14c: aload 9
      // 14e: ifnull 15b
      // 151: iload 12
      // 153: ifeq 15b
      // 156: aload 9
      // 158: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 15b: aload 10
      // 15d: ifnull 18f
      // 160: aload 10
      // 162: ldc_w -2147483644
      // 165: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 168: aload 11
      // 16a: athrow
      // 16b: astore 16
      // 16d: aload 9
      // 16f: ifnull 17c
      // 172: iload 12
      // 174: ifeq 17c
      // 177: aload 9
      // 179: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 17c: aload 10
      // 17e: ifnull 18c
      // 181: aload 10
      // 183: ldc_w -2147483644
      // 186: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 189: aload 11
      // 18b: athrow
      // 18c: aload 16
      // 18e: athrow
      // 18f: return
      // try (10 -> 64): 82 null
      // try (78 -> 82): 82 null
      // try (10 -> 64): 111 null
      // try (78 -> 82): 111 null
      // try (10 -> 64): 118 null
      // try (78 -> 82): 118 null
      // try (10 -> 64): 125 null
      // try (78 -> 82): 125 null
      // try (10 -> 64): 132 null
      // try (78 -> 82): 132 null
      // try (10 -> 64): 160 null
      // try (78 -> 97): 160 null
      // try (111 -> 147): 160 null
      // try (160 -> 161): 160 null
   }
}
