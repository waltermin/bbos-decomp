package net.rim.device.apps.api.messaging.messagelist;

import java.util.Enumeration;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ToLongHashtable;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.messaging.FolderHierarchies;

public class MessageBulkMarkOldManager implements RealtimeClockListener {
   private ToLongHashtable _messages;
   private long _messageListLastSeenTimeStamp = System.currentTimeMillis();
   private MessageBulkMarkOldManager$Worker _worker = new MessageBulkMarkOldManager$Worker(null);
   private int _batchMaxSize;
   private static final int DEFAULT_BATCH_SIZE;

   public int getBatchMaxSize() {
      return this._batchMaxSize;
   }

   public void setBatchMaxSize(int maxSize) {
      this._batchMaxSize = maxSize;
   }

   public synchronized void markLastSeenTimeStamp() {
      if (!this._messages.isEmpty()) {
         this._messageListLastSeenTimeStamp = System.currentTimeMillis();
      }
   }

   public synchronized void addMessage(Object message) {
      if (isNew(message)) {
         this._messages.put(message, System.currentTimeMillis());
      }
   }

   public synchronized void removeMessage(Object message) {
      this._messages.remove(message);
   }

   public void perform() {
      if (!this._worker.isRunning() && !this._messages.isEmpty()) {
         Object[] messagesToProcess = this.getMessagesToProcess();
         if (messagesToProcess.length > 0 && this._worker.init(messagesToProcess)) {
            ((Thread)(new Object(this._worker))).start();
         }
      }
   }

   @Override
   public void clockUpdated() {
      this.perform();
   }

   public MessageBulkMarkOldManager() {
      this._messages = (ToLongHashtable)(new Object());
      this._batchMaxSize = 12;
   }

   private synchronized Object[] getMessagesToProcess() {
      Object[] messagesToClean = new Object[0];
      Enumeration keys = this._messages.keys();

      while (keys.hasMoreElements()) {
         Object message = keys.nextElement();
         long timeStamp = this._messages.get(message);
         if (timeStamp != -1 && timeStamp <= this._messageListLastSeenTimeStamp) {
            Arrays.add(messagesToClean, message);
            this._messages.remove(message);
         }

         if (messagesToClean.length > this._batchMaxSize) {
            return messagesToClean;
         }
      }

      return messagesToClean;
   }

   private static boolean isNew(Object message) {
      if (!(message instanceof Object)) {
         return false;
      }

      ActionProvider actionProvider = (ActionProvider)message;
      return actionProvider.perform(-6072303684925088654L, null);
   }

   private static void markOld(Object message) {
      if (isNew(message)) {
         ActionProvider actionProvider = (ActionProvider)message;
         synchronized (FolderHierarchies.getLockObject()) {
            actionProvider.perform(5213547777258110094L, null);
         }
      }
   }
}
