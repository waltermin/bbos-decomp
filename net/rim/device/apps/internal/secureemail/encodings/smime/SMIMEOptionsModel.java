package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChoiceField;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.internal.secureemail.ContentCipherField;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.device.apps.internal.secureemail.SecureEmailOptions;
import net.rim.device.apps.internal.secureemail.SecureEmailOptionsModel;
import net.rim.device.apps.internal.secureemail.SecureEmailResources;
import net.rim.vm.Array;

final class SMIMEOptionsModel extends SecureEmailOptionsModel implements FieldChangeListener {
   private SMIMEOptions _smimeOptions;
   private CertificateChoiceField _signingCertField;
   private KeyStoreData[] _signingKeyStoreDataArray;
   private BooleanChoiceField _includeCertificatesField;
   private BooleanChoiceField _requestSignedReceiptsField;
   private CertificateChoiceField _encryptionCertField;
   private KeyStoreData[] _encryptionKeyStoreDataArray;
   private KeyStore _deviceKeyStore = DeviceKeyStore.getInstance();
   private KeyStore _trustedKeyStore;

   SMIMEOptionsModel(SecureEmailFactory factory, Object context) {
      super(factory, context);
      this._deviceKeyStore.addCollectionListener(new Object(this));
      this._trustedKeyStore = TrustedKeyStore.getInstance();
      this._trustedKeyStore.addCollectionListener(new Object(this));
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = super.getVerbs(context, verbs);
      VerbRepository verbRepository = VerbRepository.getVerbRepository(-3067780115376710723L);
      Verb[] importVerbs = verbRepository.getVerbs(7924745286593940558L);
      if (importVerbs != null && importVerbs.length > 0) {
         int numExistingVerbs = verbs.length;
         int numImportVerbs = importVerbs.length;
         Array.resize(verbs, numExistingVerbs + numImportVerbs);
         System.arraycopy(importVerbs, 0, verbs, numExistingVerbs, numImportVerbs);
         Field fieldWithFocus = super._vfm.getLeafFieldWithFocus();
         if (fieldWithFocus instanceof Object) {
            CertificateChoiceField certificateChoiceField = (CertificateChoiceField)fieldWithFocus;
            if (certificateChoiceField.getSize() == 0) {
               defaultVerb = importVerbs[0];
            }
         }
      }

      Arrays.add(verbs, new Object("Certificate", null));
      return defaultVerb;
   }

   @Override
   protected final void initializePerMessageOptions(SecureEmailOptions secureEmailPerMessageOptions) {
      boolean isPin = super._message.flagsSet(8192);
      ((SMIMEOptions)secureEmailPerMessageOptions).setIncludeCertificatesFlag(!isPin);
   }

   @Override
   protected final void addGlobalAndPerMessageOptionsFields(SecureEmailOptions secureEmailOptions) {
      this._smimeOptions = (SMIMEOptions)secureEmailOptions;
      LabelField signingOptionsLabel = (LabelField)(new Object(SMIMEResources.getString(2013)));
      Font boldFont = Font.getDefault();
      boldFont = boldFont.derive(boldFont.getStyle() | 1);
      signingOptionsLabel.setFont(boldFont);
      super._vfm.add(signingOptionsLabel);
      this._signingKeyStoreDataArray = new Object[0];
      this._signingCertField = (CertificateChoiceField)(new Object(
         SMIMEResources.getString(2016), this._deviceKeyStore, this._trustedKeyStore, super._factory.getCryptoSystemProperties()
      ));
      this._signingCertField.setEmptyString(SecureEmailResources.getBundle(), 12);
      super._vfm.add(this._signingCertField, 6);
      if (super._message != null) {
         this._includeCertificatesField = (BooleanChoiceField)(new Object(SMIMEResources.getString(2005), 0, this._smimeOptions.getIncludeCertificatesFlag()));
         super._vfm.add(this._includeCertificatesField, 6);
      }

      this._requestSignedReceiptsField = (BooleanChoiceField)(new Object(SMIMEResources.getString(2029), 0, this._smimeOptions.getRequestSignedReceipts()));
      super._vfm.add(this._requestSignedReceiptsField, 6);
      super._vfm.add((Field)(new Object()));
      LabelField encryptionOptionsLabel = (LabelField)(new Object(SMIMEResources.getString(2014)));
      encryptionOptionsLabel.setFont(boldFont);
      super._vfm.add(encryptionOptionsLabel);
      this._encryptionKeyStoreDataArray = new Object[0];
      this._encryptionCertField = (CertificateChoiceField)(new Object(
         SMIMEResources.getString(2016), this._deviceKeyStore, this._trustedKeyStore, super._factory.getCryptoSystemProperties()
      ));
      this._encryptionCertField.setEmptyString(SecureEmailResources.getBundle(), 12);
      this._encryptionCertField.setChangeListener(this);
      super._vfm.add(this._encryptionCertField, 6);
      super._contentCipherField = (ContentCipherField)(new Object(super._utilities.getITPolicyContentCiphers(), this.getCiphersToCheck()));
      super._vfm.add((Field)(new Object(SecureEmailResources.getString(39))), 6);
      super._vfm.add(super._contentCipherField, 12);
      this.populateCertField(this._signingCertField, 0, this._signingKeyStoreDataArray, this._smimeOptions.getSigningKeyStoreData(), this._deviceKeyStore, true);
      this.populateCertField(
         this._encryptionCertField, 1, this._encryptionKeyStoreDataArray, this._smimeOptions.getEncryptionKeyStoreData(), this._deviceKeyStore, true
      );
   }

   @Override
   protected final void addGlobalOptionsFields() {
      super.addGlobalOptionsFields();
      String emsEmailAddress = ((SMIMEUtilities)super._utilities).getEMSEmailAddress(null);
      if (emsEmailAddress != null && emsEmailAddress.length() > 0) {
         String cmimeServiceName = this.getCMIMEServiceName();
         String emsEmailLabel;
         if (cmimeServiceName != null) {
            emsEmailLabel = MessageFormat.format(SMIMEResources.getString(2107), new Object[]{cmimeServiceName});
         } else {
            emsEmailLabel = SMIMEResources.getString(2102);
         }

         ObjectChoiceField emsEmailAddressField = (ObjectChoiceField)(new Object(emsEmailLabel, new Object[]{emsEmailAddress}));
         emsEmailAddressField.setEditable(false);
         super._vfm.add(emsEmailAddressField);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._encryptionCertField && super._contentCipherField != null) {
         super._contentCipherField.setCheckedCiphers(this.getCiphersToCheck());
      }
   }

   private final int getCiphersToCheck() {
      int flags = super._secureEmailOptions.getContentCipherBitfield();
      if (this._encryptionKeyStoreDataArray != null && this._encryptionKeyStoreDataArray.length > 0) {
         KeyStoreData current = this._encryptionKeyStoreDataArray[this._encryptionCertField.getSelectedIndex()];
         if (current != null) {
            Certificate certificate = current.getCertificate();
            if (certificate != null) {
               int bitField = ((SMIMEUtilities)super._utilities).getCertificateContentCiphers(certificate);
               if (bitField != 240) {
                  flags &= bitField;
               }
            }
         }
      }

      return flags;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      KeyStoreData newSigningKeyStoreData = this._signingKeyStoreDataArray.length == 0
         ? null
         : this._signingKeyStoreDataArray[this._signingCertField.getSelectedIndex()];
      this._smimeOptions.setSigningKeyStoreData(newSigningKeyStoreData);
      KeyStoreData newEncryptionKeyStoreData = this._encryptionKeyStoreDataArray.length == 0
         ? null
         : this._encryptionKeyStoreDataArray[this._encryptionCertField.getSelectedIndex()];
      this._smimeOptions.setEncryptionKeyStoreData(newEncryptionKeyStoreData);
      this._smimeOptions.setRequestSignedReceipts(this._requestSignedReceiptsField.isAffirmative());
      if (this._includeCertificatesField != null) {
         this._smimeOptions.setIncludeCertificatesFlag(this._includeCertificatesField.isAffirmative());
      }

      return super.grabDataFromField(field, context);
   }

   @Override
   protected final void handleKeyStoreUpdate(KeyStoreData data) {
      if (super._vfm != null && data.isPrivateKeySet()) {
         Application app = Application.getApplication();
         app.invokeLater(new SMIMEOptionsModel$1(this));
      }
   }

   static final void access$300(SMIMEOptionsModel x0, CertificateChoiceField x1, int x2, KeyStoreData[] x3, KeyStoreData x4, KeyStore x5, boolean x6) {
      x0.populateCertField(x1, x2, x3, x4, x5, x6);
   }

   static final void access$600(SMIMEOptionsModel x0, CertificateChoiceField x1, int x2, KeyStoreData[] x3, KeyStoreData x4, KeyStore x5, boolean x6) {
      x0.populateCertField(x1, x2, x3, x4, x5, x6);
   }
}
