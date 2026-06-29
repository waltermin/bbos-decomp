package net.rim.device.apps.internal.secureemail.sendmethods;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;

public class SecureEmailPlainTextSendMethod extends SecureEmailAwareSendMethod implements MessageEncoderProvider {
   public SecureEmailPlainTextSendMethod(ServiceRecord serviceRecord, Object context) {
      super(serviceRecord, 182808770805039415L, 0, context);
   }

   @Override
   public MessageEncoder getMessageEncoder(
      EmailMessageModel message, RecipientData[] messageRecipientData, Certificate[] additionalCertificates, Object context
   ) {
      return new PlainTextMessageEncoder(message, this.getServiceRecord(), context);
   }

   @Override
   public boolean isConfigurable(RIMModel model, Object context) {
      return true;
   }

   @Override
   public boolean isConfigured(RIMModel model, Object context) {
      return true;
   }

   @Override
   public String toString() {
      return EmailResources.getString(146);
   }

   @Override
   public boolean requiresCertificates(EmailMessageModel emailMessageModel) {
      return false;
   }

   @Override
   public Certificate obtainSuitableRecipientCertificate(Certificate certificate) {
      return null;
   }

   @Override
   public Certificate obtainSenderEncryptionCertificate(EmailMessageModel emailMessageModel) {
      return null;
   }

   @Override
   public SecureEmailFactory getSecureEmailFactory() {
      return null;
   }
}
