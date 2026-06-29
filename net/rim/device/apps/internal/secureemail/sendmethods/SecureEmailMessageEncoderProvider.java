package net.rim.device.apps.internal.secureemail.sendmethods;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;
import net.rim.device.apps.internal.secureemail.CertificateHarvester;
import net.rim.device.apps.internal.secureemail.CertificateHarvesterManager;
import net.rim.device.apps.internal.secureemail.KeyStoreLDAPCertificateHarvester;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.device.apps.internal.secureemail.SecureEmailOptions;
import net.rim.device.apps.internal.secureemail.SecureEmailResources;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;
import net.rim.vm.Array;

public class SecureEmailMessageEncoderProvider extends SecureEmailAwareSendMethod implements MessageEncoderProvider, VerbProvider {
   protected SecureEmailFactory _secureEmailFactory;
   protected SecureEmailUtilities _secureEmailUtilities;

   public SecureEmailMessageEncoderProvider(ServiceRecord serviceRecord, int encodingAction, SecureEmailFactory secureEmailFactory, Object context) {
      super(serviceRecord, secureEmailFactory.getEncodingUID(), encodingAction, context);
      this.setFlags(18);
      this._secureEmailFactory = secureEmailFactory;
      this._secureEmailUtilities = secureEmailFactory.getUtilities();
      CertificateHarvester certificateHarvester = (CertificateHarvester)this.createCertificateHarvester(serviceRecord, encodingAction, context);
      if (certificateHarvester != null && CertificateHarvesterManager.addCertificateHarvester(certificateHarvester, context) && serviceRecord != null) {
         String alwaysBCCEmailAddress = this._secureEmailUtilities.getAlwaysBCCEmailAddress(serviceRecord.getUid());
         if (alwaysBCCEmailAddress != null && alwaysBCCEmailAddress.length() > 0) {
            RecipientData recipientData = new RecipientData(null, 1, new Object[]{alwaysBCCEmailAddress}, null);
            certificateHarvester.recipientAdded(recipientData);
         }
      }
   }

   protected CertificateHarvester createCertificateHarvester(ServiceRecord serviceRecord, int encodingAction, Object context) {
      return (encodingAction & 2) == 0 ? null : new KeyStoreLDAPCertificateHarvester(this._secureEmailFactory, serviceRecord == null);
   }

   @Override
   public boolean isConfigured(RIMModel model, Object context) {
      EmailMessageModel emailMessageModel = (EmailMessageModel)model;
      SecureEmailOptions perMessageOptions = this.findPerMessageOptions(emailMessageModel);
      if (perMessageOptions != null) {
         if (perMessageOptions.isConfigured(this.getEncodingAction(), this.getServiceRecord())) {
            return true;
         }

         emailMessageModel.remove(perMessageOptions);
      }

      this._secureEmailFactory.autoConfigureGlobalOptions();
      return this._secureEmailFactory.areGlobalOptionsConfigured(this.getEncodingAction(), this.getServiceRecord());
   }

   @Override
   public boolean isConfigurable(RIMModel model, Object context) {
      return this._secureEmailFactory.areOptionsConfigurable(this.getEncodingAction());
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      TransitoryMessagePropertiesModel messagePropertiesModel = (TransitoryMessagePropertiesModel)ContextObject.get(context, 32241034113959076L);
      if (messagePropertiesModel == null) {
         return null;
      }

      EmailMessageModel emailMessageModel = messagePropertiesModel.getEmailMessageModel();
      SecureEmailOptions perMessageOptions = this.findPerMessageOptions(emailMessageModel);
      if (perMessageOptions != null) {
         if (perMessageOptions.isConfigured(this.getEncodingAction(), this.getServiceRecord())) {
            return null;
         }

         emailMessageModel.remove(perMessageOptions);
      }

      this._secureEmailFactory.autoConfigureGlobalOptions();
      if (!this._secureEmailFactory.areGlobalOptionsConfiguredCompletely()) {
         Array.resize(verbs, 1);
         verbs[0] = (Verb)this._secureEmailFactory.createGlobalOptionsConfigureVerb();
         if (!this._secureEmailFactory.areGlobalOptionsConfigured(this.getEncodingAction(), this.getServiceRecord())) {
            return verbs[0];
         }
      }

      return null;
   }

   @Override
   public boolean requiresCertificates(EmailMessageModel emailMessageModel) {
      return (this.getEncodingAction() & 2) != 0;
   }

   @Override
   public String toString() {
      int encodingActionIndex;
      switch (this.getEncodingAction()) {
         case 0:
            throw new Object();
         case 1:
         default:
            encodingActionIndex = 0;
            break;
         case 2:
            encodingActionIndex = 1;
            break;
         case 3:
            encodingActionIndex = 2;
      }

      String[] encodingActions = SecureEmailResources.getStringArray(4);
      String encodingAction = encodingActions[encodingActionIndex];
      return (this.getFlags() & 4) != 0
         ? MessageFormat.format(SecureEmailResources.getString(109), new Object[]{this._secureEmailFactory.getEncodingString(), encodingAction})
         : encodingAction;
   }

   @Override
   public SecureEmailFactory getSecureEmailFactory() {
      return this._secureEmailFactory;
   }

   private SecureEmailOptions findPerMessageOptions(EmailMessageModel message) {
      return message != null ? (SecureEmailOptions)SubmemberUtilities.getFirstSubmember(message, this._secureEmailFactory.getOptionsRecognizer()) : null;
   }

   @Override
   public Certificate obtainSenderEncryptionCertificate(EmailMessageModel emailMessageModel) {
      SecureEmailOptions secureEmailOptions = this.findPerMessageOptions(emailMessageModel);
      if (secureEmailOptions == null) {
         secureEmailOptions = (SecureEmailOptions)this._secureEmailFactory.createGlobalOptionsCopy();
      }

      KeyStoreData myEncryptionData = secureEmailOptions.getEncryptionKeyStoreData();
      return myEncryptionData != null ? myEncryptionData.getCertificate() : null;
   }

   @Override
   public Certificate obtainSuitableRecipientCertificate(Certificate _1) {
      throw null;
   }

   @Override
   public MessageEncoder getMessageEncoder(EmailMessageModel _1, RecipientData[] _2, Certificate[] _3, Object _4) {
      throw null;
   }
}
