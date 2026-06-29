package net.rim.device.apps.internal.secureemail;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChoiceField;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.api.crypto.verb.DisplayCertificateVerb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;
import net.rim.vm.Array;

public class SecureEmailOptionsModel implements RIMModel, VerbProvider, FieldProvider, CollectionListener, EncodingSupporter {
   protected SecureEmailFactory _factory;
   protected SecureEmailUtilities _utilities;
   protected Object _applicationEventLock;
   protected VerticalIndentFieldManager _vfm;
   protected EmailMessageModel _message;
   protected SecureEmailOptions _secureEmailOptions;
   protected Recognizer _recognizer;
   protected BooleanChoiceField _showMessageDetailsField;
   protected ContentCipherField _contentCipherField;
   protected BooleanChoiceField _promptProblemPersonalCertsField;
   protected BooleanChoiceField _promptTruncatedMessageField;
   protected DisplayCertificateVerb _displayCertificateVerb;
   private boolean _initialKeyStoreDataWereAutoSelected;

   protected void populateCertField(
      CertificateChoiceField certField,
      int keyUsage,
      KeyStoreData[] keyStoreDataArray,
      KeyStoreData initialSelection,
      KeyStore keyStore,
      boolean initialScreenCreation
   ) {
      int initialCertIndex = -1;
      int defaultCertIndex = -1;
      int numData = 0;
      String[] dataLabels = new String[0];
      Certificate[] certificates = new Certificate[0];
      Array.resize(keyStoreDataArray, 0);
      Certificate initialCert = null;
      if (initialSelection != null) {
         initialCert = initialSelection.getCertificate();
      }

      keyStore.addIndex(new KeyUsagePrivateKeysKeyStoreIndex());
      Enumeration enumeration = keyStore.elements(-2733667523168089402L, new Integer(keyUsage));

      while (enumeration.hasMoreElements()) {
         KeyStoreData data = (KeyStoreData)enumeration.nextElement();
         if (this._utilities.isCertificateAllowed(data, 4)) {
            Array.resize(keyStoreDataArray, numData + 1);
            Array.resize(dataLabels, numData + 1);
            Array.resize(certificates, numData + 1);
            keyStoreDataArray[numData] = data;
            dataLabels[numData] = data.getLabel();
            certificates[numData] = data.getCertificate();
            if (initialCertIndex < 0 && defaultCertIndex < 0) {
               if (initialCert != null) {
                  if (initialCert.equals(certificates[numData])) {
                     initialCertIndex = numData;
                  }
               } else if (SecureEmailUtilities.getEmailAddresses(certificates[numData]) != null) {
                  defaultCertIndex = numData;
               }
            }

            numData++;
         }
      }

      synchronized (this._applicationEventLock) {
         boolean dirtyFlag = certField.isDirty();
         if (numData > 0) {
            if (this._message != null && keyUsage == 1) {
               Array.resize(keyStoreDataArray, numData + 1);
               Array.resize(dataLabels, numData + 1);
               Array.resize(certificates, numData + 1);
               keyStoreDataArray[numData] = null;
               dataLabels[numData] = SecureEmailResources.getString(118);
               certificates[numData] = null;
               if (initialCert == null) {
                  initialCertIndex = numData;
               }
            }

            if (initialCertIndex < 0) {
               initialCertIndex = defaultCertIndex < 0 ? 0 : defaultCertIndex;
               if (initialScreenCreation) {
                  this._initialKeyStoreDataWereAutoSelected = true;
               } else {
                  dirtyFlag = true;
               }
            }
         }

         certField.setCertificates(certificates, dataLabels, initialCertIndex);
         certField.setDirty(dirtyFlag);
      }
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      Array.resize(verbs, 0);
      Field fieldWithFocus = this._vfm.getLeafFieldWithFocus();
      if (fieldWithFocus instanceof CertificateChoiceField) {
         CertificateChoiceField certificateChoiceField = (CertificateChoiceField)fieldWithFocus;
         Certificate selectedCertificate = certificateChoiceField.getSelectedCertificate();
         if (selectedCertificate != null) {
            if (this._displayCertificateVerb == null) {
               String[] containerStringUpperSingularArray = new String[]{this._factory.getPublicKeyContainerString(true, false)};
               String displayCertificateVerbDescription = MessageFormat.format(SecureEmailResources.getString(164), containerStringUpperSingularArray);
               this._displayCertificateVerb = new DisplayCertificateVerb(displayCertificateVerbDescription);
            }

            this._displayCertificateVerb
               .setCertificate(selectedCertificate, null, this._factory.getPreferredKeyStore(), this._factory.getCryptoSystemProperties(), null);
            Arrays.add(verbs, this._displayCertificateVerb);
            defaultVerb = this._displayCertificateVerb;
         }
      }

      return defaultVerb;
   }

   protected String getCMIMEServiceName() {
      String besUIDs = ITPolicy.getString(24, 51);
      if (besUIDs != null) {
         StringTokenizer tokenizer = new StringTokenizer(besUIDs, ',');
         String uid = null;

         while (tokenizer.hasMoreElements()) {
            uid = (String)tokenizer.nextElement();
            ServiceRecord serviceRecord = ServiceBook.getSB().getRecordByUidAndCid(uid, "CMIME");
            if (serviceRecord != null) {
               return serviceRecord.getName();
            }
         }

         return uid;
      } else {
         return null;
      }
   }

   protected void addGlobalOptionsFields() {
      this._showMessageDetailsField = new BooleanChoiceField(
         SecureEmailResources.getString(9), SecureEmailResources.getStringArray(7), this._secureEmailOptions.getShowMessageDetails()
      );
      this._vfm.add(this._showMessageDetailsField);
      String alwaysBCCAddress = this._utilities.getAlwaysBCCEmailAddress(null);
      if (alwaysBCCAddress != null && alwaysBCCAddress.length() > 0) {
         String cmimeServiceName = this.getCMIMEServiceName();
         String alwaysBCCLabel;
         if (cmimeServiceName != null) {
            alwaysBCCLabel = MessageFormat.format(SecureEmailResources.getString(152), new String[]{cmimeServiceName});
         } else {
            alwaysBCCLabel = SecureEmailResources.getString(0);
         }

         ObjectChoiceField alwaysBCCAddressField = new ObjectChoiceField(alwaysBCCLabel, new String[]{alwaysBCCAddress});
         alwaysBCCAddressField.setEditable(false);
         this._vfm.add(alwaysBCCAddressField);
      }

      String promptProblemMessage = MessageFormat.format(
         SecureEmailResources.getString(172), new Object[]{this._factory.getPublicKeyContainerString(false, true)}
      );
      this._promptProblemPersonalCertsField = new BooleanChoiceField(promptProblemMessage, 0, this._secureEmailOptions.getPromptProblemPersonalCerts());
      this._vfm.add(this._promptProblemPersonalCertsField);
      String promptTruncatedMessage = SecureEmailResources.getString(180);
      this._promptTruncatedMessageField = new BooleanChoiceField(promptTruncatedMessage, 0, this._secureEmailOptions.getPromptTruncatedMessage());
      this._vfm.add(this._promptTruncatedMessageField);
   }

   protected void addGlobalAndPerMessageOptionsFields(SecureEmailOptions _1) {
      throw null;
   }

   protected void initializePerMessageOptions(SecureEmailOptions _1) {
      throw null;
   }

   protected void handleKeyStoreUpdate(KeyStoreData _1) {
      throw null;
   }

   @Override
   public boolean validate(Field field, Object context) {
      if (this._contentCipherField.getCheckedCiphers() == 0) {
         SimpleChoiceDialog dialog = new SimpleChoiceDialog(SecureEmailResources.getString(40), CommonResource.getStringArray(10004), 0, null);
         dialog.setModal(true);
         dialog.show();
         return false;
      } else {
         return true;
      }
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      this._secureEmailOptions.setContentCipherBitfield(this._contentCipherField.getCheckedCiphers());
      if (this._message == null) {
         this._secureEmailOptions.setShowMessageDetails(this._showMessageDetailsField.isAffirmative());
         this._secureEmailOptions.setPromptProblemPersonalCerts(this._promptProblemPersonalCertsField.isAffirmative());
         this._secureEmailOptions.setPromptTruncatedMessage(this._promptTruncatedMessageField.isAffirmative());
         this._factory.saveGlobalOptions(this._secureEmailOptions);
         return true;
      }

      if (!this._message.contains(this._secureEmailOptions)) {
         this._message.add(this._secureEmailOptions);
      }

      return true;
   }

   @Override
   public int getOrder(Object context) {
      return 5;
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.handleKeyStoreUpdate((KeyStoreData)element);
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.handleKeyStoreUpdate((KeyStoreData)element);
   }

   @Override
   public boolean isEncodingSupported(long encodingUID) {
      return encodingUID == this._factory.getEncodingUID();
   }

   @Override
   public void reset(Collection collection) {
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public final Field getField(Object context) {
      this._applicationEventLock = Application.getEventLock();
      this._vfm = new VerticalIndentFieldManager();
      this._vfm.setCookie(this);
      SecureEmailOptions secureEmailGlobalOptions = this._factory.createGlobalOptionsCopy();
      if (this._message == null) {
         this._secureEmailOptions = secureEmailGlobalOptions;
      } else {
         this._secureEmailOptions = (SecureEmailOptions)SubmemberUtilities.getFirstSubmember(this._message, this._recognizer);
         if (this._secureEmailOptions == null) {
            this._secureEmailOptions = secureEmailGlobalOptions;
            this.initializePerMessageOptions(this._secureEmailOptions);
         }
      }

      this.addGlobalAndPerMessageOptionsFields(this._secureEmailOptions);
      if (this._message == null) {
         this._vfm.add(new SeparatorField());
         this.addGlobalOptionsFields();
      }

      if (this._initialKeyStoreDataWereAutoSelected) {
         this.grabDataFromField(null, null);
      }

      return this._vfm;
   }

   public SecureEmailOptionsModel(SecureEmailFactory factory, Object context) {
      this._factory = factory;
      this._utilities = this._factory.getUtilities();
      this._recognizer = this._factory.getOptionsRecognizer();
      this._message = (EmailMessageModel)ContextObject.get(context, 254);
   }
}
