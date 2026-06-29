package net.rim.device.apps.internal.secureemail.encodings.smime.sendmethods;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.secureemail.CertificateHarvester;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;
import net.rim.device.apps.internal.secureemail.encodings.smime.EMSKeyStoreLDAPCertificateHarvester;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMEFactory;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMEUtilities;
import net.rim.device.apps.internal.secureemail.sendmethods.MessageEncoder;
import net.rim.device.apps.internal.secureemail.sendmethods.SecureEmailMessageEncoderProvider;

public final class SMIMESendMethod extends SecureEmailMessageEncoderProvider {
   public SMIMESendMethod(ServiceRecord serviceRecord, int encodingAction, Object context) {
      super(serviceRecord, encodingAction, SMIMEFactory.getInstance(), context);
   }

   @Override
   public final MessageEncoder getMessageEncoder(
      EmailMessageModel emailMessageModel, RecipientData[] messageRecipientData, Certificate[] additionalCertificates, Object context
   ) {
      return new SMIMEMessageEncoder(
         emailMessageModel, messageRecipientData, additionalCertificates, this.getServiceRecord(), this.getEncodingAction(), context
      );
   }

   @Override
   protected final CertificateHarvester createCertificateHarvester(ServiceRecord serviceRecord, int encodingAction, Object context) {
      String emsAddress = null;
      if (serviceRecord != null) {
         emsAddress = ((SMIMEUtilities)super._secureEmailUtilities).getEMSEmailAddress(serviceRecord.getUid());
      }

      return emsAddress != null && (encodingAction & 2) != 0
         ? new EMSKeyStoreLDAPCertificateHarvester(emsAddress, super._secureEmailFactory, serviceRecord == null)
         : super.createCertificateHarvester(serviceRecord, encodingAction, context);
   }

   @Override
   public final Certificate obtainSuitableRecipientCertificate(Certificate certificate) {
      if (certificate instanceof X509Certificate) {
         return certificate;
      }

      if (certificate instanceof PGPCertificate) {
         PGPCertificate pgpCertificate = (PGPCertificate)certificate;
         if (pgpCertificate.containsEmbeddedX509Certificates()) {
            Certificate[] x509Certs = pgpCertificate.getEmbeddedX509Certificates();
            boolean canEncrypt = false;

            for (int i = 0; i < x509Certs.length; i++) {
               if (!SecureEmailUtilities.isCertificateSupported(x509Certs[i], 2)) {
                  return null;
               }

               canEncrypt = x509Certs[i].queryKeyUsage(4) != 0 || x509Certs[i].queryKeyUsage(16) != 0;
               if (canEncrypt) {
                  return x509Certs[i];
               }
            }
         }
      }

      return null;
   }
}
