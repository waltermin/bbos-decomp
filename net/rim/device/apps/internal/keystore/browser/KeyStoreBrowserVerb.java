package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatusProviderFacade;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.RIMKeyStoreData;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public final class KeyStoreBrowserVerb extends Verb {
   private KeyStoreBrowserOptionsItem _optionsItem;
   private KeyStoreBrowserContext _context;
   private int _type;
   private KeyStoreBrowserData _browserData;
   private KeyStoreBrowserData[] _browserDatas;
   private KeyStoreData[] _keyStoreDatas;
   public static final int CANCEL_HOLD_VERB = 0;
   public static final int CHANGE_LABEL_VERB = 1;
   public static final int DELETE_VERB = 2;
   public static final int REVOKE_VERB = 3;
   public static final int FETCH_STATUS_VERB = 4;
   public static final int FETCH_STATUS_CHAIN_VERB = 5;
   public static final int FETCH_CERT_UPDATE_VERB = 6;
   public static final int ASSOCIATE_ADDRESSES_VERB = 7;
   public static final int CONTINUE_VERB = 8;
   public static final int CHANGE_SECURITY_LEVEL_VERB = 9;

   public KeyStoreBrowserVerb(int type, int menuOrdering) {
      super(menuOrdering);
      this._type = type;
   }

   @Override
   public final String toString() {
      int resourceId;
      switch (this._type) {
         case -1:
            throw new Object();
         case 0:
         default:
            resourceId = 6069;
            break;
         case 1:
            resourceId = 6055;
            break;
         case 2:
            resourceId = 6074;
            break;
         case 3:
            resourceId = 6003;
            break;
         case 4:
            resourceId = 6004;
            break;
         case 5:
            resourceId = 6043;
            break;
         case 6:
            resourceId = 6067;
            break;
         case 7:
            resourceId = 6078;
            break;
         case 8:
            resourceId = 6088;
            break;
         case 9:
            resourceId = 6099;
      }

      return KeyStoreBrowserResources.getString(resourceId);
   }

   final void initialize(KeyStoreBrowserData browserData, KeyStoreBrowserOptionsItem optionsItem) {
      this._browserData = browserData;
      this._optionsItem = optionsItem;
   }

   final void initialize(KeyStoreBrowserData browserData) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void initialize(KeyStoreBrowserData browserData, KeyStoreBrowserContext context) {
      this._browserData = browserData;
      this._context = context;
   }

   public final void initialize(KeyStoreData[] keyStoreDatas, KeyStoreBrowserOptionsItem optionsItem) {
      this._keyStoreDatas = keyStoreDatas;
      this._optionsItem = optionsItem;
   }

   public final void initialize(KeyStoreBrowserData[] browserDatas, KeyStoreBrowserOptionsItem optionsItem) {
      this._browserDatas = browserDatas;
      this._optionsItem = optionsItem;
   }

   @Override
   public final Object invoke(Object parameter) {
      switch (this._type) {
         case 0:
         default:
            this.cancelHold();
            return null;
         case 1:
            this.changeLabel();
            return null;
         case 2:
            this.removeCertificate();
            return null;
         case 3:
            this.revokeCertificate();
            return null;
         case 4:
            this.fetchStatus(false);
            return null;
         case 5:
         case 6:
            this.fetchStatus(true);
            return null;
         case 7:
            this.associateAddress();
            return null;
         case 8:
            this.selectCertificate();
            return null;
         case 9:
            this.changeSecurityLevel();
         case -1:
            return null;
      }
   }

   private final void selectCertificate() {
      this._optionsItem.selectCertificates(this._keyStoreDatas);
   }

   private final void cancelHold() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserVerb._browserData Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData;
      // 04: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData.getKeyStoreData ()Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 07: astore 1
      // 08: sipush 6070
      // 0b: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // 0e: aload 1
      // 0f: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 14: invokestatic net/rim/device/internal/ui/component/SimpleChoiceDialog.askYesNoQuestion (Ljava/lang/String;Ljava/lang/String;)Z
      // 17: ifne 1b
      // 1a: return
      // 1b: invokestatic java/lang/System.currentTimeMillis ()J
      // 1e: lstore 2
      // 1f: new java/lang/Object
      // 22: dup
      // 23: bipush 0
      // 24: lload 2
      // 25: lload 2
      // 26: bipush -1
      // 28: i2l
      // 29: bipush -1
      // 2b: i2l
      // 2c: invokespecial net/rim/device/api/crypto/certificate/CertificateStatus.<init> (IJJJJ)V
      // 2f: astore 4
      // 31: invokestatic net/rim/device/api/crypto/keystore/CertificateStatusManager.getInstance ()Lnet/rim/device/api/crypto/keystore/CertificateStatusManager;
      // 34: aload 1
      // 35: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 3a: aload 4
      // 3c: aload 0
      // 3d: getfield net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserVerb._optionsItem Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserOptionsItem;
      // 40: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserOptionsItem.getManagerTicket ()Lnet/rim/device/api/crypto/keystore/CertificateStatusManagerTicket;
      // 43: invokevirtual net/rim/device/api/crypto/keystore/CertificateStatusManager.setStatus (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/CertificateStatusManagerTicket;)V
      // 46: return
      // 47: astore 2
      // 48: return
      // 49: astore 2
      // 4a: sipush 6028
      // 4d: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // 50: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // 53: return
      // try (11 -> 32): 33 null
      // try (11 -> 32): 35 null
   }

   private final void changeLabel() {
      KeyStoreData data = this._browserData.getKeyStoreData();

      try {
         data.setLabel(null);
         this._browserData.invalidateLabel();
         this._optionsItem.invalidateItem(this._browserData);
      } finally {
         return;
      }
   }

   private final void changeSecurityLevel() {
      RIMKeyStoreData keyStoreData = (RIMKeyStoreData)this._browserData.getKeyStoreData();
      int currentSecurityLevel = keyStoreData.getSecurityLevel();
      int initialSelection = 2;
      switch (currentSecurityLevel) {
         case 1:
            initialSelection = 0;
            break;
         case 3:
            initialSelection = 1;
      }

      String[] allChoices = KeyStoreBrowserResources.getStringArray(6102);
      String[] choices = new Object[allChoices.length];
      System.arraycopy(allChoices, 0, choices, 0, allChoices.length);
      boolean isDisabledLowSecurityPolicy = ITPolicy.getBoolean(24, 7, false);
      Certificate certificate = keyStoreData.getCertificate();
      boolean sign = certificate.queryKeyUsage(1) != 0
         || certificate.queryKeyUsage(2) != 0
         || certificate.queryKeyUsage(32) != 0
         || certificate.queryKeyUsage(64) != 0;
      boolean encrypt = certificate.queryKeyUsage(4) != 0
         || certificate.queryKeyUsage(8) != 0
         || certificate.queryKeyUsage(16) != 0
         || certificate.queryKeyUsage(128) != 0
         || certificate.queryKeyUsage(256) != 0;
      int minSecurityLevel = 0;
      if (isDisabledLowSecurityPolicy) {
         minSecurityLevel = 1;
      }

      if (sign) {
         int signSecurityLevel = ITPolicy.getInteger(24, 45, 1);
         switch (signSecurityLevel) {
            case 0:
               break;
            case 1:
            default:
               signSecurityLevel = 0;
               break;
            case 2:
               signSecurityLevel = 2;
               break;
            case 3:
               signSecurityLevel = 1;
         }

         if (signSecurityLevel > minSecurityLevel) {
            minSecurityLevel = signSecurityLevel;
         }
      }

      if (encrypt) {
         int encryptSecurityLevel = ITPolicy.getInteger(24, 46, 1);
         switch (encryptSecurityLevel) {
            case 0:
               break;
            case 1:
            default:
               encryptSecurityLevel = 0;
               break;
            case 2:
               encryptSecurityLevel = 2;
               break;
            case 3:
               encryptSecurityLevel = 1;
         }

         if (encryptSecurityLevel > minSecurityLevel) {
            minSecurityLevel = encryptSecurityLevel;
         }
      }

      if (initialSelection < minSecurityLevel) {
         initialSelection = minSecurityLevel;
      }

      for (int i = 0; i < minSecurityLevel; i++) {
         Arrays.remove(choices, choices[0]);
         initialSelection--;
      }

      SimpleOKCancelChoiceDialog dialog = new SimpleOKCancelChoiceDialog(KeyStoreBrowserResources.getString(6100), null, choices, initialSelection, null, 65536);
      dialog.show();
      if (dialog.getCloseReason() != -1) {
         int levelIndex = dialog.getSelectedIndex();
         if (initialSelection != levelIndex) {
            if (minSecurityLevel > 0) {
               levelIndex += minSecurityLevel;
            }

            int securityLevel = 2;
            switch (levelIndex) {
               case -1:
                  break;
               case 0:
               default:
                  securityLevel = 1;
                  break;
               case 1:
                  securityLevel = 3;
            }

            try {
               if (!keyStoreData.setSecurityLevel(securityLevel, this._optionsItem.getKeyStoreTicket())) {
                  Dialog.alert(KeyStoreBrowserResources.getString(6103));
                  return;
               }
            } finally {
               return;
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void removeCertificate() {
      int numBrowserDatas = this._browserDatas.length;
      String label = this._browserDatas[0].getLabel();
      if (numBrowserDatas == 1 && label != null && label.length() != 0) {
         if (!SimpleChoiceDialog.askYesNoQuestion(KeyStoreBrowserResources.getString(6034), label)) {
            return;
         }
      } else {
         label = this._optionsItem.getBrowserContext().getPublicKeyContainerString(false, true);
         if (!SimpleChoiceDialog.askYesNoQuestion(KeyStoreBrowserResources.getString(6092), label)) {
            return;
         }
      }

      KeyStore keyStore = this._browserDatas[0].getKeyStore();

      for (int i = 0; i < numBrowserDatas; i++) {
         KeyStoreBrowserData browserData = this._browserDatas[i];
         if (browserData.isExplicitlyTrusted()) {
            MoveTrustVerb verb = new MoveTrustVerb();
            verb.initialize(true, false, new KeyStoreBrowserData[]{browserData}, this._optionsItem);
            verb.invoke(null);
         }

         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            if (!browserData.isExplicitlyTrusted()) {
               keyStore.removeKey(browserData.getKeyStoreData(), this._optionsItem.getKeyStoreTicket());
               var8 = false;
            } else {
               var8 = false;
            }
         } finally {
            if (var8) {
               return;
            }
         }
      }
   }

   private final void revokeCertificate() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserVerb._browserData Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData;
      // 04: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData.getKeyStoreData ()Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 07: astore 1
      // 08: sipush 6036
      // 0b: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // 0e: aload 1
      // 0f: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 14: invokestatic net/rim/device/internal/ui/component/SimpleChoiceDialog.askYesNoQuestion (Ljava/lang/String;Ljava/lang/String;)Z
      // 17: ifne 1b
      // 1a: return
      // 1b: aload 0
      // 1c: getfield net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserVerb._optionsItem Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserOptionsItem;
      // 1f: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserOptionsItem.getBrowserContext ()Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserContext;
      // 22: astore 2
      // 23: aload 2
      // 24: invokeinterface net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserContext.getRelevantRevocationReasons ()[I 1
      // 29: astore 3
      // 2a: aload 3
      // 2b: arraylength
      // 2c: istore 4
      // 2e: iload 4
      // 30: anewarray 915
      // 33: astore 5
      // 35: bipush 0
      // 36: istore 6
      // 38: iload 6
      // 3a: iload 4
      // 3c: if_icmpge 51
      // 3f: aload 5
      // 41: iload 6
      // 43: aload 3
      // 44: iload 6
      // 46: iaload
      // 47: invokestatic net/rim/device/api/crypto/keystore/RevocationReason.getRevocationReason (I)Ljava/lang/String;
      // 4a: aastore
      // 4b: iinc 6 1
      // 4e: goto 38
      // 51: aload 2
      // 52: bipush 0
      // 53: bipush 0
      // 54: invokeinterface net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserContext.getPublicKeyContainerString (ZZ)Ljava/lang/String; 3
      // 59: astore 6
      // 5b: sipush 6058
      // 5e: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // 61: bipush 1
      // 62: anewarray 935
      // 65: dup
      // 66: bipush 0
      // 67: aload 6
      // 69: aastore
      // 6a: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 6d: astore 7
      // 6f: new net/rim/device/apps/internal/keystore/browser/SimpleOKCancelChoiceDialog
      // 72: dup
      // 73: aload 7
      // 75: sipush 6040
      // 78: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // 7b: aload 5
      // 7d: bipush 0
      // 7e: aconst_null
      // 7f: ldc_w 65536
      // 82: i2l
      // 83: invokespecial net/rim/device/apps/internal/keystore/browser/SimpleOKCancelChoiceDialog.<init> (Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;ILnet/rim/device/api/system/Bitmap;J)V
      // 86: astore 8
      // 88: aload 8
      // 8a: invokevirtual net/rim/device/internal/ui/component/PopupDialog.show ()V
      // 8d: aload 8
      // 8f: invokevirtual net/rim/device/internal/ui/component/PopupDialog.getCloseReason ()I
      // 92: bipush -1
      // 94: if_icmpne 98
      // 97: return
      // 98: aload 8
      // 9a: invokevirtual net/rim/device/apps/internal/keystore/browser/SimpleOKCancelChoiceDialog.getSelectedIndex ()I
      // 9d: istore 9
      // 9f: invokestatic net/rim/device/api/crypto/keystore/CertificateStatusManager.getInstance ()Lnet/rim/device/api/crypto/keystore/CertificateStatusManager;
      // a2: aload 1
      // a3: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // a8: aload 3
      // a9: iload 9
      // ab: iaload
      // ac: aload 0
      // ad: getfield net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserVerb._optionsItem Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserOptionsItem;
      // b0: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserOptionsItem.getManagerTicket ()Lnet/rim/device/api/crypto/keystore/CertificateStatusManagerTicket;
      // b3: invokevirtual net/rim/device/api/crypto/keystore/CertificateStatusManager.setStatus (Lnet/rim/device/api/crypto/certificate/Certificate;ILnet/rim/device/api/crypto/keystore/CertificateStatusManagerTicket;)V
      // b6: return
      // b7: astore 2
      // b8: return
      // b9: astore 2
      // ba: sipush 6028
      // bd: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // c0: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // c3: return
      // try (11 -> 71): 86 null
      // try (72 -> 85): 86 null
      // try (11 -> 71): 88 null
      // try (72 -> 85): 88 null
   }

   private final void fetchStatus(boolean fetchChainStatus) {
      Certificate[] chain = this._browserData.getBestCertificateChain();
      if (chain != null && chain.length > 0 && this._context != null) {
         CertificateStatusRequest request = (CertificateStatusRequest)(new Object(chain, fetchChainStatus, this._browserData.getKeyStore(), null, null));
         CertificateStatusProviderFacade.requestCertificateStatus(request, null, true, true);
      } else {
         Dialog.alert(CryptoCommonResources.getString(2));
      }
   }

   private final void associateAddress() {
      UiApplication.getUiApplication().pushScreen(new AssociateAddressesScreen(this._browserData, this._optionsItem));
   }
}
