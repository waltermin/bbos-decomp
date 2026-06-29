package net.rim.device.apps.internal.blackberryemail.email.recipientcache;

import java.util.Enumeration;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

class RecipientCache$RecipientCacheSyncItem extends OTASyncCapableSyncItem {
   private final RecipientCache this$0;
   private static final int RECEPIENT_CACHE_DATA_LENGTH = 0;
   private static final int RECIPIENT = 1;
   private static final int SERVICE_RECORD_ID = 2;
   private static final int ENCODING_UID = 3;
   private static final int FLAGS = 4;
   private static final int SERVICE_UID_HASH = 5;
   private static final int SERVICE_USERID = 6;
   private static final int MESSAGE_CLASSIFICATION = 7;

   RecipientCache$RecipientCacheSyncItem(RecipientCache _1) {
      this.this$0 = _1;
   }

   @Override
   public String getSyncName() {
      return "Recipient Cache";
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
      Enumeration dataEnumeration = this.this$0._cache.keys();

      while (dataEnumeration.hasMoreElements()) {
         RecipientCacheData data = (RecipientCacheData)dataEnumeration.nextElement();
         ConverterUtilities.writeInt(buffer, 0, 6);
         ConverterUtilities.writeStringSmart(buffer, 1, data.getRecipient());
         ConverterUtilities.writeInt(buffer, 5, data.getServiceUIDHash());
         ConverterUtilities.writeLong(buffer, 3, data.getEncodingUID());
         ConverterUtilities.writeInt(buffer, 4, data.getFlags());
         ConverterUtilities.writeInt(buffer, 6, data.getServiceUserID());
         ConverterUtilities.writeInt(buffer, 7, data.getMessageClassification());
      }

      return true;
   }

   @Override
   public synchronized boolean setSyncData(DataBuffer buffer, int version) {
      this.this$0.removeAll();

      label94:
      try {
         while (!buffer.eof()) {
            String recipient = null;
            int serviceUserId = -1;
            int serviceUIDHash = -1;
            long encodingUID = -1;
            int flags = 0;
            int messageClassification = -1;
            int fieldType = ConverterUtilities.getType(buffer, true);
            int size = 4;
            if (fieldType == 0) {
               size = ConverterUtilities.readInt(buffer);
            }

            for (int i = 0; i < size; i++) {
               switch (ConverterUtilities.getType(buffer, true)) {
                  case 0:
                  case 2:
                     ConverterUtilities.skipField(buffer);
                     break;
                  case 1:
                  default:
                     recipient = ConverterUtilities.readString(buffer);
                     break;
                  case 3:
                     encodingUID = ConverterUtilities.readLong(buffer);
                     break;
                  case 4:
                     flags = ConverterUtilities.readInt(buffer);
                     break;
                  case 5:
                     serviceUIDHash = ConverterUtilities.readInt(buffer);
                     break;
                  case 6:
                     serviceUserId = ConverterUtilities.readInt(buffer);
                     break;
                  case 7:
                     messageClassification = ConverterUtilities.readInt(buffer);
               }
            }

            try {
               this.this$0.add(new RecipientCacheData(recipient, serviceUserId, serviceUIDHash, messageClassification, encodingUID, flags));
            } finally {
               continue;
            }
         }
      } finally {
         break label94;
      }

      this.fireSyncItemUpdated();
      return true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      this.this$0.removeAll();
      this.fireSyncItemUpdated();
      return true;
   }
}
