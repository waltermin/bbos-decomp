package net.rim.device.api.memorycleaner;

import java.io.EOFException;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.DataBuffer;

class MemoryCleanerManager$MemoryCleanerSyncItem extends OTASyncCapableSyncItem {
   private final MemoryCleanerManager this$0;
   private static final int CLEAN_WHEN_HOLSTERED = 1;
   private static final int CLEAN_WHEN_IDLE = 2;
   private static final int SHOW_APP_ON_RIBBON = 3;
   private static final int IDLE_TIMEOUT = 4;
   private static final int USER_CLEAN_ENABLED = 5;

   MemoryCleanerManager$MemoryCleanerSyncItem(MemoryCleanerManager _1) {
      this.this$0 = _1;
   }

   @Override
   public String getSyncName() {
      return "Memory Cleaner Options";
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
      ConverterUtilities.convertInt(buffer, 5, this.this$0._settings._userCleanEnabled ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 1, this.this$0._settings._cleanWhenHolstered ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 2, this.this$0._settings._cleanWhenIdle ? 1 : 0, 1);
      ConverterUtilities.convertInt(buffer, 3, this.this$0._settings._showAppOnRibbon ? 1 : 0, 1);
      ConverterUtilities.writeLong(buffer, 4, this.this$0._settings._idleTimeoutSeconds);
      return true;
   }

   @Override
   public boolean setSyncData(DataBuffer buffer, int version) {
      synchronized (this) {
         try {
            int type = 0;

            while (!buffer.eof()) {
               type = ConverterUtilities.getType(buffer);
               switch (type) {
                  case 0:
                     ConverterUtilities.skipField(buffer);
                     break;
                  case 1:
                     this.this$0._settings._cleanWhenHolstered = ConverterUtilities.readInt(buffer) == 1;
                     break;
                  case 2:
                     this.this$0._settings._cleanWhenIdle = ConverterUtilities.readInt(buffer) == 1;
                     break;
                  case 3:
                     this.this$0._settings._showAppOnRibbon = ConverterUtilities.readInt(buffer) == 1;
                     break;
                  case 4:
                     this.this$0._settings._idleTimeoutSeconds = ConverterUtilities.readLong(buffer);
                     break;
                  case 5:
                  default:
                     this.this$0._settings._userCleanEnabled = ConverterUtilities.readInt(buffer) == 1;
               }
            }
         } catch (EOFException var6) {
         }

         this.this$0._settingsHolder.commit();
      }

      this.this$0._syncItem.fireSyncItemUpdated();
      RIMGlobalMessagePoster.postGlobalEvent(5924166216341050021L);
      return true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      synchronized (this) {
         this.this$0.resetOptions();
         this.this$0._settingsHolder.commit();
      }

      this.this$0._syncItem.fireSyncItemUpdated();
      RIMGlobalMessagePoster.postGlobalEvent(5924166216341050021L);
      return true;
   }
}
