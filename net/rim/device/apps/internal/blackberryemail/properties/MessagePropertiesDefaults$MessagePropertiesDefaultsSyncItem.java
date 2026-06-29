package net.rim.device.apps.internal.blackberryemail.properties;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

class MessagePropertiesDefaults$MessagePropertiesDefaultsSyncItem extends OTASyncCapableSyncItem {
   private final MessagePropertiesDefaults this$0;
   private static final int ENCODING_UID;
   private static final int ENCODING_ACTION;
   private static final int MESSAGE_CLASSIFICATION;

   private MessagePropertiesDefaults$MessagePropertiesDefaultsSyncItem(MessagePropertiesDefaults _1) {
      this.this$0 = _1;
   }

   @Override
   public String getSyncName() {
      return "Secure Email Decision Maker";
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
      ConverterUtilities.writeLong(buffer, 1, this.this$0.getEncodingUID());
      ConverterUtilities.writeInt(buffer, 2, this.this$0.getEncodingAction());
      ConverterUtilities.writeInt(buffer, 3, this.this$0.getMessageClassification());
      return true;
   }

   @Override
   public synchronized boolean setSyncData(DataBuffer buffer, int version) {
      long encodingUID = this.this$0.getEncodingUID();
      int encodingAction = this.this$0.getEncodingAction();
      int messageClassification = this.this$0.getMessageClassification();

      label43:
      try {
         while (!buffer.eof()) {
            switch (ConverterUtilities.getType(buffer, true)) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  encodingUID = ConverterUtilities.readLong(buffer);
                  break;
               case 2:
                  encodingAction = ConverterUtilities.readInt(buffer);
                  break;
               case 3:
                  messageClassification = ConverterUtilities.readInt(buffer);
            }
         }
      } finally {
         break label43;
      }

      if (encodingUID == -742709496102783169L && encodingAction == 0) {
         encodingAction = 3;
      }

      this.this$0.setProperties(encodingUID, encodingAction, messageClassification);
      return true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      this.this$0.resetProperties();
      return true;
   }

   MessagePropertiesDefaults$MessagePropertiesDefaultsSyncItem(MessagePropertiesDefaults x0, MessagePropertiesDefaults$1 x1) {
      this(x0);
   }
}
