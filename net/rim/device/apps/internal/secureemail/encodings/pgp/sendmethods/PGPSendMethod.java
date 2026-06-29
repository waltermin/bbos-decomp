package net.rim.device.apps.internal.secureemail.encodings.pgp.sendmethods;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.secureemail.CertificateHarvester;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPFactory;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPKeyStoreLDAPCertificateHarvester;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPOptions;
import net.rim.device.apps.internal.secureemail.sendmethods.MessageEncoder;
import net.rim.device.apps.internal.secureemail.sendmethods.SecureEmailMessageEncoderProvider;

public final class PGPSendMethod extends SecureEmailMessageEncoderProvider {
   public PGPSendMethod(ServiceRecord serviceRecord, int encodingAction, Object context) {
      super(serviceRecord, encodingAction, PGPFactory.getInstance(), context);
   }

   @Override
   public final MessageEncoder getMessageEncoder(
      EmailMessageModel emailMessageModel, RecipientData[] messageRecipientData, Certificate[] additionalCertificates, Object context
   ) {
      return new PGPMessageEncoder(emailMessageModel, messageRecipientData, additionalCertificates, this.getServiceRecord(), this.getEncodingAction(), context);
   }

   @Override
   protected final CertificateHarvester createCertificateHarvester(ServiceRecord serviceRecord, int encodingAction, Object context) {
      return (encodingAction & 2) == 0 ? null : new PGPKeyStoreLDAPCertificateHarvester(super._secureEmailFactory, serviceRecord == null);
   }

   @Override
   public final boolean requiresCertificates(EmailMessageModel emailMessageModel) {
      if (!super.requiresCertificates(emailMessageModel)) {
         return false;
      }

      Recognizer optionsRecognizer = this.getSecureEmailFactory().getOptionsRecognizer();
      PGPOptions pgpOptions = (PGPOptions)SubmemberUtilities.getFirstSubmember(emailMessageModel, optionsRecognizer);
      return pgpOptions == null || !pgpOptions.getUseConventionalEncryption();
   }

   @Override
   public final Certificate obtainSuitableRecipientCertificate(Certificate certificate) {
      return certificate instanceof Object ? certificate : null;
   }
}
