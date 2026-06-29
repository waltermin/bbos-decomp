package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.crypto.certificate.CertificateChoiceField;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.internal.keystore.browser.LaunchKeyStoreBrowserVerb;
import net.rim.device.apps.internal.secureemail.ContentCipherField;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.device.apps.internal.secureemail.SecureEmailOptions;
import net.rim.device.apps.internal.secureemail.SecureEmailOptionsModel;
import net.rim.device.apps.internal.secureemail.SecureEmailResources;
import net.rim.device.apps.internal.secureemail.encodings.pgp.server.PGPUniversalServer;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

final class PGPOptionsModel extends SecureEmailOptionsModel {
   private PGPOptions _pgpOptions;
   private CertificateChoiceField _defaultKeyPairField;
   private KeyStoreData[] _defaultKeyStoreDataArray;
   private BooleanChoiceField _conventionalEncryptionField;
   private KeyStore _pgpKeyStore = PGPKeyStore.getInstance();
   private KeyStore _trustedKeyStore;

   PGPOptionsModel(SecureEmailFactory factory, Object context) {
      super(factory, context);
      this._pgpKeyStore.addCollectionListener(new WeakReference(this));
      this._trustedKeyStore = TrustedKeyStore.getInstance();
      this._trustedKeyStore.addCollectionListener(new WeakReference(this));
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = super.getVerbs(context, verbs);
      String universalServerAddress = ((PGPUtilities)super._utilities).getUniversalServerAddress(null);
      if (universalServerAddress != null && universalServerAddress.length() > 0) {
         PGPUniversalServer universalServer = PGPUniversalServer.getRegisteredServer(universalServerAddress);
         if (universalServer != null) {
            Verb[] universalServerVerbs = new Verb[0];
            Verb universalServerDefaultVerb = universalServer.getVerbs(context, universalServerVerbs);
            int numExistingVerbs = verbs.length;
            int numUniversalServerVerbs = universalServerVerbs.length;
            Array.resize(verbs, numExistingVerbs + numUniversalServerVerbs);
            System.arraycopy(universalServerVerbs, 0, verbs, numExistingVerbs, numUniversalServerVerbs);
            if (defaultVerb == null) {
               defaultVerb = universalServerDefaultVerb;
            }
         }
      }

      Arrays.add(verbs, new LaunchKeyStoreBrowserVerb("PGP", null));
      return defaultVerb;
   }

   @Override
   protected final void initializePerMessageOptions(SecureEmailOptions secureEmailPerMessageOptions) {
   }

   @Override
   protected final void addGlobalAndPerMessageOptionsFields(SecureEmailOptions secureEmailOptions) {
      this._pgpOptions = (PGPOptions)secureEmailOptions;
      LabelField sendingOptionsLabel = new LabelField(PGPResources.getString(13));
      Font boldFont = Font.getDefault();
      boldFont = boldFont.derive(boldFont.getStyle() | 1);
      sendingOptionsLabel.setFont(boldFont);
      super._vfm.add(sendingOptionsLabel);
      this._defaultKeyStoreDataArray = new KeyStoreData[0];
      this._defaultKeyPairField = new CertificateChoiceField(
         PGPResources.getString(21), this._pgpKeyStore, this._trustedKeyStore, super._factory.getCryptoSystemProperties()
      );
      this._defaultKeyPairField.setEmptyString(SecureEmailResources.getBundle(), 12);
      super._vfm.add(this._defaultKeyPairField, 6);
      if (super._message != null) {
         this._conventionalEncryptionField = new BooleanChoiceField(PGPResources.getString(27), 0, this._pgpOptions.getUseConventionalEncryption());
         super._vfm.add(this._conventionalEncryptionField, 6);
      }

      super._contentCipherField = new ContentCipherField(super._utilities.getITPolicyContentCiphers(), super._secureEmailOptions.getContentCipherBitfield());
      super._vfm.add(new LabelField(SecureEmailResources.getString(39)), 6);
      super._vfm.add(super._contentCipherField, 12);
      this.populateCertField(this._defaultKeyPairField, 2, this._defaultKeyStoreDataArray, this._pgpOptions.getSigningKeyStoreData(), this._pgpKeyStore, true);
   }

   @Override
   protected final void addGlobalOptionsFields() {
      super.addGlobalOptionsFields();
      String universalServerAddress = ((PGPUtilities)super._utilities).getUniversalServerAddress(null);
      if (universalServerAddress != null && universalServerAddress.length() > 0) {
         String cmimeServiceName = this.getCMIMEServiceName();
         String universalServerLabel;
         if (cmimeServiceName != null) {
            universalServerLabel = MessageFormat.format(PGPResources.getString(8066), new String[]{cmimeServiceName});
         } else {
            universalServerLabel = PGPResources.getString(8067);
         }

         ObjectChoiceField universalServerAddressField = new ObjectChoiceField(universalServerLabel, new String[]{universalServerAddress});
         universalServerAddressField.setEditable(false);
         super._vfm.add(universalServerAddressField);
      }
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      KeyStoreData newDefaultKeyStoreData = this._defaultKeyStoreDataArray.length == 0
         ? null
         : this._defaultKeyStoreDataArray[this._defaultKeyPairField.getSelectedIndex()];
      this._pgpOptions.setSigningKeyStoreData(newDefaultKeyStoreData);
      this._pgpOptions.setEncryptionKeyStoreData(newDefaultKeyStoreData);
      if (this._conventionalEncryptionField != null) {
         this._pgpOptions.setUseConventionalEncryption(this._conventionalEncryptionField.isAffirmative());
      }

      return super.grabDataFromField(field, context);
   }

   @Override
   protected final void handleKeyStoreUpdate(KeyStoreData data) {
      if (super._vfm != null && data.isPrivateKeySet()) {
         Application app = Application.getApplication();
         app.invokeLater(new PGPOptionsModel$1(this));
      }
   }

   static final void access$300(PGPOptionsModel x0, CertificateChoiceField x1, int x2, KeyStoreData[] x3, KeyStoreData x4, KeyStore x5, boolean x6) {
      x0.populateCertField(x1, x2, x3, x4, x5, x6);
   }
}
