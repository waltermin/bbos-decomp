package net.rim.device.apps.internal.messaging;

import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.vm.Process;

final class MessageListCleaner implements RealtimeClockListener {
   private long _lastCheckTime;
   private boolean _purgingMessages;
   private boolean _purgingOldMessagesDone;
   private boolean _purgingOldSavedMessagesDone;

   @Override
   public final void clockUpdated() {
      if (MessageListOptions.getOptions().getKeepMessagesDuration() > 0 || MessageListOptions.getOptions().getKeepSavedMessagesDuration() > 0) {
         long currentTime = System.currentTimeMillis();
         long elapsedTime = currentTime - this._lastCheckTime;
         if ((elapsedTime > 86400000 || elapsedTime < 0) && Process.ensureMinimumIdleTime(30) > 0 && !this._purgingMessages) {
            this._purgingMessages = true;
            new Thread(new MessageListCleaner$PurgeOldMessagesRunnable(this, currentTime)).start();
         }
      }
   }
}
