package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

class SMIMEOptions$SMIMEOptionsSyncItem extends OTASyncCapableSyncItem {
   private final SMIMEOptions this$0;
   private static final int INCLUDE_CERTIFICATES_FLAG;
   private static final int REQUEST_SIGNED_RECEIPTS;
   private static final int SIGNING_SERIAL_NUMBER;
   private static final int SIGNING_ISSUER;
   private static final int ENCRYPTION_SERIAL_NUMBER;
   private static final int ENCRYPTION_ISSUER;
   private static final int CONTENT_CIPHER_BIT_FIELD;
   private static final int ALWAYS_BCC;
   private static final int SHOW_MESSAGE_DETAILS;
   private static final int EMS_EMAIL_ADDRESS;
   private static final int PROMPT_PROBLEM_PERSONAL_CERTS;

   SMIMEOptions$SMIMEOptionsSyncItem(SMIMEOptions _1) {
      this.this$0 = _1;
   }

   @Override
   public String getSyncName() {
      return "S/MIME Options";
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public int getSyncVersion() {
      return 0;
   }

   @Override
   public synchronized boolean getSyncData(DataBuffer buffer, int version) {
      ConverterUtilities.convertInt(buffer, 1, this.this$0._includeCertificatesFlag ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 2, this.this$0._requestSignedReceipts ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 12, SMIMEOptions.access$200(this.this$0) ? 1 : 0, 1);
      if (SMIMEOptions.access$300(this.this$0) != null) {
         Certificate certificate = SMIMEOptions.access$400(this.this$0).getCertificate();
         if (certificate != null) {
            ConverterUtilities.writeByteArray(buffer, 3, certificate.getSerialNumber());
            ConverterUtilities.writeByteArray(buffer, 4, certificate.getIssuer().getEncoding());
         }
      }

      if (SMIMEOptions.access$500(this.this$0) != null) {
         Certificate certificate = SMIMEOptions.access$600(this.this$0).getCertificate();
         if (certificate != null) {
            ConverterUtilities.writeByteArray(buffer, 5, certificate.getSerialNumber());
            ConverterUtilities.writeByteArray(buffer, 6, certificate.getIssuer().getEncoding());
         }
      }

      ConverterUtilities.convertInt(buffer, 7, SMIMEOptions.access$700(this.this$0), 4);
      ConverterUtilities.convertInt(buffer, 10, SMIMEOptions.access$800(this.this$0) ? 1 : 0, 1);
      return true;
   }

   @Override
   public synchronized boolean setSyncData(DataBuffer buffer, int version) {
      label82:
      try {
         int type = 0;

         while (!buffer.eof()) {
            type = ConverterUtilities.getType(buffer, true);
            switch (type) {
               case 0:
               case 8:
               case 9:
               case 11:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  this.this$0._includeCertificatesFlag = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 2:
                  this.this$0._requestSignedReceipts = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 3:
                  this.this$0._signingSerialNumber = ConverterUtilities.readByteArray(buffer);
                  break;
               case 4:
                  this.this$0._signingIssuer = ConverterUtilities.readByteArray(buffer);
                  break;
               case 5:
                  this.this$0._encryptionSerialNumber = ConverterUtilities.readByteArray(buffer);
                  break;
               case 6:
                  this.this$0._encryptionIssuer = ConverterUtilities.readByteArray(buffer);
                  break;
               case 7:
                  SMIMEOptions.access$1402(this.this$0, ConverterUtilities.readInt(buffer));
                  break;
               case 10:
                  SMIMEOptions.access$1502(this.this$0, ConverterUtilities.readInt(buffer) == 1);
                  break;
               case 12:
                  SMIMEOptions.access$902(this.this$0, ConverterUtilities.readInt(buffer) == 1);
            }
         }
      } finally {
         break label82;
      }

      KeyStore keyStore = DeviceKeyStore.getInstance();
      keyStore.addIndex((KeyStoreIndex)(new Object()));
      SMIMEOptions.access$1602(this.this$0, this.this$0.findMatchingKeyStoreData(keyStore, this.this$0._signingSerialNumber, this.this$0._signingIssuer));
      SMIMEOptions.access$1802(this.this$0, this.this$0.findMatchingKeyStoreData(keyStore, this.this$0._encryptionSerialNumber, this.this$0._encryptionIssuer));
      if (SMIMEOptions.access$1900(this.this$0) != null) {
         this.this$0._signingSerialNumber = null;
         this.this$0._signingIssuer = null;
      }

      if (SMIMEOptions.access$2000(this.this$0) != null) {
         this.this$0._encryptionSerialNumber = null;
         this.this$0._encryptionIssuer = null;
      }

      this.this$0.checkITPolicyConformance();
      SMIMEFactory.getInstance().saveGlobalOptions();
      return true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      this.this$0.reset();
      this.this$0.checkITPolicyConformance();
      SMIMEFactory.getInstance().saveGlobalOptions();
      return true;
   }
}
