package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.certificate.pgp.PGPFingerprintKeyStoreIndex;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

class PGPOptions$PGPOptionsSyncItem extends OTASyncCapableSyncItem {
   private final PGPOptions this$0;
   private static final int USE_CONVENTIONAL_ENCRYPTION = 1;
   private static final int FINGERPRINT = 2;
   private static final int CONTENT_CIPHER_BIT_FIELD = 3;
   private static final int ALWAYS_BCC = 4;
   private static final int SHOW_MESSAGE_DETAILS = 6;
   private static final int PROMPT_PROBLEM_PERSONAL_CERTS = 7;

   PGPOptions$PGPOptionsSyncItem(PGPOptions _1) {
      this.this$0 = _1;
   }

   @Override
   public String getSyncName() {
      return "PGP Options";
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
      ConverterUtilities.convertInt(buffer, 1, this.this$0._useConventionalEncryption ? 1 : 0, 1);
      if (PGPOptions.access$100(this.this$0) != null) {
         Certificate certificate = PGPOptions.access$200(this.this$0).getCertificate();
         if (certificate instanceof PGPCertificate) {
            PGPCertificate pgpCert = (PGPCertificate)certificate;
            ConverterUtilities.writeByteArray(buffer, 2, pgpCert.getFingerprint());
         }
      }

      ConverterUtilities.convertInt(buffer, 3, PGPOptions.access$300(this.this$0), 4);
      ConverterUtilities.convertInt(buffer, 6, PGPOptions.access$400(this.this$0) ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 7, PGPOptions.access$500(this.this$0) ? 1 : 0, 1);
      return true;
   }

   @Override
   public synchronized boolean setSyncData(DataBuffer buffer, int version) {
      KeyStore keyStore = PGPKeyStore.getInstance();
      keyStore.addIndex(new PGPFingerprintKeyStoreIndex());

      label64:
      try {
         int type = 0;

         while (!buffer.eof()) {
            type = ConverterUtilities.getType(buffer, true);
            switch (type) {
               case 0:
               case 4:
               case 5:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  this.this$0._useConventionalEncryption = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 2:
                  this.this$0._fingerprint = ConverterUtilities.readByteArray(buffer);
                  KeyStoreData matchingKeyStoreData = this.this$0.findMatchingKeyStoreData(keyStore, this.this$0._fingerprint);
                  PGPOptions.access$802(this.this$0, matchingKeyStoreData);
                  PGPOptions.access$902(this.this$0, matchingKeyStoreData);
                  if (matchingKeyStoreData != null) {
                     this.this$0._fingerprint = null;
                  }
                  break;
               case 3:
                  PGPOptions.access$1002(this.this$0, ConverterUtilities.readInt(buffer));
                  break;
               case 6:
                  PGPOptions.access$1102(this.this$0, ConverterUtilities.readInt(buffer) == 1);
                  break;
               case 7:
                  PGPOptions.access$1202(this.this$0, ConverterUtilities.readInt(buffer) == 1);
            }
         }
      } finally {
         break label64;
      }

      PGPFactory.getInstance().saveGlobalOptions();
      return true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      this.this$0.reset();
      this.this$0.checkITPolicyConformance();
      PGPFactory.getInstance().saveGlobalOptions();
      return true;
   }
}
