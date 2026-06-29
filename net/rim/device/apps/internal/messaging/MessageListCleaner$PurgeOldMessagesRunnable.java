package net.rim.device.apps.internal.messaging;

import java.util.Calendar;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.utility.lowMemory.PurgeManager;
import net.rim.device.cldc.util.CalendarExtensions;

final class MessageListCleaner$PurgeOldMessagesRunnable implements Runnable {
   private long _checkTime;
   private final MessageListCleaner this$0;

   MessageListCleaner$PurgeOldMessagesRunnable(MessageListCleaner _1, long checkTime) {
      this.this$0 = _1;
      this._checkTime = checkTime;
   }

   private final boolean purgeOldMessages(short keepMessagesDuration, int purgeType) {
      long threshold = System.currentTimeMillis() - (long)keepMessagesDuration * 86400000;
      Calendar cal = Calendar.getInstance();
      ((CalendarExtensions)cal).setTimeLong(threshold);
      cal.set(14, 0);
      cal.set(13, 0);
      cal.set(12, 0);
      cal.set(11, 0);
      threshold = ((CalendarExtensions)cal).getTimeLong();
      return PurgeManager.getInstance().purge(purgeType, threshold);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      Thread.currentThread().setPriority(4);
      short oldMessagesKeepDuration = MessageListOptions.getOptions().getKeepMessagesDuration();
      short oldSavedMessagesKeepDuration = MessageListOptions.getOptions().getKeepSavedMessagesDuration();
      boolean processOldMessages = oldMessagesKeepDuration > 0;
      boolean processOldSavedMessages = oldSavedMessagesKeepDuration > 0;
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         if (processOldMessages && !this.this$0._purgingOldMessagesDone) {
            this.this$0._purgingOldMessagesDone = this.purgeOldMessages(oldMessagesKeepDuration, 1);
         }

         if (processOldSavedMessages && !this.this$0._purgingOldSavedMessagesDone) {
            this.this$0._purgingOldSavedMessagesDone = this.purgeOldMessages(oldSavedMessagesKeepDuration, 3);
         }

         if (processOldMessages && processOldSavedMessages) {
            if (this.this$0._purgingOldMessagesDone) {
               if (this.this$0._purgingOldSavedMessagesDone) {
                  this.this$0._lastCheckTime = this._checkTime;
                  var7 = false;
               } else {
                  var7 = false;
               }
            } else {
               var7 = false;
            }
         } else if (processOldMessages && !processOldSavedMessages) {
            if (this.this$0._purgingOldMessagesDone) {
               this.this$0._lastCheckTime = this._checkTime;
               var7 = false;
            } else {
               var7 = false;
            }
         } else if (!processOldMessages) {
            if (processOldSavedMessages) {
               if (this.this$0._purgingOldSavedMessagesDone) {
                  this.this$0._lastCheckTime = this._checkTime;
                  var7 = false;
               } else {
                  var7 = false;
               }
            } else {
               var7 = false;
            }
         } else {
            var7 = false;
         }
      } finally {
         if (var7) {
            this.this$0._purgingMessages = false;
            this.this$0._purgingOldMessagesDone = false;
            this.this$0._purgingOldSavedMessagesDone = false;
         }
      }

      this.this$0._purgingMessages = false;
      this.this$0._purgingOldMessagesDone = false;
      this.this$0._purgingOldSavedMessagesDone = false;
   }
}
