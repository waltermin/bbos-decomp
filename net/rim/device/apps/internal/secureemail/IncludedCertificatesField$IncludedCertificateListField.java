package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateListField;
import net.rim.device.apps.internal.api.crypto.verb.DisplayCertificateVerb;
import net.rim.device.apps.internal.api.crypto.verb.ImportCertificatesVerb;
import net.rim.device.apps.internal.api.crypto.verb.TrustCertificatesVerb;

class IncludedCertificatesField$IncludedCertificateListField extends CertificateListField implements CursorProviderField {
   private final IncludedCertificatesField this$0;

   IncludedCertificatesField$IncludedCertificateListField(IncludedCertificatesField _1, int size) {
      super(size);
      this.this$0 = _1;
   }

   @Override
   protected int getCheckState(int index) {
      return this.this$0._certificatesOnDevice[this.this$0._filteredIndices[index]] ? 1 : 0;
   }

   @Override
   protected String getText(int index) {
      return this.this$0._certificateNames[this.this$0._filteredIndices[index]];
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu, int instance) {
      super.makeContextMenu(contextMenu, instance);
      int filteredIndex = this.getSelectedIndex();
      int index = this.this$0._filteredIndices[filteredIndex];
      Certificate certificateToImport = this.this$0._certificates[index];
      KeyStore keyStore = this.this$0._secureEmailFactory.getPreferredKeyStore();
      Certificate[] certificateChain = null;
      if (!this.this$0._certificatesOnDevice[index]) {
         VerbMenuItem importCertificateMenuItem = this.this$0._importCertificateMenuItems[index];
         if (importCertificateMenuItem == null) {
            certificateChain = CertificateUtilities.buildCertificateChain(certificateToImport, this.this$0._certificates, keyStore);
            int chainLength = certificateChain.length;
            PrivateKey[] privateKeyChain = new PrivateKey[chainLength];

            for (int i = 0; i < chainLength; i++) {
               Certificate currentCertificate = certificateChain[i];

               for (int j = 0; j < this.this$0._certificates.length; j++) {
                  if (this.this$0._certificates[j].equals(currentCertificate)) {
                     privateKeyChain[i] = this.this$0._privateKeys[j];
                     break;
                  }
               }
            }

            String[] containerStringUpperSingularArray = new String[]{this.this$0._secureEmailFactory.getPublicKeyContainerString(true, false)};
            String importCertificateVerbDescription = MessageFormat.format(CryptoCommonResources.getString(21), containerStringUpperSingularArray);
            importCertificateMenuItem = new VerbMenuItem(
               new ImportCertificatesVerb(importCertificateVerbDescription, certificateChain, privateKeyChain, keyStore), 10
            );
            this.this$0._importCertificateMenuItems[index] = importCertificateMenuItem;
         }

         contextMenu.addItem(importCertificateMenuItem);
         contextMenu.setDefaultItem(importCertificateMenuItem);
      }

      TrustedKeyStore trustedKeyStore = (TrustedKeyStore)TrustedKeyStore.getInstance();
      if (!trustedKeyStore.isMember(certificateToImport)) {
         if (certificateChain == null) {
            certificateChain = CertificateUtilities.buildCertificateChain(certificateToImport, this.this$0._certificates, keyStore);
         }

         String[] containerStringUpperSingularArray = new String[]{this.this$0._secureEmailFactory.getPublicKeyContainerString(true, false)};
         String trustCertificateVerbDescription = MessageFormat.format(CryptoCommonResources.getString(30), containerStringUpperSingularArray);
         long properties = CertificateChainProperties.getCertificateChainProperties(certificateChain, trustedKeyStore, System.currentTimeMillis());
         if (trustedKeyStore.isAllowed(certificateChain[0]) && (properties & 8) != 0) {
            VerbMenuItem certToTrustVerb = new VerbMenuItem(new TrustCertificatesVerb(trustCertificateVerbDescription, certificateChain, keyStore), 10);
            contextMenu.addItem(certToTrustVerb);
         }
      }

      if (this.this$0._filteredIndices.length > 1 && this.this$0._numFilteredCertificatesOnDevice < this.this$0._filteredIndices.length) {
         contextMenu.addItem(this.this$0._importAllCertificatesMenuItem);
      }

      DisplayCertificateVerb dcv = (DisplayCertificateVerb)this.this$0._displayCertificateMenuItem.getVerb();
      dcv.setCertificate(certificateToImport, null, keyStore, this.this$0._secureEmailFactory.getCryptoSystemProperties(), null);
      contextMenu.addItem(this.this$0._displayCertificateMenuItem);
      if (this.this$0._certificatesOnDevice[index]) {
         contextMenu.setDefaultItem(this.this$0._displayCertificateMenuItem);
      }
   }

   @Override
   public int getCurrentCursorPosition(Object context) {
      return this.getSelectedIndex();
   }

   @Override
   public void setCursorPosition(int pos, Object context) {
      if (pos >= 0 && pos < this.getCursorCount(context)) {
         this.setSelectedIndex(pos);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public int getCursorCount(Object context) {
      return this.getSize();
   }
}
