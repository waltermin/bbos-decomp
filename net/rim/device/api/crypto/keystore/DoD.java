package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.vm.PersistentInteger;

final class DoD {
   private static final long ID;
   private static final long CERTSTATUSTIME;

   private DoD() {
   }

   public static final void main(String[] args) {
      int id = PersistentInteger.getId(8206960665880315445L, 0);
      int downloadTime = PersistentInteger.get(id);
      int currentDownloadTime = (int)CodeModuleManager.getModuleDownloadTimestamp(ApplicationDescriptor.currentApplicationDescriptor().getModuleHandle());
      if (downloadTime != currentDownloadTime) {
         CertificateStatus status = (CertificateStatus)(new Object(0, 1007118671960L, 1007118671960L, 0, 0, -1));
         String x509String = "X509";
         TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
         DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
         KeyStoreUtilitiesInternal.addRootCertificate(
            trusted, device, "DoD PKI Med Root CA", DoD$DOD_ROOT_CERTIFICATES_1.DOD_PKI_MED_ROOT_CA, x509String, status
         );
         KeyStoreUtilitiesInternal.addRootCertificate(
            trusted, device, "DoD CLASS 3 Root CA", DoD$DOD_ROOT_CERTIFICATES_1.DOD_CLASS_3_ROOT_CA, x509String, status
         );
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "ORC IECA", DoD$DOD_ROOT_CERTIFICATES_1.ORC_IECA, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "ORC IECA2", DoD$DOD_ROOT_CERTIFICATES_1.ORC_IECA2, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "DST IECA-2", DoD$DOD_ROOT_CERTIFICATES_1.DST_IECA_2, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "VeriSign IECA", DoD$DOD_ROOT_CERTIFICATES_1.VeriSign_IECA1, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "VeriSign IECA", DoD$DOD_ROOT_CERTIFICATES_1.VeriSign_IECA2, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "ECA ROOT CA1", DoD$DOD_ROOT_CERTIFICATES_2.ECA_ROOT_CA1, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "ECA ROOT CA2", DoD$DOD_ROOT_CERTIFICATES_2.ECA_ROOT_CA2, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "ORC ECA1", DoD$DOD_ROOT_CERTIFICATES_2.ORC_ECA1, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "ORC ECA2", DoD$DOD_ROOT_CERTIFICATES_2.ORC_ECA2, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "Verisign CECA", DoD$DOD_ROOT_CERTIFICATES_2.Verisign_CECA, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "Verisign CECA2", DoD$DOD_ROOT_CERTIFICATES_2.Verisign_CECA2, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "DOD OCSP SS", DoD$DOD_ROOT_CERTIFICATES_2.DOD_OCSP_SS, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(trusted, device, "DoD ROOT CA 2", DoD$DOD_ROOT_CERTIFICATES_2.DoD_ROOT_CA_2, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CLASS 3 CA-3", DoD$DOD_CA_CERTIFICATES.DOD_CLASS_3_CA_3, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CLASS 3 CA-4", DoD$DOD_CA_CERTIFICATES.DOD_CLASS_3_CA_4, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CLASS 3 CA-5", DoD$DOD_CA_CERTIFICATES.DOD_CLASS_3_CA_5, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CLASS 3 CA-6", DoD$DOD_CA_CERTIFICATES.DOD_CLASS_3_CA_6, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CLASS 3 CA-7", DoD$DOD_CA_CERTIFICATES.DOD_CLASS_3_CA_7, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CLASS 3 CA-8", DoD$DOD_CA_CERTIFICATES.DOD_CLASS_3_CA_8, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CLASS 3 CA-9", DoD$DOD_CA_CERTIFICATES.DOD_CLASS_3_CA_9, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CLASS 3 CA-10", DoD$DOD_CA_CERTIFICATES.DOD_CLASS_3_CA_10, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CA-11", DoD$DOD_CA_CERTIFICATES_2.DOD_CA_11, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CA-12", DoD$DOD_CA_CERTIFICATES_2.DOD_CA_12, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CA-13", DoD$DOD_CA_CERTIFICATES_2.DOD_CA_13, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CA-14", DoD$DOD_CA_CERTIFICATES_2.DOD_CA_14, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CA-15", DoD$DOD_CA_CERTIFICATES_2.DOD_CA_15, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CA-16", DoD$DOD_CA_CERTIFICATES_2.DOD_CA_16, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CA-17", DoD$DOD_CA_CERTIFICATES_2.DOD_CA_17, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CA-18", DoD$DOD_CA_CERTIFICATES_2.DOD_CA_18, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "Med CA-1", DoD$MED_CA_AND_EMAIL_CERTS.Med_CA_1, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "Med CA-2", DoD$MED_CA_AND_EMAIL_CERTS.Med_CA_2, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "Med EMAIL CA-1", DoD$MED_CA_AND_EMAIL_CERTS.Med_EMAIL_CA_1, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "Med EMAIL CA-2", DoD$MED_CA_AND_EMAIL_CERTS.Med_EMAIL_CA_2, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(
            null, device, "DOD CLASS 3 EMAIL CA-3", DoD$DOD_EMAIL_CA_CERTIFICATES.DOD_CLASS_3_EMAIL_CA_3, x509String, status
         );
         KeyStoreUtilitiesInternal.addRootCertificate(
            null, device, "DOD CLASS 3 EMAIL CA-4", DoD$DOD_EMAIL_CA_CERTIFICATES.DOD_CLASS_3_EMAIL_CA_4, x509String, status
         );
         KeyStoreUtilitiesInternal.addRootCertificate(
            null, device, "DOD CLASS 3 EMAIL CA-5", DoD$DOD_EMAIL_CA_CERTIFICATES.DOD_CLASS_3_EMAIL_CA_5, x509String, status
         );
         KeyStoreUtilitiesInternal.addRootCertificate(
            null, device, "DOD CLASS 3 EMAIL CA-6", DoD$DOD_EMAIL_CA_CERTIFICATES.DOD_CLASS_3_EMAIL_CA_6, x509String, status
         );
         KeyStoreUtilitiesInternal.addRootCertificate(
            null, device, "DOD CLASS 3 EMAIL CA-7", DoD$DOD_EMAIL_CA_CERTIFICATES.DOD_CLASS_3_EMAIL_CA_7, x509String, status
         );
         KeyStoreUtilitiesInternal.addRootCertificate(
            null, device, "DOD CLASS 3 EMAIL CA-8", DoD$DOD_EMAIL_CA_CERTIFICATES.DOD_CLASS_3_EMAIL_CA_8, x509String, status
         );
         KeyStoreUtilitiesInternal.addRootCertificate(
            null, device, "DOD CLASS 3 EMAIL CA-9", DoD$DOD_EMAIL_CA_CERTIFICATES.DOD_CLASS_3_EMAIL_CA_9, x509String, status
         );
         KeyStoreUtilitiesInternal.addRootCertificate(
            null, device, "DOD CLASS 3 EMAIL CA-10", DoD$DOD_EMAIL_CA_CERTIFICATES.DOD_CLASS_3_EMAIL_CA_10, x509String, status
         );
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD EMAIL CA-11", DoD$DOD_EMAIL_CA_CERTIFICATES_2.DOD_EMAIL_CA_11, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD EMAIL CA-12", DoD$DOD_EMAIL_CA_CERTIFICATES_2.DOD_EMAIL_CA_12, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD EMAIL CA-13", DoD$DOD_EMAIL_CA_CERTIFICATES_2.DOD_EMAIL_CA_13, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD EMAIL CA-14", DoD$DOD_EMAIL_CA_CERTIFICATES_2.DOD_EMAIL_CA_14, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD EMAIL CA-15", DoD$DOD_EMAIL_CA_CERTIFICATES_2.DOD_EMAIL_CA_15, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD EMAIL CA-16", DoD$DOD_EMAIL_CA_CERTIFICATES_2.DOD_EMAIL_CA_16, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD EMAIL CA-17", DoD$DOD_EMAIL_CA_CERTIFICATES_2.DOD_EMAIL_CA_17, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD EMAIL CA-18", DoD$DOD_EMAIL_CA_CERTIFICATES_2.DOD_EMAIL_CA_18, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(null, device, "DOD CLASS 3 CAC CA", DoD$DOD_CAC_CA_CERTIFICATES.DOD_CLASS_3_CAC_CA, x509String, status);
         KeyStoreUtilitiesInternal.addRootCertificate(
            null, device, "DOD CLASS 3 CAC EMAIL CA", DoD$DOD_CAC_CA_CERTIFICATES.DOD_CLASS_3_CAC_EMAIL_CA, x509String, status
         );
         PersistentInteger.set(id, currentDownloadTime);
      }
   }
}
