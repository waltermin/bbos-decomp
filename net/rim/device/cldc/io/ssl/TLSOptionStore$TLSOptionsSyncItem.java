package net.rim.device.cldc.io.ssl;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

class TLSOptionStore$TLSOptionsSyncItem extends OTASyncCapableSyncItem {
   private final TLSOptionStore this$0;
   private static final int SESSION_RESUMPTION = 1;
   private static final int PROMPT_FOR_CERTIFICATE = 2;
   private static final int PROMPT_FOR_CERTIFICATE_ONE = 3;
   private static final int PROMPT_FOR_DOMAIN_NAME = 4;
   private static final int PROMPT_FOR_CERTIFICATE_TRUST = 5;
   private static final int MIN_RSA_KEY_SIZE = 6;
   private static final int MIN_DH_KEY_SIZE = 7;
   private static final int MIN_EC_KEY_SIZE = 8;
   private static final int MIN_DSA_KEY_SIZE = 9;
   private static final int ALLOW_EXPORT = 10;
   private static final int RESTRICT_FIPS = 11;
   private static final int DEFAULT_IMPLEMENTATION = 12;
   private static final int USE_TLS = 13;
   private static final int USE_SSL = 14;
   private static final int DO_REDIRECTION = 15;
   private static final int TRUSTED_HOSTS = 16;

   TLSOptionStore$TLSOptionsSyncItem(TLSOptionStore _1) {
      this.this$0 = _1;
   }

   @Override
   public String getSyncName() {
      return "TLS Options";
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
      ConverterUtilities.convertInt(buffer, 1, this.this$0._sessionResumption ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 2, this.this$0._promptForCertificate ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 3, this.this$0._promptForCertificateOne ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 4, this.this$0._promptForDomainName ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 5, this.this$0._promptForCertificateTrust ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 6, this.this$0._minimumStrongRSAKeySize, 4);
      ConverterUtilities.convertInt(buffer, 7, this.this$0._minimumStrongDHKeySize, 4);
      ConverterUtilities.convertInt(buffer, 8, this.this$0._minimumStrongECKeySize, 4);
      ConverterUtilities.convertInt(buffer, 9, this.this$0._minimumStrongDSAKeySize, 4);
      ConverterUtilities.convertInt(buffer, 10, this.this$0._allowExport ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 11, this.this$0._restrictFIPS ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 12, this.this$0._defaultImplementation, 4);
      ConverterUtilities.convertInt(buffer, 13, this.this$0._useTLS ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 14, this.this$0._useSSL ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 15, this.this$0._doRedirection ? 1 : 0, 1);
      Enumeration enumeration = this.this$0._trustedHosts.elements();

      while (enumeration.hasMoreElements()) {
         ConverterUtilities.writeStringSmart(buffer, 16, (String)enumeration.nextElement());
      }

      return true;
   }

   @Override
   public synchronized boolean setSyncData(DataBuffer buffer, int version) {
      this.this$0._trustedHosts = (Vector)(new Object());

      label121:
      try {
         int type = 0;

         while (!buffer.eof()) {
            switch (ConverterUtilities.getType(buffer, true)) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  this.this$0._sessionResumption = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 2:
                  this.this$0._promptForCertificate = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 3:
                  this.this$0._promptForCertificateOne = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 4:
                  this.this$0._promptForDomainName = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 5:
                  this.this$0._promptForCertificateTrust = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 6:
                  this.this$0._minimumStrongRSAKeySize = ConverterUtilities.readInt(buffer);
                  break;
               case 7:
                  this.this$0._minimumStrongDHKeySize = ConverterUtilities.readInt(buffer);
                  break;
               case 8:
                  this.this$0._minimumStrongECKeySize = ConverterUtilities.readInt(buffer);
                  break;
               case 9:
                  this.this$0._minimumStrongDSAKeySize = ConverterUtilities.readInt(buffer);
                  break;
               case 10:
                  this.this$0._allowExport = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 11:
                  this.this$0._restrictFIPS = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 12:
                  this.this$0._defaultImplementation = ConverterUtilities.readInt(buffer);
                  break;
               case 13:
                  this.this$0._useTLS = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 14:
                  this.this$0._useSSL = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 15:
                  this.this$0._doRedirection = ConverterUtilities.readInt(buffer) == 1;
                  break;
               case 16:
                  this.this$0._trustedHosts.addElement(ConverterUtilities.readString(buffer));
            }
         }
      } finally {
         break label121;
      }

      TLSOptionStore._persist.commit();
      this.fireSyncItemUpdated();
      return true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      this.this$0.resetOptions();
      this.fireSyncItemUpdated();
      return true;
   }
}
