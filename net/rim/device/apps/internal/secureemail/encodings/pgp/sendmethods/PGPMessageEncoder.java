package net.rim.device.apps.internal.secureemail.encodings.pgp.sendmethods;

import java.io.OutputStream;
import java.util.Vector;
import net.rim.device.api.crypto.InvalidKeyException;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.certificate.pgp.PGPSubKeyProperties;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.pgp.PGPArmorEncoder;
import net.rim.device.api.crypto.pgp.PGPCompressedOutputStream;
import net.rim.device.api.crypto.pgp.PGPEncryptedOutputStream;
import net.rim.device.api.crypto.pgp.PGPLiteralOutputStream;
import net.rim.device.api.crypto.pgp.PGPPrivateKey;
import net.rim.device.api.crypto.pgp.PGPSaltedIteratedKDFPseudoRandomSource;
import net.rim.device.api.crypto.pgp.PGPSignedOutputStream;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.secureemail.AbortSendSecureEmailException;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.RecipientData$CertificateDetails;
import net.rim.device.apps.internal.secureemail.SecureEmailOptions;
import net.rim.device.apps.internal.secureemail.SecureEmailResources;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPFactory;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPOptions;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPResources;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPUtilities;
import net.rim.device.apps.internal.secureemail.sendmethods.SecureEmailMessageEncoder;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.PasswordDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public class PGPMessageEncoder extends SecureEmailMessageEncoder {
   private static final int NUM_USER_OPTIONS = 2;
   private static final int OK = 0;
   private static final int VIEW_CERT = 1;

   public PGPMessageEncoder(
      EmailMessageModel message,
      RecipientData[] messageRecipientData,
      Certificate[] additionalCertificates,
      ServiceRecord serviceRecord,
      int encodingAction,
      Object context
   ) {
      super(message, messageRecipientData, additionalCertificates, serviceRecord, encodingAction, PGPFactory.getInstance(), context);
   }

   @Override
   protected void setGlobalOptionsDefaults(SecureEmailOptions options) {
      ((PGPOptions)options).setUseConventionalEncryption(false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected OutputStream createOutputStream(OutputStream innermostOutputStream, SecureEmailOptions secureEmailOptions) {
      PGPOptions pgpOptions = (PGPOptions)secureEmailOptions;
      PGPEncryptedOutputStream encryptedPGPStream = null;
      PGPArmorEncoder armorEncoder = null;
      OutputStream outermostOutputStream = null;
      boolean signMessage = (super._encodingAction & 1) != 0;
      boolean encryptMessage = (super._encodingAction & 2) != 0;
      boolean warnedAboutSenderCertificate = false;
      if (encryptMessage) {
         int contentCipher = this.selectContentCipher(pgpOptions);
         int pgpContentCipher = super._secureEmailUtilities.getConstantForContentCipher(contentCipher);
         boolean useConventionalEncryption = pgpOptions.getUseConventionalEncryption();
         if (!useConventionalEncryption) {
            if (encryptMessage) {
               KeyStoreData myEncryptionData = secureEmailOptions.getEncryptionKeyStoreData();
               if (myEncryptionData != null && !warnedAboutSenderCertificate) {
                  this.showCertificateWarning(myEncryptionData, 2, secureEmailOptions);
                  warnedAboutSenderCertificate = true;
               }

               armorEncoder = new PGPArmorEncoder(innermostOutputStream, "Research In Motion 1.0", null, null, false);
               encryptedPGPStream = new PGPEncryptedOutputStream(armorEncoder, pgpContentCipher, 4);
               if (!signMessage) {
                  PGPCompressedOutputStream compressedOut = new PGPCompressedOutputStream(encryptedPGPStream);
                  PGPLiteralOutputStream literalOut = new PGPLiteralOutputStream(compressedOut);
                  outermostOutputStream = literalOut;
               }

               this.updateSigningAndEncryptingMessage();
               Vector adkIDsUsed = new Vector();
               RecipientData[] messageRecipientData = this.getMessageRecipientData();
               int numMessageRecipientData = messageRecipientData != null ? messageRecipientData.length : 0;

               for (int i = 0; i < numMessageRecipientData; i++) {
                  RecipientData$CertificateDetails[] currentCertificateDetails = super._messageRecipientData[i].getSelectedCertificates();
                  int numCurrentCertificateDetails = currentCertificateDetails != null ? currentCertificateDetails.length : 0;

                  for (int j = 0; j < numCurrentCertificateDetails; j++) {
                     this.encryptToCertificate(encryptedPGPStream, (PGPCertificate)currentCertificateDetails[j].getCertificate());
                     RecipientData$CertificateDetails[] additionalCertificateDetails = currentCertificateDetails[j].getAdditionalCertificates();
                     if (additionalCertificateDetails != null) {
                        Certificate currentADK = null;

                        for (int k = 0; k < additionalCertificateDetails.length; k++) {
                           currentADK = additionalCertificateDetails[k].getCertificate();
                           if (!adkIDsUsed.contains(currentADK.getSerialNumber())) {
                              adkIDsUsed.addElement(currentADK.getSerialNumber());
                              this.encryptToCertificate(encryptedPGPStream, (PGPCertificate)currentADK);
                           }
                        }
                     }
                  }
               }

               Certificate[] additionalCertificates = this.getAdditionalCertificates();
               int numAdditionalCertificates = additionalCertificates != null ? additionalCertificates.length : 0;

               for (int i = 0; i < numAdditionalCertificates; i++) {
                  this.encryptToCertificate(encryptedPGPStream, (PGPCertificate)additionalCertificates[i]);
               }
            }
         } else {
            PasswordDialog dialog = new PasswordDialog(PGPResources.getString(8064), PGPResources.getString(8103), 255, 134217728);
            BackgroundDialog.show(dialog);
            if (dialog.getCloseReason() == -1) {
               throw new AbortSendSecureEmailException();
            }

            byte[] password = dialog.getPassword();
            armorEncoder = new PGPArmorEncoder(innermostOutputStream, "Research In Motion 1.0", null, null, false);
            PGPSaltedIteratedKDFPseudoRandomSource source = new PGPSaltedIteratedKDFPseudoRandomSource((byte)10, password);
            encryptedPGPStream = new PGPEncryptedOutputStream(armorEncoder, pgpContentCipher, source, 4);
            if (!signMessage) {
               PGPCompressedOutputStream compressedOut = new PGPCompressedOutputStream(encryptedPGPStream);
               PGPLiteralOutputStream literalOut = new PGPLiteralOutputStream(compressedOut);
               outermostOutputStream = literalOut;
            }
         }
      }

      if (signMessage) {
         PGPPrivateKey pgpPrivateKey = null;
         boolean var27 = false /* VF: Semaphore variable */;
         boolean var30 = false /* VF: Semaphore variable */;

         try {
            try {
               var30 = true;
               var27 = true;
               KeyStoreData var38 = secureEmailOptions.getSigningKeyStoreData();
               if (var38 != null && !warnedAboutSenderCertificate) {
                  this.showCertificateWarning(var38, 1, secureEmailOptions);
                  warnedAboutSenderCertificate = true;
               }

               pgpPrivateKey = (PGPPrivateKey)this.locateMySigningPrivateKey(var38, secureEmailOptions, SecureEmailResources.getString(6));
               PGPCertificate myPGPCertificate = (PGPCertificate)var38.getCertificate();
               byte[] mySigningKeyID = PGPUtilities.getSigningKeyID(myPGPCertificate);
               PrivateKey privateKey = pgpPrivateKey.getPrivateKey(mySigningKeyID);
               if (privateKey == null) {
                  throw new PGPSigningKeyNotAvailableException(var38);
               }

               privateKey.verify();
               if (encryptMessage) {
                  PGPCompressedOutputStream compressedOut = new PGPCompressedOutputStream(encryptedPGPStream);
                  PGPSignedOutputStream signedOut = new PGPSignedOutputStream(compressedOut, 0, privateKey, mySigningKeyID);
                  PGPLiteralOutputStream literalOut = new PGPLiteralOutputStream(signedOut);
                  outermostOutputStream = literalOut;
                  var27 = false;
                  var30 = false;
               } else {
                  armorEncoder = new PGPArmorEncoder(innermostOutputStream, "Research In Motion 1.0", null, null, true);
                  PGPSignedOutputStream signedOut = new PGPSignedOutputStream(armorEncoder, 1, privateKey, mySigningKeyID);
                  outermostOutputStream = signedOut;
                  var27 = false;
                  var30 = false;
               }
            } finally {
               if (var30) {
                  throw new AbortSendSecureEmailException();
               }
            }
         } finally {
            if (var27) {
               if (pgpPrivateKey != null) {
                  pgpPrivateKey.clearPasswordTicket();
               }
            }
         }

         if (pgpPrivateKey != null) {
            pgpPrivateKey.clearPasswordTicket();
         }
      }

      return outermostOutputStream;
   }

   private void encryptToCertificate(PGPEncryptedOutputStream encryptedPGPStream, PGPCertificate certificate) throws InvalidKeyException {
      byte[] bestKeyID = null;
      PublicKey bestPublicKey = null;
      long[] properties = PGPSubKeyProperties.getPGPEncryptionSubKeyProperties(
         certificate, System.currentTimeMillis(), super._secureEmailFactory.getCryptoSystemProperties()
      );
      if (properties != null && properties.length != 0) {
         int bestSubKeyIndex = PGPSubKeyProperties.selectBestPGPSubKey(properties, certificate);
         if (bestSubKeyIndex < 0) {
            bestKeyID = certificate.getKeyID();
            bestPublicKey = certificate.getPublicKey();
         } else {
            bestKeyID = certificate.getSubKeyID(bestSubKeyIndex);
            bestPublicKey = certificate.getPublicKey(bestKeyID);
         }
      } else {
         bestKeyID = certificate.getKeyID();
         bestPublicKey = certificate.getPublicKey();
      }

      if (bestPublicKey == null) {
         throw new InvalidKeyException("No public key for encryption");
      }

      bestPublicKey.verify();
      encryptedPGPStream.addRecipient(bestKeyID, bestPublicKey);
   }

   @Override
   protected void writeDataToOutputStream(OutputStream outermostOutputStream, StringBuffer dataToEncode, Object[] attachments) {
      byte[] textPlainBytes = dataToEncode.toString().getBytes("utf-8");
      outermostOutputStream.write(textPlainBytes);
      outermostOutputStream.close();
   }

   @Override
   protected boolean handleThrowable(Throwable t) {
      if (!(t instanceof PGPSigningKeyNotAvailableException)) {
         return super.handleThrowable(t);
      }

      PGPSigningKeyNotAvailableException noKeyException = (PGPSigningKeyNotAvailableException)t;
      KeyStoreData keyData = noKeyException.getKeyData();
      if (keyData == null) {
         throw new IllegalStateException("No key store data set in PGPSigningKeyNotAvailableException");
      }

      String message = PGPResources.getString(8115) + " " + MessageFormat.format(PGPResources.getString(8113), new String[]{keyData.getLabel()});
      String[] userOptions = PGPResources.getStringArray(8114);
      SimpleChoiceDialog scd = new SimpleChoiceDialog(message, userOptions, 0, null, 134217728);

      while (true) {
         BackgroundDialog.show(scd);
         if (scd.getCloseReason() == -1) {
            return false;
         }

         switch (scd.getSelectedIndex()) {
            case -1:
               break;
            case 0:
            default:
               return false;
            case 1:
               CertificateUtilities.displayCertificateDetails(
                  keyData.getCertificate(), null, super._preferredKeyStore, super._secureEmailFactory.getCryptoSystemProperties(), true, null
               );
         }
      }
   }
}
