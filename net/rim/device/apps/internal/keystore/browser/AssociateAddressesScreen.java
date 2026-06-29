package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.AssociatedData;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.vm.Array;

public final class AssociateAddressesScreen extends AppsMainScreen {
   ObjectListField _associatedAddressesListField;
   ObjectListField _linlineAddressesListField;
   AssociateAddressesScreen$AssociateAddressesVerb _addVerb;
   AssociateAddressesScreen$AssociateAddressesVerb _editVerb;
   AssociateAddressesScreen$AssociateAddressesVerb _deleteVerb;
   AssociateAddressesScreen$AssociateAddressesVerb _saveVerb;
   KeyStoreBrowserData _keyStoreBrowserData;
   KeyStoreBrowserOptionsItem _optionsItem;

   public AssociateAddressesScreen(KeyStoreBrowserData data, KeyStoreBrowserOptionsItem optionsItem) {
      super(0);
      this._keyStoreBrowserData = data;
      this._optionsItem = optionsItem;
      this.setTitle(KeyStoreBrowserResources.getString(6078));
      Font font = Font.getDefault();
      Font boldFont = font.derive(font.getStyle() | 1);
      Certificate certificate = data.getCertificate();
      if (certificate != null) {
         String[] fixedEmails = (Object[])certificate.getInformation(-7850001002262082664L, null, null);
         String inlineLabelText = MessageFormat.format(
            KeyStoreBrowserResources.getString(6082), new Object[]{optionsItem.getBrowserContext().getPublicKeyContainerString(true, false)}
         );
         LabelField inlineLabelField = (LabelField)(new Object(inlineLabelText));
         inlineLabelField.setFont(boldFont);
         this.add(inlineLabelField);
         this._linlineAddressesListField = (ObjectListField)(new Object());
         if (fixedEmails != null) {
            this._linlineAddressesListField.set(fixedEmails);
         }

         this.add(this._linlineAddressesListField);
         this.add((Field)(new Object()));
      }

      LabelField otherAddressLabelField = (LabelField)(new Object(KeyStoreBrowserResources.getString(6083)));
      otherAddressLabelField.setFont(boldFont);
      this.add(otherAddressLabelField);
      this._associatedAddressesListField = (ObjectListField)(new Object());
      KeyStoreData keyStoreData = this._keyStoreBrowserData.getKeyStoreData();
      byte[][][] emailAddresses = (byte[][][])keyStoreData.getAssociatedData(-1124699153917633064L);
      if (emailAddresses != null) {
         int numEmailAddresses = emailAddresses.length;
         String[] emailAddressStrings = new Object[0];

         for (int i = 0; i < numEmailAddresses; i++) {
            String email = (String)(new Object((byte[])emailAddresses[i]));
            if (!this.addressExists(email)) {
               Array.resize(emailAddressStrings, emailAddressStrings.length + 1);
               emailAddressStrings[emailAddressStrings.length - 1] = email;
            }
         }

         if (emailAddressStrings.length > 0) {
            this._associatedAddressesListField.set(emailAddressStrings);
         }
      }

      this.add(this._associatedAddressesListField);
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (this._addVerb == null) {
         ResourceBundleFamily rb = KeyStoreBrowserResources.getResourceBundle();
         this._addVerb = new AssociateAddressesScreen$AssociateAddressesVerb(this, 1, 0, rb, 6079);
         this._editVerb = new AssociateAddressesScreen$AssociateAddressesVerb(this, 2, 0, rb, 6080);
         this._deleteVerb = new AssociateAddressesScreen$AssociateAddressesVerb(this, 3, 0, rb, 6081);
         this._saveVerb = new AssociateAddressesScreen$AssociateAddressesVerb(this, 4, 0, rb, 6096);
      }

      menu.add(this._addVerb);
      menu.setDefault(this._addVerb);
      Field leafField = this.getLeafFieldWithFocus();
      if (leafField == this._associatedAddressesListField) {
         int selectedIndex = this._associatedAddressesListField.getSelectedIndex();
         if (selectedIndex != -1) {
            this._editVerb.setIndex(selectedIndex);
            this._deleteVerb.setIndex(selectedIndex);
            menu.add(this._editVerb);
            menu.add(this._deleteVerb);
         }
      }

      if (this.isDirty()) {
         menu.add(this._saveVerb);
      }
   }

   private final AssociatedData[] getAssociatedDataArray() {
      AssociatedData[] associatedData = CertificateUtilities.getEmailAssociatedDataArray(this._keyStoreBrowserData.getKeyStoreData().getCertificate());
      if (associatedData == null) {
         associatedData = new Object[0];
      }

      int size = this._associatedAddressesListField.getSize();

      for (int i = 0; i < size; i++) {
         Array.resize(associatedData, associatedData.length + 1);
         String address = (String)this._associatedAddressesListField.get(this._associatedAddressesListField, i);
         associatedData[associatedData.length - 1] = (AssociatedData)(new Object(
            -1124699153917633064L, StringUtilities.toLowerCase(address, 1701707776).getBytes()
         ));
      }

      return associatedData;
   }

   @Override
   public final void save() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/keystore/browser/AssociateAddressesScreen._keyStoreBrowserData Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData;
      // 04: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData.getKeyStoreData ()Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 07: astore 1
      // 08: aload 0
      // 09: getfield net/rim/device/apps/internal/keystore/browser/AssociateAddressesScreen._keyStoreBrowserData Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData;
      // 0c: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserData.getKeyStore ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 0f: astore 2
      // 10: aload 1
      // 11: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 16: astore 3
      // 17: aload 1
      // 18: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 1d: astore 4
      // 1f: aload 0
      // 20: getfield net/rim/device/apps/internal/keystore/browser/AssociateAddressesScreen._optionsItem Lnet/rim/device/apps/internal/keystore/browser/KeyStoreBrowserOptionsItem;
      // 23: invokevirtual net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserOptionsItem.getKeyStoreTicket ()Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;
      // 26: astore 5
      // 28: aload 0
      // 29: invokespecial net/rim/device/apps/internal/keystore/browser/AssociateAddressesScreen.getAssociatedDataArray ()[Lnet/rim/device/api/crypto/keystore/AssociatedData;
      // 2c: astore 6
      // 2e: invokestatic net/rim/device/api/crypto/keystore/CertificateStatusManager.getInstance ()Lnet/rim/device/api/crypto/keystore/CertificateStatusManager;
      // 31: astore 7
      // 33: aload 7
      // 35: aload 3
      // 36: invokevirtual net/rim/device/api/crypto/keystore/CertificateStatusManager.getStatus (Lnet/rim/device/api/crypto/certificate/Certificate;)Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 39: astore 8
      // 3b: aload 2
      // 3c: aload 6
      // 3e: aload 4
      // 40: aload 3
      // 41: aload 8
      // 43: aload 5
      // 45: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.set ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData; 6
      // 4a: ifnull 63
      // 4d: aload 2
      // 4e: aload 1
      // 4f: aload 5
      // 51: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.removeKey (Lnet/rim/device/api/crypto/keystore/KeyStoreData;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)V 3
      // 56: return
      // 57: astore 1
      // 58: return
      // 59: astore 1
      // 5a: sipush 6084
      // 5d: invokestatic net/rim/device/apps/internal/keystore/browser/KeyStoreBrowserResources.getString (I)Ljava/lang/String;
      // 60: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // 63: return
      // try (0 -> 39): 40 null
      // try (0 -> 39): 42 null
   }

   private final boolean addressExists(String newAddress) {
      int size = this._associatedAddressesListField.getSize();

      for (int i = 0; i < size; i++) {
         if (newAddress.equalsIgnoreCase((String)this._associatedAddressesListField.get(this._associatedAddressesListField, i))) {
            return true;
         }
      }

      size = this._linlineAddressesListField.getSize();

      for (int i = 0; i < size; i++) {
         if (newAddress.equalsIgnoreCase((String)this._linlineAddressesListField.get(this._linlineAddressesListField, i))) {
            return true;
         }
      }

      return false;
   }
}
