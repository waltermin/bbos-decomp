package net.rim.device.apps.internal.secureemail;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateKeyStoreIndex;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.verb.DisplayCertificateVerb;
import net.rim.device.apps.internal.api.crypto.verb.ImportCertificatesVerb;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.component.HorizontalSpacerField;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class IncludedCertificatesField extends CursorProviderHorizontalFieldManager implements CollectionListener {
   protected Certificate[] _certificates;
   protected PrivateKey[] _privateKeys;
   protected int[] _filteredIndices;
   protected int _numFilteredCertificatesOnDevice;
   protected String[] _certificateNames;
   protected boolean[] _certificatesOnDevice;
   protected VerbMenuItem[] _importCertificateMenuItems;
   protected VerbMenuItem _importAllCertificatesMenuItem;
   protected VerbMenuItem _displayCertificateMenuItem;
   protected SecureEmailFactory _secureEmailFactory;
   protected IncludedCertificatesField$IncludedCertificateListField _certificateList;
   protected Application _displayApp;

   public IncludedCertificatesField(Certificate[] certificates, PrivateKey[] privateKeys, SecureEmailFactory secureEmailFactory) {
      super(1152921504606846976L);
      this._certificates = certificates;
      this._privateKeys = privateKeys;
      this._secureEmailFactory = secureEmailFactory;
      this._displayApp = Application.getApplication();
      KeyStore keyStore = this._secureEmailFactory.getPreferredKeyStore();
      keyStore.addCollectionListener(new WeakReference(this));
      keyStore.addIndex(new CertificateKeyStoreIndex());
      int numCertificates = this._certificates.length;
      int numFilteredCertificates = 0;
      this._numFilteredCertificatesOnDevice = 0;
      this._filteredIndices = new int[numCertificates];
      this._certificateNames = new String[numCertificates];
      this._certificatesOnDevice = new boolean[numCertificates];

      for (int i = 0; i < numCertificates; i++) {
         Certificate currentCert = this._certificates[i];
         if (!currentCert.isCA() && !currentCert.isRoot()) {
            this.addFilteredCert(i, numFilteredCertificates++);
         }
      }

      if (numFilteredCertificates == 0) {
         for (int i = 0; i < numCertificates; i++) {
            this.addFilteredCert(i, numFilteredCertificates++);
         }
      }

      if (numFilteredCertificates < numCertificates) {
         Array.resize(this._filteredIndices, numFilteredCertificates);
      }

      this._importCertificateMenuItems = new VerbMenuItem[numCertificates];
      String[] containerStringUpperPluralArray = new String[]{this._secureEmailFactory.getPublicKeyContainerString(true, true)};
      String importAllCertificatesVerbDescription = MessageFormat.format(SecureEmailResources.getString(46), containerStringUpperPluralArray);
      this._importAllCertificatesMenuItem = new VerbMenuItem(
         new ImportCertificatesVerb(importAllCertificatesVerbDescription, this._certificates, this._privateKeys, keyStore), Integer.MAX_VALUE
      );
      String[] containerStringUpperSingularArray = new String[]{this._secureEmailFactory.getPublicKeyContainerString(true, false)};
      String displayCertificateVerbDescription = MessageFormat.format(CryptoCommonResources.getString(20), containerStringUpperSingularArray);
      this._displayCertificateMenuItem = new VerbMenuItem(new DisplayCertificateVerb(displayCertificateVerbDescription), 10);
      this._certificateList = new IncludedCertificatesField$IncludedCertificateListField(this, numFilteredCertificates);
      this.add(CryptoIcons.getLargeImageField(1));
      this.add(new HorizontalSpacerField(3));
      String includedCertificatesString = MessageFormat.format(
         SecureEmailResources.getString(47), numFilteredCertificates > 1 ? containerStringUpperPluralArray : containerStringUpperSingularArray
      );
      VerticalFieldManager vfm = new VerticalFieldManager();
      vfm.add(new LabelField(includedCertificatesString));
      vfm.add(this._certificateList);
      this.add(vfm);
   }

   private void addFilteredCert(int index, int numFilteredCertificates) {
      KeyStore keyStore = this._secureEmailFactory.getPreferredKeyStore();
      Certificate certificate = this._certificates[index];
      this._filteredIndices[numFilteredCertificates] = index;
      boolean onDevice = false;
      boolean privateKeyIncluded = this._privateKeys[index] != null;
      KeyStoreData keyStoreData = null;
      keyStore.addIndex(new CertificateKeyStoreIndex());
      Enumeration keyStoreElements = keyStore.elements(-2038609988711824737L, certificate);

      while (keyStoreElements.hasMoreElements()) {
         keyStoreData = (KeyStoreData)keyStoreElements.nextElement();
         if (keyStoreData.isPrivateKeySet() || !privateKeyIncluded) {
            onDevice = true;
            break;
         }
      }

      this._certificatesOnDevice[index] = onDevice;
      if (onDevice) {
         this._certificateNames[index] = keyStoreData.getLabel();
         this._numFilteredCertificatesOnDevice++;
      } else {
         this._certificateNames[index] = certificate.getSubjectFriendlyName();
      }
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      KeyStoreData data = (KeyStoreData)element;
      Certificate c = data.getCertificate();
      if (c != null) {
         int numFilteredIndices = this._filteredIndices.length;

         for (int i = 0; i < numFilteredIndices; i++) {
            int index = this._filteredIndices[i];
            if (c.equals(this._certificates[index])) {
               this._certificatesOnDevice[index] = true;
               this._certificateNames[index] = data.getLabel();
               this._numFilteredCertificatesOnDevice++;
               synchronized (this._displayApp.getAppEventLock()) {
                  this._certificateList.invalidate(i);
               }
            }
         }
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      KeyStoreData data = (KeyStoreData)element;
      Certificate c = data.getCertificate();
      if (c != null) {
         int numFilteredIndices = this._filteredIndices.length;

         for (int i = 0; i < numFilteredIndices; i++) {
            int index = this._filteredIndices[i];
            if (c.equals(this._certificates[index])) {
               this._certificatesOnDevice[index] = false;
               this._certificateNames[index] = data.getLabel();
               this._numFilteredCertificatesOnDevice--;
               synchronized (this._displayApp.getAppEventLock()) {
                  this._certificateList.invalidate(i);
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

   @Override
   public boolean keyChar(char key, int status, int time) {
      if (!super.keyChar(key, status, time)) {
         if (key == '\n') {
            KeyStore keyStore = this._secureEmailFactory.getPreferredKeyStore();
            Certificate certToImport = this._certificates[this._filteredIndices[this._certificateList.getSelectedIndex()]];
            DisplayCertificateVerb dcv = (DisplayCertificateVerb)this._displayCertificateMenuItem.getVerb();
            dcv.setCertificate(certToImport, null, keyStore, this._secureEmailFactory.getCryptoSystemProperties(), null);
            dcv.invoke(null);
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }
}
