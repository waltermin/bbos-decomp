package net.rim.device.apps.internal.ldap;

import java.util.Enumeration;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

class LDAPBrowserOptionStore$LDAPOptionsSyncItem extends OTASyncCapableSyncItem {
   private final LDAPBrowserOptionStore this$0;
   private static final int CONTEXT_NAME = 1;
   private static final int FETCH_CERT_STATUS = 2;
   private static final int PROMPT_FOR_CERT_LABEL = 4;

   LDAPBrowserOptionStore$LDAPOptionsSyncItem(LDAPBrowserOptionStore _1) {
      this.this$0 = _1;
   }

   @Override
   public String getSyncName() {
      return "LDAP Browser Options";
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
      Enumeration enumeration = this.this$0._optionData.keys();

      while (enumeration.hasMoreElements()) {
         String contextName = (String)enumeration.nextElement();
         LDAPBrowserOptionStore$LDAPOptionData optionData = (LDAPBrowserOptionStore$LDAPOptionData)this.this$0._optionData.get(contextName);
         ConverterUtilities.writeStringSmart(buffer, 1, contextName);
         ConverterUtilities.convertInt(buffer, 2, optionData._fetchCertStatus, 4);
         ConverterUtilities.convertInt(buffer, 4, optionData._promptForCertLabel ? 1 : 0, 1);
      }

      return true;
   }

   @Override
   public synchronized boolean setSyncData(DataBuffer buffer, int version) {
      String contextName = null;

      label44:
      try {
         int type = 0;

         while (!buffer.eof()) {
            switch (ConverterUtilities.getType(buffer, true)) {
               case 0:
               case 3:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  contextName = ConverterUtilities.readString(buffer);
                  break;
               case 2: {
                  LDAPBrowserOptionStore$LDAPOptionData optionData = this.this$0.getOptionData(contextName);
                  optionData._fetchCertStatus = ConverterUtilities.readInt(buffer);
                  break;
               }
               case 4: {
                  LDAPBrowserOptionStore$LDAPOptionData optionData = this.this$0.getOptionData(contextName);
                  optionData._promptForCertLabel = ConverterUtilities.readInt(buffer) == 1;
               }
            }
         }
      } finally {
         break label44;
      }

      LDAPBrowserOptionStore._persist.commit();
      this.fireSyncItemUpdated();
      return true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      Enumeration enumeration = this.this$0._optionData.keys();

      while (enumeration.hasMoreElements()) {
         String contextName = (String)enumeration.nextElement();
         LDAPBrowserOptionStore$LDAPOptionData optionData = (LDAPBrowserOptionStore$LDAPOptionData)this.this$0._optionData.get(contextName);
         optionData.resetOptions();
      }

      LDAPBrowserOptionStore._persist.commit();
      this.fireSyncItemUpdated();
      return true;
   }
}
