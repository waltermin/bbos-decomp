package net.rim.device.apps.internal.api.crypto.certificate;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.verb.DisplayCertificateVerb;
import net.rim.device.apps.internal.api.crypto.verb.ImportCertificatesVerb;
import net.rim.device.apps.internal.api.crypto.verb.TrustCertificatesVerb;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.component.HorizontalSpacerField;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

class CertificateAttachmentField extends HorizontalFieldManager implements VerbProvider, CollectionListener {
   private CertificateAttachmentModel _certificateAttachmentModel;
   private ListField _listField;
   private Application _displayApp;

   CertificateAttachmentField(CertificateAttachmentModel attachmentModel) {
      this._certificateAttachmentModel = attachmentModel;
      Certificate[] certificates = attachmentModel.getCertificates();
      this.add(CryptoIcons.getLargeImageField(this._certificateAttachmentModel.getImage(), 0));
      this.add(new HorizontalSpacerField(3));
      int numRows = certificates == null ? 1 : certificates.length;
      this._listField = new CertificateAttachmentField$AttachedCertificateListField(this, numRows);
      this._listField.setCookie(this);
      this.add(this._listField);
      this._displayApp = Application.getApplication();
      this._certificateAttachmentModel.getPreferredKeyStore().addCollectionListener(new WeakReference(this));
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      if (!ContextObject.getFlag(context, 87) && contextObject.getFlag(2)) {
         Verb defaultVerb = null;
         if (this._certificateAttachmentModel.isMoreAvailable()) {
            defaultVerb = new CertificateAttachmentField$RetrieveCertificateVerb(this, this._certificateAttachmentModel);
            Array.resize(verbs, 1);
            verbs[0] = defaultVerb;
         } else {
            int selectedIndex = this._listField.getSelectedIndex();
            Certificate certificate = this.getCertificateAtIndex(selectedIndex);
            PrivateKey privateKey = this.getPrivateKeyAtIndex(selectedIndex);

            try {
               if (certificate != null) {
                  KeyStore keyStore = this._certificateAttachmentModel.getPreferredKeyStore();
                  String[] containerStringUpperSingularArray = new String[]{this._certificateAttachmentModel.getPublicKeyContainerString(true, false)};
                  String displayCertificateVerbDescription = MessageFormat.format(CryptoCommonResources.getString(20), containerStringUpperSingularArray);
                  defaultVerb = new DisplayCertificateVerb(displayCertificateVerbDescription, certificate, keyStore);
                  Array.resize(verbs, verbs.length + 1);
                  verbs[verbs.length - 1] = defaultVerb;
                  if (this._certificateAttachmentModel.showImportVerb(selectedIndex)) {
                     String importCertificateVerbDescription = MessageFormat.format(CryptoCommonResources.getString(21), containerStringUpperSingularArray);
                     Array.resize(verbs, verbs.length + 1);
                     verbs[verbs.length - 1] = new ImportCertificatesVerb(
                        importCertificateVerbDescription, new Certificate[]{certificate}, new PrivateKey[]{privateKey}, keyStore
                     );
                  }

                  Certificate[] certificateChain = CertificateUtilities.buildCertificateChain(certificate, keyStore);
                  if (this._certificateAttachmentModel.showTrustVerb(certificateChain, privateKey, keyStore)) {
                     String trustCertificateVerbDescription = MessageFormat.format(CryptoCommonResources.getString(30), containerStringUpperSingularArray);
                     Array.resize(verbs, verbs.length + 1);
                     verbs[verbs.length - 1] = new TrustCertificatesVerb(trustCertificateVerbDescription, certificateChain, privateKey, keyStore);
                  }

                  String[] addresses = (String[])certificate.getInformation(-7850001002262082664L, null, null);
                  if (addresses != null && addresses.length > 0) {
                     ContextObject emailContext = new ContextObject(44);
                     emailContext.put(253, addresses[0]);
                     Object obj = FactoryUtil.createInstance(-2985347935260258684L, emailContext);
                     if (obj instanceof VerbProvider) {
                        VerbProvider verbProvider = (VerbProvider)obj;
                        Verb[] emailVerbs = new Verb[0];
                        verbProvider.getVerbs(emailContext, emailVerbs);
                        if (emailVerbs.length > 0) {
                           int oldNumVerbs = verbs.length;
                           Array.resize(verbs, oldNumVerbs + emailVerbs.length);
                           System.arraycopy(emailVerbs, 0, verbs, oldNumVerbs, emailVerbs.length);
                        }
                     }
                  }
               }
            } finally {
               return defaultVerb;
            }
         }

         return defaultVerb;
      } else {
         return null;
      }
   }

   private Certificate getCertificateAtIndex(int index) {
      Certificate[] certificates = this._certificateAttachmentModel.getCertificates();
      return certificates != null && index >= 0 ? certificates[index] : null;
   }

   private PrivateKey getPrivateKeyAtIndex(int index) {
      PrivateKey[] privateKeys = this._certificateAttachmentModel.getPrivateKeys();
      return privateKeys != null && index >= 0 ? privateKeys[index] : null;
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.handleElementAddedOrRemoved(collection, element);
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.handleElementAddedOrRemoved(collection, element);
   }

   private void handleElementAddedOrRemoved(Collection collection, Object element) {
      Certificate[] certificates = this._certificateAttachmentModel.getCertificates();
      if (certificates != null) {
         Certificate elementCertificate = ((KeyStoreData)element).getCertificate();
         if (elementCertificate != null) {
            int numCerts = certificates.length;

            for (int i = 0; i < numCerts; i++) {
               if (elementCertificate.equals(certificates[i])) {
                  synchronized (this._displayApp.getAppEventLock()) {
                     this._listField.invalidate(i);
                  }
               }
            }
         }
      }
   }

   @Override
   public void reset(Collection collection) {
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }
}
