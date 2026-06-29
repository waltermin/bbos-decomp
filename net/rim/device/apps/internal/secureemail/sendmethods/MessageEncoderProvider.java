package net.rim.device.apps.internal.secureemail.sendmethods;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;

public interface MessageEncoderProvider extends SendMethod {
   MessageEncoder getMessageEncoder(EmailMessageModel var1, RecipientData[] var2, Certificate[] var3, Object var4);

   boolean requiresCertificates(EmailMessageModel var1);

   Certificate obtainSenderEncryptionCertificate(EmailMessageModel var1);

   Certificate obtainSuitableRecipientCertificate(Certificate var1);

   SecureEmailFactory getSecureEmailFactory();
}
