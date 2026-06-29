package net.rim.device.apps.internal.keystore.browser;

import java.util.Enumeration;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateKeyStoreIndex;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

final class MoveTrustVerb extends Verb {
   boolean _isInTrusted;
   boolean _prompt;
   private String _description;
   private KeyStoreBrowserData[] _browserDatas;
   private KeyStoreBrowserOptionsItem _optionsItem;

   MoveTrustVerb() {
      super(1048704);
   }

   final void initialize(boolean isInTrusted, boolean prompt, KeyStoreBrowserData[] browserDatas, KeyStoreBrowserOptionsItem optionsItem) {
      this._description = KeyStoreBrowserResources.getString(isInTrusted ? 6050 : 6049);
      this._isInTrusted = isInTrusted;
      this._prompt = prompt;
      this._browserDatas = browserDatas;
      this._optionsItem = optionsItem;
   }

   private final void trust() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/keystore/browser/MoveTrustVerb._browserDatas [Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData;
      // 004: arraylength
      // 005: istore 1
      // 006: invokestatic net/rim/device/api/crypto/keystore/TrustedKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 009: checkcast net/rim/device/api/crypto/keystore/TrustedKeyStore
      // 00c: astore 2
      // 00d: bipush -1
      // 00f: istore 3
      // 010: bipush 0
      // 011: istore 4
      // 013: iload 4
      // 015: iload 1
      // 016: if_icmplt 01c
      // 019: goto 138
      // 01c: aload 0
      // 01d: getfield net/rim/device/apps/internal/keystore/browser/MoveTrustVerb._browserDatas [Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData;
      // 020: iload 4
      // 022: aaload
      // 023: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData.getKeyStoreData ()Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 026: astore 5
      // 028: aload 0
      // 029: getfield net/rim/device/apps/internal/keystore/browser/MoveTrustVerb._browserDatas [Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData;
      // 02c: iload 4
      // 02e: aaload
      // 02f: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate;
      // 032: astore 6
      // 034: aload 0
      // 035: getfield net/rim/device/apps/internal/keystore/browser/MoveTrustVerb._browserDatas [Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData;
      // 038: iload 4
      // 03a: aaload
      // 03b: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData.isUntrusted ()Z
      // 03e: ifne 044
      // 041: goto 132
      // 044: aload 2
      // 045: aload 6
      // 047: invokevirtual net/rim/device/api/crypto/keystore/TrustedKeyStore.isAllowed (Lnet/rim/device/api/crypto/certificate/Certificate;)Z
      // 04a: ifne 050
      // 04d: goto 132
      // 050: aload 0
      // 051: getfield net/rim/device/apps/internal/keystore/browser/MoveTrustVerb._browserDatas [Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData;
      // 054: iload 4
      // 056: aaload
      // 057: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData.getBestCertificateChain ()[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 05a: astore 7
      // 05c: aload 7
      // 05e: aload 7
      // 060: arraylength
      // 061: bipush 1
      // 062: isub
      // 063: aaload
      // 064: astore 8
      // 066: aload 7
      // 068: arraylength
      // 069: bipush 1
      // 06a: if_icmple 076
      // 06d: aload 2
      // 06e: aload 8
      // 070: invokevirtual net/rim/device/api/crypto/keystore/TrustedKeyStore.isAllowed (Lnet/rim/device/api/crypto/certificate/Certificate;)Z
      // 073: ifne 07d
      // 076: aload 6
      // 078: astore 9
      // 07a: goto 0df
      // 07d: iload 3
      // 07e: bipush -1
      // 080: if_icmpne 0d0
      // 083: iload 1
      // 084: bipush 1
      // 085: if_icmple 09b
      // 088: sipush 6094
      // 08b: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // 08e: astore 10
      // 090: sipush 6095
      // 093: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getStringArray (I)[Ljava/lang/String;
      // 096: astore 11
      // 098: goto 0ab
      // 09b: sipush 6054
      // 09e: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // 0a1: astore 10
      // 0a3: sipush 6056
      // 0a6: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getStringArray (I)[Ljava/lang/String;
      // 0a9: astore 11
      // 0ab: new net/rim/device/internal/ui/component/SimpleChoiceDialog
      // 0ae: dup
      // 0af: aload 10
      // 0b1: aload 11
      // 0b3: bipush 0
      // 0b4: aconst_null
      // 0b5: invokespecial net/rim/device/internal/ui/component/SimpleChoiceDialog.<init> (Ljava/lang/String;[Ljava/lang/Object;ILnet/rim/device/api/system/Bitmap;)V
      // 0b8: astore 12
      // 0ba: aload 12
      // 0bc: invokevirtual net/rim/device/internal/ui/component/PopupDialog.show ()V
      // 0bf: aload 12
      // 0c1: invokevirtual net/rim/device/internal/ui/component/PopupDialog.getCloseReason ()I
      // 0c4: bipush -1
      // 0c6: if_icmpne 0ca
      // 0c9: return
      // 0ca: aload 12
      // 0cc: invokevirtual net/rim/device/internal/ui/component/SimpleChoiceDialog.getSelectedIndex ()I
      // 0cf: istore 3
      // 0d0: iload 3
      // 0d1: ifne 0db
      // 0d4: aload 6
      // 0d6: astore 9
      // 0d8: goto 0df
      // 0db: aload 8
      // 0dd: astore 9
      // 0df: aload 2
      // 0e0: aload 9
      // 0e2: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStore.isMember (Lnet/rim/device/api/crypto/certificate/Certificate;)Z
      // 0e5: ifne 0ff
      // 0e8: aload 2
      // 0e9: aconst_null
      // 0ea: aload 5
      // 0ec: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 0f1: aload 9
      // 0f3: aconst_null
      // 0f4: aload 0
      // 0f5: getfield net/rim/device/apps/internal/keystore/browser/MoveTrustVerb._optionsItem Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserOptionsItem;
      // 0f8: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserOptionsItem.getTrustedKeyStoreTicket ()Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;
      // 0fb: invokevirtual net/rim/device/api/crypto/keystore/RIMKeyStore.set ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 0fe: pop
      // 0ff: aload 0
      // 100: getfield net/rim/device/apps/internal/keystore/browser/MoveTrustVerb._browserDatas [Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData;
      // 103: iload 4
      // 105: aaload
      // 106: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData.invalidate ()V
      // 109: aload 0
      // 10a: getfield net/rim/device/apps/internal/keystore/browser/MoveTrustVerb._browserDatas [Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData;
      // 10d: iload 4
      // 10f: aaload
      // 110: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData.loadDataIfNeeded ()Z
      // 113: pop
      // 114: goto 132
      // 117: astore 10
      // 119: return
      // 11a: astore 10
      // 11c: bipush 6
      // 11e: invokestatic net/rim/device/apps/internal/api/crypto/CryptoCommonResources.getString (I)Ljava/lang/String;
      // 121: invokestatic net/rim/device/api/ui/component/Dialog.inform (Ljava/lang/String;)V
      // 124: goto 132
      // 127: astore 10
      // 129: sipush 6011
      // 12c: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // 12f: invokestatic net/rim/device/api/ui/component/Dialog.inform (Ljava/lang/String;)V
      // 132: iinc 4 1
      // 135: goto 013
      // 138: return
      // try (107 -> 133): 134 null
      // try (107 -> 133): 136 null
      // try (107 -> 133): 141 null
   }

   private final void distrust() {
      try {
         int numBrowserDatas = this._browserDatas.length;
         if (this._prompt) {
            if (numBrowserDatas == 1) {
               KeyStoreData keyStoreData = this._browserDatas[0].getKeyStoreData();
               if (!SimpleChoiceDialog.askYesNoQuestion(KeyStoreBrowserResources.getString(6035), keyStoreData.getLabel())) {
                  return;
               }
            } else if (!SimpleChoiceDialog.askYesNoQuestion(
               KeyStoreBrowserResources.getString(6093), this._optionsItem.getBrowserContext().getPublicKeyContainerString(false, true)
            )) {
               return;
            }
         }

         boolean errorOccured = false;

         for (int i = 0; i < numBrowserDatas; i++) {
            KeyStoreData keyStoreData = this._browserDatas[i].getKeyStoreData();
            Certificate certificate = keyStoreData.getCertificate();
            if (certificate == null) {
               errorOccured = true;
            } else if (this._browserDatas[i].isExplicitlyTrusted()) {
               KeyStore trustedKeyStore = TrustedKeyStore.getInstance();
               trustedKeyStore.addIndex(new CertificateKeyStoreIndex());
               Enumeration enumeration = trustedKeyStore.elements(-2038609988711824737L, certificate);

               while (enumeration.hasMoreElements()) {
                  KeyStoreData data = (KeyStoreData)enumeration.nextElement();
                  trustedKeyStore.removeKey(data, this._optionsItem.getTrustedKeyStoreTicket());
               }

               this._browserDatas[i].invalidate();
               this._browserDatas[i].loadDataIfNeeded();
            }
         }

         if (errorOccured) {
            Dialog.alert(KeyStoreBrowserResources.getString(6012));
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   public final String toString() {
      return this._description;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (this._isInTrusted) {
         this.distrust();
         return null;
      } else {
         this.trust();
         return null;
      }
   }
}
